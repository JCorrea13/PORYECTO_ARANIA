package com.example.jc.app_arania;


import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.jc.app_arania.camara.Camara;
import com.example.jc.app_arania.servidor.Cliente;
import com.physicaloid.lib.Physicaloid;
import com.physicaloid.lib.usb.driver.uart.ReadLisener;

import java.io.IOException;
import java.nio.ByteBuffer;

public class MainActivity extends AppCompatActivity implements Camara.CallBack, Cliente.CallBackMsg {

    private Cliente cliente = null;
    private Button button;
    private Button btnEscribeSerial;
    private Physicaloid  usboid;
    private EditText text;
    private TextView log;
    private ImageView imageView;
    private Cliente.CallBackMsg self = this;

    //private static final String IP = "192.168.1.88";
    private static final String IP = "192.168.43.33";
    private static final int PUERTO = 1500;

    //Objetos para manejo de comandos
    private volatile boolean ejecutando_comando = false;
    private volatile char comando;
    private volatile byte [] imagen;
    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private Camara camara ;
    public boolean safeToTakePicture = false;
    private static final boolean IS_TEST = true;

    @Override
    protected void onNewIntent(Intent intent) {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        button = (Button) findViewById(R.id.button);
        btnEscribeSerial = (Button) findViewById(R.id.EscribeSerial);
        text = (EditText) findViewById(R.id.textConeccion);
        surfaceView = (SurfaceView) findViewById(R.id.surfaceView);
        imageView = (ImageView) findViewById(R.id.imageView);
        log = (TextView) findViewById(R.id.textLog);
        //surfaceView.setVisibility(View.INVISIBLE);

        final PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
        pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK, "My Tag").acquire();


        if(IS_TEST){
            button.setVisibility(View.INVISIBLE);
            btnEscribeSerial.setVisibility(View.INVISIBLE);
            text.setVisibility(View.INVISIBLE);
            imageView.setVisibility(View.INVISIBLE);
        }



        AsyncTask a = new AsyncTask() {
            @Override
            protected Object doInBackground(Object[] params) {

                Log.d("SERVIDOR", "Creando cliente");
                //System.out.println("Creando el cliente");
                cliente = new Cliente(IP, PUERTO, self);
                return true;
            }
        }.execute();


        /*button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cliente.write("hola desde android".getBytes());
            }
        });*/



        //Configuracion USB
        usboid = new Physicaloid(this);
        usboid.setBaudrate(9600);

        if(usboid.open()){
            //text.setText("Puerto abierto");
            updateLog("Puerto abierto");
        }

        usboid.addReadListener(new ReadLisener() {
            @Override
            public void onRead(int i) {
                Log.d("Serial","ACK = " +  i);
                updateLog("USB-ACK: " + i);
                ackComando((byte) i);
            }
        });


