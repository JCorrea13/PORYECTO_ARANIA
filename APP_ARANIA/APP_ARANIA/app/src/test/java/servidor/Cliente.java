package servidor;

import java.util.*;
import java.net.Socket;
import java.io.*;


public class Cliente {

	static String HOST;
	static int PUERTO;

	private Socket socket;
	private ObjectOutputStream flujoSalida;
	private ObjectInputStream flujoEntrada;
	private Timer t;
	private CallBack callback;
	private boolean conexion = true;
	private Paquete paquete;

	public Cliente(String HOST, int PUERTO){
		this.HOST = HOST;
		this.PUERTO = PUERTO;

		abreConexion(false);
	}

	public Cliente(Socket socket, CallBack callback){
		this.callback = callback;
		this.socket = socket;
		abreConexion(true);
	}

	public void abreConexion(boolean hsocket){

		try{
			if(!hsocket)
				socket = new Socket(HOST, PUERTO);
			flujoEntrada = new ObjectInputStream(socket.getInputStream());
			flujoSalida = new ObjectOutputStream(socket.getOutputStream());

		}catch(Exception e){
			System.out.println("Error: " + e);
		}

		t = new Timer();
		t.schedule(new Listener(), 0, 100);
	}

	private class Listener extends TimerTask{
		private int size;

		public void run(){
			Paquete paquete;
			try{

				if(flujoEntrada == null) return;

				paquete = (Paquete) flujoEntrada.readObject();
				if(paquete != null)
					setPaquete(paquete);

			}catch(Exception e){
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

	public void write(String datos){

		try{	
			flujoSalida.write(datos.getBytes());
		}catch(Exception e){
                       // System.out.println("Error: " + e);
                }
	}

	public interface CallBack{
		void desconexion(Cliente cliente);
	}

	public synchronized Paquete getPaquete() {
		return paquete;
	}

	public synchronized void setPaquete(Paquete paquete) {
		this.paquete = paquete;
		System.out.println("Nuevo paquete recibido");
	}
}
