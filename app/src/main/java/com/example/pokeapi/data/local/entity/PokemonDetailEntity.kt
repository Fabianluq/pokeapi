package com.example.pokeapi.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pokemon_detail")
data class PokemonDetailEntity(
    @PrimaryKey val id: Int,
    val name: String,
    val height: Int,
    val weight: Int,
    val types: String, // Stored as comma-separated string for simplicity
    val abilities: String, // Stored as comma-separated string for simplicity
    val imageUrl: String
)
