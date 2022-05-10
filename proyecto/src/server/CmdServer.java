package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Period;
import java.util.ArrayList;
import java.io.File;
import java.io.FileWriter;

import clasesdedatos.Cliente;
import clasesdedatos.Usuario;

public class CmdServer extends Thread {

	public static final int TIMEOUT = 30 * 1000; // timeout por defecto para el keep alive (30 segundos)
	public static final String MAL = "4"; // primer digito de la respuesta de comandos (mala ejecucion)
	public static final String PREOK = "3"; // primer digito de la respuesta de comandos (preok)
	public static final String BIEN = "2"; // primer digito de la respuesta de comandos (buena ejecucion)
	// segunda parte del codigo de respuesta (se usa principalmente para manejar el
	// canal de datos en funcion del comando)
	public static final String USER = "0";
	public static final String PASS = "1";
	public static final String ADD = "2";
	public static final String GET = "3";
	public static final String REMOVE = "4";
	public static final String LIST = "5";
	public static final String COUNT = "6";
	public static final String LOAD = "7";
	public static final String SAVE = "8";
	public static final String UPDATE = "9";
	public static final String ACCT = "a";

	// tercera parte del codigo de respuesta, se usa principalente para saber que
	// clase hay que enviar o recibir
	public static final String INCORRECTO = "5";
	public static final String NONEED = "0"; // en caso de que no haya que enviar o recibir nada con el canal de datos
	public static final String CLIENTE = "1";
	public static final String USUARIO = "2";
	public static final String PRODUCTO = "3";

	private Server server;
	private Socket socket;
	private BufferedReader bufferedReader;
	private PrintWriter printWriter;
	private Usuario usuario; // guardamos el usuario que está ejecutando este hilo
	private File ficheroLog;
	private FileWriter log;
	private LocalDateTime horaConexion;

	/**
	 * Constructor del hilo de comandos del server
	 * 
	 * @param socket socket creado tras el accept mediante el cual se va a hacer el
	 *               intercambio de comandos y respuestas
	 */
	public CmdServer(Socket socket, Server server) {

		this.socket = socket;
		this.server = server;
		try {
			this.bufferedReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
			this.printWriter = new PrintWriter(new OutputStreamWriter(this.socket.getOutputStream()));
			ficheroLog = new File("log.log");
			horaConexion = LocalDateTime.now();
		} catch (IOException e) {

			e.printStackTrace();
			;
		}
		usuario = null;

	}

	/**
	 * Metodo que calcula el tiempo que lleva conectao el usuario
	 * 
	 * @return el tiempo con formato "H horas M minutos S segundos"
	 */
	private String getTiempoConexion() {

		Duration duration = Duration.between(horaConexion, LocalDateTime.now());

		long segundos = duration.getSeconds();

		long horas = segundos / (60 * 60);
		long minutos = ((segundos) / 60);
		long secs = (segundos % 60);
		String tiempo = horas + " horas " + minutos + " minutos " + secs + " segundos";
		return tiempo;
	}

	@Override
	public String toString() {
		String datos;
		datos = "\nTiempo conectado : " + this.getTiempoConexion() + "\n";
		datos += "Usuario Conectado: \n" + usuario.toString();
		return datos;
	}

	/**
	 * Metodo que sirve para comprobar que el usuario sigue activo
	 * 
	 * @return true si el usuario responde, false si no
	 */
	public boolean keepAlive() {
		try {
			this.enviarMensaje("KEEPALIVE");
			this.socket.setSoTimeout(TIMEOUT);
			String string = this.bufferedReader.readLine();
			String comprobacion = string.split(" ")[1];
			if ("OK".equals(comprobacion))
				return true;

		} catch (Exception e) {

			this.enviarMensaje("se va a cerrar todo");
			writeLog(e.getMessage());
		}
		return false;
	}

	/**
	 * Metodo para encontrar un puerto libre que asignar al canal de datos
	 * 
	 * @return el primer puerto libre que encuentra
	 */
	public int encontrarPuertoLibre() {
		int puerto = 2023;
		while (true) {
			if (!Server.puertosEnUso.contains(puerto)) {
				break;
			}
			puerto++;
		}
		Server.puertosEnUso.add(puerto);
		return puerto;
	}

