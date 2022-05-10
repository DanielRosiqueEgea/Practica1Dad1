package server;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;

import clasesdedatos.Cliente;
import clasesdedatos.Producto;

public class HiloCanalDatosServer extends Thread {

	private ServerSocket ss;
	private int puerto;
	private ObjectOutputStream oos;
	private ObjectInputStream ios;
	private Socket socket;
	private String modo;
	private String number;
	private CmdServer hiloCmd;
	private int id;

	public HiloCanalDatosServer(int puerto, String modo, String number, CmdServer hiloCmd) {
		this.puerto = puerto;
		try {
			this.hiloCmd = hiloCmd;
			ss = new ServerSocket(this.puerto);
			this.modo = modo;
			this.number = number;

			this.start();
		} catch (IOException e) {
			hiloCmd.writeLog("Error al crear canal de datos");
		}
	}

	public HiloCanalDatosServer(int puerto, String modo, String number, CmdServer hiloCmd, int id) {
		this(puerto, modo, number, hiloCmd);
		this.id = id;
	}

	/**
	 * Metodo que envia un objeto a traves del canal de datos
	 * 
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
			hiloCmd.writeLog("Error al enviar objeto por el canal de datos");
			return false;
		}
	}

	/**
	 * Metodo para leer un objeto usando el canal de datos
	 * 
	 * @return el objeto leido si es capaz de leerlo
	 */
	public Object leerObjeto() {
		Object object = null;
		try {
			ios = new ObjectInputStream(socket.getInputStream());
			object = (Object) ios.readObject();
		} catch (Exception e) {
			hiloCmd.writeLog("Error al recibir objeto por el canal de datos");
		}
		return object;
	}

