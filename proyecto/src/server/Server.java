package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;


import sede.*;


public class Server {

	public static final Sede sede = new Sede();
	private ArrayList<CmdServer> hilosConectados;
	//private int puertoBase;
	private ServerSocket serverSocket;
	public static ArrayList<Integer> puertosEnUso;
	
	public Server() {
	
		
		hilosConectados = new ArrayList<CmdServer>();
		puertosEnUso = new ArrayList<Integer>();
		try {
			
			this.serverSocket = new ServerSocket(2022);
			do {
			Socket socket = serverSocket.accept();			
			CmdServer hilo = new CmdServer(socket,this);
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
	/**
	 * Metodo que devuelve el array que contiene los hilos conectados
	 * @return devuelve los hilos conectados
	 */
	public ArrayList<CmdServer> getConectados(){
		return this.hilosConectados;
	}
	public static void main(String[] args) {
		new Server();
	}

}
