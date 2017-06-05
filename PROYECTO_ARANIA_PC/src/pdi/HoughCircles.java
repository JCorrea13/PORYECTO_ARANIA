package pdi;

/**
 * Created by taka on 5/16/2017.
 */
//import org.opencv.core.*;
//import org.opencv.core.Point;
//import org.opencv.imgcodecs.Imgcodecs;
//import org.opencv.imgproc.Imgproc;
//import org.opencv.imgproc.Moments;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class HoughCircles {
    public static void main(String[] args) {

        /*
        // Load the native library.
        long startcircles=System.currentTimeMillis();
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        System.out.println(new Utils().getVisibility(46.0,5.0));
        Mat image = Imgcodecs.imread( "C:\\Users\\taka\\Pictures\\ot.jpg");



        System.out.println(new HoughCirclesRun().run(image));


        Mat hsv=new Mat();
        //Imgproc.cvtColor(image,hsv,Imgproc.COLOR_BGR2HSV);
//        Mat filter=new Utils().ColorReprocess(hsv,30,27);

        Mat gray=new Mat();

       // Moments m=Imgproc.moments(filter);
        //double[] centroide=new Utils().Centroide(m.m00,m.m10,m.m01);
        //System.out.println(centroide[0]);
       // System.out.println(centroide[1]);
        //Imgproc.cvtColor(image,hsv,Imgproc.COLOR_BGR2HSV);
        //los valores HSV del tono del rojo pueden ir de 0-10 y de 160-179

        //Mat res= new Utils().ColorReprocess(hsv,160,179);

        //new HoughCirclesRun().displayImage("test",new HoughCirclesRun().toBufferedImage(res));
        //System.out.println(System.currentTimeMillis()-startcircles);
        //long start=System.currentTimeMillis();
        //System.out.println(start);
        //new Utils().CalcularMomentos(image);
        //System.out.println(System.currentTimeMillis()-start);
        //new HoughCirclesRun().displayImage(res);*/
    }
}
