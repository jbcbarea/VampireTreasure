package edu.uoc.videojuego_pmd_tarea07;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Build;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.util.ArrayList;
import java.util.Iterator;

public class Juego extends SurfaceView implements SurfaceHolder.Callback,View.OnTouchListener {
    private SurfaceHolder holder;
    public BucleJuego bucle;
    private Activity actividad;


    public int AltoPantalla;
    public int AnchoPantalla;
    private static final int COLUMNAS=1;
    private static final int COLUMNAS_SUB=4;// tal vez es 16

    private static final String TAG = Juego.class.getSimpleName();

    /*Array de Touch */
    private ArrayList<Toque> toques = new ArrayList<Toque>();
    boolean hayToque=false;

    private Bitmap fondo; //Bitmap auxiliar para cargar en el array los recursos
    private static final int MAX_IMAGENES_FONDO= 4; //imagenes que componen el escenario

    Bitmap imagenesFondo[]=new Bitmap[MAX_IMAGENES_FONDO]; // Arrays de imágenes
    /* Array de recursos que componen el escenario*/
    int recursos_imagenes[]={R.drawable.castle,R.drawable.castle,R.drawable.city,R.drawable.jungle,R.drawable.city};
    //coordenadas y del fondo actual y del siguiente
    int yImgActual,yImgSiguiente;

    /*índices del array de imagenes para alternar el fondo*/
    int img_actual=0,img_siguiente=1;


    /* Controles */
    private final int ARRIBA =0;
    private final int DERECHA=1;
    private final int DISPARO=2;
    private final int IZQUIERDA =3;
    private final int ABAJO=4;
    private final int CAMBIADISPARO=5;

    private final float VELOCIDAD_HORIZONTAL; //pixels por frame
    Control controles[]=new Control[5];

    /* Enemigos */
    Bitmap enemigo1, enemigo2;
    public final int TOTAL_ENEMIGOS=150; //Enemigos para acabar el juego
    private int enemigos_minuto=35; //número de enemigos por minuto
    private int frames_para_nuevo_enemigo=0; //frames que restan hasta generar nuevo enemigo
    private int enemigos_muertos=0; //Contador de enemigos muertos
    private int enemigos_creados=0;

    /* Para los Sprites */
    public int anchoSprite;
    public int altoSprite;
    public int anchoEscenas;
    private int anchoUnaEscena;
    private int anchoPrincpioEscenas=0;


    public static int getPuntos() {
        return Puntos;
    }

    public void setPuntos(int puntos) {
        Puntos = puntos;
    }

    public int getPuntuacionMaxima() {
        return PuntuacionMaxima;
    }

    public void setPuntuacionMaxima(int puntuacionMaxima) {
        PuntuacionMaxima = puntuacionMaxima;
    }

    /*Puntos */
    private static int Puntos=0;
    private int PuntuacionMaxima;
    private int Nivel=0;
    private int PUNTOS_CAMBIO_NIVEL=1500;
    private SharedPreferences sharedPreferences;
    private final String SCORE = "SCORE";


    /* Fin de juego */
    private boolean victoria=false,derrota=false;

    /* Lista Enemigos */
   private ArrayList<Enemigo> lista_enemigos=new ArrayList<Enemigo>();

    /* Disparos */
    private ArrayList<Disparo> lista_disparos=new ArrayList<Disparo>();

    Bitmap disparo;
    private int frames_para_nuevo_disparo=0;
    //entre disparo y disparo deben pasar al menos MAX_FRAMES_ENTRE_DISPARO
    private final int MAX_FRAMES_ENTRE_DISPARO=BucleJuego.MAX_FPS/4;  //4 disparos por segundo aprox.
    private boolean nuevo_disparo=false;

    /*explosiones*/

    private ArrayList<Explosion> lista_explosiones=new ArrayList<Explosion>();
    Bitmap explosion;

    /*Personaje*/
    Bitmap personaje;
    float xPersonaje; //Coordenada X
    float yPersonaje; //Coordenada Y

