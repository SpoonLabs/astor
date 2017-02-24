/**
 * 
 * @author Matias Martinez
 *
 */
public class ClassBugV {

	public String f1 = "HolaField";
	
	public void test1(){
		
		String l1 = "HolaLocal";
		
		String l2 = f1; //and try to replace the field f1 by local l1
		
		System.out.println("value "+l2);
		
	}
	
	public void test2(){
		
		String l1 = "HolaLocal";
		String l2 = "otro";
		
		l1 = l2; //and try replace the local l2 by field f1
		
		System.out.println("value "+l1);
		
	}
	
	public void test3(String p1){
		
		String l1 = "HolaLocal";
		
		String l2 = f1; //and try to replace the field f1 by param p1
		
		System.out.println("value "+l2);
		
	}
	
	public void test4(){
		
		String l1 = "HolaLocal";
		String l2 = "otro";
		
		l1 = l2; //and try replace the local l1 by field f1
		
		System.out.println("value "+l1);
		
	}
}
