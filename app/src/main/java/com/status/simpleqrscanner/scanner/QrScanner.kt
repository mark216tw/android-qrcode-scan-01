package com.status.simpleqrscanner.scanner

import android.content.Context
import android.net.Uri
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.google.mlkit.vision.barcode.BarcodeScanner
import com.google.mlkit.vision.barcode.BarcodeScannerOptions
import com.google.mlkit.vision.barcode.BarcodeScanning
import com.google.mlkit.vision.barcode.common.Barcode
import com.google.mlkit.vision.common.InputImage
import java.util.concurrent.Executors
import java.util.concurrent.atomic.AtomicBoolean

@Composable
fun CameraQrScanner(
    active: Boolean,
    modifier: Modifier = Modifier,
    onCameraChanged: (Camera?) -> Unit,
    onQrCode: (String) -> Unit,
) {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val latestOnQrCode by rememberUpdatedState(onQrCode)
    val previewView = remember {
        PreviewView(context).apply {
            scaleType = PreviewView.ScaleType.FILL_CENTER
            implementationMode = PreviewView.ImplementationMode.COMPATIBLE
        }
    }
    val scanner = remember { createQrScanner() }
    val executor = remember { Executors.newSingleThreadExecutor() }

    AndroidView(factory = { previewView }, modifier = modifier)

    DisposableEffect(lifecycleOwner, active) {
        if (!active) {
            onCameraChanged(null)
            return@DisposableEffect onDispose { }
        }

        val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
        val processing = AtomicBoolean(false)
        val delivered = AtomicBoolean(false)
        val disposed = AtomicBoolean(false)
        var provider: ProcessCameraProvider? = null

        cameraProviderFuture.addListener(
            {
                if (disposed.get()) return@addListener
                runCatching {
                    provider = cameraProviderFuture.get()
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(previewView.surfaceProvider)
                    }
                    val analysis = ImageAnalysis.Builder()
                        .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                        .build()
                        .also { useCase ->
                            useCase.setAnalyzer(executor) { imageProxy ->
                                analyzeImage(
                                    imageProxy = imageProxy,
                                    scanner = scanner,
                                    processing = processing,
                                ) { value ->
                                    if (delivered.compareAndSet(false, true)) latestOnQrCode(value)
                                }
                            }
                        }

                    provider?.unbindAll()
                    val camera = provider?.bindToLifecycle(
                        lifecycleOwner,
                        CameraSelector.DEFAULT_BACK_CAMERA,
                        preview,
                        analysis,
                    )
                    onCameraChanged(camera)
                }.onFailure {
                    onCameraChanged(null)
                }
            },
            ContextCompat.getMainExecutor(context),
        )

        onDispose {
            disposed.set(true)
            provider?.unbindAll()
            onCameraChanged(null)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            scanner.close()
            executor.shutdown()
        }
    }

}

fun scanQrFromImage(
    context: Context,
    uri: Uri,
    onResult: (String?) -> Unit,
) {
    val scanner = createQrScanner()
    runCatching { InputImage.fromFilePath(context, uri) }
        .onSuccess { image ->
            scanner.process(image)
                .addOnSuccessListener { barcodes ->
                    onResult(barcodes.firstNotNullOfOrNull { it.rawValue?.takeIf(String::isNotBlank) })
                }
                .addOnFailureListener { onResult(null) }
                .addOnCompleteListener { scanner.close() }
        }
        .onFailure {
            scanner.close()
            onResult(null)
        }
}

private fun createQrScanner(): BarcodeScanner {
    val options = BarcodeScannerOptions.Builder()
        .setBarcodeFormats(
            Barcode.FORMAT_QR_CODE,
            Barcode.FORMAT_EAN_8,
            Barcode.FORMAT_EAN_13,
            Barcode.FORMAT_UPC_A,
            Barcode.FORMAT_UPC_E,
            Barcode.FORMAT_CODE_39,
            Barcode.FORMAT_CODE_93,
            Barcode.FORMAT_CODE_128,
            Barcode.FORMAT_ITF,
            Barcode.FORMAT_CODABAR,
        )
        .build()
    return BarcodeScanning.getClient(options)
}

@androidx.annotation.OptIn(markerClass = [ExperimentalGetImage::class])
private fun analyzeImage(
    imageProxy: ImageProxy,
    scanner: BarcodeScanner,
    processing: AtomicBoolean,
    onResult: (String) -> Unit,
) {
    if (!processing.compareAndSet(false, true)) {
        imageProxy.close()
        return
    }

    val mediaImage = imageProxy.image
    if (mediaImage == null) {
        processing.set(false)
        imageProxy.close()
        return
    }

    val image = InputImage.fromMediaImage(mediaImage, imageProxy.imageInfo.rotationDegrees)
    scanner.process(image)
        .addOnSuccessListener { barcodes ->
            barcodes.firstNotNullOfOrNull { it.rawValue?.takeIf(String::isNotBlank) }?.let(onResult)
        }
        .addOnCompleteListener {
            processing.set(false)
            imageProxy.close()
        }
}
