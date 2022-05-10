package sede;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.Hashtable;
import java.util.Map.Entry;
import clasesdedatos.*;
import java.util.Scanner;

public class Sede {

	private Hashtable<String, Usuario> usuarios;
	private Hashtable<Integer, Cliente> clientes;
	private Hashtable<Integer, Producto> productos;

	/**
	 * Metodo que te muestra el menu con lo que puedes hacer en la sede de forma
	 * local
	 * 
	 * @deprecated
	 */
	public void menuSede() {
		System.out.println("\nQUE DESEA HACER?");
		System.out.println("1- Añadir usuario");
		System.out.println("2- Mostrar usuarios");
		System.out.println("3- Borrar usuario");
		System.out.println("4- Modificar usuario");
		System.out.println("5- Añadir cliente");
		System.out.println("6- Mostrar clientes");
		System.out.println("7- Borrar cliente");
		System.out.println("8- Modificar cliente");
		System.out.println("9- Añadir producto");
		System.out.println("10- Mostrar productos");
		System.out.println("11- Borrar producto");
		System.out.println("12- Modificar producto");
		System.out.println("13- Cargar Base de datos");
		System.out.println("14- Guardar Base de datos");
		System.out.println("15- Salir\n");
	}

	/**
	 * Constructor de clase sede (carga la base de datos automaticamente)
	 */
	public Sede() {

		this.usuarios = new Hashtable<String, Usuario>();
		this.clientes = new Hashtable<Integer, Cliente>();
		this.productos = new Hashtable<Integer, Producto>();
		// this.guardarBaseDatos();
		if (!this.cargarBaseDatos())
			System.out.println("ERRROR CARGANDO LA BASE DE DATOS");
		Usuario admin = new Usuario("admin", "admin");
		this.addUser(admin);
	}

	/**
	 * Metodo main de la clase sede, se usa para efectuar pruebas locales (sin
	 * servidor)
	 * 
	 * @param args
	 * @deprecated
	 */
	public static void main(String[] args) {
		Sede sede = new Sede();
		Scanner sc = new Scanner(System.in);
		String respuesta = null;
		Usuario admin = new Usuario("admin", "admin");
		sede.addUser(admin);

		do {
			respuesta = funcionalidadSede(sede, sc);
		} while (!"15".equals(respuesta));
		System.out.println("UN PLACER ATENDERTE");

	}

