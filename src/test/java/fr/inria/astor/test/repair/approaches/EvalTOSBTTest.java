package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import fr.inria.astor.approaches.tos.core.EvalTOSBTApproach;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class EvalTOSBTTest {

	@Test
	public void testBT_Math85_1() throws Exception {
		// We want to find 4 solutions at most.
		int maxSolutions = 4;

		CommandSummary command = MathCommandsTests.getMath85Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", EvalTOSBTApproach.class.getCanonicalName());
		command.command.put("-maxgen", "0");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "false");
		command.command.put("-flthreshold", "0.24");
		command.command.put("-parameters", "disablelog:true:maxnumbersolutions:" + maxSolutions);

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof EvalTOSBTApproach);
		EvalTOSBTApproach approach = (EvalTOSBTApproach) main.getEngine();
		// Retrieve the buggy if condition.
		ModificationPoint mp198 = approach.getVariants().get(0)
				.getModificationPoints().stream().filter(e -> (e.getCodeElement().getPosition().getLine() == 198 && e
						.getCodeElement().getPosition().getFile().getName().equals("UnivariateRealSolverUtils.java")))
				.findAny().get();
		assertNotNull(mp198);
		// Let's indicate the number of candidate solutions we want to try
		approach.MAX_GENERATIONS = 1000;
		approach.analyzeModificationPoint(approach.getVariants().get(0), mp198);

		assertTrue(approach.getSolutions().size() > 0);
		assertEquals(maxSolutions, approach.getSolutions().size());
		// Call atEnd to print the solutions.
		approach.atEnd();

	}

	@Test
	public void testBT_Math_70_1() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-mode", "custom");
		command.command.put("-customengine", EvalTOSBTApproach.class.getCanonicalName());
		command.command.put("-maxgen", "100");
		command.command.put("-loglevel", "DEBUG");
		command.command.put("-scope", "local");
		command.command.put("-stopfirst", "false");
		command.command.put("-flthreshold", "0.5");
		command.command.put("-parameters", "disablelog:true");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		assertTrue(main.getEngine() instanceof EvalTOSBTApproach);
		EvalTOSBTApproach approach = (EvalTOSBTApproach) main.getEngine();
		assertTrue(approach.getSolutions().isEmpty());
	}

}
