package administrador;

import java.awt.EventQueue;

import javax.swing.JFrame;
import java.awt.GridBagLayout;
import javax.swing.JLabel;
import java.awt.GridBagConstraints;
import javax.swing.JTextField;

import clasesdedatos.Cliente;
import server.HiloCanalDatosServer;

import java.awt.Insets;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

public class InterfazCliente {

	private JFrame frmAadirCliente;
	private JTextField txtNombreCLiente;
	private JTextField txtApellidos;
	private CanalDatosAdmin canal;
	private String nombre;
	private String apellidos;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					InterfazCliente window = new InterfazCliente(null);
					window.frmAadirCliente.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public InterfazCliente(CanalDatosAdmin canal) {
		this.canal = canal;
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {

					initialize();
					frmAadirCliente.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public InterfazCliente(CanalDatosAdmin canal, String nombre, String apellidos) {
		this(canal);
		this.nombre = nombre;
		this.apellidos = apellidos;

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frmAadirCliente = new JFrame();
		frmAadirCliente.setTitle("A\u00F1adir Cliente");
		frmAadirCliente.setBounds(100, 100, 362, 229);
		frmAadirCliente.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 30, 263, 99, 0 };
		gridBagLayout.rowHeights = new int[] { 30, 0, 13, 27, 27, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 1.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		frmAadirCliente.getContentPane().setLayout(gridBagLayout);

		JLabel lblNewLabel = new JLabel("Nombre del cliente");
		GridBagConstraints gbc_lblNewLabel = new GridBagConstraints();
		gbc_lblNewLabel.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel.gridx = 1;
		gbc_lblNewLabel.gridy = 1;
		frmAadirCliente.getContentPane().add(lblNewLabel, gbc_lblNewLabel);

		txtNombreCLiente = new JTextField();
		GridBagConstraints gbc_txtNombreCLiente = new GridBagConstraints();
		gbc_txtNombreCLiente.insets = new Insets(0, 0, 5, 5);
		gbc_txtNombreCLiente.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtNombreCLiente.gridx = 1;
		gbc_txtNombreCLiente.gridy = 2;
		frmAadirCliente.getContentPane().add(txtNombreCLiente, gbc_txtNombreCLiente);
		txtNombreCLiente.setColumns(10);

		JLabel lblNewLabel_1 = new JLabel("Apellidos del cliente");
		GridBagConstraints gbc_lblNewLabel_1 = new GridBagConstraints();
		gbc_lblNewLabel_1.insets = new Insets(0, 0, 5, 5);
		gbc_lblNewLabel_1.gridx = 1;
		gbc_lblNewLabel_1.gridy = 3;
		frmAadirCliente.getContentPane().add(lblNewLabel_1, gbc_lblNewLabel_1);

		txtApellidos = new JTextField();
		GridBagConstraints gbc_txtApellidos = new GridBagConstraints();
		gbc_txtApellidos.insets = new Insets(0, 0, 5, 5);
		gbc_txtApellidos.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtApellidos.gridx = 1;
		gbc_txtApellidos.gridy = 4;
		frmAadirCliente.getContentPane().add(txtApellidos, gbc_txtApellidos);
		txtApellidos.setColumns(10);

		JButton btnAñadirCliente = new JButton("aceptar");
		btnAñadirCliente.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				enviarCliente();
			}

		});
		GridBagConstraints gbc_btnAñadirCliente = new GridBagConstraints();
		gbc_btnAñadirCliente.insets = new Insets(0, 0, 0, 5);
		gbc_btnAñadirCliente.gridx = 1;
		gbc_btnAñadirCliente.gridy = 5;
		txtNombreCLiente.setText(nombre);
		txtApellidos.setText(apellidos);
		frmAadirCliente.getContentPane().add(btnAñadirCliente, gbc_btnAñadirCliente);
	}

	private void enviarCliente() {

		String nombre = txtNombreCLiente.getText();
		String apellido = txtApellidos.getText();
		if (nombre.isEmpty() || apellido.isEmpty()) {
			return;
		}
		canal.enviarObjeto(new Cliente(nombre, apellido));
		this.frmAadirCliente.dispose();
	}
}
