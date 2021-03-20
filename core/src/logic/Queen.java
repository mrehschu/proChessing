package logic;

import java.util.ArrayList;

/**
 * A class representing the classical chess-piece of a queen.
 * Extended from {@link Piece}.
 * @author Moritz Rehschuh
 */
public class Queen extends Piece {
	
	public Queen(Player Owner, Pos position) {
		super(Owner, position);
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
			
			try{ for(int i = 1; i < 8; i++) { answer.add(new Pos(getPosition().x + i, getPosition().y + i)); if(Gamelogic.chessboard[getPosition().x + i][getPosition().y + i] != null) break; } }catch(ArrayIndexOutOfBoundsException e) {}
			try{ for(int i = 1; i < 8; i++) { answer.add(new Pos(getPosition().x + i, getPosition().y - i)); if(Gamelogic.chessboard[getPosition().x + i][getPosition().y - i] != null) break; } }catch(ArrayIndexOutOfBoundsException e) {}
			try{ for(int i = 1; i < 8; i++) { answer.add(new Pos(getPosition().x - i, getPosition().y + i)); if(Gamelogic.chessboard[getPosition().x - i][getPosition().y + i] != null) break; } }catch(ArrayIndexOutOfBoundsException e) {}
			try{ for(int i = 1; i < 8; i++) { answer.add(new Pos(getPosition().x - i, getPosition().y - i)); if(Gamelogic.chessboard[getPosition().x - i][getPosition().y - i] != null) break; } }catch(ArrayIndexOutOfBoundsException e) {}
			
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
		//no Events for a Queen
	}

	@Override
	public boolean equals(Piece piece) {
		if(piece == null) return false;
		if(piece.getClass() == Queen.class && piece.getPosition().x == getPosition().x && piece.getPosition().y == getPosition().y && piece.getOwner().equals(getOwner())) return true;
		else return false;
	}

	@Override
	public Piece clone() {
		return new Queen(getOwner(), getPosition());
	}

}
