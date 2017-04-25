package servidor;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.*;

public class Servicio implements Runnable, Cliente.CallBack{

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
				clientes.add(new Cliente(socketCliente, this));
				System.out.println("Conexión establecida");

			}

		}catch(IOException e){
			e.printStackTrace();
		}
	}

	public void enviaDatos(String datos){
		for(Cliente c: clientes)
			c.write(datos);
	}

	public void desconexion(Cliente cliente){
		System.out.println("Conexión terminada con un cliente");
		clientes.remove(cliente);
	}
}
