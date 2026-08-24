package com.appfinanza.cruscotto.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appfinanza.cruscotto.ui.cruscotto.DatoMensile
import com.appfinanza.cruscotto.ui.theme.Petrolio
import com.appfinanza.cruscotto.ui.theme.Senape

/**
 * Grafico a barre affiancate Entrate/Uscite per i 12 mesi dell'anno,
 * equivalente al grafico costruito dai dati O3:Q15 del foglio Cruscotto.
 */
@Composable
fun BarChartEntrateUscite(dati: List<DatoMensile>, modifier: Modifier = Modifier) {
    val textMeasurer = rememberTextMeasurer()
    val coloreEntrate = Petrolio
    val coloreUscite = Senape
    val coloreGriglia = MaterialTheme.colorScheme.outlineVariant
    val coloreTesto = MaterialTheme.colorScheme.onSurfaceVariant

    Column(modifier) {
        Row(modifier = Modifier.padding(bottom = 8.dp)) {
            LegendaVoce(colore = coloreEntrate, testo = "Incassi")
            Spacer(modifier = Modifier.width(16.dp))
            LegendaVoce(colore = coloreUscite, testo = "Spese")
        }
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height(220.dp)
        ) {
            val massimo = (dati.maxOfOrNull { maxOf(it.incassi, it.spese) } ?: 0.0).coerceAtLeast(1.0)
            val larghezzaEtichette = 32.dp.toPx()
            val areaGrafico = size.height - larghezzaEtichette
            val larghezzaGruppo = size.width / dati.size
            val larghezzaBarra = larghezzaGruppo / 3.2f

            val numeroLinee = 4
            for (i in 0..numeroLinee) {
                val y = areaGrafico - (areaGrafico * i / numeroLinee)
                drawLine(
                    color = coloreGriglia,
                    start = Offset(0f, y),
                    end = Offset(size.width, y),
                    strokeWidth = 1.dp.toPx()
                )
            }

            dati.forEachIndexed { indice, dato ->
                val centroGruppo = larghezzaGruppo * indice + larghezzaGruppo / 2
                val altezzaEntrate = (dato.incassi / massimo * areaGrafico).toFloat()
                val altezzaUscite = (dato.spese / massimo * areaGrafico).toFloat()

                drawRect(
                    color = coloreEntrate,
                    topLeft = Offset(centroGruppo - larghezzaBarra - 2.dp.toPx(), areaGrafico - altezzaEntrate),
                    size = Size(larghezzaBarra, altezzaEntrate)
                )
                drawRect(
                    color = coloreUscite,
                    topLeft = Offset(centroGruppo + 2.dp.toPx(), areaGrafico - altezzaUscite),
                    size = Size(larghezzaBarra, altezzaUscite)
                )

                val etichetta = dato.etichetta.take(3)
                val risultatoTesto = textMeasurer.measure(
                    AnnotatedString(etichetta),
                    style = TextStyle(fontSize = 10.sp, color = coloreTesto, textAlign = TextAlign.Center)
                )
                drawText(
                    textLayoutResult = risultatoTesto,
                    topLeft = Offset(centroGruppo - risultatoTesto.size.width / 2, areaGrafico + 8.dp.toPx())
                )
            }

            drawLine(
                color = coloreGriglia,
                start = Offset(0f, areaGrafico),
                end = Offset(size.width, areaGrafico),
                strokeWidth = 1.dp.toPx()
            )
        }
    }
}

@Composable
private fun LegendaVoce(colore: Color, testo: String) {
    Row {
        Box(
            modifier = Modifier
                .padding(top = 4.dp)
                .size(10.dp)
                .background(colore, CircleShape)
        )
        Text(
            text = "  $testo",
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
