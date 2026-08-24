package com.appfinanza.cruscotto.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.appfinanza.cruscotto.ui.common.formattaEuro
import com.appfinanza.cruscotto.ui.cruscotto.DatoMensile
import com.appfinanza.cruscotto.ui.theme.Bordo
import com.appfinanza.cruscotto.ui.theme.Grigio
import com.appfinanza.cruscotto.ui.theme.Petrolio
import com.appfinanza.cruscotto.ui.theme.Teal

/**
 * Andamento dei mesi dell'anno: una barra per l'incasso di ogni mese, con la
 * media dei mesi con incassi come riga tratteggiata e il mese selezionato in
 * evidenza. Toccare una barra seleziona quel mese.
 */
@Composable
fun AndamentoAnnuale(
    dati: List<DatoMensile>,
    meseSelezionato: Int,
    onSeleziona: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val massimo = (dati.maxOfOrNull { it.incassi } ?: 0.0).coerceAtLeast(1.0)
    val conValore = dati.filter { it.incassi > 0.0 }
    val media = if (conValore.isNotEmpty()) conValore.sumOf { it.incassi } / conValore.size else 0.0
    val piuBasso = conValore.minOfOrNull { it.incassi }

    Column(modifier) {
        Box(Modifier.fillMaxWidth().height(120.dp)) {
            if (media > 0.0) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val y = size.height - (media / massimo * size.height).toFloat()
                    drawLine(
                        color = Grigio.copy(alpha = 0.5f),
                        start = Offset(0f, y),
                        end = Offset(size.width, y),
                        strokeWidth = 1.dp.toPx(),
                        pathEffect = PathEffect.dashPathEffect(floatArrayOf(6f, 6f))
                    )
                }
            }
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                dati.forEach { dato ->
                    val selezionato = dato.numeroMese == meseSelezionato
                    val frazione = if (dato.incassi > 0) (dato.incassi / massimo).toFloat().coerceAtLeast(0.04f) else 0.02f
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable { onSeleziona(dato.numeroMese) },
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .fillMaxHeight(frazione)
                                .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                .background(
                                    when {
                                        selezionato -> Teal
                                        dato.incassi > 0 -> Color(0xFFBFD4D4)
                                        else -> Bordo
                                    }
                                )
                        )
                        Text(
                            text = dato.etichetta.take(1),
                            style = MaterialTheme.typography.labelSmall,
                            color = if (selezionato) Petrolio else Grigio,
                            fontWeight = if (selezionato) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.padding(top = 5.dp)
                        )
                    }
                }
            }
        }
        Row(
            modifier = Modifier.fillMaxWidth().padding(top = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text("MEDIA DEI MESI CON INCASSI", style = MaterialTheme.typography.labelSmall, color = Grigio)
                Text(formattaEuro(media), style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text("MESE PIÙ BASSO", style = MaterialTheme.typography.labelSmall, color = Grigio)
                Text(
                    piuBasso?.let { formattaEuro(it) } ?: "—",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}
