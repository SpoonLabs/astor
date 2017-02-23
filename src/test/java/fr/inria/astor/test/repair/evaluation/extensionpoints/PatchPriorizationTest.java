package fr.inria.astor.test.repair.evaluation.extensionpoints;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.extension.IdentifierPriorityCriterion;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.validation.validators.TestCasesProgramValidationResult;
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
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "package", 
				"-seed", "10", "-maxgen", "500", "-stopfirst", "false",
				"-maxtime", "10",//
				"-patchprioritization", IdentifierPriorityCriterion.class.getName(),

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(2, solutions.size());
		ProgramVariant variant = solutions.get(0);
		assertTrue(((TestCasesProgramValidationResult) variant.getValidationResult()).isRegressionExecuted());
		//TODO: include new assertions
	}
}
