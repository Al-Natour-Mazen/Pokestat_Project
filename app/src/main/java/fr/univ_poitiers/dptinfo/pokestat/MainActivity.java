package fr.univ_poitiers.dptinfo.pokestat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {
    public static final String APP_TAG = "¨POKESTAT";
    private Button exitBtn, searchBtn;
    private TextInputEditText searchField;

    private GestureDetector gestureDetector;

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

        searchBtn.setOnClickListener( v -> {
            Intent intent = new Intent(MainActivity.this, InfoPokemon.class);

            String name = searchField.getText().toString();
            intent.putExtra("inputpokemonname", name);
            startActivity(intent);
        });

        gestureDetector = new GestureDetector(this, new SwipeGestureDetector());
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