import java.util.ArrayList;

class Test{
	public static void main(String[] args) {
		ArrayList <Integer> map = new ArrayList<>();

		map.add(3);
		map.add(5);
		map.add(7);

		Integer x_1 = 3;
		Integer x_2 = 5;
		Integer x_3 = 7;

		System.out.println(map.contains(x_1));
		System.out.println(map.contains(x_2));
		System.out.println(map.contains(x_3));
	}
}