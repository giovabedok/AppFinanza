package com.appfinanza.cruscotto.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.PieChart
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.ui.graphics.vector.ImageVector

enum class AppDestination(val route: String, val etichetta: String, val icona: ImageVector) {
    CRUSCOTTO("cruscotto", "Cruscotto", Icons.Filled.Dashboard),
    INCASSI("incassi", "Incassi", Icons.Filled.AccountBalanceWallet),
    SPESE("spese", "Spese", Icons.Filled.Receipt),
    RIPARTIZIONE("ripartizione", "Ripartizione", Icons.Filled.PieChart)
}
