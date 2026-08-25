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
import androidx.compose.ui.unit.sp

/** Sfondo "sabbia" tenue condiviso dai selettori a pillola dell'app. */
val Sabbia = Color(0xFFEFE9DD)

/**
 * Selettore a pillola generico: sfondo tenue con la voce attiva sollevata su
 * una pillola bianca. Usato sia per la navigazione principale sia per i
 * toggle interni (ad esempio Sedute/Spese nella scheda Movimenti).
 */
@Composable
fun PillTabBar(
    etichette: List<String>,
    indiceSelezionato: Int,
    onSeleziona: (Int) -> Unit,
    modifier: Modifier = Modifier,
    dimensioneTesto: androidx.compose.ui.unit.TextUnit = 13.sp
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(Sabbia, RoundedCornerShape(12.dp))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        etichette.forEachIndexed { indice, etichetta ->
            val attiva = indice == indiceSelezionato
            Text(
                text = etichetta,
                textAlign = TextAlign.Center,
                fontSize = dimensioneTesto,
                fontWeight = if (attiva) FontWeight.Bold else FontWeight.Medium,
                color = if (attiva) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onSeleziona(indice) }
                    .background(if (attiva) Color.White else Color.Transparent, RoundedCornerShape(9.dp))
                    .padding(vertical = 10.dp)
            )
        }
    }
}

/** Selettore a pillola con le cinque sezioni dell'app. */
@Composable
fun TabBarSegmentata(
    selezionata: AppDestination,
    onSeleziona: (AppDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    val destinazioni = AppDestination.entries
    PillTabBar(
        etichette = destinazioni.map { it.etichetta },
        indiceSelezionato = destinazioni.indexOf(selezionata),
        onSeleziona = { indice -> onSeleziona(destinazioni[indice]) },
        modifier = modifier,
        dimensioneTesto = 12.sp
    )
}
