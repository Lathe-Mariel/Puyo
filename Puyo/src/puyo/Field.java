package puyo;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
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
import javax.swing.JFrame;
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
	boolean kumiPuyoBroke = false;
	private boolean keyInputDisable = false;
	/**
	 * Key input polling time interval
	 */
	long keyCheckIntervalTime = 65;
	ArrayList<Puyo> droppedPuyos;
	Image[] imageArray;
	JFrame container;
	MiddlePanel middlePanel;
	TopPanel topPanel;
	int[] rensaBonus = {0,8,16,32,64,96,128,160,192,224,256,288,320,352,384,416,448,480,520};//連鎖
	int[] sameBonus = {0,3,6,12,24};//同時消し
	int[] numberBonus= {0,2,3,4,5,6,7,10};//個数more than 4

	int rensa;

	public Field(JFrame container) {
		this.container = container;
		setLayout(null);
		droppedPuyos = new ArrayList<Puyo>();
		puyoArray = new Puyo[8][16];//including brim

		setBackground(Color.orange);

		try {
			imageArray = new Image[7];
			imageArray[0] = ImageIO.read(new File("java.jpg"));
			imageArray[1] = ImageIO.read(new File("sutenaide.jpg"));
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
		topPanel = new TopPanel();
		topPanel.setSize(new Dimension(400, 150));
		add(topPanel);
		setComponentZOrder(topPanel, 0);

		middlePanel = new MiddlePanel();
		middlePanel.setBounds(0, 151, 400, 420);
		add(middlePanel);
		setComponentZOrder(middlePanel, 0);

		listener = new PuyoKeyListener();
		container.addKeyListener(listener);
	}

	KeyListener listener;

	public void gameRoop() {
		rensa = 0;
		//System.out.println("gameRoop");
		while (processDisappearing()) {
			rensa++;
			middlePanel.showImage(1, rensa + "", 750);
			repaint();
			try {
				Thread.sleep(250);
			} catch (Exception ex0) {
				ex0.printStackTrace();
			}

			Thread t = new Thread() {
				public void run() {
					processAllDown();
				}
			};
			t.start();
			try {
				t.join();
			} catch (Exception e) {
				e.printStackTrace();
			}

			for (Iterator<Puyo> i = droppedPuyos.iterator(); i.hasNext();) {
				surveyLinkedPuyos(i.next());
			}
			droppedPuyos.clear();

			try {
				Thread.sleep(300);
			} catch (Exception ex0) {
				ex0.printStackTrace();
			}
			rensa++;
		}

		if (puyoArray[3][3] != null) {
			keyInputDisable = true;
			//middlePanel.showImage(2, "ばたんきゅ～～", 30000);
			middlePanel.gameOver();
			System.out.println("Game Over");
		} else {
			//System.out.println(1);
			kumiPuyo = npp.pop();
			puyo0Movable = true;
			puyo1Movable = true;
			add(kumiPuyo[0]);
			add(kumiPuyo[1]);
			createNewTimer(1000 - topPanel.getScore()/20);
			keyInputDisable = false;
		}
	}

	public void setNPP(NextPuyoPanel npp) {
		this.npp = npp;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		g.drawImage(imageArray[0], 102, 250, 200, 273, this);
	}

	public Dimension getPreferredSize() {
		return new Dimension(400, 850);
	}

	public Rectangle getBounds() {
		return new Rectangle(400, 850, 0, 0);
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
		kumiPuyoDownTimer = new Timer(interval, (ActionListener) listener);
		kumiPuyoDownTimer.start();
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
			if (puyo == null)continue;			
			puyo.increaseUnderSpace();
			if(puyoArray[x+1][i] != null)
				puyo.disConnectPuyos(puyoArray[x+1][i]);
			if(puyoArray[x-1][i] != null)
				puyo.disConnectPuyos(puyoArray[x-1][i]);
		}
	}

	/**
	 * Disappearing if exists more than four linked puyos.
	*/
	public boolean processDisappearing() {
		int sameDisappearing =0;
		boolean hasDisappear = false;
		System.out.println("processDisapeearing()");
		for (int i = 0; i < LinkedPuyos.master.size(); i++) {
			LinkedPuyos currentLink = LinkedPuyos.master.get(i);
			if (currentLink.isDisappearable()) {
				int puyoNumber = currentLink.puyos.size();
				int number = (puyoNumber-4)>10?10:puyoNumber -4;
				int bonus = rensaBonus[rensa] + sameBonus[sameDisappearing] + numberBonus[number];
				int bonusRate = bonus == 0?1:bonus;
				topPanel.addScore(puyoNumber*bonusRate);
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
					//p.disConnectPuyos(puyoArray[disappearX + 1][disappearY]);
					//p.disConnectPuyos(puyoArray[disappearX - 1][disappearY]);

					//System.out.println("Disappearing " + p.getFrameX() + " : " + p.getFrameY());
				}
				currentLink = null;
				sameDisappearing++;
			}
		}
		//repaint();
		return hasDisappear;
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

	}

	boolean puyo0Movable = true;
	boolean puyo1Movable = true;

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
			if (keyInputDisable || (puyo0Movable == false && puyo1Movable == false)) {
				return;
			}
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
				if (puyo0Movable && puyo1Movable) {
					increaseY1 = 1;
					increaseY2 = 1;
				} else if (puyo1Movable) {
					keyInputDisable = true;
					while (puyoArray[frameX2][++frameY2] == null) {
						kumiPuyo[1].increaseUnderSpace();
					}
					frameY2--;
					new PuyoDropper2(kumiPuyo[1], null).run();
					puyo1Movable = false;

				} else {
					keyInputDisable = true;
					while (puyoArray[frameX1][++frameY1] == null) {
						kumiPuyo[0].increaseUnderSpace();
					}
					frameY1--;
					new PuyoDropper2(kumiPuyo[0], null).run();
					puyo0Movable = false;
				}
			}
			if (puyo0Movable == false) {

				puyoArray[frameX1][frameY1] = kumiPuyo[0];
				surveyLinkedPuyos(kumiPuyo[0]);

				//kumiPuyoBroke = true;
				//System.out.println("kumiPuyo[0] -> puyoArray:X " + frameX1 + " :Y " +frameY1);
			}
			if (puyo1Movable == false) {
				puyoArray[frameX2][frameY2] = kumiPuyo[1];
				surveyLinkedPuyos(kumiPuyo[1]);
				//kumiPuyoBroke = true;
				//System.out.println("kumiPuyo[1] -> puyoArray");
			}
			if (puyo0Movable == false && puyo1Movable == false) {
				System.out.println(3);
				kumiPuyoDownTimer.stop();
				keyInputDisable = true;
				gameRoop();
			}
		}

		void toReverse() {
			if (frameY1 < frameY2) {
				if (puyoArray[frameX2 + 1][frameY2 - 1] == null) {
					increaseX2 = 1;
					increaseY2 = -1;
				}
			} else if (frameY1 > frameY2) {
				if (puyoArray[frameX2 - 1][frameY2 + 1] == null) {
					increaseX2 = -1;
					increaseY2 = 1;
				}
			} else if (frameX1 < frameX2) {
				if (puyoArray[frameX2 - 1][frameY2 - 1] == null) {
					increaseX2 = -1;
					increaseY2 = -1;
				}
			} else if (frameX1 > frameX2) {
				if (puyoArray[frameX2 + 1][frameY2 + 1] == null) {
					increaseX2 = 1;
					increaseY2 = 1;
				}
			}
			allKeyLock = false;
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
			allKeyLock = false;
		}

		Puyo[] tempKumiPuyo = new Puyo[2];
		int x1, x2, y1, y2;

		public void run() {
			if (increaseX2 == 0 && increaseY2 == 0 && increaseY1 == 0) {
				//System.out.println("run return");
				allKeyLock = false;
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
			allKeyLock = false;
		}
	}

	boolean allKeyLock;

	public class PuyoKeyListener implements KeyListener, ActionListener {
		private boolean keyState[];
		private boolean doubleRotationLock;

		public PuyoKeyListener() {
			keyState = new boolean[6]; //left,right,down,rotate,autoDown, reverse-rotate
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
			case KeyEvent.VK_S:
				keyState[5] = true;
				break;
			default:
				break;
			}
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
			case KeyEvent.VK_S:
				keyState[5] = false;
				doubleRotationLock = false;
				break;
			default:
				break;
			}
		}

		private void keyPressHandler() {
			if (keyInputDisable)
				return;
			synchronized (kumiPuyo) {
				if (allKeyLock)
					return;
				allKeyLock = true;
				if (keyState[4]) {
					PuyoMover pm = new PuyoMover();
					pm.toDown();
					pm.start();
					keyState[4] = false;
				} else if (keyState[3]) {
					if (doubleRotationLock) {
						allKeyLock = false;
						return;
					}
					PuyoMover pm = new PuyoMover();
					doubleRotationLock = true;
					pm.toRotate();
					pm.start();
				} else if (keyState[5]) {
					if (doubleRotationLock) {
						allKeyLock = false;
						return;
					}
					PuyoMover pm = new PuyoMover();
					doubleRotationLock = true;
					pm.toReverse();
					pm.start();
				} else if (keyState[1]) {
					PuyoMover pm = new PuyoMover();
					pm.toRight();
					pm.start();
				} else if (keyState[0]) {
					PuyoMover pm = new PuyoMover();
					pm.toLeft();
					pm.start();
				} else if (keyState[2]) {
					PuyoMover pm = new PuyoMover();
					pm.toDown();
					pm.start();

				} else {
					allKeyLock = false;
				}
			}
			//keyProcess = false;
		}

		public void actionPerformed(ActionEvent e) {
			//System.out.println("timer down");
			keyState[4] = true;
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
					sleep(13);
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
			if (latch == null)
				return;
			latch.countDown();
		}
	}
}
