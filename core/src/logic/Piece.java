package logic;
/**
 * A piece for a game of chess.
 * This is the abstract class for defining a chess-piece, containing all methods to be implemented or inherited.
 * @author Moritz Rehschuh
 */
public abstract class Piece {
	
	/**
	 * The {@link Player} who owns this piece.
	 */
	private Player Owner;
	/**
	 * The {@link Pos position} of this piece.
	 */
	private Pos myPosition;
	
	Piece(Player Owner, Pos position) {
		this.Owner = Owner;
		this.myPosition = position;
	}
	
	/**
	 * A method to calculate all possible moves, the piece can make at the current moment.
	 * @param advancedSearch If this {@code boolean} is set to true, the method will filter out all moves, that will put yourself in a check-situation.
	 * @return A {@link Pos}-array of all possible moves.
	 */
	public abstract Pos[] getPossibleMoves(boolean advancedSearch);
	
	/**
	 * This method will trigger any additional data-manipulations, needed after moving this piece.
	 * @param target The target-{@link Pos}, that the piece has moved to.
	 */
	protected abstract void triggerEvent(Pos target);
	
	/**
	 * @see Object#equals(Object)
	 * @param piece The piece to compare to
	 * @return {@code true} if equal, else {@code false}
	 */
	public abstract boolean equals(Piece piece);
	
	/**
	 * Returns an identical copy of this Object.
	 */
	public abstract Piece clone();
	
	/**
	 * A Getter-method, returning the {@link #Owner} of this piece.
	 * @return {@link #Owner}
	 */
	public Player getOwner() {
		return Owner;
	}
	
	/**
	 * A Getter-method, returning the {@link Pos} of this piece.
	 * @return {@link #myPosition}
	 */
	public Pos getPosition() {
		return myPosition;
	}
	
	/**
	 * An internal Setter-method to update the {@link #myPosition Pos} of this piece.
	 * @param position The new position.
	 */
	protected void updatePosition(Pos position) {
		this.myPosition = position;
	}	
	
}
