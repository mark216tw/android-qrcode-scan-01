package com.status.simpleqrscanner.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.VolumeUp
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.DarkMode
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.LightMode
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Vibration
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.status.simpleqrscanner.BuildConfig
import com.status.simpleqrscanner.data.AppAccent
import com.status.simpleqrscanner.data.AppMode
import com.status.simpleqrscanner.data.AppRepository
import com.status.simpleqrscanner.ui.theme.accentPreviewColor

@Composable
fun SettingsScreen(contentPadding: PaddingValues, repository: AppRepository) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(contentPadding)
            .statusBarsPadding(),
        contentPadding = PaddingValues(horizontal = 18.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text("設定", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
            Text("打造你喜歡的掃描畫面", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            SettingsSection(title = "主題色彩", icon = Icons.Outlined.Palette) {
                Text(
                    "點選後立即套用",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(14.dp))
                AppAccent.entries.chunked(3).forEach { rowAccents ->
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        rowAccents.forEach { accent ->
                            AccentChoice(
                                accent = accent,
                                selected = repository.accent == accent,
                                dark = repository.mode == AppMode.DARK,
                                modifier = Modifier.weight(1f),
                                onClick = { repository.updateAccent(accent) },
                            )
                        }
                    }
                    if (rowAccents != AppAccent.entries.chunked(3).last()) Spacer(Modifier.height(10.dp))
                }
            }
        }
        item {
            SettingsSection(title = "外觀模式", icon = Icons.Outlined.Settings) {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    ModeChoice(
                        title = "明亮模式",
                        selected = repository.mode == AppMode.LIGHT,
                        icon = Icons.Outlined.LightMode,
                        modifier = Modifier.weight(1f),
                        onClick = { repository.updateMode(AppMode.LIGHT) },
                    )
                    ModeChoice(
                        title = "深色模式",
                        selected = repository.mode == AppMode.DARK,
                        icon = Icons.Outlined.DarkMode,
                        modifier = Modifier.weight(1f),
                        onClick = { repository.updateMode(AppMode.DARK) },
                    )
                }
            }
        }
        item {
            SettingsSection(title = "掃描偏好", icon = Icons.Outlined.Settings) {
                SettingSwitch(
                    title = "掃描提示音",
                    description = "辨識成功時播放提示音",
                    icon = Icons.AutoMirrored.Outlined.VolumeUp,
                    checked = repository.soundEnabled,
                    onCheckedChange = repository::updateSoundEnabled,
                )
                SettingSwitch(
                    title = "掃描震動",
                    description = "辨識成功時短暫震動",
                    icon = Icons.Outlined.Vibration,
                    checked = repository.vibrationEnabled,
                    onCheckedChange = repository::updateVibrationEnabled,
                )
                SettingSwitch(
                    title = "保存掃描紀錄",
                    description = "紀錄只保存在此裝置",
                    icon = Icons.Outlined.History,
                    checked = repository.saveHistory,
                    onCheckedChange = repository::updateSaveHistory,
                )
            }
        }
        item {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Icon(Icons.Outlined.PrivacyTip, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Column {
                        Text("隱私優先", fontWeight = FontWeight.SemiBold)
                        Text(
                            "QR Code 在裝置上辨識，不上傳掃描內容。",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
        }
        item {
            Text(
                "簡單QR掃描 ${BuildConfig.VERSION_NAME}",
                modifier = Modifier.fillMaxWidth(),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@Composable
private fun SettingsSection(
    title: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
    ) {
        Column(Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(9.dp)) {
                Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }
            Spacer(Modifier.height(14.dp))
            content()
        }
    }
}

@Composable
private fun AccentChoice(
    accent: AppAccent,
    selected: Boolean,
    dark: Boolean,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    val preview = accentPreviewColor(accent, dark)
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = if (selected) preview.copy(alpha = 0.18f) else MaterialTheme.colorScheme.surface,
        border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) preview else MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 12.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier.size(34.dp),
                contentAlignment = Alignment.Center,
            ) {
                Surface(modifier = Modifier.fillMaxSize(), shape = CircleShape, color = preview) { }
                if (selected) Icon(Icons.Outlined.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(20.dp))
            }
            Spacer(Modifier.height(7.dp))
            Text(accent.title, style = MaterialTheme.typography.labelMedium, maxLines = 1)
        }
    }
}

@Composable
private fun ModeChoice(
    title: String,
    selected: Boolean,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    modifier: Modifier,
    onClick: () -> Unit,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = RoundedCornerShape(18.dp),
        color = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface,
        border = BorderStroke(1.dp, if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant),
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Icon(icon, contentDescription = null, tint = if (selected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(Modifier.height(7.dp))
            Text(title, style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun SettingSwitch(
    title: String,
    description: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onCheckedChange(!checked) }
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
        Column(Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium)
            Text(description, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}