	/**
	 * Metodo para escribir en el log
	 */
	public void writeLog(String string) {
		try {
			log = new FileWriter(ficheroLog, true);
			log.write(LocalDateTime.now() + "\t: " + string + "\n");
			log.close();
		} catch (IOException e) {

		}

	}

	/**
	 * metodo para enviar un mensaje por el socket
	 * 
	 * @param str mensaje a enviar
	 */
	public void enviarMensaje(String str) {
		printWriter.println(str);
		printWriter.flush();
	}

	/**
	 * Metodo para generar los mensajes y los codigos
	 * 
	 * @param resultado digito que corresponde al resultado de la operacion
	 * @param comando   digito que corresponde al comando ejecutado
	 * @param clase     digito que corresponde a la clase que se requiere
	 * @param number    numero del comando enviado por el cliente
	 */

	public void generarMensaje(String resultado, String comando, String clase, String number, int puertoCanalDatos) {

		String codigo = resultado + comando + clase;
		switch (codigo) {

		case BIEN + USER + NONEED:
			this.enviarMensaje("OK " + number + " " + codigo + " ENVIE CONTRASEÑA");
			break;
		case MAL + USER + NONEED:
			this.enviarMensaje("FAILED " + number + " " + codigo + " NOT A USER");
			break;

		case BIEN + PASS + NONEED:
			this.enviarMensaje("OK " + number + " " + codigo + " WELCOME: " + usuario.getUser());
			break;
		case MAL + PASS + NONEED:
			this.enviarMensaje("FAILED " + number + " " + codigo + " PRUEBA DE NUEVO");
			break;

		case MAL + REMOVE + CLIENTE:
			this.enviarMensaje("FAILED " + number + " " + codigo + " ERROR AL ELIMINAR CLIENTE");
			break;
		case BIEN + REMOVE + CLIENTE:
			this.enviarMensaje("OK " + number + " " + codigo + " CLIENTE ELIMINADO CON EXITO");
			break;

		case MAL + REMOVE + PRODUCTO:
			this.enviarMensaje("FAILED " + number + " " + codigo + " ERROR AL ELIMINAR PRODUCTO");
			break;
		case BIEN + REMOVE + PRODUCTO:
			this.enviarMensaje("OK " + number + " " + codigo + " PRODUCTO ELIMINADO CON EXITO");
			break;

		case MAL + GET + CLIENTE:
			this.enviarMensaje("FAILED " + number + " " + codigo + " ERROR AL OBTENER CLIENTE");
			break;
		case MAL + GET + PRODUCTO:
			this.enviarMensaje("FAILED " + number + " " + codigo + " ERROR AL OBTENER PRODUCTO");
			break;
		case MAL + LIST + CLIENTE:
			this.enviarMensaje("FAILED " + number + " " + codigo + " ERROR AL LISTAR CLIENTES");
			break;
		case MAL + LIST + PRODUCTO:
			this.enviarMensaje("FAILED " + number + " " + codigo + " ERROR AL LISTAR PRODUCTO");
			break;
		case MAL + LIST + USUARIO:
			this.enviarMensaje("FAILED " + number + " " + codigo + " ERROR AL LISTAR USUARIOS CONECTADOS");
			break;
		case MAL + ADD + CLIENTE:
			this.enviarMensaje("FAILED " + number + " " + codigo + " ERROR AL AÑADIR EL CLIENTE");
			break;
		case MAL + ADD + PRODUCTO:
			this.enviarMensaje("FAILED " + number + " " + codigo + " ERROR AL AÑADIR EL PRODUCTO");
			break;
		case MAL + UPDATE + CLIENTE:
			this.enviarMensaje("FAILED " + number + " " + codigo + " ERROR AL MODIFICAR EL CLIENTE");
			break;
		case MAL + COUNT + CLIENTE:
			this.enviarMensaje("FAILED " + number + " " + codigo + " ERROR AL CONTAR LOS CLIENTES");
			break;
		case MAL + UPDATE + PRODUCTO:
			this.enviarMensaje("FAILED " + number + " " + codigo + " ERROR AL MODIFICAR EL PRODUCTO");
			break;
		case MAL + COUNT + PRODUCTO:
			this.enviarMensaje("FAILED " + number + " " + codigo + " ERROR AL CONTAR LOS PRODUCTO");
			break;
		case BIEN + COUNT + CLIENTE:
			this.enviarMensaje("OK " + number + " " + codigo + " " + Server.sede.contarClientes());
			break;
		case BIEN + COUNT + PRODUCTO:
			this.enviarMensaje("OK " + number + " " + codigo + " " + Server.sede.contarProductos());
			break;

		case BIEN + ADD + PRODUCTO:
		case BIEN + GET + PRODUCTO:
		case BIEN + LIST + PRODUCTO:
		case BIEN + UPDATE + PRODUCTO:
		case BIEN + GET + CLIENTE:
		case BIEN + UPDATE + CLIENTE:
		case BIEN + LIST + USUARIO:
		case BIEN + ADD + CLIENTE:
		case BIEN + LIST + CLIENTE:
			this.enviarMensaje("OK " + number + " " + codigo + " TRANSFERENCIA TERMINADA");
			break;

		case PREOK + GET + PRODUCTO:
		case PREOK + UPDATE + PRODUCTO:
		case PREOK + LIST + PRODUCTO:
		case PREOK + ADD + PRODUCTO:
		case PREOK + GET + CLIENTE:
		case PREOK + UPDATE + CLIENTE:
		case PREOK + LIST + USUARIO:
		case PREOK + LIST + CLIENTE:
		case PREOK + ADD + CLIENTE:
			this.enviarMensaje("PREOK " + number + " " + codigo + " localhost " + puertoCanalDatos);
			break;

		case BIEN + LOAD + NONEED:
			this.enviarMensaje("OK " + number + " " + codigo + " Base de datos cargada correctamente");
			break;
		case BIEN + SAVE + NONEED:
			this.enviarMensaje("OK " + number + " " + codigo + " Base de datos guardada correctamente");
			break;
		case MAL + LOAD + NONEED:
			this.enviarMensaje("FAILED " + number + " " + codigo + " No se ha podido cargar la base de datos");
			break;
		case MAL + SAVE + NONEED:
			this.enviarMensaje("FAILED " + number + " " + codigo + " No se ha podido guardar la base de datos");
			break;

		case MAL + INCORRECTO + INCORRECTO:
			this.enviarMensaje("FAILED COMANDO NO VALIDO");
			break;
		default:
			this.enviarMensaje("ERROR DE RESPUESTA");
			break;
		}
	}

