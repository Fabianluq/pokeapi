package com.example.pokeapi.di

import android.content.Context
import androidx.room.Room
import com.example.pokeapi.data.local.PokeDatabase
import com.example.pokeapi.data.local.dao.PokemonDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): PokeDatabase {
        return Room.databaseBuilder(
            context,
            PokeDatabase::class.java,
            "poke_database"
        ).build()
    }

    @Provides
    fun providePokemonDao(database: PokeDatabase): PokemonDao {
        return database.pokemonDao()
    }
}
