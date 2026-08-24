package com.appfinanza.cruscotto.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.appfinanza.cruscotto.ui.theme.Bordo
import com.appfinanza.cruscotto.ui.theme.Crema
import com.appfinanza.cruscotto.ui.theme.Salvia

/** Sei tacche che si riempiono in base ai mesi di autonomia disponibili. */
@Composable
fun BarraAutonomia(mesiAutonomia: Double, modifier: Modifier = Modifier) {
    Row(modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(4.dp)) {
        repeat(6) { indice ->
            val pieno = (mesiAutonomia - indice).coerceIn(0.0, 1.0)
            val frazioneAnimata by animateFloatAsState(
                targetValue = pieno.toFloat(),
                animationSpec = tween(600),
                label = "autonomia$indice"
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(10.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Crema)
                    .border(1.dp, Bordo, RoundedCornerShape(50))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth(frazioneAnimata.coerceAtLeast(0f))
                        .clip(RoundedCornerShape(50))
                        .background(Salvia)
                )
            }
        }
    }
}
