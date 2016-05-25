package fr.inria.astor.test.repair.evaluation;

import java.io.File;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.AbstractMain;
import fr.inria.main.evolution.AstorMain;
/**
 * Test for MutRepair engine. (it mutates if conditions)
 * @author Matias Martinez matias.martinez@inria.fr
 *
 */
public class MutRepairTest extends BaseEvolutionaryTest {
	
	
	@Test
	//@Ignore
	public void testMath85issue280() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { 
				"-dependencies", dep, "-mode", "mutation", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons",
				 "-srcjavafolder", "/src/java/",
				"-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.1", 
				"-stopfirst", "true",
				"-maxtime", "10",
				"-seed","10"};
		System.out.println(Arrays.toString(args));
		main1.main(args);
		validatePatchExistence(ConfigurationProperties.getProperty("workingDirectory")+File.separator+"AstorMain-math_85/");
		
	}
	
	
	@Test
	public void testMath288() throws Exception {
		AstorMain main1 = new AstorMain();
		File out = new File("./outputMutation/");
			String[] args = new String[] {
				"-dependencies", new File("./examples/libs/junit-4.4.jar").getAbsolutePath(),
				"-mode","mutation",
				"-failing", 
				"org.apache.commons.math.optimization.linear.SimplexSolverTest", 
				"-location",new File("./examples/Math-issue-288/").getAbsolutePath(),							
				"-srcjavafolder", "/src/main/java/",
				"-srctestfolder", "/src/test/java/", 
				"-binjavafolder", "/target/classes/", 
				"-bintestfolder","/target/test-classes/",
				"-flthreshold","0.1",
				"-out",out.getAbsolutePath(),
				"-stopfirst", "true",
				"-seed","10"
				};
		System.out.println(Arrays.toString(args));
		main1.main(args);
		validatePatchExistence(ConfigurationProperties.getProperty("workingDirectory")
				+File.separator+"AstorMain-Math-issue-288/");
	}
	



	
	

}
