package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.operators.RemoveOp;
import fr.inria.astor.approaches.jkali.operators.ReplaceIfBooleanOp;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.test.repair.core.BaseEvolutionaryTest;
import fr.inria.main.evolution.AstorMain;

/**
 * Execution of multiples kali scenarios.
 * 
 * @author Matias Martinez matias.martinez@inria.fr
 *
 */
public class jKaliTest extends BaseEvolutionaryTest {

	File out = null;

	public jKaliTest() {
		out = new File(ConfigurationProperties.getProperty("workingDirectory"));

	}

	protected List<ProgramVariant> testSeedExampleKali(String projectpath) throws Exception {
		AstorMain main1 = new AstorMain();
		File out = new File("./outputMutation/");
		String[] args = new String[] { "-dependencies",
				new File(projectpath + File.separator + "lib/").getAbsolutePath(), "-mode", "jkali", "-failing",
				"mooctest.TestSuite_all", "-location", new File(projectpath).getAbsolutePath(), "-package", "mooctest",
				"-srcjavafolder", "/src/", "-srctestfolder", "/junit/", "-binjavafolder", "bin/", "-bintestfolder",
				"bin/", "-flthreshold", "0.1", "-out", out.getAbsolutePath() };
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		assertTrue(main1.getEngine().getSolutions().size() > 0);
		return main1.getEngine().getSolutions();
	}

	@Test
	public void testSeedExampleKaliRemove() throws Exception {
		List<ProgramVariant> solutions = this.testSeedExampleKali("./examples/testKaliRemoveStatement");
		checkOperator(solutions, RemoveOp.class);
	}

	private void checkOperator(List<ProgramVariant> solutions, Class _class) {
		boolean present = false;
		for (ProgramVariant programVariant : solutions) {
			present = present || programVariant.getAllOperations().stream()
					.anyMatch(e -> _class.isInstance(e.getOperationApplied()));
		}
		assertTrue(present);
	}

	@Test
	public void testSeedExampleKaliIfFalse() throws Exception {
		List<ProgramVariant> solutions = this.testSeedExampleKali("./examples/testKaliIfFalse");
		checkOperator(solutions, ReplaceIfBooleanOp.class);

	}

	@Test
	public void testSeedExampleKaliAddReturnInt() throws Exception {
		List<ProgramVariant> solutions = this.testSeedExampleKali("./examples/testKaliAddReturnPrimitive");
		// checkOperator(solutions, ReplaceReturnOp.class);
	}

	@Test
	public void testSeedExampleKaliAddReturnVoid() throws Exception {
		List<ProgramVariant> solutions = this.testSeedExampleKali("./examples/testKaliAddReturnVoid");
		validatePatchExistence(ConfigurationProperties.getProperty("workingDirectory") + File.separator
				+ "AstorMain-testKaliAddReturnVoid/");

		// checkOperator(solutions, ReplaceReturnOp.class);

	}

	@SuppressWarnings("static-access")
	@Test
	@Ignore // We ignore this test, Gzoltar works different with Java 8
	public void testMath2ExampleRemoveModeOneSolution() throws Exception {
		AstorMain main1 = new AstorMain();

		String[] args = (new String[] { "-dependencies", "./examples/math_2/libmvn/", "-mode", "jkali", "-failing",
				"org.apache.commons.math3.distribution.HypergeometricDistributionTest", "-location",
				"./examples/math_2/", "-package", "org.apache.commons", "-stopfirst", "true", "-srcjavafolder",
				"/src/main/java/", "-srctestfolder", "/src/test/java/", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-stopfirst", "true", });
		main1.execute(args);
		// As 'stopfirst' is true, kali must return 1 solution
		assertEquals(1, main1.getEngine().getSolutions().size());
	}

	@SuppressWarnings("static-access")
	@Test
	@Ignore
	public void testMath2ExampleRemoveModeManySolutions() throws Exception {
		AstorMain main1 = new AstorMain();

		String[] args = (new String[] { "-dependencies", "./examples/math_2/libmvn/", "-mode", "jkali", "-failing",
				"org.apache.commons.math3.distribution.HypergeometricDistributionTest", "-location",
				new File("./examples/math_2/").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/main/java/", "-srctestfolder", "/src/test/java/", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-stopfirst", "false", });
		main1.execute(args);
		int numberSolution = main1.getEngine().getSolutions().size();
		// As 'stopfirst' is false, kali must return more than 1 solution
		assertTrue(numberSolution > 1);
	}

}
