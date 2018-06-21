package fr.inria.astor.test.repair.evaluation.extensionpoints.patchsorting;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.extension.IdentifierPriorityCriterion;
import fr.inria.astor.core.validation.results.TestCasesProgramValidationResult;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author matias
 *
 */
public class PatchPriorizationTest {

	@Test
	public void testMath70PriorityLocalSolution() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "1", "-out",
				out.getAbsolutePath(), "-scope", "package", "-seed", "10", "-maxgen", "10000", "-stopfirst", "false",
				"-maxtime", "15", //
				"-patchprioritization", IdentifierPriorityCriterion.class.getName(), "-parameters",
				"maxnumbersolutions:2"

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(2, solutions.size());
		ProgramVariant variant = solutions.get(0);
		assertTrue(((TestCasesProgramValidationResult) variant.getValidationResult()).isRegressionExecuted());
		
		assertEquals(AstorOutputStatus.STOP_BY_PATCH_FOUND, main1.getEngine().getOutputStatus());
	}
}
