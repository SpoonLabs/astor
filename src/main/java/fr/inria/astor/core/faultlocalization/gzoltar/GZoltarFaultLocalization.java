package fr.inria.astor.core.faultlocalization.gzoltar;

import java.io.File;
import java.net.URL;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.gzoltar.core.GZoltar;
import com.gzoltar.core.components.Statement;
import com.gzoltar.core.instr.testing.TestResult;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.faultlocalization.FaultLocalizationResult;
import fr.inria.astor.core.faultlocalization.FaultLocalizationStrategy;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;

/**
 * Facade of Fault Localization techniques like GZoltar or own implementations
 * (package {@link org.inria.sacha.faultlocalization}.).
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class GZoltarFaultLocalization implements FaultLocalizationStrategy {

	Logger logger = Logger.getLogger(GZoltarFaultLocalization.class.getName());

	public FaultLocalizationResult searchSuspicious(ProjectRepairFacade project) throws Exception {

		String regressionTC = ConfigurationProperties.getProperty("regressiontestcases4fl");
		List<String> regressionTestForFaultLocalization = null;
		if (regressionTC != null && !regressionTC.trim().isEmpty()) {
			regressionTestForFaultLocalization = Arrays.asList(regressionTC.split(File.pathSeparator));
		} else
			regressionTestForFaultLocalization = project.getProperties().getRegressionTestCases();

		return this.calculateSuspicious(
				ConfigurationProperties.getProperty("location") + File.separator
						+ ConfigurationProperties.getProperty("srcjavafolder"),
				project.getOutDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT),
				ConfigurationProperties.getProperty("packageToInstrument"), ProgramVariant.DEFAULT_ORIGINAL_VARIANT,
				project.getProperties().getFailingTestCases(), regressionTestForFaultLocalization,
				ConfigurationProperties.getPropertyBool("regressionforfaultlocalization"), project);

	}

	private FaultLocalizationResult calculateSuspicious(String locationSrc, String locationBytecode,
			String packageToInst, String mutatorIdentifier, List<String> failingTest, List<String> allTest,
			boolean mustRunAllTest, ProjectRepairFacade project) throws Exception {

		List<String> testcasesToExecute = null;

		if (mustRunAllTest) {
			testcasesToExecute = allTest;
		} else {
			testcasesToExecute = failingTest;
		}

		if (testcasesToExecute == null || testcasesToExecute.isEmpty()) {
			new IllegalArgumentException("Astor needs at least one test case for running");
		}

		logger.info("-Executing Gzoltar classpath: " + locationBytecode + " from " + +testcasesToExecute.size()
				+ " classes with test cases");

		List<String> listTOInst = new ArrayList<String>();
		listTOInst.add(packageToInst);

		Set<String> classPath = new HashSet<String>();
		classPath.add(locationBytecode);
		for (URL dep : project.getProperties().getDependencies()) {
			classPath.add(dep.getPath());
		}

		FaultLocalizationResult flResult = this.searchSuspicious(locationBytecode, testcasesToExecute, listTOInst,
				classPath, locationSrc);

		List<SuspiciousCode> suspiciousStatemens = flResult.getCandidates();

		if (suspiciousStatemens == null || suspiciousStatemens.isEmpty())
			throw new IllegalArgumentException("No suspicious gen for analyze");

		List<String> failingTestCases = flResult.getFailingTestCases();
		if (ConfigurationProperties.getPropertyBool("ignoreflakyinfl")) {
			addFlakyFailingTestToIgnoredList(failingTestCases, project);
		}

		if (project.getProperties().getFailingTestCases().isEmpty()) {
			logger.debug("Failing test cases was not pass as argument: we use failings from FL "
					+ flResult.getFailingTestCases());
			project.getProperties().setFailingTestCases(failingTestCases);
		}

		if (ConfigurationProperties.getPropertyBool("filterfaultlocalization")) {
			List<SuspiciousCode> filtercandidates = new ArrayList<SuspiciousCode>();

			for (SuspiciousCode suspiciousCode : suspiciousStatemens) {
				if (!suspiciousCode.getClassName().endsWith("Exception")) {
					filtercandidates.add(suspiciousCode);
				}
			}
			flResult.setCandidates(filtercandidates);

		}
		return flResult;

	}

	/**
	 * It adds to the ignore list all failing TC that were not passed as
	 * argument. \ They are probably flaky test.
	 * 
	 * @param failingTestCases
	 */
	private void addFlakyFailingTestToIgnoredList(List<String> failingTestCases, ProjectRepairFacade project) {
		//
		if (project.getProperties().getFailingTestCases() == null)
			return;
		List<String> originalFailing = project.getProperties().getFailingTestCases();
		List<String> onlyFailingInFL = new ArrayList<>(failingTestCases);
		// we remove those that we already know that fail
		onlyFailingInFL.removeAll(originalFailing);
		logger.debug("failing before " + onlyFailingInFL + ", added to the ignored list");
		String ignoredTestCases = ConfigurationProperties.getProperty("ignoredTestCases");
		for (String failingFL : onlyFailingInFL) {
			ignoredTestCases += File.pathSeparator + failingFL;
		}
		ConfigurationProperties.properties.setProperty("ignoredTestCases", ignoredTestCases);
	}

	protected FaultLocalizationResult searchSuspicious(String locationBytecode, List<String> testsToExecute,
			List<String> toInstrument, Set<String> cp, String srcFolder) throws Exception {

		List<String> failingTestCases = new ArrayList<String>();

		Double thr = ConfigurationProperties.getPropertyDouble("flthreshold");
		logger.info("Gzoltar fault localization: min susp value parameter: " + thr);
		// 1. Instantiate GZoltar
		// here you need to specify the working directory where the tests will
		// be run. Can be the full or relative path.
		// Example: GZoltar gz = new
		// GZoltar("C:\\Personal\\develop\\workspaceEvolution\\testProject\\target\\classes");

		File projLocationFile = new File(locationBytecode + File.separator);
		String projLocationPath = projLocationFile.getAbsolutePath();
		logger.debug("Gzoltar run over: " + projLocationPath + " , does it exist? " + projLocationFile.exists());

		GZoltar gz = new GZoltar(projLocationPath + File.separator);

		// 2. Add Package/Class names to instrument
		// 3. Add Package/Test Case/Test Suite names to execute
		// Example: gz.addPackageToInstrument("org.test1.Person");
		for (String to : toInstrument) {
			gz.addPackageToInstrument(to);
		}
		if (cp != null || !cp.isEmpty()) {
			logger.info("-gz-Adding classpath: " + cp);
			gz.getClasspaths().addAll(cp);
		}
		for (String test : testsToExecute) {
			gz.addTestToExecute(test);
			gz.addClassNotToInstrument(test);
		}

		String testToAvoid = ConfigurationProperties.getProperty("gzoltartestpackagetoexclude");
		if (testToAvoid != null) {
			String[] testtoavoidarray = testToAvoid.split("_");
			for (String test : testtoavoidarray) {
				gz.addTestPackageNotToExecute(test);
			}
		}

		String packagetonotinstrument = ConfigurationProperties.getProperty("gzoltarpackagetonotinstrument");

		if (packagetonotinstrument != null) {
			String[] packages = packagetonotinstrument.split("_");
			for (String p : packages) {
				gz.addPackageNotToInstrument(p);

			}

		}

		gz.run();
		int[] sum = new int[2];
		for (TestResult tr : gz.getTestResults()) {
			String testName = tr.getName().split("#")[0];
			if (testName.startsWith("junit")) {
				continue;
			}
			sum[0]++;
			sum[1] += tr.wasSuccessful() ? 0 : 1;
			if (!tr.wasSuccessful()) {
				logger.info("Test failt: " + tr.getName());

				String testCaseName = testName.split("\\#")[0];
				if (!failingTestCases.contains(testCaseName)) {
					failingTestCases.add(testCaseName);
				}
			}
		}

		int gzPositives = gz.getSuspiciousStatements().stream().filter(x -> x.getSuspiciousness() > 0)
				.collect(Collectors.toList()).size();

		logger.info("Gzoltar Test Result Total:" + sum[0] + ", fails: " + sum[1] + ", GZoltar suspicious "
				+ gz.getSuspiciousStatements().size() + ", with positive susp " + gzPositives);

		DecimalFormat df = new DecimalFormat("#.###");
		int maxSuspCandidates = ConfigurationProperties.getPropertyInt("maxsuspcandidates");

		List<Statement> gzCandidates = new ArrayList();
		List<TestResult> testResults = gz.getTestResults();
		logger.info("nr test results " + testResults.size());
		for (Statement gzoltarStatement : gz.getSuspiciousStatements()) {
			String compName = gzoltarStatement.getMethod().getParent().getLabel();
			if (isSource(compName, srcFolder) && (!ConfigurationProperties.getPropertyBool("limitbysuspicious")
					|| (gzoltarStatement.getSuspiciousness() >= thr))) {
				gzCandidates.add(gzoltarStatement);

			}

		}
		// If we do not have candidate due the threshold is to high, we add all
		// as suspicious
		if (gzCandidates.isEmpty()) {
			gzCandidates.addAll(gz.getSuspiciousStatements());

		}

		if (!ConfigurationProperties.getPropertyBool("considerzerovaluesusp")) {
			gzCandidates.removeIf(susp -> (susp.getSuspiciousness() == 0));
		}

		// we order the suspicious DESC
		Collections.sort(gzCandidates, (o1, o2) -> Double.compare(o2.getSuspiciousness(), o1.getSuspiciousness()));

		// We select the best X candidates.
		int max = (gzCandidates.size() < maxSuspCandidates) ? gzCandidates.size() : maxSuspCandidates;

		List<SuspiciousCode> candidates = new ArrayList<SuspiciousCode>();

		for (int i = 0; i < max; i++) {
			Statement gzoltarStatement = gzCandidates.get(i);
			String compName = gzoltarStatement.getMethod().getParent().getLabel();

			logger.debug("Suspicious: line " + compName + " l: " + gzoltarStatement.getLineNumber() + ", susp "
					+ df.format(gzoltarStatement.getSuspiciousness()));
			SuspiciousCode suspcode = new SuspiciousCode(compName, gzoltarStatement.getMethod().toString(),
					gzoltarStatement.getLineNumber(), gzoltarStatement.getSuspiciousness(),
					gzoltarStatement.getCountMap());
			candidates.add(suspcode);

			List<TestCaseResult> test = getTestCaseResults(testResults, gzoltarStatement);
			suspcode.setCoveredByTests(test);

		}

		logger.info("Gzoltar found: " + candidates.size() + " with susp > " + thr + ", we consider: " + max);

		return new FaultLocalizationResult(candidates, failingTestCases);
	}

	private List<TestCaseResult> getTestCaseResults(List<TestResult> testResults, Statement gzoltarStatement) {
		List<TestCaseResult> tcresults = new ArrayList<>();

		BitSet coverage = gzoltarStatement.getCoverage();
		int nextTest = coverage.nextSetBit(0);
		while (nextTest != -1) {
			TestResult testResult = testResults.get(nextTest);
			if (!ConfigurationProperties.getPropertyBool("savecoverbyfailtests") || !testResult.wasSuccessful()) {

				TestCaseResult tcri = new TestCaseResult(testResult.getName(), testResult.wasSuccessful());
				tcresults.add(tcri);
			}
			nextTest = coverage.nextSetBit(nextTest + 1);
		}
		return tcresults;
	}

	private boolean isSource(String compName, String srcFolder) {
		String clRoot = compName.split("\\$")[0];
		String[] segmentationName = clRoot.split("\\.");
		String simpleClassName = segmentationName[segmentationName.length - 1];

		// File root = new File(srcFolder+"/"+clRoot.replace(".", "/")+".java");

		return // root.exists()
				// &&
		!compName.toLowerCase().endsWith("test") && !compName.toLowerCase().endsWith("tests")
				&& !simpleClassName.toLowerCase().startsWith("test")
				&& !simpleClassName.toLowerCase().startsWith("validate");

	}

	public class ComparatorCandidates implements Comparator<SuspiciousCode> {

		@Override
		public int compare(SuspiciousCode o1, SuspiciousCode o2) {
			if (o1 == null || o2 == null) {
				return 0;
			}
			return Double.compare(o2.getSuspiciousValue(), o1.getSuspiciousValue());
		}

	}

}
