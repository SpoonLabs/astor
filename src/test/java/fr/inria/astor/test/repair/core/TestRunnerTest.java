package fr.inria.astor.test.repair.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import fr.inria.astor.core.validation.results.TestCasesProgramValidationResult;
import fr.inria.astor.core.validation.results.TestResult;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class TestRunnerTest {

	@SuppressWarnings("rawtypes")
	@Test
	public void testMath70LocalSimpleExternal() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-flthreshold", "1");
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "INFO");
		cs.command.put("-saveall", "true");
		cs.append("-parameters", ("logtestexecution:true:runjava7code:true"));

		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());
		ProgramVariant variant = solutions.get(0);

		OperatorInstance mi = variant.getOperations().values().iterator().next().get(0);
		assertNotNull(mi);
		assertEquals(IngredientPoolScope.LOCAL, mi.getIngredientScope());

		assertEquals("return solve(f, min, max)", mi.getModified().toString());

	}

	@SuppressWarnings("rawtypes")
	@Test
	public void testMath70LocalFailingTest() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-maxgen", "0");
		cs.command.put("-flthreshold", "1");
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "INFO");
		cs.command.put("-saveall", "true");
		cs.append("-parameters", ("logtestexecution:true:runjava7code:true"));

		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		List<ProgramVariant> variants = main1.getEngine().getVariants();
		assertTrue(variants.size() > 0);
		assertEquals(1, variants.size());
		ProgramVariant variant = variants.get(0);

		assertTrue(variant.getValidationResult() instanceof TestCasesProgramValidationResult);

		TestCasesProgramValidationResult tcresultvalidation = (TestCasesProgramValidationResult) variant
				.getValidationResult();
		TestResult tresults = tcresultvalidation.getTestResult();

		assertEquals(1, tresults.failures);
		assertEquals(1, tresults.failTest.size());
		assertTrue(tresults.failTest.get(0)
				.startsWith("testMath369(org.apache.commons.math.analysis.solvers.BisectionSolverTest"));

	}

}
