package io.github.muntashirakon.AppManager.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

// Core M3 color palettes – minimal custom colors since XML theme already styles views.
private val LightColors: ColorScheme = lightColorScheme()
private val DarkColors: ColorScheme = darkColorScheme()

@Composable
fun AppTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    useDynamicColor: Boolean = true, // dynamic colors when available
    pureBlack: Boolean = false,      // AMOLED mode
    content: @Composable () -> Unit
) {
    val context = LocalContext.current

    // Prefer dynamic colors on Android 12+
    val colors = if (useDynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
        if (useDarkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
    } else {
        if (useDarkTheme) DarkColors else LightColors
    }.let { scheme ->
        // If AMOLED/pure black requested, force surface/background to black and adjust on-colors
        if (useDarkTheme && pureBlack) scheme.copy(
            background = androidx.compose.ui.graphics.Color.Black,
            surface = androidx.compose.ui.graphics.Color.Black,
            surfaceVariant = androidx.compose.ui.graphics.Color(0xFF121212)
        ) else scheme
    }

    MaterialTheme(
        colorScheme = colors,
        // Typography and shapes can be extended later if needed; defaults are fine for now
        content = content
    )
}