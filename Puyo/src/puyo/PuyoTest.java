package puyo;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

public class PuyoTest extends JFrame {

	private JPanel contentPane;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					PuyoTest frame = new PuyoTest();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the frame.
	 */
	public PuyoTest() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 0, 550, 850);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(null);
		setContentPane(contentPane);
		field0 = new Field(this);
		field0.init();

		npp = new NextPuyoPanel(field0);
		field0.setNPP(npp);
		field0.setBounds(0,0,400,850);
		contentPane.add(field0);
		PuyoUtil.addNextPuyoPanel(npp);
		PuyoUtil.generatePuyos(5);
		PuyoUtil.generatePuyos(5);
		npp.setBounds(420,150, 110,150);
		contentPane.add(npp);
		field0.gameRoop();

	}
Field field0;
NextPuyoPanel npp;
}
