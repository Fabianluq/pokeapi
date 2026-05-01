package com.example.pokeapi.ui.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeapi.domain.model.Pokemon
import com.example.pokeapi.domain.repository.PokemonRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: PokemonRepository
) : ViewModel() {

    private val _state = MutableStateFlow(HomeState())
    val state: StateFlow<HomeState> = _state.asStateFlow()

    private var currentOffset = 0
    private val limit = 20

    init {
        // Collect DB updates
        repository.getPokemonsFlow().onEach { pokemons ->
            _state.value = _state.value.copy(
                pokemons = if (_state.value.isFiltering) _state.value.pokemons else pokemons,
                isLoading = false
            )
        }.launchIn(viewModelScope)

        loadNextPage()
        loadTypes()
    }

    fun loadNextPage() {
        if (_state.value.isLoading || _state.value.isFiltering) return

        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true)
            repository.fetchPokemons(limit, currentOffset)
            currentOffset += limit
            // Flow will automatically update UI with new pokemons
        }
    }

    private fun loadTypes() {
        viewModelScope.launch {
            val result = repository.getTypes()
            result.onSuccess { types ->
                _state.value = _state.value.copy(types = types)
            }
        }
    }

    fun filterByType(typeId: String) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, isFiltering = true)
            val result = repository.fetchPokemonsByType(typeId)
            result.onSuccess { filtered ->
                _state.value = _state.value.copy(pokemons = filtered, isLoading = false)
            }.onFailure {
                _state.value = _state.value.copy(isLoading = false, error = "Failed to load type")
            }
        }
    }

    fun clearFilter() {
        _state.value = _state.value.copy(isFiltering = false, isLoading = true)
        // Reset offset to refetch from DB flow
        viewModelScope.launch {
            val currentPokemons = repository.getPokemonsFlow()
            _state.value = _state.value.copy(isLoading = false)
            // It will update from flow automatically
        }
    }
}

data class HomeState(
    val pokemons: List<Pokemon> = emptyList(),
    val types: List<Pair<String, String>> = emptyList(),
    val isLoading: Boolean = false,
    val isFiltering: Boolean = false,
    val error: String? = null
)
