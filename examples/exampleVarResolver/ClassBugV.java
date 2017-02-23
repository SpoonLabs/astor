/**
 * 
 * @author Matias Martinez
 *
 */
public class ClassBugV {

	public String f1 = "HolaField";
	
	public void test1(){
		
		String l1 = "HolaLocal";
		
		String l2 = f1;
		
		System.out.println("value "+l2);
		
	}
	
	
}
