package com.status.simpleqrscanner.model

import org.junit.Assert.assertEquals
import org.junit.Test

class AppModelsTest {
    @Test
    fun `classifies supported QR payloads`() {
        val cases = mapOf(
            "https://example.com" to QrContentType.URL,
            "www.example.com" to QrContentType.URL,
            "WIFI:T:WPA;S:Home;P:secret;;" to QrContentType.WIFI,
            "tel:+886912345678" to QrContentType.PHONE,
            "hello@example.com" to QrContentType.EMAIL,
            "smsto:0912345678:Hello" to QrContentType.SMS,
            "geo:25.033,121.565" to QrContentType.LOCATION,
            "BEGIN:VCARD\nFN:王小明\nEND:VCARD" to QrContentType.CONTACT,
            "BEGIN:VEVENT\nSUMMARY:會議\nEND:VEVENT" to QrContentType.CALENDAR,
            "一般文字" to QrContentType.TEXT,
        )

        cases.forEach { (content, expected) ->
            assertEquals(content, expected, classifyQrContent(content))
        }
    }

    @Test
    fun `classification ignores leading and trailing whitespace`() {
        assertEquals(QrContentType.URL, classifyQrContent("  https://example.com  "))
    }
}
