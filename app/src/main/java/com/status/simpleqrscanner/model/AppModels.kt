package com.status.simpleqrscanner.model

enum class QrContentType(val title: String) {
    URL("網址"),
    WIFI("Wi-Fi"),
    PHONE("電話"),
    EMAIL("電子郵件"),
    SMS("簡訊"),
    LOCATION("位置"),
    CONTACT("聯絡人"),
    CALENDAR("行事曆"),
    TEXT("文字"),
}

data class ScanRecord(
    val id: Long,
    val content: String,
    val type: QrContentType,
    val scannedAt: Long,
)

fun classifyQrContent(content: String): QrContentType {
    val value = content.trim()
    return when {
        value.startsWith("http://", true) ||
            value.startsWith("https://", true) ||
            value.startsWith("www.", true) -> QrContentType.URL
        value.startsWith("WIFI:", true) -> QrContentType.WIFI
        value.startsWith("tel:", true) -> QrContentType.PHONE
        value.startsWith("mailto:", true) ||
            Regex("^[^@\\s]+@[^@\\s]+\\.[^@\\s]+$").matches(value) -> QrContentType.EMAIL
        value.startsWith("sms:", true) || value.startsWith("smsto:", true) -> QrContentType.SMS
        value.startsWith("geo:", true) -> QrContentType.LOCATION
        value.startsWith("BEGIN:VCARD", true) || value.startsWith("MECARD:", true) -> QrContentType.CONTACT
        value.startsWith("BEGIN:VEVENT", true) -> QrContentType.CALENDAR
        else -> QrContentType.TEXT
    }
}
