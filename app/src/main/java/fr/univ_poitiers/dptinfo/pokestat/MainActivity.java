package fr.univ_poitiers.dptinfo.pokestat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

import java.util.Date;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;

public class MainActivity extends AppCompatActivity {
    public static final String APP_TAG = "POKESTAT";
    private Button exitBtn, searchBtn;
    private TextInputEditText searchField;

    private GestureDetector gestureDetector;

    private Set<String> searchedPokemonName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(APP_TAG, "onCreate");

        exitBtn = findViewById(R.id.buttonExit);
        searchBtn = findViewById(R.id.buttonSearch);
        searchField = findViewById(R.id.textInputEditText);

        exitBtn.setOnClickListener(v->{
            finish();
        });

        // Chargement de l'historique à partir des préférences partagées
        reload_historic();

        // Affichage de l'historique dans les logs
        display_historic();

        searchBtn.setOnClickListener( v -> {
            Intent intent = new Intent(MainActivity.this, InfoPokemon.class);

            String pokemonName = Objects.requireNonNull(searchField.getText()).toString();

            // Ajout de la saisie dans l'historique
            searchedPokemonName.add(pokemonName);

            // Enregistrement de l'historique dans les préférences partagées
            SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
            sharedPref.edit().putStringSet("historyPokemonName", searchedPokemonName).commit();

            intent.putExtra("inputpokemonname", pokemonName);
            startActivity(intent);
        });

        gestureDetector = new GestureDetector(this, new SwipeGestureDetector());
        verifyStoragePermissions(this);
    }
    
    // Fonction qui recharge un historique
    public void reload_historic() {
        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(this);
        searchedPokemonName = sharedPref.getStringSet("historyPokemonName", new TreeSet<String>());
    }

    public void display_historic() {
        Log.d(APP_TAG,"Historique ("+ (new Date())+ ") size="+ searchedPokemonName.size()+": ");
        for (String item : searchedPokemonName) {
            Log.d(APP_TAG,"\t- " + item);
        }
    }

    // Listes des permissions
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public static void verifyStoragePermissions(Activity activity) {
        // Vérifie si nous avons les droits d'écriture
        int permission = ActivityCompat.checkSelfPermission(activity, android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            // Aïe, il faut les demander à l'utilisateur
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    REQUEST_EXTERNAL_STORAGE
            );
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(APP_TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(APP_TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(APP_TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(APP_TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d(APP_TAG, "onDestroy");
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(APP_TAG, "onSaveInstanceState");
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        Log.d(APP_TAG, "onRestoreInstanceState");
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (gestureDetector.onTouchEvent(event)) {
            return true;
        }
        return super.onTouchEvent(event);
    }

    private class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        private static final int SWIPE_MIN_DISTANCE = 120;
        private static final int SWIPE_THRESHOLD_VELOCITY = 200;

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            assert e1 != null;
            if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                Intent intent = new Intent(MainActivity.this, InfoPokemon.class);
                startActivity(intent);
                return true;
            }
            return false;
        }
    }
}