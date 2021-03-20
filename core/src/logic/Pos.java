package logic;
/**
 * This class is a simple way to express a position in a two-dimensional array. It consists of an x- and an y-coordinate.
 * 
 * @author Moritz Rehschuh
 */
public class Pos {
	
	/**
	 * The x-coordinate of this position
	 */
	public int x;
	/**
	 * The y-coordinate of this position
	 */
	public int y;
	
	
	/**
	 * Initializes a newly created Pos-object (position-object) with an x- and an y-coordinate.
	 * @param x The x-coordinate of this position
	 * @param y The y-coordinate of this position
	 */
	public Pos(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	/**
	 * An alternative equals()-method directly for other {@link Pos}-objects.
	 * @param other the reference {@link Pos} to which to compare to
	 * @see #equals(Object)
	 * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
	 */
	public boolean equals(Pos other) {
		if(other == null) return false;
		if(x == other.x && y == other.y) return true;
		else return false;
	}
	
	/**
	 * @see #equals(Pos)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj.getClass() == Pos.class) return equals((Pos) obj);
		else return false;
	}
	
	@Override
	public String toString() {
		return new String("(" + x + ", " + y + ")");
	}
}