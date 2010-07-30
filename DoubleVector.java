
/**
 * 
 * @author Alex Weeks
 *
 */
public class DoubleVector {

	protected int order;

	protected double[] comps;

	/**
	 * Creates a new DoubleVector with specified order
	 * @param order
	 */
	public DoubleVector( int order ){

		this.order = order;
	}

	/**
	 * Creates a new DoubleVector with specified components
	 * @param initialComps
	 */
	public DoubleVector( double[] initialComps ) {

		this.comps = initialComps.clone();

		this.order = comps.length;	
	}

	/**
	 * @return Returns a string representation of the vector
	 */
	public String toString() {

		StringBuffer result = new StringBuffer("{ ");

		for ( double comp : this.comps ) {

			result.append( comp + ", ");

		}
		result.delete( result.length() - 2 , result.length());

		result.append(" }");

		return result.toString();

	}

	/**
	 * 
	 * @param index
	 * @return Returns the vector component at the index specified
	 */
	public double getComp( int index ) {
		return this.comps[index];
	}

	/**
	 * Sets the vector component specified by index to the given value
	 * @param index
	 * @param value Value to set
	 */
	public void setComp( int index, double value ) {	
		this.comps[index] = value;
	}

	/**
	 * Adds DoubleVectors
	 * @param other DoubleVector to add.  Must be of the same order.
	 * @return Returns a new DoubleVector who's components are the sum of this vector's components, and the other vector's components.
	 * @throws Throws IllegalArgumentException if the orders do not match.
	 */
	public DoubleVector add( DoubleVector other ) {

		DoubleVector result = new DoubleVector( this.comps );

		result.addCompsTo( other.comps );

		return result;

	}

	/**
	 * 
	 * @param comps Components to add
	 * @return Returns a new DoubleVector who's components are the sum of this vector's components, and the given components.
	 */
	public DoubleVector addComps( double[] comps ) {

		DoubleVector result = new DoubleVector( this.comps );

		result.addCompsTo( comps );

		return result;
	}


	/**
	 * Adds the components of other to this vector's components.  The vector's orders must match.
	 * @param other Vector to add
	 * @throws Throws IllegalArgumentException if the orders do not match.
	 * 
	 */
	public void addTo( DoubleVector other ) {
		this.addCompsTo( other.comps );
	}

	/**
	 * Adds the given components to this vector's components
	 * @param comps Components to add
	 * @throws IllegalArgumentException if the orders do not match.
	 */
	public void addCompsTo( double[] comps ) {
		if( this.order != comps.length ) {
			throw new IllegalArgumentException("Orders do not match");
		}

		for( int n = 0; n < this.order; n++ ) {
			this.comps[n] = this.comps[n] + comps[n];
		}
	}

	/**
	 * Subtracts vectors
	 * @param other Vector to subtract
	 * @return Returns a new DoubleVector who's components are this vector's components minus the other vector's components
	 */
	public DoubleVector subtract( DoubleVector other ) {

		DoubleVector result = new DoubleVector( this.comps );

		result.subtractCompsTo( other.comps );

		return result;
	}

	/**
	 * Subtracts vectors
	 * @param comps Components to subtract
	 * @return Returns a new DoubleVector who's components are this vector's components minus the given components
	 */
	public DoubleVector subtractComps( double[] comps ) {

		DoubleVector result = new DoubleVector( this.comps );

		result.subtractCompsTo( comps );

		return result;
	}

	/**
	 * Subtracts the other vectors components from this vector's components
	 * @param other Vector to subtract
	 */
	public void subtractTo( DoubleVector other ) {
		this.subtractCompsTo( other.comps );
	}

	/**
	 * Subtracts components from this vector's components
	 * @param comps Components to subtract
	 */
	public void subtractCompsTo( double[] comps ) {
		if( this.order != comps.length ) {
			throw new IllegalArgumentException("Orders do not match");
		}

		for( int n = 0; n < this.order; n++ ) {
			this.comps[n] = this.comps[n] - comps[n];
		}
	}

	/**
	 * 
	 * @param scalar Scalar multiple
	 * @return Returns a new DoubleVector of same order as this DoubleVector who's components are the scalar product of this and scalar.
	 */
	public DoubleVector scalarMult ( double scalar ) {
		DoubleVector result = new DoubleVector( this.comps );

		result.scalarMultTo(scalar);

		return result;

	}

	/**
	 * Multiples each component of this vector by the scalar.
	 * @param scalar
	 */
	public void scalarMultTo ( double scalar ) {
		for( int n = 0; n < this.order; n++ ) {
			this.comps[n] *= scalar;
		}
	}

	/**
	 * 
	 * @return Returns the magnitude of the vector.
	 */
	public double magnitude() {
		return Math.sqrt( this.squareSumOfComps() );
	}

	/**
	 * 
	 * @return Returns the sum of the components squared.  Useful in place of the magnitude in many equations when you wish to avoid using a square root.
	 */
	public double squareSumOfComps() {
		double result = 0;

		for ( int n = 0; n < this.comps.length; n++ ) {
			result += this.comps[n] * this.comps[n];
		}

		return result;
	}

	/**
	 * 
	 * @return Returns a new DoubleVector of magnitude 1 in the direction of this vector.
	 */
	public DoubleVector unitVector() {
		DoubleVector result = new DoubleVector(this.comps);

		result.scalarMultTo( 1d / result.magnitude() );

		return result;
	}



	/**
	 * 
	 * @param vec1 First vector
	 * @param vec2 Second vector
	 * @return Returns the dot product of vec1 and vec2
	 * @throws Throws IllegalArgumentException if the orders do not match.
	 * 
	 */
	public static double dotProduct ( DoubleVector vec1, DoubleVector vec2 ) {
		if( vec1.order != vec2.order ) {
			throw new IllegalArgumentException("Orders do not match");
		}

		double result = 0;

		for ( int n = 0; n < vec1.order; n++ ) {
			result += vec1.comps[n] * vec2.comps[n];
		}

		return result;	
	}

}
