package com.example.jc.app_arania.servidor;

import android.util.Log;

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
	private CallBackMsg callBackMsg;
	private boolean conexion = true;
	private byte [] datos;
	private final int TAM_PAQUETE = 1024;
	private static final String TAG = "Cliente";

	public Cliente(String HOST, int PUERTO,  CallBackMsg callBackMsg){
		this.HOST = HOST;
		this.PUERTO = PUERTO;
		this.callBackMsg = callBackMsg;

		abreConexion(false);
	}

	public Cliente(Socket socket, CallBackServer callback,  CallBackMsg callBackMsg){
		this.callback = callback;
		this.socket = socket;
		this.callBackMsg = callBackMsg;
		abreConexion(true);
	}

	public void abreConexion(boolean hsocket){

		try{
			if(!hsocket)
				socket = new Socket(HOST, PUERTO);

			flujoSalida = new DataOutputStream(socket.getOutputStream());
			flujoEntrada = new DataInputStream(socket.getInputStream());

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

				byte[] buffer = new byte [1];
				if (flujoEntrada == null) {
					return;
				}

				if(flujoEntrada.available() <= 0){
					return;
				}

				size = flujoEntrada.read(buffer);
				if (size <= -1){
					desconexion();
				} else if (size > 0) {
					Log.d(TAG, "Comando Recibido: " + new String(buffer));
					setDatos(buffer);
				}

			}catch(Exception e){
				Log.d(TAG, "Error al leer datos: " +  e.getMessage());
				e.printStackTrace();
				desconexion();
			}
		}
	}


	public void desconexion(){
		System.out.println("Se ha perdido la conexió");
		try{
			t.cancel();
			socket.close();
			conexion = false;
			if(socket.isConnected())
				callback.desconexion(this);
		}catch(Exception e){}
	}

	public void write(byte [] paquete){

		try{
			flujoSalida.write(paquete);
			flujoSalida.flush();
			Log.d("SERVIDOR", "Datos escritos");
		}catch(Exception e){
        	Log.d("SERVIDOR" ,"Error: " + e.getMessage());
			e.printStackTrace();
		}
	}

	public interface CallBackServer {
		void desconexion(Cliente cliente);
	}

	public interface CallBackMsg{
		void onMsg(byte [] datos, int size);
	}



	public synchronized byte [] getDatos() {
		return datos;
	}

	public synchronized void setDatos(byte [] datos) {
		this.datos = datos;
		if(callBackMsg != null) callBackMsg.onMsg(datos, datos.length);
		System.out.println("Nuevo paquete recibido");
	}
}
