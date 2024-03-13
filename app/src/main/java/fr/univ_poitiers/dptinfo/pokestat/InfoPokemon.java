package fr.univ_poitiers.dptinfo.pokestat;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.Normalizer;
import java.util.ArrayList;

import fr.univ_poitiers.dptinfo.pokestat.DAOs.PokemonDao;
import fr.univ_poitiers.dptinfo.pokestat.DataBases.PokemonRoomDatabase;
import fr.univ_poitiers.dptinfo.pokestat.Entities.Pokemon;
import fr.univ_poitiers.dptinfo.pokestat.Repositories.PokemonRepository;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InfoPokemon extends AppCompatActivity {

    public static final String APP_TAG = "INFOPOKEMON";

    private String pokemonName, pokemonNameNonNormalize;
    private TextView pokeName, pokeSize, pokeWeight, pokeType, pokeHp;
    private ImageView pokeImage;
    private Button backBtn, webSiteBtn;
    private PokemonRepository ripo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_pokemon);

        pokemonName = getIntent().getStringExtra("inputpokemonname");
        pokemonNameNonNormalize = pokemonName;
        pokemonName = normalizePokemonName(pokemonName);

        ripo = new PokemonRepository(InfoPokemon.this.getApplication());

        initializeViews();

        backBtn.setOnClickListener(v -> finish());

        webSiteBtn.setOnClickListener(v -> openPokemonWebsite());

        if (pokemonName != null && !pokemonName.isEmpty() && pokeName != null) {
            pokeName.setText(pokemonName);
            fetchDataFromApiOrDatabase();
        }

    }

    private String normalizePokemonName(String pokemonName) {
        pokemonName = Normalizer.normalize(pokemonName, Normalizer.Form.NFD);
        return pokemonName.replaceAll("\\p{M}", "");
    }

    private void initializeViews() {
        pokeImage = findViewById(R.id.pokemonImage);
        pokeName = findViewById(R.id.textViewPSN);
        pokeSize = findViewById(R.id.textViewPSV);
        pokeWeight = findViewById(R.id.textViewPWV);
        pokeType = findViewById(R.id.textViewPTV);
        pokeHp = findViewById(R.id.textViewPHP);
        backBtn = findViewById(R.id.buttonBack);
        webSiteBtn = findViewById(R.id.buttonWebSite);
    }

    private void openPokemonWebsite() {
        String pokeUrl = "https://www.pokepedia.fr/" + pokemonNameNonNormalize;
        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(pokeUrl));
        startActivity(intent);
    }

    private void fetchDataFromApiOrDatabase() {
        LiveData<Pokemon> pokemonLiveData = ripo.getPokemon(pokemonName);
        pokemonLiveData.observe(this, new Observer<Pokemon>() {
            @Override
            public void onChanged(Pokemon poke) {
                if (poke != null) {

                    displayPokemonData(poke);
                } else {
                    new FetchPokemonDataTask().execute(pokemonName);
                }
                pokemonLiveData.removeObserver(this);
            }
        });
    }

    private void displayPokemonData(Pokemon poke) {
        pokeSize.setText(poke.getHeight());
        pokeWeight.setText(poke.getWeight());
        pokeType.setText(poke.getType());
        pokeHp.setText(poke.getHp());
        Glide.with(InfoPokemon.this).load(poke.getImageUrl()).into(pokeImage);
        Toast.makeText(InfoPokemon.this, "Pokemon loaded from DB", Toast.LENGTH_SHORT).show();
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchPokemonDataTask extends AsyncTask<String, Void, JsonObject> {

        @Override
        protected JsonObject doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            String url = "https://tyradex.vercel.app/api/v1/pokemon/" + params[0];
            Request request = new Request.Builder().url(url).build();
            try (Response response = client.newCall(request).execute()) {
                if (!response.isSuccessful()) {
                    throw new IOException("Code inattendu " + response.code());
                }
                String responseBody = response.body().string();
                Gson gson = new Gson();
                return gson.fromJson(responseBody, JsonObject.class);
            } catch (IOException e) {
                Log.d(APP_TAG, "Erreur réseau : " + e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPostExecute(JsonObject jsonObject) {
            if (jsonObject != null && jsonObject.has("stats") && !jsonObject.get("stats").isJsonNull()) {
                processPokemonData(jsonObject);
            } else {
                String message = getString(R.string.pokemon_not_found);
                Toast.makeText(InfoPokemon.this, message, Toast.LENGTH_SHORT).show();
            }
        }

        private void processPokemonData(JsonObject jsonObject) {
            String hp = jsonObject.get("stats").getAsJsonObject().get("hp").getAsString();
            JsonArray types = jsonObject.get("types").getAsJsonArray();
            ArrayList<String> typeNames = new ArrayList<>();
            for (JsonElement typeElement : types) {
                typeNames.add(typeElement.getAsJsonObject().get("name").getAsString());
            }
            String weight = jsonObject.get("weight").getAsString();
            String height = jsonObject.get("height").getAsString();
            String imageUrl = jsonObject.get("sprites").getAsJsonObject().get("regular").getAsString();

            Glide.with(InfoPokemon.this).load(imageUrl).into(pokeImage);
            pokeSize.setText(height);
            pokeWeight.setText(weight);
            pokeType.setText(typeNames.toString());
            pokeHp.setText(hp);

            String fiche = "pokemonName " + pokemonName + "\n" +
                    "pokeWeight : " + weight + "\n" +
                    "pokeSize : " + height + "\n" +
                    "pokeType : " + typeNames.toString() + "\n" +
                    "pokeHp : " + hp + "\n" +
                    "pokeImage : " + imageUrl + "\n";

            writePokemonDataToFile(fiche);

            savePokemonToDatabase(weight, height, hp, typeNames.toString(), imageUrl);
        }

        private void savePokemonToDatabase(String weight, String height, String hp, String types, String imageUrl) {
            Pokemon poke = new Pokemon();
            poke.setHeight(height);
            poke.setHp(hp);
            poke.setName(pokemonName);
            poke.setType(types);
            poke.setWeight(weight);
            poke.setImageUrl(imageUrl);
            ripo.insertPokemon(poke);
        }
    }

    private void writePokemonDataToFile(String fiche) {
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File fileout = new File(folder, "pokestat_fiche.txt");
        try (FileOutputStream fos = new FileOutputStream(fileout, true)) {
            PrintStream ps = new PrintStream(fos);
            ps.println(fiche);
            ps.close();
            String message = this.getString(R.string.pokemon_save_good);
            Toast.makeText(InfoPokemon.this, message, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.e(APP_TAG, "File not found", e);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(APP_TAG, "Error I/O", e);
        }
    }
}
