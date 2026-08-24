package com.appfinanza.cruscotto.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.appfinanza.cruscotto.ui.common.formattaEuro
import com.appfinanza.cruscotto.ui.common.formattaPercentuale
import com.appfinanza.cruscotto.ui.cruscotto.VoceRipartizione
import com.appfinanza.cruscotto.ui.theme.ColoriRipartizione

/**
 * Grafico a ciambella con la ripartizione del mese (tasse, spese, stipendio, fondo, futuro),
 * equivalente alla tabella O18:P23 del foglio Cruscotto.
 */
@Composable
fun PieChartRipartizione(voci: List<VoceRipartizione>, modifier: Modifier = Modifier) {
    val totale = voci.sumOf { it.importo }.coerceAtLeast(0.01)

    Row(modifier = modifier, verticalAlignment = androidx.compose.ui.Alignment.CenterVertically) {
        Canvas(
            modifier = Modifier
                .size(140.dp)
                .aspectRatio(1f)
        ) {
            var angoloIniziale = -90f
            val spessore = size.minDimension * 0.28f
            voci.forEachIndexed { indice, voce ->
                val angoloSweep = (voce.importo / totale * 360.0).toFloat()
                drawArc(
                    color = ColoriRipartizione[indice % ColoriRipartizione.size],
                    startAngle = angoloIniziale,
                    sweepAngle = angoloSweep.coerceAtLeast(0f),
                    useCenter = false,
                    topLeft = Offset(spessore / 2, spessore / 2),
                    size = Size(size.width - spessore, size.height - spessore),
                    style = Stroke(width = spessore)
                )
                angoloIniziale += angoloSweep
            }
        }

        Column(Modifier.padding(start = 16.dp)) {
            voci.forEachIndexed { indice, voce ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 3.dp),
                    verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .background(ColoriRipartizione[indice % ColoriRipartizione.size], CircleShape)
                    )
                    Column(Modifier.padding(start = 8.dp)) {
                        Text(voce.etichetta, style = MaterialTheme.typography.bodyMedium)
                        Text(
                            text = "${formattaEuro(voce.importo)} · ${formattaPercentuale(voce.importo / totale)}",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }
        }
    }
}
