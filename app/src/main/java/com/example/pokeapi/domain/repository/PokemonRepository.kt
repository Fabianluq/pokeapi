package com.example.pokeapi.domain.repository

import com.example.pokeapi.domain.model.Pokemon
import com.example.pokeapi.domain.model.PokemonDetail
import com.example.pokeapi.domain.model.PokemonType

interface PokemonRepository {
    suspend fun getPokemonList(limit: Int, offset: Int): List<Pokemon>
    suspend fun getPokemonDetail(id: Int): PokemonDetail
    suspend fun getPokemonTypes(): List<PokemonType>
    suspend fun getPokemonByType(typeId: Int): List<Pokemon>
}
