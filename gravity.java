import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

public class TestGravity extends JPanel implements ActionListener, KeyListener, MouseMotionListener{

	// Constants 
	private static final double gravity = 20;  
	private static final double jump = 70.0;  
	private static final double time = 0.1;   
	private Rectangle2D player, platform, platform1, platformCheck, pLeft, pRight;
	private BufferedImage img, rotatedImage; 
	private ImageIcon DrawImg; 


	private int mXPos, mYPos; 

	// Variables
	private double xPos, yPos, yVel, gunX, gunY; 
	private boolean airBorne, move, left, right; 
	private JFrame frame = new JFrame();

	// Game Timer 
	private Timer t, gunTimer; 

	// Camera position
	private double x = 0;
	private double y = 0;

	// Camera velocity
	private double vx = 0;
	private double vy = 0;

	public static void main(String[] args) throws IOException {
		new TestGravity(); 
	}

	public TestGravity() throws IOException { 

		img = ImageIO.read(new File("gun (1).png")); 

		// Start Timer 
		t = new Timer(8, this);
		gunTimer = new Timer(0, this); 

		gunTimer.start();
		t.start();

		// (xPos, yPos)
		xPos = 100;
		yPos = 100; 

		gunX = xPos + 30; 
		gunY = yPos + 12.5;

		player = new Rectangle2D.Double(xPos, yPos, 25, 25);

		// (x, y, width, height)
		platform = new Rectangle2D.Double(102.5, 300, 97.5, 25);
		pLeft = new Rectangle2D.Double(102.5, 300, 5, 25); 

		platformCheck = new Rectangle2D.Double(105, 300, 92.5, 10); 

		platform1 = new Rectangle2D.Double(0, 400, 1000, 1000);

		airBorne = true; 
		move = true; 

		double angle = Math.atan2(mYPos - gunY, mXPos - gunX);
		double angleDegrees = Math.toDegrees(angle); 
		
		left = false; right = false; 

		addKeyListener(this);
		setFocusable(true);
		requestFocus();
		
		AffineTransform transform = AffineTransform.getRotateInstance(angleDegrees, img.getWidth() / 2, img.getHeight() / 2);

		AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);

		rotatedImage = op.filter(img, null);

		DrawImg = new ImageIcon(rotatedImage);

		// Create JFrame 
		frame.setContentPane(this);
		frame.setSize(600, 600); 
		frame.setTitle("Testing Gravity");
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);

	}
	public void mouseDragged(MouseEvent e) {}

	public void mouseMoved(MouseEvent e) {
		
		mXPos = e.getX(); 
		mYPos = e.getY();
		
	}
	
	public void keyTyped(KeyEvent e) {}
	
	public void keyPressed(KeyEvent e) 
	{
		// Left 
		if (e.getKeyCode() == KeyEvent.VK_LEFT) 
		{
			left = true;

		}
		// Right 
		else if (e.getKeyCode() == KeyEvent.VK_RIGHT) 
		{ 
			right = true;   
		}
		// Up

		if (e.getKeyCode() == KeyEvent.VK_UP) 
		{
			if (!airBorne) 
			{
				vy = yVel; 
				yVel -= jump; 
				airBorne = true; 
			}

		}
	}
	
	public void keyReleased(KeyEvent e) 
	{
		left= false; 
		right = false; 
		vx = 0; 
		vy = 0; 
	}

	public void actionPerformed(ActionEvent e) 
	{

		if (e.getSource() == t) 
		{ 	
			if (left)
				xPos -= 5; 
			else if (right)
				xPos += 5; 


			updateCamera(); 

			// Updating Values
			yPos += yVel * time;
			yVel += gravity * time;

			// Updating Player
			player = new Rectangle2D.Double(xPos, yPos, 50, 50);

			// Check if player intersects platform  
			if (player.intersects(platformCheck)) 
			{	

				if (yPos > 250) {
					yPos = 250; 
					airBorne = false; 
					yVel = 0; 
				}
			}

			else if (player.intersects(platform)) {
				yVel = 10; 
				airBorne = true; 
			}

			else if (player.intersects(platform1)) 
			{
				airBorne = false; 
				yVel = 0;
			}
		}
		else if (e.getSource() == gunTimer) {
			gunX = xPos + 30; 
			gunY = yPos + 12.5;
			
			double angle = Math.atan2(mYPos - gunY, mXPos - gunX);
			double angleDegrees = Math.toDegrees(angle); 

			AffineTransform transform = AffineTransform.getRotateInstance(angleDegrees, img.getWidth() / 2, img.getHeight() / 2);

			AffineTransformOp op = new AffineTransformOp(transform, AffineTransformOp.TYPE_BILINEAR);
			
			rotatedImage = op.filter(img, null);

			DrawImg = new ImageIcon(rotatedImage);
		}

		repaint(); 

	}
	public void paint(Graphics g) {
		
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		
		g2.translate((int) -x, (int) -y);
		
		g2.setColor(Color.red);
		g2.fill(player);

		g2.setColor(Color.blue);
		g2.fill(platform);
		g2.fill(platform1);

		g2.drawImage(DrawImg.getImage(), (int)gunX, (int)gunY, this);

	}

	public void updateCamera() 
	{
		x += vx;
		y = yPos - 325; 
	}

}
