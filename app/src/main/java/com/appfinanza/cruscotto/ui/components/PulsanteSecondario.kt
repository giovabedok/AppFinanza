package com.appfinanza.cruscotto.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.appfinanza.cruscotto.ui.theme.Bordo
import com.appfinanza.cruscotto.ui.theme.Crema
import com.appfinanza.cruscotto.ui.theme.Petrolio

/**
 * Pulsante secondario chiaro e bordato (sfondo crema, testo petrolio), lo
 * stile usato nel mockup per le azioni "Salva…" e "Aggiungi…" non primarie,
 * al posto del pulsante Material pieno.
 */
@Composable
fun PulsanteSecondario(
    testo: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    abilitato: Boolean = true
) {
    OutlinedButton(
        onClick = onClick,
        enabled = abilitato,
        colors = ButtonDefaults.outlinedButtonColors(containerColor = Crema, contentColor = Petrolio),
        border = BorderStroke(1.dp, Bordo),
        modifier = modifier
    ) {
        Text(testo, fontWeight = FontWeight.Bold)
    }
}
