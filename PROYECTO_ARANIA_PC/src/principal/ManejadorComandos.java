package principal;

import com.sun.deploy.util.SessionState;
import servidor.Cliente;
import servidor.Servicio;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by jc on 6/3/17.
 */
public class ManejadorComandos implements Cliente.CallBackMsg{

    private CallBack callBack;
    private Servicio server;
    private volatile boolean ejecutando_comando = false;
    private Queue<Character> cola_comandos = new LinkedBlockingQueue<>();
    private Character comando_actual = '0';


    //------------- VARIABLES PARA MANEJO DE COMANDOS 'A' ----- Imagenes
    private boolean primer = true;
    private int tam_imagen;
    private int indice_actual;
    private byte [] imagen;

    public ManejadorComandos(CallBack callBack){
        this.callBack = callBack;
    }

    public void setServer(Servicio server){
        this.server = server;
    }

    @Override
    public void onMsg(byte[] datos, int size) throws IOException {

        switch (comando_actual){
            case 'A':
                leeComandoA(datos, size);
                break;
            default:
                if (callBack != null) callBack.onAck(comando_actual, datos[0]);
                System.out.println("Paquete normal recibido ["+ size +"]");
                if (callBack != null) callBack.onAck(comando_actual, 1);
                ejecutando_comando = false;
                ejecutaSiguienteComando();

        }


    }

    private void leeComandoA(byte [] datos, int size) throws IOException {

        int i = 0;
        if(primer){
            tam_imagen = ByteBuffer.wrap(Arrays.copyOf(datos, 4)).getInt();
            imagen = new byte[tam_imagen];
            indice_actual = 0;
            i = 4;
            primer = false;
        }

        //eliminar los primeros 4 bytes
        for( ; i < size; i ++)
            imagen[indice_actual ++] = datos[i];

        if ((indice_actual) == tam_imagen) {
            primer = true;
            System.out.println("Imagen recibida["+ indice_actual +"]");
            if (callBack != null) callBack.onAck('A', 1, imagen);
            ejecutando_comando = false;
            ejecutaSiguienteComando();

        }

    }

    private void ejecutaSiguienteComando() {
        if (!ejecutando_comando){
            comando_actual = cola_comandos.poll();
            if(comando_actual != null){
                ejecutando_comando = true;
                server.enviaDatos(new byte[]{(byte) (char) comando_actual});
                System.out.println("Ejecutando comando: " +  comando_actual);
            }
        }
    }

    public interface CallBack{
        void onAck(char comando, int ack);
        void onAck(char comando, int ack, byte [] img) throws IOException;
    }

    public void enviaComando(char comando){
        cola_comandos.offer(comando);
        ejecutaSiguienteComando();
    }

    public byte[] getImagen() {

        
        return imagen;
    }
}
