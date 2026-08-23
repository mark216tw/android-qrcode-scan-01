package com.status.simpleqrscanner.ui

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.status.simpleqrscanner.data.AppRepository
import com.status.simpleqrscanner.model.ScanRecord
import com.status.simpleqrscanner.model.classifyQrContent

private enum class MainTab(val title: String) {
    SCAN("掃描"),
    HISTORY("紀錄"),
    SETTINGS("設定"),
}

@Composable
fun SimpleQrApp(repository: AppRepository) {
    var selectedTab by remember { mutableStateOf(MainTab.SCAN) }
    var displayedResult by remember { mutableStateOf<ScanRecord?>(null) }

    BackHandler(enabled = displayedResult != null) {
        displayedResult = null
    }

    fun showNewScan(context: Context, content: String) {
        val type = classifyQrContent(content)
        displayedResult = repository.addScan(content.trim(), type)
        playScanFeedback(context, repository.soundEnabled, repository.vibrationEnabled)
    }

    if (displayedResult != null) {
        ResultScreen(
            record = displayedResult!!,
            onBack = { displayedResult = null },
            onScanAgain = {
                displayedResult = null
                selectedTab = MainTab.SCAN
            },
        )
        return
    }

    Scaffold(
        contentWindowInsets = WindowInsets(0, 0, 0, 0),
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surfaceContainer,
            ) {
                MainTab.entries.forEach { tab ->
                    val icon = when (tab) {
                        MainTab.SCAN -> Icons.Outlined.QrCodeScanner
                        MainTab.HISTORY -> Icons.Outlined.History
                        MainTab.SETTINGS -> Icons.Outlined.Settings
                    }
                    NavigationBarItem(
                        selected = selectedTab == tab,
                        onClick = { selectedTab = tab },
                        icon = { Icon(icon, contentDescription = null) },
                        label = { Text(tab.title) },
                    )
                }
            }
        },
    ) { padding ->
        when (selectedTab) {
            MainTab.SCAN -> ScanScreen(
                contentPadding = padding,
                active = true,
                onQrCode = { context, value -> showNewScan(context, value) },
            )
            MainTab.HISTORY -> HistoryScreen(
                contentPadding = padding,
                records = repository.history,
                onOpen = { displayedResult = it },
                onDelete = repository::deleteScan,
                onClear = repository::clearHistory,
            )
            MainTab.SETTINGS -> SettingsScreen(
                contentPadding = padding,
                repository = repository,
            )
        }
    }
}

private fun playScanFeedback(context: Context, sound: Boolean, vibration: Boolean) {
    if (sound) {
        val tone = ToneGenerator(AudioManager.STREAM_NOTIFICATION, 70)
        tone.startTone(ToneGenerator.TONE_PROP_ACK, 100)
        Handler(Looper.getMainLooper()).postDelayed(tone::release, 180)
    }
    if (vibration) {
        val effect = VibrationEffect.createOneShot(60, VibrationEffect.DEFAULT_AMPLITUDE)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            context.getSystemService(VibratorManager::class.java)?.defaultVibrator?.vibrate(effect)
        } else {
            @Suppress("DEPRECATION")
            (context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator)?.vibrate(effect)
        }
    }
}
