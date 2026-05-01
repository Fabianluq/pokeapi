package com.example.pokeapi.data.remote

import com.example.pokeapi.data.remote.dto.PokemonDetailDto
import com.example.pokeapi.data.remote.dto.PokemonListResponse
import com.example.pokeapi.data.remote.dto.TypeDetailResponse
import com.example.pokeapi.data.remote.dto.TypeListResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @GET("pokemon")
    suspend fun getPokemons(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int
    ): Response<PokemonListResponse>

    @GET("pokemon/{idOrName}")
    suspend fun getPokemonDetail(
        @Path("idOrName") idOrName: String
    ): Response<PokemonDetailDto>

    @GET("type")
    suspend fun getTypes(): Response<TypeListResponse>

    @GET("type/{id}")
    suspend fun getPokemonsByType(
        @Path("id") typeId: String
    ): Response<TypeDetailResponse>
}
