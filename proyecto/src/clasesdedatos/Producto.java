package clasesdedatos;

import java.io.Serializable;

public class Producto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nombre;
	private float precio;
	
	public Producto(String nombre,float precio) {
		this.nombre=nombre;
		this.precio=precio;
	}
	public String getNombre() {
		return this.nombre;	
	}
	public float getPrecio() {
		return this.precio;
	}
	public String toString() {
		return "Nombre: "+this.nombre+ "\nPrecio: "+ this.precio +"€";
	}

}
