package servidor;


import java.util.*;
import java.net.Socket;
import java.io.*;


public class Cliente {

	static String HOST;
	static int PUERTO;

	private Socket socket;
	private DataOutputStream flujoSalida;
	private DataInputStream flujoEntrada;
	private Timer t;
	private CallBackServer callback;
	private CallBackMsg callbackmsg;
	private boolean conexion = true;
	private byte [] datos;
	private final int TAM_PAQUETE = 1024;

	public Cliente(String HOST, int PUERTO){
		this.HOST = HOST;
		this.PUERTO = PUERTO;

		abreConexion(false);
	}

	public Cliente(Socket socket, CallBackServer callback, CallBackMsg callBackMsg) {
		this.callbackmsg = callBackMsg;
		this.callback = callback;
		this.socket = socket;
		abreConexion(true);

	}

	public void abreConexion(boolean hsocket){

		try{
			if(!hsocket)
				socket = new Socket(HOST, PUERTO);

			flujoEntrada = new DataInputStream(socket.getInputStream());
			flujoSalida = new DataOutputStream(socket.getOutputStream());

		}catch(Exception e){
			System.out.println("Error: " + e);
			e.printStackTrace();
		}

		t = new Timer();
		t.schedule(new Listener(), 0, 100);
	}

	private class Listener extends TimerTask{
		private int size;

		public void run(){
			try {

				byte[] buffer = new byte[10000];
				if (flujoEntrada == null) {
					return;
				}

				size = flujoEntrada.read(buffer);
				if (size == -1){
					desconexion();
				} else if (size > 0) {
					//System.out.println(new String(buffer));
					setDatos(buffer, size);
				}

			}catch(Exception e){
				e.printStackTrace();
				desconexion();
			}
		}
	}


	private void desconexion(){
		System.out.println("Se ha perdido la conexió");
		try{
			t.cancel();
			socket.close();
			conexion = false;
			if(socket.isConnected())
				callback.desconexion(this);
		}catch(Exception e){}
	}

	public void write(byte [] datos){

		try{
			flujoSalida.write(datos);
		}catch(Exception e){
        	System.out.println("Error: " + e.getStackTrace());
		}
	}

	public interface CallBackServer {
		void desconexion(Cliente cliente);
	}

	public interface CallBackMsg {
		void onMsg(byte [] datos, int size) throws IOException;
	}

	public synchronized byte [] getDatos() {
		return datos;
	}



	public synchronized void setDatos(byte [] datos, int size) throws IOException {
		this.datos = datos;
		if (callbackmsg != null){
			callbackmsg.onMsg(datos, size);
		}
		//System.out.println("Nuevo paquete recibido: ["+ size +"]");
	}

	public static void main(String[] args) {
		System.out.printf("Inicio programa");
		Cliente c = new Cliente("localhost", 1500);

		//c.write("Hola desde el cliente".getBytes());
		System.out.println("Paquete escrito");
	}
}
