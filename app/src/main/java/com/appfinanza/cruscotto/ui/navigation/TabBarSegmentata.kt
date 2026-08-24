package com.appfinanza.cruscotto.ui.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

/**
 * Selettore a pillola con le quattro sezioni dell'app, come nel mockup:
 * sfondo tenue con la scheda attiva sollevata su una pillola bianca.
 */
@Composable
fun TabBarSegmentata(
    selezionata: AppDestination,
    onSeleziona: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFFEFE9DD), RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        AppDestination.entries.forEach { destinazione ->
            val attiva = destinazione == selezionata
            Text(
                text = destinazione.etichetta,
                textAlign = TextAlign.Center,
                fontWeight = if (attiva) FontWeight.Bold else FontWeight.Medium,
                color = if (attiva) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSeleziona(destinazione) }
                    .background(if (attiva) Color.White else Color.Transparent, RoundedCornerShape(9.dp))
                    .padding(vertical = 10.dp)
            )
        }
    }
}
