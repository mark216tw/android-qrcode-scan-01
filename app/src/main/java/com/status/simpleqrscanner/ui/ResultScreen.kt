package com.status.simpleqrscanner.ui

import android.content.ActivityNotFoundException
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.provider.CalendarContract
import android.provider.ContactsContract
import android.provider.Settings
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.automirrored.outlined.OpenInNew
import androidx.compose.material.icons.outlined.ContentCopy
import androidx.compose.material.icons.outlined.QrCodeScanner
import androidx.compose.material.icons.outlined.Share
import androidx.compose.material.icons.outlined.Visibility
import androidx.compose.material.icons.outlined.VisibilityOff
import androidx.compose.material.icons.outlined.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.net.toUri
import com.status.simpleqrscanner.model.QrContentType
import com.status.simpleqrscanner.model.ScanRecord

@Composable
fun ResultScreen(record: ScanRecord, onBack: () -> Unit, onScanAgain: () -> Unit) {
    val context = LocalContext.current
    var showWifiPassword by remember(record.id) { mutableStateOf(false) }
    val displayContent = if (record.type == QrContentType.WIFI && !showWifiPassword) {
        record.content.replace(Regex("(?i)(P:)([^;]*)"), "$1••••••")
    } else {
        record.content
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = 18.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            IconButton(onClick = onBack) {
                Icon(Icons.AutoMirrored.Outlined.ArrowBack, contentDescription = "返回")
            }
            Text("掃描結果", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
        }

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(rememberScrollState()),
        ) {
            Spacer(Modifier.height(12.dp))
            Surface(
                shape = CircleShape,
                color = MaterialTheme.colorScheme.primaryContainer,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            ) {
                Icon(
                    record.type.icon(),
                    contentDescription = null,
                    modifier = Modifier.padding(22.dp).size(42.dp),
                    tint = MaterialTheme.colorScheme.primary,
                )
            }
            Spacer(Modifier.height(14.dp))
            Text(
                record.type.title,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.align(Alignment.CenterHorizontally),
            )
            Spacer(Modifier.height(20.dp))

            if (record.type == QrContentType.URL && record.content.startsWith("http://", true)) {
                Surface(
                    shape = RoundedCornerShape(18.dp),
                    color = MaterialTheme.colorScheme.errorContainer,
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(Icons.Outlined.WarningAmber, contentDescription = null, tint = MaterialTheme.colorScheme.error)
                        Text("這個網址未使用 HTTPS，開啟前請確認來源。", style = MaterialTheme.typography.bodyMedium)
                    }
                }
                Spacer(Modifier.height(12.dp))
            }

            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(22.dp),
                color = MaterialTheme.colorScheme.surfaceContainer,
            ) {
                Column(Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            "掃描內容",
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.weight(1f),
                        )
                        if (record.type == QrContentType.WIFI) {
                            IconButton(onClick = { showWifiPassword = !showWifiPassword }) {
                                Icon(
                                    if (showWifiPassword) Icons.Outlined.VisibilityOff else Icons.Outlined.Visibility,
                                    contentDescription = if (showWifiPassword) "隱藏密碼" else "顯示密碼",
                                )
                            }
                        }
                    }
                    Text(
                        displayContent,
                        style = MaterialTheme.typography.bodyLarge.copy(fontFamily = FontFamily.Monospace),
                    )
                }
            }
            Spacer(Modifier.height(16.dp))

            primaryActionLabel(record.type)?.let { label ->
                Button(
                    onClick = { openQrContent(context, record) },
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Icon(Icons.AutoMirrored.Outlined.OpenInNew, contentDescription = null)
                    Spacer(Modifier.size(9.dp))
                    Text(label)
                }
                Spacer(Modifier.height(10.dp))
            }

            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                OutlinedButton(
                    onClick = { copyContent(context, record.content) },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Outlined.ContentCopy, contentDescription = null)
                    Spacer(Modifier.size(7.dp))
                    Text("複製")
                }
                OutlinedButton(
                    onClick = { shareContent(context, record.content) },
                    modifier = Modifier.weight(1f),
                ) {
                    Icon(Icons.Outlined.Share, contentDescription = null)
                    Spacer(Modifier.size(7.dp))
                    Text("分享")
                }
            }
            Spacer(Modifier.height(20.dp))
        }

        FilledTonalButton(
            onClick = onScanAgain,
            modifier = Modifier.fillMaxWidth().padding(vertical = 12.dp),
        ) {
            Icon(Icons.Outlined.QrCodeScanner, contentDescription = null)
            Spacer(Modifier.size(9.dp))
            Text("繼續掃描")
        }
    }
}

