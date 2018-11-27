package puyo;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
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
		//Image pic = imageArray[imageNumber];
		g.drawImage(image, 85, 50, getWidth() * 2 / 3, getHeight() * 2 / 3, this);
		g.setFont(new Font("MS Gothic", Font.BOLD, 84));
		g.setColor(Color.black);
		g.drawString(message, 172, 305);
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
		BufferedImage originalImage = imageArray[3];
		
		for (int i = 0; i < 90; i++) {
			int angle = i * -2;
			
			BufferedImage newImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
			Graphics2D gra = (Graphics2D)newImage.getGraphics();
			gra.rotate(angle * Math.PI / 180, originalImage.getWidth() / 2.0, originalImage.getHeight() / 2.0);
			gra.drawImage(originalImage, 0, 0, this);
			
			Graphics2D gra2 = (Graphics2D)newImage.getGraphics();
			gra2.setColor(new Color(0x55, 0xFF, 0xFF));
			gra2.setFont(new Font("Serif", Font.BOLD, 80));
			gra2.drawString("ばたん", 0,400);
			gra2.drawString("きゅ～", 100,480);
			image = newImage;
			repaint();
			try {
				Thread.sleep(10);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	private BufferedImage angle(BufferedImage image, int angle) {
		int w = image.getWidth();
		int h = image.getHeight();

		Point diag1 = rotate(new Point(w, h), angle);
		Point diag2 = rotate(new Point(-w, h), angle);
		int width = Math.max(Math.abs(diag1.x), Math.abs(diag2.x));
		int height = Math.max(Math.abs(diag1.y), Math.abs(diag2.y));

		double cx = (w * Math.cos(angle) - h * Math.sin(angle)) / 2.0;
		double cy = (w * Math.sin(angle) + h * Math.cos(angle)) / 2.0;

		AffineTransform af = new AffineTransform();
		double dx = width / 2.0 - cx;
		double dy = height / 2.0 - cy;
		af.setToTranslation(dx, dy);
		af.rotate(angle);

		BufferedImage rotated = new BufferedImage(width, height, image.getType());
		AffineTransformOp op = new AffineTransformOp(af, AffineTransformOp.TYPE_BICUBIC);
		op.filter(image, rotated);
		return rotated;
	}

	public static Point rotate(Point point, double angle) {
		double th = Math.atan2(point.getY(), point.getX());
		double norm = Math.sqrt(point.getX() * point.getX() + point.getY() * point.getY());
		double x = norm * Math.cos(th - angle);
		double y = norm * Math.sin(th - angle);
		return new Point((int) Math.round(x), (int) Math.round(y));
	}
}
