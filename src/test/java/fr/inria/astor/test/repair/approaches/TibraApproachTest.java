package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.extension.TibraApproach;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class TibraApproachTest {

	@SuppressWarnings("rawtypes")
	@Test
	public void testTimbraMath70() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-seed", "10");
		cs.command.put("-stopfirst", "true");
		cs.command.put("-maxgen", "2000");
		cs.command.put("-loglevel", "DEBUG");
		cs.command.put("-flthreshold", "1");
		cs.command.put("-mode", "custom");
		cs.command.put("-customengine", TibraApproach.class.getCanonicalName());

		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());
	}
}
