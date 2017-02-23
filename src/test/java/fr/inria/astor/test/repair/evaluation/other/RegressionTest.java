package fr.inria.astor.test.repair.evaluation.other;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.validation.validators.TestCasesProgramValidationResult;
import fr.inria.astor.test.repair.evaluation.BaseEvolutionaryTest;
import fr.inria.main.evolution.AstorMain;
/**
 * Tests for assert the execution/ exclussion of regression 
 * @author Matias Martinez
 *
 */
public class RegressionTest extends BaseEvolutionaryTest{

	@Test
	public void testExecuteRegression() throws Exception {
		
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "10", "-maxgen", "50", "-stopfirst", "true",
				"-maxtime", "100",
		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());
		ProgramVariant variant = solutions.get(0);
		TestCasesProgramValidationResult validationResult = (TestCasesProgramValidationResult) variant.getValidationResult();
		
		assertTrue(validationResult.isRegressionExecuted());
		assertEquals(2071, validationResult.getCasesExecuted());
		validatePatchExistence(out + File.separator + "AstorMain-math_70/", solutions.size());

		
	}
	
	@Test
	public void testExecuteRegressionFalse() throws Exception {
		
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "10", "-maxgen", "50", "-stopfirst", "true",
				"-maxtime", "100",
				"-excludeRegression"

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());
		ProgramVariant variant = solutions.get(0);
		TestCasesProgramValidationResult validationResult = (TestCasesProgramValidationResult) variant.getValidationResult();
		assertFalse(validationResult.isRegressionExecuted());
		assertEquals(12, validationResult.getCasesExecuted());
		validatePatchExistence(out + File.separator + "AstorMain-math_70/", solutions.size());

		
	}
	
	@Test
	public void testExecuteRegressionNoSolution() throws Exception {
		
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "10", 
				//I write 1 generation, to only evaluate once
				"-maxgen", "1", "-stopfirst", "true",
				"-maxtime", "100",
				"-excludeRegression"

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		assertTrue(main1.getEngine().getSolutions().isEmpty());
		
		ProgramVariant variant = main1.getEngine().getVariants().get(0);
		TestCasesProgramValidationResult validationResult = (TestCasesProgramValidationResult) variant.getValidationResult();
		
		assertFalse(validationResult.isRegressionExecuted());
		//The failing
		assertEquals(12, validationResult.getCasesExecuted());
		assertEquals(1, validationResult.getFailureCount());
		assertEquals(11,validationResult.getPassingTestCases());

		
	}
}
