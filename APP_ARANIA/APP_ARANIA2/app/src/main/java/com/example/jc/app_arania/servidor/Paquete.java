package com.example.jc.app_arania.servidor;


import java.io.IOException;
import java.io.Serializable;

/**
 * Esta clase modela los paquetes que se
 * enviaran y recibiran con los clientes.
 *
 * @author Jose Correa
 */

public class Paquete implements Serializable{
    static final long serialVersionUID = 1020100102103120123L;

    private String msg;
    private byte [] img;
    private int distancia;
    private int vector [];

    public Paquete(byte [] img, int distancia) {
        this.img = img;
        this.distancia = distancia;
    }

    public byte [] getImg() {
        return img;
    }

    public void setImg(byte [] img) {
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

    public void setVector(int magitud, int angulo) throws IOException {
        this.vector[0] = magitud;
        this.vector[1] = angulo;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString(){
        return msg;
    }
}