    /*VOY A PROBAR EL SPRITE!!!!!!!!!!!!!!!!!!!!    */
    Bitmap vampiro;
    int xZombie;
    int yZombie;
    public int contadorFrames=0;
    public Boolean pasa = true;


    final float SEGUNDOS_EN_RECORRER_PANTALLA_HORIZONTAL=5;

    /* sonidos */
    MediaPlayer mediaPlayer; //para reproducir la música de fondo


    public Juego(Activity context) {
        super(context);
        actividad = context;
        holder = getHolder();
        holder.addCallback(this);

        IniciarMusicaJuego();
        CalculaTamañoPantalla();

        /*Carga pertsonaje*/
        personaje = BitmapFactory.decodeResource(getResources(), R.drawable.personaje);

        vampiro = BitmapFactory.decodeResource(getResources(),R.drawable.zombie);

        /*Carga la explosión*/
        explosion = BitmapFactory.decodeResource(getResources(), R.drawable.explosion);
        /* SharedPreferences para mostrar las puntuaciones*/
        sharedPreferences = getContext().getSharedPreferences(getResources().getString(R.string.app_name),
                Context.MODE_PRIVATE);

        /*posición inicial del Personaje */

        xPersonaje = 100;//posición inicial de la Nave
        yPersonaje = 250;// posición fija a 4/5 de alto y la mitad de ancho
        xZombie = 100;
        yZombie = 200;
        VELOCIDAD_HORIZONTAL = AnchoPantalla / SEGUNDOS_EN_RECORRER_PANTALLA_HORIZONTAL / BucleJuego.MAX_FPS;
        /* Inicialización de coordenadas de fondo (Se ejecuta primero actualizar()*/
        yImgActual=-1;
        yImgSiguiente=-AnchoPantalla-1;

        CargaBackground();
        CargaControles();
        anchoSprite = vampiro.getWidth()/COLUMNAS_SUB;
        altoSprite= vampiro.getHeight();
        anchoUnaEscena= vampiro.getWidth()/COLUMNAS;

        CargaEnemigos();

        disparo = BitmapFactory.decodeResource(getResources(), R.drawable.shot);
        
        //listener para onTouch
        setOnTouchListener(this);

    }

    public void CargaEnemigos(){
        frames_para_nuevo_enemigo=bucle.MAX_FPS*60/enemigos_minuto;
        enemigo1 = BitmapFactory.decodeResource(getResources(), R.drawable.enemigo1);
        enemigo2 = BitmapFactory.decodeResource(getResources(), R.drawable.enemigo2);
    }

    public void CrearNuevoEnemigo(){
        if(TOTAL_ENEMIGOS-enemigos_creados>0) {
            lista_enemigos.add(new Enemigo(this, Nivel));
            enemigos_creados++;
        }
    }

    public void CargaBackground() {
        //cargamos todos los fondos en un array
        for(int i=0;i<MAX_IMAGENES_FONDO;i++) {
            fondo = BitmapFactory.decodeResource(getResources(), recursos_imagenes[i]);
            if(imagenesFondo[i]==null)
                //Para rescalar las imagenes
                imagenesFondo[i] = fondo.createScaledBitmap(fondo, AnchoPantalla, AltoPantalla, true);
            fondo.recycle();
        }
    }

