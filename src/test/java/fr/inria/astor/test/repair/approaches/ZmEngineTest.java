package fr.inria.astor.test.repair.approaches;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import fr.inria.astor.approaches.zm.FileProgramVariant;
import fr.inria.astor.approaches.zm.ZmEngine;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.entities.VariantValidationResult;
import fr.inria.astor.core.validation.results.TestCasesProgramValidationResult;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ZmEngineTest {

	@Test
	public void testMath70FileZm() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		CommandSummary cs = new CommandSummary();
		cs.command.put("-dependencies", dep);
		cs.command.put("-location", new File("./examples/math_70").getAbsolutePath());
		cs.command.put("-flthreshold", "0.5");
		cs.command.put("-maxgen", "0");
		cs.command.put("-customengine", ZmEngine.class.getCanonicalName());
		cs.command.put("-parameters", "disablelog:false:logtestexecution:true");

		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		// Check it astor loads the mode correctly
		Assert.assertTrue(main1.getEngine() instanceof ZmEngine);
		ZmEngine zmengine = (ZmEngine) main1.getEngine();

		// Get the suspicious
		List<SuspiciousModificationPoint> susp = zmengine.getSuspicious();
		Assert.assertTrue(susp.size() > 0);

		// Case 1:try a file with the solution.

		File solutionFile = new File(
				"./examples/math_70_fixed/src/main/java/org/apache/commons/math/analysis/solvers/BisectionSolver.java");

		Assert.assertTrue(solutionFile.exists());

		// this id corresponds to the patch identifier
		int idVariant = 100;
		FileProgramVariant fvariant = new FileProgramVariant(idVariant,
				"org.apache.commons.math.analysis.solvers.BisectionSolver", solutionFile);

		VariantValidationResult resultValidation = zmengine.validateInstance(fvariant);
		// As the file has a solution, the validation must work
		Assert.assertTrue(resultValidation.isSuccessful());

		TestCasesProgramValidationResult resultOk = (TestCasesProgramValidationResult) resultValidation;
		Assert.assertTrue(resultOk.getPassingTestCases() > 0);
		Assert.assertEquals(0, resultOk.getFailureCount());

		// Case 2: try a file with a not solution

		File nosolutionFile = new File(
				"./examples/math_70/src/main/java/org/apache/commons/math/analysis/solvers/BisectionSolver.java");

		FileProgramVariant fvariantNoSolution = new FileProgramVariant(idVariant++,
				"org.apache.commons.math.analysis.solvers.BisectionSolver", nosolutionFile);

		Assert.assertTrue(nosolutionFile.exists());

		VariantValidationResult resultValidationNoSolution = zmengine.validateInstance(fvariantNoSolution);
		// The validation must fail (at least one failing test)
		Assert.assertFalse(resultValidationNoSolution.isSuccessful());

		TestCasesProgramValidationResult resultFailing = (TestCasesProgramValidationResult) resultValidationNoSolution;
		Assert.assertTrue(resultFailing.getPassingTestCases() > 0);
		Assert.assertTrue(resultFailing.getFailureCount() > 0);

	}

	@Test
	public void testMath70FileZmSuspicFile() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		CommandSummary cs = new CommandSummary();
		cs.command.put("-dependencies", dep);
		cs.command.put("-location", new File("./examples/math_70").getAbsolutePath());
		cs.command.put("-flthreshold", "0.5");
		cs.command.put("-maxgen", "0");
		cs.command.put("-customengine", ZmEngine.class.getCanonicalName());
		cs.command.put("-parameters", "disablelog:false:logtestexecution:true");

		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		// Check it astor loads the mode correctly
		Assert.assertTrue(main1.getEngine() instanceof ZmEngine);
		ZmEngine zmengine = (ZmEngine) main1.getEngine();

		// Get the suspicious
		List<SuspiciousModificationPoint> susp = zmengine.getSuspicious();
		Assert.assertTrue(susp.size() > 0);
		for (SuspiciousModificationPoint suspiciousModificationPoint : susp) {
			// GZoltar does not provide that info
			Assert.assertNull(suspiciousModificationPoint.getSuspicious().getFileName());
			Assert.assertNotNull(suspiciousModificationPoint.getCodeElement().getPosition().getFile());
			System.out.println(suspiciousModificationPoint.getCodeElement().getPosition().getFile());
		}
	}
}
