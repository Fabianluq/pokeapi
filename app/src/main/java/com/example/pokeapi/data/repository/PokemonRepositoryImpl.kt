package com.example.pokeapi.data.repository

import com.example.pokeapi.data.local.dao.PokemonDao
import com.example.pokeapi.data.mapper.toDomain
import com.example.pokeapi.data.mapper.toEntity
import com.example.pokeapi.data.remote.ApiService
import com.example.pokeapi.domain.model.Pokemon
import com.example.pokeapi.domain.model.PokemonDetail
import com.example.pokeapi.domain.repository.PokemonRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PokemonRepositoryImpl @Inject constructor(
    private val apiService: ApiService,
    private val pokemonDao: PokemonDao
) : PokemonRepository {

    override fun getPokemonsFlow(): Flow<List<Pokemon>> {
        return pokemonDao.getPokemonsFlow().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override suspend fun fetchPokemons(limit: Int, offset: Int): Result<Unit> {
        return try {
            val response = apiService.getPokemons(limit, offset)
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    val entities = body.results.map { it.toEntity() }
                    if (offset == 0) {
                        // If it's the first page, we might want to clear old data or just replace.
                        // Here we just insert/replace
                    }
                    withContext(Dispatchers.IO) {
                        pokemonDao.insertPokemons(entities)
                    }
                    Result.success(Unit)
                } ?: Result.failure(Exception("Empty body"))
            } else {
                Result.failure(Exception("Network error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getPokemonDetail(idOrName: String): Result<PokemonDetail> {
        return try {
            // Check local DB first if it's an ID or try to search by name
            val localId = idOrName.toIntOrNull()
            val localData = withContext(Dispatchers.IO) {
                if (localId != null) {
                    pokemonDao.getPokemonDetail(localId, "")
                } else {
                    pokemonDao.getPokemonDetail(0, idOrName)
                }
            }

            if (localData != null) {
                return Result.success(localData.toDomain())
            }

            // If not in local DB, fetch from API
            val response = apiService.getPokemonDetail(idOrName.lowercase())
            if (response.isSuccessful) {
                response.body()?.let { dto ->
                    val entity = dto.toEntity()
                    withContext(Dispatchers.IO) {
                        pokemonDao.insertPokemonDetail(entity)
                    }
                    Result.success(entity.toDomain())
                } ?: Result.failure(Exception("Empty body"))
            } else {
                Result.failure(Exception("Pokemon not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun fetchPokemonsByType(typeId: String): Result<List<Pokemon>> {
        return try {
            val response = apiService.getPokemonsByType(typeId)
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    val entities = body.pokemon.map { it.pokemon.toEntity() }
                    // Insert to DB as well so they are cached
                    withContext(Dispatchers.IO) {
                        pokemonDao.insertPokemons(entities)
                    }
                    Result.success(entities.map { it.toDomain() })
                } ?: Result.failure(Exception("Empty body"))
            } else {
                Result.failure(Exception("Type not found"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun getTypes(): Result<List<Pair<String, String>>> {
        return try {
            val response = apiService.getTypes()
            if (response.isSuccessful) {
                response.body()?.let { body ->
                    val types = body.results.map { Pair(it.name, it.getId()) }
                    Result.success(types)
                } ?: Result.failure(Exception("Empty body"))
            } else {
                Result.failure(Exception("Failed to load types"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
