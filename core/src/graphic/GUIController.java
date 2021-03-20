package graphic;

import logic.Piece;
import logic.Player;

/**
 * This interface can be used to implement a GUI for a chess-game. Use an instance of a GUIController to access the methods of the {@link logic.Gamelogic} from this library.
 * @author Moritz Rehschuh
 */
public interface GUIController {

	/**
	 * A method to start the chess application. And to handle the general gameloop.
	 */
	public void run();
	
	/**
	 * This method will be called by the {@link logic.Gamelogic Gamelogic}, if a pawn reaches the other side of the board and gets a promotion.
	 * It should implement a menu for the player to choose the kind of piece the pawn shall be promoted into.
	 * The answer is to be send at {@link logic.Gamelogic#sendPromotionChoice(String)} and should be a {@code String}, that is either: "Queen", "Rook", "Bishop" or "Knight".
	 */
	public void choosePromotion();
	
	/**
	 * This method will be called by the {@link logic.Gamelogic Gamelogic} if a check occurs.
	 * @param player The {@link Player}, who is in check.
	 * @param pieces An array of the {@link Piece pieces}, who are causing this check.
	 */
	public void check(Player player, Piece[] pieces);
	
	/**
	 * This method will be called by the {@link logic.Gamelogic Gamelogic} if a checkmate occurs.
	 * @param player The {@link Player}, who is put into checkmate.
	 * @param pieces An array of the {@link Piece pieces}, who are causing this checkmate.
	 */
	public void checkmate(Player player, Piece[] pieces);
	
	/**
	 * This method will be called by the {@link logic.Gamelogic Gamelogic} if a remis occurs.
	 * @param reason An explanation why remis was called.
	 */
	public void remis(String reason);
	
}
