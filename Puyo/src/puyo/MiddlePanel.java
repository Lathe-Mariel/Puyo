package puyo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

public class MiddlePanel extends JPanel {
	BufferedImage imageArray[];
	int imageNumber;
	String message = "";
	int sleepTime;
	BufferedImage image;

	public MiddlePanel() {
		imageNumber = -1;
		try {
			imageArray = new BufferedImage[7];
			imageArray[0] = ImageIO.read(new File("java.jpg"));
			imageArray[1] = ImageIO.read(new File("sutenaide.png"));
			imageArray[2] = ImageIO.read(new File("batan.png"));
			imageArray[3] = ImageIO.read(new File("hakubishi.png"));
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
		if(imageNumber == -1) {
			message = "";
			return;
		}
		//Image pic = imageArray[imageNumber];
		g.drawImage(image, 73, 50, getWidth() * 2 / 3, getHeight() * 2 / 3, this);
		g.setFont(new Font("MS Gothic", Font.BOLD, 84));
		g.setColor(Color.black);
		g.drawString(message, 172, 355);
	}

	public void showImage(int iNumber, String message, int sleep) {
		this.imageNumber = iNumber;
		this.message = message;
		this.sleepTime = sleep;
		image = imageArray[iNumber];

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

	public void gameOver() {
		imageNumber = 0;
		BufferedImage originalImage = imageArray[3];
int i =0;
int j=1;
		while (true) {
			boolean  highEnd, lowEnd;
			i += j;
			int angle = i * -2;

			BufferedImage newImage = new BufferedImage(originalImage.getWidth()+100, originalImage.getHeight()+50, BufferedImage.TYPE_INT_ARGB);
			Graphics2D gra = (Graphics2D)newImage.getGraphics();
			gra.rotate(angle * Math.PI / 180, 40+ originalImage.getWidth() / 2.0, 20+originalImage.getHeight() / 2.0);
			gra.drawImage(originalImage, 0, 0, this);

			Graphics2D gra2 = (Graphics2D)newImage.getGraphics();
			gra2.setColor(new Color(0x55, 0x55, 0xFF));
			gra2.setFont(new Font("Serif", Font.BOLD, 86));
			gra2.drawString("ばたん", 0,400);
			gra2.drawString("きゅ～", 100,480);
			image = newImage;
			repaint();
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}
			if(i > 100) {
				j= -1;
			}
			if(i < 60) {
				j=1;
			}
			
		}
	}
}
