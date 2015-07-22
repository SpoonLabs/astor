package mooctest;
public class IntMax {
	public int max(int x, int y, int z) {
		Integer i = null;
		if (x > y)
			y = x;
		if (y > z){  //The if has bug
			z = x; // Seeded bug. Should be z = y
		}else{//
			z = y;
		}
		return z;
	}
}
