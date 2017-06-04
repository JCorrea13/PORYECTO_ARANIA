package com.example.jc.app_arania.servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Servicio implements Runnable, Cliente.CallBackServer {

	private final int PUERTO;
	private static ArrayList<Cliente> clientes = new ArrayList();
	private volatile boolean terminar = false;
	private ServerSocket servidor;

	private Socket socketCliente;

	public Servicio(int PUERTO){
		this.PUERTO = PUERTO;
	}

	public void inicia(){
		Thread thread = new Thread(this);
		thread.start();
	}

	public void run(){

		try{

			//Iniciamos el servidor
			servidor = new ServerSocket(PUERTO);
			System.out.println("Servidor prendido en el puerto: " +  PUERTO);

			while(true){
				socketCliente = servidor.accept();
				clientes.add(new Cliente(socketCliente, this, null));
				System.out.println("Conexión establecida");

			}

		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void enviaDatos(byte [] datos){
		for(Cliente c: clientes)
			c.write(datos);
	}

	public void desconexion(Cliente cliente){
		System.out.println("Conexión terminada con un cliente");
		clientes.remove(cliente);
	}
}
