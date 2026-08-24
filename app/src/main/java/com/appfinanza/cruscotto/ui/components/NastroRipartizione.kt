package com.appfinanza.cruscotto.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.appfinanza.cruscotto.ui.common.VOCI_RIPARTIZIONE
import com.appfinanza.cruscotto.ui.common.formattaEuro
import com.appfinanza.cruscotto.ui.theme.Bordo
import com.appfinanza.cruscotto.ui.theme.Crema
import com.appfinanza.cruscotto.ui.theme.Grigio
import kotlinx.coroutines.delay
import kotlin.math.roundToInt

/**
 * Il "nastro": l'incasso del mese tagliato in cinque fasce colorate, una per
 * ogni voce di ripartizione. Le larghezze riflettono le percentuali fisse
 * decise nella scheda Regole, non l'incasso del mese (che può anche essere 0).
 */
@Composable
fun NastroRipartizione(
    percentuali: List<Double>,
    importi: List<Double>,
    modifier: Modifier = Modifier
) {
    var evidenziata by remember { mutableIntStateOf(-1) }
    var montato by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(60)
        montato = true
    }

    Column(modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(44.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(Crema)
                .border(1.dp, Bordo, RoundedCornerShape(12.dp))
        ) {
            VOCI_RIPARTIZIONE.forEachIndexed { indice, voce ->
                val quota = percentuali.getOrElse(indice) { 0.0 }
                val attiva = evidenziata == indice
                val larghezzaAnimata by animateFloatAsState(
                    targetValue = if (montato) quota.toFloat().coerceAtLeast(0.0001f) else 0.0001f,
                    animationSpec = tween(700),
                    label = "larghezzaFascia"
                )
                Box(
                    modifier = Modifier
                        .weight(larghezzaAnimata)
                        .fillMaxHeight()
                        .background(voce.colore.copy(alpha = if (evidenziata >= 0 && !attiva) 0.32f else 1f))
                        .clickable { evidenziata = if (attiva) -1 else indice },
                    contentAlignment = Alignment.Center
                ) {
                    if (quota >= 0.09) {
                        Text(
                            text = "${(quota * 100).roundToInt()}%",
                            color = Color.White,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
        Text(
            text = if (evidenziata >= 0) {
                val voce = VOCI_RIPARTIZIONE[evidenziata]
                "${voce.etichetta}: ${formattaEuro(importi.getOrElse(evidenziata) { 0.0 })} — ${voce.nota.lowercase()}"
            } else {
                "Tocca una fascia per vedere quanto vale."
            },
            style = MaterialTheme.typography.bodyMedium,
            color = Grigio,
            modifier = Modifier.padding(top = 8.dp)
        )
    }
}
