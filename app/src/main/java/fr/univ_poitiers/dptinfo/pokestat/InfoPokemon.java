package fr.univ_poitiers.dptinfo.pokestat;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

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

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class InfoPokemon extends AppCompatActivity {

    public static final String APP_TAG = "INFOPOKEMON";

    private String pokemonName,pokemonNameNonNormalize;
    private TextView pokeName, pokeSize, pokeWeight, pokeType, pokeHp;

    private ImageView pokeImage;

    private Button backBtn,webSiteBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_pokemon);

        // Récupérer le nom du Pokemon passé en argument
        pokemonName = getIntent().getStringExtra("inputpokemonname");
        pokemonNameNonNormalize = pokemonName;
        // Normaliser le nom du Pokemon en forme NFD (décomposition canonique)
        pokemonName = Normalizer.normalize(pokemonName, Normalizer.Form.NFD);
        // Remplacer les marques diacritiques par des chaînes vides
        pokemonName = pokemonName.replaceAll("\\p{M}", "");


        pokeImage = findViewById(R.id.pokemonImage);
        pokeName = findViewById(R.id.textViewPSN);
        pokeSize = findViewById(R.id.textViewPSV);
        pokeWeight = findViewById(R.id.textViewPWV);
        pokeType = findViewById(R.id.textViewPTV);
        pokeHp = findViewById(R.id.textViewPHP);
        backBtn = findViewById(R.id.buttonBack);
        webSiteBtn = findViewById(R.id.buttonWebSite);

        backBtn.setOnClickListener(v -> {
            finish();
        });

        webSiteBtn.setOnClickListener(v -> {
            String pokeUrl = "https://www.pokepedia.fr/"+ pokemonNameNonNormalize;
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(pokeUrl));
            startActivity(intent);
        });


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


                String fiche = "";
                fiche += "pokemonName " + pokemonName + "\n";
                fiche += "pokeWeight : " + weight + "\n";
                fiche += "pokeSize : " + height + "\n";
                fiche += "pokeType : " + typeNames.toString() + "\n";
                fiche += "pokeHp : " + hp + "\n";
                fiche += "pokeImage : " + imageUrl + "\n";
                write_fiche_in_file(fiche);

            } else {
                String message = getString(R.string.pokemon_not_found);
                Toast.makeText(InfoPokemon.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Méthode qui écrit la fiche d'un Pokemon dans un fichier
    public void write_fiche_in_file(String fiche) {
        // Choix du répertoire et du nom du fichier
        File folder = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File fileout = new File(folder, "pokestat_fiche.txt");

        // Tentative d'écriture dans le fichier
        try (FileOutputStream fos = new FileOutputStream(fileout, true)) {
            PrintStream ps = new PrintStream(fos);
            ps.println(fiche);
            ps.close();
            String message = getString(R.string.pokemon_save_good);
            Toast.makeText(InfoPokemon.this, message, Toast.LENGTH_SHORT).show();
        } catch (FileNotFoundException e) {
            Log.e(APP_TAG,"File not found",e);
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            Log.e(APP_TAG,"Error I/O",e);
        }
    }
}
