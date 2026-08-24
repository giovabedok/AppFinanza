package com.appfinanza.cruscotto.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = BluNotte,
    onPrimary = Color.White,
    primaryContainer = CremaGiallo,
    onPrimaryContainer = BluNotte,
    secondary = VerdeAccento,
    onSecondary = Color.White,
    secondaryContainer = VerdeChiaro,
    onSecondaryContainer = BluNotte,
    tertiary = AranciAccento,
    background = Color.White,
    onBackground = BluNotte,
    surface = Color.White,
    onSurface = BluNotte,
    surfaceVariant = GrigioChiaro,
    onSurfaceVariant = BluNotteChiaro,
    error = RossoAvviso
)

private val DarkColors = darkColorScheme(
    primary = CremaGiallo,
    onPrimary = BluNotte,
    primaryContainer = BluNotteChiaro,
    onPrimaryContainer = CremaGiallo,
    secondary = VerdeAccento,
    onSecondary = Color.White,
    tertiary = AranciAccento,
    background = Color(0xFF10202F),
    onBackground = Color(0xFFEDEDED),
    surface = Color(0xFF16283A),
    onSurface = Color(0xFFEDEDED),
    surfaceVariant = Color(0xFF223650),
    onSurfaceVariant = Color(0xFFCBD5E1),
    error = Color(0xFFE57373)
)

@Composable
fun AppFinanzaTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColors else LightColors
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppFinanzaTypography,
        content = content
    )
}
