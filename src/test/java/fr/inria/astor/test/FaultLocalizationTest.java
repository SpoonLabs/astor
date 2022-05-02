package fr.inria.astor.test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.faultlocalization.FaultLocalizationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.FaultLocalizationMain;
import fr.inria.main.FaultLocalizationMain.FaultLocalization;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
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

		// 3 more tests
		assertEquals(2181, er.getExecutedTestCasesMethods().size());
	}

	@org.junit.Test
	public void testFLMath70_GZoltar_limited() throws Exception {
		FaultLocalizationMain main = new FaultLocalizationMain();
		CommandSummary cs = MathCommandsTests.getMath70Command();
		// by default, max generations is zero, that means, it does not evolve
		cs.command.put("-faultlocalization", FaultLocalization.GZOLTAR.name());
		cs.command.put("-flthreshold", "0.0001");

		String oneFailingTestClassToRun = "org.apache.commons.math.analysis.solvers.BisectionSolverTest";
		String anotherTestClassToRun = "org.apache.commons.math.estimation.LevenbergMarquardtEstimatorTest";
		cs.command.put("-regressiontestcases4fl",
				oneFailingTestClassToRun + File.pathSeparator + anotherTestClassToRun);

		// We execute astor for creating the model and run FL

		FaultLocalizationResult er = (FaultLocalizationResult) main.execute(cs.flat());
		System.out.println(er);
		assertFalse(er.getCandidates().isEmpty());
		assertFalse(er.getExecutedTestCasesMethods().isEmpty());
		assertFalse(er.getFailingTestCasesClasses().isEmpty());
		assertFalse(er.getFailingTestCasesMethods().isEmpty());

		assertEquals(27, er.getExecutedTestCasesMethods().size());

		// All test method must come from some of those two test clases
		for (String testMethod : er.getExecutedTestCasesMethods()) {

			assertTrue(testMethod.contains(oneFailingTestClassToRun) || testMethod.contains(anotherTestClassToRun));

		}

		assertTrue(er.getFailingTestCasesClasses().contains(oneFailingTestClassToRun));

		assertEquals(1, er.getFailingTestCasesClasses().size());
	}

	@org.junit.Test
	public void testFLMath70GZoltar_limitedMethod() throws Exception {

		FaultLocalization fl = FaultLocalization.GZOLTAR;

		helperLimitedNumberOfTestMethods(fl);

	}

	@org.junit.Test
	public void testFLMath70_limited() throws Exception {

		FaultLocalization flocalization = FaultLocalization.GZOLTAR;

		helperLimitedTestClasses(flocalization);

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

		// In order to check that we dont have duplicates
		assertEquals(er.getExecutedTestCasesMethods().stream().distinct().count(),
				er.getExecutedTestCasesMethods().size());

		assertEquals(2178, er.getExecutedTestCasesMethods().size());
	}

	@org.junit.Test
	public void testFLMath70Flacoco_limitedTest() throws Exception {
		FaultLocalization flocalization = FaultLocalization.FLACOCO;

		helperLimitedTestClasses(flocalization);

	}

	@org.junit.Test
	public void testFLMath70Flacoco_limitedMixed() throws Exception {

		FaultLocalization fl = FaultLocalization.FLACOCO;

		helperLimitedTestAndClasses(fl);

	}

	private void helperLimitedTestAndClasses(FaultLocalization fl) throws Exception {
		FaultLocalizationMain main = new FaultLocalizationMain();
		CommandSummary cs = MathCommandsTests.getMath70Command();
		// by default, max generations is zero, that means, it does not evolve

		cs.command.put("-faultlocalization", fl.name());
		cs.command.put("-flthreshold", "0.0000");
		cs.command.put("-parameters", "includeZeros:false:keepGZoltarFiles:false");

		String oneFailingTestClassToRun = "org.apache.commons.math.analysis.solvers.BisectionSolverTest#testMath369";
		String anotherTestClassToRun = "org.apache.commons.math.estimation.LevenbergMarquardtEstimatorTest";

		cs.command.put("-regressiontestcases4fl",
				oneFailingTestClassToRun + File.pathSeparator + anotherTestClassToRun);
		// We execute astor for creating the model and run FL

		FaultLocalizationResult er = (FaultLocalizationResult) main.execute(cs.flat());

		assertFalse(er.getCandidates().isEmpty());
		assertFalse(er.getExecutedTestCasesMethods().isEmpty());
		assertFalse(er.getFailingTestCasesClasses().isEmpty());
		assertFalse(er.getFailingTestCasesMethods().isEmpty());

		assertEquals(16, er.getExecutedTestCasesMethods().size());

		// All test method must come from some of those two test clases
		for (String testMethod : er.getExecutedTestCasesMethods()) {

			assertTrue(testMethod.contains(oneFailingTestClassToRun) || testMethod.contains(anotherTestClassToRun));

		}

		assertTrue(er.getFailingTestCasesClasses().contains(oneFailingTestClassToRun.split("#")[0]));
		assertFalse(er.getFailingTestCasesClasses().contains(anotherTestClassToRun.split("#")[0]));

		assertTrue(er.getFailingTestCasesMethods().contains(oneFailingTestClassToRun));
		assertFalse(er.getFailingTestCasesMethods().contains(anotherTestClassToRun));

		assertEquals(1, er.getFailingTestCasesClasses().size());
	}

	@org.junit.Test
	public void testFLMath70Flacoco_limitedMethodTest() throws Exception {

		FaultLocalization fl = FaultLocalization.FLACOCO;

		helperLimitedNumberOfTestMethods(fl);

	}

	@Test
	public void testMath70FixedFL() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-faultlocalization", "flacoco", "-scope", "package", "-seed", "10", "-maxgen",
				"10000", "-stopfirst", "true", "-maxtime", "10", "-population", "1" };
		System.out.println(Arrays.toString(args));
		CommandSummary command = new CommandSummary(args);

		command.command.put("-parameters", "fixedLocation:BisectionSolver-72");
		main1.execute(command.flat());

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() >= 1);

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

		FaultLocalizationResult er = (FaultLocalizationResult) main.execute(cs.flat());

		assertFalse(er.getCandidates().isEmpty());
		assertFalse(er.getExecutedTestCasesMethods().isEmpty());
		assertFalse(er.getFailingTestCasesClasses().isEmpty());
		assertFalse(er.getFailingTestCasesMethods().isEmpty());

		// Avoid the first line of the result file
		assertFalse(er.getExecutedTestCasesMethods().contains("name"));

		assertEquals(2178, er.getExecutedTestCasesMethods().size());

	}

	@org.junit.Test
	public void testFLMath70Ngz_limited() throws Exception {

		FaultLocalization flocalization = FaultLocalization.GZOLTAR1_7;

		helperLimitedTestClasses(flocalization);

	}

	private void helperLimitedTestClasses(FaultLocalization flocalization) throws Exception {
		FaultLocalizationMain main = new FaultLocalizationMain();
		CommandSummary cs = MathCommandsTests.getMath70Command();
		// by default, max generations is zero, that means, it does not evolve

		cs.command.put("-faultlocalization", flocalization.name());
		cs.command.put("-flthreshold", "0.0000");
		cs.command.put("-parameters", "includeZeros:false:keepGZoltarFiles:false");

		String oneFailingTestClassToRun = "org.apache.commons.math.analysis.solvers.BisectionSolverTest";
		String anotherTestClassToRun = "org.apache.commons.math.estimation.LevenbergMarquardtEstimatorTest";
		cs.command.put("-regressiontestcases4fl",
				oneFailingTestClassToRun + File.pathSeparator + anotherTestClassToRun);
		// We execute astor for creating the model and run FL

		FaultLocalizationResult er = (FaultLocalizationResult) main.execute(cs.flat());

		assertFalse(er.getCandidates().isEmpty());
		assertFalse(er.getExecutedTestCasesMethods().isEmpty());
		assertFalse(er.getFailingTestCasesClasses().isEmpty());
		assertFalse(er.getFailingTestCasesMethods().isEmpty());

		assertEquals(27, er.getExecutedTestCasesMethods().size());

		// All test method must come from some of those two test clases
		for (String testMethod : er.getExecutedTestCasesMethods()) {

			assertTrue(testMethod.contains(oneFailingTestClassToRun) || testMethod.contains(anotherTestClassToRun));

		}

		assertTrue(er.getFailingTestCasesClasses().contains(oneFailingTestClassToRun));
		assertTrue(er.getFailingTestCasesMethods()
				.contains("org.apache.commons.math.analysis.solvers.BisectionSolverTest#testMath369"));

		assertEquals(1, er.getFailingTestCasesClasses().size());
	}

	@org.junit.Test
	public void testFLMath70Ngz_limitedMethod() throws Exception {

		FaultLocalization fl = FaultLocalization.GZOLTAR1_7;

		helperLimitedNumberOfTestMethods(fl);

	}

	private void helperLimitedNumberOfTestMethods(FaultLocalization fl) throws Exception {
		FaultLocalizationMain main = new FaultLocalizationMain();
		CommandSummary cs = MathCommandsTests.getMath70Command();
		// by default, max generations is zero, that means, it does not evolve

		cs.command.put("-faultlocalization", fl.name());
		cs.command.put("-flthreshold", "0.0000");
		cs.command.put("-parameters", "includeZeros:false:keepGZoltarFiles:false");

		String oneFailingTestClassToRun = "org.apache.commons.math.analysis.solvers.BisectionSolverTest#testMath369";
		String anotherTestClassToRun = "org.apache.commons.math.estimation.LevenbergMarquardtEstimatorTest#testTrivial";

		cs.command.put("-regressiontestcases4fl",
				oneFailingTestClassToRun + File.pathSeparator + anotherTestClassToRun);
		// We execute astor for creating the model and run FL

		FaultLocalizationResult er = (FaultLocalizationResult) main.execute(cs.flat());

		assertFalse(er.getCandidates().isEmpty());
		assertFalse(er.getExecutedTestCasesMethods().isEmpty());
		assertFalse(er.getFailingTestCasesClasses().isEmpty());
		assertFalse(er.getFailingTestCasesMethods().isEmpty());

		assertEquals(2, er.getExecutedTestCasesMethods().size());

		// All test method must come from some of those two test clases
		for (String testMethod : er.getExecutedTestCasesMethods()) {

			assertTrue(testMethod.contains(oneFailingTestClassToRun) || testMethod.contains(anotherTestClassToRun));

		}

		assertTrue(er.getFailingTestCasesClasses().contains(oneFailingTestClassToRun.split("#")[0]));
		assertFalse(er.getFailingTestCasesClasses().contains(anotherTestClassToRun.split("#")[0]));

		assertTrue(er.getFailingTestCasesMethods().contains(oneFailingTestClassToRun));
		assertFalse(er.getFailingTestCasesMethods().contains(anotherTestClassToRun));

		assertEquals(1, er.getFailingTestCasesClasses().size());
	}

	@org.junit.Test
	public void testFLMath70Ngz_limitedMixed() throws Exception {
		FaultLocalization fl = FaultLocalization.GZOLTAR1_7;

		helperLimitedTestAndClasses(fl);

	}

}