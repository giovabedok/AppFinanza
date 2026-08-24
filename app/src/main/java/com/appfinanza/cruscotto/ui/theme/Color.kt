package com.appfinanza.cruscotto.ui.theme

import androidx.compose.ui.graphics.Color

// Palette calda ispirata al mockup di riferimento: crema, petrolio, corallo, senape, salvia.
val Crema = Color(0xFFFBF7EF)
val Carta = Color(0xFFFFFFFF)
val Petrolio = Color(0xFF0F4C5C)
val PetrolioScuro = Color(0xFF0B3743)
val Teal = Color(0xFF1B7F84)
val Corallo = Color(0xFFE8735C)
val Senape = Color(0xFFE0A32E)
val Salvia = Color(0xFF6E9E7A)
val Blu = Color(0xFF4A7FA5)
val Inchiostro = Color(0xFF22383D)
val Grigio = Color(0xFF7C8B8E)
val Bordo = Color(0xFFE6DFD2)
val RossoErrore = Color(0xFFA8412C)
val VerdeSuccesso = Color(0xFF3D6B4C)

// Sfondo scuro dei form (dentro alle card in Petrolio) e loro bordo.
val PetrolioCampo = Color(0xFF17606F)
val PetrolioBordoCampo = Color(0xFF2C7686)

// Colori delle cinque voci di ripartizione, coerenti con la ciambella e il "nastro".
val ColoreTasse = Petrolio
val ColoreSpeseProfessionali = Senape
val ColoreStipendio = Corallo
val ColoreFondoSicurezza = Blu
val ColoreFuturo = Salvia

val ColoriRipartizione = listOf(
    ColoreTasse,
    ColoreSpeseProfessionali,
    ColoreStipendio,
    ColoreFondoSicurezza,
    ColoreFuturo
)
