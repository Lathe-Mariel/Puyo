package puyo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.TimerTask;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;
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
	private boolean keyProcess = false;
	long keyCheckIntervalTime = 60;
	ArrayList<Puyo> droppedPuyos;
	Image[] imageArray;

	public Field() {
		setLayout(null);
		droppedPuyos = new ArrayList<Puyo>();
		puyoArray = new Puyo[8][16];//including brim

		setBackground(Color.orange);
		
		try {
			imageArray = new Image[7];
			imageArray[0] = ImageIO.read(new File("java.jpg"));
			imageArray[1] = ImageIO.read(new File("green.png"));
			imageArray[2] = ImageIO.read(new File("blue.png"));
			imageArray[3] = ImageIO.read(new File("yellow.png"));
			imageArray[4] = ImageIO.read(new File("purple.png"));
			imageArray[5] = ImageIO.read(new File("gray.png"));
			imageArray[6] = ImageIO.read(new File("rock.png"));
		} catch (IOException e) {
			System.out.println("Error from static block, It's Puyo images loading process");
			e.printStackTrace();
		}
	}

	void init() {
		for (int i = 0; i < 8; i++) {
			puyoArray[i][15] = new Puyo(6);
			puyoArray[i][15].setContainer(this);
			puyoArray[i][15].setStartFrame(i, 15);
			add(puyoArray[i][15]);
		}
		for (int i = 1; i < 15; i++) {
			puyoArray[0][i] = new Puyo(6);
			puyoArray[7][i] = new Puyo(6);
			puyoArray[0][i].setContainer(this);
			puyoArray[7][i].setContainer(this);
			puyoArray[0][i].setStartFrame(0, i);
			puyoArray[7][i].setStartFrame(7, i);
			add(puyoArray[0][i]);
			add(puyoArray[7][i]);
		}
		JPanel topPanel = new TopPanel();
		topPanel.setSize(new Dimension(400,150));
		add(topPanel);
		setComponentZOrder(topPanel, 0);
	}

	public void setNPP(NextPuyoPanel npp) {
		this.npp = npp;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(imageArray[0], 102, 250, 200, 273, this);
	}

	public boolean isOptimizedDrawingEnabled() {
        return false;
    }


	boolean isThereNoPuyo(int frameX, int frameY) {
		//System.out.println("frameX: " + frameX + "    frameY: " + frameY);
		if (puyoArray[frameX][frameY] == null) {
			return true;
		}
		return false;
	}

	private void createNewTimer(int interval) {
		if (kumiPuyoDownTimer != null) {
			kumiPuyoDownTimer.stop();
		}
		kumiPuyoDownTimer = new Timer(interval, new PuyoListener());
		kumiPuyoDownTimer.start();
	}

	void startNewPuyo() {
		if (puyoArray[3][3] != null) {
			keyProcess = true;
			System.out.println("Game Over");
			return;
		}
		kumiPuyo = npp.pop();
		puyo0Movable = true;
		puyo1Movable = true;
		add(kumiPuyo[0]);
		System.out.println("kumiPuyo0 posY: " + kumiPuyo[0].getY());
		add(kumiPuyo[1]);
		System.out.println("kumiPuyo1 posY: " + kumiPuyo[1].getY());
		createNewTimer(800);
	}

	private synchronized void surveyLinkedPuyos(Puyo puyo) {
		int currentX = puyo.getFrameX();
		int currentY = puyo.getFrameY();

		Puyo rightPuyo = puyoArray[currentX + 1][currentY];
		if (rightPuyo != null && puyo.getColorNumber() == rightPuyo.getColorNumber()) {
			System.out.println("right");
			puyo.connectPuyos(rightPuyo);
		}

		Puyo leftPuyo = puyoArray[currentX - 1][currentY];
		if (leftPuyo != null && puyo.getColorNumber() == leftPuyo.getColorNumber()) {
			System.out.println("left");
			puyo.connectPuyos(leftPuyo);
		}

		Puyo lowerPuyo = puyoArray[currentX][currentY + 1];
		if (lowerPuyo != null && puyo.getColorNumber() == lowerPuyo.getColorNumber()) {
			System.out.println("lower");
			puyo.connectPuyos(lowerPuyo);
		}

		Puyo upperPuyo = puyoArray[currentX][currentY - 1];
		if (upperPuyo != null && puyo.getColorNumber() == upperPuyo.getColorNumber()) {
			System.out.println("upper");
			puyo.connectPuyos(upperPuyo);
		}
	}

	private void notifyDisappeared(Puyo disappearingPuyo) {
		int x = disappearingPuyo.getFrameX();
		for (int i = disappearingPuyo.getFrameY() - 1; i > 0; i--) {
			Puyo puyo = puyoArray[x][i];
			if (puyo == null)
				continue;
			puyo.increaseUnderSpace();
		}
	}

	/**
	 * Disappearing if exists more than four linked puyos.
	*/
	public void processDisappearing() {
		boolean hasDisappear = false;
		do {
			hasDisappear = false;
			System.out.println("processDisapeearing()");
			for (int i = 0; i < LinkedPuyos.master.size(); i++) {
				LinkedPuyos currentLink = LinkedPuyos.master.get(i);
				if (currentLink.isDisappearable()) {
					hasDisappear = true;
					LinkedPuyos.master.remove(currentLink);
					i--;
					//System.out.println("disappearing process");
					for (Iterator<Puyo> j = currentLink.iterator(); j.hasNext();) {
						Puyo p = j.next();
						int disappearX = p.getFrameX();
						int disappearY = p.getFrameY();
						remove(puyoArray[disappearX][disappearY]);
						puyoArray[disappearX][disappearY] = null;
						notifyDisappeared(p);
						p.disConnectPuyos(puyoArray[disappearX + 1][disappearY]);
						p.disConnectPuyos(puyoArray[disappearX - 1][disappearY]);

						//System.out.println("Disappearing " + p.getFrameX() + " : " + p.getFrameY());
					}
					currentLink = null;
				}
			}
			repaint();
			if (!hasDisappear) {
				startNewPuyo();
				return;
			}

			Thread t = new Thread() {
				public void run() {
					processAllDown();
				}
			};
			t.start();
			try{
			t.join();
			}catch(Exception e) {e.printStackTrace();}

		} while (true);
	}

	/**
	 * After disappearing, spaces which were created by disappearing process need to be processed.
	 */
	public void processAllDown() {
		System.out.println("processAllDown");

		for (int i = 14; i > 0; i--) {
			for (int j = 1; j < 7; j++) {
				Puyo puyo = puyoArray[j][i];
				if (puyo == null || puyo.getUnderSpace() == 0)
					continue;
				droppedPuyos.add(puyo);
				puyoArray[j][i] = null;
				puyoArray[j][puyo.getFrameY() + puyo.getUnderSpace()] = puyo;
			}
		}
		ExecutorService executor = Executors.newCachedThreadPool();
		CountDownLatch latch = new CountDownLatch(droppedPuyos.size());
		for (Iterator<Puyo> i = droppedPuyos.iterator(); i.hasNext();) {
			new PuyoDropper2(i.next(), latch).start();
		}
		try {
			latch.await();
			System.gc();
		} catch (Exception e) {
			e.printStackTrace();
		}
		executor.shutdown();
		for (Iterator<Puyo> i = droppedPuyos.iterator(); i.hasNext();) {
			surveyLinkedPuyos(i.next());
		}
		droppedPuyos.clear();

	}

	public Dimension getPreferredSize() {
		return new Dimension(400, 850);
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
				if (puyoArray[frameX1][frameY1] == null) {
					puyoArray[frameX1][frameY1] = kumiPuyo[0];
					surveyLinkedPuyos(kumiPuyo[0]);
				}
				//kumiPuyoBroke = true;
				//System.out.println("kumiPuyo[0] -> puyoArray:X " + frameX1 + " :Y " +frameY1);
			}
			if (puyo1Movable == false) {
				if (puyoArray[frameX2][frameY2] == null) {
					puyoArray[frameX2][frameY2] = kumiPuyo[1];
					surveyLinkedPuyos(kumiPuyo[1]);
				}
				//kumiPuyoBroke = true;
				//System.out.println("kumiPuyo[1] -> puyoArray");
			}
			if (puyo0Movable == false && puyo1Movable == false) {
				inProcessKeyEvent = false;
				kumiPuyoDownTimer.stop();
				isBreakKumiPuyo = false;

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
				//System.out.println("run return");
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
				tempKumiPuyo = kumiPuyo;//copy kumiPuyo to protect data, since another thread can destroy the data in key input judge process.
			}

			for (int i = 0; i < 10; i++) {
				try {
					sleep(oneStepIntervalTime);
				} catch (Exception e) {
					System.out.println("Puyo Dropper Sleep");
					e.printStackTrace();
				}
				//System.out.println("x1: " + x1 + ",  y1: " + y1);
				x1 += incX1;
				y1 += incY1;
				x2 += incX2;
				y2 += incY2;
				//System.out.println("x1: " + x1 + ",  y1: " + y1);

				SwingUtilities.invokeLater(new Runnable() {

					public void run() {
						tempKumiPuyo[0].setPara(x1, y1);
						tempKumiPuyo[1].setPara(x2, y2);

						repaint(5, x1, y1, 50, 50);
						repaint(5, x2, y2, 50, 50);
					}
				});

			}

		}
	}

	public class PuyoKeyListener implements KeyListener {
		private boolean keyState[];
		private boolean doubleRotationLock;

		public PuyoKeyListener() {
			keyState = new boolean[4]; //left,right,down,rotate
			new java.util.Timer().schedule(new TimerTask() {
				public void run() {
					keyPressHandler();
				}
			}, 500, keyCheckIntervalTime);
		}

		@Override
		public void keyPressed(KeyEvent e) {
			int key = e.getKeyCode();
			switch (key) {
			case KeyEvent.VK_RIGHT:
				keyState[1] = true;
				break;
			case KeyEvent.VK_LEFT:
				keyState[0] = true;
				break;
			case KeyEvent.VK_DOWN:
				keyState[2] = true;
				break;
			case KeyEvent.VK_DECIMAL:
				keyState[3] = true;
				break;
			case KeyEvent.VK_A:
				keyState[3] = true;
				break;
			default:
				break;
			}
			//keyPressHandler(e);
		}

		@Override
		public void keyReleased(KeyEvent e) {
			int key = e.getKeyCode();
			switch (key) {
			case KeyEvent.VK_RIGHT:
				keyState[1] = false;
				break;
			case KeyEvent.VK_LEFT:
				keyState[0] = false;
				break;
			case KeyEvent.VK_DOWN:
				keyState[2] = false;
				break;
			case KeyEvent.VK_DECIMAL:
				keyState[3] = false;
				doubleRotationLock = false;
				break;
			case KeyEvent.VK_A:
				keyState[3] = false;
				doubleRotationLock = false;
				break;
			default:
				break;
			}
		}

		private void keyPressHandler() {
			//System.out.println("keyPressHandler");
			if (keyProcess || isBreakKumiPuyo) {
				//System.out.println("return: 1");
				return;
			}
			//keyProcess = false;
			synchronized (kumiPuyo) {
				if (keyState[1]) {
					PuyoMover pm = new PuyoMover();
					pm.toRight();
					pm.start();
					//	keyProcess = false;
				} else if (keyState[0]) {
					PuyoMover pm = new PuyoMover();
					pm.toLeft();
					pm.start();
					//	keyProcess = false;
				} else if (keyState[2]) {
					PuyoMover pm = new PuyoMover();
					pm.toDown();
					pm.start();
					//	keyProcess = false;
				} else if (keyState[3]) {
					if (doubleRotationLock)
						return;
					PuyoMover pm = new PuyoMover();
					doubleRotationLock = true;
					pm.toRotate();
					pm.start();
					//	keyProcess = false;
				}
			}
			//keyProcess = false;
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

	}

	class PuyoDropper2 extends Thread {
		Puyo puyo;
		int x, y;
		CountDownLatch latch;

		PuyoDropper2(Puyo puyo, CountDownLatch latch) {
			this.puyo = puyo;
			this.latch = latch;
		}

		public void run() {

			int originY = puyo.getFrameY();
			int steps = puyo.getUnderSpace();

			x = puyo.getFrameX() * 50;
			y = originY * 50;

			for (int i = 0; i < 10 * steps; i++) {
				try {
					sleep(20);
				} catch (Exception e) {
					System.out.println("all Puyo Dropper Sleep");
					e.printStackTrace();
				}

				y += 5;
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						puyo.setPara(x, y);
						repaint(5, x, y, 50, 50);
						//System.out.println("x: " + x + "  y: " + y);
					}
				});

			}
			puyo.resetUnderSpace();
			puyo.setFrameY(originY + steps);
			latch.countDown();
		}
	}

	class PuyoListener implements ActionListener {
		//boolean puyo0Movable = true, puyo1Movable = true;
		public void actionPerformed(ActionEvent e) {
			//System.out.println("timer down");
			synchronized (kumiPuyo) {
				PuyoMover pm = new PuyoMover();
				pm.toDown();
				pm.start();
			}
		}
	}
}