package puyo;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.FlowLayout;
import java.awt.Image;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import javax.swing.border.LineBorder;

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
		setBounds(100, 100, 450, 700);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		field0 = new Field();
		field0.setLayout(new FlowLayout());
		npp = new NextPuyoPanel(field0);
		field0.setNPP(npp);
		field0.setBorder(new LineBorder(Color.red, 4));
		contentPane.add(field0, BorderLayout.WEST);
		PuyoUtil.addNextPuyoPanel(npp);
		PuyoUtil.generatePuyos(5);
		PuyoUtil.generatePuyos(5);
		field0.startNewPuyo();
		Image img = null;
		try {
		img = ImageIO.read(new File("red.jpg"));
		}catch(Exception e) {}
		ImageIcon imgIcon = new ImageIcon(img);
		contentPane.add(new JButton(imgIcon), BorderLayout.EAST);
	}
Field field0;
NextPuyoPanel npp;
}
