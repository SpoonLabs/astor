package fr.inria.astor.test;

import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.FaultLocalizationMain;

public class FaultLocalizationTest {
	@org.junit.Test
	public void testFLMath70() throws Exception {
		FaultLocalizationMain main = new FaultLocalizationMain();
		CommandSummary cs = MathCommandsTests.getMath70Command();
		// by default, max generations is zero, that means, it does not evolve
		cs.command.put("-faultlocalization", "gzoltar");
		cs.command.put("-flthreshold", "0.0001");

		// We execute astor for creating the model and run FL

		main.execute(cs.flat());

	}

	@org.junit.Test
	public void testFLMath70FC() throws Exception {
		FaultLocalizationMain main = new FaultLocalizationMain();
		CommandSummary cs = MathCommandsTests.getMath70Command();
		// by default, max generations is zero, that means, it does not evolve
		cs.command.put("-faultlocalization", "flacoco");
		cs.command.put("-flthreshold", "0.0001");

		// We execute astor for creating the model and run FL

		main.execute(cs.flat());

	}

}