	public void run() {

		String userName = null;

		String lineaLeida = null;
		String comando = null;
		String number = null;
		Cliente tmpCliente = null;

		do {

			try {

				this.socket.setSoTimeout(TIMEOUT);
				lineaLeida = bufferedReader.readLine();
				if (lineaLeida.isEmpty()) {
					this.generarMensaje(MAL, INCORRECTO, INCORRECTO, number, 0);
					continue;
				}
				String split[] = lineaLeida.split(" ");
				comando = split[1]; // separa el comando y el numero de la linea leida
				number = split[0];
				writeLog("Se ha introducido el comando: " + lineaLeida);

				if ((!"USER".equals(comando) && !"PASS".equals(comando)) && !"ACCT".equals(comando)
						&& usuario == null) {
					this.enviarMensaje("FAILED: NO CONECTADO");
					continue;
				}

				switch (comando) {
				case "ACCT":
					usuario = null; // al introducir el comando ACCT elimina el anterior usuario (si es que hubiera
									// uno)

					if (split.length < 4) {
						this.generarMensaje(MAL, ACCT, NONEED, number, 0);
						writeLog("ERROR: Comando ACCT Intoducido Incorrectamente");
						break;
					}
					userName = comandoUser(number, split);
					split[2] = split[3];
					comandoPass(number, split, userName);
					break;
				case "USER":
					usuario = null; // al introducir el comando user elimina el anterior usuario (si es que hubiera
									// uno)
					userName = comandoUser(number, split);
					break;
				case "PASS":
					if (userName == null) {
						this.generarMensaje(MAL, PASS, NONEED, number, 0);
						writeLog("WARNING: Contraseña introducida antes que el usuario");
						break;
					}
					comandoPass(number, split, userName);
					break;
				case "ADDCLIENTE":
					comandoAddCliente(number, split);
					break;
				case "ADDPRODUCTO":
					comandoAddProducto(number, split);

					break;
				case "UPDATECLIENTE":
					comandoUpdateCliente(number, split);
					break;
				case "UPDATEPRODUCTO":
					comandoUpdateProducto(number, split);
					break;
				case "GETCLIENTE":
					comandoGetCliente(number, split);
					break;
				case "GETPRODUCTO":
					comandoGetProducto(number, split);
					break;
				case "REMOVECLIENTE":
					comandoRemoveCLiente(number, split);
					break;
				case "REMOVEPRODUCTO":
					comandoRemoveProducto(number, split);
					break;
				case "LISTCLIENTES":
					comandoListClientes(number, split);
					break;
				case "LISTPRODUCTOS":
					comandoListProductos(number, split);
					break;
				case "COUNTCLIENTES":
					this.generarMensaje(BIEN, COUNT, CLIENTE, number, 0);
					break;
				case "COUNTPRODUCTOS":
					this.generarMensaje(BIEN, COUNT, PRODUCTO, number, 0);
					break;
				case "CONNECTED":
					comandoConnected(number, split);
					break;
				case "LOAD":
					comandoLoad(number);
					break;
				case "SAVE":
					comandoSave(number);
					break;
				case "EXIT":
				case "KEEPME":
					break;
				default:
					this.generarMensaje(MAL, INCORRECTO, INCORRECTO, number, 0);
					break;
				}

			} catch (SocketTimeoutException e) {
				writeLog("Se ha ejecutado un KEEPALIVE");
				if (!keepAlive()) {
					writeLog("No está activo");
					break;
				}
				continue; // no tengo claro si SocketTimeoutException es un tipo de IOException por lo que
							// por si acaso, continuamos con el bucle
			} catch (IOException e) {

				// si el problema es un IOException (excepcion en el readLine)
				this.enviarMensaje("FAILED ERROR EN EL SERVIDOR");
				writeLog("SEVERE ERROR: HA OCURRIDO UN ERROR EN EL SERVIDOR");
				writeLog(e.getMessage());
				break;

			} catch (NullPointerException e) {
				this.enviarMensaje("FAILED ERROR EN EL SERVIDOR");
				writeLog("SEVERE ERROR: HA OCURRIDO UN ERROR EN EL SERVIDOR");
				writeLog(e.getMessage());
				break;
			}

		} while (!"EXIT".equals(comando));
		this.enviarMensaje("EXIT : SALIENDO DEL SERVIDOR");
		writeLog("Se ha cerrado la conexion");

		this.enviarMensaje("Gracias por su visita");
		this.server.getConectados().remove(this);
		try {
			socket.close();
		} catch (IOException e) {

			writeLog(e.getMessage());
		}
		this.interrupt();
	}

