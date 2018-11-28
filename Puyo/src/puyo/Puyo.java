package puyo;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;

public class Puyo extends Component {
	private Image image;
	private Field container;
	/**
	 * When puyo which is under this puyo desappears, it will increment this variable.
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
	/**
	 * this number refers this puyo's color.
	 */
	private int colorNumber;
	private static Image[] puyoImageArray; //redPuyoImage, greenPuyoImage, bluePuyoImage, yellowPuyoImage, yellowGreenPuyoImage, grayPuyoImage;
	private LinkedPuyos linkedPuyos = null;

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
		setBounds(0, 0, 50, 50);
	}

	public Puyo(int color) {
		this.colorNumber = color;
		this.image = puyoImageArray[color];
	}

	void setLink(LinkedPuyos linkedPuyos) {
		this.linkedPuyos = linkedPuyos;
	}

	LinkedPuyos getLink() {
		return linkedPuyos;
	}

	void connectPuyos(Puyo second) {
		if (linkedPuyos == null && second.getLink() == null) {
			linkedPuyos = new LinkedPuyos(this);
			second.setLink(linkedPuyos.add(second));
		} else if (linkedPuyos == null) {
			if (second.getLink().puyos.contains(this)) {
				linkedPuyos = second.getLink();
				return;
			}
			linkedPuyos = second.getLink().add(this);
		} else if (second.getLink() == null) {
			linkedPuyos.add(second);
			second.setLink(linkedPuyos);
		} else {
			System.out.println("puyoLink junction");
			if(linkedPuyos == second.getLink())return;
			Iterator<Puyo> i = linkedPuyos.iterator();
			LinkedPuyos escape = linkedPuyos;
			for (; i.hasNext();) {
				Puyo processPuyo = i.next();
				processPuyo.setLink(second.getLink().add(processPuyo));
			}
			LinkedPuyos.master.remove(escape);
		}
	}
	
	void disConnectPuyos() {
		if(linkedPuyos == null)return;
		if(linkedPuyos.puyos.size() > 2) {
			linkedPuyos.remove(this);
			linkedPuyos = null;
		}else {
			LinkedPuyos.master.remove(linkedPuyos);
			LinkedPuyos link = linkedPuyos;
			link.puyos.get(0).setLink(null);
			link.puyos.get(1).setLink(null);
		}
	}
	public void resetUnderSpace() {
		underSpace = 0;
	}
	public int getUnderSpace() {
		return underSpace;
	}

	void increaseUnderSpace() {
		underSpace++;
		System.out.println("Puyo X: " + frameX + "  Y: " + frameY + "  underSapce: " + underSpace);
	}

	public Field getField() {
		return container;
	}

	public void setStartFrame(int frameX, int frameY) {
		this.frameX = frameX;
		this.frameY = frameY;
		this.x = frameX * 50;
		this.y = frameY * 50;
		setBounds(x, y, 50, 50);
	}
/**
 * Position set by dot
 * @param x	X position should be described by dot
 * @param y	Y position should be described by dot
 */
	void setPara(int x, int y) {
		this.x = x;
		this.y = y;
		setBounds(x, y, 50, 50);
	}

	void setFrameX(int x) {
		this.frameX = x;
	}

	int getFrameX() {
		return frameX;
	}

	void setFrameY(int y) {
		this.frameY = y;
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

	public Dimension getPreferredSize() {
		return new Dimension(50, 50);
	}

	public Dimension getSize() {
		return new Dimension(50, 50);
	}

	public Rectangle getBounds() {
		return new Rectangle(x, y, 50, 50);
	}

	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
		if(linkedPuyos != null)
			g.drawString(linkedPuyos.puyos.size()+"", 10,10);
	}

	public void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, getWidth(), getHeight(), this);
	}

}