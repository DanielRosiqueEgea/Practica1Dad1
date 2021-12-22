package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class HiloCanalDatosServer extends Thread {
	
	private ServerSocket ss;
	private int puerto;
	private ObjectOutputStream oos;
	private ObjectInputStream ios;
	private Socket socket;
	
	public HiloCanalDatosServer(int puerto, Socket socket) {
		// TODO Auto-generated constructor stub
		this.puerto=puerto;
		try {
			ss = new ServerSocket(this.puerto);
			socket = ss.accept();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/**
	 * Metodo que envia un objeto a traves del canal de datos
	 * @param object objeto que hay que enviar
	 * @return true si ha sido capaz de hacerlo correctamente
	 */
	public boolean enviarObjeto(Object object) {
		
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(object);
			oos.flush();
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Metodo para leer un objeto usando el canal de datos
	 * @return el objeto leido si es capaz de leerlo
	 */
	public Object leerObjeto() {
		ObjectInputStream ios;
		Object object=null;
		try {
			ios = new ObjectInputStream(socket.getInputStream());
			object = (Object) ios.readObject();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return object;
	}
	
	public void run() {
		
		
	}

}