	/**
	 * A partir de aqui está la gestión de los comandos ejecutados extraida a
	 * metodos para facilitar cambios Tambien es para que el switch no quede tan
	 * cargado de codigo y se pueda ver mas facilmente lo que hace cada comando
	 */

	private void comandoRemoveCLiente(String number, String[] split) {
		if (split.length < 3) {
			this.generarMensaje(MAL, REMOVE, CLIENTE, number, 0);
			writeLog("ERROR: Comando REMOVECLIENTE introducido Incorrectamente");
			return;
		}
		if (!Server.sede.removeCliente(Integer.parseInt(split[2]))) {
			this.generarMensaje(MAL, REMOVE, CLIENTE, number, 0);
			writeLog("No se ha podido borrar el cliente");
			return;
		}
		this.generarMensaje(BIEN, REMOVE, CLIENTE, number, 0);
		writeLog("Se ha elimindao el cliente correctamente");
		return;

	}

	private void comandoRemoveProducto(String number, String[] split) {
		if (split.length < 3) {
			this.generarMensaje(MAL, REMOVE, PRODUCTO, number, 0);
			writeLog("ERROR: Comando REMOVEPRODUCTO introducido Incorrectamente");
			return;
		}
		if (!Server.sede.removeProducto(Integer.parseInt(split[2]))) {
			this.generarMensaje(MAL, REMOVE, PRODUCTO, number, 0);
			writeLog("No se ha podido borrar el producto");
			return;
		}
		this.generarMensaje(BIEN, REMOVE, PRODUCTO, number, 0);
		writeLog("Se ha elimindao el producto correctamente");
		return;

	}