	/**
	 * Metodo que recoge las funcionalidades de la sede
	 * 
	 * @param sede sede sobre la cual efectuar los cambios
	 * @param sc   scanner conn el que leer la respuesta del usuario
	 * @return respuesta del usuario
	 * @deprecated
	 */
	public static String funcionalidadSede(Sede sede, Scanner sc) {
		String respuesta;
		String nombre;
		String apellido;
		String pass;
		float precio;
		int id;
		Usuario tmpUser;
		Cliente tmpCliente;
		Producto tmpProducto;
		sede.menuSede();
		respuesta = sc.nextLine();
		switch (respuesta) {

		case "1":

			System.out.println("SE VA A PROCEDER A AÑADIR UN USUARIO");
			System.out.println("Introduce nombre:");
			nombre = sc.nextLine();
			System.out.println("Introduce contraseña:");
			pass = sc.nextLine();
			tmpUser = new Usuario(nombre, pass);
			if (sede.addUser(tmpUser))
				System.out.println("Usuario añadido correctamente");
			else
				System.out.println("Usuario ya existente");
			break;
		case "2":
			System.out.println("SE VA A PROCEDER A MOSTRAR LOS USUARIOS");
			sede.mostrarUsuarios();
			break;
		case "3":
			System.out.println("SE VA A PROCEDER A BORRAR UN USUARIO");
			System.out.println("Introduce el nombre del usuario que quieres borrar");
			nombre = sc.nextLine();
			if (sede.removeUsuario(nombre))
				System.out.println("Usuario borrado correctamente");
			else
				System.out.println("No se ha encontrado el usuario");
			break;
		case "4":
			System.out.println("SE VA A PROCEDER A MODIFICAR UN USUARIO");
			System.out.println("Introduce el nombre del usuario que quieres buscar");
			nombre = sc.nextLine();

			if (!sede.buscarUser(nombre)) {
				System.out.println("Usuario no encontrado");
				break;
			}
			sede.removeUsuario(nombre);

			System.out.println("Introduce nuevo nombre:");
			String nuevoNombre = sc.nextLine();
			System.out.println("Introduce nueva contraseña:");
			pass = sc.nextLine();

			tmpUser = new Usuario(nuevoNombre, pass);
			if (sede.addUser(tmpUser))
				System.out.println("Usuario añadido correctamente");
			else
				System.out.println("Usuario ya existente");

			break;
		case "5":
			System.out.println("SE VA A PROCEDER A AÑADIR UN CLIENTE");
			System.out.println("Introduce el nombre del cliente");
			nombre = sc.nextLine();
			System.out.println("Introduce el apellido del cliente");
			apellido = sc.nextLine();
			tmpCliente = new Cliente(nombre, apellido);

			if (!sede.addCliente(tmpCliente))
				System.out.println("No se puede añadir el cliente");
			else
				System.out.println("Cliente añadido correctamente");
			break;
		case "6":
			System.out.println("SE VA A PROCEDER A MOSTRAR LOS CLIENTES");
			sede.mostrarClientes();
			break;
		case "7":
			System.out.println("SE VA A PROCEDER A BORRAR UN CLIENTE");
			System.out.println("Introduce el Id del cliente");
			id = Integer.parseInt(sc.nextLine());

			if (sede.removeCliente(id))
				System.out.println("Cliente borrado correctamente");
			else
				System.out.println("Cliente no encontrado");
			break;
		case "8":
			System.out.println("SE VA A PROCEDER A MODIFICAR UN CLIENTE");
			System.out.println("Introduce el Id del cliente");
			id = Integer.parseInt(sc.nextLine());
			if (!sede.buscarCliente(id)) {
				System.out.println("Cliente no encontrado");
				break;
			}
			System.out.println("Introduce el nuevo nombre del cliente");
			nombre = sc.nextLine();
			System.out.println("Introduce el nuevo apellido del cliente");
			apellido = sc.nextLine();
			tmpCliente = new Cliente(nombre, apellido);
			sede.clientes.replace(id, tmpCliente);
			System.out.println("Cliente modificado con exito");
			break;

		case "9":
			System.out.println("SE VA A PROCEDER A AÑADIR UN PRODUCTO");
			System.out.println("Introduce el nombre del producto");
			nombre = sc.nextLine();
			System.out.println("Introduce el precio del producto");
			precio = Float.parseFloat(sc.nextLine());
			tmpProducto = new Producto(nombre, precio);

			if (!sede.addProducto(tmpProducto))
				System.out.println("No se puede añadir el producto");
			else
				System.out.println("Procuto añadido correctamente");
			break;
		case "10":
			System.out.println("SE VA A PROCEDER A MOSTRAR LOS PRODUCTOS");
			sede.mostrarProductos();
			break;
		case "11":
			System.out.println("SE VA A PROCEDER A BORRAR UN PRODUCTO");
			System.out.println("Introduce el Id del producto");
			id = Integer.parseInt(sc.nextLine());

			if (sede.removeProducto(id))
				System.out.println("Producto borrado correctamente");
			else
				System.out.println("Producto no encontrado");
			break;
		case "12":
			System.out.println("SE VA A PROCEDER A MODIFICAR UN PRODUCTO");
			System.out.println("Introduce el Id del producto");
			id = Integer.parseInt(sc.nextLine());
			if (!sede.buscarProducto(id)) {
				System.out.println("Producto no encontrado");
				break;
			}
			System.out.println("Introduce el nombre del producto");
			nombre = sc.nextLine();
			System.out.println("Introduce el precio del producto");
			precio = Float.parseFloat(sc.nextLine());
			tmpProducto = new Producto(nombre, precio);

			sede.productos.replace(id, tmpProducto);
			System.out.println("Cliente modificado con exito");
			break;
		case "13":
			System.out.println("SE VA A PROCEDER A CARGAR LA BASE DE DATOS");
			sede.cargarBaseDatos();
			break;
		case "14":
			System.out.println("SE VA A PROCEDER A GUARDAR LA BASE DE DATOS");
			sede.guardarBaseDatos();
			break;
		case "15":
			System.out.println("SE VA A PROCEDER A SALIR DEL PROGRAMA");
			break;
		default:
			System.out.println("ERROR AL INTRODUCIR COMANDO");
			break;
		}
		return respuesta;
	}

	/**
	 * Metodo para añadir un usuario a la base de datos
	 * 
	 * @param user usuario que hay que añadir en la base de datos
	 * @return false si no es capaz de añadirlo, true si lo hace sin problema
	 */
	public boolean addUser(Usuario user) {
		if (this.usuarios.containsKey(user.getUser()))
			return false; // si el hashtable contine el nombre del usuario como llave devuelve false
		this.usuarios.put(user.getUser(), user); // añade el usuario al hashtable usando el nombre como clave
		return true; // si todo va bien, devuelve true
	}

