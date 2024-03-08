package fr.univ_poitiers.dptinfo.pokestat.DataBases;

import android.content.Context;

import androidx.room.*;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import fr.univ_poitiers.dptinfo.pokestat.DAOs.PokemonDao;
import fr.univ_poitiers.dptinfo.pokestat.Entities.Pokemon;

@Database(entities = {Pokemon.class}, version = 1, exportSchema = false)
public abstract class PokemonRoomDatabase extends RoomDatabase {
    public abstract PokemonDao pokemonDao();

    private static volatile PokemonRoomDatabase pokemonRoomDatabase;
    private static final int NUMBER_OF_THREADS = 4;
    public static final ExecutorService databaseWriteExecutor =
            Executors.newFixedThreadPool(NUMBER_OF_THREADS);

    public static PokemonRoomDatabase getDatabase(final Context context) {
        if (pokemonRoomDatabase == null) {
            synchronized (PokemonRoomDatabase.class) {
                if (pokemonRoomDatabase == null) {
                    pokemonRoomDatabase = Room.databaseBuilder(context.getApplicationContext(),
                                    PokemonRoomDatabase.class, "student_database")
                            .build();
                }
            }
        }
        return pokemonRoomDatabase;
    }
}

