package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.test.repair.core.BaseEvolutionaryTest;
import fr.inria.main.evolution.AstorMain;

/**
 * Test for MutRepair engine. (it mutates if conditions)
 * 
 * @author Matias Martinez matias.martinez@inria.fr
 *
 */
public class MutRepairTest extends BaseEvolutionaryTest {

	@Test
	// @Ignore
	public void testMath85issue280() throws Exception {
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath() + File.pathSeparator
				+ new File("./examples/libs/commons-discovery-0.5.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "mutation", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.1", "-stopfirst", "true",
				"-maxtime", "15", "-seed", "10"

		};
		System.out.println(Arrays.toString(args));
		AstorMain m = new AstorMain();
		m.execute(args);
		assertTrue(m.getEngine().getSolutions().size() > 0);
		// location= org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils
		// line= 198
		// original statement= if ((fa * fb) >= 0.0) {
	}

	@Test
	@Ignore
	public void testMath288() throws Exception {
		File out = new File("./outputMutation/");
		String[] args = new String[] { "-dependencies", new File("./examples/libs/junit-4.4.jar").getAbsolutePath(),
				"-mode", "mutation", "-failing", "org.apache.commons.math.optimization.linear.SimplexSolverTest",
				"-location", new File("./examples/Math-issue-288/").getAbsolutePath(), "-srcjavafolder",
				"/src/main/java/", "-srctestfolder", "/src/test/java/", "-binjavafolder", "/target/classes/",
				"-bintestfolder", "/target/test-classes/", "-flthreshold", "0.1", "-out", out.getAbsolutePath(),
				"-stopfirst", "true", "-seed", "10" };
		System.out.println(Arrays.toString(args));
		AstorMain main1 = new AstorMain();
		main1.execute(args);
		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		log.debug("Solutions " + solutions);
		assertNotNull(solutions);
		int nrsolutions = solutions.size();
		assertEquals(1, nrsolutions);

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testIssue196bis_repairing_jmutrepair() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jmutrepair", //

				"-location", new File("./examples/issues/LeapYearIssue196-bis/").getAbsolutePath(), "-package",
				"LeapYear.bug1", "-srcjavafolder", "/src/main/java/", "-srctestfolder", "/src/test/java/",
				"-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes", "-javacompliancelevel",
				"7", "-flthreshold", "0.5", "-stopfirst", "true"// Forced

		};

		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> variants = main1.getEngine().getVariants();
		assertTrue(variants.size() > 0);

		ProgramVariant oneVariant = variants.get(0);
		assertTrue(oneVariant.getModificationPoints().size() > 0);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertEquals(1, solutions.size());

	}

	@Test
	public void testExampleMutateReturn() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.10.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep,
				//
				"-mode", "jmutrepair",

				"-location", new File("./examples/example_return_mutation").getAbsolutePath(),

				"-srcjavafolder", "/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.1",
				"-stopfirst", "true", "-maxgen", "400", "-scope", "package", "-seed", "10", "-loglevel", "DEBUG" };
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);

		assertTrue(super.existPatchWithCode(solutions, "(e % 2) == 0"));
		assertFalse(super.existPatchWithCode(solutions, "(e % 2) != 0"));
	}

	@Test
	public void testExample_return_arithmetic() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.10.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep,
				//
				"-mode", "jmutrepair",

				"-location", new File("./examples/example_return_arthmetic").getAbsolutePath(),

				"-srcjavafolder", "/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.1",
				"-stopfirst", "true", "-maxgen", "400", "-scope", "package", "-seed", "10", "-loglevel", "DEBUG" };
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);

		assertTrue(super.existPatchWithCode(solutions, "x + y"));
		assertFalse(super.existPatchWithCode(solutions, "x - y"));
	}

}
