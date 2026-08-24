package com.appfinanza.cruscotto.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChevronLeft
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.appfinanza.cruscotto.ui.theme.Grigio
import com.appfinanza.cruscotto.ui.theme.Petrolio
import com.appfinanza.cruscotto.ui.theme.Teal
import java.time.YearMonth
import java.time.format.TextStyle
import java.util.Locale

/** Selettore ‹ Mese Anno › con sotto-etichetta (numero voci, totale) usato in Incassi e Spese. */
@Composable
fun SelettoreMeseSemplice(
    mese: YearMonth,
    sottoEtichetta: String,
    onCambia: (YearMonth) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { onCambia(mese.minusMonths(1)) }) {
            Icon(Icons.Filled.ChevronLeft, contentDescription = "Mese precedente", tint = Teal)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            val nomeMese = mese.month.getDisplayName(TextStyle.FULL, Locale.ITALIAN).replaceFirstChar { it.uppercase() }
            Text("$nomeMese ${mese.year}", style = MaterialTheme.typography.titleLarge, color = Petrolio)
            Text(sottoEtichetta, style = MaterialTheme.typography.labelSmall, color = Grigio)
        }
        IconButton(onClick = { onCambia(mese.plusMonths(1)) }) {
            Icon(Icons.Filled.ChevronRight, contentDescription = "Mese successivo", tint = Teal)
        }
    }
}
