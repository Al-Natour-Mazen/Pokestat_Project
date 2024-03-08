package fr.univ_poitiers.dptinfo.pokestat.DAOs;

import androidx.lifecycle.LiveData;
import androidx.room.*;
import java.util.List;

import fr.univ_poitiers.dptinfo.pokestat.Entities.Pokemon;

@Dao
public interface PokemonDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insert(Pokemon pokemon);

    @Query("DELETE FROM pokemon_table")
    void deleteAll();

    @Query("SELECT * from pokemon_table ORDER BY name ASC")
    LiveData<List<Pokemon>> getAllPokemons();

    @Query("SELECT * FROM pokemon_table WHERE name = :name")
    LiveData<Pokemon> getPokemonByName(String name);
}

