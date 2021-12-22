package administrador;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Hashtable;
import java.util.Map.Entry;

import clasesdedatos.Cliente;

import java.util.Scanner;

public class Administrador {

	private int number =0;
	private HiloComandosAdmin hilo;
	
	/**
	 * Metodo que muestra le muestra el menú al admin
	 */
	public void menu() {
		System.out.println("\nQUE DESEA HACER?");
		System.out.println("USER <name>");
		System.out.println("PASS <pass>");
		System.out.println("ADDCLIENTE");
		System.out.println("UPDATECLIENTE <id>");
		System.out.println("GETCLIENTE <id>");
		System.out.println("REMOVECLIENTE <id>");
		System.out.println("LISTCLIENTES");
		System.out.println("COUNTCLIENTES");
		System.out.println("9 -> Cargar Base de datos");
		System.out.println("10 -> Guardar Base de datos");
		System.out.println("EXIT\n");	
	}
	
	public Administrador() {
		
		Socket socket= null;
		String lineaLeida;
		String comando;
		String respuestaServer;
		Scanner scanner=null;
		
		try {
			socket = new Socket("localhost", 2022);
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			PrintWriter pw = new PrintWriter(socket.getOutputStream());
			System.out.println("CONECTADO CORRECTAMENTE");
			hilo= new HiloComandosAdmin(socket,br);
			hilo.start();
			scanner = new Scanner(System.in);
			
			do {
				this.menu();
				lineaLeida = scanner.nextLine();
				comando= lineaLeida.split(" ")[0];
				lineaLeida= this.number +" "+lineaLeida;
				pw.println(lineaLeida);
				pw.flush();
				switch(comando) {
				case "USER":
					break;
				case "PASS":
					break;
				case "ADDCLIENTE":	
					break;
				case "GETCLIENTE":
					break;
					default:
						System.out.println("COMANDO NO ADMITIDO");
						break;
				}
				
				this.actualizarNumber();
				
			}while(!"EXIT".equals(lineaLeida));
			
			
		} catch (Exception e) {
			
			e.printStackTrace();
		}		
		
		try {
			socket.close();
			scanner.close();
			hilo.interrupt();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}

	public static void main(String[] args) {
		new Administrador();
	}
	
	
	
	/**
	 * Metodo que actuliza el numero de la funcion (se hace una llamada cada vez que se ejecuta un comando)
	 */
	public void actualizarNumber() {
		this.number++;
	}

}
