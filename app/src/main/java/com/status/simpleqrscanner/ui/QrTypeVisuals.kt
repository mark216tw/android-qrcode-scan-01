package com.status.simpleqrscanner.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.AlternateEmail
import androidx.compose.material.icons.outlined.ContactPage
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Language
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Sms
import androidx.compose.material.icons.outlined.TextFields
import androidx.compose.material.icons.outlined.Wifi
import androidx.compose.ui.graphics.vector.ImageVector
import com.status.simpleqrscanner.model.QrContentType

fun QrContentType.icon(): ImageVector = when (this) {
    QrContentType.URL -> Icons.Outlined.Language
    QrContentType.WIFI -> Icons.Outlined.Wifi
    QrContentType.PHONE -> Icons.Outlined.Phone
    QrContentType.EMAIL -> Icons.Outlined.AlternateEmail
    QrContentType.SMS -> Icons.Outlined.Sms
    QrContentType.LOCATION -> Icons.Outlined.LocationOn
    QrContentType.CONTACT -> Icons.Outlined.ContactPage
    QrContentType.CALENDAR -> Icons.Outlined.Event
    QrContentType.TEXT -> Icons.Outlined.TextFields
}
