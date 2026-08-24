package com.appfinanza.cruscotto.ui.spese

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.Laptop
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.ui.graphics.vector.ImageVector

/** Un'icona riconoscibile per ogni categoria di spesa, per rendere l'elenco più leggibile a colpo d'occhio. */
fun iconaCategoria(categoria: String): ImageVector = when (categoria) {
    "Studio" -> Icons.Filled.Business
    "Commercialista" -> Icons.Filled.Calculate
    "Formazione" -> Icons.Filled.School
    "Supervisione" -> Icons.Filled.Groups
    "Assicurazioni" -> Icons.Filled.HealthAndSafety
    "Software" -> Icons.Filled.Laptop
    "Telefono e Internet" -> Icons.Filled.Wifi
    "Marketing e sito" -> Icons.Filled.Campaign
    "Trasporti" -> Icons.Filled.DirectionsCar
    "Attrezzature" -> Icons.Filled.Build
    else -> Icons.Filled.MoreHoriz
}
