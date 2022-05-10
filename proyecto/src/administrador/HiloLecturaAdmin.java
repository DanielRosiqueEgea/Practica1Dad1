package administrador;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Scanner;

import javax.swing.JOptionPane;

import java.util.Map.Entry;

import clasesdedatos.Cliente;
import clasesdedatos.Producto;
import clasesdedatos.Usuario;
import server.CmdServer;

public class HiloLecturaAdmin extends Thread {

	protected Socket socket;
	protected BufferedReader br;

	protected PrintWriter pw;
	protected Usuario usuario;
	protected CanalDatosAdmin canal;
	protected int number = 0;
	private InterfazComandosAdmin ica;

	public HiloLecturaAdmin(Socket socket, BufferedReader br, PrintWriter pw, InterfazComandosAdmin ica) {
		this.socket = socket;
		this.br = br;
		this.pw = pw;
		this.ica = ica;
	}

	public void run() {

		String str;
		do {
			try {
				str = br.readLine();
				ica.imprimirTexto(str); // respuesta del servidor

				if ("PREOK".equals(str.split(" ")[0])) { // Solo los comandos que devuelven PEROK se necesitan manejar
															// de esta manera
					menejoPreok(str);
				}
				if ("KEEPALIVE".equals(str.split(" ")[0])) {
					new Keepalive(ica);
				}
			} catch (IOException e) {
				ica.imprimirTexto("HA HABIDO UN ERROR");

				break;
			}
		} while (!"EXIT".equals(str.split(" ")[0]));

		ica.imprimirTexto("UN PLACER, VUELVA PRONTO");
		try {
			socket.close();
		} catch (Exception e) {
		}

		this.interrupt();
	}

	public Socket getSocket() {
		return this.socket;
	}

	/**
	 * @param scanner
	 * @param str
	 */

	public void menejoPreok(String str) {

		String codRespuesta = str.split(" ")[2];
		int puerto = Integer.parseInt(str.split(" ")[4]);
		String ip = str.split(" ")[3];

		CanalDatosAdmin canal = new CanalDatosAdmin(ip, puerto);
		Cliente cliente;
		Producto producto;
		// si se conecta correctamente manda un OK al hilo de comandos
		if (!canal.conectado) {
			pw.println("NOT");
			pw.flush();
			ica.imprimirTexto("ERROR AL CONECTAR CON EL CANAL DE DATOS");
			return;
		}

		pw.println("OK");
		pw.flush();
		switch (codRespuesta) {
		case CmdServer.PREOK + CmdServer.UPDATE + CmdServer.CLIENTE:
			cliente = (Cliente) canal.leerObjeto();
			if (cliente == null) {
				ica.imprimirTexto("ERROR AL OBTENER EL CLIENTE");
				return;
			}
			new InterfazCliente(canal, cliente.getNombre(), cliente.getApellido());
			break;
		case CmdServer.PREOK + CmdServer.UPDATE + CmdServer.PRODUCTO:
			producto = (Producto) canal.leerObjeto();
			if (producto == null) {
				ica.imprimirTexto("ERROR AL OBTENER EL PRODUCTO");
				return;
			}
			new InterfazProducto(canal, producto.getNombre(), producto.getPrecio());
			break;
		case CmdServer.PREOK + CmdServer.ADD + CmdServer.CLIENTE:

			new InterfazCliente(canal);

			break;
		case CmdServer.PREOK + CmdServer.ADD + CmdServer.PRODUCTO:

			new InterfazProducto(canal);

			break;
		case CmdServer.PREOK + CmdServer.GET + CmdServer.CLIENTE:
			cliente = (Cliente) canal.leerObjeto();
			if (cliente == null) {
				ica.imprimirTexto("ERROR AL OBTENER EL CLIENTE");
				return;
			}
			ica.imprimirTexto(cliente.toString());

			break;
		case CmdServer.PREOK + CmdServer.GET + CmdServer.PRODUCTO:
			producto = (Producto) canal.leerObjeto();
			if (producto == null) {
				ica.imprimirTexto("ERROR AL OBTENER EL PRODUCTO");
				return;
			}
			ica.imprimirTexto(producto.toString());

			break;
		case CmdServer.PREOK + CmdServer.LIST + CmdServer.CLIENTE:

			@SuppressWarnings("unchecked")
			Hashtable<Integer, Cliente> cli = (Hashtable<Integer, Cliente>) canal.leerObjeto();
			for (Entry<Integer, Cliente> e : cli.entrySet()) {
				ica.imprimirTexto(e.getKey() + " -> " + e.getValue().toString());
			}
			break;
		case CmdServer.PREOK + CmdServer.LIST + CmdServer.PRODUCTO:

			@SuppressWarnings("unchecked")
			Hashtable<Integer, Producto> prod = (Hashtable<Integer, Producto>) canal.leerObjeto();
			for (Entry<Integer, Producto> e : prod.entrySet()) {
				ica.imprimirTexto(e.getKey() + " -> " + e.getValue().toString());
			}
			break;
		case CmdServer.PREOK + CmdServer.LIST + CmdServer.USUARIO:
			@SuppressWarnings("unchecked")
			ArrayList<String> conectados = (ArrayList<String>) canal.leerObjeto();
			for (String s : conectados) {
				ica.imprimirTexto(s);
			}

			break;

		default:
			ica.imprimirTexto("Ha habido un error con la respuesta de PREOK");
			break;
		}
		try {
			ica.imprimirTexto(br.readLine());
		} catch (IOException e1) {
			return;
		}
	}


}
