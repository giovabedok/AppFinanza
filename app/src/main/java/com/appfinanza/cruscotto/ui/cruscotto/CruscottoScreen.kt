package com.appfinanza.cruscotto.ui.cruscotto

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appfinanza.cruscotto.ui.common.VOCI_RIPARTIZIONE
import com.appfinanza.cruscotto.ui.common.formattaEuro
import com.appfinanza.cruscotto.ui.components.AndamentoAnnuale
import com.appfinanza.cruscotto.ui.components.BarraAutonomia
import com.appfinanza.cruscotto.ui.components.NastroRipartizione
import com.appfinanza.cruscotto.ui.components.NavMese
import com.appfinanza.cruscotto.ui.components.SchedaBordo
import com.appfinanza.cruscotto.ui.theme.Bordo
import com.appfinanza.cruscotto.ui.theme.Corallo
import com.appfinanza.cruscotto.ui.theme.Grigio
import com.appfinanza.cruscotto.ui.theme.Petrolio
import com.appfinanza.cruscotto.ui.theme.RossoErrore
import com.appfinanza.cruscotto.ui.theme.Senape

@Composable
fun CruscottoScreen(viewModel: CruscottoViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 20.dp)
    ) {
        NavMese(
            anno = uiState.anno,
            mese = uiState.mese,
            onCambiaPeriodo = viewModel::cambiaPeriodo,
            modifier = Modifier.padding(top = 16.dp)
        )

        if (!uiState.percentualiCorrette) {
            SchedaBordo(
                colore = RossoErrore.copy(alpha = 0.08f),
                colorebordo = RossoErrore.copy(alpha = 0.35f),
                modifier = Modifier.padding(top = 12.dp)
            ) {
                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.WarningAmber, contentDescription = null, tint = RossoErrore)
                    Text(
                        "Le percentuali nella scheda Regole non sommano al 100%.",
                        color = RossoErrore,
                        modifier = Modifier.padding(start = 8.dp)
                    )
                }
            }
        }

        SchedaBordo(modifier = Modifier.padding(top = 16.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("INCASSATO QUESTO MESE", style = MaterialTheme.typography.labelSmall, color = Grigio)
                Text(
                    text = formattaEuro(uiState.incassiDelMese),
                    style = MaterialTheme.typography.headlineMedium.copy(fontSize = 40.sp),
                    color = Petrolio,
                    modifier = Modifier.padding(top = 4.dp)
                )
                Text(
                    text = if (uiState.numeroSedute == 1) "1 seduta registrata" else "${uiState.numeroSedute} sedute registrate",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Grigio,
                    modifier = Modifier.padding(top = 2.dp)
                )
                NastroRipartizione(
                    percentuali = uiState.percentuali,
                    importi = uiState.ripartizioneMese.map { it.importo },
                    modifier = Modifier.padding(top = 16.dp)
                )
            }
        }

        Column(Modifier.padding(top = 16.dp)) {
            uiState.ripartizioneMese.forEachIndexed { indice, voce ->
                RigaVoce(
                    etichetta = voce.etichetta,
                    nota = VOCI_RIPARTIZIONE.getOrNull(indice)?.nota.orEmpty(),
                    colore = VOCI_RIPARTIZIONE.getOrNull(indice)?.colore ?: Petrolio,
                    importo = voce.importo,
                    percentuale = uiState.percentuali.getOrElse(indice) { 0.0 },
                    inEvidenza = indice == 2,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
            }
        }

        val quotaSpese = uiState.incassiDelMese * uiState.percentuali.getOrElse(1) { 0.0 }
        SchedaBordo(modifier = Modifier.padding(top = 8.dp)) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("SPESE PROFESSIONALI DEL MESE", style = MaterialTheme.typography.labelSmall, color = Grigio)
                    Text(
                        formattaEuro(uiState.speseProfessionaliDelMese),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (quotaSpese > 0 && uiState.speseProfessionaliDelMese > quotaSpese) RossoErrore
                        else MaterialTheme.colorScheme.onSurface
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .height(8.dp)
                        .clip(RoundedCornerShape(50))
                        .background(MaterialTheme.colorScheme.surfaceVariant)
                        .border(1.dp, Bordo, RoundedCornerShape(50))
                ) {
                    val frazione = if (quotaSpese > 0) (uiState.speseProfessionaliDelMese / quotaSpese).coerceIn(0.0, 1.0) else 0.0
                    Box(
                        Modifier
                            .fillMaxWidth(frazione.toFloat())
                            .fillMaxSize()
                            .clip(RoundedCornerShape(50))
                            .background(if (uiState.speseProfessionaliDelMese > quotaSpese) Corallo else Senape)
                    )
                }
                Text(
                    text = when {
                        quotaSpese <= 0 -> "Registra un incasso per calcolare la quota prevista."
                        uiState.speseProfessionaliDelMese > quotaSpese ->
                            "Hai superato di ${formattaEuro(uiState.speseProfessionaliDelMese - quotaSpese)} la quota prevista di ${formattaEuro(quotaSpese)}."
                        else -> "Rientri nella quota prevista di ${formattaEuro(quotaSpese)}."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = Grigio,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        SchedaBordo(modifier = Modifier.padding(top = 16.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("MESI DI AUTONOMIA", style = MaterialTheme.typography.labelSmall, color = Grigio)
                Row(Modifier.padding(top = 4.dp), verticalAlignment = Alignment.Bottom) {
                    Text(
                        text = String.format("%.1f", uiState.mesiAutonomia),
                        style = MaterialTheme.typography.headlineMedium,
                        color = Petrolio
                    )
                    Text(
                        " mesi coperti",
                        style = MaterialTheme.typography.bodyLarge,
                        color = Grigio,
                        modifier = Modifier.padding(bottom = 6.dp, start = 4.dp)
                    )
                }
                BarraAutonomia(mesiAutonomia = uiState.mesiAutonomia, modifier = Modifier.padding(top = 12.dp))
                Text(
                    "Con ${formattaEuro(uiState.fondoSicurezzaAccumulato)} da parte e ${formattaEuro(uiState.spesePersonaliMedie)} di spese personali al mese. Obiettivo: da quattro a sei mesi.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Grigio,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }

        if (uiState.prossimeScadenze.isNotEmpty()) {
            SchedaBordo(modifier = Modifier.padding(top = 16.dp)) {
                Column(Modifier.padding(16.dp)) {
                    Text("PROSSIME SCADENZE", style = MaterialTheme.typography.labelSmall, color = Grigio)
                    Column(Modifier.padding(top = 12.dp)) {
                        uiState.prossimeScadenze.forEach { scadenza ->
                            val urgente = scadenza.giorni <= 30
                            Row(
                                modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    Icons.Filled.Event,
                                    contentDescription = null,
                                    tint = if (urgente) Corallo else Grigio,
                                    modifier = Modifier.size(18.dp)
                                )
                                Text(
                                    scadenza.titolo,
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(start = 10.dp).weight(1f)
                                )
                                Text(
                                    text = when {
                                        scadenza.giorni < 0 -> "scaduta"
                                        scadenza.giorni == 0L -> "oggi"
                                        else -> "fra ${scadenza.giorni} g"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (urgente) Corallo else Grigio
                                )
                            }
                        }
                    }
                }
            }
        }

        SchedaBordo(modifier = Modifier.padding(top = 16.dp, bottom = 16.dp)) {
            Column(Modifier.padding(16.dp)) {
                Text("ULTIMI DODICI MESI", style = MaterialTheme.typography.labelSmall, color = Grigio)
                AndamentoAnnuale(
                    dati = uiState.datiMensili,
                    meseSelezionato = uiState.mese,
                    onSeleziona = { m -> viewModel.cambiaPeriodo(uiState.anno, m) },
                    modifier = Modifier.padding(top = 12.dp)
                )
            }
        }
    }
}

@Composable
private fun RigaVoce(
    etichetta: String,
    nota: String,
    colore: Color,
    importo: Double,
    percentuale: Double,
    inEvidenza: Boolean,
    modifier: Modifier = Modifier
) {
    SchedaBordo(
        colore = if (inEvidenza) Corallo.copy(alpha = 0.08f) else MaterialTheme.colorScheme.surface,
        colorebordo = if (inEvidenza) Corallo.copy(alpha = 0.35f) else Bordo,
        modifier = modifier
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                Modifier
                    .width(4.dp)
                    .height(36.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(colore)
            )
            Column(Modifier.padding(start = 12.dp).weight(1f)) {
                Text(etichetta, style = MaterialTheme.typography.bodyLarge, fontWeight = if (inEvidenza) FontWeight.Bold else FontWeight.Medium)
                Text(nota, style = MaterialTheme.typography.labelSmall, color = Grigio)
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    formattaEuro(importo),
                    style = if (inEvidenza) MaterialTheme.typography.titleLarge else MaterialTheme.typography.titleMedium,
                    fontWeight = if (inEvidenza) FontWeight.Bold else FontWeight.Normal
                )
                Text("${(percentuale * 100).let { if (it == it.toInt().toDouble()) it.toInt().toString() else "%.0f".format(it) }}%", style = MaterialTheme.typography.labelSmall, color = Grigio)
            }
        }
    }
}
