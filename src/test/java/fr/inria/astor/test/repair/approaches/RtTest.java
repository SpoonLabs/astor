package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.inria.astor.approaches.extensions.rt.RtEngine;
import fr.inria.astor.approaches.extensions.rt.RtEngine.TestClassificationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class RtTest {

	public static Logger log = Logger.getLogger(Thread.currentThread().getName());

	@Test
	public void testRTMath70d() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "INFO");
		cs.command.put("-location", new File("./examples/math_70_rt").getAbsolutePath());
		cs.command.put("-mode", "custom");
		cs.command.put("-customengine", RtEngine.class.getCanonicalName());

		main1.execute(cs.flat());
		RtEngine etEn = (RtEngine) main1.getEngine();

		List<TestClassificationResult> resultByTest = etEn.getResultByTest();

		int sumAssertionNotExec = resultByTest.stream()
				.map(e -> e.getClassificationAssert().getResultNotExecuted().size())
				.collect(Collectors.summingInt(i -> i));

		assertEquals(7, sumAssertionNotExec);

		int sumHelperNotExec = resultByTest.stream().map(e -> e.getClassificationHelperAssertion().getResultNotExecuted().size())
				.collect(Collectors.summingInt(i -> i));

		assertEquals(1, sumHelperNotExec);

		int sumReturnNotExec = resultByTest.stream().map(e -> e.getAllSkipFromTest().size())
				.collect(Collectors.summingInt(i -> i));

		assertEquals(1, sumReturnNotExec);

		TestClassificationResult testwithskip = resultByTest.stream().filter(e -> !e.getAllSkipFromTest().isEmpty())
				.findFirst().get();
		assertEquals("testMMRt_return_truePositive", testwithskip.getTestMethodFromClass());
	}

	@Test
	public void testRow01() throws Exception {
		RtEngine etEn = detectRt();

		List<TestClassificationResult> resultByTest = etEn.getResultByTest();

		assertNotNull(resultByTest);
		List<TestClassificationResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass()
						.equals("RTFRow01HelperExecutedAssertionExecutedContainsHelperContainsAssertion"))
				.collect(Collectors.toList());

		assertTrue(tc.isEmpty());
		// assert: rottenTestsFound rottenTests isEmpty
		// assertFalse(tc.isRotten());
		// assertFalse(resultByTest.stream().filter(e ->
		// e.isRotten()).findFirst().isPresent());
	}

	@Test
	public void testRow02() throws Exception {
		RtEngine etEn = detectRt();

		List<TestClassificationResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestClassificationResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass()
						.contains("RTFRow02HelperNotExecutedAssertionExecutedContainsHelperContainsAssertion"))
				.collect(Collectors.toList());

		assertFalse(tc.isEmpty());
		// self
		// assert: rottenTestsFound rottenTests size equals: 1;
		assertEquals(1, tc.size());

		List<TestClassificationResult> rottens = tc.stream()
				.filter(e -> e.getTestMethodFromClass().equals("test0") && e.isRotten()).collect(Collectors.toList());

		assertFalse(rottens.isEmpty());

	}
//	{ #category : #tests }
//	RottenTestsFinderTestForPaper >> testRow03 [
//		| rottenTestsFound |
//		rottenTestsFound := RottenTestsFinder analyze: RTFRow03HelperExecutedAssertionNotExecutedContainsHelperContainsAssertion suite.
//		
//		self
//			assert: rottenTestsFound rottenTests size equals: 2;
//			assert: (rottenTestsFound rottenCompiledMethods anySatisfy: [ :m | 
//				m methodClass = RTFRow03HelperExecutedAssertionNotExecutedContainsHelperContainsAssertion
//					and: [ m selector = #test ] ]);
//			assert: (rottenTestsFound rottenCompiledMethods anySatisfy: [ :m | 
//				m methodClass = RTFAbstractTestCaseForPaper
//					and: [ m selector = #rottenHelper ] ])
//	]

	@Test
	public void testRow03() throws Exception {
		RtEngine etEn = detectRt();

		List<TestClassificationResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestClassificationResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass()
						.contains("RTFRow03HelperExecutedAssertionNotExecutedContainsHelperContainsAssertion"))
				.collect(Collectors.toList());

		assertFalse(tc.isEmpty());
		// self
		// assert: rottenTestsFound rottenTests size equals: 1;
		assertEquals(1, tc.size());

		List<TestClassificationResult> rottens = tc.stream()
				.filter(e -> e.getTestMethodFromClass().equals("test0") && e.isRotten()).collect(Collectors.toList());

		assertFalse(rottens.isEmpty());

		TestClassificationResult rottenTest0 = rottens.get(0);

		assertEquals(1, rottenTest0.getClassificationAssert().getResultNotExecuted().size());
		assertEquals(0, rottenTest0.getClassificationHelperAssertion().getResultNotExecuted().size());
		assertEquals(0, rottenTest0.getClassificationHelperAssertion().getResultExecuted().size());

	}

	private RtEngine detectRt() throws Exception {
		AstorMain main1 = new AstorMain();

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		int generations = 500;

		String[] args = new String[] { "-dependencies", dep, "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "10", "-maxgen", Integer.toString(generations),
				"-stopfirst", "true", "-maxtime", "100",

		};
		CommandSummary cs = new CommandSummary(args);
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "INFO");
		cs.command.put("-location", new File("./examples/rt-project").getAbsolutePath());
		cs.command.put("-mode", "custom");
		cs.command.put("-customengine", RtEngine.class.getCanonicalName());
		cs.command.put("-parameters", "canhavezerosusp:true");

		main1.execute(cs.flat());
		RtEngine etEn = (RtEngine) main1.getEngine();
		return etEn;
	}
}
