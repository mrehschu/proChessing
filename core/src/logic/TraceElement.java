package logic;

import java.util.ArrayList;

/**
 * A TraceElement stores all relevant data to recreate or undo a move (a change to the data-model), made in the game.
 * @author Moritz Rehschuh
 */
public class TraceElement {

	private Pos targetPosition;
	private Piece originalPiece;
	private Piece deadPiece;
	private int RemisCounter;
	private int playerIndex;
	private ArrayList<Pawn> pawns;
	private Piece enPassantPiece;
	private Piece rochadePiece;
	private Pos rochadeTargetPosition;
	private String pawnUpgradeType;
	
	/**
	 * The Standart-Constructor for a TraceElement.
	 * @param oldPos The original {@link Pos} of the piece moved
	 * @param newPos The new {@link Pos} of the piece moved
	 */
	protected TraceElement(Pos oldPos, Pos newPos) {
		targetPosition = newPos;
		originalPiece = Gamelogic.chessboard[oldPos.x][oldPos.y].clone();
		deadPiece = (Gamelogic.chessboard[newPos.x][newPos.y] != null ? Gamelogic.chessboard[newPos.x][newPos.y].clone() : null);
		RemisCounter = Gamelogic.getRemisCounter();
		playerIndex = Gamelogic.getCurrentPlayerIndex();
		
		pawns = new ArrayList<Pawn>();
		for(Piece piece: Gamelogic.getCurrentPlayer().getPieces()) if(piece.getClass() == Pawn.class) pawns.add((Pawn) piece.clone());
		for(Piece piece: Gamelogic.getOtherPlayer().getPieces()) if(piece.getClass() == Pawn.class) pawns.add((Pawn) piece.clone());
	}
	
	/**
	 * In case of an EnPassant, use this method to add further information to the TraceElement
	 * @param piece The slain piece
	 */
	protected void addEnPassantInfo(Piece piece) {
		enPassantPiece = piece.clone();
	}
	
	/**
	 * In case of an Rochade, use this method to add further information to the TraceElement
	 * @param oldPos The old {@link Pos} of the moves Rook
	 * @param newPos The new {@link Pos} of the moved Rook
	 */
	protected void addRochadeInfo(Pos oldPos, Pos newPos) {
		rochadePiece = Gamelogic.chessboard[oldPos.x][oldPos.y].clone();
		rochadeTargetPosition = newPos;
	}
	
	/**
	 * In case of an Promotion, use this method to add further information to the TraceElement
	 * @param type A {@code String} to mark the chosen upgrade
	 */
	protected void addPawnUpgradeInfo(String type) {
		pawnUpgradeType = type;
	}
	
	/**
	 * A Getter-method
	 * @return The old {@link Pos} of the moved piece
	 */
	public Pos getOldPosition() {
		return originalPiece.getPosition();
	}
	
	/**
	 * A Getter-method
	 * @return The new {@link Pos} of the moved piece
	 */
	public Pos getNewPosition() {
		return targetPosition;
	}
	
	/**
	 * A Getter-method
	 * @return A cloned instance of the original piece
	 */
	public Piece getOriginalPiece() {
		return originalPiece;
	}
	
	/**
	 * A Getter-method
	 * @return Either a cloned instance of the slain piece or {@code null}
	 */
	public Piece getDeadPiece() {
		return deadPiece;
	}
	
	/**
	 * A Getter-method
	 * @return The saved state of the {@link Gamelogic#getRemisCounter() RemisCounter}
	 */
	public int getRemisCounter() {
		return RemisCounter;
	}
	
	/**
	 * A Getter-method
	 * @return The saved state of the {@link Gamelogic#getCurrentPlayerIndex() currentPlayerIndex}
	 */
	public int getPlayerIndex() {
		return playerIndex;
	}
	
	/**
	 * A Getter-method
	 * @return An array containing cloned instances of all {@link Pawn pawns} on the board
	 */
	public Pawn[] getPawns() {
		return pawns.toArray(new Pawn[0]);
	}
	
	/**
	 * A Getter-method
	 * @return Either {@code null} or a cloned instance of the slain {@link Pawn}
	 */
	public Piece getEnPassantPiece() {
		return enPassantPiece;
	}
	
	/**
	 * A Getter-method
	 * @return Either {@code null} or a cloned instance of the moved {@link Rook}
	 */
	public Piece getRochadePiece() {
		return rochadePiece;
	}
	
	/**
	 * A Getter-method
	 * @return Either {@code null} or the {@link Pos} the Rook has been moved to
	 */
	public Pos getRochadeTarget() {
		return rochadeTargetPosition;
	}
	
	/**
	 * A Getter-method
	 * @return Either {@code null} or a String-representation of the chosen pawn-upgrade
	 */
	public String getPawnUpgradeType() {
		return pawnUpgradeType;
	}
	
}
