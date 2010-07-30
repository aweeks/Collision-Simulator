/**
 * 
 * @author Alex Weeks
 *
 */
public class Collision {

	public double deltaT;

	public final Ball ball1;

	public final Ball ball2;

	public final boolean isWallCollision;
	public final int wallCompIndex;

	/**
	 * Creates a new ball-to-ball collision object
	 * @param actor1 The first ball
	 * @param actor2 The second ball
	 * @param deltaT Time to the collision
	 */
	public Collision(Ball actor1, Ball actor2, double deltaT) {
		this.ball1 = actor1;
		this.ball2 = actor2;
		this.deltaT = deltaT;

		this.isWallCollision = false;
		this.wallCompIndex = 0;

	}

	/**
	 * Creates a new wall collision object
	 * @param ball The ball
	 * @param deltaT Time to the collision
	 * @param wallCompIndex Direction of collision
	 */
	public Collision( Ball ball, double deltaT, int wallCompIndex ) {
		this.ball1 = ball;
		this.ball2 = null;

		this.deltaT = deltaT;
		this.isWallCollision = true;
		this.wallCompIndex = wallCompIndex;
	}

	/**
	 * Performs the collision
	 */
	public void doCollision() {
		if( this.isWallCollision ) {
			Physics.doWallCollision(ball1, wallCompIndex);
		}
		else Physics.doElasticCollision( ball1, ball2 );
	}


}
