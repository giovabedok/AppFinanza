package com.appfinanza.cruscotto.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.appfinanza.cruscotto.ui.theme.Bordo
import com.appfinanza.cruscotto.ui.theme.Carta

/**
 * Card piatta con bordo sottile e angoli arrotondati, senza ombra: lo stile
 * "carta" usato in tutto il mockup di riferimento, al posto della Card
 * Material con elevazione.
 */
@Composable
fun SchedaBordo(
    modifier: Modifier = Modifier,
    colore: Color = Carta,
    colorebordo: Color = Bordo,
    contenuto: @Composable () -> Unit
) {
    androidx.compose.foundation.layout.Column(
        modifier = modifier
            .fillMaxWidth()
            .background(colore, RoundedCornerShape(16.dp))
            .border(1.dp, colorebordo, RoundedCornerShape(16.dp))
    ) {
        contenuto()
    }
}
