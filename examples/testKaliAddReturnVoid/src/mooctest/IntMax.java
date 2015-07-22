package mooctest;
public class IntMax {
	public int max(int x, int y, int z) {
		String message = null;
		if (x > y){
			y = x;
		}	
		if (y > z){ 
			z = y;
			print(message);
		}
		return z;
	}
	public void print(String s){
	//	if(true) return;
		System.out.println(s.toString());
	}
}
