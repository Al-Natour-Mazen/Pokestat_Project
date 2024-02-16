package fr.univ_poitiers.dptinfo.pokestat;

import androidx.appcompat.app.AppCompatActivity;

import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InfoPokemon extends AppCompatActivity {

    public static final String APP_TAG = "INFOPOKEMON";

    private String pokemonName;
    private TextView pokeName, pokeSize, pokeWeight, pokeType, pokeHp;

    private ImageView pokeImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_pokemon);

        pokemonName = getIntent().getStringExtra("inputpokemonname");
        pokeImage = findViewById(R.id.pokemonImage);
        pokeName = findViewById(R.id.textViewPSN);
        pokeSize = findViewById(R.id.textViewPSV);
        pokeWeight = findViewById(R.id.textViewPWV);
        pokeType = findViewById(R.id.textViewPTV);
        pokeHp = findViewById(R.id.textViewPHP);


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
            if (jsonObject.has("stats") && !jsonObject.get("stats").isJsonNull()) {
                JsonObject stats = jsonObject.get("stats").getAsJsonObject();

                String hp = stats.get("hp").getAsString();

                JsonArray types = jsonObject.get("types").getAsJsonArray();
                ArrayList<String> typeNames = new ArrayList<>();

                for (JsonElement typeElement : types) {
                    JsonObject typeObject = typeElement.getAsJsonObject();
                    String typeName = typeObject.get("name").getAsString();
                    typeNames.add(typeName);
                }

                String weight = jsonObject.get("weight").getAsString();
                String height = jsonObject.get("height").getAsString();


                String imageUrl = jsonObject.get("sprites").getAsJsonObject().get("regular").getAsString();
                Glide.with(InfoPokemon.this).load(imageUrl).into(pokeImage);

                pokeSize.setText(height);
                pokeWeight.setText(weight);
                pokeType.setText(typeNames.toString());
                pokeHp.setText(hp);
            } else {
                String message = getString(R.string.pokemon_not_found);
                Toast.makeText(InfoPokemon.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
