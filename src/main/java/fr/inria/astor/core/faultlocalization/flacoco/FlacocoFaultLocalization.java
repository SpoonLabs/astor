package fr.inria.astor.core.faultlocalization.flacoco;

import static fr.inria.astor.core.faultlocalization.FaultLocalizationUtils.addFlakyFailingTestToIgnoredList;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import fr.inria.astor.core.faultlocalization.FaultLocalizationResult;
import fr.inria.astor.core.faultlocalization.FaultLocalizationStrategy;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.spoonlabs.flacoco.api.Flacoco;
import fr.spoonlabs.flacoco.api.result.FlacocoResult;
import fr.spoonlabs.flacoco.api.result.Location;
import fr.spoonlabs.flacoco.api.result.Suspiciousness;
import fr.spoonlabs.flacoco.core.config.FlacocoConfig;
import fr.spoonlabs.flacoco.core.coverage.framework.JUnit4Strategy;
import fr.spoonlabs.flacoco.core.coverage.framework.JUnit5Strategy;
import fr.spoonlabs.flacoco.core.test.TestContext;
import fr.spoonlabs.flacoco.core.test.TestDetector;
import fr.spoonlabs.flacoco.core.test.method.TestMethod;

/**
 * 
 * @author Matias Martinez
 *
 */
public class FlacocoFaultLocalization implements FaultLocalizationStrategy {

	Logger logger = Logger.getLogger(FlacocoFaultLocalization.class);

	List<TestContext> testContexts = new ArrayList<>();

	@Override
	public FaultLocalizationResult searchSuspicious(ProjectRepairFacade projectToRepair, List<String> testToRun)
			throws Exception {

		FlacocoConfig config = setupFlacocoConfig(projectToRepair);

		// We force the detection of test cases
		if (this.testContexts == null || testContexts.isEmpty()) {

			this.testContexts = new TestDetector(config).getTests();
		}
		// We put the test cases names in the flacoco configuration
		setupTestCasesToExecute(config, this.testContexts, testToRun);

		Flacoco flacoco = new Flacoco(config);

		FlacocoResult flacocoResult = flacoco.run();

		List<SuspiciousCode> candidates = new ArrayList<>();

		int i = 0;
		for (Map.Entry<Location, Suspiciousness> entry : flacocoResult.getDefaultSuspiciousnessMap().entrySet()) {
			double suspvalue = entry.getValue().getScore();
			String className = entry.getKey().getClassName();
			Integer lineNumber = entry.getKey().getLineNumber();

			logger.info("Suspicious: " + ++i + " line " + className + " l: " + lineNumber + ", susp " + suspvalue);

			SuspiciousCode sc = new SuspiciousCode(className, null, lineNumber, suspvalue, null);
			candidates.add(sc);
		}

		int maxSuspCandidates = ConfigurationProperties.getPropertyInt("maxsuspcandidates");
		candidates = candidates.subList(0, Math.min(maxSuspCandidates, candidates.size()));

		FaultLocalizationResult result = new FaultLocalizationResult(candidates, flacocoResult.getFailingTests()
				.stream().map(TestMethod::getFullyQualifiedClassName).distinct().collect(Collectors.toList()));

		result.setExecutedTestCasesMethods(flacocoResult.getExecutedTests().stream()
				.map(TestMethod::getFullyQualifiedMethodName).distinct().collect(Collectors.toList()));

		result.setFailingTestCasesMethods(flacocoResult.getFailingTests().stream()
				.map(TestMethod::getFullyQualifiedMethodName).distinct().collect(Collectors.toList()));

		if (ConfigurationProperties.getPropertyBool("ignoreflakyinfl")) {
			addFlakyFailingTestToIgnoredList(result.getFailingTestCasesClasses(), projectToRepair);
		}

		if (projectToRepair.getProperties().getFailingTestCases().isEmpty()) {
			logger.debug("Failing test cases was not passed as argument: we use the results from running them"
					+ result.getFailingTestCasesClasses());
			projectToRepair.getProperties().setFailingTestCases(result.getFailingTestCasesClasses());
		}

		// FIXME?: This does nothing, but it is in GZoltar like this.
		if (ConfigurationProperties.getPropertyBool("filterfaultlocalization")) {
			List<SuspiciousCode> filtercandidates = new ArrayList<>();

			for (SuspiciousCode suspiciousCode : result.getCandidates()) {
				filtercandidates.add(suspiciousCode);
				logger.info("Suspicious:  line " + suspiciousCode.getClassName() + " l: "
						+ suspiciousCode.getLineNumber() + ", susp " + suspiciousCode.getSuspiciousValue());
			}

			result.setCandidates(filtercandidates);
		}

		return result;
	}

