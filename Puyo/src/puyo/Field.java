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
			puyoArray[i][14].setStartFrame(i, 14);
			puyoArray[i][1].setStartFrame(i, 1);
			puyoArray[i][0].setStartFrame(i, 0);
			add(puyoArray[i][14]);
			add(puyoArray[i][1]);
			add(puyoArray[i][0]);
		}
		for (int i = 1; i < 14; i++) {
			puyoArray[0][i] = new Puyo(6);
			puyoArray[7][i] = new Puyo(6);
			puyoArray[0][i].setContainer(this);
			puyoArray[7][i].setContainer(this);
			puyoArray[0][i].setStartFrame(0, i);
			puyoArray[7][i].setStartFrame(7, i);
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

	private void createNewTimer(int interval) {
		if (kumiPuyoDownTimer != null) {
			kumiPuyoDownTimer.stop();
		}
		kumiPuyoDownTimer = new Timer(interval, new PuyoListener());
		kumiPuyoDownTimer.start();
	}

	void startNewPuyo() {
		createNewTimer(800);
		kumiPuyo = npp.pop();
		puyo0Movable = true;
		puyo1Movable = true;
		add(kumiPuyo[0]);
		System.out.println("kumiPuyo0 posY: " + kumiPuyo[0].getY());
		add(kumiPuyo[1]);
		System.out.println("kumiPuyo1 posY: " + kumiPuyo[1].getY());
	}

	public Dimension getPreferredSize() {
		return new Dimension(400, 800);
	}

	boolean puyo0Movable = true;
	boolean puyo1Movable = true;
	boolean isBreakKumiPuyo = false;

	class PuyoMover extends Thread {
		Timer timer;
		private int increaseX1 = 0, increaseY1 = 0, increaseX2 = 0, increaseY2 = 0;

		int frameX1, frameY1, frameX2, frameY2;

		private PuyoMover() {
			frameX1 = kumiPuyo[0].getFrameX();
			frameY1 = kumiPuyo[0].getFrameY();
			frameX2 = kumiPuyo[1].getFrameX();
			frameY2 = kumiPuyo[1].getFrameY();
		}

		void toRight() {
			if (frameX1 > frameX2) {
				if (puyoArray[frameX1 + 1][frameY1] == null) {
					increaseX1 = 1;
					increaseX2 = 1;
				}
			} else if (frameX1 < frameX2) {
				if (puyoArray[frameX2 + 1][frameY2] == null) {
					increaseX1 = 1;
					increaseX2 = 1;
				}
			} else if (frameY1 > frameY2) {
				if (puyoArray[frameX1 + 1][frameY1] == null) {
					increaseX1 = 1;
					increaseX2 = 1;
				}
			} else if (puyoArray[frameX2 + 1][frameY2] == null) {
				increaseX1 = 1;
				increaseX2 = 1;
			}
		}

		void toLeft() {
			if (frameX1 > frameX2) {
				if (puyoArray[frameX2 - 1][frameY2] == null) {
					increaseX1 = -1;
					increaseX2 = -1;
				}
			} else if (frameX1 < frameX2) {
				if (puyoArray[frameX1 - 1][frameY1] == null) {
					increaseX1 = -1;
					increaseX2 = -1;
				}
			} else if (frameY1 > frameY2) {
				if (puyoArray[frameX1 - 1][frameY1] == null) {
					increaseX1 = -1;
					increaseX2 = -1;
				}
			} else if (puyoArray[frameX2 - 1][frameY2] == null) {
				increaseX1 = -1;
				increaseX2 = -1;
			}
		}

		void toDown() {
			if (frameY2 < frameY1 && frameX1 == frameX2) {//kumiPuyo state is vertical, and kumiPuyo[0] is upper side. &kumi puyo is still kumi(not splited).
				puyo0Movable = isThereNoPuyo(frameX1, frameY1 + 1);
				if (puyo0Movable) {
					increaseY1 = 1;
					increaseY2 = 1;
				} else {
					puyo1Movable = false;
				}
			} else if (frameY2 > frameY1 && frameX1 == frameX2) {//kumiPuyo state is vertical, and kumiPuyo[1] is upper side. &kumi puyo is still kumi(not splited).
				puyo1Movable = isThereNoPuyo(frameX2, frameY2 + 1);
				if (puyo1Movable) {
					increaseY2 = 1;
					increaseY1 = 1;
				} else {
					puyo0Movable = false;
				}
			} else {//if kumiPuyo state is horizontal
				puyo0Movable = isThereNoPuyo(frameX1, frameY1 + 1);// is there space under the kumiPuyo[0].
				puyo1Movable = isThereNoPuyo(frameX2, frameY2 + 1);// is there space under the kumiPuyo[1].
				if (puyo0Movable) {
					increaseY1 = 1;
				} else {
					isBreakKumiPuyo = true;
					createNewTimer(60);
				}
				if (puyo1Movable) {
					increaseY2 = 1;
				} else {
					isBreakKumiPuyo = true;
					createNewTimer(60);
				}
			}

			if (puyo0Movable == false) {
				puyoArray[frameX1][frameY1] = kumiPuyo[0];
				//kumiPuyoBroke = true;
				//System.out.println("kumiPuyo[0] -> puyoArray:X " + frameX1 + " :Y " +frameY1);
			}
			if (puyo1Movable == false) {
				puyoArray[frameX2][frameY2] = kumiPuyo[1];
				//kumiPuyoBroke = true;
				//System.out.println("kumiPuyo[1] -> puyoArray");
			}
			if (puyo0Movable == false && puyo1Movable == false) {
				inProcessKeyEvent = false;
				kumiPuyoDownTimer.stop();
				isBreakKumiPuyo = false;
				createNewTimer(800);
				processDisappearing();
			}
		}

		void toRotate() {
			if (frameY1 < frameY2) {
				if (puyoArray[frameX2 - 1][frameY2 - 1] == null) {
					increaseX2 = -1;
					increaseY2 = -1;
				}
			} else if (frameY1 > frameY2) {
				if (puyoArray[frameX2 + 1][frameY2 + 1] == null) {
					increaseX2 = 1;
					increaseY2 = 1;
				}
			} else if (frameX1 < frameX2) {
				if (puyoArray[frameX2 - 1][frameY2 + 1] == null) {
					increaseX2 = -1;
					increaseY2 = 1;
				}
			} else if (frameX1 > frameX2) {
				if (puyoArray[frameX2 + 1][frameY2 - 1] == null) {
					increaseX2 = 1;
					increaseY2 = -1;
				}
			}
		}
Puyo[] tempKumiPuyo = new Puyo[2];
		int x1, x2, y1, y2;
		public void run() {
			if (increaseX2 == 0 && increaseY2 == 0 && increaseY1 == 0) {
				System.out.println("run return");
				return;
			}
			int incX1;
			int incY1;
			int incX2;
			int incY2;
			synchronized (kumiPuyo) {
				kumiPuyo[0].setFrameX(frameX1 + increaseX1);
				kumiPuyo[0].setFrameY(frameY1 + increaseY1);
				kumiPuyo[1].setFrameX(frameX2 + increaseX2);
				kumiPuyo[1].setFrameY(frameY2 + increaseY2);
				incX1 = increaseX1 * 5;
				incY1 = increaseY1 * 5;
				incX2 = increaseX2 * 5;
				incY2 = increaseY2 * 5;
				
				x1 = frameX1 * 50;
				y1 = frameY1 * 50;
				x2 = frameX2 * 50;
				y2 = frameY2 * 50;

				//kumiPuyo[0].setPara(x1, y1);
				//kumiPuyo[1].setPara(x2, y2);
				//System.out.println(Thread.currentThread().getName());
				tempKumiPuyo = kumiPuyo;
			}


			for (int i = 0; i < 10; i++) {
				try {
					sleep(oneStepIntervalTime);
				} catch (Exception e) {
					System.out.println("Puyo Dropper Sleep");
					e.printStackTrace();
				}
				System.out.println("x1: " + x1 + ",  y1: " + y1);
				x1 += incX1;
				y1 += incY1;
				x2 += incX2;
				y2 += incY2;
				System.out.println("x1: " + x1 + ",  y1: " + y1);
				
				SwingUtilities.invokeLater(new Runnable() {

					public void run() {
						tempKumiPuyo[0].setPara(x1, y1);
						tempKumiPuyo[1].setPara(x2, y2);

						//						kumiPuyo[0].setBounds(x1, y1,50,50);
						//						kumiPuyo[1].setBounds(x2, y2,50,50);

						repaint(5, x1, y1, 50, 55);
						repaint(5, x2, y2, 50, 55);
					}
				});
				
			}

		}
	}

	public class PuyoKeyListener implements KeyListener {
		public void keyPressed(KeyEvent e) {
			new Thread() {
				public void run() {
					keyPressHandler(e);
				}
			}.start();
		}

		private void keyPressHandler(KeyEvent e) {
			System.out.println(1);
			if (key != 0 || isBreakKumiPuyo) {
				System.out.println("return: " + key);
				return;
			}
			synchronized (kumiPuyo) {
				key = e.getKeyCode();
				System.out.println("keypressed");
				if (key == KeyEvent.VK_RIGHT) {
					System.out.println(key);
					PuyoMover pm = new PuyoMover();
					pm.toRight();
					pm.start();
					try {
						//pm.join();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					key = 0;
				} else if (key == KeyEvent.VK_LEFT) {
					PuyoMover pm = new PuyoMover();
					pm.toLeft();
					pm.start();
					try {
						//pm.join();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					key = 0;
				} else if (key == KeyEvent.VK_DOWN) {
					System.out.println(key);
					PuyoMover pm = new PuyoMover();
					pm.toDown();
					pm.start();
					try {
						//pm.join();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					key = 0;
				} else if (key == KeyEvent.VK_DECIMAL || key == KeyEvent.VK_A) {
					PuyoMover pm = new PuyoMover();
					pm.toRotate();
					pm.start();
					try {
						//pm.join();
					} catch (Exception ex) {
						ex.printStackTrace();
					}
					key = 0;
				}
			}
			key = 0;
		}

		public void keyReleased(KeyEvent e) {
		}

		public void keyTyped(KeyEvent e) {

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
			System.out.println("timer down");
			synchronized (kumiPuyo) {
				PuyoMover pm = new PuyoMover();
				pm.toDown();
				pm.start();
				try {
					//pm.join();
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		}
	}
}