	private void comandoGetCliente(String number, String[] split) throws IOException {
		if (split.length < 3) {
			this.generarMensaje(MAL, GET, CLIENTE, number, 0);
			writeLog("ERROR: Comando GETCLIENTE Intoducido Incorrectamente");
			return;
		}
		int puerto = this.encontrarPuertoLibre();
		int id = Integer.parseInt(split[2]);
		writeLog("Se va a abrir el canal de datos en el puerto " + puerto);
		new HiloCanalDatosServer(puerto, GET + CLIENTE, number, this, id);

		this.generarMensaje(PREOK, GET, CLIENTE, number, puerto);
		writeLog("Se ha lanzado un preok para obtener el cliente");
		if ("NOT".equals(bufferedReader.readLine())) {
			this.generarMensaje(MAL, GET, CLIENTE, number, 0);
			writeLog("ERROR : No se ha conectado correctamente el canal de datos");
			return;
		}
		// el mensaje de confirmacion lo imprime "desde" el canal de datos usando este
		// hilo
		return;

	}

	private void comandoGetProducto(String number, String[] split) throws IOException {
		if (split.length < 3) {
			this.generarMensaje(MAL, GET, PRODUCTO, number, 0);
			writeLog("ERROR: Comando GETPRODUCTO Intoducido Incorrectamente");
			return;
		}
		int puerto = this.encontrarPuertoLibre();
		int id = Integer.parseInt(split[2]);
		writeLog("Se va a abrir el canal de datos en el puerto " + puerto);
		new HiloCanalDatosServer(puerto, GET + PRODUCTO, number, this, id);

		this.generarMensaje(PREOK, GET, PRODUCTO, number, puerto);
		writeLog("Se ha lanzado un preok para obtener el producto");
		if ("NOT".equals(bufferedReader.readLine())) {
			this.generarMensaje(MAL, GET, PRODUCTO, number, 0);
			writeLog("ERROR : No se ha conectado correctamente el canal de datos");
			return;
		}
		// el mensaje de confirmacion lo imprime "desde" el canal de datos usando este
		// hilo
		return;

	}

	/**
	 * gestion del comando user
	 * 
	 * @param number numero de la ejecucion del cliente
	 * @param split  Split es la linea enviada por el cliente separada por espacios
	 * @return nombre del usuario en caso de ser introducido correctamente
	 */
	private String comandoUser(String number, String[] split) {
		if (split.length < 3) {
			this.generarMensaje(MAL, USER, NONEED, number, 0);
			writeLog("ERROR: Comando USER introducido incorrectamente");
			return null;
		}
		String userName;
		userName = split[2];
		if (!Server.sede.buscarUser(userName)) {
			this.generarMensaje(MAL, USER, NONEED, number, 0);
			writeLog("ERROR: Usuario no encontrado");
			return null;
		}

		this.generarMensaje(BIEN, USER, NONEED, number, 0);
		writeLog("Usuario encontrado correctamente " + userName);
		return userName;
	}

	/**
	 * Metodo que gestiona el comando Pass
	 * 
	 * @param number   numero de la ejecucion del cliente
	 * @param split    Split es la linea enviada por el cliente separada por
	 *                 espacios
	 * @param userName Nombre del usuario introducido anteriormente
	 */
	private void comandoPass(String number, String[] split, String userName) {
		if (split.length < 3) { //
			this.generarMensaje(MAL, PASS, NONEED, number, 0);
			writeLog("ERROR: Comando PASS Intoducido Incorrectamente");
			return;
		}
		String password = split[2];
		usuario = Server.sede.inicioSesion(userName, password);

		if (usuario == null) {
			this.generarMensaje(MAL, PASS, NONEED, number, 0);
			writeLog("ERROR: La contraseña no coincide con el usuario");
			return;

		}
		this.generarMensaje(BIEN, PASS, NONEED, number, 0);

		File ficheroUsuario = new File(userName + ".log"); // se cambia de nombre al fichero de log
		ficheroLog.renameTo(ficheroUsuario);
		ficheroLog = ficheroUsuario;
		writeLog("Se ha iniciado sesion correctamente");
		return;
	}

