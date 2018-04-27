package fr.inria.astor.test.repair.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientSpaceScope;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.astor.util.CommandSummary;
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
		cs.append("-parameters", ("logtestexecution:true:runexternalvalidator:true"));

		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());
		ProgramVariant variant = solutions.get(0);

		OperatorInstance mi = variant.getOperations().values().iterator().next().get(0);
		assertNotNull(mi);
		assertEquals(IngredientSpaceScope.LOCAL, mi.getIngredientScope());

		assertEquals("return solve(f, min, max)", mi.getModified().toString());
		

	}

}
