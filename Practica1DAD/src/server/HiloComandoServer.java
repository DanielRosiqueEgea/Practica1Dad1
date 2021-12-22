package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import clasesdedatos.Cliente;
import clasesdedatos.Usuario;

public class HiloComandoServer extends Thread {
	
	public static final int timeout= 300*1000; //timeout por defecto para el keep alive (300 segundos)
	public static final int mal = 4;			//primer digito de la respuesta de comandos (mala ejecucion)
	public static final int preok=3;			//primer digito de la respuesta de comandos (preok)
	public static final int bien= 2;			//primer digito de la respuesta de comandos (buena ejecucion)
	//segunda parte del codigo de respuesta (se usa principalmente para manejar el canal de datos correctamente)
	public static final int user = 1;
	public static final int pass = 2;
	public static final int add = 3;			
	public static final int get= 4;
	public static final int incorrecto =5;
	public static final int cliente= 1;
	
	
	
	
	private Socket socket;
	private ServerSocket serverSocket;
	private BufferedReader bufferedReader;
	private PrintWriter printWriter;
	
	private Usuario usuario;

	public HiloComandoServer(Socket socket, ServerSocket serverSocket) {
		// TODO Auto-generated constructor stub
		this.socket=socket;
		this.serverSocket= serverSocket;
		try {
			this.bufferedReader=new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.printWriter= new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		usuario=null;
	}
	
	
	public void generarMensaje(int resultado,int comando,int clase, String number) {
		switch(Integer.parseInt(resultado+comando+clase+"")) {
		case 210: //bien + user
			this.enviarMensaje("OK " + number + " "+210+ " ENVIE CONTRASEÑA");
			break;
		case 410: // mal + user
			this.enviarMensaje("FAILED " + number + " "+410+" NOT A USER");
			break;
		case 220: //bien+pass
			this.enviarMensaje("OK " + number + " "+ 220+ " WELCOME: "+ usuario.getUser());
			break;
		case 420: //mal + pass
			this.enviarMensaje("FAILED " + number+ " "+420 + " PRUEBA DE NUEVO");
			break;
		case 231: // ok+ add+ cliente
			this.enviarMensaje("OK " +number+" "+231+ " TRANSFERENCIA TERMINADA");
			break;
		case 331:  // preok + add + cliente
			this.enviarMensaje("PREOK "+ number +" " +331+ " localhost " + 2023);
			break;
		case 431: // mal +add + cliente
			this.enviarMensaje("FAILED " + number +" "+ 431+ " ERROR AL AÑADIR EL CLIENTE");
			break;
		
		
		default :
			
			break;
		}
	}
	
	
	
	public void run() {
		
	
		String userName=null;
		String password=null;
		boolean existe=false;	
		
		String lineaLeida =null;
		String comando=null;
		String number=null;
		Cliente tmpCliente=null;
		
		do {
			
			try {
				lineaLeida =bufferedReader.readLine();
				String split[] = lineaLeida.split(" ");
				comando = split[1]; //separa el comando y el numero de la linea leida
				number = lineaLeida.split(" ")[0];
				switch(comando) {
				case "USER":
					if(lineaLeida.split(" ").length<3) {
						this.generarMensaje(mal, user, 0, number);
						break;
					}
					userName = lineaLeida.split(" ")[2];
					if(!Server.sede.buscarUser(userName)) {
						this.generarMensaje(mal, user, 0, number);
						break;
					}
					
					this.generarMensaje(bien, user, 0, number);
					break;
				case "PASS":
					if(userName ==null) {
						this.generarMensaje(mal, pass, 0, number);
						break;
					}
					
					usuario = Server.sede.inicioSesion(userName, password);
					if(usuario == null) {
						this.generarMensaje(mal, pass, 0, number);
						break;
					}
					this.generarMensaje(bien, pass, 0, number);
					
					break;
				case "ADDCLIENTE":
					if(usuario== null) {
						this.generarMensaje(mal, add, cliente, number);
						break;
					}
					HiloCanalDatosServer hiloDatos = new HiloCanalDatosServer(2023,this.socket);
					this.generarMensaje(preok,add,cliente,number);
					tmpCliente =(Cliente)hiloDatos.leerObjeto();
					if(tmpCliente == null) {
						this.generarMensaje(mal, add, cliente, number);
						break;
					}
					if(!Server.sede.addCliente(tmpCliente)) {
						this.generarMensaje(mal, add, cliente, number);
						break;
					}
					this.generarMensaje(bien, add, cliente, number);
					break;
				case "GETCLIENTES":
					if(usuario== null) {
						this.enviarMensaje("FAILED "+ number +" "+ mal+get+cliente+ " USUARIO NO CONECTADO");
						break;
					}
					
					
					this.enviarMensaje("PREOK "+ number + " " +preok+get+cliente+ " localhost " + 2023);
					 hiloDatos = new HiloCanalDatosServer(2023,this.socket);
					hiloDatos.enviarObjeto(Server.sede.getClientes());
					break;
				
				
				
				}
				
				
			} catch (IOException e) {
				System.out.println("HA HABIDO UN ERROR");
				this.enviarMensaje("FAILED ERROR EN EL SERVIDOR" );
				e.printStackTrace();
			}
			
		}while(!"EXIT".equals(comando));
		
		
		this.enviarMensaje("Gracias por su visita");
		try {
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		this.interrupt();
	}
	
	/**
	 * Metodo que sirve para comprobar que el usuario sigue activo
	 * @return true si el usuario responde, false si no
	 */
	public boolean keepAlive() {
		try {
			System.out.println("Se va a cerrar la conexion, manda OK para mantenerte conectado");
			printWriter.println("Se va a cerrar la conexion, manda OK para mantenerte conectado");
			printWriter.flush();
			this.socket.setSoTimeout(timeout);
			String string=this.bufferedReader.readLine();
			String comprobacion = string.split(" ")[1];
			if("OK".equals(comprobacion))return true;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			System.out.println("se va a cerrar todo");
			try {
				this.interrupt();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				//e1.printStackTrace();
			}
			
			
			//e.printStackTrace();
		}
		
		return false;
	}
	
	/**
	 * metodo para enviar un mensaje por el socket
	 * @param str mensaje a enviar
	 */
	public void enviarMensaje(String str) {
		
		printWriter.println(str);
		printWriter.flush();
		
		
	}
}
