import java.util.regex.Pattern;

class TryPat {
	static boolean parseLevel(String tag){
		return Pattern.matches("L[0-9]+", tag.subSequence(0, tag.length()));
	}
	public static void main(String[] args) {
		String try1 = "L15";
		String try2 = "L1";
		String try3 = "L0";
		String try4 = "15";

		System.out.println(try1.substring(1) + parseLevel(try1));
		System.out.println(try2.substring(1) + parseLevel(try2));
		System.out.println(try3.substring(1) + parseLevel(try3));
		System.out.println(try4.substring(1) + parseLevel(try4));
	}
}