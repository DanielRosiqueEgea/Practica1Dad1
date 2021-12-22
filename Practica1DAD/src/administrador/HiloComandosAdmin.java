package administrador;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.Socket;
import java.util.Hashtable;
import java.util.Scanner;
import java.util.Map.Entry;

import clasesdedatos.Cliente;

public class HiloComandosAdmin extends Thread {
	
	public static final int timeout= 300*1000; //timeout por defecto para el keep alive (300 segundos)
	public static final int mal = 4;			//primer digito de la respuesta de comandos (mala ejecucion)
	public static final int preok=3;			//primer digito de la respuesta de comandos (preok)
	public static final int bien= 2;			//primer digito de la respuesta de comandos (buena ejecucion)
	//segunda parte del codigo de respuesta (se usa principalmente para manejar el canal de datos correctamente)
	public static final int user = 10;
	public static final int pass = 15;
	public static final int add = 2;			
	public static final int get= 3;
	public static final int cliente= 1;
	//public static final int factura =2;
	
	
	
	protected Socket socket;
	protected BufferedReader br;
	private Administrador admin;
	
	
	public HiloComandosAdmin(Socket socket, BufferedReader br) {
		// TODO Auto-generated constructor stub
		this.socket= socket;
		this.br=br;
	}

	public void run() {
		
		while(true) {
			this.escucharMensaje();
		}
	}
	
	public Socket getSocket() {
		return this.socket;
	}
	
	/**
	 * Metodo que se ejecuta dentro del hilo para escuchar todo el rato los mensajes quemanda el server
	 */
	public void escucharMensaje() {
		Scanner scanner = new Scanner(System.in);
		String nombre = null;
		String apellido= null;
		try {
			
			String str =br.readLine();
			System.out.println(str);
			
			if("PREOK".equals(str.split(" ")[0])) {
				
				CanalDatosAdmin canal =new CanalDatosAdmin(str.split(" ")[3],Integer.parseInt(str.split(" ")[4]));
				switch(str.split(" ")[2]) {
				case preok+add+cliente+"":
					System.out.println("Introduce datos del Cliente que quieres crear");
					System.out.println("Introduce el nombre:");
					nombre = scanner.nextLine();
					System.out.println("Introduce el apellido:");
					apellido = scanner.nextLine();
					canal.enviarObjeto(new Cliente(nombre,apellido));
					break;
				case preok+get+cliente+"":
					System.out.println("Introduce el id del cliente");
					int id= scanner.nextInt();
					Hashtable<Integer,Cliente> obj= (Hashtable<Integer,Cliente>)canal.leerObjeto();
					for(Entry<Integer, Cliente> e: obj.entrySet()) {
						System.out.println(e.getValue().toString());
					}	
					break;
					
					default:
						System.out.println("Ha habido un error con la respuesta de PREOK");
						break;
				}
				
					

				
			}
			
			
		} catch (IOException e) {
			try {
				socket.close();
			} catch (IOException e1) {
				
				e1.printStackTrace();
			}
			System.out.println("HA HABIDO UN ERROR");
			this.interrupt();
			System.exit(0);
			e.printStackTrace();
		}
		
	}
	

}
