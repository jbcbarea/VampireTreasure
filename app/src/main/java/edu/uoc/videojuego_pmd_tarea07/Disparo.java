package edu.uoc.videojuego_pmd_tarea07;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.media.MediaPlayer;
import android.util.Log;

public class Disparo {

    public float coordenada_x, coordenada_y; //coordenadas donde se dibuja el control
    private Juego juego;
    private float velocidad;
    private MediaPlayer mediaPlayer; //para reproducir el sonido de disparo
    private final float MAX_SEGUNDOS_EN_CRUZAR_PANTALLA=3;
    public int DERECHA = 0;
    public int ARRIBA =1;
    public int IZQUIERDA =2;
    public int ABAJO =3;

    /*Constructor con coordenadas iniciales y número de disparo*/
    public Disparo(Juego j,float x, float y) {
        juego = j;

       // if (direccionDisparo == DERECHA) {

           coordenada_x = x + (j.vampiro.getHeight() / 2);
           coordenada_y = y - j.disparo.getWidth() + 60;

       // coordenada_x = x + (j.vampiro.getWidth()/2);
       // coordenada_y = y - j.disparo.getHeight();

        //} else if (direccionDisparo == ARRIBA){

        //   coordenada_x = x + (j.vampiro.getHeight());
        //    coordenada_y = y - j.disparo.getWidth() /2;
       //}

        velocidad=j.AltoPantalla/MAX_SEGUNDOS_EN_CRUZAR_PANTALLA/BucleJuego.MAX_FPS; //adaptar velocidad al tamaño de pantalla
        Log.i(Juego.class.getSimpleName(),"Velocidad de disparo: " + velocidad);
        mediaPlayer=MediaPlayer.create(j.getContext(), R.raw.disparo);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
           public void onCompletion(MediaPlayer mp) {
                mp.release();
           }
        });
        mediaPlayer.start();
    }



    //se actualiza la coordenada y nada más
    public void ActualizaCoordenadas(){

        //derecha.....
        coordenada_x +=velocidad;
    }

    public void Dibujar(Canvas c, Paint p) {

        c.drawBitmap(juego.disparo, coordenada_x, coordenada_y, p);
    }

    public int Ancho(){
        return juego.disparo.getWidth();
    }

    public int Alto(){
        return juego.disparo.getHeight();
    }

    public boolean FueraDePantalla() {
        return coordenada_x < 0;
    }

}
