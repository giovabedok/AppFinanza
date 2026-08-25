package com.appfinanza.cruscotto.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.appfinanza.cruscotto.R
import com.appfinanza.cruscotto.ui.common.ViewModelFactory
import com.appfinanza.cruscotto.ui.cruscotto.CruscottoScreen
import com.appfinanza.cruscotto.ui.cruscotto.CruscottoViewModel
import com.appfinanza.cruscotto.ui.impostazioni.ImportExportViewModel
import com.appfinanza.cruscotto.ui.incassi.IncassiViewModel
import com.appfinanza.cruscotto.ui.movimenti.MovimentiScreen
import com.appfinanza.cruscotto.ui.persone.PersoneScreen
import com.appfinanza.cruscotto.ui.persone.PersoneViewModel
import com.appfinanza.cruscotto.ui.ripartizione.RipartizioneScreen
import com.appfinanza.cruscotto.ui.ripartizione.RipartizioneViewModel
import com.appfinanza.cruscotto.ui.spese.SpeseViewModel
import com.appfinanza.cruscotto.ui.studio.StudioScreen
import com.appfinanza.cruscotto.ui.studio.StudioViewModel
import com.appfinanza.cruscotto.ui.theme.Grigio
import com.appfinanza.cruscotto.ui.theme.Petrolio
import kotlinx.coroutines.launch

@Composable
fun AppFinanzaApp(factory: ViewModelFactory) {
    var vistaSelezionata by remember { mutableStateOf(AppDestination.IL_MESE) }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    val cruscottoViewModel: CruscottoViewModel = viewModel(factory = factory)
    val incassiViewModel: IncassiViewModel = viewModel(factory = factory)
    val speseViewModel: SpeseViewModel = viewModel(factory = factory)
    val studioViewModel: StudioViewModel = viewModel(factory = factory)
    val personeViewModel: PersoneViewModel = viewModel(factory = factory)
    val ripartizioneViewModel: RipartizioneViewModel = viewModel(factory = factory)
    val importExportViewModel: ImportExportViewModel = viewModel(factory = factory)

    val periodo by cruscottoViewModel.uiState.collectAsStateWithLifecycle()

    fun mostraMessaggio(testo: String) {
        scope.launch { snackbarHostState.showSnackbar(testo) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) { Snackbar(it) } }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            Column(Modifier.padding(horizontal = 20.dp, vertical = 20.dp)) {
                Text(
                    text = stringResource(R.string.app_name),
                    style = MaterialTheme.typography.headlineMedium,
                    color = Petrolio
                )
                Text(
                    text = stringResource(R.string.app_byline),
                    style = MaterialTheme.typography.labelSmall,
                    color = Grigio,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = stringResource(R.string.app_tagline),
                    style = MaterialTheme.typography.bodyLarge,
                    color = Grigio,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            TabBarSegmentata(
                selezionata = vistaSelezionata,
                onSeleziona = { vistaSelezionata = it },
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
            )

            Box(Modifier.weight(1f).fillMaxWidth()) {
                when (vistaSelezionata) {
                    AppDestination.IL_MESE -> CruscottoScreen(cruscottoViewModel)
                    AppDestination.MOVIMENTI -> MovimentiScreen(
                        incassiViewModel = incassiViewModel,
                        speseViewModel = speseViewModel,
                        tariffa = studioViewModel.uiState.collectAsStateWithLifecycle().value.tariffa,
                        anno = periodo.anno,
                        mese = periodo.mese,
                        onCambiaPeriodo = cruscottoViewModel::cambiaPeriodo
                    )
                    AppDestination.STUDIO -> StudioScreen(studioViewModel, cruscottoViewModel::cambiaPeriodo)
                    AppDestination.PERSONE -> PersoneScreen(personeViewModel)
                    AppDestination.REGOLE -> RipartizioneScreen(ripartizioneViewModel, importExportViewModel, ::mostraMessaggio)
                }
            }
        }
    }
}
