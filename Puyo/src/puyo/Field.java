package puyo;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Field extends JPanel {
	private Puyo[][] puyoArray;
	private Timer downTimer;
	private Puyo[] kumiPuyo;

	public Field() {
		setLayout(null);
	}

	public void paintComponent(Graphics g) {
	}

	boolean isTherePuyo(int frameX, int frameY) {
		if (puyoArray[frameX][frameY] == null) {
			return false;
		}
		return true;
	}

	public void processAllDown() {
		for (int i = 12; i > 1; i--) {
			for (int j = 0; j < 6; j++) {
				if (puyoArray[j][i].getUnderSpace() == 0)
					return;
				puyoArray[j][i].downStairs();
			}
		}
	}

	private void startNewPuyo() {
		downTimer = new Timer(500, new PuyoListener());
	}
	
	public Dimension getPreferredSize() {
		return new Dimension(310,610);
	}

	class PuyoListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == downTimer) {
				boolean puyo0 = kumiPuyo[0].naturalDrop();
				boolean puyo1 = kumiPuyo[1].naturalDrop();

				if (puyo0 == false) {
					puyoArray[kumiPuyo[0].getFrameX()][kumiPuyo[0].getFrameY()] = kumiPuyo[0];
				}
				if (puyo1 == false) {
					puyoArray[kumiPuyo[1].getFrameX()][kumiPuyo[1].getFrameY()] = kumiPuyo[1];
				}
				if(puyo0 == false && puyo1 == false)downTimer.stop();

			}
		}
	}
}