package com.example.pokeapi.ui.detail

import androidx.lifecycle.SavedStateHandle

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pokeapi.domain.model.PokemonDetail
import com.example.pokeapi.domain.repository.PokemonRepository
import com.example.pokeapi.util.NetworkConnectivityObserver

import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class DetailViewModel @Inject constructor(
    private val repository: PokemonRepository,
    private val connectivityObserver: NetworkConnectivityObserver,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetailUiState())
    val uiState: StateFlow<DetailUiState> = _uiState.asStateFlow()

    private val pokemonId: Int = checkNotNull(savedStateHandle["pokemonId"])

    init {
        loadDetail()
        viewModelScope.launch {
            connectivityObserver.observe().collect { status ->
                _uiState.value = _uiState.value.copy(
                    isOnline = status == NetworkConnectivityObserver.Status.Available

                )
            }
        }
    }

    private fun loadDetail() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            try {
                val detail = repository.getPokemonDetail(pokemonId)
                _uiState.value = _uiState.value.copy(detail = detail, isLoading = false)
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(isLoading = false, errorMessage = e.message)
            }
        }
    }
}

data class DetailUiState(
    val detail: PokemonDetail? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val isOnline: Boolean = true

)
