package mooctest;
public class IntMax {
	public int max(int x, int y, int z) {
		Integer i = null;
		if (x > y)
			y = x;
		if (y > z){ 
		//	z = x; // Seeded bug. Should be z = y
			z =y;
			i.toString();
		}
			return z;
	}
}
