package puyo;

import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Graphics;

import javax.swing.JLabel;
import javax.swing.JPanel;

public class TopPanel extends JPanel {
	JLabel scoreLabel;
	
	public TopPanel() {
		setLayout(new FlowLayout(FlowLayout.TRAILING));
		scoreLabel = new JLabel(0+"");
		scoreLabel.setFont(new Font("Serif", Font.PLAIN, 40));
		add(scoreLabel);
	}
	
	public void addScore(int score) {
		scoreLabel.setText(Integer.parseInt(scoreLabel.getText()) + score + "");
	}
	
	public int getScore() {
		return Integer.parseInt(scoreLabel.getText());
	}
	
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.setColor(new java.awt.Color(0xF0,0xFF,0xFF));
		g.fillRect(0, 0, getWidth(), getHeight());
	}
}
