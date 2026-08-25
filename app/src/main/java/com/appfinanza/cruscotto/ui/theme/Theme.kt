package com.appfinanza.cruscotto.ui.theme

import androidx.compose.material3.MaterialTheme
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

/**
 * Il mockup di riferimento è pensato solo per un tema chiaro: le card hanno
 * sfondo chiaro fisso, quindi qui il colore rimane sempre quello chiaro
 * anche se il sistema è in modalità scura, per non ritrovarsi testo chiaro
 * su sfondo chiaro (o viceversa) in nessun punto dell'app.
 */
@Composable
fun AppFinanzaTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = LightColors,
        typography = AppFinanzaTypography,
        content = content
    )
}
