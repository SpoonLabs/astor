package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

import org.junit.Test;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.util.TimeUtil;
import fr.inria.main.AbstractMain;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class JGenProgTest extends BaseEvolutionaryTest {

	File out = null;
	
	public JGenProgTest() {
		out = new File(ConfigurationProperties.getProperty("workingDirectory"));
	}
	@Override
	public void generic(String location, String folder, String regression, String failing, String dependenciespath,
			String packageToInstrument, double thfl) throws Exception {

		getMain().run(location, folder, dependenciespath, packageToInstrument, thfl, failing);

	}

	@Override
	public AbstractMain createMain() {
		if (main == null) {
			return new AstorMain();
		}
		return main;
	}


	@Test
	public void testExample280CommandLine() throws Exception {
		AstorMain main1 = new AstorMain();
		String[] args = new String[] { "-bug280"};
		main1.main(args);
		validatePatchExistence(out + File.separator + "AstorMain-math_85/");
	}
	
	@Test
	public void testExample288CommandLine() throws Exception {
		AstorMain main1 = new AstorMain();
		String[] args = new String[] { "-bug288"};
		main1.main(args);
		validatePatchExistence(out + File.separator + "AstorMain-Math-issue-288/");
	}
	
	//@Test
	public void testExample340CommandLine() throws Exception {
		AstorMain main1 = new AstorMain();
		String[] args = new String[] { "-bug340"};
		main1.main(args);
		validatePatchExistence(out + File.separator + "Math-issue-340/");
	}
	
	@Test
	public void testExample309CommandLine() throws Exception {
		AstorMain main1 = new AstorMain();
		String[] args = new String[] { "-bug309"};
		main1.main(args);
		validatePatchExistence(out + File.separator + "Math-issue-309/");
	}
	
	/**
	 * The fix is a replacement of an return statement
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMath85issue280() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons",
				"-srcjavafolder", "/src/java/",
				"-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				"-maxgen", "100", "-scope", "package" };
		System.out.println(Arrays.toString(args));
		main1.main(args);
		validatePatchExistence(out + File.separator + "AstorMain-math_85/", 5);
	}
	
	/**
	 *
	 * 
	 * @throws Exception
	 */


	/**
	 * Math 70 bug can be fixed by replacing a method invocation inside a return statement. 
	 * + return solve(f, min, max); 
	 * - return solve(min, max);
	 * One solution with local scope, another with package
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMath70TwoSolutions() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package",
				"org.apache.commons",
				"-srcjavafolder", "/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope" , "package",
				"-seed", "10",
				"-maxgen", "50", 
				};
		System.out.println(Arrays.toString(args));
		main1.main(args);
		//Last minute comment: I suspect Math-70 has flaky test cases, so, number of solutions discovered can be different
		validatePatchExistence(out + File.separator + "AstorMain-math_70/", 2);

	}
	
	

	/**
	 * Test to assert the max time argument (which it does not includes the Fault localization step)
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	@Test
	public void testRunMainTime() throws Exception {

		Date init = new Date();
		AstorMain main1 = new AstorMain();
		String pathExample = new File("./examples/Math-0c1ef").getAbsolutePath();
		main1.main(new String[] {
				"-dependencies",
				"examples/Math-0c1ef/lib/junit-4.11.jar" + File.pathSeparator + pathExample + File.separator
						+ "lib/hamcrest-core-1.3.jar", "-id", "tttMath-0c1ef", "-failing",
				"org.apache.commons.math3.primes.PrimesTest", "-location", pathExample, "-package",
				"org.apache.commons", "-maxgen", "400", "-population", "2", "-saveall", "-maxtime", "1" });
		long t = TimeUtil.deltaInMinutes(init);
		assertTrue(t > 1);// more than one minute
		assertFalse(t < 2);// less than two minutes
	}

	
	
	@Test
	public void testArguments() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package",
				"org.apache.commons",
				"-srcjavafolder", "/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope" , "package",
				"-seed", "10",
				 "-stopfirst","true",
				"-maxgen", "50", 
				"-saveall","false"};
		boolean correct = main1.processArguments(args);
		assertTrue(correct);
		
		String javahome = ConfigurationProperties.properties
		.getProperty("jvm4testexecution");
	
		assertNotNull(javahome);
		
		assertTrue(javahome.endsWith("bin"));
	}
	



}
