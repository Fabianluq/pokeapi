package com.example.pokeapi.ui.theme

import androidx.compose.ui.graphics.Color

// === Background & Surface ===
val PokeBackground    = Color(0xFF0D0D1A)
val PokeSurface       = Color(0xFF16162A)
val PokeSurfaceVariant = Color(0xFF1E1E38)
val PokeCardSurface   = Color(0xFF1C1C34)

// === Brand Colors ===
val PokeRed           = Color(0xFFE8283C)
val PokeRedDark       = Color(0xFFB01E2D)
val PokeYellow        = Color(0xFFF4C931)
val PokeBlue          = Color(0xFF3D7DCA)
val PokeWhite         = Color(0xFFF5F5F5)

// === Text ===
val TextPrimary       = Color(0xFFF0F0FF)
val TextSecondary     = Color(0xFFAAAAAA)
val TextHint          = Color(0xFF666680)

// === Type Colors ===
val TypeNormal    = Color(0xFFA8A878)
val TypeFire      = Color(0xFFF08030)
val TypeWater     = Color(0xFF6890F0)
val TypeGrass     = Color(0xFF78C850)
val TypeElectric  = Color(0xFFF8D030)
val TypeIce       = Color(0xFF98D8D8)
val TypeFighting  = Color(0xFFC03028)
val TypePoison    = Color(0xFFA040A0)
val TypeGround    = Color(0xFFE0C068)
val TypeFlying    = Color(0xFFA890F0)
val TypePsychic   = Color(0xFFF85888)
val TypeBug       = Color(0xFFA8B820)
val TypeRock      = Color(0xFFB8A038)
val TypeGhost     = Color(0xFF705898)
val TypeDragon    = Color(0xFF7038F8)
val TypeDark      = Color(0xFF705848)
val TypeSteel     = Color(0xFFB8B8D0)
val TypeFairy     = Color(0xFFEE99AC)

fun typeColor(typeName: String): Color = when (typeName.lowercase()) {
    "fire"     -> TypeFire
    "water"    -> TypeWater
    "grass"    -> TypeGrass
    "electric" -> TypeElectric
    "ice"      -> TypeIce
    "fighting" -> TypeFighting
    "poison"   -> TypePoison
    "ground"   -> TypeGround
    "flying"   -> TypeFlying
    "psychic"  -> TypePsychic
    "bug"      -> TypeBug
    "rock"     -> TypeRock
    "ghost"    -> TypeGhost
    "dragon"   -> TypeDragon
    "dark"     -> TypeDark
    "steel"    -> TypeSteel
    "fairy"    -> TypeFairy
    else       -> TypeNormal
}

// Generates a soft gradient pair from a Pokémon ID (for list cards without type info)
fun cardGradientColors(pokemonId: Int): Pair<Color, Color> {
    val palettes = listOf(
        Pair(Color(0xFF3D7DCA), Color(0xFF1A3A6E)),   // blue
        Pair(Color(0xFFF08030), Color(0xFF7A3D10)),   // fire
        Pair(Color(0xFF78C850), Color(0xFF3A6020)),   // grass
        Pair(Color(0xFFF8D030), Color(0xFF8A6A00)),   // electric
        Pair(Color(0xFFA040A0), Color(0xFF501060)),   // poison
        Pair(Color(0xFF98D8D8), Color(0xFF406060)),   // ice
        Pair(Color(0xFFE8283C), Color(0xFF8A0E1E)),   // red
        Pair(Color(0xFF705898), Color(0xFF302040)),   // ghost
        Pair(Color(0xFFA8B820), Color(0xFF505800)),   // bug
        Pair(Color(0xFF7038F8), Color(0xFF300880)),   // dragon
    )
    return palettes[(pokemonId - 1) % palettes.size]
}