package fr.inria.astor.core.faultlocalization;

import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.gzoltar.core.GZoltar;
import com.gzoltar.core.components.Statement;
import com.gzoltar.core.instr.testing.TestResult;

import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.setup.ConfigurationProperties;

/**
 * Facade of Fault Localization techniques like GZoltar or own implementations
 * (package {@link org.inria.sacha.faultlocalization}.).
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class GZoltarFaultLocalization implements IFaultLocalization {

	Logger logger = Logger.getLogger(GZoltarFaultLocalization.class.getName());

	public FaultLocalizationResult searchSuspicious(String location, List<String> testsToExecute,
			List<String> toInstrument, Set<String> cp, String srcFolder) throws Exception {

		List<String> failingTestCases = new ArrayList<String>();

		Double thr = ConfigurationProperties.getPropertyDouble("flthreshold");
		logger.info("Gzoltar fault localization: min susp value parameter: " + thr);
		// 1. Instantiate GZoltar
		// here you need to specify the working directory where the tests will
		// be run. Can be the full or relative path.
		// Example: GZoltar gz = new
		// GZoltar("C:\\Personal\\develop\\workspaceEvolution\\testProject\\target\\classes");

		File projLocationFile = new File(ConfigurationProperties.getProperty("location"));
		String projLocation = projLocationFile.getAbsolutePath();
		logger.debug("Gzoltar run over: " + projLocation + " , does it exist? " + projLocationFile.exists());

		GZoltar gz = new GZoltar(projLocation);

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
		gz.addTestPackageNotToExecute("junit.framework");
		gz.addPackageNotToInstrument("junit.framework");
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
				failingTestCases.add(testName.split("\\#")[0]);
			}
		}

		logger.info("Gzoltar Test Result Total:" + sum[0] + ", fails: " + sum[1] + ", GZoltar suspicious "
				+ gz.getSuspiciousStatements().size());

		DecimalFormat df = new DecimalFormat("#.###");
		int maxSuspCandidates = ConfigurationProperties.getPropertyInt("maxsuspcandidates");

		List<Statement> gzCandidates = new ArrayList();

		for (Statement gzoltarStatement : gz.getSuspiciousStatements()) {
			String compName = gzoltarStatement.getMethod().getParent().getLabel();
			if (gzoltarStatement.getSuspiciousness() >= thr && isSource(compName, srcFolder)) {
				gzCandidates.add(gzoltarStatement);

			}

		}
		// If we do not have candidate due the threshold is to high, we add all
		// as suspicious
		if (gzCandidates.isEmpty()) {
			gzCandidates.addAll(gz.getSuspiciousStatements());

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
		}

		logger.info("Gzoltar found: " + candidates.size() + " with susp > " + thr + ", we consider: " + max);

		return new FaultLocalizationResult(candidates, failingTestCases);
	}

	protected boolean isSource(String compName, String srcFolder) {
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
