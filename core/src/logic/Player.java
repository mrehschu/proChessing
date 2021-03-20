package logic;
import java.util.ArrayList;

/**
 * This is a data-class, that stores all necessary information to define a chess-player. It consists of a name, a list of its own {@link Piece pieces} and a {@link ChessColor color}-object, 
 * that is either {@link ChessColor#BLACK black} or {@link ChessColor#WHITE white}.
 * 
 * @author Moritz Rehschuh
 */
public class Player {

	private String name;
	private ArrayList<Piece> myPieces = new ArrayList<Piece>();
	private ChessColor myColor;
	private King myKing;
	
	/**
	 * Initializes a {@link Player}-object with a name and a Player-Color.
	 * @param name A {@code String} representing the name of the player.
	 * @param color The {@link ChessColor color} of the player.
	 */
	public Player(String name, ChessColor color) {
		this.name = name;
		myColor = color;
	}
	
	/**
	 * An internal method to remove a {@link Piece} from the players {@link #myPieces piece-list}.
	 * @param piece The {@link Piece} to remove
	 * @return {@code true} if piece was successfully removed; {@code false} if otherwise
	 */
	protected boolean removePiece(Piece piece) {
		if(myPieces.remove(piece)) return true;
		else return false;
	}
	
	/**
	 * An internal method to add a {@link Piece} to the players {@link #myPieces piece-list}.
	 * @param piece The {@link Piece} to add
	 */
	protected void addPiece(Piece piece) {
		myPieces.add(piece);
		if(piece.getClass() == King.class) myKing = (King) piece;
	}
	
	/**
	 * An internal method to update the entire {@link #myPieces piece-list} of the player according to the current state of the {@link Gamelogic#chessboard chessboard}.
	 */
	protected void updatePieces() {
		myPieces.clear();
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 8; x++) {
				if(Gamelogic.chessboard[x][y] != null && Gamelogic.chessboard[x][y].getOwner().equals(this)) addPiece(Gamelogic.chessboard[x][y]);
			}
		}
	}
	
	/**
	 * A Getter-method, returning the name of the player.
	 * @return The name of the player
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * A Getter-method, returning the color of the player.
	 * @return The {@link ChessColor color} of the player.
	 */
	public ChessColor getColor() {
		return myColor;
	}
	
	/**
	 * A Getter-method, returning an array of all pieces belonging to the player. 
	 * @return An array containing all {@link Piece pieces}, that belong to this player.
	 */
	public Piece[] getPieces() {
		return myPieces.toArray(new Piece[0]);
	}
	
	/**
	 * A Getter-method, returning the king of this player.
	 * @return The {@link King} of this player.
	 */
	public King findKing() {
		return myKing;
	}
	
	/**
	 * An alternative equals()-method directly for other {@link Player}-objects.
	 * @param other the reference {@link Player} to which to compare to
	 * @see #equals(Object)
	 * @return {@code true} if this object is the same as the obj argument; {@code false} otherwise.
	 */
	public boolean equals(Player other) {
		if(other == null) return false;
		if(name.equals(other.name) && myColor.equals(other.myColor)) return true;
		else return false;
	}
	
	/**
	 * @see #equals(Player)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj == null) return false;
		if(obj.getClass() == Player.class) return equals((Player) obj);
		else return false;
	}
}
