package edu.uoc.videojuego_pmd_tarea07;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.w3c.dom.Text;

public class GameOverActivity extends AppCompatActivity {

    private Button reintentar;
    private Button menuPrincipal;
    private TextView puntuacion;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gameover);

        reintentar = findViewById(R.id.retry);
        reintentar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOverActivity.this,ActividadJuego.class);
                startActivity(intent);
            }
        });
        menuPrincipal = findViewById(R.id.menu);
        menuPrincipal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GameOverActivity.this,MainActivity.class);
                startActivity(intent);
            }
        });
        puntuacion = findViewById(R.id.puntuacion);
        puntuacion.setText("Puntuación: "+String.valueOf(Juego.getPuntos()));
    }
}