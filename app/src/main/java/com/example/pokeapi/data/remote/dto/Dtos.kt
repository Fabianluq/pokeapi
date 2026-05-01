package com.example.pokeapi.data.remote.dto

data class PokemonResponseDto(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<PokemonDto>
)

data class PokemonDto(
    val name: String,
    val url: String
)

data class PokemonDetailDto(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<TypeSlotDto>
)

data class TypeSlotDto(
    val slot: Int,
    val type: PokemonTypeDto
)

data class PokemonTypeResponseDto(
    val count: Int,
    val results: List<PokemonTypeDto>
)

data class PokemonTypeDto(
    val name: String,
    val url: String
)

data class TypeDetailResponseDto(
    val pokemon: List<PokemonSlotDto>
)

data class PokemonSlotDto(
    val pokemon: PokemonDto
)
