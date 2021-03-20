package logic;
import java.util.ArrayList;
import java.util.Collections;

/**
 * This class contains many static methods to play a game of chess. Using the {@link graphic.GUIController GUIController}-interface, you can build your own custom chess-application with the help of those methods.
 * Make sure to initialize the Gamelogic first, before using any other methods.
 * 
 * @author Moritz Rehschuh
 */
public class Gamelogic {

	/**
	 * An array of {@link Piece pieces} that stores the current state of the game.
	 */
	protected static Piece[][] chessboard = new Piece[8][8];
	
	/**
	 * An array to store a snapshot of the original {@link #chessboard}.
	 */
	protected static Piece[][] snapshot = new Piece[8][8];
	
	/**
	 * This array stores both {@link Player players}
	 */
	private static Player[] playerList = new Player[2];
	
	/**
	 * An ArrayList of {@link TraceElement TraceElements} that stores every made move in order.
	 */
	protected static ArrayList<TraceElement> gameTrace;
	
	/**
	 * An ArrayList of {@link TraceElement TraceElements} that stores every undone move in order.
	 */
	private static ArrayList<TraceElement> redoTrace;
	
	/**
	 * The index of the currentPlayer in the {@link #playerList}.
	 */
	private static int currentPlayerIndex = -1;
	
	/**
	 * A Counter that represents how many moves where made since the last pawn-move/kill.
	 */
	private static int RemisCounter = 0;
	
	/**
	 * Storing the pawn to promote during a Promotion.
	 */
	protected static Pawn pawnPromotion;
	
	/**
	 * An instance of a {@link graphic.GUIController GUIController}, the Gamelogic will respond to.
	 */
	protected static graphic.GUIController currentGUI;
	
