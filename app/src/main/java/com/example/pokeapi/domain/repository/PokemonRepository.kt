package com.example.pokeapi.domain.repository

import com.example.pokeapi.domain.model.Pokemon
import com.example.pokeapi.domain.model.PokemonDetail
import kotlinx.coroutines.flow.Flow

interface PokemonRepository {
    // Flow that emits the list of pokemon (updates when DB changes)
    fun getPokemonsFlow(): Flow<List<Pokemon>>
    
    // Fetch from API and save to DB
    suspend fun fetchPokemons(limit: Int, offset: Int): Result<Unit>
    
    // Get detail for a specific pokemon (will fetch from API if not in DB)
    suspend fun getPokemonDetail(idOrName: String): Result<PokemonDetail>
    
    // Fetch pokemons by type (Filter)
    suspend fun fetchPokemonsByType(typeId: String): Result<List<Pokemon>>
    
    // Get all types (for the filter dropdown)
    suspend fun getTypes(): Result<List<Pair<String, String>>> // name and url/id
}
