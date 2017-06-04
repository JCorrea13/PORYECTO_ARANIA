package com.example.jc.app_arania.camara;

import android.hardware.Camera;
import android.util.Log;
import com.example.jc.app_arania.MainActivity;
import java.util.List;

/**
 * Created by jc on 6/2/17.
 */

public class Camara {

    private Camera camera;
    private MainActivity mainActivity;
    private CallBack callBack;
    private static final String TAG = "CAMARA";

    public Camara(MainActivity activity, CallBack callBack) {

        this.callBack = callBack;
        this.mainActivity = activity;


        if (camera != null) {
            camera.release();
            camera = null;
        }

        camera = Camera.open();
        Camera.Parameters cp = camera.getParameters();
        Log.d(TAG,"Angulo de vision vertical: " + cp.getVerticalViewAngle());
        Log.d(TAG,"Angulo de vision horizontal: " + cp.getHorizontalViewAngle());
        List<Camera.Size> l =  cp.getSupportedPictureSizes();

        Log.d(TAG,"Size Photo");
        for(Camera.Size s : l){
            Log.d(TAG,"H: " + s.height + "-- W: " + s.width);
        }
        cp.setPictureSize(640, 480);

        Log.d(TAG,"Tamaño Actual:: H: " + cp.getPictureSize().height + "W: " + cp.getPictureSize().width);
        cp.set("orientation", "portrait");
        cp.setRotation(180);

    }

    public void tomaFoto(){


        Camera.PictureCallback jpeg = new Camera.PictureCallback() {
            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                System.out.println("Foto tomada: datos[" + data.length + "]");
                System.gc();
                callBack.onTakePhoto(data);
                camera.startPreview();
            }
        };

        System.gc();
        camera.takePicture(null, null, jpeg);
    }

    public void liberaCamara(){
        camera.release();
    }

    public Camera getCamera() {
        return camera;
    }


    public interface CallBack
    {
        void onTakePhoto(byte [] datos);
    }
}
