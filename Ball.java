import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JComponent;

/**
 * 
 * @author Alex Weeks
 *
 */
public class Ball extends JComponent {
	
	public final double mass;
	public final double radius;
	
	protected Color color = Color.BLACK;
	
	protected DoubleVector pos;
	protected DoubleVector vel;
	
	
	protected static double drawXScale = 1;
	protected static double drawYScale = 1;
	
	/**
	 * 
	 * @param mass Mass of the ball
	 * @param radius Radius of the ball
	 * @param initPos Initial position of the ball
	 * @param initVel Initial velocity of the ball
	 */
	public Ball( double mass, double radius, DoubleVector initPos, DoubleVector initVel ) {
		this.mass = mass;
		this.radius = radius;
		
		this.pos = initPos;
		this.vel = initVel;
		
	}
	
	/**
	 * 
	 * @param mass Mass of the ball
	 * @param radius Radius of the ball
	 * @param initPosComps Initial position of the ball
	 * @param initVelComps Initial velocity of the ball
	 */
	public Ball( double mass, double radius, double[] initPosComps, double[] initVelComps ) {
		this.mass = mass;
		this.radius = radius;
		
		this.pos = new DoubleVector( initPosComps );
		this.vel = new DoubleVector( initVelComps );
		
	}
	
	/**
	 * @return Returns a string representation of the ball
	 */
	public String toString() {
		StringBuffer result = new StringBuffer();
		
		result.append("m = " + this.mass + "  ");
		result.append("r = " + this.radius + "  ");
		
		result.append("pos: " + this.pos.toString() + "  ");
		result.append("vel: " + this.vel.toString() + "  ");
		
		return result.toString();
	}
	
	/**
	 * Paints the ball
	 */
	public void paintComponent(Graphics g) {
		g.setColor( this.color );
		
		g.fillOval(0, 0, this.getDrawWidth(), this.getDrawHeight());
	}
	
	/**
	 * @return Returns the x-coordinate of the upper left corner of a minimum bounding box for the ball given its drawXScale.  Used to position the ball in a GUI.
	 */
	public int getX() {		
		return (int) (this.pos.comps[0] * drawXScale - this.radius );
	}
	
	/**
	 * @return Returns the y-coordinate of the upper left corner of a minimum bounding box for the ball given its drawYScale.  Used to position the ball in a GUI.
	 */
	public int getY() {
		return (int) (this.pos.comps[1] * drawYScale - this.radius );
	}
	
	/**
	 * 
	 * @return Returns the width in pixels of the ball given its drawXScale
	 */
	public int getDrawWidth() {		
		return (int) (this.radius * 2 * drawXScale) + 1;
	}
	
	/**
	 * 
	 * @return Returns the height in pixels of the ball given its drawYScale
	 */
	public int getDrawHeight() {
		return (int) (this.radius * 2 * drawYScale) + 1;
	}
	

}
