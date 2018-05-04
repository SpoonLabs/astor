package fr.inria.astor.test.repair.evaluation.extensionpoints.operatorselection;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Before;
import org.junit.Test;

import fr.inria.astor.core.solutionsearch.AstorCoreEngine;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.core.solutionsearch.spaces.operators.WeightedRandomOperatorSelection;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class SelectionOperatorTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testWeightedRandom() throws Exception {

		CommandSummary csDefault = MathCommandsTests.getMath70Command();
		csDefault.command.put("-maxgen", "0");
		csDefault.command.put("-opselectionstrategy", WeightedRandomOperatorSelection.class.getName());

		csDefault.append("-parameters", "weightsopselection:0.25_0.25_0.25_0.25");
		AstorMain main1 = new AstorMain();
		main1.execute(csDefault.flat());

		AstorCoreEngine  ap =  main1.getEngine();

		assertTrue(WeightedRandomOperatorSelection.class.isInstance(ap.getOperatorSelectionStrategy()));
	}

	@Test
	public void testWeightedRandom1Exception() throws Exception {
		try {
			CommandSummary csDefault = MathCommandsTests.getMath70Command();
			csDefault.command.put("-maxgen", "0");
			csDefault.command.put("-opselectionstrategy", WeightedRandomOperatorSelection.class.getName());
			csDefault.append("-parameters", "weightsopselection:0.25");
			AstorMain main1 = new AstorMain();
			main1.execute(csDefault.flat());
			fail();
		} catch (Exception e) {

		}

		// Not double
		try {
			CommandSummary csDefault = MathCommandsTests.getMath70Command();
			csDefault.command.put("-maxgen", "0");
			csDefault.command.put("-opselectionstrategy", WeightedRandomOperatorSelection.class.getName());
			csDefault.append("-parameters", "weightsopselection:0.a5_0.25_0.25_0.25");
			AstorMain main1 = new AstorMain();
			main1.execute(csDefault.flat());
			fail();
		} catch (Exception e) {

		}
	}

	@Test
	public void testWeightedRandom1Operator() throws Exception {
		CommandSummary csDefault = MathCommandsTests.getMath70Command();
		csDefault.command.put("-maxgen", "0");
		csDefault.command.put("-opselectionstrategy", WeightedRandomOperatorSelection.class.getName());

		csDefault.append("-parameters", "weightsopselection:1_0.0_0.0_0.0");
		AstorMain main1 = new AstorMain();
		main1.execute(csDefault.flat());

		AstorCoreEngine  ap =  main1.getEngine();

		assertTrue(WeightedRandomOperatorSelection.class.isInstance(ap.getOperatorSelectionStrategy()));
		WeightedRandomOperatorSelection ws = (WeightedRandomOperatorSelection) ap.getOperatorSelectionStrategy();
		AstorOperator aoperator = ws.getNextOperator();
		AstorOperator ab0 = ap.getOperatorSpace().getOperators().get(0);
		assertEquals(ab0, aoperator);

		AstorOperator aoperator1 = ws.getNextOperator();
		assertEquals(ab0, aoperator1);

	}

}
