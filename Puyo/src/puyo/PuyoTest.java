package puyo;

import java.awt.BorderLayout;
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
		setBounds(100, 0, 460, 850);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		field0 = new Field(this);
		field0.init();

		npp = new NextPuyoPanel(field0);
		field0.setNPP(npp);
		contentPane.add(field0, BorderLayout.WEST);
		PuyoUtil.addNextPuyoPanel(npp);
		PuyoUtil.generatePuyos(5);
		PuyoUtil.generatePuyos(5);
		field0.gameRoop();

	}
Field field0;
NextPuyoPanel npp;
}
