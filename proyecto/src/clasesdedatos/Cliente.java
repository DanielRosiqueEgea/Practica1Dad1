package clasesdedatos;

import java.io.Serializable;

public class Cliente implements Serializable {
	
	private String nombre;
	private String apellido;
	public Cliente(String nombre,String apellido) {
		// TODO Auto-generated constructor stub
		this.nombre=nombre;
		this.apellido=apellido;
	}
	
	public String getNombre() {
		return this.nombre;
	}
	public String getApellido() {
		return this.apellido;
	}

	public String toString() {
		return "Nombre: " + this.nombre + "\nApellido: " + this.apellido;
	}
}
