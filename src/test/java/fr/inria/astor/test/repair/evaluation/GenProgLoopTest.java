package fr.inria.astor.test.repair.evaluation;

import java.io.File;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.stats.Stats;
import fr.inria.main.AbstractMain;
/**
 * Main Test: Executes the Genetic Programming process
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class GenProgLoopTest extends BaseEvolutionaryTest {

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	@Before
	public void setUp() throws Exception {

		ConsoleAppender console = new ConsoleAppender();
		String PATTERN = "%m%n";
		console.setLayout(new PatternLayout(PATTERN));
		console.setThreshold(Level.INFO);
		console.activateOptions();
		Logger.getRootLogger().getLoggerRepository().resetConfiguration();
		Logger.getRootLogger().addAppender(console);
	}
	
	/** Fixes a bug in a dummy artificial project.
	 * The source code of the dummy test project is in src/test/resources/testProject/src/main/java
	 * 
	 * The tests (incl the failing one) is in src/test/resources/testProject/src/test/java
	 * 
	 * To execute the test (and the failing one!) in order to see the bug:
	 * $ cd src/test/resources/testProject/
	 * $ mvn test
	 * outputMutation
	 */
	@Test
	public void testGpBasic() throws Exception {
		ProjectRepairFacade rep = null;
		
		/*String classpath = "C:/Users/adam/.m2/repository/junit/junit/4.11/junit-4.11.jar;C:/Users/adam/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar;C:/Users/adam/.m2/repository/commons-io/commons-io/2.4/commons-io-2.4.jar;C:/Users/adam/.m2/repository/org/easymock/easymock/3.1/easymock-3.1.jar;C:/Users/adam/.m2/repository/cglib/cglib-nodep/2.2.2/cglib-nodep-2.2.2.jar;C:/Users/adam/.m2/repository/org/objenesis/objenesis/1.2/objenesis-1.2.jar;";
		String location = "C:/Personal/develop/astor/workspace-version/"; 
		String projectName = "commons-lang3-before";
		String packageToMine = "org.apache.commons.math";
		String failing = "org.apache.commons.math.distribution.NormalDistributionTest";
		String experimentName = "math-bug-280"; 
		
		*/
		String dependenciespath= "examples/Math-issue-280/lib/junit-4.4.jar";
		//Thread.currentThread().getContextClassLoader().getResource("Math-issue-280/lib/junit-4.4.jar").getPath();
String folder= "Math-issue-280";
String failing= "org.apache.commons.math.distribution.NormalDistributionTest";
Stats currentStat= new Stats();
File f = new File("examples/Math-issue-280/");
String location = f.getParent();
	String regression= "org.apache.commons.math.distribution.NormalDistributionTest";
	String packageToInstrument= "org.apache.commons";
	double thfl = 0.5;

	}

	
	@Test
	public void testExampleMath280() throws Exception{
		
		String dependenciespath= "examples/Math-issue-280/lib/junit-4.4.jar";
		String folder= "Math-issue-280";
		String failing= "org.apache.commons.math.distribution.NormalDistributionTest";
		File f = new File("examples/Math-issue-280/");
		String location = f.getParent();
		String regression= "org.apache.commons.math.distribution.NormalDistributionTest";
		String packageToInstrument= "org.apache.commons";
		double thfl = 0.5;

		this.generic(location, folder, regression, failing, dependenciespath, packageToInstrument, thfl);
		
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
