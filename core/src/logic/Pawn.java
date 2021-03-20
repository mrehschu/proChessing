package logic;

import java.util.ArrayList;

/**
 * A class representing the classical chess-piece of a pawn.
 * Extended from {@link Piece}.
 * @author Moritz Rehschuh
 */
public class Pawn extends Piece {

	/**
	 * Is this the first move of this pawn?
	 */
	private boolean firstMove;
	
	/**
	 * If this value is greater then zero, then this piece can be slain by an EnPassant-move.
	 */
	protected int enPassantCounter;
	protected String pawnUpgradeOverride;
	
	public Pawn(Player Owner, Pos position) {
		super(Owner, position);
		firstMove = true;
		enPassantCounter = 0;
		pawnUpgradeOverride = null;
	}
	
	@Override
	public Pos[] getPossibleMoves(boolean advancedSearch) {
		try {
			if(Gamelogic.pawnPromotion != null) return new Pos[0];
			
			ArrayList<Pos> answer = new ArrayList<Pos>();
			
			if(getOwner().getColor().equals(ChessColor.BLACK)) {
				if(Gamelogic.chessboard[getPosition().x][getPosition().y + 1] == null) {
					answer.add(new Pos(getPosition().x, getPosition().y + 1));
					if(firstMove && Gamelogic.chessboard[getPosition().x][getPosition().y + 2] == null) answer.add(new Pos(getPosition().x, getPosition().y + 2));		
				}
				
				try { if(Gamelogic.chessboard[getPosition().x + 1][getPosition().y + 1] != null) answer.add(new Pos(getPosition().x + 1, getPosition().y + 1)); }catch(ArrayIndexOutOfBoundsException e) {}
				try { if(Gamelogic.chessboard[getPosition().x - 1][getPosition().y + 1] != null) answer.add(new Pos(getPosition().x - 1, getPosition().y + 1)); }catch(ArrayIndexOutOfBoundsException e) {}
				
				try {	Piece tmpOne = Gamelogic.chessboard[getPosition().x + 1][getPosition().y];
						if(tmpOne != null && !tmpOne.getOwner().equals(getOwner()) && tmpOne.getClass() == Pawn.class && ((Pawn)tmpOne).enPassantCounter > 0) answer.add(new Pos(getPosition().x + 1, getPosition().y + 1));
				}catch(ArrayIndexOutOfBoundsException e) {}
				try {	Piece tmpTwo = Gamelogic.chessboard[getPosition().x - 1][getPosition().y];
						if(tmpTwo != null && !tmpTwo.getOwner().equals(getOwner()) && tmpTwo.getClass() == Pawn.class && ((Pawn)tmpTwo).enPassantCounter > 0) answer.add(new Pos(getPosition().x - 1, getPosition().y + 1));
				}catch(ArrayIndexOutOfBoundsException e) {}
			}else {
				if(Gamelogic.chessboard[getPosition().x][getPosition().y - 1] == null) {
					answer.add(new Pos(getPosition().x, getPosition().y - 1));
					if(firstMove && Gamelogic.chessboard[getPosition().x][getPosition().y - 2] == null) answer.add(new Pos(getPosition().x, getPosition().y - 2));		
				}
				
				try { if(Gamelogic.chessboard[getPosition().x + 1][getPosition().y - 1] != null) answer.add(new Pos(getPosition().x + 1, getPosition().y - 1)); }catch(ArrayIndexOutOfBoundsException e) {}
				try { if(Gamelogic.chessboard[getPosition().x - 1][getPosition().y - 1] != null) answer.add(new Pos(getPosition().x - 1, getPosition().y - 1)); }catch(ArrayIndexOutOfBoundsException e) {}
				
				try {	Piece tmpOne = Gamelogic.chessboard[getPosition().x + 1][getPosition().y];
						if(tmpOne != null && !tmpOne.getOwner().equals(getOwner()) && tmpOne.getClass() == Pawn.class && ((Pawn)tmpOne).enPassantCounter > 0) answer.add(new Pos(getPosition().x + 1, getPosition().y - 1));
				}catch(ArrayIndexOutOfBoundsException e) {}
				try {	Piece tmpTwo = Gamelogic.chessboard[getPosition().x - 1][getPosition().y];
						if(tmpTwo != null && !tmpTwo.getOwner().equals(getOwner()) && tmpTwo.getClass() == Pawn.class && ((Pawn)tmpTwo).enPassantCounter > 0) answer.add(new Pos(getPosition().x - 1, getPosition().y - 1));
				}catch(ArrayIndexOutOfBoundsException e) {}
			}
			
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
		Piece tmp;
		if(getOwner().getColor().equals(ChessColor.BLACK)) {
			if(firstMove && target.equals(new Pos(getPosition().x, getPosition().y + 2))) enPassantCounter = 2;
			tmp = Gamelogic.chessboard[target.x][target.y - 1];
			if(target.y == 7) {
				Gamelogic.pawnPromotion = this;
				if(pawnUpgradeOverride != null) Gamelogic.sendPromotionChoice(pawnUpgradeOverride);
				else Gamelogic.currentGUI.choosePromotion();
			}
		}else {
			if(firstMove && target.equals(new Pos(getPosition().x, getPosition().y - 2))) enPassantCounter = 2;
			tmp = Gamelogic.chessboard[target.x][target.y + 1];
			if(target.y == 0) {
				Gamelogic.pawnPromotion = this;
				if(pawnUpgradeOverride != null) Gamelogic.sendPromotionChoice(pawnUpgradeOverride);
				else Gamelogic.currentGUI.choosePromotion();
			}
		}
		if(tmp != null && !tmp.getOwner().equals(getOwner()) && tmp.getClass() == Pawn.class && ((Pawn)tmp).enPassantCounter > 0) {
			Gamelogic.gameTrace.get(Gamelogic.gameTrace.size() - 1).addEnPassantInfo(tmp);
			Gamelogic.chessboard[tmp.getPosition().x][tmp.getPosition().y] = null;
			tmp.getOwner().removePiece(tmp);
		}
		pawnUpgradeOverride = null;
		firstMove = false;
	}

	@Override
	public boolean equals(Piece piece) {
		if(piece == null) return false;
		if(piece.getClass() == Pawn.class && piece.getPosition().x == getPosition().x && piece.getPosition().y == getPosition().y && piece.getOwner().equals(getOwner())) {
			Pawn pawn = (Pawn) piece;
			if(this.pawnUpgradeOverride == null) if(pawn.firstMove == this.firstMove && pawn.enPassantCounter == this.enPassantCounter && pawn.pawnUpgradeOverride == null) return true;
			else if(pawn.firstMove == this.firstMove && pawn.enPassantCounter == this.enPassantCounter && this.pawnUpgradeOverride.equals(pawn.pawnUpgradeOverride)) return true;
		}
		return false;
	}
	
	/**
	 * An internal method to modify the values of a cloned instance of a pawn.
	 * @param firstMove Has the pawn been moved before?
	 * @param enPassantCounter The wanted value of the enPassantCounter.
	 */
	private void modifyArguments(boolean firstMove, int enPassantCounter) {
		this.firstMove = firstMove;
		this.enPassantCounter = enPassantCounter;
	}
	
	/**
	 * A method to set an upgrade-override for the next move.
	 * This String will be passed on to {@link Gamelogic#sendPromotionChoice(String) sendPromotionChoice()}-method, overriding the calling of the {@link graphic.GUIController#choosePromotion() choosePromotion()}-menu.
	 * This override will expire after the next move and be replaced by a {@code null}.
	 * @param override The override-String, that should be either: "Queen", "Rook", "Bishop" or "Knight".
	 */
	protected void setUpgradeOverride(String override) {
		pawnUpgradeOverride = override;
	}

	@Override
	public Piece clone() {
		Pawn clone = new Pawn(getOwner(), getPosition());
		clone.modifyArguments(firstMove, enPassantCounter);
		return clone;
	}
	
}
