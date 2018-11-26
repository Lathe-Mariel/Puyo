package puyo;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.JPanel;

public class NextPuyoPanel extends JPanel {
	private ArrayList<Puyo> nextPuyoQueue;
	private Field connectField;

	public NextPuyoPanel(Field connectField) {
		this.connectField = connectField;
		nextPuyoQueue = new ArrayList<Puyo>();
	}

	Puyo[] pop() {
		Puyo[] popOutPuyos = new Puyo[2];
		popOutPuyos[0] = (nextPuyoQueue.get(0));
		nextPuyoQueue.remove(0);
		popOutPuyos[1] = (nextPuyoQueue.get(0));
		nextPuyoQueue.remove(0);
		repaint();
		while(nextPuyoQueue.size() <=4) {
			System.out.println("nextPuyoQueue.size(): " + nextPuyoQueue.size());
			PuyoUtil.generatePuyos(5);
		}
		return popOutPuyos;
	}

	void insertPuyos(Puyo[] newPuyo) {
		nextPuyoQueue.add(newPuyo[0]);
		nextPuyoQueue.add(newPuyo[1]);
		newPuyo[0].setContainer(connectField);
		newPuyo[1].setContainer(connectField);
	}
	
	public Rectangle getBounds() {
		return new Rectangle(100,150,460,150);
	}
	public Dimension getPreferredSize() {
		return new Dimension(100,150);
	}
	
	public void paintComponent(Graphics g) {
		g.fill3DRect(0, 0, getWidth(), getHeight(), true);
		g.drawImage(nextPuyoQueue.get(0).getImage(),0,0,this);
		g.drawImage(nextPuyoQueue.get(1).getImage(),0,50,this);
		g.drawImage(nextPuyoQueue.get(2).getImage(),55,25,this);
		g.drawImage(nextPuyoQueue.get(3).getImage(),55,75,this);
	}
}
