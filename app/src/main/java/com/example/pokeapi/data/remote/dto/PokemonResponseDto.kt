package com.example.pokeapi.data.remote.dto

data class PokemonListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonDto>
)

data class PokemonDto(
    val name: String,
    val url: String
) {
    // Extract ID from url "https://pokeapi.co/api/v2/pokemon/1/"
    fun getId(): Int {
        return try {
            val parts = url.trimEnd('/').split("/")
            parts.last().toInt()
        } catch (e: Exception) {
            0
        }
    }
}
