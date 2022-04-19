package fr.inria.astor.test;

import static org.junit.Assert.assertFalse;

import fr.inria.astor.core.faultlocalization.FaultLocalizationResult;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.FaultLocalizationMain;
import fr.inria.main.FaultLocalizationMain.FaultLocalization;

public class FaultLocalizationTest {
	@org.junit.Test
	public void testFLMath70_GZoltar() throws Exception {
		FaultLocalizationMain main = new FaultLocalizationMain();
		CommandSummary cs = MathCommandsTests.getMath70Command();
		// by default, max generations is zero, that means, it does not evolve
		cs.command.put("-faultlocalization", FaultLocalization.GZOLTAR.name());
		cs.command.put("-flthreshold", "0.0001");

		// We execute astor for creating the model and run FL

		FaultLocalizationResult er = (FaultLocalizationResult) main.execute(cs.flat());
		System.out.println(er);
		assertFalse(er.getCandidates().isEmpty());
		assertFalse(er.getExecutedTestCasesMethods().isEmpty());
		assertFalse(er.getFailingTestCasesClasses().isEmpty());
		assertFalse(er.getFailingTestCasesMethods().isEmpty());

	}

	@org.junit.Test
	public void testFLMath70Flacoco() throws Exception {
		FaultLocalizationMain main = new FaultLocalizationMain();
		CommandSummary cs = MathCommandsTests.getMath70Command();
		// by default, max generations is zero, that means, it does not evolve
		cs.command.put("-faultlocalization", FaultLocalization.FLACOCO.name());
		cs.command.put("-flthreshold", "0.0001");

		// We execute astor for creating the model and run FL

		FaultLocalizationResult er = (FaultLocalizationResult) main.execute(cs.flat());
		System.out.println(er);
		assertFalse(er.getCandidates().isEmpty());
		assertFalse(er.getExecutedTestCasesMethods().isEmpty());
		assertFalse(er.getFailingTestCasesClasses().isEmpty());
		assertFalse(er.getFailingTestCasesMethods().isEmpty());
	}

	@org.junit.Test
	public void testFLMath70Ngz() throws Exception {
		FaultLocalizationMain main = new FaultLocalizationMain();
		CommandSummary cs = MathCommandsTests.getMath70Command();
		// by default, max generations is zero, that means, it does not evolve
		cs.command.put("-faultlocalization", FaultLocalization.GZOLTAR1_7.name());
		cs.command.put("-flthreshold", "0.0000");
		cs.command.put("-parameters", "includeZeros:false:keepGZoltarFiles:false");

		// We execute astor for creating the model and run FL

		main.execute(cs.flat());

	}
}