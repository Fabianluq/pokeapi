package com.example.pokeapi.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.pokeapi.data.local.entity.PokemonDetailEntity
import com.example.pokeapi.data.local.entity.PokemonEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PokemonDao {
    @Query("SELECT * FROM pokemon_list")
    fun getPokemonsFlow(): Flow<List<PokemonEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPokemons(pokemons: List<PokemonEntity>)

    @Query("SELECT * FROM pokemon_detail WHERE id = :id OR name = :name LIMIT 1")
    fun getPokemonDetail(id: Int, name: String): PokemonDetailEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertPokemonDetail(pokemonDetail: PokemonDetailEntity)
    
    @Query("DELETE FROM pokemon_list")
    fun clearPokemons()
}
