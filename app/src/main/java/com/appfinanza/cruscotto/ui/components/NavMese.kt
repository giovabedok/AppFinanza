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
import com.appfinanza.cruscotto.ui.cruscotto.CruscottoUiState
import com.appfinanza.cruscotto.ui.theme.Grigio
import com.appfinanza.cruscotto.ui.theme.Petrolio
import com.appfinanza.cruscotto.ui.theme.Teal

/**
 * Selettore ‹ Mese Anno › condiviso da tutte le schermate che ragionano su un
 * mese: cambiare mese qui aggiorna lo stesso periodo ovunque nell'app.
 */
@Composable
fun NavMese(
    anno: Int,
    mese: Int,
    onCambiaPeriodo: (Int, Int) -> Unit,
    sottoEtichetta: String? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = {
            val nuovoMese = if (mese == 1) 12 else mese - 1
            val nuovoAnno = if (mese == 1) anno - 1 else anno
            onCambiaPeriodo(nuovoAnno, nuovoMese)
        }) {
            Icon(Icons.Filled.ChevronLeft, contentDescription = "Mese precedente", tint = Teal)
        }
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("${CruscottoUiState.NOMI_MESI[mese - 1]} $anno", style = MaterialTheme.typography.titleLarge, color = Petrolio)
            if (sottoEtichetta != null) {
                Text(sottoEtichetta, style = MaterialTheme.typography.labelSmall, color = Grigio)
            }
        }
        IconButton(onClick = {
            val nuovoMese = if (mese == 12) 1 else mese + 1
            val nuovoAnno = if (mese == 12) anno + 1 else anno
            onCambiaPeriodo(nuovoAnno, nuovoMese)
        }) {
            Icon(Icons.Filled.ChevronRight, contentDescription = "Mese successivo", tint = Teal)
        }
    }
}