    private void IniciarMusicaJuego(){
        mediaPlayer = MediaPlayer.create(actividad, R.raw.temaprincipal);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.start();
                mediaPlayer.setVolume(10,10);
            }
        });
        mediaPlayer.start();
    }

    public void CargaControles(){
        float aux;

        //flecha_izda
        controles[ARRIBA]=new Control(getContext(),0,AltoPantalla-250);
        controles[ARRIBA].Cargar( R.drawable.izquierda);
        controles[ARRIBA].nombre="ARRIBA";
        //flecha_derecha
        controles[DERECHA]=new Control(getContext(),
             controles[0].Ancho()+controles[0].coordenada_x+40,controles[0].coordenada_y);
       controles[DERECHA].Cargar(R.drawable.derecha);
       controles[DERECHA].nombre="DERECHA";
//arriba
        controles[IZQUIERDA]=new Control(getContext(),
                controles[0].Ancho()+controles[0].coordenada_x-80,controles[1].coordenada_y-200);
        controles[IZQUIERDA].Cargar(R.drawable.arriba);
        controles[IZQUIERDA].nombre="IZQUIERDA";
//abajo
        controles[ABAJO]=new Control(getContext(),
                controles[0].Ancho()+controles[0].coordenada_x-80,controles[0].coordenada_y+200);
        controles[ABAJO].Cargar(R.drawable.abajo);
        controles[ABAJO].nombre="ABAJO";

        //disparo
       aux=5.0f/7.0f*AnchoPantalla+15; //en los 5/7 del ancho
        controles[DISPARO]=new Control(getContext(),aux,controles[0].coordenada_y);
        controles[DISPARO].Cargar(R.drawable.disparo);
        controles[DISPARO].nombre="DISPARO";


    }


    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // se crea la superficie, creamos el game loop

        // Para interceptar los eventos de la SurfaceView
        getHolder().addCallback(this);

        // creamos el game loop
        bucle = new BucleJuego(getHolder(), this);

        // Hacer la Vista focusable para que pueda capturar eventos
        setFocusable(true);

        //comenzar el bucle
        bucle.start();

    }

    public void CalculaTamañoPantalla(){

        if(Build.VERSION.SDK_INT > 13) {
            Display display = actividad.getWindowManager().getDefaultDisplay();
            Point size = new Point();
            display.getSize(size);
            AnchoPantalla = size.x;
            AltoPantalla = size.y;
        }
        else{
            Display display = actividad.getWindowManager().getDefaultDisplay();
            AnchoPantalla = display.getWidth();  // deprecated
            AltoPantalla = display.getHeight();  // deprecated
        }
        Log.i(Juego.class.getSimpleName(), "alto:" + AltoPantalla + "," + "ancho:" + AnchoPantalla);
    }

    /**
     * Este método actualiza el estado del juego. Contiene la lógica del videojuego
     * generando los nuevos estados y dejando listo el sistema para un repintado.
     */

    public void actualizar() {

        actualiza_fondo();
        /* Controles */
        if (!derrota) {
            //eN REALIDAD ES EL IZQUIERDO CREO QUE LO POPSICIONE MALLLL ----lO QUE META AQUI LO HARÁ EN EL IZQUIERDO COMO SI PULSARA
            if (controles[ARRIBA].pulsado) {

                if (pasa) {
                    if (xPersonaje < AnchoPantalla - personaje.getWidth()+25 &&  xPersonaje>0 ) {
                        xPersonaje = xPersonaje - 6;

                    }

                    if (anchoEscenas == 0) {

                        if (xZombie < AnchoPantalla  &&  xZombie>0 ) {

                            xZombie = (int) (xZombie - 6);
                        }
                    }


                    anchoSprite = vampiro.getWidth() / COLUMNAS_SUB;

                    contadorFrames = ++contadorFrames % 4;
                }

            }

            if (controles[DERECHA].pulsado) {
                if (xPersonaje < AnchoPantalla - personaje.getWidth()) {
                    xPersonaje = xPersonaje + 6;
                }

                if (anchoEscenas == 0) {

                    if (xZombie < AnchoPantalla - vampiro.getWidth() +255 ) {

                        xZombie = (int) (xZombie + 6);
                    }
                }

                anchoSprite = vampiro.getWidth() / COLUMNAS_SUB;
                contadorFrames = ++contadorFrames % 4;

            }

            if (controles[ABAJO].pulsado) {

                if (yPersonaje < 1100) {
                    yPersonaje = yPersonaje + 6;
                }

                if (pasa) {


                    if (anchoEscenas == 0) {

                        if (yZombie < 1050) {
                            yZombie = (int) (yZombie + 6);
                        }
                    }

                    anchoSprite = vampiro.getWidth() / COLUMNAS_SUB;
                    contadorFrames = ++contadorFrames % 4;
                }

            }

            if (controles[IZQUIERDA].pulsado) {

                if (yPersonaje > 50) {

                    yPersonaje = yPersonaje - 6;
                }

                if (pasa) {


                    if (anchoEscenas == 0) {

                        if (yZombie > 0) {
                            yZombie = (int) (yZombie - 6);
                        }
                    }

                    anchoSprite = vampiro.getWidth() / COLUMNAS_SUB;
                    contadorFrames = ++contadorFrames % 4;
                }

            }

            /* Disparo */
            if (controles[DISPARO].pulsado)
                nuevo_disparo = true;

            if (frames_para_nuevo_disparo == 0) {
                if (nuevo_disparo) {
                    CreaDisparo();
                    nuevo_disparo = false;
                }
                //nuevo ciclo de disparos
                frames_para_nuevo_disparo = MAX_FRAMES_ENTRE_DISPARO;
            }
            frames_para_nuevo_disparo--;
        }

        //actualizaSpriteEnemigos();

        //Los disparos se mueven
        for (Iterator<Disparo> it_disparos = lista_disparos.iterator(); it_disparos.hasNext(); ) {
            Disparo d = it_disparos.next();
            d.ActualizaCoordenadas();

            if (d.FueraDePantalla()) {
                it_disparos.remove();
            }
        }
        if (frames_para_nuevo_enemigo == 0) {
            CrearNuevoEnemigo();
            //nuevo ciclo de enemigos
            frames_para_nuevo_enemigo = bucle.MAX_FPS * 60 / enemigos_minuto;
        }
        frames_para_nuevo_enemigo--;

        //Los enemigos persiguen al jugador
        for (Enemigo e : lista_enemigos) {
            e.ActualizaCoordenadas();

        }

        //Colisiones recorremos la lista dfe enemigos y de disparos y si hay colision creamos un objeto de la clase explosion

        for(Iterator<Enemigo> it_enemigos= lista_enemigos.iterator();it_enemigos.hasNext();) {
            Enemigo e = it_enemigos.next();
            for(Iterator<Disparo> it_disparos=lista_disparos.iterator();it_disparos.hasNext();)
            {
                Disparo d=it_disparos.next();
                if (colision(e, d)) {
                    /* Creamos un nuevo objeto explosión */
                    lista_explosiones.add(new Explosion(this,e.Enemigocoordenada_x, e.Enemigocoordenada_y));
                    /* eliminamos de las listas tanto el disparo como el enemigo */
                    try {
                        it_enemigos.remove();
                        it_disparos.remove();
                    }
                    catch(Exception ex){}
                    enemigos_muertos++; //un enemigo menos para el final

                    /*Puntos*/
                    if(e.tipo_enemigo==e.ENEMIGO1)
                        Puntos+=50;
                    else
                        Puntos+=100;

                }
            }
        }

        //actualizar explosiones
        for(Iterator<Explosion> it_explosiones=lista_explosiones.iterator();it_explosiones.hasNext();){
            Explosion exp=it_explosiones.next();
            exp.ActualizarEstado();
            if(exp.HaTerminado()) it_explosiones.remove();
        }

        //cada PUNTOS_CAMBIO_NIVEL puntos se incrementa la dificultad incrementamos los enemigos_minuto

        if(Nivel!=Puntos/PUNTOS_CAMBIO_NIVEL) {
            Nivel = Puntos / PUNTOS_CAMBIO_NIVEL;
            enemigos_minuto += (20 * Nivel);
        }

        //Para que podamos mostrar el record de la puntuacion mas alta

        PuntuacionMaxima = this.sharedPreferences.getInt(SCORE, 0);
        if (PuntuacionMaxima < Puntos) {
            this.sharedPreferences.edit().putInt(SCORE,Puntos).apply();
            PuntuacionMaxima=Puntos;
        }
        if(!derrota && !victoria)
            CompruebaFinJuego();
    }

    public void CompruebaFinJuego() {

        for (Enemigo e : lista_enemigos) {
            if ((colisionPersonaje(e))) {
                lista_explosiones.add(new Explosion(this, e.Enemigocoordenada_x, e.Enemigocoordenada_y));
                derrota = true;
                bucle.fin();
                Intent intent = new Intent().setClass(getContext(), GameOverActivity.class);
                getContext().startActivity(intent);
            }
        }

        if (!derrota) {
            if (enemigos_muertos == TOTAL_ENEMIGOS)
                victoria = true;

        }
        if (victoria) {

            //Vams  a la Actividad de Condicion Victoria.........
            Intent intent = new Intent().setClass(getContext(), ActivityVictoria.class);
            getContext().startActivity(intent);
        }
    }
    public boolean colisionPersonaje(Enemigo e){

        return Colision.hayColision(e.bitmap(),(int)e.Enemigocoordenada_x,(int)e.Enemigocoordenada_y,
                personaje,(int)xPersonaje,(int)yPersonaje);
    }

    public boolean colision(Enemigo e, Disparo d){
        Bitmap enemigo=this.enemigo1;
        Bitmap disparo=this.disparo;

        return Colision.hayColision(e.bitmap(),(int) e.Enemigocoordenada_x,(int)e.Enemigocoordenada_y,
                disparo,(int)d.coordenada_x,(int)d.coordenada_y);
    }

    public void CreaDisparo(){
        lista_disparos.add(new Disparo(this,xZombie,yZombie));

    }

    /**
     * Este método dibuja el siguiente paso de la animación correspondiente
     */
    public void renderizar(Canvas canvas) {
        if (canvas != null) {
            //pinceles
            Paint myPaint = new Paint();
            myPaint.setStyle(Paint.Style.FILL_AND_STROKE);
            myPaint.setColor(Color.YELLOW);

            Paint myPaint2 = new Paint();
            myPaint2.setStyle(Paint.Style.FILL);
            myPaint2.setTextSize(50);

            //dibujamos el fondo
            canvas.drawBitmap(imagenesFondo[img_actual], yImgActual, 0, null);
            canvas.drawBitmap(imagenesFondo[img_siguiente], yImgSiguiente, 0, null);


            //Si ha ocurrido un toque en la pantalla "Touch", dibujar un círculo
/*
            if (hayToque) {
                synchronized (this) {
                    for (Toque t : toques) {
                        //canvas.drawCircle(t.x, t.y, 100, myPaint);
                        //canvas.drawText(t.index + "", t.x, t.y, myPaint2);
                        lista_disparos.add(new Disparo(this,t.x,t.y));
                    }
                }
            }
*/
            if (!derrota) {

                canvas.drawBitmap(personaje, xPersonaje, yPersonaje, null);
                int srcX = (contadorFrames * anchoSprite) + anchoEscenas;
                int srcY = altoSprite;

                Rect src = new Rect(srcX, 0, srcX + anchoSprite, srcY);
                Rect dst = new Rect(xZombie, yZombie, xZombie + anchoSprite, altoSprite + yZombie);

                canvas.drawBitmap(vampiro, src, dst, null);

                //dibuja los controles
                myPaint.setAlpha(200);
                for (int i = 0; i < 5; i++) {

                    controles[i].Dibujar(canvas, myPaint);

                }
                //dibuja los enemigos
                for (Enemigo e : lista_enemigos) {
                    e.Dibujar(canvas, myPaint);
                }

                //dibuja los disparos
                for (Disparo d : lista_disparos) {
                    d.Dibujar(canvas, myPaint);
                }

                //dibuja las explosiones
                for (Explosion exp : lista_explosiones)
                    exp.Dibujar(canvas, myPaint);

            }

            //escribe los puntos
            myPaint.setTextSize(AnchoPantalla/25); //25 es el número de letras aprox que sale en una línea
            canvas.drawText("PUNTOS " + Puntos + " - Nivel " + Nivel, 550, 1400, myPaint);
            canvas.drawText("Enemigos por matar "+(TOTAL_ENEMIGOS-enemigos_muertos), 550, 1500, myPaint);

            /*
            if(victoria){

                myPaint.setAlpha(0);
                myPaint.setColor(Color.WHITE);
                myPaint.setTextSize(AnchoPantalla/10);
                canvas.drawText("VICTORIA!!", 50, AltoPantalla/2-100, myPaint);
                myPaint.setTextSize(AnchoPantalla/20);
                canvas.drawText("Las tropas enemigas han sido derrotadas", 50, AltoPantalla/2+100, myPaint);
            }

            if(derrota) {

                myPaint.setAlpha(0);
                myPaint.setColor(Color.WHITE);
                myPaint.setTextSize(AnchoPantalla/10);
                canvas.drawText("DERROTA!!", 50, AltoPantalla/2-100, myPaint);
                myPaint.setTextSize(AnchoPantalla/20);
                canvas.drawText("La raza humana está condenada!!!!", 50, AltoPantalla/2+100, myPaint);

                System.out.println("Has perdido");
            }
*/
        }
    }
    public void actualizaSprite() {

//Probar a poner imagene del vampiro en la del dragon


            if (anchoEscenas == 0) {

                xPersonaje = (int) (xPersonaje + 5);

            }
              if (anchoEscenas == anchoPrincpioEscenas) {

                yPersonaje = (int) (yPersonaje + 5);
            }
            if (anchoEscenas == anchoUnaEscena * 2) {

                xPersonaje = (int)  (xPersonaje - 5);
            }
            if (anchoEscenas == anchoUnaEscena * 3) {

                xPersonaje = (int) (yPersonaje - 5);
            }


            anchoSprite = vampiro.getWidth() / COLUMNAS_SUB;

            contadorFrames = ++contadorFrames % 4;
    }

    public void actualiza_fondo(){
        //nueva posición del fondo
        yImgActual ++;
        yImgSiguiente++;

        /*Si la imagen de fondo actual ya ha bajado completamente*/
        if(yImgActual > AnchoPantalla ) {
            //Se actualiza la imagen actual a la siguiente del array de imagenes
            if(img_actual==MAX_IMAGENES_FONDO-1)
                img_actual=0;
            else
                img_actual++;

            //Se actualiza la imagen siguiente
            if(img_siguiente==MAX_IMAGENES_FONDO-1)
                img_siguiente=0;
            else
                img_siguiente++;

            //Nuevas coordenadsa
            yImgActual=0;
            yImgSiguiente=-AnchoPantalla;
        }

    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        int index;
        int x,y;

        // Obtener el pointer asociado con la acción
        index = event.getActionIndex();


        x = (int) event.getX(index);
        y = (int) event.getY(index);

        switch(event.getActionMasked()){
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                hayToque=true;

                synchronized(this) {
                    toques.add(index, new Toque(index, x, y));
                }

                    //se comprueba si se ha pulsado
                    for (int i = 0; i < 5; i++)
                        controles[i].comprueba_pulsado(x, y);

                    break;

            case MotionEvent.ACTION_POINTER_UP:
                synchronized(this) {
                    toques.remove(index);
                }

                //se comprueba si se ha soltado el botón
                for(int i=0;i<5;i++)
                    controles[i].comprueba_soltado(toques);
                break;

            case MotionEvent.ACTION_UP:
                synchronized(this) {
                    toques.clear();
                }
                hayToque=false;
                //se comprueba si se ha soltado el botón
                for(int i=0;i<5;i++)
                    controles[i].comprueba_soltado(toques);
                break;
        }

        return true;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

        // cerrar el thread y esperar que acabe
        boolean retry = true;
        while (retry) {
            try {
                fin();
                bucle.join();
                retry = false;
            } catch (InterruptedException e) {

            }
        }
    }


    public void fin(){

        bucle.fin();
        mediaPlayer.stop();
        mediaPlayer.release();
        for(int i=0;i<MAX_IMAGENES_FONDO;i++)

                 imagenesFondo[i].recycle();
                 personaje.recycle();
                 enemigo2.recycle();
                 enemigo1.recycle();
                 disparo.recycle();

    }
}