private fun primaryActionLabel(type: QrContentType): String? = when (type) {
    QrContentType.URL -> "開啟網址"
    QrContentType.WIFI -> "前往 Wi-Fi 設定"
    QrContentType.PHONE -> "撥打電話"
    QrContentType.EMAIL -> "撰寫郵件"
    QrContentType.SMS -> "傳送簡訊"
    QrContentType.LOCATION -> "在地圖中開啟"
    QrContentType.CONTACT -> "新增聯絡人"
    QrContentType.CALENDAR -> "新增到行事曆"
    QrContentType.TEXT -> null
}

private fun openQrContent(context: Context, record: ScanRecord) {
    val value = record.content.trim()
    val intent = when (record.type) {
        QrContentType.URL -> Intent(Intent.ACTION_VIEW, (if (value.startsWith("www.", true)) "https://$value" else value).toUri())
        QrContentType.WIFI -> Intent(Settings.ACTION_WIFI_SETTINGS)
        QrContentType.PHONE -> Intent(Intent.ACTION_DIAL, (if (value.startsWith("tel:", true)) value else "tel:$value").toUri())
        QrContentType.EMAIL -> Intent(Intent.ACTION_SENDTO, (if (value.startsWith("mailto:", true)) value else "mailto:$value").toUri())
        QrContentType.SMS -> Intent(Intent.ACTION_SENDTO, value.toUri())
        QrContentType.LOCATION -> Intent(Intent.ACTION_VIEW, value.toUri())
        QrContentType.CONTACT -> contactIntent(value)
        QrContentType.CALENDAR -> calendarIntent(value)
        QrContentType.TEXT -> return
    }
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, "找不到可執行這項操作的 App", Toast.LENGTH_SHORT).show()
    } catch (_: SecurityException) {
        Toast.makeText(context, "無法開啟這項內容", Toast.LENGTH_SHORT).show()
    }
}

private fun contactIntent(value: String): Intent {
    fun field(name: String): String? {
        if (value.startsWith("MECARD:", true)) {
            return value.substringAfter(':')
                .split(';')
                .firstOrNull { it.substringBefore(':').equals(name, true) }
                ?.substringAfter(':')
        }
        return value.lineSequence()
            .firstOrNull { line -> line.substringBefore(':').substringBefore(';').equals(name, true) }
            ?.substringAfter(':')
    }
    return Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI).apply {
        putExtra(ContactsContract.Intents.Insert.NAME, field("FN") ?: field("N"))
        putExtra(ContactsContract.Intents.Insert.PHONE, field("TEL"))
        putExtra(ContactsContract.Intents.Insert.EMAIL, field("EMAIL"))
    }
}

private fun calendarIntent(value: String): Intent {
    val title = value.lineSequence()
        .firstOrNull { it.startsWith("SUMMARY:", true) }
        ?.substringAfter(':')
        ?: "QR Code 活動"
    return Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI).apply {
        putExtra(CalendarContract.Events.TITLE, title)
        putExtra(CalendarContract.Events.DESCRIPTION, value)
    }
}

private fun copyContent(context: Context, content: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    clipboard.setPrimaryClip(ClipData.newPlainText("掃描內容", content))
    Toast.makeText(context, "已複製", Toast.LENGTH_SHORT).show()
}

private fun shareContent(context: Context, content: String) {
    val intent = Intent.createChooser(
        Intent(Intent.ACTION_SEND).apply {
            type = "text/plain"
            putExtra(Intent.EXTRA_TEXT, content)
        },
        "分享掃描內容",
    )
    context.startActivity(intent)
}
