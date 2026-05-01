package com.example.pokeapi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeapi.domain.model.Pokemon
import com.example.pokeapi.domain.model.PokemonType
import com.example.pokeapi.domain.repository.PokemonRepository
import com.example.pokeapi.util.NetworkConnectivityObserver
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PokemonRepository,
    private val connectivityObserver: NetworkConnectivityObserver
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private var currentOffset = 0
    private val limit = 20
    private var isFiltering = false

    init {
        observeNetwork()
        loadTypes()
        loadPokemonList()
    }

    private fun observeNetwork() {
        viewModelScope.launch {
            connectivityObserver.observe().collectLatest { status ->
                val isOnline = status == NetworkConnectivityObserver.Status.Available
                _uiState.value = _uiState.value.copy(isOnline = isOnline)
            }
        }
    }

    private fun loadTypes() {
        viewModelScope.launch {
            try {
                val types = repository.getPokemonTypes()
                _uiState.value = _uiState.value.copy(types = types)
            } catch (e: Exception) {
                // Ignore if offline
            }
        }
    }

    fun loadPokemonList() {
        if (_uiState.value.isLoading || isFiltering) return
        
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val newPokemon = repository.getPokemonList(limit, currentOffset)
                currentOffset += limit
                
                val currentList = _uiState.value.pokemonList
                _uiState.value = _uiState.value.copy(
                    pokemonList = currentList + newPokemon,
                    isLoading = false
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }

    fun searchPokemon(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
    }

    fun filterByType(type: PokemonType?) {
        _uiState.value = _uiState.value.copy(selectedType = type)
        viewModelScope.launch {
            if (type == null) {
                isFiltering = false
                currentOffset = 0
                _uiState.value = _uiState.value.copy(pokemonList = emptyList())
                loadPokemonList()
            } else {
                isFiltering = true
                _uiState.value = _uiState.value.copy(isLoading = true, pokemonList = emptyList())
                try {
                    val filtered = repository.getPokemonByType(type.id)
                    _uiState.value = _uiState.value.copy(
                        pokemonList = filtered,
                        isLoading = false
                    )
                } catch (e: Exception) {
                    _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
                }
            }
        }
    }
}

data class HomeUiState(
    val pokemonList: List<Pokemon> = emptyList(),
    val types: List<PokemonType> = emptyList(),
    val selectedType: PokemonType? = null,
    val searchQuery: String = "",
    val isLoading: Boolean = false,
    val isOnline: Boolean = true,
    val errorMessage: String? = null
) {
    val filteredList: List<Pokemon>
        get() = if (searchQuery.isBlank()) {
            pokemonList
        } else {
            pokemonList.filter { it.name.contains(searchQuery, ignoreCase = true) }
        }
}
