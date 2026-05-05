package com.example.pokeapi.ui.home

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.pokeapi.domain.model.Pokemon
import com.example.pokeapi.domain.model.PokemonType
import com.example.pokeapi.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToDetail: (Int) -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyGridState()

    // Pagination trigger
    LaunchedEffect(listState.canScrollForward) {
        if (!listState.canScrollForward && !uiState.isLoading &&
            uiState.selectedType == null && uiState.searchQuery.isBlank()
        ) {
            viewModel.loadPokemonList()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PokeBackground)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ──────────────────────────────────────────────────────
            PokedexHeader(isOnline = uiState.isOnline)

            // ── Search + Filter ─────────────────────────────────────────────
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                SearchBar(
                    query = uiState.searchQuery,
                    onQueryChange = { viewModel.searchPokemon(it) }
                )

                if (uiState.types.isNotEmpty()) {
                    TypeFilterRow(
                        types = uiState.types,
                        selectedType = uiState.selectedType,
                        onTypeSelected = { viewModel.filterByType(it) }
                    )
                }
            }

            // ── Pokémon Grid ────────────────────────────────────────────────
            if (uiState.filteredList.isEmpty() && !uiState.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("😕", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "No se encontraron resultados",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    state = listState,
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(uiState.filteredList, key = { it.id }) { pokemon ->
                        AnimatedPokemonCard(
                            pokemon = pokemon,
                            onClick = { onNavigateToDetail(pokemon.id) }
                        )
                    }
                    if (uiState.isLoading) {
                        item {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(
                                    color = PokeRed,
                                    strokeWidth = 3.dp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Header
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun PokedexHeader(isOnline: Boolean) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color(0xFF1A0510), PokeRedDark, PokeRed)
                )
            )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = "Pokédex",
                    style = MaterialTheme.typography.headlineLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        color = PokeWhite
                    )
                )
                Text(
                    text = "Elige tu Pokémon",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = PokeWhite.copy(alpha = 0.7f),
                        letterSpacing = 1.sp
                    )
                )
            }
            // Pokéball decoration
            Box(
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .border(3.dp, PokeWhite.copy(alpha = 0.3f), CircleShape)
                    .background(PokeWhite.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Text("⚡", fontSize = 24.sp)
            }
        }

        // Offline indicator inside header
        if (!isOnline) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .background(Color(0xDD1A0A00))
                    .padding(horizontal = 16.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("📡", fontSize = 12.sp)
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "Sin conexión — datos locales",
                    color = PokeYellow,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Search Bar
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun SearchBar(query: String, onQueryChange: (String) -> Unit) {
    TextField(
        value = query,
        onValueChange = onQueryChange,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp)),
        placeholder = {
            Text(
                "Buscar Pokémon...",
                color = TextHint,
                style = MaterialTheme.typography.bodyMedium
            )
        },
        leadingIcon = {
            Icon(Icons.Default.Search, contentDescription = null, tint = TextHint)
        },
        singleLine = true,
        colors = TextFieldDefaults.colors(
            focusedContainerColor    = PokeSurfaceVariant,
            unfocusedContainerColor  = PokeSurfaceVariant,
            focusedIndicatorColor    = Color.Transparent,
            unfocusedIndicatorColor  = Color.Transparent,
            focusedTextColor         = TextPrimary,
            unfocusedTextColor       = TextPrimary,
            cursorColor              = PokeRed,
        )
    )
}

// ─────────────────────────────────────────────────────────────────────────────
// Type Filter Row
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun TypeFilterRow(
    types: List<PokemonType>,
    selectedType: PokemonType?,
    onTypeSelected: (PokemonType?) -> Unit
) {
    LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        item {
            TypeChip(
                label = "Todos",
                color = PokeRed,
                selected = selectedType == null,
                onClick = { onTypeSelected(null) }
            )
        }
        items(types) { type ->
            val color = typeColor(type.name)
            TypeChip(
                label = type.name.capitalize(Locale.current),
                color = color,
                selected = selectedType?.id == type.id,
                onClick = { onTypeSelected(type) }
            )
        }
    }
}

@Composable
private fun TypeChip(label: String, color: Color, selected: Boolean, onClick: () -> Unit) {
    val bgColor = if (selected) color else color.copy(alpha = 0.15f)
    val textColor = if (selected) Color.White else color

    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(bgColor)
            .border(1.dp, color.copy(alpha = if (selected) 0f else 0.4f), RoundedCornerShape(20.dp))
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 6.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            color = textColor,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Pokémon Card
// ─────────────────────────────────────────────────────────────────────────────
@Composable
private fun AnimatedPokemonCard(pokemon: Pokemon, onClick: () -> Unit) {
    val (colorStart, colorEnd) = cardGradientColors(pokemon.id)

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(0.85f)
            .shadow(8.dp, RoundedCornerShape(20.dp))
            .clip(RoundedCornerShape(20.dp))
            .clickable { onClick() },
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.linearGradient(colors = listOf(colorStart, colorEnd))
                )
        ) {
            // Decorative Pokéball watermark
            Text(
                text = "◎",
                fontSize = 90.sp,
                color = Color.White.copy(alpha = 0.07f),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .offset(x = 16.dp, y = 16.dp)
            )

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Number badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    Text(
                        text = "#${pokemon.id.toString().padStart(3, '0')}",
                        style = MaterialTheme.typography.labelMedium.copy(
                            color = Color.White.copy(alpha = 0.6f),
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Sprite
                AsyncImage(
                    model = pokemon.imageUrl,
                    contentDescription = pokemon.name,
                    modifier = Modifier.size(90.dp),
                    contentScale = ContentScale.Fit
                )

                // Name
                Text(
                    text = pokemon.name.capitalize(Locale.current),
                    style = MaterialTheme.typography.titleMedium.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    ),
                    textAlign = TextAlign.Center,
                    maxLines = 1
                )
            }
        }
    }
}
