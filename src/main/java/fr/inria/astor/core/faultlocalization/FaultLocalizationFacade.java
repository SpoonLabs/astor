package fr.inria.astor.core.faultlocalization;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;

/**
 * 
 * @author Matias Martinez
 *
 */
public class FaultLocalizationFacade {

	Logger logger = Logger.getLogger(FaultLocalizationFacade.class.getName());

	public List<SuspiciousCode> calculateSuspicious(FaultLocalizationStrategy faultLocalization,
			ProjectRepairFacade project) throws Exception {

		String regressionTC = ConfigurationProperties.getProperty("regressiontestcases4fl");
		List<String> regressionTestForFaultLocalization = null;
		if (regressionTC != null && !regressionTC.trim().isEmpty()) {
			regressionTestForFaultLocalization = Arrays.asList(regressionTC.split(File.pathSeparator));
		} else
			regressionTestForFaultLocalization = project.getProperties().getRegressionTestCases();

		List<SuspiciousCode> candidates = this.calculateSuspicious(faultLocalization,
				ConfigurationProperties.getProperty("location") + File.separator
						+ ConfigurationProperties.getProperty("srcjavafolder"),
				project.getOutDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT),
				ConfigurationProperties.getProperty("packageToInstrument"), ProgramVariant.DEFAULT_ORIGINAL_VARIANT,
				project.getProperties().getFailingTestCases(), regressionTestForFaultLocalization,
				ConfigurationProperties.getPropertyBool("regressionforfaultlocalization"), project);
		return candidates;
	}

	// TODO:remove project facade
	private List<SuspiciousCode> calculateSuspicious(FaultLocalizationStrategy faultLocalization, String locationSrc,
			String locationBytecode, String packageToInst, String mutatorIdentifier, List<String> failingTest,
			List<String> allTest, boolean mustRunAllTest, ProjectRepairFacade project) throws Exception {

		if (faultLocalization == null)
			throw new IllegalArgumentException("Fault localization is null");

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

		FaultLocalizationResult flResult = faultLocalization.searchSuspicious(locationBytecode, testcasesToExecute,
				listTOInst, classPath, locationSrc);

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
			return filtercandidates;
		} else
			return suspiciousStatemens;

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

}