	@Override
	public List<String> findTestCasesToExecute(ProjectRepairFacade projectFacade) {
		FlacocoConfig config = setupFlacocoConfig(projectFacade);
		this.testContexts = new TestDetector(config).getTests();
		return this.testContexts.stream().flatMap(x -> x.getTestMethods().stream())
				.map(TestMethod::getFullyQualifiedClassName).distinct().collect(Collectors.toList());
	}

	private FlacocoConfig setupFlacocoConfig(ProjectRepairFacade projectFacade) {
		FlacocoConfig config = new FlacocoConfig();

		config.setThreshold(ConfigurationProperties.getPropertyDouble("flthreshold"));

		// Handle project location configuration

		Integer timeOut = 0;

		if (ConfigurationProperties.getProperty("maxtime") != null)
			timeOut = new Integer(ConfigurationProperties.getProperty("maxtime"));

		if (timeOut == 0) {
			timeOut = 10;
		}

		config.setTestRunnerTimeoutInMs(timeOut * 60000);

		if (ConfigurationProperties.properties.containsKey("maxmemory")) {
			config.setTestRunnerJVMArgs(ConfigurationProperties.properties.get("maxmemory").toString());

		} else {
			config.setTestRunnerJVMArgs(null);

		}

		config.setProjectPath(projectFacade.getProperties().getOriginalProjectRootDir());
		config.setClasspath(projectFacade.getProperties().getDependenciesString());
		config.setComplianceLevel(ConfigurationProperties.getPropertyInt("javacompliancelevel"));
		config.setSrcJavaDir(projectFacade.getProperties().getOriginalDirSrc());
		config.setSrcTestDir(projectFacade.getProperties().getTestDirSrc());
		if (projectFacade.getProperties().getOriginalAppBinDir() != null)
			config.setBinJavaDir(projectFacade.getProperties().getOriginalAppBinDir());
		if (projectFacade.getProperties().getOriginalTestBinDir() != null)
			config.setBinTestDir(projectFacade.getProperties().getOriginalTestBinDir());

		// Handle manually set includes/excludes
		if (ConfigurationProperties.getProperty("packageToInstrument") != null
				&& !ConfigurationProperties.getProperty("packageToInstrument").isEmpty())

		{
			String option = ConfigurationProperties.getProperty("packageToInstrument");
			if (!option.endsWith(".*")) {
				option += ".*";
			}
			config.setJacocoIncludes(Collections.singleton(option));
		}
		config.setJacocoExcludes(Collections.singleton("java.*"));
		// Handle test configuration
		config.setjUnit4Tests(new HashSet<>());
		config.setjUnit5Tests(new HashSet<>());

		if (ConfigurationProperties.getProperty("ignoredTestCases") != null
				&& !ConfigurationProperties.getProperty("ignoredTestCases").isEmpty()) {
			config.setIgnoredTests(
					Arrays.stream(ConfigurationProperties.getProperty("ignoredTestCases").split(File.pathSeparator))
							.collect(Collectors.toSet()));
		}

		return config;
	}

	private void setupTestCasesToExecute(FlacocoConfig config, List<TestContext> testContexts, List<String> testToRun) {

		boolean isMethodName = (!testToRun.isEmpty() && testToRun.get(0).split("#").length >= 2);

		for (TestContext testContext : testContexts) {
			if (testContext.getTestFrameworkStrategy() instanceof JUnit4Strategy) {

				config.setjUnit4Tests(testContext.getTestMethods().stream()
						.filter(e -> testToRun.isEmpty() || testToRun.contains(
								isMethodName ? e.getFullyQualifiedMethodName() : e.getFullyQualifiedClassName()))
						.map(TestMethod::getFullyQualifiedMethodName).collect(Collectors.toSet()));
			} else if (testContext.getTestFrameworkStrategy() instanceof JUnit5Strategy) {
				config.setjUnit5Tests(testContext.getTestMethods().stream()
						.filter(e -> testToRun.isEmpty() || testToRun.contains(
								isMethodName ? e.getFullyQualifiedMethodName() : e.getFullyQualifiedClassName()))
						.map(TestMethod::getFullyQualifiedMethodName).collect(Collectors.toSet()));
			}
		}

	}
}