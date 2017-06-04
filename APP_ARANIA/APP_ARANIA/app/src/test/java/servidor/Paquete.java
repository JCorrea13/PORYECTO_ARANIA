package servidor;

import java.awt.*;
import java.io.Serializable;

/**
 * Esta clase modela los paquetes que se
 * enviaran y recibiran con los clientes.
 *
 * @author Jose Correa
 */

public class Paquete implements Serializable{

    private Image img;
    private int distancia;
    private int vector [];

    public Paquete(Image img, int distancia) {
        this.img = img;
        this.distancia = distancia;
    }

    public Image getImg() {
        return img;
    }

    public void setImg(Image img) {
        this.img = img;
    }

    public int getDistancia() {
        return distancia;
    }

    public void setDistancia(int distancia) {
        this.distancia = distancia;
    }

    public int[] getVector() {
        return vector;
    }

    public void setVector(int magitud, int angulo) {
        this.vector[0] = magitud;
        this.vector[1] = angulo;
    }
}
