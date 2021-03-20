package logic;

import java.util.ArrayList;

/**
 * A class representing the classical chess-piece of a rook.
 * Extended from {@link Piece}.
 * @author Moritz Rehschuh
 */
public class Rook extends Piece {

	/**
	 * Has the rook not been moved before?
	 */
	private boolean firstMove;
	
	public Rook(Player Owner, Pos position) {
		super(Owner, position);
		firstMove = true;
	}
	
	@Override
	public Pos[] getPossibleMoves(boolean advancedSearch) {
		try {
			if(Gamelogic.pawnPromotion != null) return new Pos[0];
			
			ArrayList<Pos> answer = new ArrayList<Pos>();
			
			for(int x = getPosition().x + 1; x < 8; x++) { answer.add(new Pos(x, getPosition().y)); if(Gamelogic.chessboard[x][getPosition().y] != null) break; }
			for(int x = getPosition().x - 1; x >= 0; x--) { answer.add(new Pos(x, getPosition().y)); if(Gamelogic.chessboard[x][getPosition().y] != null) break; }
			for(int y = getPosition().y + 1; y < 8; y++) { answer.add(new Pos(getPosition().x, y)); if(Gamelogic.chessboard[getPosition().x][y] != null) break; }
			for(int y = getPosition().y - 1; y >= 0; y--) { answer.add(new Pos(getPosition().x, y)); if(Gamelogic.chessboard[getPosition().x][y] != null) break; }
			
			answer.removeIf(item -> (item.x < 0 || item.x > 7 || item.y < 0 || item.y > 7));
			answer.removeIf(item -> (Gamelogic.chessboard[item.x][item.y] != null && Gamelogic.chessboard[item.x][item.y].getOwner().equals(getOwner())));
			
			if(advancedSearch) {
				ArrayList<Pos> removable = new ArrayList<Pos>();
				answer.forEach(item -> {
					Gamelogic.makeSnapshot();
					Piece[] exception;
					if(Gamelogic.chessboard[item.x][item.y] != null) exception = new Piece[] {Gamelogic.chessboard[item.x][item.y]}; else exception = null;
					Gamelogic.project(getPosition(), item);
					if(getOwner().findKing().isThreatened(getOwner().findKing().getPosition(), exception)) removable.add(item);
					Gamelogic.loadSnapshot();
				});
				answer.removeAll(removable);
			}
			return answer.toArray(new Pos[0]);
		}catch(Exception e) {
			System.out.println("This is probably an error made by the creator of this libary. If you encounter this message, please report this incident to the developer.");
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void triggerEvent(Pos target) {
		firstMove = false;
	}

	@Override
	public boolean equals(Piece piece) {
		if(piece == null) return false;
		if(piece.getClass() == Rook.class && piece.getPosition().x == getPosition().x && piece.getPosition().y == getPosition().y && piece.getOwner().equals(getOwner()) && ((Rook) piece).isFirstMove() == firstMove) return true;
		else return false;
	}
	
	/**
	 * Is this the first move of this rook?
	 * @return If this is the first move of the rook {@code true}, if the rook has been moved before {@code false}
	 */
	public boolean isFirstMove() {
		return firstMove;
	}

	@Override
	public Piece clone() {
		Rook clone = new Rook(getOwner(), getPosition());
		if(!firstMove) clone.triggerEvent(null);
		return clone;
	}

}
