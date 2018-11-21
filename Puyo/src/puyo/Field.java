package puyo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.Timer;

public class Field extends JPanel {
	private Puyo[][] puyoArray;
	private Timer kumiPuyoDownTimer;
	/**
	 * Newly downing puyos which always consists two puyos.(A couple of Puyos which is in downing process).
	 */
	private Puyo[] kumiPuyo;
	private NextPuyoPanel npp;

	public Field() {
		setLayout(null);
		puyoArray = new Puyo[8][15];//including brim

		setBackground(Color.orange);
	}

	void init() {
		for (int i = 0; i < 8; i++) {
			puyoArray[i][14] = new Puyo(6);
			puyoArray[i][1] = new Puyo(6);
			puyoArray[i][0] = new Puyo(6);
			puyoArray[i][14].setContainer(this);
			puyoArray[i][1].setContainer(this);
			puyoArray[i][0].setContainer(this);
			puyoArray[i][14].setFrameX(i);
			puyoArray[i][1].setFrameX(i);
			puyoArray[i][0].setFrameX(i);
			puyoArray[i][14].setFrameY(14);
			puyoArray[i][1].setFrameY(1);
			puyoArray[i][0].setFrameY(0);
			add(puyoArray[i][14]);
			add(puyoArray[i][1]);
			add(puyoArray[i][0]);
		}
		for (int i = 1; i < 14; i++) {
			puyoArray[0][i] = new Puyo(6);
			puyoArray[7][i] = new Puyo(6);
			puyoArray[0][i].setContainer(this);
			puyoArray[7][i].setContainer(this);
			puyoArray[0][i].setFrameX(0);
			puyoArray[7][i].setFrameX(7);
			puyoArray[0][i].setFrameY(i);
			puyoArray[7][i].setFrameY(i);
			add(puyoArray[0][i]);
			add(puyoArray[7][i]);
		}

	}

	public void setNPP(NextPuyoPanel npp) {
		this.npp = npp;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
	}

	boolean isThereNoPuyo(int frameX, int frameY) {
		//System.out.println("frameX: " + frameX + "    frameY: " + frameY);
		if (puyoArray[frameX][frameY] == null) {
			return true;
		}
		return false;
	}

	/**
	 * Disappearing over four linked puyos.
	*/
	public void processDisappearing() {
		//System.out.println("processDisapeearing()");
		processAllDown();
	}

	/**
	 * After disappearing, spaces which were created by disappearing process need to be processed.
	 */
	public void processAllDown() {
		//System.out.println("processAllDown");
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
		kumiPuyoDownTimer = new Timer(800, new PuyoListener());
		kumiPuyo = npp.pop();
		add(kumiPuyo[0]);
		//System.out.println("kumiPuyo0 posY: " + kumiPuyo[0].getY());
		add(kumiPuyo[1]);
		//System.out.println("kumiPuyo1 posY: " + kumiPuyo[1].getY());
		kumiPuyoDownTimer.start();
	}

	public Dimension getPreferredSize() {
		return new Dimension(400, 800);
	}

	boolean inProcessKeyEvent;

