package com.example.pokeapi.data.remote

import com.example.pokeapi.data.remote.dto.PokemonDetailDto
import com.example.pokeapi.data.remote.dto.PokemonResponseDto
import com.example.pokeapi.data.remote.dto.PokemonTypeResponseDto
import com.example.pokeapi.data.remote.dto.TypeDetailResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PokeApiService {
    @GET("pokemon")
    suspend fun getPokemonList(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): PokemonResponseDto

    @GET("pokemon/{id}")
    suspend fun getPokemonDetail(
        @Path("id") id: Int
    ): PokemonDetailDto

    @GET("type")
    suspend fun getPokemonTypes(): PokemonTypeResponseDto

    @GET("type/{id}")
    suspend fun getPokemonByType(
        @Path("id") id: Int
    ): TypeDetailResponseDto
}
