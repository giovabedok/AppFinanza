package com.appfinanza.cruscotto.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.appfinanza.cruscotto.ui.common.ViewModelFactory
import com.appfinanza.cruscotto.ui.cruscotto.CruscottoScreen
import com.appfinanza.cruscotto.ui.cruscotto.CruscottoViewModel
import com.appfinanza.cruscotto.ui.impostazioni.ImportExportViewModel
import com.appfinanza.cruscotto.ui.impostazioni.ImpostazioniScreen
import com.appfinanza.cruscotto.ui.incassi.IncassiScreen
import com.appfinanza.cruscotto.ui.incassi.IncassiViewModel
import com.appfinanza.cruscotto.ui.ripartizione.RipartizioneScreen
import com.appfinanza.cruscotto.ui.ripartizione.RipartizioneViewModel
import com.appfinanza.cruscotto.ui.spese.SpeseScreen
import com.appfinanza.cruscotto.ui.spese.SpeseViewModel

@Composable
fun AppFinanzaApp(factory: ViewModelFactory) {
    val navController = rememberNavController()

    Scaffold(
        bottomBar = {
            NavigationBar {
                val backStackEntry by navController.currentBackStackEntryAsState()
                val currentDestination = backStackEntry?.destination

                AppDestination.entries.forEach { destinazione ->
                    val selezionata = currentDestination?.hierarchy?.any { it.route == destinazione.route } == true
                    NavigationBarItem(
                        selected = selezionata,
                        onClick = {
                            navController.navigate(destinazione.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
                        icon = { Icon(destinazione.icona, contentDescription = destinazione.etichetta) },
                        label = { Text(destinazione.etichetta) }
                    )
                }
            }
        }
    ) { paddingInterno ->
        NavHost(
            navController = navController,
            startDestination = AppDestination.CRUSCOTTO.route,
            modifier = androidx.compose.ui.Modifier.padding(paddingInterno)
        ) {
            composable(AppDestination.CRUSCOTTO.route) {
                val viewModel: CruscottoViewModel = viewModel(factory = factory)
                CruscottoScreen(viewModel)
            }
            composable(AppDestination.INCASSI.route) {
                val viewModel: IncassiViewModel = viewModel(factory = factory)
                IncassiScreen(viewModel)
            }
            composable(AppDestination.SPESE.route) {
                val viewModel: SpeseViewModel = viewModel(factory = factory)
                SpeseScreen(viewModel)
            }
            composable(AppDestination.RIPARTIZIONE.route) {
                val viewModel: RipartizioneViewModel = viewModel(factory = factory)
                RipartizioneScreen(viewModel)
            }
            composable(AppDestination.IMPOSTAZIONI.route) {
                val viewModel: ImportExportViewModel = viewModel(factory = factory)
                ImpostazioniScreen(viewModel)
            }
        }
    }
}
