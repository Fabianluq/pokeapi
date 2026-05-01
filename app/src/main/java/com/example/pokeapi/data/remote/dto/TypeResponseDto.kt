package com.example.pokeapi.data.remote.dto

data class TypeListResponse(
    val results: List<NameUrlDto>
)

data class TypeDetailResponse(
    val pokemon: List<TypePokemonSlotDto>
)

data class TypePokemonSlotDto(
    val pokemon: NameUrlDto
)
