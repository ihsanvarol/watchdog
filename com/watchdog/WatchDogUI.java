package watchdog;

import java.awt.Component;
import java.awt.EventQueue;
import java.awt.Graphics;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import java.awt.BorderLayout;
import java.beans.PropertyVetoException;
import java.io.File;

import javax.swing.JDesktopPane;
import javax.swing.JInternalFrame;
import javax.swing.border.TitledBorder;
import javax.swing.JToggleButton;

import java.awt.FlowLayout;
import java.awt.image.BufferedImage;

import javax.swing.BoxLayout;
import javax.swing.JTree;
import javax.swing.event.TreeSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.JButton;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.JSlider;

import java.awt.event.InputMethodListener;
import java.awt.event.InputMethodEvent;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;

public class WatchDogUI {

	private JFrame frame;
	private JTree tree;
	private JSlider slider;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {

		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					WatchDogUI window = new WatchDogUI();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				} catch (Throwable e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 * 
	 * @throws Throwable
	 */
	public WatchDogUI() throws Throwable {
		initialize();
	}

	/**
	 * Initialize the contents of the frame.
	 * 
	 * @throws Throwable
	 */
	private void initialize() throws Throwable {
		frame = new JFrame();
		frame.setBounds(100, 100, 450, 300);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));

		File f = new File("Images");
		File[] fl = f.listFiles();

		tree = new JTree(fl);
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			public void valueChanged(TreeSelectionEvent arg0) {
				String selectedFile = arg0.getPath().getLastPathComponent()
						.toString();
				BufferedImage img = loadImage(selectedFile);
				JPanel panel = new MyPanel(img);
				frame.getContentPane().add(panel, BorderLayout.CENTER);
			}
		});
		frame.getContentPane().add(tree, BorderLayout.WEST);

		JButton btnNewButton = new JButton("New button");
		btnNewButton.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent arg0) {
				System.out.println("Button Clicked");
			}
		});
		frame.getContentPane().add(btnNewButton, BorderLayout.SOUTH);

		slider = new JSlider();
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				JSlider source = (JSlider) arg0.getSource();
				if (!source.getValueIsAdjusting()) {
					int brightness = (int) source.getValue();
					int s = frame.getContentPane().getComponentCount();
					if (s>2)
					{
						MyPanel c = (MyPanel) frame.getContentPane()
								.getComponent(3);
						if (c == null)
							return;
						c.changeBrightness(brightness);
						c.repaint();	
					}
					
				}
			}
		});
		slider.setValue(0);
		slider.setMinimum(-100);
		slider.setPaintTicks(true);
		slider.setPaintLabels(true);
		frame.getContentPane().add(slider, BorderLayout.NORTH);
	}

	public JTree getTree() {
		return tree;
	}

	// Load an image
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
}

class MyPanel extends JPanel {
	BufferedImage image;
	int x;
	int y;

	public MyPanel(BufferedImage img) {
		image = img;
		y = image.getHeight();
		x = image.getWidth();
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	public void paint(Graphics g) {
		g.drawImage(image, 0, 0, this);
	}

	public void changeBrightness(int x) {
		Mat original = img2Mat(image);
		Mat destination = new Mat(original.rows(), original.cols(),
				CvType.CV_8UC3);
		float alpha = 1.0f;
		int beta = x; // brightness, negative for darkening(?)
		original.convertTo(destination, CvType.CV_8UC3, alpha, beta);
		image = mat2Img(destination);
	}

	public BufferedImage mat2Img(Mat in) {
		BufferedImage out;
		byte[] data = new byte[this.x * this.y * (int) in.elemSize()];
		int type;
		in.get(0, 0, data);

		if (in.channels() == 1)
			type = BufferedImage.TYPE_BYTE_GRAY;
		else
			type = BufferedImage.TYPE_3BYTE_BGR;

		out = new BufferedImage(this.x, this.y, type);

		out.getRaster().setDataElements(0, 0, this.x, this.y, data);
		return out;
	}

	public Mat img2Mat(BufferedImage in) {
		Mat out;
		byte[] data;
		int r, g, b;

		if (in.getType() == BufferedImage.TYPE_INT_RGB) {
			out = new Mat(this.y, this.x, CvType.CV_8UC3);
			data = new byte[this.x * this.y * (int) out.elemSize()];
			int[] dataBuff = in.getRGB(0, 0, this.x, this.y, null, 0, this.x);
			for (int i = 0; i < dataBuff.length; i++) {
				data[i * 3] = (byte) ((dataBuff[i] >> 16) & 0xFF);
				data[i * 3 + 1] = (byte) ((dataBuff[i] >> 8) & 0xFF);
				data[i * 3 + 2] = (byte) ((dataBuff[i] >> 0) & 0xFF);
			}
		} else {
			out = new Mat(this.y, this.x, CvType.CV_8UC1);
			data = new byte[this.x * this.y * (int) out.elemSize()];
			int[] dataBuff = in.getRGB(0, 0, this.x, this.y, null, 0, this.x);
			for (int i = 0; i < dataBuff.length; i++) {
				r = (byte) ((dataBuff[i] >> 16) & 0xFF);
				g = (byte) ((dataBuff[i] >> 8) & 0xFF);
				b = (byte) ((dataBuff[i] >> 0) & 0xFF);
				data[i] = (byte) ((0.21 * r) + (0.71 * g) + (0.07 * b)); // luminosity
			}
		}
		out.put(0, 0, data);
		return out;
	}
}
