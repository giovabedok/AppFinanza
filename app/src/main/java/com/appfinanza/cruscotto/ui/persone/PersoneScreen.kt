package com.appfinanza.cruscotto.ui.persone

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appfinanza.cruscotto.ui.common.formattaEuro
import com.appfinanza.cruscotto.ui.components.SchedaBordo
import com.appfinanza.cruscotto.ui.theme.Crema
import com.appfinanza.cruscotto.ui.theme.Grigio
import com.appfinanza.cruscotto.ui.theme.Petrolio
import com.appfinanza.cruscotto.ui.theme.Senape
import com.appfinanza.cruscotto.ui.theme.Teal
import kotlin.math.roundToInt

@Composable
fun PersoneScreen(viewModel: PersoneViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        SchedaBordo {
            Column(Modifier.padding(16.dp)) {
                Text("ANNO ${uiState.anno}", style = MaterialTheme.typography.labelSmall, color = Grigio)
                Text(
                    formattaEuro(uiState.totaleAnno),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Petrolio,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = "${uiState.pazienti.size} ${if (uiState.pazienti.size == 1) "persona seguita" else "persone seguite"} · ${uiState.numeroSedute} sedute",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Grigio
                )
            }
        }

        if (uiState.nomeConcentrato != null) {
            SchedaBordo(
                colore = androidx.compose.ui.graphics.Color(0xFFFDF6E8),
                colorebordo = androidx.compose.ui.graphics.Color(0xFFF0DDB0),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    "${uiState.nomeConcentrato} vale il ${(uiState.percentualeConcentrato * 100).roundToInt()}% degli incassi dell'anno. Quando un percorso si chiude, quella quota va sostituita: tienilo presente nella programmazione.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = androidx.compose.ui.graphics.Color(0xFF8A6516),
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        if (uiState.pazienti.isEmpty()) {
            SchedaBordo(modifier = Modifier.padding(top = 16.dp)) {
                Text(
                    "Nessuna seduta registrata nel ${uiState.anno}.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Grigio,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth().padding(24.dp)
                )
            }
        } else {
            Column(Modifier.padding(top = 16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                uiState.pazienti.forEach { paziente ->
                    RigaPaziente(paziente, uiState.totaleAnno)
                }
            }
        }
    }
}

@Composable
private fun RigaPaziente(paziente: VocePaziente, totaleAnno: Double) {
    val peso = if (totaleAnno > 0) paziente.totale / totaleAnno else 0.0
    SchedaBordo {
        Column(Modifier.padding(12.dp)) {
            Row(Modifier.fillMaxWidth()) {
                Column(Modifier.weight(1f)) {
                    Text(paziente.nome, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
                    Text(
                        text = "${paziente.sedute} sedute · ultima ${if (paziente.giorniUltimaVisita <= 0) "oggi" else "${paziente.giorniUltimaVisita} giorni fa"}",
                        style = MaterialTheme.typography.labelSmall,
                        color = Grigio
                    )
                }
                Column(horizontalAlignment = androidx.compose.ui.Alignment.End) {
                    Text(formattaEuro(paziente.totale), style = MaterialTheme.typography.bodyLarge)
                    Text("${(peso * 100).roundToInt()}%", style = MaterialTheme.typography.labelSmall, color = Grigio)
                }
            }
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp)
                    .height(4.dp)
                    .clip(RoundedCornerShape(50))
                    .background(Crema)
            ) {
                Box(
                    Modifier
                        .fillMaxWidth(peso.toFloat().coerceIn(0f, 1f))
                        .fillMaxSize()
                        .clip(RoundedCornerShape(50))
                        .background(if (peso > 0.3) Senape else Teal)
                )
            }
        }
    }
}
