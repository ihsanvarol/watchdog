package watchdog;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import org.opencv.core.Core;

public class ImageShow extends JPanel {

	BufferedImage image;
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		 System.out.println("Hello, OpenCV");

		    // Load the native library.
		    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		    BufferedImage img = loadImage("Images/Drop.jpg");
		    window(img, "hede", 50, 50);
	}
	
	//Load an image
	public static BufferedImage loadImage(String file) {
	    BufferedImage img;

	    try {
	        File input = new File(file);
	        img = ImageIO.read(input);

	        return img;
	    } catch (Exception e) {
	        System.out.println("erro");
	    }

	    return null;
	}
	
	@Override
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, this);
	}
	
	public ImageShow() {
    }

    public ImageShow(BufferedImage img) {
        image = img;
    }   
	
	public static void window(BufferedImage img, String text, int x, int y) {
        JFrame frame0 = new JFrame();
        frame0.getContentPane().add(new ImageShow(img));
        frame0.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame0.setTitle(text);
        frame0.setSize(img.getWidth(), img.getHeight() + 30);
        frame0.setLocation(x, y);
        frame0.setVisible(true);
    }

}