	public class PuyoKeyListener implements KeyListener {
		public void keyPressed(KeyEvent e) {
			if (inProcessKeyEvent) {
				e.consume();
				return;
			}
			//System.out.println("keyPressed");
			int key = e.getKeyCode();
			if (key == KeyEvent.VK_PERIOD) {
			} else if (key == KeyEvent.VK_RIGHT) {
				if (kumiPuyo[0].getFrameY() > kumiPuyo[1].getFrameY()) {//kumiPuyo is in vertical, and kumiPuyo[0] is lower side.
					if (puyoArray[kumiPuyo[0].getFrameX() + 1][kumiPuyo[0].getFrameY()] == null) {
						kumiPuyoMove(1, 0);
					}
				} else if (kumiPuyo[0].getFrameY() < kumiPuyo[1].getFrameY()) {//kumiPuyo is in vertical, and kumiPuyo[1] is lower side.
					if (puyoArray[kumiPuyo[1].getFrameX() + 1][kumiPuyo[1].getFrameY()] == null) {
						kumiPuyoMove(1, 0);
					}
				} else if (kumiPuyo[0].getFrameX() > kumiPuyo[1].getFrameX()) {// kumiPuyo is in horizontal, and kumiPuyo[0] is right side.
					if (puyoArray[kumiPuyo[0].getFrameX() + 1][kumiPuyo[0].getFrameY()] == null) {
						kumiPuyoMove(1, 0);
					}
				} else if (kumiPuyo[0].getFrameX() < kumiPuyo[1].getFrameX()) {// kumiPuyo is in horizontal, and kumiPuyo[1] is right side.)
					if (puyoArray[kumiPuyo[1].getFrameX() + 1][kumiPuyo[1].getFrameY()] == null) {
						kumiPuyoMove(1, 0);
					}
				}
			} else if (key == KeyEvent.VK_LEFT) {
				if (kumiPuyo[0].getFrameY() > kumiPuyo[1].getFrameY()) {//kumiPuyo is in vertical, and kumiPuyo[0] is lower side.
					if (puyoArray[kumiPuyo[0].getFrameX() - 1][kumiPuyo[0].getFrameY()] == null) {
						kumiPuyoMove(-1, 0);
					}
				} else if (kumiPuyo[0].getFrameY() < kumiPuyo[1].getFrameY()) {//kumiPuyo is in vertical, and kumiPuyo[1] is lower side.
					if (puyoArray[kumiPuyo[1].getFrameX() - 1][kumiPuyo[1].getFrameY()] == null) {
						kumiPuyoMove(-1, 0);
					}
				} else if (kumiPuyo[0].getFrameX() > kumiPuyo[1].getFrameX()) {// kumiPuyo is in horizontal, and kumiPuyo[0] is right side.
					if (puyoArray[kumiPuyo[0].getFrameX() - 1][kumiPuyo[0].getFrameY()] == null) {
						kumiPuyoMove(-1, 0);
					}
				} else if (kumiPuyo[0].getFrameX() < kumiPuyo[1].getFrameX()) {// kumiPuyo is in horizontal, and kumiPuyo[1] is right side.)
					if (puyoArray[kumiPuyo[1].getFrameX() - 1][kumiPuyo[1].getFrameY()] == null) {
						kumiPuyoMove(-1, 0);
					}
				}

			}
			inProcessKeyEvent = false;
		}

		private void kumiPuyoMove(int x, int y) {
			kumiPuyo[0].moveCommand(x, y);
			kumiPuyo[1].moveCommand(x, y);
		}

		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {
			System.out.println("keyTyped");
		}

	}

	class PuyoListener implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == kumiPuyoDownTimer) {
				boolean puyo0Movable = false, puyo1Movable = false;
				//System.out.println("timer");
				if (kumiPuyo[1].getY() < kumiPuyo[0].getY()) {//kumiPuyo state is vertical, and kumiPuyo[0] is upper side.
					puyo0Movable = isThereNoPuyo(kumiPuyo[0].getFrameX(), kumiPuyo[0].getFrameY() + 1);
					if (puyo0Movable) {
						kumiPuyo[0].moveCommand(0, 1);//puyo[0] 1step down
						kumiPuyo[1].moveCommand(0, 1);//puyo[1] 1step down
						puyo1Movable = true;
					} else {
						puyo1Movable = false;
					}
				} else if (kumiPuyo[1].getY() > kumiPuyo[0].getY()) {//kumiPuyo state is vertical, and kumiPuyo[1] is upper side.
					puyo1Movable = isThereNoPuyo(kumiPuyo[1].getFrameX(), kumiPuyo[1].getFrameY() + 1);
					if (puyo1Movable) {
						kumiPuyo[1].moveCommand(0, 1);//puyo[1] 1step down
						kumiPuyo[0].moveCommand(0, 1);//puyo[0] 1step down
						puyo0Movable = true;
					} else {
						puyo0Movable = false;
					}
				} else {//if kumiPuyo state is horizontal
					puyo0Movable = isThereNoPuyo(kumiPuyo[0].getFrameX(), kumiPuyo[0].getFrameY() + 1);// is there space under the kumiPuyo[0].
					puyo1Movable = isThereNoPuyo(kumiPuyo[1].getFrameX(), kumiPuyo[1].getFrameY() + 1);// is there space under the kumiPuyo[1].
					if (puyo0Movable)
						kumiPuyo[0].moveCommand(0, 1);
					if (puyo1Movable)
						kumiPuyo[1].moveCommand(0, 1);
				}

				if (puyo0Movable == false) {
					puyoArray[kumiPuyo[0].getFrameX()][kumiPuyo[0].getFrameY()] = kumiPuyo[0];
					//System.out.println("kumiPuyo[0] -> puyoArray:X " + kumiPuyo[0].getFrameX() + " :Y " +kumiPuyo[0].getFrameY());
				}
				if (puyo1Movable == false) {
					puyoArray[kumiPuyo[1].getFrameX()][kumiPuyo[1].getFrameY()] = kumiPuyo[1];
					//System.out.println("kumiPuyo[1] -> puyoArray");
				}
				if (puyo0Movable == false && puyo1Movable == false) {
					kumiPuyoDownTimer.stop();
					processDisappearing();
				}
			}
		}
	}
}