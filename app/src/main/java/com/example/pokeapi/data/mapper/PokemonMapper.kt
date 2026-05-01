package com.example.pokeapi.data.mapper

import com.example.pokeapi.data.local.entity.PokemonDetailEntity
import com.example.pokeapi.data.local.entity.PokemonEntity
import com.example.pokeapi.data.remote.dto.PokemonDetailDto
import com.example.pokeapi.data.remote.dto.PokemonDto
import com.example.pokeapi.data.remote.dto.NameUrlDto
import com.example.pokeapi.domain.model.Pokemon
import com.example.pokeapi.domain.model.PokemonDetail

fun PokemonEntity.toDomain(): Pokemon {
    return Pokemon(
        id = id,
        name = name,
        imageUrl = imageUrl
    )
}

fun PokemonDto.toEntity(): PokemonEntity {
    val id = getId()
    return PokemonEntity(
        id = id,
        name = name,
        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
    )
}

fun NameUrlDto.toEntity(): PokemonEntity {
    val id = getId().toIntOrNull() ?: 0
    return PokemonEntity(
        id = id,
        name = name,
        imageUrl = "https://raw.githubusercontent.com/PokeAPI/sprites/master/sprites/pokemon/other/official-artwork/$id.png"
    )
}

fun PokemonDetailEntity.toDomain(): PokemonDetail {
    return PokemonDetail(
        id = id,
        name = name,
        height = height,
        weight = weight,
        types = types.split(",").filter { it.isNotBlank() },
        abilities = abilities.split(",").filter { it.isNotBlank() },
        imageUrl = imageUrl
    )
}

fun PokemonDetailDto.toEntity(): PokemonDetailEntity {
    val typesStr = types.joinToString(",") { it.type.name }
    val abilitiesStr = abilities.joinToString(",") { it.ability.name }
    val image = sprites.other?.officialArtwork?.frontDefault ?: ""
    
    return PokemonDetailEntity(
        id = id,
        name = name,
        height = height,
        weight = weight,
        types = typesStr,
        abilities = abilitiesStr,
        imageUrl = image
    )
}
