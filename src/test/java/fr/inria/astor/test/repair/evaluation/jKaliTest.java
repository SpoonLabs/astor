package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.evolution.AstorMain;
/**
 * Execution of multiples kali scenarios.
 * @author Matias Martinez matias.martinez@inria.fr
 *
 */
public class jKaliTest extends BaseEvolutionaryTest{
	
	File out = null;
	public jKaliTest(){
		out = new File(ConfigurationProperties.getProperty("workingDirectory"));

	}
	
	protected void testSeedExampleKali(String projectpath) throws Exception {
		AstorMain main1 = new AstorMain();
		File out = new File("./outputMutation/");
		String[] args = new String[] {
				"-dependencies", new File(projectpath+File.separator+"lib/").getAbsolutePath(),
				"-mode","statement-remove",
				"-failing", 
				"mooctest.TestSuite_all", 
				"-location",new File(projectpath).getAbsolutePath(),							
				"-package", "mooctest",
				"-srcjavafolder", "/src/",
				"-srctestfolder", "/junit/", 
				"-binjavafolder", "/bin/", 
				"-bintestfolder","/bin/",
				"-flthreshold","0.1",
				"-out",out.getAbsolutePath()
				};
		System.out.println(Arrays.toString(args));
		main1.main(args);
		
	}
	@Test
	public void testSeedExampleKaliRemove() throws Exception{
		this.testSeedExampleKali("./examples/testKaliRemoveStatement");
		validatePatchExistence(ConfigurationProperties.getProperty("workingDirectory")+File.separator+"AstorMain-testKaliRemoveStatement/",1);
	}
	@Test
	public void testSeedExampleKaliIfFalse() throws Exception{
		this.testSeedExampleKali("./examples/testKaliIfFalse");
		validatePatchExistence(ConfigurationProperties.getProperty("workingDirectory")+File.separator+"AstorMain-testKaliIfFalse/",1);
		
	}
	
	@Test
	public void testSeedExampleKaliAddReturnInt() throws Exception{
		this.testSeedExampleKali("./examples/testKaliAddReturnPrimitive");
		validatePatchExistence(ConfigurationProperties.getProperty("workingDirectory")+File.separator+"AstorMain-testKaliAddReturnPrimitive/");
		
	}
	
	@Test
	public void testSeedExampleKaliAddReturnVoid() throws Exception{
		this.testSeedExampleKali("./examples/testKaliAddReturnVoid");
		validatePatchExistence(ConfigurationProperties.getProperty("workingDirectory")+File.separator+"AstorMain-testKaliAddReturnVoid/");
		
	}
	

	
	@SuppressWarnings("static-access")
	@Test
	@Ignore //We ignore this test, Gzoltar works different with Java 8 
	public void testMath2ExampleRemoveModeOneSolution() throws Exception {
		AstorMain main1 = new AstorMain();

		String[] args = (new String[] { 
				"-dependencies", "./examples/math_2/libmvn/",
				"-mode", "statement-remove",
				"-failing", "org.apache.commons.math3.distribution.HypergeometricDistributionTest", "-location",
				"./examples/math_2/", "-package", "org.apache.commons", "-stopfirst", "true", "-srcjavafolder", "/src/main/java/", "-srctestfolder",
				"/src/test/java/", "-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes",
				"-stopfirst","true",
		});
		main1.main(args);
		int numberSolution = numberSolutions(out + File.separator + "AstorMain-math_2");
		//As  'stopfirst' is true, kali must return 1 solution
		assertEquals(1,numberSolution);
	}

	@SuppressWarnings("static-access")
	@Test
	@Ignore
	public void testMath2ExampleRemoveModeManySolutions() throws Exception {
		AstorMain main1 = new AstorMain();

		String[] args = (new String[] { 
				"-dependencies", "./examples/math_2/libmvn/",
				"-mode", "statement-remove",
				"-failing", "org.apache.commons.math3.distribution.HypergeometricDistributionTest", 
				"-location",
				new File("./examples/math_2/").getAbsolutePath(), 
				"-package", "org.apache.commons", "-srcjavafolder", "/src/main/java/", "-srctestfolder",
				"/src/test/java/", "-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes",
				"-stopfirst","false",
		});
		main1.main(args);
		int numberSolution = numberSolutions(out + File.separator + "AstorMain-math_2");
		//As  'stopfirst' is false, kali must return more than 1 solution
		assertTrue(numberSolution > 1);
	}
		

}
