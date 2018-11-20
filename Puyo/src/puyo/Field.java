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
	/**
	 * Newly downing puyos which always consists two puyos.
	 */
	private Puyo[] kumiPuyo;
	private NextPuyoPanel npp;

	public Field() {
		setLayout(null);
		puyoArray = new Puyo[6][14];
	}

	public void setNPP(NextPuyoPanel npp) {
		this.npp = npp;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	boolean isTherePuyo(int frameX, int frameY) {
		System.out.println("frameX: " + frameX + "    frameY: " + frameY);
		if (puyoArray[frameX][frameY] == null) {
			return false;
		}
		return true;
	}

	/**
	 * Disappearing over four linked puyos.
	*/
	public void processDisappearing() {
		System.out.println("processDisapeearing()");
		processAllDown();
	}

	/**
	 * After disappearing, spaces which were created by disappearing process need to be processed.
	 */
	public void processAllDown() {
		System.out.println("processAllDown");
		for (int i = 12; i > 1; i--) {
			for (int j = 0; j < 6; j++) {
				if (puyoArray[j][i] == null || puyoArray[j][i].getUnderSpace() == 0)
					continue;
				puyoArray[j][i].downStairs();
			}
		}
		startNewPuyo();
	}

	void startNewPuyo() {
		downTimer = new Timer(500, new PuyoListener());
		kumiPuyo = npp.pop();
		add(kumiPuyo[0]);
		System.out.println("kumiPuyo0 posY: " + kumiPuyo[0].getY());
		add(kumiPuyo[1]);
		System.out.println("kumiPuyo1 posY: " + kumiPuyo[1].getY());
		downTimer.start();
	}

	public Dimension getPreferredSize() {
		return new Dimension(310, 610);
	}

	class PuyoListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == downTimer) {
				boolean puyo0 = false, puyo1 = false;
				System.out.println("timer");
				if (kumiPuyo[1].getY() < kumiPuyo[0].getY()) {
					puyo0 = kumiPuyo[0].naturalDrop();
					if (puyo0) {
						puyo1 = kumiPuyo[1].naturalDrop();
					} else {
						puyo1 = false;
					}
				} else if (kumiPuyo[1].getY() > kumiPuyo[0].getY()) {
					puyo1 = kumiPuyo[1].naturalDrop();
					if (puyo1) {
						puyo0 = kumiPuyo[0].naturalDrop();
					} else {
						puyo0 = false;
					}
				}

				if (puyo0 == false) {
					puyoArray[kumiPuyo[0].getFrameX()][kumiPuyo[0].getFrameY()] = kumiPuyo[0];
					System.out.println("kumiPuyo[0] -> puyoArray");
				}
				if (puyo1 == false) {
					puyoArray[kumiPuyo[1].getFrameX()][kumiPuyo[1].getFrameY()] = kumiPuyo[1];
					System.out.println("kumiPuyo[1] -> puyoArray");
				}
				if (puyo0 == false && puyo1 == false) {
					downTimer.stop();
					processDisappearing();
				}
			}
		}
	}
}