package fr.univ_poitiers.dptinfo.pokestat.Repositories;

import android.app.Application;

import androidx.lifecycle.LiveData;

import fr.univ_poitiers.dptinfo.pokestat.DAOs.PokemonDao;
import fr.univ_poitiers.dptinfo.pokestat.Entities.Pokemon;
import fr.univ_poitiers.dptinfo.pokestat.DataBases.PokemonRoomDatabase;

public class PokemonRepository {
    PokemonRoomDatabase pokemonRoomDatabase;
    PokemonDao pokemonDao;

    public PokemonRepository(Application application) {
        pokemonRoomDatabase = PokemonRoomDatabase.getDatabase(application);
        pokemonDao = pokemonRoomDatabase.pokemonDao();
    }

    public void insertPokemon(Pokemon poke) {
        PokemonRoomDatabase.databaseWriteExecutor.execute(() -> pokemonDao.insert(poke));
    }

    public LiveData<Pokemon> getPokemon(String pokename) {
        return pokemonDao.getPokemonByName(pokename);
    }

}
