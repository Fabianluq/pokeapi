package com.example.pokeapi.data.remote.dto

import com.google.gson.annotations.SerializedName

data class PokemonDetailDto(
    val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: List<TypeSlotDto>,
    val abilities: List<AbilitySlotDto>,
    val sprites: SpritesDto
)

data class TypeSlotDto(
    val type: NameUrlDto
)

data class AbilitySlotDto(
    val ability: NameUrlDto
)

data class NameUrlDto(
    val name: String,
    val url: String
) {
    fun getId(): String {
        return try {
            val parts = url.trimEnd('/').split("/")
            parts.last()
        } catch (e: Exception) {
            ""
        }
    }
}

data class SpritesDto(
    val other: OtherSpritesDto?
)

data class OtherSpritesDto(
    @SerializedName("official-artwork") val officialArtwork: OfficialArtworkDto?
)

data class OfficialArtworkDto(
    @SerializedName("front_default") val frontDefault: String?
)
