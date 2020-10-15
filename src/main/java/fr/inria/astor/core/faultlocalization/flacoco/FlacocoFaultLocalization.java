package fr.inria.astor.core.faultlocalization.flacoco;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import eu.stamp_project.testrunner.runner.coverage.JUnit4JacocoRunner;
import eu.stamp_project.testrunner.runner.coverage.JUnit5JacocoRunner;
import eu.stamp_project.testrunner.runner.coverage.JacocoRunner;
import eu.stamp_project.testrunner.test_framework.TestFramework;
import eu.stamp_project.testrunner.test_framework.TestFrameworkSupport;
import eu.stamp_project.testrunner.test_framework.implementations.AssertJTestFramework;
import eu.stamp_project.testrunner.test_framework.implementations.GoogleTruthTestFramework;
import eu.stamp_project.testrunner.test_framework.implementations.junit.JUnit3Support;
import eu.stamp_project.testrunner.test_framework.implementations.junit.JUnit4Support;
import eu.stamp_project.testrunner.test_framework.implementations.junit.JUnit5Support;
import fr.inria.astor.core.faultlocalization.FaultLocalizationResult;
import fr.inria.astor.core.faultlocalization.FaultLocalizationStrategy;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.faultlocalization.gzoltar.TestCaseResult;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.FinderTestCases;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.spoonlabs.flacoco.core.CoverageRunner;
import fr.spoonlabs.flacoco.core.SuspiciousComputation;
import fr.spoonlabs.flacoco.core.TestDetector;
import fr.spoonlabs.flacoco.core.TestInformation;
import fr.spoonlabs.flacoco.entities.MatrixCoverage;
import fr.spoonlabs.flacoco.formulas.OchiaiFormula;
import spoon.reflect.declaration.CtMethod;

/**
 * 
 * @author Matias Martinez
 *
 */
public class FlacocoFaultLocalization implements FaultLocalizationStrategy {

	Logger logger = Logger.getLogger(FlacocoFaultLocalization.class.getName());

	private TestDetector testDetector = new TestDetector();

	@Override
	public FaultLocalizationResult searchSuspicious(ProjectRepairFacade projectToRepair, List<String> testToRun)
			throws Exception {

		// We first compute the covered lines

		SuspiciousComputation flcalc = new SuspiciousComputation();

		CoverageRunner detector = new CoverageRunner();

		String dependencies = projectToRepair.getProperties().getDependenciesString();

		// Path of classes under test
		String pathToClasses = projectToRepair.getProperties().getOriginalAppBinDir().stream()
				.collect(Collectors.joining(File.pathSeparator));
		// Path of test under test
		String pathToTestClasses = projectToRepair.getProperties().getOriginalTestBinDir().stream()
				.collect(Collectors.joining(File.pathSeparator));

		// The input format of Flacoco is different from Astor: flacoco executes
		// methods (tests), Astor considered classes with tests.
		// TODO: we could replace List<String> per List<TestInformation> everywhere.
		List<TestInformation> tests = testDetector.findTest(MutationSupporter.getFactory());

		if (tests == null || tests.isEmpty()) {
			throw new IllegalStateException("No test found");
		}

		// Obtain the runner based on the test cases we ve just collected
		JacocoRunner runner = determineTestRunner(pathToClasses, pathToTestClasses, tests);

		if (runner == null) {
			throw new IllegalStateException("Test Runner cannot be determined");
		}

		logger.debug("Tests detected " + tests.size());

		MatrixCoverage matrix = detector.getCoverageMatrix(runner, dependencies, pathToClasses, pathToTestClasses,
				tests);

		// We then compute the suspicious value
		Map<String, Double> susp = flcalc.calculateSuspicious(matrix, new OchiaiFormula(), false);

		logger.info("Flacoco Test Result Total:" + matrix.getTestResult().keySet().size() + ", fails: "
				+ matrix.getFailingTestCases().size());

		List<SuspiciousCode> candidates = new ArrayList<>();

		int i = 0;
		for (String line : susp.keySet()) {
			double suspvalue = susp.get(line);

			String[] sp = line.split(MatrixCoverage.JOIN);
			String className = sp[0].replace(File.separator, ".");
			Integer lineNumber = new Integer(sp[1]);

			logger.info("Suspicious: " + ++i + " line " + className + " l: " + lineNumber + ", susp " + suspvalue);

			SuspiciousCode sc = new SuspiciousCode(className, null, lineNumber, suspvalue, null);
			candidates.add(sc);

			Set<Integer> testExecuted = matrix.getResultExecution().get(line);

			List<TestCaseResult> testsNames = new ArrayList();
			for (Integer itest : testExecuted) {
				String testName = matrix.getTests().get(itest);
				String[] ts = testName.split(MatrixCoverage.JOIN);
				boolean testResult = matrix.getTestResult().get(itest);
				String testNameSingle = ts[1];
				String testClass = ts[0];
				TestCaseResult trc = new TestCaseResult(testName, testNameSingle, testClass, testResult);

				testsNames.add(trc);
			}
			sc.setCoveredByTests(testsNames);

		}

		List<String> failingTestCases = matrix.getFailingTestCases();
		return new FaultLocalizationResult(candidates, failingTestCases);
	}

