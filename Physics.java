import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * 
 * @author Alex Weeks
 *
 */
public class Physics {
	
	public static final double DOUBLE_THRESHOLD = 0.000000000001;
	
	
	/**
	 * Updates the velocity vectors of both actors to those the moment after a collision.  Does not check for an actual collision, will give incorrect results if called for actors that are not very close to each other.
	 * @param b1 The first ball 
	 * @param b2 The second ball
	 */
	public static void doElasticCollision( Ball b1, Ball b2 ) {
		
		double m1 = b1.mass;
		double m2 = b2.mass;
		
		//Compute the basis vector upon which all further calculations will be based.
		//The unit position vector from b1 to b2
		DoubleVector pHat = ( b2.pos.subtract(b1.pos) ).unitVector();
		
		
		//Compute initial scalar velocities of actor1 and actor2 in the new coordinate system
		double v1Init = DoubleVector.dotProduct(b1.vel, pHat);
		double v2Init = DoubleVector.dotProduct(b2.vel, pHat);
		
		//Compute the vector components of actor1 and actor2 parallel to the surface of collision
		//Will not be affected by the collision
		DoubleVector b1ParVel = b1.vel.subtract(  pHat.scalarMult( v1Init )  );
		DoubleVector b2ParVel = b2.vel.subtract(  pHat.scalarMult( v2Init )  );
		
		//Compute final scalar velocities of actor1 and actor2 in the new coordinate system
		double v1Final = (  v1Init * ( m1 - m2 ) + 2 * m2 * v2Init  ) / ( m1 + m2 );
		double v2Final = (  v2Init * ( m2 - m1 ) + 2 * m1 * v1Init  ) / ( m1 + m2 );
		
		//Put everything back together to yield the final vector velocities for both objects:
		b1.vel = b1ParVel.add(  pHat.scalarMult( v1Final )  );
		b2.vel = b2ParVel.add(  pHat.scalarMult( v2Final )  );
		

		
		
	}
	
	
	/**
	 * Performs a wall collision
	 * @param ball The ball
	 * @param compIndex Direction of the collision
	 */
	public static void doWallCollision(Ball ball, int compIndex ) {
		
		ball.vel.setComp(  compIndex, -1 * ball.vel.getComp( compIndex ) );		
	}
	
