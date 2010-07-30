import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;

/**
 * 
 * @author Alex Weeks
 *
 */
public class MyGui extends JFrame {
	// put your instance fields here

	private Universe uni;

	private JButton pauseButton = new JButton();
	private JButton speedUp = new JButton();
	private JButton slowDown = new JButton();
	private JButton changeColors = new JButton();
	private JButton resetColors = new JButton();
	private JButton addBall = new JButton();
	private JButton toggleGravity = new JButton();
	private Font defaultFont = new Font("Arial", 0, 12);

	Thread universeThread;

	/**
	 * Constructor for the GUI
	 */
	public MyGui() {
		setLayout(null);  // throw away the layout manager for this container
		setSize(720,800); //change the size if you want
		setLocation(20,40); //change the location if you want
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setTitle("Elastic Collision Simulation");

		//Initialize universe
		uni = new Universe( new DoubleVector( new double[] {0, 0} ) );
		uni.setSize(700, 650);
		uni.setLocation(10, 10);
		uni.setBackground(Color.GRAY);
		uni.setVisible(true);
		uni.setLayout(null);
		this.getContentPane().add(uni);

		//Pause button
		pauseButton.setSize(150, 30);
		pauseButton.setLocation(30, 670);
		pauseButton.setText("Start / Stop");
		pauseButton.setVisible(true);
		pauseButton.addActionListener(new Pause());

		pauseButton.setFont(defaultFont);
		this.getContentPane().add(pauseButton);

		//Speed up button
		speedUp.setSize(150, 30);
		speedUp.setLocation(200, 670);
		speedUp.setText("Speed Up");
		speedUp.setVisible(true);
		speedUp.addActionListener(new SpeedUp());

		speedUp.setFont(defaultFont);
		this.getContentPane().add(speedUp);

		//Slow down button
		slowDown.setSize(150, 30);
		slowDown.setLocation(350, 670);
		slowDown.setText("Slow Down");
		slowDown.setVisible(true);
		slowDown.addActionListener(new SlowDown());

		slowDown.setFont(defaultFont);
		this.getContentPane().add(slowDown);

		//Change colors button
		changeColors.setSize(200, 30);
		changeColors.setLocation(30, 700);
		changeColors.setText("Randomize Colors");
		changeColors.setVisible(true);
		changeColors.addActionListener(new ChangeColors());

		changeColors.setFont(defaultFont);
		this.getContentPane().add(changeColors);

		//Reset colors button
		resetColors.setSize(200, 30);
		resetColors.setLocation(30, 730);
		resetColors.setText("Reset Colors");
		resetColors.setVisible(true);
		resetColors.addActionListener(new ResetColors());

		resetColors.setFont(defaultFont);
		this.getContentPane().add(resetColors);

		//Add ball button
		addBall.setSize(200, 30);
		addBall.setLocation(250, 700);
		addBall.setText("Add a Ball");
		addBall.setVisible(true);
		addBall.addActionListener(new AddBall());

		addBall.setFont(defaultFont);
		this.getContentPane().add(addBall);


		//Toggle gravity button
		toggleGravity.setSize(200, 30);
		toggleGravity.setLocation(500, 700);
		toggleGravity.setText("Gravity on/off");
		toggleGravity.setVisible(true);
		toggleGravity.addActionListener(new ToggleGravity());

		toggleGravity.setFont(defaultFont);
		this.getContentPane().add(toggleGravity);
		
		//Initialize starting random balls
		uni.addRandomActors(7); 

		JOptionPane.showMessageDialog(this,
				"Please note:  There are several known issues with this simulation, and I do not \n" +
				"consider it complete from a simulation accuracy, or reliability standpoint.  Known issues include: \n\n" +
				"-Increasing the speed excessively causes objects to be lost\n" +
				"-Adding an excessive number of balls both increases the simulation complexity, and increases the\n" +
				"chances of objects becoming stuck within other objects, despite the great deal of effort\n" +
				"I have put into preventing this.\n" +
				"-Occasionally, even at normal speeds, and with a reasonable number of balls present,\n" +
				"particularly when adding balls, a ball will be lost out of a wall or become stuck in another object.\n\n" +
				"The majority of these issues stem from the unusual method of simulation:  This program does not operate\n" +
				"iteratively.  It solves for moments of collision exactly, finds the soonest collision, performs the collision, and moves on. \n" +
				"In particular, collisions involving more than two objects are not implemented, and if two objects should collide\n" +
				"within a very short amount of time, one usually ends up inside another. \n\n" +
				"Given more time, these problems could probably be solved, but alas the deadline is approaching...\n\n" +
		"If the simulation freaks out, re-launching the program is the most sure way to solve it.  Have fun :)");

		universeThread = new Thread(uni);
		universeThread.start();

	}

	/**
	 * Implenents start/stop action
	 *
	 */
	public class Pause implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			if ( universeThread == null ) {
				universeThread = new Thread(uni);
				universeThread.start();
			} else {
				universeThread.interrupt();
				universeThread = null;
			}

		}

	}

	/**
	 * 
	 * Speeds up simulation
	 *
	 */
	public class SpeedUp implements ActionListener {

		public void actionPerformed(ActionEvent e) {

			if ( universeThread != null ) universeThread.interrupt();
			uni.timeStep *= 1.25;			
			universeThread = new Thread(uni);
			universeThread.start();
		}

	}

	/**
	 * Slows down simulation
	 *
	 */
	public class SlowDown implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if ( universeThread != null ) universeThread.interrupt();
			uni.timeStep *= 4d/5d;
			universeThread = new Thread(uni);
			universeThread.start();
		}
	}

	/**
	 * Randomizes colors of all balls
	 *
	 */
	public class ChangeColors implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Random rnd = new Random();

			for ( Ball ball: uni.actors) {
				ball.color = new Color( rnd.nextInt(255), rnd.nextInt(255), rnd.nextInt(255) );
			}

			uni.paintImmediately(0, 0, uni.getWidth(), uni.getWidth());

		}
	}

	/**
	 * Resets all ball colors to black
	 */
	public class ResetColors implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			Random rnd = new Random();

			for ( Ball ball: uni.actors) {
				ball.color = Color.BLACK;
			}

			uni.paintImmediately(0, 0, uni.getWidth(), uni.getWidth());

		}
	}

	/**
	 * Adds a randomized ball
	 */
	public class AddBall implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if ( universeThread != null ) universeThread.interrupt();
			uni.addRandomActors(1);			
			universeThread = new Thread(uni);
			universeThread.start();

		}
	}

	/**
	 * Toggles gravity for the universe
	 */
	public class ToggleGravity implements ActionListener {
		public void actionPerformed(ActionEvent e) {
			if ( universeThread != null ) universeThread.interrupt();
			if ( uni.gravity.magnitude() == 0 ) {
				uni.gravity = new DoubleVector( new double[] {0, 400} );
			} else {
				uni.gravity.scalarMultTo(0);
			}

			uni.nextCollision = uni.nextCollision();

			universeThread = new Thread(uni);
			universeThread.start();

		}
	}

}

