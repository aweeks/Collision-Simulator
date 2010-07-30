import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * 
 * @author Alex Weeks
 *
 */
public class Universe extends JPanel implements Runnable {

	protected ArrayList<Ball> actors = new ArrayList<Ball>();

	protected double absoluteTime = 0;
	protected double timeStep = 0.04;
	protected DoubleVector gravity;

	protected JLabel timeField;

	protected double drawXScale = 1;
	protected double drawYScale = 1;

	protected Collision nextCollision;

	//Upper and lower bounds for the region, a wall collision will be done when these are hit
	DoubleVector lowerBounds = new DoubleVector( new double[] {0,0} );
	DoubleVector upperBounds = new DoubleVector( new double[] {700, 650} );

	/**
	 * Constructs a new Universe
	 * @param gravity Gravity acceleration vector
	 */
	public Universe( DoubleVector gravity ) {

		this.gravity = gravity;

		this.timeField = new JLabel();

		this.timeField.setLocation(360, 660);
		this.timeField.setText( Double.toString(this.absoluteTime ) );

		this.timeField.setSize(100, 50);

		this.timeField.setVisible(true);
		this.timeField.setForeground(Color.WHITE);


	}

	/**
	 * Adds a new ball to the Universe
	 * @param ball The ball to add
	 */
	public void addActor( Ball ball ) {

		this.add(ball);

		ball.setLocation(ball.getX(), ball.getY());
		ball.setSize(ball.getDrawWidth(), ball.getDrawHeight());
		ball.setLayout(null);
		ball.setVisible(true);

		this.actors.add( ball );

		//With the new actor calculate the next collision and store it
		this.nextCollision = this.nextCollision();

		this.refresh();

	}

	/**
	 * Adds randomized balls to random locations in the Universe.  Checks to ensure a new ball is not placed inside another.
	 * @param num Number of balls to add
	 */
	public void addRandomActors( int num ) {
		Random rnd = new Random();

		for( int k = 0; k < num; k ++) {
			//Radius in the range 25...55
			double r = rnd.nextDouble() * 20 + 10;

			//Something reasonable for the mass (In 2D at least...)
			double m = r * r;

			double xRange = this.upperBounds.comps[0]-this.lowerBounds.comps[0];
			double yRange = this.upperBounds.comps[1]-this.lowerBounds.comps[1];

			boolean inside;
			DoubleVector pos;
			DoubleVector vel = new DoubleVector( new double[] {rnd.nextDouble() * 300 - 150, rnd.nextDouble() * 300 - 150});


			do{
				//Create a new x and y in a reasonable range (at least 20 units from the edge)
				double x = this.lowerBounds.comps[0] + rnd.nextDouble() *  (xRange - 2*r - 40) + r + 20;
				double y = this.lowerBounds.comps[1] + rnd.nextDouble() *  (yRange - 2*r - 40) + r + 20;

				pos = new DoubleVector( new double[] {x,y} );



				//Check to make sure we're not putting the ball inside another one

				inside = false;

				for( Ball ball : actors ) {
					if (ball.pos.subtract(pos).magnitude() < r + 30) {
						inside = true;
					}
				}

			} while( inside );

			Ball newBall = new Ball(m, r, pos , vel);

			this.addActor( newBall);
		}
	}

	/**
	 * Repaints the Universe
	 */
	public void refresh() {
		this.timeField.setText( Double.toString(this.absoluteTime ) );
		this.paintImmediately(0, 0, this.getWidth(), this.getHeight());
	}

	/**
	 * @return Returns a string representation of the Universe
	 */
	public String toString() {

		StringBuffer result = new StringBuffer();

		result.append( "Time: " + this.absoluteTime + "\n");

		for ( int n = 0; n < this.actors.size(); n++ ) {

			result.append("Actor " + n + ": " + this.actors.get(n).toString() + "\n");

		}

		return result.toString();
	}

	/**
	 * Runs the Universe for the specified ammount of time
	 * @param time
	 */
	public void runFor( double time ) {

		if ( time < 0 ) throw new IllegalArgumentException("Negative time specified");

		double timeRemaining = time;

		while( this.nextCollision != null ) {

			//If the next collision is within our time frame, do it
			if ( this.nextCollision.deltaT <= time  ) {

				//Update every actor's position to be the moment of the collision
				this.updatePos( nextCollision.deltaT );

				//Subtract time
				timeRemaining -= nextCollision.deltaT;

				//Perform the collision calculation, updating the velocity vectors of the objects.
				nextCollision.doCollision();

				//System.out.println("collision at t + " + this.absoluteTime + "seconds\n");
				//System.out.println(this);

				//Perform the next collision calculation
				this.nextCollision = this.nextCollision();
			}

			//If the collision is too far into the future, break
			else break;

		}

		//There are no more collisions in the time remaining, update everybody's position.
		this.updatePos(timeRemaining);

		if (this.nextCollision != null ) this.nextCollision.deltaT -= timeRemaining;

		this.refresh();

	}


	/**
	 * Integrates and updates positions of all actors without regard for collisions for deltaT time from the current absolute time.  Updates current absolute time.
	 * @param deltaT Time interval to integrate over
	 */
	public void updatePos( double deltaT ) {

		for( Ball ball : this.actors ) {

			//new position: p = p0 + v * t
			ball.pos.addTo( ball.vel.scalarMult( deltaT ) );

			//new position = p0 + 1/2 a * t^2
			ball.pos.addTo( this.gravity.scalarMult( deltaT * deltaT / 2 ) );

			//new velocity: v = v0 + a * t
			ball.vel.addTo( this.gravity.scalarMult( deltaT));

		}

		this.absoluteTime += deltaT;
	}

	/**
	 * Calculates the exact deltaT (as limited by the precision of double arithmaic) from the current absolute time to the next collision between actors, or between an actor and a wall.
	 * @return Returns a new Collision object containing the calculated deltaT and references to both colliding objects, or a single object and a wall.
	 */
	public Collision nextCollision( ) {
		//Find collision routine

		ArrayList<Collision> collisions = new ArrayList<Collision>();

		Collision check;

		for ( Ball ball1 : this. actors) {
			for ( Ball ball2 : this. actors) {

				//Don't check for collisions with self
				if ( ball1 == ball2 ) continue;

				check = Physics.checkCollision(ball1, ball2);

				if ( check != null ) {
					collisions.add(check);
				}

			}

			check = Physics.checkWallCollision(ball1, lowerBounds, upperBounds, gravity);
			if ( check != null ) {
				collisions.add(check);
			}

		}

		//If there are no collisions, return null
		if ( collisions.size() == 0 ) {
			return null;
		}


		//If there are, find the soonest one, and return it
		Collision result = collisions.get(0);

		for( Collision next : collisions ) {
			if( next.deltaT < result.deltaT ) result = next;
		}


		//System.out.println("collisions caculated");
		return result;

	}

	/**
	 * Run the simulation and display it
	 */
	public void run() {	
		boolean cont = true;		
		while ( cont ) {

			this.runFor(this.timeStep);
			try {Thread.sleep(20);} catch (InterruptedException e) {
				cont = false;
			}
		}
	}

	/**
	 * The answer to life, the Universe and everything
	 * @return Returns the answer
	 */
	public int answer() {
		return 42;
	}
}
