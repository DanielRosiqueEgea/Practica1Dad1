package administrador;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;

public class CanalDatosAdmin{

	private ObjectOutputStream oos;
	private Socket socket;
	
	
	public CanalDatosAdmin(String ip,int puerto) {
		
		try {
			socket = new Socket(ip,puerto);
			
			
		} catch (UnknownHostException e) {
			
			e.printStackTrace();
		} catch (IOException e) {
			
			e.printStackTrace();
		}
		
		
		
	}
	
	
	public boolean enviarObjeto(Object object) {
	
		try {
			oos = new ObjectOutputStream(socket.getOutputStream());
			oos.writeObject(object);
			oos.flush();
			return true;
		} catch (IOException e) {
			
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Metodo para leer un objeto desde el administrador
	 * @return devuelve el objeto leido
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
	
	
}
