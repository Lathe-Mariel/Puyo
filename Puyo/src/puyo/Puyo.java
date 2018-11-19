package puyo;

import java.awt.Component;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

public class Puyo extends Component {
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
	private int frameX, frameY;
	private int colorNumber;
	private static Image[] puyoImageArray; //redPuyoImage, greenPuyoImage, bluePuyoImage, yellowPuyoImage, yellowGreenPuyoImage, grayPuyoImage;

	/**
	 * How many puyos are connected including this puyo.
	 */
	private int connectNumber = 1;

	static {
		try {
			puyoImageArray = new Image[6];
			puyoImageArray[0] = ImageIO.read(new File("red.png"));
			puyoImageArray[1] = ImageIO.read(new File("green.png"));
			puyoImageArray[2] = ImageIO.read(new File("blue.png"));
			puyoImageArray[3] = ImageIO.read(new File("yellow.png"));
			puyoImageArray[4] = ImageIO.read(new File("yellowGreen.png"));
			puyoImageArray[5] = ImageIO.read(new File("gray.png"));
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

	int getFrameX() {
		return frameX;
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
		if (container.isTherePuyo(frameX, frameY + 1))
			return false;
		SwingUtilities.invokeLater(new PuyoDropper());
		return true;
	}

	public void downStairs() {
		for (int roop = 0; roop < underSpace; roop++) {
			Boolean isFinish = false;
			Thread dropper = new PuyoDropper();
			SwingUtilities.invokeLater(dropper);
			try {
				while (dropper.isAlive()) {
					Thread.sleep(50);
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		underSpace = 0;
	}

	class PuyoDropper extends Thread {
		private int interY;

		public PuyoDropper() {
		}

		//repaint(long tm, int x, int y, int width, int height)
		public void run() {
			for (int interY = 0; interY < 50; interY += 5) {
				repaint(50, posX, posY, posX + 50, posY + interY);
				try {
					sleep(50);
				} catch (Exception e) {
					System.out.println("Puyo Dropper Sleep");
					e.printStackTrace();
				}
			}
			frameY--;
		}
	}
}