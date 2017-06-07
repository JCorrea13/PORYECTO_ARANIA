package pdi;

/**
 * Created by taka on 5/16/2017.
 */
import org.opencv.core.*;
import org.opencv.core.Point;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

public class HoughCirclesRun {
    public int run(Mat imagen) {
        Mat src =imagen; //Imgcodecs.imread( "C:\\Users\\taka\\Pictures\\circles.jpg");
        double centro=0;
        if(src.empty()) {
            System.out.println("Error opening image");
            System.exit(-1);
        }
       Mat srchsv=new Mat();
        Imgproc.cvtColor(src,srchsv,Imgproc.COLOR_BGR2HSV);
        Mat src_gray =new Utils().ColorReprocess(srchsv,30,27) ;
        //Imgproc.Canny(src_gray,src_gray,100.0,10.0);

        //Mat otra=new Utils().ColorReprocess(src,10,0);
        //Imgproc.cvtColor(otra, lb,Imgproc.COLOR_HSV2BGR);
        //Imgproc.cvtColor(src, src_gray,Imgproc.COLOR_BGR2GRAY);
        Imgproc.GaussianBlur( src_gray, src_gray, new Size(9, 9), 2, 2 );
        Mat gray=new Mat();
//        Imgproc.cvtColor(src_gray,gray,Imgproc.COLOR_BGR2GRAY);
        Mat circles = new Mat();
        double minDist = (double)src.rows()/8;
        // If unknown the min and max radius, put zero as default.
        int minRadius = 0, maxRadius = 15;
        Imgproc.HoughCircles(src_gray, circles, Imgproc.HOUGH_GRADIENT, 1.0, minDist, 100.0, 30.0, minRadius, maxRadius);
        for (int x = 0; x < circles.cols(); x++) {
            double[] c = circles.get(0, x);
            Point center = new Point(Math.round(c[0]), Math.round(c[1]));
            System.out.println(center.x);
            centro=center.x;
            System.out.println(center.y);
            int radius = (int) Math.round(c[2]);
            // circle center
            Imgproc.circle(src_gray, center, 3, new Scalar(100,200,0), -1, 8, 0 );
            // circle outline
            Imgproc.circle(src_gray, center, radius, new Scalar(100,200,0), 3, 8, 0 );
        }
        Image tmpImg = toBufferedImage(src_gray);
        //displayImage("Hough Circle Transform Demo", tmpImg);

        if(centro>0&&centro<=80)
        {
            return 1;

        }
        else if(centro>81&&centro<=160)
        {
            return 2;

        }
        else if(centro>161&&centro<=320)
        {
            return 3;

        }
        else if(centro>321&&centro<=400)
        {
            return 4;
        }
        else if(centro>401&&centro<=480)
        {
            return 5;
        }

        else
        {
            return -1;
        }

    }
    public Image toBufferedImage(Mat m) {
        int type = BufferedImage.TYPE_BYTE_GRAY;
        if ( m.channels() > 1 ) {
            type = BufferedImage.TYPE_3BYTE_BGR;
        }
        int bufferSize = m.channels()*m.cols()*m.rows();
        byte [] b = new byte[bufferSize];
        m.get(0,0,b); // get all the pixels
        BufferedImage image = new BufferedImage(m.cols(),m.rows(), type);
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(b, 0, targetPixels, 0, b.length);
        return image;
    }
    public void displayImage(String title, Image img)
    {
        ImageIcon icon=new ImageIcon(img);
        JFrame frame=new JFrame(title);
        JLabel lbl=new JLabel(icon);
        frame.add(lbl);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}

