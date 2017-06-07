package pdi;

/**
 * Created by taka on 5/16/2017.
 */
import org.opencv.core.*;
import org.opencv.core.Scalar;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;
public class Utils {

public Mat ColorReprocess(Mat image,double upper_tresh, double lower_tresh)
{
    //vector escalar con los valores HSV del treshold bajo
    Scalar lower=new Scalar(lower_tresh,200.0,15.0);
    // vector escalar con los valores HSV del treshold alto
    Scalar upper=new Scalar(upper_tresh,255.0,255.0);
    //imagen HSV
    Mat hsv = new Mat(image.width(),image.height(),image.type());
    //Imagen donde se almacena el resultado
    Mat res = new Mat(image.width(),image.height(),image.type());
    //convirtiendo la imagen BGR a HSV
    Imgproc.cvtColor(image, hsv,Imgproc.COLOR_BGR2HSV);
    //se limpian los colores que no estan definidos en el treshold
    Core.inRange(hsv,lower,upper,res);

    return res;
}
public double CalcularMomentos(Mat image)
{
    Mat reference = Imgcodecs.imread( "C:\\Users\\taka\\Pictures\\pruebagf.png");
    //imagen a grises
    Mat imggray=new Mat();

    Mat imagemoments=new Mat();

    //Mat momentsref=new Mat();
    //imagen de rferencia en grises
    Mat refGray=new Mat();
    //convirtiendo imagen de referencia a grises
    Imgproc.cvtColor(reference, refGray,Imgproc.COLOR_BGR2GRAY);
   Imgproc.cvtColor(image, imggray,Imgproc.COLOR_BGR2GRAY);
    Moments invariant=Imgproc.moments(refGray);
    Moments invariant2=Imgproc.moments(imggray);
    double hu[]=new double[7];
    double hu2[]=new double[7];
    //Imgproc.HuMoments(invariant,momentsref);


    //momentos invariantes de la imagen de referencia
    hu[0]=invariant.get_m20()+invariant.get_m02();
    hu[1]=Math.pow(invariant.get_m20()-invariant.get_m02(),2)+4*(Math.pow(invariant.get_m11(),2));
    hu[2]=Math.pow(invariant.get_m30()-3*(invariant.get_m12()),2)+Math.pow(3*(invariant.get_m21())-invariant.get_m03(),2);
    //System.out.println(hu[0]);
    //System.out.println(hu[1]);
    //System.out.println(hu[2]);



//momentos invariantes de la imagen capturada
    hu2[0]=invariant2.get_m20()+invariant2.get_m02();
    hu2[1]=Math.pow(invariant2.get_m20()-invariant2.get_m02(),2)+4*(Math.pow(invariant2.get_m11(),2));
    hu2[2]=Math.pow(invariant2.get_m30()-3*(invariant2.get_m12()),2)+Math.pow(3*(invariant2.get_m21())-invariant2.get_m03(),2);

    //System.out.println(hu2[0]);
    //System.out.println(hu2[1]);
    //System.out.println(hu2[2]);

    //difrencia de la imagen

    //System.out.println("La diferncia es");
    double difm0=(hu[0]-hu2[0])/hu[0];
    double difm1=(hu[1]-hu2[1])/hu[1];
    double difm2=(hu[2]-hu2[2])/hu[2];
    //System.out.println(difm0);
    //System.out.println(difm1);
    //System.out.println(difm2);

    return difm1;
    //System.out.println(momentsref.toString());

    //Imgproc.HuMoments(Imgproc.moments(image),imagemoments);



}
public double[] Centroide(double m00,double m10, double m01)
{
    double[] centroide=new double[2];
    centroide[0]=m10/m00;
    centroide[1]=m01/m00;

    return centroide;
}
public double getVisibility(double angle, double distance)
{
    double alpha=180-(90+(angle/2));
    //System.out.println("angle: "+alpha);
    double sideb=(distance*Math.sin(Math.toRadians(angle/2)))/Math.sin(Math.toRadians(alpha));
    //System.out.println(angle/2);

    //double hip=Math.pow(distance,2)+Math.pow(sideb,2);

    return sideb*2;

}

}
