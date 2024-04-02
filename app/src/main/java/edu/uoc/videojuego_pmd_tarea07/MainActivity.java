package edu.uoc.videojuego_pmd_tarea07;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageView logo = (ImageView) findViewById(R.id.logo);
        Button comenzar = (Button) findViewById(R.id.buttonComenzar);
        comenzar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,ActividadJuego.class);
                startActivity(intent);
            }
        });

        TextView record = findViewById(R.id.record);
        SharedPreferences sharedPreferences = this.getSharedPreferences(getResources().getString(R.string.app_name),
                Context.MODE_PRIVATE);
        int topScore = sharedPreferences.getInt("SCORE", 0);

        record.setText(String.valueOf(topScore));

        //Animación del diamante sube y baja
        TranslateAnimation diamante = new TranslateAnimation(0, 200,
                0, 50);
        diamante.setDuration(1000);  // duración de la animación
        diamante.setRepeatCount(Animation.INFINITE);
        diamante.setRepeatMode(Animation.REVERSE);
        logo.startAnimation(diamante);

        Button instructions = findViewById(R.id.buttonComoJugar);
        instructions.setOnClickListener(v -> showInstructions());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
         getMenuInflater().inflate(R.menu.menu_principal, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public void showInstructions() {
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Intrucciones");
        alert.setMessage("No dejes que los vampiros enemigos toquen el diamante, o si no perderás.Según vayas acabando con los vampiros enemigos el nivel de dificultad será mayor y vendrán más enemigos a intentar robarte el preciado diamante.");

        alert.setPositiveButton("Ok", null);
        alert.show();

    }
}




