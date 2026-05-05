package com.example.pokeapi.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val PokeColorScheme = darkColorScheme(
    primary            = PokeRed,
    onPrimary          = PokeWhite,
    primaryContainer   = PokeRedDark,
    onPrimaryContainer = PokeWhite,
    secondary          = PokeYellow,
    onSecondary        = PokeBackground,
    tertiary           = PokeBlue,
    onTertiary         = PokeWhite,
    background         = PokeBackground,
    onBackground       = TextPrimary,
    surface            = PokeSurface,
    onSurface          = TextPrimary,
    surfaceVariant     = PokeSurfaceVariant,
    onSurfaceVariant   = TextSecondary,
    error              = Color(0xFFCF6679),
    outline            = Color(0xFF44445A),
)

@Composable
fun PokeapiTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = PokeColorScheme,
        typography  = Typography,
        content     = content
    )
}