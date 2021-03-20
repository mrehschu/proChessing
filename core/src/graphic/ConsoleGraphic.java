package graphic;

import logic.ChessColor;
import logic.Gamelogic;
import logic.Piece;
import logic.Player;
import logic.Pos;

/**
 * This is an example implementation of the {@link GUIController} of this library. A simple console application to play a game of chess with two players.
 * @author Moritz Rehschuh
 */
public class ConsoleGraphic implements GUIController {
	
	/*public static void main(String[] args) {
		
		GUIController myChessGame = new ConsoleGraphic();
		myChessGame.run();
		
	}*/
	
	private boolean exit = false;
	
	public void run() {		
		logic.Gamelogic.initialize(this, new Player[] {new Player("Steven", ChessColor.BLACK), new Player("Michelle", ChessColor.WHITE)});
		try{ new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor(); }catch(Exception e) {e.printStackTrace();}
		System.out.println();
		
		while(!exit) {
			
			try{ new ProcessBuilder("cmd", "/c", "cls").inheritIO().start().waitFor(); }catch(Exception e) {e.printStackTrace();}
			
			showBoard();
			
			System.out.println(logic.Gamelogic.getCurrentPlayer().getName() + "s turn. Make your move!");
			System.out.print("Select a piece: ");
			String from = Keyboard.readString();
			
			if(from.toLowerCase().equals("exit")) { exit = true; break; }
			if(from.toLowerCase().equals("undo")) { Gamelogic.undo(); System.out.println(); continue; }
			if(from.toLowerCase().equals("redo")) { Gamelogic.redo(); System.out.println(); continue; }
			if(from.toLowerCase().equals("remis")) { if(Gamelogic.canClaimRemis()) remis("A player has claimed a remis.");    System.out.println(); continue; }
			
			String[] oldS = from.split(",");
			Piece piece;
			try{piece = logic.Gamelogic.getChessboard()[Integer.parseInt(oldS[0].trim())][Integer.parseInt(oldS[1].trim())]; }catch(Exception e) {
				System.out.println("Your Input should be in the form of: x, y and in the range of 0-7. Try again!\n");
				try { Thread.sleep(2000); }catch(InterruptedException i) {}
				continue;
			}
			if(piece != null && !piece.getOwner().equals(logic.Gamelogic.getCurrentPlayer())) {
				System.out.println("This Piece does not belong to you. Try again!\n");
				try { Thread.sleep(2000); }catch(InterruptedException i) {}
				continue;
			}
			try {
				Pos[] positions = piece.getPossibleMoves(true);
				System.out.print("Possible Moves: ");
				for(int i = 0; i < positions.length; i++) System.out.print(positions[i].toString() + "  ");
				System.out.println();
			}catch(NullPointerException e) {
				System.out.println("There is no Piece on that position. Try again!\n");
				try { Thread.sleep(2000); }catch(InterruptedException i) {}
				continue;
			}
			
			System.out.print("Select where you want to go: ");
			String to = Keyboard.readString();
			
			String[] newS = to.split(",");
			try{
				if(!logic.Gamelogic.move(piece, new Pos(Integer.parseInt(newS[0].trim()), Integer.parseInt(newS[1].trim())))) {
					System.out.println("Thats not possible. Try again!\n");
					try { Thread.sleep(2000); }catch(InterruptedException e) {}
					continue;
				}
			}catch(Exception e) {
				System.out.println("Your Input should be in the form of: x, y and in the range of 0-7. Try again!\n");
				try { Thread.sleep(2000); }catch(InterruptedException i) {}
				continue;
			}
			
			System.out.println();
		}
	}
	
	/**
	 * A custom method to print the {@link logic.Gamelogic#getChessboard() chessboard} into the console.
	 */
	private void showBoard() {
		System.out.println("    0 1 2 3 4 5 6 7  ");
		System.out.print("  -------------------");
		if(Gamelogic.canClaimRemis()) System.out.print("      To claim a remis type 'remis'.");
		System.out.println();
		Piece[][] chessboard = Gamelogic.getChessboard();
		
		for(int y = 0; y < 8; y++) {
			System.out.print(y + " | ");
			for(int x = 0; x < 8; x++) {
				try{
					switch(chessboard[x][y].getClass().toString()) {
						case "class logic.Pawn": System.out.print("P "); break;
						case "class logic.Rook": System.out.print("R "); break;
						case "class logic.Knight": System.out.print("H "); break;
						case "class logic.Bishop": System.out.print("B "); break;
						case "class logic.Queen": System.out.print("Q "); break;
						case "class logic.King": System.out.print("K "); break;
						default: System.out.print("U ");
					}
				}catch(NullPointerException e) { System.out.print("  "); }
			}
			System.out.println("|");
			//System.out.println("  |                 |");
		}
		System.out.println("  -------------------");
	}
	
	@Override
	public void choosePromotion() {
		String input;
		do {
			System.out.println("One of your pawns has reached the enemies baseline. It will be promoted.");
			System.out.print("Type one of the following:[ 'Queen', 'Rook', 'Bishop' or 'Knight' ]: ");
			input = Keyboard.readString();
		} while(!Gamelogic.sendPromotionChoice(input));
		Gamelogic.checkKings();
	}

	@Override
	public void check(Player player, Piece[] pieces) {
		System.out.println("\nCHECK!!! " + player.getName() + " your king is threatened. Do something!");
	}

	@Override
	public void checkmate(Player player, Piece[] pieces) {
		exit = true;
		String name = (Gamelogic.getCurrentPlayer().equals(player) ? Gamelogic.getOtherPlayer().getName() : player.getName());
		System.out.println("\nCHECKMATE!!! Game Over. And the winner is: " + name);
	}

	@Override
	public void remis(String reason) {
		exit = true;
		System.out.println("\nREMIS!!! Game over.");
		System.out.println("Reason: " + reason);
	}
	
}