	/**
	 * This method initializes all data to prepare for a chess-game.
	 * 
	 * @param caller The {@link graphic.GUIController GUIController} the {@link Gamelogic} shall respond to. Honestly just put "this" in here.
	 * @param playerList An array of two {@link Player}-Objects, containing a Player with {@link ChessColor#WHITE} and one with {@link ChessColor#BLACK}
	 */
	public static void initialize(graphic.GUIController caller, Player[] playerList) {
		currentGUI = caller;
		RemisCounter = 0;
		pawnPromotion = null;
		gameTrace = new ArrayList<TraceElement>();
		redoTrace = new ArrayList<TraceElement>();
		
		try{
			Gamelogic.playerList[0] = playerList[0];
			Gamelogic.playerList[1] = playerList[1];
		}catch(ArrayIndexOutOfBoundsException e) { throw new IllegalStateException("A Game of Chess needs two Players.", e); }
		
		if(playerList[0].getColor().equals(ChessColor.WHITE)) currentPlayerIndex = 0;
		else if(playerList[1].getColor().equals(ChessColor.WHITE)) currentPlayerIndex = 1;
		else throw new IllegalStateException("The Player Colors can only be ChessColor.BLACK and ChessColor.WHITE.");
		
		int otherIndex;
		otherIndex = (currentPlayerIndex == 0 ? 1 : 0);
		if(!playerList[otherIndex].getColor().equals(ChessColor.BLACK)) throw new IllegalStateException("The Player Colors can only be ChessColor.BLACK and ChessColor.WHITE.");
		
		
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 8; x++) {
				if(y == 1) chessboard[x][y] = new Pawn(playerList[otherIndex], new Pos(x, y));
				else if(y == 6)	chessboard[x][y] = new Pawn(playerList[currentPlayerIndex], new Pos(x, y));
				else if(y == 0) switch(x) {
					case 0: case 7: chessboard[x][y] = new Rook(playerList[otherIndex], new Pos(x, y)); break;
					case 1: case 6: chessboard[x][y] = new Knight(playerList[otherIndex], new Pos(x, y)); break;
					case 2: case 5: chessboard[x][y] = new Bishop(playerList[otherIndex], new Pos(x, y)); break;
					case 3: chessboard[x][y] = new Queen(playerList[otherIndex], new Pos(x, y)); break;
					case 4: chessboard[x][y] = new King(playerList[otherIndex], new Pos(x, y)); break;
					default: chessboard[x][y] = null;
				}else if(y == 7) switch(x) {
					case 0: case 7: chessboard[x][y] = new Rook(playerList[currentPlayerIndex], new Pos(x, y)); break;
					case 1: case 6: chessboard[x][y] = new Knight(playerList[currentPlayerIndex], new Pos(x, y)); break;
					case 2: case 5: chessboard[x][y] = new Bishop(playerList[currentPlayerIndex], new Pos(x, y)); break;
					case 3: chessboard[x][y] = new Queen(playerList[currentPlayerIndex], new Pos(x, y)); break;
					case 4: chessboard[x][y] = new King(playerList[currentPlayerIndex], new Pos(x, y)); break;
					default: chessboard[x][y] = null;
				}else {
					chessboard[x][y] = null;
				}
				
				if(chessboard[x][y] != null) chessboard[x][y].getOwner().addPiece(chessboard[x][y]);
			}
		}
	}
	
	/**
	 * This method is used to make a move during a game. This will check if the move is possible, then execute the move and trigger every update-method of the data-set, including the {@link #checkKings()}-method.
	 * 
	 * @param piece The {@link Piece}-Object you want to move. The Piece needs to be taken from the {@link #getChessboard() chessboard}.
	 * @param target A {@link Pos}-Object containing the target-position.
	 * @return If the requested move is legal and has been executed correctly, returns {@code true}, else {@code false}.
	 */
	public static boolean move(Piece piece, Pos target) {
		if(currentGUI == null) throw new IllegalStateException("Initialize the Gamelogic first!");
		if(pawnPromotion != null) return false;
		
		try {	
			if(piece == null) return false;
			Pos oldPos = piece.getPosition();
			if(!piece.getOwner().equals(playerList[currentPlayerIndex])) return false;
			
			boolean pieceflag = false;
			for(int y = 0; y < 8; y++) {
				if(pieceflag) break;
				for(int x = 0; x < 8; x++) {
					if(chessboard[x][y] != null && chessboard[x][y].equals(piece)) { pieceflag = true; break; }
				}
			}
			if(!pieceflag) return false;
			
			boolean posflag = false;
			for(Pos possiblePos: piece.getPossibleMoves(true)) {
				if(possiblePos.equals(target)) { posflag = true; break; }
			}
			if(!posflag) return false;
			
			gameTrace.add(new TraceElement(oldPos, target));
			redoTrace.clear();
			
			RemisCounter++;
			if(chessboard[target.x][target.y] != null || piece.getClass() == Pawn.class) RemisCounter = 0;
			
			if(chessboard[target.x][target.y] != null) {
				if(!chessboard[target.x][target.y].getOwner().removePiece(chessboard[target.x][target.y])) return false;
			}
			piece.triggerEvent(target);
			chessboard[target.x][target.y] = chessboard[piece.getPosition().x][piece.getPosition().y];
			chessboard[target.x][target.y].updatePosition(target);
			chessboard[oldPos.x][oldPos.y] = null;
			
			for(int i = 0; i < 2; i++) {
				for(Piece p: playerList[i].getPieces()) {
					if(p.getClass() == Pawn.class && ((Pawn)p).enPassantCounter > 0) ((Pawn)p).enPassantCounter--;
				}
			}
			
			if(pawnPromotion == null) {
				currentPlayerIndex = (currentPlayerIndex == 0 ? 1 : 0);
				checkKings();
			}
			
			return true;
		}catch(Exception e) {
			System.out.println("This is probably an error made by the creator of this libary. If you encounter this message, please report this incident to the developer.");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * Triggering this method will delete all data of the current chess-game. After using this method, the {@link Gamelogic} needs to be initiated again, before using any other methods of this class.
	 */
	public static void clearAll() {
		
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 8; x++) {
				chessboard[x][y] = null;
				snapshot[x][y] = null;
			}
		}
		
		for(int i = 0; i < playerList.length; i++) playerList[i] = null;
		gameTrace = null;
		redoTrace = null;
		currentPlayerIndex = -1;
		RemisCounter = 0;
		currentGUI = null;
		pawnPromotion = null;
	}
	
	/**
	 * This method will undo the last turn of the game by loading the data of the latest {@link TraceElement} of the {@link #gameTrace} and moving it to the {@link #redoTrace}.
	 * If there are no further moves to undo, this method will do nothing.
	 */
	public static void undo() {
		if(currentGUI == null) throw new IllegalStateException("Initialize the Gamelogic first!");
		if(pawnPromotion != null) return;
		
		try {
			if(gameTrace != null && !gameTrace.isEmpty()) {
				TraceElement trace = gameTrace.get(gameTrace.size() - 1);
				redoTrace.add(trace);
				gameTrace.remove(gameTrace.size() - 1);
				chessboard[trace.getOldPosition().x][trace.getOldPosition().y] = trace.getOriginalPiece();
				chessboard[trace.getNewPosition().x][trace.getNewPosition().y] = trace.getDeadPiece();
				currentPlayerIndex = trace.getPlayerIndex();
				RemisCounter = trace.getRemisCounter();
				
				for(Pawn pawn: trace.getPawns()) {
					chessboard[pawn.getPosition().x][pawn.getPosition().y] = pawn;
				}
				
				if(trace.getEnPassantPiece() != null) {
					Piece piece = trace.getEnPassantPiece();
					chessboard[piece.getPosition().x][piece.getPosition().y] = piece;
				}
				
				if(trace.getRochadePiece() != null) {
					Piece piece = trace.getRochadePiece();
					chessboard[piece.getPosition().x][piece.getPosition().y] = piece;
					chessboard[trace.getRochadeTarget().x][trace.getRochadeTarget().y] = null;
				}
				
				for(int i = 0; i < 2; i++) {
					playerList[i].updatePieces();
				}
			}
		}catch(Exception e) {
			System.out.println("This is probably an error made by the creator of this libary. If you encounter this message, please report this incident to the developer.");
			e.printStackTrace();
		}
	}
	
	/**
	 * This method will redo the last undone turn of the game by loading the data of the latest {@link TraceElement} of the {@link #redoTrace} and moving it back to the {@link #gameTrace}.
	 * If there are no further moves to redo, this method will do nothing.
	 */
	public static void redo() {
		if(currentGUI == null) throw new IllegalStateException("Initialize the Gamelogic first!");
		if(pawnPromotion != null) return;
		
		try {
			if(redoTrace != null && !redoTrace.isEmpty()) {
				TraceElement trace = redoTrace.get(redoTrace.size() - 1);
				gameTrace.add(new TraceElement(trace.getOldPosition(), trace.getNewPosition()));
				redoTrace.remove(redoTrace.size() - 1);
				Pos target = trace.getNewPosition();
				Piece piece = trace.getOriginalPiece();
				Pos oldPos = trace.getOldPosition();
				
				RemisCounter++;
				if(chessboard[target.x][target.y] != null || piece.getClass() == Pawn.class) RemisCounter = 0;
				
				chessboard[oldPos.x][oldPos.y] = piece;
				if(trace.getPawnUpgradeType() != null && piece.getClass() == Pawn.class) ((Pawn) piece).setUpgradeOverride(trace.getPawnUpgradeType());
				piece.triggerEvent(target);
				chessboard[target.x][target.y] = chessboard[oldPos.x][oldPos.y];
				chessboard[target.x][target.y].updatePosition(target);
				chessboard[oldPos.x][oldPos.y] = null;
				
				for(int i = 0; i < 2; i++) {
					playerList[i].updatePieces();
				}
				
				for(int i = 0; i < 2; i++) {
					for(Piece p: playerList[i].getPieces()) {
						if(p.getClass() == Pawn.class && ((Pawn)p).enPassantCounter > 0) ((Pawn)p).enPassantCounter--;
					}
				}
				
				if(gameTrace.get(gameTrace.size() - 1).getPlayerIndex() == currentPlayerIndex) {
					currentPlayerIndex = (currentPlayerIndex == 0 ? 1 : 0);
				}
			}
		}catch(Exception e) {
			System.out.println("This is probably an error made by the creator of this libary. If you encounter this message, please report this incident to the developer.");
			e.printStackTrace();
		}
	}
	
	/**
	 * This method checks for all game-ending criteria and notifies the corresponding methods of your {@link graphic.GUIController GUIController} if necessary.
	 * While you can trigger this method manually, it is not required, since it will be called after each turn by the {@link #move(Piece, Pos) move()}-method.
	 */
	public static void checkKings() {
		if(currentGUI == null) throw new IllegalStateException("Initialize the Gamelogic first!");
		if(pawnPromotion != null) return;
		
		try {
			for(int i = 0; i < 2; i++) {				
				if(playerList[i].getPieces().length == 1) {
					boolean hasPieces = false;
					int bishopCount = 0;
					int knightCount = 0;
					boolean[] bishopFlags = new boolean[2];
					for(Piece piece: playerList[i == 1 ? 0 : 1].getPieces()) {
						if(hasPieces) break;
						if(piece.getClass() == Knight.class) knightCount++;
						if(piece.getClass() == Bishop.class) {
							bishopCount++;
							if((piece.getPosition().x + piece.getPosition().y) % 2 == 0 ) bishopFlags[0] = true;
							else bishopFlags[1] = true;
						}
						
						if(	piece.getClass() == Pawn.class ||
							piece.getClass() == Rook.class ||
							piece.getClass() == Queen.class		) hasPieces = true;
					}
					if(!(hasPieces ||
						 knightCount >= 3 ||
						 (bishopCount >= 2 && bishopFlags[0] && bishopFlags[1]) ||
						 (bishopCount == 1 && knightCount >= 1))) currentGUI.remis("Impossibility of checkmate: A checkmate is not possible anymore.");
				}
				
				King king = playerList[i].findKing();
				if(king.isThreatened(king.getPosition(), null)) {
					ArrayList<Pos> possibleMoves = new ArrayList<Pos>();
					for(Piece piece: playerList[i].getPieces()) {
						for(Pos pos: piece.getPossibleMoves(true)) {
							possibleMoves.add(pos);
						}
					}
					
					ArrayList<Piece> threads = new ArrayList<Piece>();
					for(Piece piece: playerList[(i == 0 ? 1 : 0)].getPieces()) {
						for(Pos pos: piece.getPossibleMoves(true)) {
							if(king.getPosition().equals(pos)) { threads.add(piece); break; }
						}
					}
					
					if(possibleMoves.size() == 0) currentGUI.checkmate(playerList[i], threads.toArray(new Piece[0]));
					else currentGUI.check(playerList[i], threads.toArray(new Piece[0]));
				}else {
					ArrayList<Pos> possibleMoves = new ArrayList<Pos>();
					for(Piece piece: playerList[i].getPieces()) {
						for(Pos pos: piece.getPossibleMoves(true)) {
							possibleMoves.add(pos);
						}
					}
					if(possibleMoves.size() == 0) currentGUI.remis("Stalemate: " + playerList[i].getName() + " has no legal move but is not in check.");
				}
			}
			
			if(RemisCounter >= 75) currentGUI.remis("Seventy-five-move rule: No capture or no pawn move has occurred in the last 75 moves (by both players).");
			if(countOccurrencesOfSituation() >= 5) currentGUI.remis("Fivefold repetition: The same position has occured for five times during the course of the game.");
			
		}catch(Exception e) {
			System.out.println("This is probably an error made by the creator of this libary. If you encounter this message, please report this incident to the developer.");
			e.printStackTrace();
		}
	}
	
	/**
	 * A private method to determine if a situation on the board has already occurred during the game.
	 * 
	 * @return An {@code integer}, representing how many times the current situation of the game has already occurred.
	 */
	private static int countOccurrencesOfSituation() {
		makeSnapshot();
		int counter = 0;
		int answer = 1;
		int playerIndexSaved = currentPlayerIndex;
		ArrayList<Pos> movesNow = new ArrayList<Pos>();
		for(int y = 0; y < 8; y++) for(int x = 0; x < 8; x++) if(snapshot[x][y] != null) Collections.addAll(movesNow, snapshot[x][y].getPossibleMoves(true));
		
		for(int i = gameTrace.size() - 1; i >= 0; i--) {
			if(gameTrace.get(i).getDeadPiece() != null) break;
			undo();
			counter++;
			
			if(currentPlayerIndex == playerIndexSaved || gameTrace.isEmpty()) {
				boolean breaker = false;
				ArrayList<Pos> movesThen = new ArrayList<Pos>();
				for(int y = 0; y < 8; y++) {
					for(int x = 0; x < 8; x++) {
						if(breaker) break;
						Piece piece = chessboard[x][y];
						if(piece == null && snapshot[x][y] == null) {
						}else if(piece != null && snapshot[x][y] != null && piece.equals(snapshot[x][y])) {
							Collections.addAll(movesThen, piece.getPossibleMoves(true));
						}else breaker = true;
					}
				}
				if(!breaker && movesThen.equals(movesNow)) answer++;
			}
		}
		
		while(counter > 0) {
			redo();
			counter--;
		}
		
		return answer;
	}
	
	/**
	 * A method to respond if {@link graphic.GUIController#choosePromotion()} gets called.
	 * If the response can't be processed, it will return false.
	 * @param response A {@code String} that should be either: "Queen", "Rook", "Bishop" or "Knight"
	 * @return A boolean to determine if the response was accepted.
	 */
	public static boolean sendPromotionChoice(String response) {
		if(currentGUI == null) throw new IllegalStateException("Initialize the Gamelogic first!");
		if(pawnPromotion == null) return false;
		
		try {
			Piece newPiece = null;
			boolean somethingIsWrong = false;
			
			switch(response.toLowerCase().trim()) {
				case "queen":	newPiece = new Queen(pawnPromotion.getOwner(), pawnPromotion.getPosition()); break;
				case "rook":	newPiece = new Rook(pawnPromotion.getOwner(), pawnPromotion.getPosition()); break;
				case "bishop":	newPiece = new Bishop(pawnPromotion.getOwner(), pawnPromotion.getPosition()); break;
				case "knight":	newPiece = new Knight(pawnPromotion.getOwner(), pawnPromotion.getPosition()); break;
				default: somethingIsWrong = true;
			}
			
			if(somethingIsWrong) return false;
			
			gameTrace.get(gameTrace.size() - 1).addPawnUpgradeInfo(response.toLowerCase().trim());
			pawnPromotion.getOwner().removePiece(pawnPromotion);
			newPiece.triggerEvent(pawnPromotion.getPosition());
			chessboard[pawnPromotion.getPosition().x][pawnPromotion.getPosition().y] = newPiece;
			newPiece.getOwner().addPiece(newPiece);
			pawnPromotion = null;
			
			currentPlayerIndex = (currentPlayerIndex == 0 ? 1 : 0);
			
			return true;
			
		}catch(Exception e) {
			System.out.println("This is probably an error made by the creator of this libary. If you encounter this message, please report this incident to the developer.");
			e.printStackTrace();
			return false;
		}
	}
	
	/**
	 * An internal method to load the current state of the {@link #chessboard} into the {@link #snapshot}-array.
	 */
	protected static void makeSnapshot() {
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 8; x++) {
				snapshot[x][y] = chessboard[x][y];
			}
		}
	}
	
	/**
	 * An internal method to load the saved state of the game from the {@link #snapshot}-array back to the {@link #chessboard}.
	 */
	protected static void loadSnapshot() {
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 8; x++) {
				chessboard[x][y] = snapshot[x][y];
			}
		}
	}
	
	/**
	 * An internal method to move pieces from one position to another without actually triggering the {@link Piece#triggerEvent(Pos) triggerEvent()}-method of that piece.
	 * This method is used to simulate future states of the board.
	 * 
	 * @param oldPos The {@link Pos position} where the piece is located now.
	 * @param newPos The target-{@link Pos position} of the piece.
	 */
	protected static void project(Pos oldPos, Pos newPos) {
		chessboard[newPos.x][newPos.y] = chessboard[oldPos.x][oldPos.y];
		chessboard[oldPos.x][oldPos.y] = null;
	}
	
	/**
	 * An internal Getter-method, returning the {@link #currentPlayerIndex}.
	 * @return {@link #currentPlayerIndex}
	 */
	protected static int getCurrentPlayerIndex() {
		return currentPlayerIndex;
	}
	
	/**
	 * An internal Getter-method, returning the {@link #RemisCounter}.
	 * @return {@link #RemisCounter}
	 */
	protected static int getRemisCounter() {
		return RemisCounter;
	}
	
	/**
	 * This Getter-method returns a copy of the {@link #chessboard}-array.
	 * @return {@link #chessboard}
	 */
	public static Piece[][] getChessboard() {
		if(currentGUI == null) throw new IllegalStateException("Initialize the Gamelogic first!");
		Piece[][] clone = new Piece[8][8];
		for(int y = 0; y < 8; y++) {
			for(int x = 0; x < 8; x++) {
				clone[x][y] = chessboard[x][y];
			}
		}
		return clone;
	}
	
	/**
	 * A Getter-method, returning the {@link Player} whose turn it is.
	 * @see #getOtherPlayer()
	 * @return The {@link Player} whose turn it is.
	 */
	public static Player getCurrentPlayer() {
		if(currentGUI == null) throw new IllegalStateException("Initialize the Gamelogic first!");
		return playerList[currentPlayerIndex];
	}
	
	/**
	 * A Getter-method, returning the other {@link Player}.
	 * @see #getCurrentPlayer()
	 * @return The inactive {@link Player}.
	 */
	public static Player getOtherPlayer() {
		if(currentGUI == null) throw new IllegalStateException("Initialize the Gamelogic first!");
		return playerList[(currentPlayerIndex == 0 ? 1 : 0)];
	}
	
	/**
	 * This method checks whether or not a remis can be claimed by the current player.
	 * @return A {@code boolean} representing if a remis can be claimed.
	 */
	public static boolean canClaimRemis() {
		if(currentGUI == null) throw new IllegalStateException("Initialize the Gamelogic first!");
		if(pawnPromotion != null) return false;
		
		try {
			if(RemisCounter >= 50 || countOccurrencesOfSituation() >= 3) return true;
			else return false;
			
		}catch(Exception e) {
			System.out.println("This is probably an error made by the creator of this libary. If you encounter this message, please report this incident to the developer.");
			e.printStackTrace();
			return false;
		}
	}
		
}
