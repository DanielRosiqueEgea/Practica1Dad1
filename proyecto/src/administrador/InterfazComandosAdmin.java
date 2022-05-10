package administrador;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTextPane;

public class InterfazComandosAdmin {

	private JFrame frame;
	private Socket socket;
	private HiloLecturaAdmin hilo;
	protected int number = 0;
	protected BufferedReader manual;
	private JTextField textoComando;
	protected PrintWriter pw;
	private JTextPane textoRespuesta;
	private JScrollPane scrollPane;
	private ArrayList<String> historico;// se guardarán los comandos introducidos en la sesion
	private int indiceHistorico;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InterfazComandosAdmin window = new InterfazComandosAdmin();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public InterfazComandosAdmin() {

		socket = null;
		historico = new ArrayList<String>();
		try {

			socket = new Socket("localhost", 2022);
			BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			this.pw = new PrintWriter(socket.getOutputStream());
			System.out.println("CONECTADO CORRECTAMENTE");

			hilo = new HiloLecturaAdmin(socket, br, pw, this);
			hilo.start();
			initialize();
		} catch (Exception e) {

			e.printStackTrace();
		}

		
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setBounds(100, 100, 662, 382);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 0, 480, 0, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 24, 35, 237, 40, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 1.0, 1.0, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		textoComando = new JTextField();
		textoComando.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case KeyEvent.VK_ENTER:
					enviarComando();
					break;
				case KeyEvent.VK_UP:
					subirComando();
					break;
				case KeyEvent.VK_DOWN:
					bajarComando();
					break;
				}

			}

		});
		GridBagConstraints gbc_textoComando = new GridBagConstraints();
		gbc_textoComando.insets = new Insets(0, 0, 5, 5);
		gbc_textoComando.fill = GridBagConstraints.HORIZONTAL;
		gbc_textoComando.gridx = 1;
		gbc_textoComando.gridy = 1;
		frame.getContentPane().add(textoComando, gbc_textoComando);
		textoComando.setColumns(10);

		JButton btnNewButton = new JButton("Enviar Comando");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enviarComando();
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.gridx = 2;
		gbc_btnNewButton.gridy = 1;
		frame.getContentPane().add(btnNewButton, gbc_btnNewButton);

		scrollPane = new JScrollPane();
		GridBagConstraints gbc_scrollPane = new GridBagConstraints();
		gbc_scrollPane.gridwidth = 2;
		gbc_scrollPane.insets = new Insets(0, 0, 5, 5);
		gbc_scrollPane.fill = GridBagConstraints.BOTH;
		gbc_scrollPane.gridx = 1;
		gbc_scrollPane.gridy = 2;
		frame.getContentPane().add(scrollPane, gbc_scrollPane);

		textoRespuesta = new JTextPane();
		textoRespuesta.setText("Introduce \"MAN\" para ver el manual");
		scrollPane.setViewportView(textoRespuesta);
	}

	/**
	 * Metodo que envia el comando por el canal de comandos
	 * 
	 */
	private void enviarComando() {

		String comando;
		String lineaLeida;
		lineaLeida = textoComando.getText();
		if (lineaLeida.isEmpty()) {
			return;
		}
		historico.add(lineaLeida);
		indiceHistorico = historico.size();
		comando = lineaLeida.split(" ")[0]; // se separa el comando de lo que
		lineaLeida = this.number + " " + lineaLeida;
		textoRespuesta.setText("");
		if ("MAN".equals(comando)) {
			// this.menu();
			this.mostrarManual();
			return;
		}

		this.enviarMensaje(lineaLeida);
		if ("EXIT".equals(comando)) {
			this.cerrarSesion();
			return;
		}
		this.number++;
		return;
	}

	public void enviarMensaje(String str) {
		pw.println(str);
		pw.flush();
	}

	public void cerrarSesion() {
		this.hilo.interrupt();
		try {
			this.socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Metodo que sube un comando en el historico de comandos
	 */
	private void subirComando() {
		if (indiceHistorico != 0 && historico.size() != 0) {
			indiceHistorico--;
			textoComando.setText(historico.get(indiceHistorico));

		}

	}

	/**
	 * Metodo que baja un comando en el historico de comandos
	 */
	private void bajarComando() {
		if (indiceHistorico < (historico.size() - 1) && historico.size() != 0) {
			indiceHistorico++;
			textoComando.setText(historico.get(indiceHistorico));

		}

	}

	public void imprimirTexto(String str) {
		textoRespuesta.setText(textoRespuesta.getText() + "\n" + str);
	}

	/**
	 * Metodo que imprime el manual con los comandos disponibles en el servidor
	 */
	public void mostrarManual() {
		String str;
		try {
			this.manual = new BufferedReader(new FileReader("manual.txt"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		try {
			while ((str = manual.readLine()) != null) {
				this.imprimirTexto(str);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Metodo que muestra le muestra el menú al admin
	 * 
	 * @deprecated
	 */
	public void menu() {
		this.imprimirTexto("QUE DESEA HACER?");
		this.imprimirTexto("USER <name>");
		this.imprimirTexto("PASS <pass>");
		this.imprimirTexto("ADDCLIENTE");
		this.imprimirTexto("UPDATECLIENTE <id>");
		this.imprimirTexto("GETCLIENTE <id>");
		this.imprimirTexto("REMOVECLIENTE <id>");
		this.imprimirTexto("LISTCLIENTES");
		this.imprimirTexto("COUNTCLIENTES");
		this.imprimirTexto("LOAD");
		this.imprimirTexto("SAVE");
		this.imprimirTexto("EXIT\n");
	}

}
