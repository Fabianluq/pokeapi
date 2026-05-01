package com.example.pokeapi.data.repository

import com.example.pokeapi.data.local.dao.PokemonDao

import com.example.pokeapi.data.local.entity.PokemonEntity
import com.example.pokeapi.data.remote.PokeApiService
import com.example.pokeapi.domain.model.Pokemon
import com.example.pokeapi.domain.model.PokemonDetail
import com.example.pokeapi.domain.model.PokemonType
import com.example.pokeapi.domain.repository.PokemonRepository

import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepositoryImpl @Inject constructor(

    private val apiService: PokeApiService,
    private val pokemonDao: PokemonDao
) : PokemonRepository {

    override suspend fun getPokemonList(limit: Int, offset: Int): List<Pokemon> {
        return try {
            val response = apiService.getPokemonList(limit, offset)
            val pokemonList = response.results.map { 
                Pokemon(name = it.name, url = it.url) 
            }
            val entities = pokemonList.map { 
                PokemonEntity(id = it.id, name = it.name, imageUrl = it.imageUrl) 
            }
            pokemonDao.insertAll(entities)
            
            pokemonList
        } catch (e: Exception) {
            val localData = pokemonDao.getPokemonList(limit, offset)
            localData.map { 
                Pokemon(name = it.name, url = "https://pokeapi.co/api/v2/pokemon/${it.id}/") 
            }
        }
    }

    override suspend fun getPokemonDetail(id: Int): PokemonDetail {
        val response = apiService.getPokemonDetail(id)
        return PokemonDetail(
            id = response.id,
            name = response.name,
            height = response.height,
            weight = response.weight,
            types = response.types.map { it.type.name },
            imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/${response.id}.png"
        )
    }

    override suspend fun getPokemonTypes(): List<PokemonType> {
        val response = apiService.getPokemonTypes()
        return response.results.map { 
            PokemonType(name = it.name, url = it.url) 
        }
    }

    override suspend fun getPokemonByType(typeId: Int): List<Pokemon> {
        val response = apiService.getPokemonByType(typeId)
        return response.pokemon.map { 
            Pokemon(name = it.pokemon.name, url = it.pokemon.url) 

        }
    }
}
