package principal;


import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import servidor.Servicio;

import java.util.Scanner;

public class Main implements ManejadorComandos.CallBack{

    public static void main(String [] args) throws InterruptedException {
        ManejadorComandos mc = new ManejadorComandos(null);
        Servicio servicio = new Servicio(1500, mc);
        servicio.inicia();
        mc.setServer(servicio);


        Scanner sc = new Scanner(System.in);
        String entrada = "";
        while(true){
            entrada = sc.nextLine();
            mc.enviaComando(entrada.charAt(0));
        }

    }

    @Override
    public void onAck(char comando, int ack) {
        System.out.println("Comando [" + comando + "]: "+ ack);
    }

    @Override
    public void onAck(char comando, int ack, byte[] img) {

    }
}
