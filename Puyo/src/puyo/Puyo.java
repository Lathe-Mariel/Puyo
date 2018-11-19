package puyo;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

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
	private int posX, posY;
	/**
	 * Position in the field by fram(6 x 12)
	 */
	private int frameX=0, frameY=0;
	private int colorNumber;
	private static Image[] puyoImageArray; //redPuyoImage, greenPuyoImage, bluePuyoImage, yellowPuyoImage, yellowGreenPuyoImage, grayPuyoImage;

	/**
	 * How many puyos are connected including this puyo.
	 */
	private int connectNumber = 1;

	static {
		try {
			puyoImageArray = new Image[6];
			puyoImageArray[0] = ImageIO.read(new File("red.jpg"));
			puyoImageArray[1] = ImageIO.read(new File("green.jpg"));
			puyoImageArray[2] = ImageIO.read(new File("blue.jpg"));
			puyoImageArray[3] = ImageIO.read(new File("yellow.jpg"));
			puyoImageArray[4] = ImageIO.read(new File("yellowGreen.jpg"));
			puyoImageArray[5] = ImageIO.read(new File("gray.jpg"));
		} catch (IOException e) {
			System.out.println("Error from static block, It's Puyo images loading process");
			e.printStackTrace();
		}
	}

	public Puyo(Field container, int color) {
		this.image = puyoImageArray[color];
		this.container = container;
		this.colorNumber = color;
	}

	public Puyo(int color) {
		this.colorNumber = color;
	}

	public int getUnderSpace() {
		return underSpace;
	}

	void increaseUnderSpace() {
		underSpace++;
	}

	void setFrameX(int x) {
		this.frameX = x;
		posX = frameX *50;
	}
	int getFrameX() {
		return frameX;
	}
	void setFrameY(int y) {
		this.frameY = y;
		posY = frameY *50;
	}
	int getFrameY(){
		return frameY;
	}
	void setContainer(Field container) {
		this.container = container;
	}

	public int getColorNumber() {
		return colorNumber;
	}

	public boolean naturalDrop() {
		if (container.isTherePuyo(frameX, frameY + 1) || frameY == 12)
			return false;
		SwingUtilities.invokeLater(new PuyoDropper());
		return true;
	}

	public void downStairs() {
		System.out.println("downStairs");
		for (int roop = 0; roop < underSpace; roop++) {
			Boolean isFinish = false;
			Thread dropper = new PuyoDropper();
			SwingUtilities.invokeLater(dropper);
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
		return new Dimension(50,50);
	}
	public Rectangle getBounds() {
		return new Rectangle(posX, posY, 50, 50);
	}
	
	public void paintComponent(Graphics g) {
		g.drawImage(image, 0,  0, this);
	}

	class PuyoDropper extends Thread {

		public PuyoDropper() {
		}

		//repaint(long tm, int x, int y, int width, int height)
		public void run() {
			System.out.println(Thread.currentThread().getName());
			for (int i = 0; i < 10; i++) {
				posY += 5;
				System.out.println("posY: " + posY);
				//container.repaint(30, posX, posY, 50, 50 + interY);
				container.repaint();
				try {
					sleep(30);
				} catch (Exception e) {
					System.out.println("Puyo Dropper Sleep");
					e.printStackTrace();
				}
			}
			frameY++;
			posY = frameY *50;
		}
	}
}