	/**
	 * Metodo para buscar un usuario en la base de datos
	 * 
	 * @param name nombre del usuario que quieres buscar
	 * @return true si el usuario existe false si no lo encuentra
	 */
	public boolean buscarUser(String name) {
		return this.usuarios.containsKey(name);
	}

	/**
	 * Metodo para mostrar los datos de todos los usuarios guardados (Metodo de
	 * permisos de admin)
	 */
	public void mostrarUsuarios() {
		for (Usuario user : this.usuarios.values()) {
			System.out.println(user.toString());
		}
	}

	/**
	 * Metodo que elimina un cliente de la base de datos
	 * 
	 * @param nombre nombre del usuario que hay que eliminar
	 * @return false si no es capaz de eliminarlo
	 */
	public boolean removeUsuario(String nombre) {
		if (!this.usuarios.containsKey(nombre))
			return false;
		this.usuarios.remove(nombre);
		return true;
	}

	/**
	 * Metodo que añade un cliente a la base de datos
	 * 
	 * @param cliente cliente a añadir a la base de datos
	 * @return false si ya había un cliente en la posicion en la que se va a añadir
	 *         el cliente (no deberia ocurrir)
	 */
	public boolean addCliente(Cliente cliente) {
		int key = generateClientKey();
		if (this.buscarCliente(key))
			return false;
		clientes.put(key, cliente);
		return true;
	}

	/**
	 * Metodo que añade un producto a la base de datos
	 * 
	 * @param producto producto a añadir a la base de datos
	 * @return false si ya había un producto en la posicion en la que se va a añadir
	 *         el producto (no deberia ocurrir)
	 */
	public boolean addProducto(Producto producto) {
		int key = generateProductKey();
		if (this.buscarProducto(key))
			return false;
		productos.put(key, producto);
		return true;
	}

