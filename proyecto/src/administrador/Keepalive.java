package administrador;

import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextField;

public class Keepalive {

	private JFrame frame;
	private JTextField txtSeVaA;
	private int tiempo;
	private InterfazComandosAdmin ica;
	private Timer timer;

	/**
	 * Launch the application.
	 * 
	 * @deprecated
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Keepalive window = new Keepalive(null);
					window.frame.setVisible(true);
				} catch (Exception e) {

				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Keepalive(InterfazComandosAdmin ica) {
		this.tiempo = 30;
		this.ica = ica;
		try {
			initialize();
			frame.setVisible(true);

			ActionListener actualizarTiempo = new ActionListener() {
				public void actionPerformed(ActionEvent evt) {
					txtSeVaA.setText("Se va a cerrar cerrar la sesion en " + tiempo + " segundos");
					tiempo--;
					if (tiempo == 0) {
						ica.enviarMensaje("x EXIT");
						ica.cerrarSesion();
						frame.dispose();
					}
				}
			};
			timer = new Timer(1000, actualizarTiempo);
			timer.setRepeats(true);
			timer.start();

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		frame = new JFrame();
		frame.setResizable(false);
		frame.setBounds(100, 100, 445, 157);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		GridBagLayout gridBagLayout = new GridBagLayout();
		gridBagLayout.columnWidths = new int[] { 66, 147, 155, 0, 0 };
		gridBagLayout.rowHeights = new int[] { 38, 0, 0, 0, 0, 0 };
		gridBagLayout.columnWeights = new double[] { 0.0, 0.0, 0.0, 1.0, Double.MIN_VALUE };
		gridBagLayout.rowWeights = new double[] { 0.0, 0.0, 0.0, 0.0, 0.0, Double.MIN_VALUE };
		frame.getContentPane().setLayout(gridBagLayout);

		JButton btnNewButton_1 = new JButton("Mantenerse vivo");
		btnNewButton_1.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ica.enviarMensaje("x OK");
				timer.stop();
				frame.dispose();
			}
		});

		txtSeVaA = new JTextField();
		txtSeVaA.setEnabled(false);
		txtSeVaA.setText("Se va a cerrar cerrar la sesion en 30 segundos");
		txtSeVaA.setEditable(false);
		GridBagConstraints gbc_txtSeVaA = new GridBagConstraints();
		gbc_txtSeVaA.gridwidth = 2;
		gbc_txtSeVaA.insets = new Insets(0, 0, 5, 5);
		gbc_txtSeVaA.fill = GridBagConstraints.HORIZONTAL;
		gbc_txtSeVaA.gridx = 1;
		gbc_txtSeVaA.gridy = 1;
		frame.getContentPane().add(txtSeVaA, gbc_txtSeVaA);
		txtSeVaA.setColumns(10);
		GridBagConstraints gbc_btnNewButton_1 = new GridBagConstraints();
		gbc_btnNewButton_1.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton_1.gridx = 1;
		gbc_btnNewButton_1.gridy = 3;
		frame.getContentPane().add(btnNewButton_1, gbc_btnNewButton_1);

		JButton btnNewButton = new JButton("Cerrar sesion");
		btnNewButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				ica.enviarMensaje("x EXIT");
				ica.cerrarSesion();
				frame.dispose();
			}
		});
		GridBagConstraints gbc_btnNewButton = new GridBagConstraints();
		gbc_btnNewButton.insets = new Insets(0, 0, 5, 5);
		gbc_btnNewButton.gridx = 2;
		gbc_btnNewButton.gridy = 3;
		frame.getContentPane().add(btnNewButton, gbc_btnNewButton);
	}

}
