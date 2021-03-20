package graphic;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class Keyboard {
	
	private static BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

	public static String readString() {
		String s = "";
		try {
			s = in.readLine();
		} catch (IOException e) { }
		return s;
	}

	public static int readint() throws NumberFormatException {
		String s = readString();
		int n = Integer.parseInt(s);
		return n;
	}

	public static long readlong() throws NumberFormatException {
		String s = readString();
		long n = Long.parseLong(s);
		return n;
	}

	public static float readfloat() throws NumberFormatException {
		String s = readString();
		float n = Float.valueOf(s).floatValue();
		return n;
	}

	public static double readdouble() throws NumberFormatException {
		String s = readString();
		double n = Double.valueOf(s).doubleValue();
		return n;
	}
}
