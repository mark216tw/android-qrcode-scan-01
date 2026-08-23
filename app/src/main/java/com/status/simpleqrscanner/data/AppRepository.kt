package com.status.simpleqrscanner.data

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.core.content.edit
import com.status.simpleqrscanner.model.QrContentType
import com.status.simpleqrscanner.model.ScanRecord
import org.json.JSONArray
import org.json.JSONObject

enum class AppAccent(val title: String) {
    VIOLET("葡萄紫"),
    OCEAN("海洋藍"),
    MINT("薄荷綠"),
    CORAL("珊瑚紅"),
    SUNSHINE("陽光橘"),
    BERRY("莓果粉"),
}

enum class AppMode { LIGHT, DARK }

class AppRepository(context: Context) {
    private val preferences = context.getSharedPreferences("simple_qr_preferences", Context.MODE_PRIVATE)

    var accent by mutableStateOf(
        runCatching { AppAccent.valueOf(preferences.getString(KEY_ACCENT, null).orEmpty()) }
            .getOrDefault(AppAccent.VIOLET),
    )
        private set

    var mode by mutableStateOf(
        runCatching { AppMode.valueOf(preferences.getString(KEY_MODE, null).orEmpty()) }
            .getOrDefault(AppMode.LIGHT),
    )
        private set

    var soundEnabled by mutableStateOf(preferences.getBoolean(KEY_SOUND, true))
        private set

    var vibrationEnabled by mutableStateOf(preferences.getBoolean(KEY_VIBRATION, true))
        private set

    var saveHistory by mutableStateOf(preferences.getBoolean(KEY_SAVE_HISTORY, true))
        private set

    val history = mutableStateListOf<ScanRecord>()

    init {
        loadHistory()
    }

    fun updateAccent(value: AppAccent) {
        accent = value
        preferences.edit { putString(KEY_ACCENT, value.name) }
    }

    fun updateMode(value: AppMode) {
        mode = value
        preferences.edit { putString(KEY_MODE, value.name) }
    }

    fun updateSoundEnabled(value: Boolean) {
        soundEnabled = value
        preferences.edit { putBoolean(KEY_SOUND, value) }
    }

    fun updateVibrationEnabled(value: Boolean) {
        vibrationEnabled = value
        preferences.edit { putBoolean(KEY_VIBRATION, value) }
    }

    fun updateSaveHistory(value: Boolean) {
        saveHistory = value
        preferences.edit { putBoolean(KEY_SAVE_HISTORY, value) }
    }

    fun addScan(content: String, type: QrContentType): ScanRecord {
        val now = System.currentTimeMillis()
        val record = ScanRecord(id = now, content = content, type = type, scannedAt = now)
        if (saveHistory) {
            history.add(0, record)
            while (history.size > MAX_HISTORY) history.removeAt(history.lastIndex)
            persistHistory()
        }
        return record
    }

    fun deleteScan(record: ScanRecord) {
        history.remove(record)
        persistHistory()
    }

    fun clearHistory() {
        history.clear()
        persistHistory()
    }

    private fun loadHistory() {
        val raw = preferences.getString(KEY_HISTORY, null) ?: return
        runCatching {
            val array = JSONArray(raw)
            repeat(array.length()) { index ->
                val item = array.getJSONObject(index)
                history += ScanRecord(
                    id = item.getLong("id"),
                    content = item.getString("content"),
                    type = QrContentType.valueOf(item.getString("type")),
                    scannedAt = item.getLong("scannedAt"),
                )
            }
        }.onFailure {
            history.clear()
            preferences.edit { remove(KEY_HISTORY) }
        }
    }

    private fun persistHistory() {
        val array = JSONArray()
        history.forEach { record ->
            array.put(
                JSONObject()
                    .put("id", record.id)
                    .put("content", record.content)
                    .put("type", record.type.name)
                    .put("scannedAt", record.scannedAt),
            )
        }
        preferences.edit { putString(KEY_HISTORY, array.toString()) }
    }

    private companion object {
        const val KEY_ACCENT = "accent"
        const val KEY_MODE = "mode"
        const val KEY_SOUND = "sound"
        const val KEY_VIBRATION = "vibration"
        const val KEY_SAVE_HISTORY = "save_history"
        const val KEY_HISTORY = "history"
        const val MAX_HISTORY = 200
    }
}