	/**
	 * Metodo algo pocho para encontrar una clave libre de producto, se ejecuta al
	 * crear un producto
	 * 
	 * @return clave que asignar al nuevo producto
	 */
	public int generateProductKey() {
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			if (!this.buscarProducto(i))
				return i;
		}
		return productos.size() + 1;
	}

	/**
	 * Metodo algo pocho para encontrar una clave libre de cliente, se ejecuta al
	 * crear un cliente
	 * 
	 * @return clave que asignar al nuevo cliente
	 */
	public int generateClientKey() {
		for (int i = 0; i < Integer.MAX_VALUE; i++) {
			if (!this.buscarCliente(i))
				return i;
		}
		return clientes.size() + 1;
	}

	/**
	 * Metodo para mostrar el nombre de todos los clientes guardados
	 */
	public void mostrarClientes() {
		for (Entry<Integer, Cliente> entry : this.clientes.entrySet()) {
			System.out.println(entry.getKey() + " -> " + entry.getValue().toString());
		}
	}

	/**
	 * Metodo para mostrar el nombre de todos los productos guardados
	 */
	public void mostrarProductos() {
		for (Entry<Integer, Producto> entry : this.productos.entrySet()) {
			System.out.println(entry.getKey() + " -> " + entry.getValue().toString());
		}
	}

	/**
	 * metodo que devuelve el numero de clientes existentes en la base de datos
	 * 
	 * @return tamaño del hashmap cliente
	 */
	public int contarClientes() {
		return this.clientes.size();
	}

	/**
	 * metodo que devuelve el numero de productos existentes en la base de datos
	 * 
	 * @return tamaño del hashmap Productos
	 */
	public int contarProductos() {
		return this.productos.size();
	}

	/**
	 * Metodo para actualizar un cliente
	 * 
	 * @param key id del cliente que se quiere actualizar
	 * @param c   nuevo cliente que asignar a esa id
	 * @return
	 */
	public boolean updateCliente(int key, Cliente c) {
		if (!this.buscarCliente(key)) { // si no lo contine
			return false;
		}
		this.clientes.replace(key, c);
		return true;
	}

	/**
	 * Metodo para actualizar un producto
	 * 
	 * @param key id del cliente que se quiere actualizar
	 * @param c   nuevo cliente que asignar a esa id
	 * @return
	 */
	public boolean updateProducto(int key, Producto p) {
		if (!this.buscarCliente(key)) { // si no lo contine
			return false;
		}
		this.productos.replace(key, p);
		return true;
	}

	/**
	 * Metodo para borrar un cliente
	 * 
	 * @param key id del cliente que hay que borrar
	 * @return true si es capaz de borrarlo, false si no lo encuentra
	 */
	public boolean removeCliente(int key) {
		if (!this.buscarCliente(key))
			return false;
		this.clientes.remove(key);
		return true;
	}

	/**
	 * Metodo para borrar un producto
	 * 
	 * @param key id del producto que hay que borrar
	 * @return true si es capaz de borrarlo, false si no lo encuentra
	 */
	public boolean removeProducto(int key) {
		if (!this.buscarProducto(key))
			return false;
		this.productos.remove(key);
		return true;
	}

	/**
	 * Metodo que busca un cliente
	 * 
	 * @param key id del cliente que quieres buscar
	 * @return true si existe, false si no.
	 */
	public boolean buscarCliente(int key) {
		return this.clientes.containsKey(key);
	}

	/**
	 * Metodo que busca un producto
	 * 
	 * @param key id del cliente que quieres buscar
	 * @return true si existe, false si no.
	 */
	public boolean buscarProducto(int key) {
		return this.productos.containsKey(key);
	}

	/**
	 * Metodo para cargar la "Base de Datos" desde varios ficheros
	 * 
	 * @return false si no es capaz de hacerlo, true si es capaz
	 */
	@SuppressWarnings("unchecked")
	public boolean cargarBaseDatos() {
		// System.out.println("vamos a cargar la base de datos");
		try {
			this.usuarios = (Hashtable<String, Usuario>) cargarFichero("usuarios.ser");
			this.clientes = (Hashtable<Integer, Cliente>) cargarFichero("clientes.ser");
			this.productos = (Hashtable<Integer, Producto>) cargarFichero("productos.ser");
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * Metodo para cargar los datos de un fichero
	 * 
	 * @param fichero fichero del que cargar los datos
	 * @return los datos cargados
	 */
	public Object cargarFichero(String fichero) {

		FileInputStream fis;
		Object readObject = new Object();
		try {
			fis = new FileInputStream(fichero);
			ObjectInputStream ois = new ObjectInputStream(fis);
			readObject = ois.readObject();
			ois.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return readObject;
	}

	/**
	 * Metodo para guardar la "Base de datos" en varios ficheros
	 * 
	 * @return false si no ha sido capaz de hacerlo,true si se ha guardado
	 *         correctamente;
	 *
	 */
	public boolean guardarBaseDatos() {
		if (!guardarFichero("usuarios.ser", this.usuarios))
			return false;
		if (!guardarFichero("clientes.ser", this.clientes))
			return false;
		if (!guardarFichero("productos.ser", this.productos))
			return false;
		return true;
	}

	/**
	 * Metodo para guardar datos en un fichero
	 * 
	 * @param fichero fichero destino donde se van a guardar los datos
	 * @param guardar datos que se deben guardar
	 * @return true si ha sido capaz de hacerlo, false si ha habido alguna excepcion
	 */
	public boolean guardarFichero(String fichero, Object guardar) {

		FileOutputStream fop;
		try {
			fop = new FileOutputStream(fichero);
			try (ObjectOutputStream oos = new ObjectOutputStream(fop)) {
				oos.writeObject(guardar);
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	/**
	 * metodo de inicio de sesion
	 * 
	 * @param nombre nombre del usuario que inicia sesion
	 * @param pass   contraseña del usuario que inicia sesion
	 * @return el usuario si el inicio de sesion es correcto
	 */
	public Usuario inicioSesion(String nombre, String pass) {
		if (!this.buscarUser(nombre))
			return null;
		Usuario user = this.usuarios.get(nombre);
		if (user.validatePass(pass))
			return user;
		return null;
	}

	/**
	 * Metodo que devuelve los usuarios que hay guardados en la base de datos
	 * 
	 * @return El Hashtable que contiene todos los users creados
	 */
	public Hashtable<String, Usuario> getUsuarios() {
		return this.usuarios;
	}

	/**
	 * Metodo que devuelve los productos que hay guardados en la base de datos
	 * 
	 * @return El Hashtable que contiene todos los users creados
	 */
	public Hashtable<Integer, Producto> getProductos() {
		return this.productos;
	}

	/**
	 * Metodo que devuelve los clientes almacenados en la Base de datos
	 * 
	 * @return
	 */
	public Hashtable<Integer, Cliente> getClientes() {
		return clientes;
	}

}
