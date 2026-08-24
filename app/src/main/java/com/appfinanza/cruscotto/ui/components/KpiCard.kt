package com.appfinanza.cruscotto.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.appfinanza.cruscotto.ui.common.formattaEuro

@Composable
fun KpiCard(
    titolo: String,
    valore: Double,
    sottotitolo: String? = null,
    modifier: Modifier = Modifier,
    coloreValore: Color = MaterialTheme.colorScheme.onSurface
) {
    Card(
        modifier = modifier.width(180.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(Modifier.padding(16.dp).fillMaxWidth()) {
            Text(
                text = titolo,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = formattaEuro(valore),
                style = MaterialTheme.typography.titleLarge,
                color = coloreValore,
                modifier = Modifier.padding(top = 4.dp)
            )
            if (sottotitolo != null) {
                Text(
                    text = sottotitolo,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}
