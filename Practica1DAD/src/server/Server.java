package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;

import clasesdedatos.*;
import sede.*;


public class Server {

	static final Sede sede =new Sede();
	private ArrayList<HiloComandoServer> hilosConectados;
	//private int puertoBase;
	private ServerSocket serverSocket;
	
	
	public Server() {
	
		
		hilosConectados = new ArrayList<HiloComandoServer>();
		try {
			this.serverSocket = new ServerSocket(2022);
			do {
			Socket socket = serverSocket.accept();			
			HiloComandoServer hilo = new HiloComandoServer(socket,serverSocket);
			hilosConectados.add(hilo);
			hilo.start();
			}while(true);
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
	}
	
	/**
	 * Metodo que devuelve el serversocket
	 * @return server socket 
	 */
	
	public ServerSocket getServerSocket() {
		return this.serverSocket;
	}
	public static void main(String[] args) {
		
		Server server = new Server();
	}

}
