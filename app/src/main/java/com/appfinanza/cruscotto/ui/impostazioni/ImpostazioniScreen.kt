package com.appfinanza.cruscotto.ui.impostazioni

import android.content.Context
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.TableChart
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.appfinanza.cruscotto.data.io.FormatoFile
import com.appfinanza.cruscotto.data.io.formatoDaEstensione
import com.appfinanza.cruscotto.ui.components.PulsanteSecondario
import com.appfinanza.cruscotto.ui.components.SchedaBordo
import com.appfinanza.cruscotto.ui.theme.Corallo
import com.appfinanza.cruscotto.ui.theme.Grigio
import com.appfinanza.cruscotto.ui.theme.Petrolio
import com.appfinanza.cruscotto.ui.theme.Salvia
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Sezione "I tuoi dati": esportazione/importazione in CSV, Excel e TXT.
 * Incorporata nella scheda Regole, senza una propria Scaffold: gli avvisi
 * (esito di esportazione/importazione) sono mostrati dallo snackbar condiviso
 * dell'app tramite [mostraMessaggio].
 */
@Composable
fun SezioneDatiEImpostazioni(
    viewModel: ImportExportViewModel,
    mostraMessaggio: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val risultatoImportazione by viewModel.risultatoImportazione.collectAsStateWithLifecycle()

    LaunchedEffect(risultatoImportazione) {
        val risultato = risultatoImportazione ?: return@LaunchedEffect
        mostraMessaggio(
            if (risultato.vuoto) "Nessuna riga valida trovata nel file selezionato."
            else buildString {
                append("Importati ${risultato.incassiImportati} incassi, ${risultato.speseImportate} spese")
                if (risultato.scadenzeImportate > 0) append(", ${risultato.scadenzeImportate} scadenze")
                if (risultato.impostazioniAggiornate) append(" e le impostazioni")
                append(".")
            }
        )
        viewModel.azzeraRisultato()
    }

    val esportaCsv = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(FormatoFile.CSV.mimeType)) { uri ->
        uri?.let { esporta(context, it, FormatoFile.CSV, scope, viewModel, mostraMessaggio) }
    }
    val esportaXlsx = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(FormatoFile.XLSX.mimeType)) { uri ->
        uri?.let { esporta(context, it, FormatoFile.XLSX, scope, viewModel, mostraMessaggio) }
    }
    val esportaTxt = rememberLauncherForActivityResult(ActivityResultContracts.CreateDocument(FormatoFile.TXT.mimeType)) { uri ->
        uri?.let { esporta(context, it, FormatoFile.TXT, scope, viewModel, mostraMessaggio) }
    }
    val importaFile = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
        uri?.let { importa(context, it, scope, viewModel, mostraMessaggio) }
    }

    SchedaBordo(modifier = modifier) {
        Column(Modifier.padding(16.dp)) {
            Text("I TUOI DATI", style = MaterialTheme.typography.labelSmall, color = Grigio)
            Text(
                "Backup completo: sedute, spese, scadenze e impostazioni, nel formato che preferisci.",
                style = MaterialTheme.typography.bodyMedium,
                color = Grigio,
                modifier = Modifier.padding(top = 4.dp, bottom = 12.dp)
            )
            RigaFormato(Icons.Filled.TableChart, "CSV", "Compatibile con Excel e Fogli Google.", Salvia) {
                esportaCsv.launch("il_mio_studio_backup.csv")
            }
            RigaFormato(Icons.Filled.GridOn, "Excel (.xlsx)", "Foglio di calcolo pronto da aprire.", Petrolio) {
                esportaXlsx.launch("il_mio_studio_backup.xlsx")
            }
            RigaFormato(Icons.Filled.Description, "TXT", "Testo semplice, leggibile ovunque.", Corallo) {
                esportaTxt.launch("il_mio_studio_backup.txt")
            }
            PulsanteSecondario(
                testo = "Importa un backup (.csv, .xlsx, .txt)",
                onClick = { importaFile.launch(arrayOf("*/*")) },
                modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
            )
        }
    }
}

@Composable
private fun RigaFormato(icona: ImageVector, titolo: String, descrizione: String, colore: Color, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.size(36.dp).background(colore.copy(alpha = 0.14f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(icona, contentDescription = null, tint = colore, modifier = Modifier.size(18.dp))
        }
        Column(Modifier.padding(start = 12.dp).weight(1f)) {
            Text(titolo, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.SemiBold)
            Text(descrizione, style = MaterialTheme.typography.labelSmall, color = Grigio)
        }
        Icon(Icons.Filled.FileDownload, contentDescription = "Esporta in $titolo", tint = Grigio)
    }
}

private fun esporta(
    context: Context,
    uri: Uri,
    formato: FormatoFile,
    scope: kotlinx.coroutines.CoroutineScope,
    viewModel: ImportExportViewModel,
    mostraMessaggio: (String) -> Unit
) {
    scope.launch {
        val riuscito = runCatching {
            withContext(Dispatchers.IO) {
                if (formato == FormatoFile.XLSX) {
                    val contenuto = viewModel.generaContenutoXlsx()
                    context.contentResolver.openOutputStream(uri)?.use { it.write(contenuto) }
                } else {
                    val contenuto = viewModel.generaContenutoDelimitato(formato)
                    context.contentResolver.openOutputStream(uri)?.use { it.write(contenuto.toByteArray(Charsets.UTF_8)) }
                }
            }
        }.isSuccess
        mostraMessaggio(if (riuscito) "Esportazione ${formato.etichetta} completata." else "Esportazione non riuscita.")
    }
}

private fun importa(
    context: Context,
    uri: Uri,
    scope: kotlinx.coroutines.CoroutineScope,
    viewModel: ImportExportViewModel,
    mostraMessaggio: (String) -> Unit
) {
    scope.launch {
        val nomeFile = nomeVisualizzato(context, uri)
        val formato = formatoDaEstensione(nomeFile)
        if (formato == null) {
            mostraMessaggio("Formato non riconosciuto: usa un file .csv, .xlsx o .txt.")
            return@launch
        }
        val esito = runCatching {
            withContext(Dispatchers.IO) {
                if (formato == FormatoFile.XLSX) {
                    val bytes = context.contentResolver.openInputStream(uri)?.use { it.readBytes() }
                    bytes?.let { viewModel.importaXlsx(it) }
                } else {
                    val testo = context.contentResolver.openInputStream(uri)?.use { it.readBytes().toString(Charsets.UTF_8) }
                    testo?.let { viewModel.importaTesto(it, formato) }
                }
            }
        }
        if (esito.isFailure) {
            mostraMessaggio("Importazione non riuscita: controlla il file selezionato.")
        }
    }
}

private fun nomeVisualizzato(context: Context, uri: Uri): String {
    var nome = uri.lastPathSegment.orEmpty()
    runCatching {
        context.contentResolver.query(uri, null, null, null, null)?.use { cursor ->
            val indice = cursor.getColumnIndex(android.provider.OpenableColumns.DISPLAY_NAME)
            if (indice >= 0 && cursor.moveToFirst()) {
                nome = cursor.getString(indice)
            }
        }
    }
    return nome
}