        /*btnEscribeSerial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                usboid.write("D".getBytes());
            }
        });

        /*
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                camara.tomaFoto();
                safeToTakePicture = false;
            }
        });
        */
    }

    @Override
    public void onStop(){
        //cliente.desconexion();
        super.onStop();
    }


    @Override
    public void onResume(){
        super.onResume();
        camara = new Camara(this, this);

        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.setFixedSize(10,10);

        surfaceHolder.addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    camara.getCamera().setPreviewDisplay(surfaceHolder);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                Camera.Parameters params = camara.getCamera().getParameters();
                Camera.Size size = getBestPreviewSize(width, height, params);
                Camera.Size pictureSize=getSmallestPictureSize(params);
                params.setPictureSize(640, 480);

                camara.getCamera().startPreview();
                safeToTakePicture = true;
            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }
        });
        surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    @Override
    public void onPause() {
        super.onPause();
        camara.liberaCamara();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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


    private void EjecutaComandos(final char comando){

        //validamos si se esta ejecutando algun comando
        if(ejecutando_comando) return;

        switch (comando){
            case 'A': { //Imagen y distancia por ultrasonico
                ejecutando_comando = true;
                this.comando = comando;
                camara.tomaFoto();
                break;}
            case 'B': { //Caminar cuadrante 1, Derecha exterior
                ejecutando_comando = true;
                this.comando = comando;
                byte [] ac = new byte[1];
                ac[0] = (byte) comando;
                updateLog("Escribiendo en usb:" + comando);
                usboid.write(ac);
                break;}
            case 'C': { //Caminar cuadrante 2, Derecha interior
                ejecutando_comando = true;
                this.comando = comando;
                byte [] ac = new byte[1];
                ac[0] = (byte) comando;
                usboid.write(ac);
                break;}
            case 'D': { //Caminar cuadrante 3, Centro
                ejecutando_comando = true;
                this.comando = comando;
                byte [] ac = new byte[1];
                ac[0] = (byte) comando;
                usboid.write(ac);
                break;}
            case 'E': { //Caminar cuadrante 4, Izquerda interior
                ejecutando_comando = true;
                this.comando = comando;
                byte [] ac = new byte[1];
                ac[0] = (byte) comando;
                usboid.write(ac);
                break;}
            case 'F': {//Caminar cuadrante 5, Izquierda exterior
                ejecutando_comando = true;
                this.comando = comando;
                byte [] ac = new byte[1];
                ac[0] = (byte) comando;
                usboid.write(ac);
                break;}
            case 'G': { //Caminar reversa
                ejecutando_comando = true;
                this.comando = comando;
                byte [] ac = new byte[1];
                ac[0] = (byte) comando;
                usboid.write(ac);
                break;}
            case 'H': { //Ultrasonico
                ejecutando_comando = true;
                this.comando = comando;
                byte [] ac = new byte[1];
                ac[0] = (byte) comando;
                usboid.write(ac);
                break;}
        }

        updateLog("Comando Escrito:" + comando);

    }

    public void updateLog(final String string){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                log.setText(string);
            }
        });
    }

    private void ackComando(byte ack){

        switch (this.comando){

            case 'A':
                cliente.write(unir( ByteBuffer.allocate(4).putInt(imagen.length).array() ,imagen));
                break;
            default:
                cliente.write(new byte []{ack});
                break;
        }

        ejecutando_comando = false;
    }


    private byte [] unir(byte [] array1, byte [] array2){
        byte [] union = new byte [array1.length+array2.length];

        int i = 0;
        for(; i < array1.length; i ++)
            union[i] = array1[i];

        for(int j = 0; j < array2.length; j ++, i++)
            union[i] = array2[j];

        return union;
    }


    private Camera.Size getBestPreviewSize(int width, int height,
                                           Camera.Parameters parameters) {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
            if (size.width <= width && size.height <= height) {
                if (result == null) {
                    result=size;
                }
                else {
                    int resultArea=result.width * result.height;
                    int newArea=size.width * size.height;

                    if (newArea > resultArea) {
                        result=size;
                    }
                }
            }
        }

        return(result);
    }

    private Camera.Size getSmallestPictureSize(Camera.Parameters parameters) {
        Camera.Size result=null;

        for (Camera.Size size : parameters.getSupportedPictureSizes()) {
            if (result == null) {
                result=size;
            }
            else {
                int resultArea=result.width * result.height;
                int newArea=size.width * size.height;

                if (newArea < resultArea) {
                    result=size;
                }
            }
        }

        return(result);
    }


    @Override
    public void onDestroy(){
        if(usboid != null) {
            usboid.clearReadListener();
            usboid.close();
        }
        super.onDestroy();
    }

    @Override
    public void onTakePhoto(byte[] datos) {
        //imageView.setImageBitmap(BitmapFactory.decodeByteArray(datos, 0, datos.length));
        imagen = datos;
        ackComando((byte) -1); //no se manda nada de ack
        //cliente.write(datos);
    }

    @Override
    public void onMsg(byte[] datos, int size) {
        EjecutaComandos((char) datos[0]);
    }
}
