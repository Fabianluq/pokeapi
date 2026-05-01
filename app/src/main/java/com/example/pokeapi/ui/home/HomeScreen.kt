package com.example.pokeapi.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.example.pokeapi.core.util.NetworkConnectivityObserver

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeViewModel = hiltViewModel(),
    onNavigateToDetail: (String) -> Unit
) {
    val state by viewModel.state.collectAsState()
    val context = LocalContext.current
    val networkObserver = remember { NetworkConnectivityObserver(context) }
    val networkStatus by networkObserver.observe().collectAsState(
        initial = NetworkConnectivityObserver.Status.Available
    )

    var showFilterDialog by remember { mutableStateOf(false) }
    var showSearchDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("PokeAPI") },
                actions = {
                    IconButton(onClick = { showSearchDialog = true }) {
                        Icon(Icons.Default.Search, contentDescription = "Search")
                    }
                    IconButton(onClick = { showFilterDialog = true }) {
                        Icon(Icons.Default.List, contentDescription = "Filter")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Network Indicator
            if (networkStatus != NetworkConnectivityObserver.Status.Available) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.Red.copy(alpha = 0.8f))
                        .padding(8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Offline Mode - Showing cached data", color = Color.White)
                }
            }
            
            // Filter indicator
            if (state.isFiltering) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Filtro activo")
                    TextButton(onClick = { viewModel.clearFilter() }) {
                        Text("Limpiar Filtro")
                    }
                }
            }

            val listState = rememberLazyGridState()

            // Infinite Scroll Logic
            LaunchedEffect(listState) {
                snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
                    .collect { lastVisibleItemIndex ->
                        if (lastVisibleItemIndex != null) {
                            if (lastVisibleItemIndex >= state.pokemons.size - 4 && !state.isLoading && !state.isFiltering && networkStatus == NetworkConnectivityObserver.Status.Available) {
                                viewModel.loadNextPage()
                            }
                        }
                    }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                state = listState,
                contentPadding = PaddingValues(8.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                items(state.pokemons) { pokemon ->
                    PokemonCard(
                        name = pokemon.name,
                        imageUrl = pokemon.imageUrl,
                        onClick = { onNavigateToDetail(pokemon.name) }
                    )
                }
                
                if (state.isLoading) {
                    item {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }

    if (showFilterDialog) {
        FilterDialog(
            types = state.types,
            onDismiss = { showFilterDialog = false },
            onTypeSelected = { typeId ->
                viewModel.filterByType(typeId)
                showFilterDialog = false
            }
        )
    }

    if (showSearchDialog) {
        SearchDialog(
            onDismiss = { showSearchDialog = false },
            onSearch = { idOrName ->
                onNavigateToDetail(idOrName)
                showSearchDialog = false
            }
        )
    }
}

@Composable
fun PokemonCard(name: String, imageUrl: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = name,
                modifier = Modifier.size(100.dp),
                contentScale = ContentScale.Fit
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = name.replaceFirstChar { it.uppercase() }, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun FilterDialog(
    types: List<Pair<String, String>>,
    onDismiss: () -> Unit,
    onTypeSelected: (String) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filtrar por Tipo") },
        text = {
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                items(types) { type ->
                    TextButton(onClick = { onTypeSelected(type.second) }) {
                        Text(type.first.replaceFirstChar { it.uppercase() })
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) { Text("Cerrar") }
        }
    )
}

@Composable
fun SearchDialog(
    onDismiss: () -> Unit,
    onSearch: (String) -> Unit
) {
    var query by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Buscar Pokémon") },
        text = {
            OutlinedTextField(
                value = query,
                onValueChange = { query = it },
                label = { Text("Nombre o ID exacto") },
                singleLine = true
            )
        },
        confirmButton = {
            Button(onClick = { if (query.isNotBlank()) onSearch(query) }) {
                Text("Buscar")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancelar") }
        }
    )
}
