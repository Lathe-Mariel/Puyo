package puyo;

import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class MiddlePanel extends JPanel {
	Image imageArray[];
	int imageNumber;

	public MiddlePanel() {
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
		System.out.println("Error from constracta block, It's middle images loading process");
		e.printStackTrace();
	}}

	public void paintComponent(Graphics g) {
		g.setColor(new java.awt.Color(0xF0,0xFF,0xFF));
		Image pic = imageArray[imageNumber];
		g.drawImage(pic, 70, 0, pic.getWidth(this), pic.getHeight(this), this);
	}

	public void selectImageNumber(int number) {
		imageNumber = number;
	}
	
	public void showImage() {
		
	}
}
