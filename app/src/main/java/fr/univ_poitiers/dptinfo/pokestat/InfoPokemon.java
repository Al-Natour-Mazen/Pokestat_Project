package fr.univ_poitiers.dptinfo.pokestat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InfoPokemon extends AppCompatActivity {

    public static final String APP_TAG = "INFOPOKEMON";

    private String pokemonName;
    private TextView pokeName, pokeSize, pokeWeight, pokeType;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_pokemon);

        pokemonName = getIntent().getStringExtra("inputpokemonname");
        pokeName = findViewById(R.id.textViewPSN);
        pokeSize = findViewById(R.id.textViewPSV);
        pokeWeight = findViewById(R.id.textViewPWV);
        pokeType = findViewById(R.id.textViewPTV);

        if (pokemonName != null && !pokemonName.equals("") && pokeName != null) {
            pokeName.setText(pokemonName);
            new FetchPokemonDataTask().execute(pokemonName);
        }

        Log.d(APP_TAG, "onCreate");
    }

    private class FetchPokemonDataTask extends AsyncTask<String, Void, JsonObject> {

        @Override
        protected JsonObject doInBackground(String... params) {
            OkHttpClient client = new OkHttpClient();
            String url = "https://pokebuildapi.fr/api/v1/pokemon/" + params[0];
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
            if (jsonObject != null) {
                JsonObject stats = jsonObject.get("stats").getAsJsonObject();
                String hp = stats.get("HP").getAsString();

                String special_attack = stats.get("special_attack").getAsString();


                JsonArray apiTypes = jsonObject.get("apiTypes").getAsJsonArray();
                String typeName = apiTypes.get(0).getAsJsonObject().get("name").getAsString();


                pokeSize.setText("HP: " + hp  );
                pokeWeight.setText("Special Attack: " + special_attack );
                pokeType.setText(typeName);
            }
        }
    }
}
