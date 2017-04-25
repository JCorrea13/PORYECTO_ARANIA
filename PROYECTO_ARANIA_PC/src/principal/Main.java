package principal;


import servidor.Servicio;

public class Main {

    public static void main(String [] args){
        Servicio servicio = new Servicio(1001);
        servicio.inicia();
    }
}
