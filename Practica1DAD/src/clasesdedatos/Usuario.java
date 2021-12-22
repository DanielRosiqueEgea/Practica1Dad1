package clasesdedatos;

import java.io.Serializable;
import java.security.NoSuchAlgorithmException;  
import java.security.MessageDigest;  
  


public class Usuario implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String password;
	private String username; //se usará el user como key para el hashtable guardado en la sede
	//private int permit; //para implementar un sistema de permisos  permiso = 1 
	
	/**
	 * Constructor clase Usuario 
	 * @param user nombre del usuario que quieres añadir al sistema
	 * @param pass contraseña del usuario (se almacena cifrada)
	 * @throws Exception en caso de que alguno de los dos campos esté vacío
	 */
	public Usuario(String user,String pass){
		// TODO Auto-generated constructor stub

		this.username=user;
		this.password=cifrarContraseña(pass);
	}
	
	/**
	 * main para Pruebas con la contraseña 
	 * @param args
	 * @deprecated
	 */	
	public static void main(String[] args) {
		
			Usuario user =new Usuario("Dani","contraseña");
			System.out.println(user.cifrarContraseña("contraseña"));
			if(user.validatePass("contraseña")) System.out.println("Bienvenido: " + user.username);
			else System.out.println("ERROR");
		
	}
	
	
	/**
	 * Metodo que devuelve el nombre de usuario
	 * @return nombre de usuario
	 */
	public String getUser() {
		return this.username;
	}
	
	/**
	 * Metodo para validar la contraseña (necesario para el inicio de sesion)
	 * @param pass contraseña que el usuario introduce y hace falta validar
	 * @return true si se valida correctamente
	 */
	public boolean validatePass(String pass) {
		return this.password.equals(cifrarContraseña(pass));
	}

	
	
	/**
	 * cifra la contraseña, tanto para almacenarla por primera vez como para validarla una vez almacenada
	 * @param pass contraseña a cifrar
	 * @return contraseña cifrada o una cadena vacia si no ha sido capaz de cifrar la contraseña correctamente
	 */
	public static String cifrarContraseña(String pass) {
		
		try {
			MessageDigest m = MessageDigest.getInstance("MD5");
			m.update(pass.getBytes());
			byte[] bytes =m.digest();
			StringBuilder s = new StringBuilder();
			for(int i=0;i<bytes.length;i++) {
				s.append(Integer.toString((bytes[i] & 0xff) + 0x100,16).substring(1));
			}
			String encriptada = s.toString();
			return encriptada;
		}catch(NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return ""; //devuelve una cadena vacia si no ha sido capaz de cifrar la contraseña correctamente
	}
	
	
	/**
	 * Metodo que devuelve la contraseña del usuario
	 * @return devuelve la contraseña del usuario
	 * @deprecated No usar puesto que minimiza la seguridad (solo para pruebas)
	 */
	private String getPass() {
		return this.password;
	}
	
	
	public String toString() {
		return "USERNAME: "+this.username + "\nCONTRASEÑA: "+ this.getPass();
	}
}
