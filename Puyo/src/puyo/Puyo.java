package puyo;

import java.awt.Component;
import java.awt.Container;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

public class Puyo extends Component {
	private Image image;
	private Container container;
	private int x, y;
	private int colorNumber;
	private static Image[] puyoImageArray; //redPuyoImage, greenPuyoImage, bluePuyoImage, yellowPuyoImage, yellowGreenPuyoImage, grayPuyoImage;

	/**
	 * How many puyos are connected including this puyo.
	 */
	private int connectNumber;

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

public Puyo(Container container, int color) {
	this.image = image;
	this.container = container;
	this.colorNumber = color;
}
public Puyo(int color) {
	this.colorNumber = color;
}

void setContainer(Container container) {
	this.container = container;
}

public int getColorNumber() {
	return colorNumber;
}
public void downStairs() {

}
}