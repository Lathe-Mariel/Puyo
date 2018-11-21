package puyo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Field extends JPanel {
	private Puyo[][] puyoArray;
	private Timer kumiPuyoDownTimer;
	/**
	 * Newly downing puyos which always consists two puyos.(A couple of Puyos which is in downing process).
	 */
	private Puyo[] kumiPuyo;
	/**
	 * The time which is interval of one step in puyo 1frame downing process.
	 */
	private static int oneStepIntervalTime = 5;
	private NextPuyoPanel npp;
	boolean inProcessKeyEvent = false;
	boolean kumiPuyoBroke = false;
	private int key = 0;
	private PuyoKeyListener pkl;

	void setKeyListener(PuyoKeyListener pkl) {
		this.pkl = pkl;
	}

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
				//puyoArray[j][i].downStairs();
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
		//inProcessKeyEvent = true;
		kumiPuyoDownTimer.start();
		//		try {
		//			new Thread() {
		//				public void run() {
		//					pkl.keyHandler();
		//				}
		//			}.start();
		//		} catch (Exception e) {
		//			e.printStackTrace();
		//		}
	}

	public Dimension getPreferredSize() {
		return new Dimension(400, 800);
	}

	class PuyoMover extends Thread {
		Timer timer;
		private int increaseX1 = 0, increaseY1 = 0, increaseX2 = 0, increaseY2 = 0;
		boolean puyo0Movable = true;
		boolean puyo1Movable = true;
		int frameX1, frameY1, frameX2, frameY2;

		private PuyoMover() {
			frameX1 = kumiPuyo[0].getFrameX();
			frameY1 = kumiPuyo[0].getFrameY();
			frameX2 = kumiPuyo[1].getFrameX();
			frameY2 = kumiPuyo[1].getFrameY();
		}

		void toRight() {
			if (kumiPuyo[0].getFrameX() > kumiPuyo[1].getFrameX()) {
				if (puyoArray[kumiPuyo[0].getFrameX() + 1][kumiPuyo[0].getFrameY()] == null) {
					increaseX1 = 1;
					increaseX2 = 1;
				}
			} else if (puyoArray[kumiPuyo[1].getFrameX() + 1][kumiPuyo[1].getFrameY()] == null) {
				increaseX1 = 1;
				increaseX2 = 1;
			} else if (kumiPuyo[0].getFrameY() > kumiPuyo[1].getFrameY()) {
				if (puyoArray[kumiPuyo[0].getFrameX() + 1][kumiPuyo[0].getFrameY()] == null) {
					increaseX1 = 1;
					increaseX2 = 1;
				}
			} else if (puyoArray[kumiPuyo[1].getFrameX() + 1][kumiPuyo[1].getFrameY()] == null) {
				increaseX1 = 1;
				increaseX2 = 1;
			}

		}

		void toLeft() {
			if (kumiPuyo[0].getFrameX() > kumiPuyo[1].getFrameX()) {
				if (puyoArray[kumiPuyo[1].getFrameX() - 1][kumiPuyo[1].getFrameY()] == null) {
					increaseX1 = -1;
					increaseX2 = -1;
				}
			} else if(kumiPuyo[0].getFrameX() < kumiPuyo[1].getFrameX()) { 
				if (puyoArray[kumiPuyo[0].getFrameX() - 1][kumiPuyo[0].getFrameY()] == null) {
				increaseX1 = -1;
				increaseX2 = -1;
				}
			} else if (kumiPuyo[0].getFrameY() > kumiPuyo[1].getFrameY()) {
				if (puyoArray[kumiPuyo[0].getFrameX() - 1][kumiPuyo[0].getFrameY()] == null) {
					increaseX1 = -1;
					increaseX2 = -1;
				}
			} else if (puyoArray[kumiPuyo[1].getFrameX() - 1][kumiPuyo[1].getFrameY()] == null) {
				increaseX1 = -1;
				increaseX2 = -1;
			}

		}

		void toDown() {
			if (kumiPuyo[1].getFrameY() < kumiPuyo[0].getFrameY() && kumiPuyo[0].getFrameX() == kumiPuyo[1].getFrameX()) {//kumiPuyo state is vertical, and kumiPuyo[0] is upper side. &kumi puyo is still kumi(not splited).
				puyo0Movable = isThereNoPuyo(kumiPuyo[0].getFrameX(), kumiPuyo[0].getFrameY() + 1);
				if (puyo0Movable) {
					increaseY1 = 1;
					increaseY2 = 1;
				} else {
					puyo1Movable = false;
				}
			} else if (kumiPuyo[1].getFrameY() > kumiPuyo[0].getFrameY() && kumiPuyo[0].getFrameX() == kumiPuyo[1].getFrameX()) {//kumiPuyo state is vertical, and kumiPuyo[1] is upper side. &kumi puyo is still kumi(not splited).
				puyo1Movable = isThereNoPuyo(kumiPuyo[1].getFrameX(), kumiPuyo[1].getFrameY() + 1);
				if (puyo1Movable) {
					increaseY2 = 1;
					increaseY1 = 1;
				} else {
					puyo0Movable = false;
				}
			} else {//if kumiPuyo state is horizontal
				puyo0Movable = isThereNoPuyo(kumiPuyo[0].getFrameX(), kumiPuyo[0].getFrameY() + 1);// is there space under the kumiPuyo[0].
				puyo1Movable = isThereNoPuyo(kumiPuyo[1].getFrameX(), kumiPuyo[1].getFrameY() + 1);// is there space under the kumiPuyo[1].
				if (puyo0Movable) {
					increaseY1 = 1;

				}
				if (puyo1Movable) {
					increaseY2 = 1;

				}
			}

			if (puyo0Movable == false) {
				puyoArray[kumiPuyo[0].getFrameX()][kumiPuyo[0].getFrameY()] = kumiPuyo[0];
				//kumiPuyoBroke = true;
				//System.out.println("kumiPuyo[0] -> puyoArray:X " + kumiPuyo[0].getFrameX() + " :Y " +kumiPuyo[0].getFrameY());
			}
			if (puyo1Movable == false) {
				puyoArray[kumiPuyo[1].getFrameX()][kumiPuyo[1].getFrameY()] = kumiPuyo[1];
				//kumiPuyoBroke = true;
				//System.out.println("kumiPuyo[1] -> puyoArray");
			}
			if (puyo0Movable == false && puyo1Movable == false) {
				inProcessKeyEvent = false;
				kumiPuyoDownTimer.stop();
				processDisappearing();
			}
		}

		void toRotate() {
			puyo0Movable = false;
			if (kumiPuyo[0].getFrameY() < kumiPuyo[1].getFrameY()) {
				if (puyoArray[kumiPuyo[1].getFrameX() - 1][kumiPuyo[1].getFrameY() - 1] == null) {
					increaseX2 = -1;
					increaseY2 = -1;
				}
			} else if (kumiPuyo[0].getFrameY() > kumiPuyo[1].getFrameY()) {
				if (puyoArray[kumiPuyo[1].getFrameX() + 1][kumiPuyo[1].getFrameY() + 1] == null) {
					increaseX2 = 1;
					increaseY2 = 1;
				}
			} else if (kumiPuyo[0].getFrameX() < kumiPuyo[1].getFrameX()) {
				if (puyoArray[kumiPuyo[1].getFrameX() - 1][kumiPuyo[1].getFrameY() + 1] == null) {
					increaseX2 = -1;
					increaseY2 = 1;
				}
			} else if (kumiPuyo[0].getFrameX() > kumiPuyo[1].getFrameX()) {
				if (puyoArray[kumiPuyo[1].getFrameX() + 1][kumiPuyo[1].getFrameY() - 1] == null) {
					increaseX2 = 1;
					increaseY2 = -1;
				}
			}

		}

		int x1, x2, y1, y2;

		public synchronized void run() {
			if (increaseX2 == 0 && increaseY2 == 0)
				return;

			int incX1 = increaseX1 * 5;
			int incY1 = increaseY1 * 5;
			int incX2 = increaseX2 * 5;
			int incY2 = increaseY2 * 5;
			x1 = kumiPuyo[0].getFrameX() * 50;
			y1 = kumiPuyo[0].getFrameY() * 50;
			x2 = kumiPuyo[1].getFrameX() * 50;
			y2 = kumiPuyo[1].getFrameY() * 50;
			Field c1 = kumiPuyo[0].getField();
			Field c2 = kumiPuyo[1].getField();

			kumiPuyo[0].setBounds(x1, y1, 50, 50);
			kumiPuyo[1].setBounds(x2, y2, 50, 50);
			//System.out.println(Thread.currentThread().getName());
			for (int i = 0; i < 10; i++) {
				x1 += incX1;
				y1 += incY1;
				x2 += incX2;
				y2 += incY2;

				kumiPuyo[0].setLocation(x1, y1);
				kumiPuyo[1].setLocation(x2, y2);

				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						c1.repaint(10, x1, y1, 50, 55);
						c2.repaint(10, x2, y2, 50, 55);
					}
				});
				kumiPuyo[0].setFrameX(frameX1 + increaseX1);
				kumiPuyo[0].setFrameY(frameY1 + increaseY1);
				kumiPuyo[1].setFrameX(frameX2 + increaseX2);
				kumiPuyo[1].setFrameY(frameY2 + increaseY2);
				try {
					sleep(oneStepIntervalTime);
				} catch (Exception e) {
					System.out.println("Puyo Dropper Sleep");
					e.printStackTrace();
				}
			}
		}
	}

	public class PuyoKeyListener implements KeyListener {
		public void keyPressed(KeyEvent e) {
			
			key = e.getKeyCode();
			if (key == KeyEvent.VK_RIGHT) {
				key = 0;
				PuyoMover pm = new PuyoMover();
				pm.toRight();
				new Thread(pm).start();
			} else if (key == KeyEvent.VK_LEFT) {
				key = 0;
				PuyoMover pm = new PuyoMover();
				pm.toLeft();
				new Thread(pm).start();
			} else if (key == KeyEvent.VK_DOWN) {
				key = 0;
				PuyoMover pm = new PuyoMover();
				pm.toDown();
				new Thread(pm).start();
			} else if (key == KeyEvent.VK_DECIMAL || key == KeyEvent.VK_A) {
				key = 0;
				PuyoMover pm = new PuyoMover();
				pm.toRotate();
				new Thread(pm).start();
			}
		}

		//		private void kumiPuyoMove(int x, int y) {
		//			kumiPuyo[0].moveCommand(x, y);
		//			kumiPuyo[1].moveCommand(x, y);
		//		}

		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {
			System.out.println("keyTyped");
		}

//		void keyHandler() {
//			while (inProcessKeyEvent) {
//				if (key == KeyEvent.VK_RIGHT) {
//					key = 0;
//					PuyoMover pm = new PuyoMover();
//					pm.toRight();
//					new Thread(pm).start();
//				} else if (key == KeyEvent.VK_LEFT) {
//					key = 0;
//					PuyoMover pm = new PuyoMover();
//					pm.toLeft();
//					new Thread(pm).start();
//				} else if (key == KeyEvent.VK_DECIMAL || key == KeyEvent.VK_A) {
//					key = 0;
//					PuyoMover pm = new PuyoMover();
//					pm.toRotate();
//					new Thread(pm).start();
//				}
//			}
//		}
	}

	class PuyoListener implements ActionListener {
		//boolean puyo0Movable = true, puyo1Movable = true;
		public void actionPerformed(ActionEvent e) {
			System.out.println("actionperformed");
			PuyoMover pm = new PuyoMover();
			pm.toDown();
			new Thread(pm).start();
		}
	}
}