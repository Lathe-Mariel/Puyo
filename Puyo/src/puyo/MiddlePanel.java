package puyo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class MiddlePanel extends JPanel {
	Image imageArray[];
	int imageNumber;
	String step ="";
	int sleepTime;

	public MiddlePanel() {
		imageNumber = -1;
		try {
			imageArray = new Image[7];
			imageArray[0] = ImageIO.read(new File("java.jpg"));
			imageArray[1] = ImageIO.read(new File("sutenaide.png"));
			imageArray[2] = ImageIO.read(new File("batan.png"));
			imageArray[3] = ImageIO.read(new File("yellow.png"));
			imageArray[4] = ImageIO.read(new File("purple.png"));
			imageArray[5] = ImageIO.read(new File("gray.png"));
			imageArray[6] = ImageIO.read(new File("rock.png"));
		} catch (IOException e) {
			System.out.println("Error from constracta block, It's middle images loading process");
			e.printStackTrace();
		}
	}

	public boolean isOpaque() {
		return false;
	}

	public void paintComponent(Graphics g) {
		if(imageNumber == -1)return;
		Image pic = imageArray[imageNumber];
		g.drawImage(pic, 74, 50, pic.getWidth(this), pic.getHeight(this), this);
		g.setFont(new Font("MS Gothic", Font.BOLD, 84));
		g.setColor(Color.black);
		g.drawString(step,  172, 335);
	}

	public void showImage(int iNumber, String step, int sleep) {
		this.imageNumber = iNumber;
		this.step = step;
		this.sleepTime = sleep;

		new Thread() {
			public void run() {
				repaint();
				try {
					Thread.sleep(sleepTime);
				} catch (Exception e) {
					e.printStackTrace();
				}
				imageNumber = -1;
				repaint();
			}
		}.start();
	}
}
