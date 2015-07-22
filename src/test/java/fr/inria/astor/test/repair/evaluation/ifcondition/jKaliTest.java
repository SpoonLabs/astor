package fr.inria.astor.test.repair.evaluation.ifcondition;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import fr.inria.main.evolution.MainjGenProg;
/**
 * Execution of multiples kali scenarios.
 * @author Matias Martinez matias.martinez@inria.fr
 *
 */
public class jKaliTest {

	protected void testSeedExampleKali(String projectpath) throws Exception {
		MainjGenProg main1 = new MainjGenProg();
		String[] args = new String[] {
				"-dependencies", new File(projectpath+File.separator+"lib/").getAbsolutePath(),
				"-mode","statement-remove",
				"-failing", 
				"mooctest.TestSuite_all", 
				//"mooctest.TestSuite_all#test1_1:mooctest.TestSuite_all#test1_2:mooctest.TestSuite_all#test1_3", 
				"-location",new File(projectpath).getAbsolutePath(),							
				"-package", "mooctest",
				"-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin",
				"-srcjavafolder", "/src/",
				"-srctestfolder", "/junit/", 
				"-binjavafolder", "/bin/", 
				"-bintestfolder","/bin/",
				"-flthreshold","0.1",
				};
		System.out.println(Arrays.toString(args));
		main1.main(args);
		
	}
	@Test
	public void testSeedExampleKaliRemove() throws Exception{
		this.testSeedExampleKali("./examples/testKaliRemoveStatement");
	}
	@Test
	public void testSeedExampleKaliIfFalse() throws Exception{
		this.testSeedExampleKali("./examples/testKaliIfFalse");
	}
	
	@Test
	public void testSeedExampleKaliAddReturnInt() throws Exception{
		this.testSeedExampleKali("./examples/testKaliAddReturnPrimitive");
	}
	
	@Test
	public void testSeedExampleKaliAddReturnVoid() throws Exception{
		this.testSeedExampleKali("./examples/testKaliAddReturnVoid");
	}
	
	

}
