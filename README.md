# Practica1Dad1
Practica de desarrollo de aplicaciones distribuidas 1, este es un trabajo universitario que trata de una conexion a un servidor local (en el propio ordenador) para manejar clientes y usuarios
<p>
El enunciado del trabajo se encuentra en el pdf "Enero enunciado paractica sockets.pdf"
Subiré el JavaDoc en cuanto el trabajo esté terminado.
</p>
<p>
<h1>Algunas funcionalidades extra (no incluidas en el enunciado) son:</h1>
<ul>
    <li>Cifrado de contraseña del usuario -> clasesdedatos.Usuario.cifrarContraseña(String)</li>
    <li>Uso de una "Base de datos" mediante ficheros .ser -> sede.Sede.cargarBaseDatos() y sede.Sede.guardarBaseDatos()</li>
    <li>Manejo de los usuarios -> sede.Sede.addUser(Usuario), sede.Sede.removeUsuario(String) y sede.Sede.mostrarUsuarios()</li>
    <li>Manejo de permisos de usuario (pendiente de implementacion) -> clasesdedatos.Usuario.permit  -> permit=3 (admin) permit=2 (contributor) permit=1 (lector)</li>
    <li>Metodo para la generacion "automatica" de mensajes por parte del server -> server.HiloComandoServer.generarMensaje(int, int, int, String)</li>
</ul>
<p>