	/**
	 * This method returns the runner that should be used for the test
	 * 
	 * @param pathToClasses
	 * @param pathToTestClasses
	 * @param tests             test used for determining the runner
	 * @return
	 */
	private JacocoRunner determineTestRunner(String pathToClasses, String pathToTestClasses,
			List<TestInformation> tests) {

		// TODO: not the best strategy, the list from the test runner is private...
		List<TestFrameworkSupport> testFrameworkSupportList = new ArrayList();
		testFrameworkSupportList.add(new JUnit3Support());
		testFrameworkSupportList.add(new JUnit4Support());
		testFrameworkSupportList.add(new JUnit5Support());
		testFrameworkSupportList.add(new GoogleTruthTestFramework());
		testFrameworkSupportList.add(new AssertJTestFramework());

		// Compute the frequency of each test framework (as one app can use Junit 4 and
		// jUnit 5....)
		Map<String, Integer> freqOfTestFramework = computeFrameworksUsed(tests, testFrameworkSupportList);

		logger.info("Frameworks used: " + freqOfTestFramework);

		// Sort framework used according to the tests associated to each
		List<String> mostUsedFrameworks = freqOfTestFramework.keySet().stream()
				.sorted((o1, o2) -> freqOfTestFramework.get(o2).compareTo(freqOfTestFramework.get(o1)))
				.collect(Collectors.toList());

		if (mostUsedFrameworks.indexOf(JUnit4Support.class.getSimpleName()) == 0
				|| mostUsedFrameworks.indexOf(JUnit3Support.class.getSimpleName()) == 0) {
			logger.info("Running test on Junit 3.x/4.x: " + (mostUsedFrameworks.get(0)));
			return new JUnit4JacocoRunner(pathToClasses, pathToTestClasses);
		}
		if (mostUsedFrameworks.indexOf(JUnit5Support.class.getSimpleName()) == 0) {
			logger.info("Running test on Junit 5");
			return new JUnit5JacocoRunner(pathToClasses, pathToTestClasses);
		} else {

			logger.error("Not runner recognized: " + mostUsedFrameworks);
			return null;
		}
	}

	/**
	 * Computes the frequency of the test frameworks
	 * 
	 * @param tests
	 * @param testFrameworkSupportList
	 * @return
	 */
	public Map<String, Integer> computeFrameworksUsed(List<TestInformation> tests,
			List<TestFrameworkSupport> testFrameworkSupportList) {
		Map<String, Integer> freq = new HashMap<String, Integer>();

		// let's inspect every test class
		for (TestInformation testInformation : tests) {
			// every method
			for (CtMethod<?> method : testInformation.getTestMethods()) {

				// let's count how many frameworks accept them.
				for (TestFrameworkSupport frm : testFrameworkSupportList) {

					// todo: wait the PR accepted
					String key = TestFramework.get().getKeyOfTestFramework(frm);

					// get the metadata from the element
					Object metadata = method.getMetadata(key);

					boolean hasDetected = method.getMetadata(key) != null && Boolean.valueOf(metadata.toString());

					if (hasDetected) {

						int count = freq.containsKey(key) ? freq.get(key) : 0;
						freq.put(key, count + 1);
					}
				}
			}

		}

		return freq;
	}

	@Override
	public List<String> findTestCasesToExecute(ProjectRepairFacade projectFacade) {
		List<String> testCasesToRun = FinderTestCases.findJUnit4XTestCasesForRegression(projectFacade);
		return testCasesToRun;

	}
}
