package servidor;

import jdk.nashorn.internal.codegen.CompilerConstants;
import principal.ManejadorComandos;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Servicio implements Runnable, Cliente.CallBackServer {

	private final int PUERTO;
	private static ArrayList<Cliente> clientes = new ArrayList();
	private volatile boolean terminar = false;
	private ServerSocket servidor;
	private Cliente.CallBackMsg  callBackMsg;

	private Socket socketCliente;

	public Servicio(int PUERTO, Cliente.CallBackMsg callBackMsg){
		this.PUERTO = PUERTO;
		this.callBackMsg = callBackMsg;
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

			while(!terminar){
				socketCliente = servidor.accept();
				clientes.add(new Cliente(socketCliente, this, callBackMsg));
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

    public void close() {
		for (Cliente c : clientes) desconexion(c);
		terminar = true;
    }
}
