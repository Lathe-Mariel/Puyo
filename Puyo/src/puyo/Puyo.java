package puyo;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

public class Puyo extends JPanel {
	private Image image;
	private Field container;
	/**
	 * If puyo which is under this puyo, it will increment this variable.
	 */
	private int underSpace = 0;
	/**
	 * Position in the field by dot
	 */
	private int x, y;
	/**
	 * Position in the field by fram(6 x 12)
	 */
	private int frameX = 0, frameY = 0;
	private int colorNumber;
	private static Image[] puyoImageArray; //redPuyoImage, greenPuyoImage, bluePuyoImage, yellowPuyoImage, yellowGreenPuyoImage, grayPuyoImage;

	/**
	 * How many puyos are connected including this puyo.
	 */
	private int connectNumber = 1;

	static {
		try {
			puyoImageArray = new Image[7];
			puyoImageArray[0] = ImageIO.read(new File("red.png"));
			puyoImageArray[1] = ImageIO.read(new File("green.png"));
			puyoImageArray[2] = ImageIO.read(new File("blue.png"));
			puyoImageArray[3] = ImageIO.read(new File("yellow.png"));
			puyoImageArray[4] = ImageIO.read(new File("purple.png"));
			puyoImageArray[5] = ImageIO.read(new File("gray.png"));
			puyoImageArray[6] = ImageIO.read(new File("rock.png"));
		} catch (IOException e) {
			System.out.println("Error from static block, It's Puyo images loading process");
			e.printStackTrace();
		}
	}

	public Puyo(Field container, int color) {
		this(color);
		this.container = container;
	}

	public Puyo(int color) {
		this.colorNumber = color;
		this.image = puyoImageArray[color];
	}

	public int getUnderSpace() {
		return underSpace;
	}

	void increaseUnderSpace() {
		underSpace++;
	}

	void setFrameX(int x) {
		this.frameX = x;
		this.x = frameX * 50;
		setBounds(x, y, 50, 50);
	}

	int getFrameX() {
		return frameX;
	}

	void setFrameY(int y) {
		this.frameY = y;
		this.y = frameY * 50;
		setBounds(x, y, 50, 50);
		//System.out.println("Puyo position: " + this.y);
	}

	int getFrameY() {
		return frameY;
	}

	@Override
	public int getX() {
		return x;
	}

	@Override
	public int getY() {
		return y;
	}

	Image getImage() {
		return image;
	}

	void setContainer(Field container) {
		this.container = container;
	}

	public int getColorNumber() {
		return colorNumber;
	}

	/**
	 * Developer can make puyo to move for 4ways.
	 * But only one way is possible to order in one time.
	 * @param x	it can be -1,0,1
	 * @param y	it can be -1,0,1
	 */
	synchronized void moveCommand(int x, int y) {
		//System.out.println("x: " + x + "  Y: " + y);
		new PuyoMover(x, y).start();
		return;
	}

	public synchronized void downStairs() {
		System.out.println("downStairs");
		for (int roop = 0; roop < underSpace; roop++) {
			Boolean isFinish = false;
			Thread dropper = new PuyoMover();
			new Thread(dropper).start();
			;
			try {
				while (dropper.isAlive()) {
					Thread.sleep(30);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		underSpace = 0;
	}

	public Dimension getPreferredSize() {
		return new Dimension(50, 50);
	}

	public Dimension getSize() {
		return new Dimension(50, 50);
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, 50, 50);
	}

	public void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, this);
	}

	class PuyoMover extends Thread implements ActionListener {
		Timer timer;
		private int increaseX, increaseY;

		public void actionPerformed(ActionEvent e) {
			System.out.println("actionPerformed");
			//notifyAll();
		}

		private PuyoMover() {
		}

		public PuyoMover(int x, int y) {
			increaseX = x;
			increaseY = y;
		}

		public void run() {
			int incX = increaseX * 5;
			int incY = increaseY * 5;
			//System.out.println(Thread.currentThread().getName());
			for (int i = 0; i < 10; i++) {
				x += incX;
				y += incY;
				//System.out.println("posY: " + y);

				setBounds(x, y, 50, 50);
				SwingUtilities.invokeLater(new Runnable() {
					public void run() {
						container.repaint(30, x, y, 50, 50 + y);
					}
				});

				try {
					sleep(5);
				} catch (Exception e) {
					System.out.println("Puyo Dropper Sleep");
					e.printStackTrace();
				}
			}
			frameX += increaseX;
			frameY += increaseY;
			x = frameX * 50;
			y = frameY * 50;
		}
	}
}