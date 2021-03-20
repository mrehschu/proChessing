package logic;

/**
 * This enum consists of the two colors {@link ChessColor#BLACK black} and {@link ChessColor#WHITE white}.
 * It marks the colors, accepted for this chess library.
 * @author Moritz Rehschuh
 */
public enum ChessColor {

	/**
	 * The color black: 
	 * #000000
	 */
	BLACK("#000000"),
	
	/**
	 * The color white: 
	 * #ffffff
	 */
	WHITE("#ffffff");
	
	String value;
	
	private ChessColor(String color) {
		value = color;
	}
}
