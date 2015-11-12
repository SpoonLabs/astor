package fr.inria.astor.test.repair.evaluation;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.AbstractMain;
import fr.inria.main.evolution.AstorMain;
/**
 * MutRepair test
 * @author Matias Martinez matias.martinez@inria.fr
 *
 */
public class MutRepairTest extends BaseEvolutionaryTest {
	
	
	@Test
	public void testMath85issue280() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/math_85/libs/junit-4.4.jar").getAbsolutePath();//"/home/matias/.m2/repository/junit/junit/4.4/junit-4.4.jar";

		String[] args = new String[] { 
				"-dependencies", dep, "-mode", "mutation", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons",
				"-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder", "/src/java/",
				"-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.1", "-stopfirst", "false",
				"-maxgen", "100"};
		System.out.println(Arrays.toString(args));
		main1.main(args);
		validatePatchExistence(ConfigurationProperties.getProperty("workingDirectory")+File.separator+"AstorMain-math_85/",1);
		
	}
	
	
	@Test
	public void testMath288() throws Exception {
		AstorMain main1 = new AstorMain();
		File out = new File("./outputMutation/");
			String[] args = new String[] {
				"-dependencies", new File("examples/Math-issue-288/lib/junit-4.4.jar").getAbsolutePath(),
				"-mode","mutation",
				"-failing", 
				"org.apache.commons.math.optimization.linear.SimplexSolverTest", 
				"-package", "org",
				"-location",new File("./examples/Math-issue-280/").getAbsolutePath(),							
				"-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin",
				"-srcjavafolder", "/src/main/java/",
				"-srctestfolder", "/src/test/java/", 
				"-binjavafolder", "/target/classes/", 
				"-bintestfolder","/target/test-classes/",
				"-flthreshold","0.5",
				"-out",out.getAbsolutePath()
				};
		System.out.println(Arrays.toString(args));
		main1.main(args);
		//validatePatchExistence(ConfigurationProperties.getProperty("workingDirectory")+File.separator+"MainjGenProg-testKaliRemoveStatement/",1);
		
	}
	
	@Override
	public AbstractMain createMain() {
		return  new AstorMain();
	}
	
	@Deprecated
	public void Math280() throws Exception {
		AstorMain main1 = new AstorMain();
		File out = new File("./outputMutation/");
			String[] args = new String[] {
				"-dependencies", new File("examples/Math-issue-280/lib/junit-4.4.jar").getAbsolutePath(),
				"-mode","mutation",
				"-failing", 
				"org.apache.commons.math.distribution.NormalDistributionTest", 
				"-package", "org",
				"-location",new File("./examples/Math-issue-280/").getAbsolutePath(),							
				"-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin",
				"-srcjavafolder", "/src/java/",
				"-srctestfolder", "/test/", 
				"-binjavafolder", "/target/classes/", 
				"-bintestfolder","/target/test-classes/",
				"-flthreshold","0.5",
				"-out",out.getAbsolutePath()
				};
		System.out.println(Arrays.toString(args));
		main1.main(args);
		//validatePatchExistence(ConfigurationProperties.getProperty("workingDirectory")+File.separator+"MainjGenProg-testKaliRemoveStatement/",1);
		
	}
	
	@Override
	public void generic(String location, String folder, String regression, String failing, String dependenciespath,
			String packageToInstrument, double thfl) throws Exception {
		// TODO Auto-generated method stub
		
	}
	
}
