package logic;

import java.util.ArrayList;

/**
 * A class representing the classical chess-piece of a king.
 * Extended from {@link Piece}.
 * @author Moritz Rehschuh
 */
public class King extends Piece {

	/**
	 * Has the king not been moved before?
	 */
	private boolean firstMove;
	
	public King(Player Owner, Pos position) {
		super(Owner, position);
		firstMove = true;
	}
	
	@Override
	public Pos[] getPossibleMoves(boolean advancedSearch) {
		try {
			if(Gamelogic.pawnPromotion != null) return new Pos[0];
			
			ArrayList<Pos> answer = new ArrayList<Pos>();
			
			answer.add(new Pos(getPosition().x, getPosition().y - 1));
			answer.add(new Pos(getPosition().x, getPosition().y + 1));
			answer.add(new Pos(getPosition().x - 1, getPosition().y));
			answer.add(new Pos(getPosition().x + 1, getPosition().y));
			answer.add(new Pos(getPosition().x - 1, getPosition().y - 1));
			answer.add(new Pos(getPosition().x - 1, getPosition().y + 1));
			answer.add(new Pos(getPosition().x + 1, getPosition().y - 1));
			answer.add(new Pos(getPosition().x + 1, getPosition().y + 1));
			
			if(firstMove && !isThreatened(getPosition(), null)) {
				for(Piece p: getOwner().getPieces()) {
					if(p.getClass() == Rook.class && ((Rook)p).isFirstMove()) {
						Rook rook = (Rook) p;
						switch(rook.getPosition().x) {
							case 0: if(Gamelogic.chessboard[getPosition().x - 1][getPosition().y] == null && Gamelogic.chessboard[getPosition().x - 2][getPosition().y] == null && !isThreatened(new Pos(getPosition().x - 1, getPosition().y), null) && !isThreatened(new Pos(getPosition().x - 2, getPosition().y), null)) {answer.add(new Pos(getPosition().x - 2, getPosition().y));} break;
							case 7: if(Gamelogic.chessboard[getPosition().x + 1][getPosition().y] == null && Gamelogic.chessboard[getPosition().x + 2][getPosition().y] == null && !isThreatened(new Pos(getPosition().x + 1, getPosition().y), null) && !isThreatened(new Pos(getPosition().x + 2, getPosition().y), null)) {answer.add(new Pos(getPosition().x + 2, getPosition().y));} break;
							default: throw new IllegalStateException("A Rook is not where he should be! Rochade failed.");
						}
					}
				}
			}		
			
			answer.removeIf(item -> (item.x < 0 || item.x > 7 || item.y < 0 || item.y > 7));
			answer.removeIf(item -> (Gamelogic.chessboard[item.x][item.y] != null && Gamelogic.chessboard[item.x][item.y].getOwner().equals(getOwner())));
			answer.removeIf(item -> {
				Gamelogic.makeSnapshot();
				Gamelogic.project(this.getPosition(), item);
				boolean result = isThreatened(item, null);
				Gamelogic.loadSnapshot();
				return result;
			});
			return answer.toArray(new Pos[0]);
		}catch(Exception e) {
			System.out.println("This is probably an error made by the creator of this libary. If you encounter this message, please report this incident to the developer.");
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	protected void triggerEvent(Pos target) {
		if(new Pos(getPosition().x - 2, getPosition().y).equals(target)) rochade(new Pos(0, getPosition().y), new Pos(getPosition().x - 1, getPosition().y));
		if(new Pos(getPosition().x + 2, getPosition().y).equals(target)) rochade(new Pos(7, getPosition().y), new Pos(getPosition().x + 1, getPosition().y));
		firstMove = false;
	}

	@Override
	public boolean equals(Piece piece) {
		if(piece == null) return false;
		if(piece.getClass() == King.class && piece.getPosition().x == getPosition().x && piece.getPosition().y == getPosition().y && piece.getOwner().equals(getOwner()) && ((King) piece).isFirstMove() == firstMove) return true;
		else return false;
	}
	
	/**
	 * Is this the first move of this king?
	 * @return If this is the first move of the king {@code true}, if the king has been moved before {@code false}
	 */
	public boolean isFirstMove() {
		return firstMove;
	}
	
	/**
	 * A method to check if a position is threatened by any of the other players pieces.
	 * @param pos The {@link Pos} to be checked
	 * @param exceptions If there are {@link Piece pieces}, that shall be ignored during the search, they can be listed in this array. If there are no exceptions wanted, use {@code null}.
	 * @return {@code true} if the position is threatened by at least one of the other players pieces, else {@code false}
	 */
	public boolean isThreatened(Pos pos, Piece[] exceptions) {
		for(Piece piece: (getOwner().equals(Gamelogic.getCurrentPlayer()) ? Gamelogic.getOtherPlayer() : Gamelogic.getCurrentPlayer()).getPieces()) {
			if(exceptions != null && exceptions.length != 0) {
				boolean flag = false;
				for(int i = 0; i < exceptions.length; i++) if(exceptions[i].equals(piece)) flag = true;
				if(flag) continue;
			}
			if(piece.getClass() == King.class) {
				if(pos.equals(new Pos(piece.getPosition().x, piece.getPosition().y + 1))) return true;
				if(pos.equals(new Pos(piece.getPosition().x, piece.getPosition().y - 1))) return true;
				if(pos.equals(new Pos(piece.getPosition().x + 1, piece.getPosition().y))) return true;
				if(pos.equals(new Pos(piece.getPosition().x - 1, piece.getPosition().y))) return true;
				if(pos.equals(new Pos(piece.getPosition().x + 1, piece.getPosition().y + 1))) return true;
				if(pos.equals(new Pos(piece.getPosition().x + 1, piece.getPosition().y - 1))) return true;
				if(pos.equals(new Pos(piece.getPosition().x - 1, piece.getPosition().y + 1))) return true;
				if(pos.equals(new Pos(piece.getPosition().x - 1, piece.getPosition().y - 1))) return true;
			}else{	
				for(Pos threatened: piece.getPossibleMoves(false)) {
					if(piece.getClass() != Pawn.class) {
						if(pos.equals(threatened)) return true;
					}else {
						if(pos.equals(threatened) && !(pos.equals(new Pos(piece.getPosition().x, piece.getPosition().y + 1)) || pos.equals(new Pos(piece.getPosition().x, piece.getPosition().y - 1)))) return true;
					}
				}
			}
		}
		return false;
	}
	
	/**
	 * An internal method to move a second piece (a rook) during a Rochade.
	 * @param oldPos The original {@link Pos} of the rook.
	 * @param newPos The target-{@link Pos} of the rook.
	 */
	private void rochade(Pos oldPos, Pos newPos) {
		Gamelogic.gameTrace.get(Gamelogic.gameTrace.size() - 1).addRochadeInfo(oldPos, newPos);
		Gamelogic.chessboard[oldPos.x][oldPos.y].triggerEvent(newPos);
		Gamelogic.chessboard[newPos.x][newPos.y] = Gamelogic.chessboard[oldPos.x][oldPos.y];
		Gamelogic.chessboard[newPos.x][newPos.y].updatePosition(newPos);
		Gamelogic.chessboard[oldPos.x][oldPos.y] = null;
	}
	
	/**
	 * An internal method to modify the values of a cloned instance of a king.
	 * @param firstMove Has the king been moved before?
	 */
	private void modifyArguments(boolean firstMove) {
		this.firstMove = firstMove;
	}

	@Override
	public Piece clone() {
		King clone = new King(getOwner(), getPosition());
		clone.modifyArguments(firstMove);
		return clone;
	}

}
