package edu.uoc.videojuego_pmd_tarea07;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

public class Enemigo {


    public final int ENEMIGO1 =0; //enemigo que dispara o que va mas lento pero es más grande eso lo veo despues....
    public final int ENEMIGO2 =1;       //enemigo que se mueve aleatoriamente


    public final float VELOCIDAD_ENEMIGO_INTELIGENTE=4;
    public final float VELOCIDAD_ENEMIGO_TONTO=2;
    public float velocidad;

    public int Enemigocoordenada_x, Enemigocoordenada_y;
    //coordenadas donde se dibuja el control
    public int tipo_enemigo; //imagen del control

    public float direccion_vertical=1;  //inicialmente hacia abajo
    public float direccion_horizontal=1; //inicialmente derecha

    private int Nivel;

    private Juego juego;


    public Enemigo () {}

    public Enemigo(Juego j, int n){
        juego=j;
        Nivel=n;

        // recien creado......
        //20 segundos en cruzar * factor de inteligencia y nivel
        float VELOCIDAD_ENEMIGO=j.AltoPantalla/20f/BucleJuego.MAX_FPS;

        //probabilidad de enemigo tonto 80%, enemigo listo 20%

        if(Math.random()>0.20) {
            tipo_enemigo = ENEMIGO1;
            velocidad = (VELOCIDAD_ENEMIGO_TONTO+Nivel)*VELOCIDAD_ENEMIGO;
        }
        else {
            tipo_enemigo = ENEMIGO2;
            velocidad = (VELOCIDAD_ENEMIGO_INTELIGENTE+Nivel)*VELOCIDAD_ENEMIGO;
        }

        //para el enemigo tonto se calcula la dirección aleatoria
        if(Math.random()>0.5)
            direccion_horizontal=1; //derecha
        else
            direccion_horizontal=-1; //izquierda

        if(Math.random()>0.5)
            direccion_vertical=1; //abajo
        else
            direccion_vertical=-1; //arriba

        CalculaCoordenadas();
    }

    public void CalculaCoordenadas() {

        Enemigocoordenada_x = (juego.AnchoPantalla + juego.enemigo1.getWidth());
        Enemigocoordenada_y = (int) (Math.random() * juego.AnchoPantalla);
    }

    //Actualiza la coordenada del enemigo con respecto a la coordenada de la nave
    public void ActualizaCoordenadas() {



            //el enemigo tonto hace caso omiso a la posición de la nave,
            //simplemente pulula por la pantalla
            Enemigocoordenada_x += direccion_horizontal * velocidad;
            Enemigocoordenada_y += direccion_vertical * velocidad;
            //Cambios de direcciones al llegar a los bordes de la pantalla
            if (Enemigocoordenada_x <= 0 && direccion_horizontal == -1)
                direccion_horizontal = 1;
            if (Enemigocoordenada_x > juego.AltoPantalla - juego.enemigo1.getWidth() && direccion_horizontal == 1)
                direccion_horizontal = -1;
            if (Enemigocoordenada_y >= juego.AnchoPantalla && direccion_vertical == 1)
                direccion_vertical = -1;
            if (Enemigocoordenada_y <= 0 && direccion_vertical == -1)
                direccion_vertical = 1;

    }

    public void Dibujar(Canvas c, Paint p) {

        if (tipo_enemigo == ENEMIGO1) {

/*
            int srcX= (juego.contadorFrames*juego.anchoSprite)+juego.anchoEscenas;
            int srcY= juego.altoSprite;

            Rect src= new Rect(srcX,0,srcX+juego.anchoSprite,srcY);
            Rect dst= new Rect(Enemigocoordenada_x,Enemigocoordenada_y,Enemigocoordenada_x+juego.anchoSprite, juego.altoSprite + Enemigocoordenada_y);

            c.drawBitmap(juego.enemigo_tonto,src,dst,null);

 */
            c.drawBitmap(juego.enemigo1, Enemigocoordenada_x, Enemigocoordenada_y, p);
        } else {

            c.drawBitmap(juego.enemigo2, Enemigocoordenada_x, Enemigocoordenada_y, p);
        }
    }

    public int Ancho(){
        if(tipo_enemigo== ENEMIGO1)
            return juego.enemigo1.getWidth();
        else
            return juego.enemigo2.getWidth();
    }

    public int Alto(){
        if(tipo_enemigo== ENEMIGO1)
            return juego.enemigo1.getHeight();
        else
            return juego.enemigo2.getHeight();
    }

    public Bitmap bitmap() {
        if (tipo_enemigo == ENEMIGO1) {
            return juego.enemigo1;
        } else {
            return juego.enemigo2;
        }

    }
     }