	/**
	 * 
	 * @param ball The ball
	 * @param lowerBounds Lower boundary vector
	 * @param upperBounds Upper boundary vector
	 * @param accelVec acceleration (gravity) vector of the Universe
	 * @return Returns a new Collision object
	 */
	public static Collision checkWallCollision( Ball ball, DoubleVector lowerBounds, DoubleVector upperBounds, DoubleVector accelVec ) {
		
		double r = ball.radius;
		
		ArrayList<Collision> collisions = new ArrayList<Collision>();
		
		Double lowerT = null;
		Double upperT = null;
		
		//Check collisions
		for( int n = 0; n < lowerBounds.order; n++ ) {
			double a = accelVec.getComp( n );
			double v = ball.vel.getComp( n );
			double p = ball.pos.getComp( n );
			
			double lowerBound = lowerBounds.getComp( n );
			double upperBound = upperBounds.getComp( n );
			
			
			//If the object is accelerating in this component direction, then we have a quadratic equation:
			//1/2 * t^2 + v * t + p - x = lowerBound +- r
			if ( a != 0 ) {
				lowerT = Physics.leastPositiveQuadraticSolution(0.5 * a, v, -lowerBound - r + p);
				upperT = Physics.leastPositiveQuadraticSolution(0.5 * a, v, -upperBound + r + p);
			}
			//Otherwise, if the object has no velocity, then it can never collide with a wall
			else if ( v == 0) {
				lowerT = null;
				upperT = null;
			}
			//Finally, if v is not zero and there is no acceleration, then we have a linear equation:
			// v * t + p = x +- r
			else {
				lowerT = ( lowerBound + r - p ) / v;
				upperT = ( upperBound - r - p ) / v;
				
				//System.out.println("lower: " + lowerT);
				//System.out.println("upper: " + upperT);
			}
			
			Double t = null;
			
			if (  ( lowerT != null) && ( upperT == null )  ) {
				if ( lowerT - DOUBLE_THRESHOLD > 0 ) t = lowerT;
			}
			if (  ( upperT != null) && ( lowerT == null )  ) {
				if( upperT - DOUBLE_THRESHOLD > 0 ) t = upperT;
			}
			
			if (  ( upperT != null ) && ( lowerT != null )  ) {
				
				if ( (upperT - DOUBLE_THRESHOLD < 0) && (lowerT -DOUBLE_THRESHOLD> 0) ) {
					t = lowerT;
				}
				if ( (lowerT - DOUBLE_THRESHOLD < 0) && (upperT -DOUBLE_THRESHOLD > 0) ){
					t = upperT;
				}
				if ( ( lowerT -DOUBLE_THRESHOLD> 0 ) && ( upperT -DOUBLE_THRESHOLD> 0) ) {
					
					
					if ( ( upperT < lowerT )) t = upperT;
					else t = lowerT;
				}
			}
			
			
			
			//System.out.println("t = " + t + " n = " + n);
			
			if ( ( t!= null ) && ( t - DOUBLE_THRESHOLD > 0) ) collisions.add( new Collision(ball, t.doubleValue(), n) );
			
		}
		
		if ( collisions.size() > 0 ) {
			Collision result = collisions.get(0);
						
			for ( Collision c: collisions ) {
				if ( ( c.deltaT < result.deltaT ) && ( c.deltaT  > 0 ) ) result = c;
			}			
			return result;
			
			
		}
		else return null;

	}
	
	
	/**
	 * 
	 * @param b1 The first ball
	 * @param b2 The second ball
	 * @return Returns a new Collision object containing the next collision between the balls.  Returns null if no such collision exists
	 */
	public static Collision checkCollision( Ball b1, Ball b2 ) {
		
		//Difference in position vectors
		DoubleVector deltaP = b1.pos.subtract( b2.pos );
		
		//Difference in velocity vectors
		DoubleVector deltaV = b1.vel.subtract( b2.vel );
		
		//The difference in acceleration vectors is always zero, because the acceleration field is linear and static.  See detailed explanation above.
		
		//Dot product of deltaV with itself (its magnitude squared)
		double vDotv = DoubleVector.dotProduct(deltaV, deltaV);
		
		
		//deltaV dot deltaP
		double vDotp = DoubleVector.dotProduct(deltaV, deltaP);
		
		//Dot product of deltaP with itself (its magnitude squared)
		double pDotp = DoubleVector.dotProduct(deltaP, deltaP);
		
		//Sum of the radii of the actors.  The actors will be at exactly this distance when a collision occurs.
		double radiusSum = b1.radius + b2.radius;
		
		Double t = Physics.leastPositiveQuadraticSolution( vDotv, 2 * vDotp, pDotp - radiusSum * radiusSum);
		
		if ( t != null ) return new Collision(b1, b2, t.doubleValue() );
		else return null;
		
		
	}
	
	/**
	 * 
	 * @param a
	 * @param b
	 * @param c
	 * @return Returns the smallest positive solution to a quadratic in the form a*x^2 + b*x + c = 0.  Returns null if there is no positive solution
	 */
	public static Double leastPositiveQuadraticSolution (double a, double b, double c ) {
		
		//The discriminant b^2 - 4ac
		double discriminant = b * b - 4 * a * c;
		
		//If the discriminant is negative, then there are no real solutions to the equation, return null
		if ( !(discriminant > 0) ) return null;
		
		//Otherwise, there are two solutions, by the quadratic formula:
		double x1 = (-b + Math.sqrt(discriminant) ) / ( 2 * a);
		double x2 = (-b - Math.sqrt(discriminant) ) / ( 2 * a);
		
		
		if( x1 - Physics.DOUBLE_THRESHOLD > 0 && x2 - Physics.DOUBLE_THRESHOLD > 0 ) {
			if ( x1 < x2 ) return x1;
			else return x2;
		}
		
		if ( x1 > 0 && x2 - Physics.DOUBLE_THRESHOLD < 0 ) {
			if ( x1 - Physics.DOUBLE_THRESHOLD > 0 ) return x1;
			else return null;
		}
		
		if ( x2 > 0 && x1 - Physics.DOUBLE_THRESHOLD < 0 ) {
			if ( x2 - Physics.DOUBLE_THRESHOLD > 0 ) return x2;
			else return null;
		}
		
		return null;
	}
	
	

}
