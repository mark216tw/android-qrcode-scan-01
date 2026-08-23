package com.status.simpleqrscanner.ui.theme

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.status.simpleqrscanner.data.AppAccent
import com.status.simpleqrscanner.data.AppMode

private data class AccentColors(
    val light: Color,
    val lightContainer: Color,
    val dark: Color,
    val darkContainer: Color,
)

private fun AppAccent.colors(): AccentColors = when (this) {
    AppAccent.VIOLET -> AccentColors(Color(0xFF6555C7), Color(0xFFE6DEFF), Color(0xFFC9BFFF), Color(0xFF493CA6))
    AppAccent.OCEAN -> AccentColors(Color(0xFF006A8A), Color(0xFFC1E8FF), Color(0xFF66D3FF), Color(0xFF004D65))
    AppAccent.MINT -> AccentColors(Color(0xFF006B58), Color(0xFF9EF2D8), Color(0xFF65DCBE), Color(0xFF005143))
    AppAccent.CORAL -> AccentColors(Color(0xFFA63C4A), Color(0xFFFFDADB), Color(0xFFFFB2B8), Color(0xFF842535))
    AppAccent.SUNSHINE -> AccentColors(Color(0xFF805600), Color(0xFFFFDEA5), Color(0xFFFFBA45), Color(0xFF614000))
    AppAccent.BERRY -> AccentColors(Color(0xFF8A4E91), Color(0xFFFFD6FF), Color(0xFFF5AEFB), Color(0xFF6E3676))
}

fun accentPreviewColor(accent: AppAccent, dark: Boolean): Color {
    val colors = accent.colors()
    return if (dark) colors.dark else colors.light
}

@Composable
fun SimpleQrTheme(
    accent: AppAccent,
    mode: AppMode,
    context: Context,
    content: @Composable () -> Unit,
) {
    val dark = when (mode) {
        AppMode.LIGHT -> false
        AppMode.DARK -> true
    }
    val accentColors = accent.colors()
    val scheme = if (dark) {
        darkColorScheme(
            primary = accentColors.dark,
            onPrimary = Color(0xFF1B1730),
            primaryContainer = accentColors.darkContainer,
            onPrimaryContainer = Color.White,
            secondary = accentColors.dark,
            background = Color(0xFF111318),
            surface = Color(0xFF111318),
            surfaceContainer = Color(0xFF1D2026),
            surfaceContainerHigh = Color(0xFF282A31),
        )
    } else {
        lightColorScheme(
            primary = accentColors.light,
            onPrimary = Color.White,
            primaryContainer = accentColors.lightContainer,
            onPrimaryContainer = Color(0xFF211A35),
            secondary = accentColors.light,
            background = Color(0xFFFCF9FF),
            surface = Color(0xFFFCF9FF),
            surfaceContainer = Color(0xFFF1EDF5),
            surfaceContainerHigh = Color(0xFFEAE5EE),
        )
    }

    SideEffect {
        context.findActivity()?.enableEdgeToEdge(
            statusBarStyle = if (dark) {
                SystemBarStyle.dark(scheme.surface.toArgb())
            } else {
                SystemBarStyle.light(scheme.surface.toArgb(), scheme.surface.toArgb())
            },
            navigationBarStyle = if (dark) {
                SystemBarStyle.dark(scheme.surfaceContainer.toArgb())
            } else {
                SystemBarStyle.light(
                    scheme.surfaceContainer.toArgb(),
                    scheme.surfaceContainer.toArgb(),
                )
            },
        )
    }

    MaterialTheme(colorScheme = scheme, content = content)
}

private tailrec fun Context.findActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.findActivity()
    else -> null
}
