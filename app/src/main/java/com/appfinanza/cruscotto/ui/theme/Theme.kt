package com.appfinanza.cruscotto.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Petrolio,
    onPrimary = Color.White,
    primaryContainer = Crema,
    onPrimaryContainer = Petrolio,
    secondary = Teal,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFDCEDEE),
    onSecondaryContainer = PetrolioScuro,
    tertiary = Corallo,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFFDF0ED),
    onTertiaryContainer = Color(0xFF7A2E1E),
    background = Crema,
    onBackground = Inchiostro,
    surface = Carta,
    onSurface = Inchiostro,
    surfaceVariant = Color(0xFFF3EEE2),
    onSurfaceVariant = Grigio,
    outline = Bordo,
    outlineVariant = Bordo,
    error = RossoErrore,
    onError = Color.White,
    errorContainer = Color(0xFFFDF0ED)
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF7FC4CC),
    onPrimary = PetrolioScuro,
    primaryContainer = PetrolioScuro,
    onPrimaryContainer = Color(0xFFCDEBEE),
    secondary = Color(0xFF6FC6CB),
    onSecondary = Color(0xFF08343A),
    tertiary = Color(0xFFF0A08E),
    onTertiary = Color(0xFF4A180D),
    background = Color(0xFF15211F),
    onBackground = Color(0xFFEDE8DC),
    surface = Color(0xFF1C2B29),
    onSurface = Color(0xFFEDE8DC),
    surfaceVariant = Color(0xFF283937),
    onSurfaceVariant = Color(0xFFB9C4C2),
    outline = Color(0xFF3C4E4B),
    outlineVariant = Color(0xFF3C4E4B),
    error = Color(0xFFE8917F),
    onError = Color(0xFF4A180D)
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