	/**
	 * Metodo que gestiona el comando ADDCLIENTE
	 * 
	 * @param number numero de la ejecucion del cliente
	 * @param split  Split es la linea enviada por el cliente separada por espacios
	 * @throws IOException
	 */
	private void comandoAddCliente(String number, String[] split) throws IOException {

		int puerto = this.encontrarPuertoLibre();
		writeLog("Se va a abrir el canal de datos " + puerto);
		new HiloCanalDatosServer(puerto, ADD + CLIENTE, number, this); // dentro del constructor del hilo, se lanza un
																		// start automaticamente

		this.generarMensaje(PREOK, ADD, CLIENTE, number, puerto);
		writeLog("Se ha lanzado un preok para insertar un cliente");

		if ("NOT".equals(bufferedReader.readLine())) {
			this.generarMensaje(MAL, ADD, CLIENTE, number, 0);
			writeLog("ERROR : No se ha conectado correctamente el canal de datos");
			return;
		}
		return;

	}

	/**
	 * Metodo que gestiona el comando ADDPRODUCTO
	 * 
	 * @param number numero de la ejecucion del cliente
	 * @param split  Split es la linea enviada por el cliente separada por espacios
	 * @throws IOException
	 */
	private void comandoAddProducto(String number, String[] split) throws IOException {

		int puerto = this.encontrarPuertoLibre();
		writeLog("Se va a abrir el canal de datos " + puerto);
		new HiloCanalDatosServer(puerto, ADD + PRODUCTO, number, this); // dentro del constructor del hilo, se lanza un
																		// start automaticamente

		this.generarMensaje(PREOK, ADD, PRODUCTO, number, puerto);
		writeLog("Se ha lanzado un preok para insertar un producto");

		if ("NOT".equals(bufferedReader.readLine())) {
			this.generarMensaje(MAL, ADD, PRODUCTO, number, 0);
			writeLog("ERROR : No se ha conectado correctamente el canal de datos");
			return;
		}

		// el mensaje de confirmacion lo imprime "desde" el canal de datos usando este
		// hilo
		return;

	}

	private void comandoUpdateCliente(String number, String[] split) throws IOException {
		if (split.length < 3) {
			this.generarMensaje(MAL, UPDATE, CLIENTE, number, 0);
			writeLog("ERROR: Comando UPDATECLIENTE Intoducido Incorrectamente");
			return;
		}
		int id = Integer.parseInt(split[2]);
		int puerto = this.encontrarPuertoLibre();
		writeLog("Se va a abrir el canal de datos " + puerto);
		new HiloCanalDatosServer(puerto, UPDATE + CLIENTE, number, this, id); // dentro del constructor del hilo, se
																				// lanza un start automaticamente

		this.generarMensaje(PREOK, UPDATE, CLIENTE, number, puerto);
		writeLog("Se ha lanzado un preok para actualizar un cliente");

		if ("NOT".equals(bufferedReader.readLine())) {
			this.generarMensaje(MAL, UPDATE, CLIENTE, number, 0);
			writeLog("ERROR : No se ha conectado correctamente el canal de datos");
			return;
		}

		// el mensaje de confirmacion lo imprime "desde" el canal de datos usando este
		// hilo
		return;
	}

	private void comandoUpdateProducto(String number, String[] split) throws IOException {
		if (split.length < 3) {
			this.generarMensaje(MAL, UPDATE, PRODUCTO, number, 0);
			writeLog("ERROR: Comando UPDATEPRODUTO Intoducido Incorrectamente");
			return;
		}
		int id = Integer.parseInt(split[2]);
		int puerto = this.encontrarPuertoLibre();
		writeLog("Se va a abrir el canal de datos " + puerto);
		new HiloCanalDatosServer(puerto, UPDATE + PRODUCTO, number, this, id); // dentro del constructor del hilo, se
																				// lanza un start automaticamente

		this.generarMensaje(PREOK, UPDATE, PRODUCTO, number, puerto);
		writeLog("Se ha lanzado un preok para actualizar un producto");

		if ("NOT".equals(bufferedReader.readLine())) {
			this.generarMensaje(MAL, UPDATE, PRODUCTO, number, 0);
			writeLog("ERROR : No se ha conectado correctamente el canal de datos");
			return;
		}

		return;
	}

