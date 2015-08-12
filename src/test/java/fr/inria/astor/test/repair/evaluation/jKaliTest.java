package fr.inria.astor.test.repair.evaluation;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.AbstractMain;
import fr.inria.main.evolution.MainjGenProg;
/**
 * Execution of multiples kali scenarios.
 * @author Matias Martinez matias.martinez@inria.fr
 *
 */
public class jKaliTest extends BaseEvolutionaryTest{

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
		validatePatchExistence(ConfigurationProperties.getProperty("workingDirectory")+File.separator+"MainjGenProg-testKaliRemoveStatement/",1);
	}
	@Test
	public void testSeedExampleKaliIfFalse() throws Exception{
		this.testSeedExampleKali("./examples/testKaliIfFalse");
		validatePatchExistence(ConfigurationProperties.getProperty("workingDirectory")+File.separator+"MainjGenProg-testKaliIfFalse/",1);
		
	}
	
	@Test
	public void testSeedExampleKaliAddReturnInt() throws Exception{
		this.testSeedExampleKali("./examples/testKaliAddReturnPrimitive");
		validatePatchExistence(ConfigurationProperties.getProperty("workingDirectory")+File.separator+"MainjGenProg-testKaliAddReturnPrimitive/",6);
		
	}
	
	@Test
	public void testSeedExampleKaliAddReturnVoid() throws Exception{
		this.testSeedExampleKali("./examples/testKaliAddReturnVoid");
		validatePatchExistence(ConfigurationProperties.getProperty("workingDirectory")+File.separator+"MainjGenProg-testKaliAddReturnVoid/",3);
		
	}
	
	
	@Override
	public AbstractMain createMain() {
			return null;
	}
	@Override
	public void generic(String location, String folder, String regression, String failing, String dependenciespath,
			String packageToInstrument, double thfl) throws Exception {
		
	}
	
	

}