	public void run() {
		try {
			this.socket = ss.accept();
			Cliente tmpCliente;
			Producto tmpProducto;
			switch (this.modo) {

			case CmdServer.ADD + CmdServer.CLIENTE:
				tmpCliente = null;
				tmpCliente = (Cliente) this.leerObjeto();
				if (tmpCliente == null) {
					hiloCmd.generarMensaje(CmdServer.MAL, CmdServer.ADD, CmdServer.CLIENTE, number, 0);
					hiloCmd.writeLog("ERROR: No se ha leido ningun objeto");
					break;
				}
				if (!Server.sede.addCliente(tmpCliente)) {
					hiloCmd.generarMensaje(CmdServer.MAL, CmdServer.ADD, CmdServer.CLIENTE, number, 0);
					hiloCmd.writeLog("ERROR: El cliente no se ha podido insertar correctamente");
					break;
				}
				hiloCmd.generarMensaje(CmdServer.BIEN, CmdServer.ADD, CmdServer.CLIENTE, number, 0);
				hiloCmd.writeLog("Se ha insertado correctamente el cliente");

				break;
			case CmdServer.ADD + CmdServer.PRODUCTO:
				tmpProducto = null;
				tmpProducto = (Producto) this.leerObjeto();
				if (tmpProducto == null) {
					hiloCmd.generarMensaje(CmdServer.MAL, CmdServer.ADD, CmdServer.PRODUCTO, number, 0);
					hiloCmd.writeLog("ERROR: No se ha leido ningun objeto");
					break;
				}
				if (!Server.sede.addProducto(tmpProducto)) {
					hiloCmd.generarMensaje(CmdServer.MAL, CmdServer.ADD, CmdServer.PRODUCTO, number, 0);
					hiloCmd.writeLog("ERROR: El Producto no se ha podido insertar correctamente");
					break;
				}
				hiloCmd.generarMensaje(CmdServer.BIEN, CmdServer.ADD, CmdServer.PRODUCTO, number, 0);
				hiloCmd.writeLog("Se ha insertado correctamente el producto");
				break;
			case CmdServer.UPDATE + CmdServer.CLIENTE:

				getCliente();// le envia el cliente al usuario para que pueda visualizar los datos antes de
								// modificarlos
				tmpCliente = null;
				tmpCliente = (Cliente) this.leerObjeto();
				if (tmpCliente == null) {
					hiloCmd.generarMensaje(CmdServer.MAL, CmdServer.UPDATE, CmdServer.CLIENTE, number, 0);
					hiloCmd.writeLog("ERROR: No se ha leido ningun objeto");
					break;
				}
				if (!Server.sede.updateCliente(id, tmpCliente)) {
					hiloCmd.generarMensaje(CmdServer.MAL, CmdServer.UPDATE, CmdServer.CLIENTE, number, 0);
					hiloCmd.writeLog("ERROR: El cliente no se ha podido actualizar correctamente");
					break;
				}
				hiloCmd.generarMensaje(CmdServer.BIEN, CmdServer.UPDATE, CmdServer.CLIENTE, number, 0);
				hiloCmd.writeLog("Se ha actualizado correctamente el cliente");

				break;
			case CmdServer.UPDATE + CmdServer.PRODUCTO:

				getProducto();// le envia el cliente al usuario para que pueda visualizar los datos antes de
								// modificarlos
				tmpProducto = null;
				tmpProducto = (Producto) this.leerObjeto();
				if (tmpProducto == null) {
					hiloCmd.generarMensaje(CmdServer.MAL, CmdServer.UPDATE, CmdServer.PRODUCTO, number, 0);
					hiloCmd.writeLog("ERROR: No se ha leido ningun objeto");
					break;
				}
				if (!Server.sede.updateProducto(id, tmpProducto)) {
					hiloCmd.generarMensaje(CmdServer.MAL, CmdServer.UPDATE, CmdServer.PRODUCTO, number, 0);
					hiloCmd.writeLog("ERROR: El Producto no se ha podido actualizar correctamente");
					break;
				}
				hiloCmd.generarMensaje(CmdServer.BIEN, CmdServer.UPDATE, CmdServer.PRODUCTO, number, 0);
				hiloCmd.writeLog("Se ha actualizado correctamente el producto");

				break;
			case CmdServer.GET + CmdServer.CLIENTE:
				getCliente();
				break;
			case CmdServer.GET + CmdServer.PRODUCTO:
				getProducto();
				break;
			case CmdServer.LIST + CmdServer.CLIENTE:
				Hashtable<Integer, Cliente> clientes = Server.sede.getClientes();
				if (clientes == null) {
					hiloCmd.generarMensaje(CmdServer.MAL, CmdServer.LIST, CmdServer.CLIENTE, number, 0);
					break;
				}
				if (!this.enviarObjeto(clientes)) {
					hiloCmd.generarMensaje(CmdServer.MAL, CmdServer.LIST, CmdServer.CLIENTE, number, 0);
					hiloCmd.writeLog("No se ha enviado correctamente la lista");
					break;
				}
				hiloCmd.generarMensaje(CmdServer.BIEN, CmdServer.LIST, CmdServer.CLIENTE, number, 0);
				hiloCmd.writeLog("Se ha enviado la lista de los clientes correctamente");
				break;
			case CmdServer.LIST + CmdServer.PRODUCTO:
				Hashtable<Integer, Producto> productos = Server.sede.getProductos();
				if (productos == null) {
					hiloCmd.generarMensaje(CmdServer.MAL, CmdServer.LIST, CmdServer.PRODUCTO, number, 0);
					break;
				}
				if (!this.enviarObjeto(productos)) {
					hiloCmd.generarMensaje(CmdServer.MAL, CmdServer.LIST, CmdServer.PRODUCTO, number, 0);
					hiloCmd.writeLog("No se ha enviado correctamente la lista");
					break;
				}
				hiloCmd.generarMensaje(CmdServer.BIEN, CmdServer.LIST, CmdServer.PRODUCTO, number, 0);
				hiloCmd.writeLog("Se ha enviado la lista de los clientes correctamente");
				break;
			case CmdServer.LIST + CmdServer.USUARIO:
				ArrayList<String> infoConectados = hiloCmd.getInfoConectados();
				if (infoConectados == null) {
					hiloCmd.generarMensaje(CmdServer.MAL, CmdServer.LIST, CmdServer.USUARIO, number, 0);
					break;
				}
				if (!this.enviarObjeto(infoConectados)) {
					hiloCmd.generarMensaje(CmdServer.MAL, CmdServer.LIST, CmdServer.USUARIO, number, 0);
					hiloCmd.writeLog("No se ha enviado correctamente la lista");
					break;
				}
				hiloCmd.generarMensaje(CmdServer.BIEN, CmdServer.LIST, CmdServer.USUARIO, number, 0);
				hiloCmd.writeLog("Se ha enviado la lista de los usuarios correctamente");
				break;
			default:
				hiloCmd.generarMensaje(CmdServer.MAL, CmdServer.INCORRECTO, CmdServer.INCORRECTO, number, 0);
				break;

			}
			this.socket.close();
			this.ss.close();

			Server.puertosEnUso.remove(Server.puertosEnUso.indexOf(puerto));// una vez terminado el envio/recibo, se
																			// elimina el puerto de los puertos en uso
			this.interrupt();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Metodo de gestion de get cliente (como se usa varias veces se ha extraído)
	 */
	private void getCliente() {
		Cliente tmpCliente;
		tmpCliente = Server.sede.getClientes().get(id);

		if (!this.enviarObjeto(tmpCliente)) {
			hiloCmd.generarMensaje(CmdServer.MAL, CmdServer.GET, CmdServer.CLIENTE, number, 0);
			hiloCmd.writeLog("No se ha enviado correctamente el cliente");
			return;
		}
		hiloCmd.generarMensaje(CmdServer.BIEN, CmdServer.GET, CmdServer.CLIENTE, number, 0);
		hiloCmd.writeLog("Se ha enviado el cliente correctamente");
		return;
	}

	/**
	 * Metodo de gestion de get Producto (como se usa varias veces se ha extraído)
	 */
	private void getProducto() {
		Producto tmpProducto;
		tmpProducto = Server.sede.getProductos().get(id);

		if (!this.enviarObjeto(tmpProducto)) {
			hiloCmd.generarMensaje(CmdServer.MAL, CmdServer.GET, CmdServer.PRODUCTO, number, 0);
			hiloCmd.writeLog("No se ha enviado correctamente el producto");
			return;
		}
		hiloCmd.generarMensaje(CmdServer.BIEN, CmdServer.GET, CmdServer.PRODUCTO, number, 0);
		hiloCmd.writeLog("Se ha enviado el producto correctamente");
		return;
	}

}
