package puyo;

import java.awt.Graphics;

import javax.swing.JPanel;

public class TopPanel extends JPanel {

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(new java.awt.Color(0xF0,0xFF,0xFF));
		g.fillRect(0, 0, getWidth(), getHeight());
	}
}