	/**
	 * Metodo que gestiona el comando listar clientes
	 * 
	 * @param number numero de la ejecucion del cliente
	 * @param split  Split es la linea enviada por el cliente separada por espacios
	 * @throws IOException
	 */
	private void comandoListClientes(String number, String[] split) throws IOException {
		int puerto = this.encontrarPuertoLibre();
		writeLog("Se va a abrir el canal de datos en el puerto " + puerto);
		new HiloCanalDatosServer(puerto, LIST + CLIENTE, number, this);

		this.generarMensaje(PREOK, LIST, CLIENTE, number, puerto);
		writeLog("Se ha lanzado un preok para listar los clientes");
		if ("NOT".equals(bufferedReader.readLine())) {
			this.generarMensaje(MAL, LIST, CLIENTE, number, 0);
			writeLog("ERROR : No se ha conectado correctamente el canal de datos");
			return;
		}
		// el mensaje de confirmacion lo imprime "desde" el canal de datos usando este
		// hilo
		return;
	}

	/**
	 * Metodo que gestiona el comando listar productos
	 * 
	 * @param number numero de la ejecucion del cliente
	 * @param split  Split es la linea enviada por el cliente separada por espacios
	 * @throws IOException
	 */
	private void comandoListProductos(String number, String[] split) throws IOException {
		int puerto = this.encontrarPuertoLibre();
		writeLog("Se va a abrir el canal de datos en el puerto " + puerto);
		new HiloCanalDatosServer(puerto, LIST + PRODUCTO, number, this);

		this.generarMensaje(PREOK, LIST, PRODUCTO, number, puerto);
		writeLog("Se ha lanzado un preok para listar los clientes");
		if ("NOT".equals(bufferedReader.readLine())) {
			this.generarMensaje(MAL, LIST, PRODUCTO, number, 0);
			writeLog("ERROR : No se ha conectado correctamente el canal de datos");
			return;
		}
		// el mensaje de confirmacion lo imprime "desde" el canal de datos usando este
		// hilo
		return;
	}

	/**
	 * Metodo que devuelve la informacion de los hilos conectados en forma de un
	 * array de strings
	 * 
	 * @return
	 */
	public ArrayList<String> getInfoConectados() {
		ArrayList<String> infoConectados = new ArrayList<String>();
		infoConectados.add("Hay " + server.getConectados().size() + " usuarios Conectados");
		for (CmdServer s : server.getConectados()) {
			infoConectados.add(s.toString());
		}
		return infoConectados;
	}

	/**
	 * Metodo que gestiona el comando connected
	 * 
	 * @param number numero de la ejecucion del cliente
	 * @param split  Split es la linea enviada por el cliente separada por espacios
	 * @throws IOException
	 */
	private void comandoConnected(String number, String[] split) throws IOException {

		int puerto = this.encontrarPuertoLibre();
		writeLog("Se va a abrir el canal de datos en el puerto " + puerto);
		new HiloCanalDatosServer(puerto, LIST + USUARIO, number, this);

		this.generarMensaje(PREOK, LIST, USUARIO, number, puerto);
		writeLog("Se ha lanzado un preok para listar los usuarios conectados");
		if ("NOT".equals(bufferedReader.readLine())) {
			this.generarMensaje(MAL, LIST, USUARIO, number, 0);
			writeLog("ERROR : No se ha conectado correctamente el canal de datos");
			return;
		}
		// el mensaje de confirmacion lo imprime "desde" el canal de datos usando este
		// hilo
		return;

	}

	/**
	 * Metodo que gestiona el comando LOAD (carga la base de datos)
	 * 
	 * @param number numero de la ejecucion del cliente
	 */
	private void comandoLoad(String number) {
		if (!Server.sede.cargarBaseDatos()) {
			this.generarMensaje(MAL, LOAD, NONEED, number, 0);
			writeLog("ERROR al cargar la base de datos");
			return;
		}
		this.generarMensaje(BIEN, LOAD, NONEED, number, 0);
		writeLog("Se ha cargado la base de datos");
		return;
	}

	/**
	 * Metodo que gestiona el comando Save (guardar la base de datos)
	 * 
	 * @param number numero de la ejecucion del cliente
	 */
	private void comandoSave(String number) {
		if (!Server.sede.guardarBaseDatos()) {
			this.generarMensaje(MAL, SAVE, NONEED, number, 0);
			writeLog("ERROR al guardar la base de datos");
			return;
		}
		this.generarMensaje(BIEN, SAVE, NONEED, number, 0);
		writeLog("Se ha guardado la base de datos");
		return;
	}
}
