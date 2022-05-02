package fr.inria.main;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import fr.inria.astor.core.faultlocalization.FaultLocalizationResult;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.faultlocalization.flacoco.FlacocoFaultLocalization;
import fr.inria.astor.core.faultlocalization.gzoltar.GZoltarFaultLocalization;
import fr.inria.astor.core.faultlocalization.gzoltar.NovelGZoltarFaultLocalization;
import fr.inria.astor.core.setup.ConfigurationProperties;

/**
 * Facade for using fault localization from Astor
 * 
 * @author Matias Martinez
 *
 */
public class FaultLocalizationMain extends AbstractMain {

	public enum FaultLocalization {
		FLACOCO, ALL, GZOLTAR, GZOLTAR1_7
	};

	public static void main(String[] args) throws Exception {
		FaultLocalizationMain m = new FaultLocalizationMain();
		m.execute(args);
	}

	@Override
	public ExecutionResult run(String location, String projectName, String dependencies, String packageToInstrument,
			double thfl, String failing) throws Exception {

		CompositeExecutionResult results = new CompositeExecutionResult();
		long startT = System.currentTimeMillis();
		initProject(location, projectName, dependencies, packageToInstrument, thfl, failing);

		String faultLocalizationModeS = ConfigurationProperties.getProperty("faultlocalization").toUpperCase();

		FaultLocalization faultLocalizationMode = FaultLocalization.valueOf(faultLocalizationModeS);

		List<String> testsToRun = new ArrayList<>();

		String toRun = null;

		if (ConfigurationProperties.hasProperty("regressiontestcases4fl")) {
			toRun = ConfigurationProperties.getProperty("regressiontestcases4fl");

			if (toRun != null && !toRun.isEmpty()) {
				testsToRun = Arrays.asList(toRun.split(File.pathSeparator));
			}

		}

		else if (ConfigurationProperties.hasProperty("filetestcases4fl")) {

			File ftcases = new File(ConfigurationProperties.getProperty("filetestcases4fl"));
			if (ftcases.exists()) {
				testsToRun = Files.lines(ftcases.toPath()).collect(Collectors.toList());
			} else {
				new IllegalArgumentException("File with test cases not found " + ftcases.getAbsolutePath());
			}

		}

		if (faultLocalizationMode == null)

		{

			log.error(
					"No mode for " + faultLocalizationMode + " Options " + Arrays.toString(FaultLocalization.values()));
		}

		boolean executed = false;

		if (faultLocalizationMode.equals(FaultLocalization.GZOLTAR)
				|| faultLocalizationMode.equals(FaultLocalization.ALL)) {

			executed = true;

			GZoltarFaultLocalization gZoltarFaultLocalization = new GZoltarFaultLocalization();

			FaultLocalizationResult result = null;

			if (testsToRun.size() > 0) {

				result = gZoltarFaultLocalization.searchSuspicious(this.projectFacade, testsToRun);

			} else {
				List<String> regressionTestForFaultLocalization = gZoltarFaultLocalization
						.findTestCasesToExecute(projectFacade);

				result = gZoltarFaultLocalization.searchSuspicious(this.projectFacade,
						regressionTestForFaultLocalization);

			}

			save(result, FaultLocalization.GZOLTAR);

			results.add(result);
		}

		if (faultLocalizationMode.equals(FaultLocalization.FLACOCO)
				|| faultLocalizationMode.equals(FaultLocalization.ALL)) {

			executed = true;

			FlacocoFaultLocalization flacocoFaultLocalization = new FlacocoFaultLocalization();

			FaultLocalizationResult result = flacocoFaultLocalization.searchSuspicious(this.projectFacade, testsToRun);

			save(result, FaultLocalization.FLACOCO);

			results.add(result);

		}

		if (faultLocalizationMode.equals(FaultLocalization.GZOLTAR1_7)
				|| faultLocalizationMode.equals(FaultLocalization.ALL)) {

			executed = true;

			NovelGZoltarFaultLocalization gzoltarFaultLocalization = new NovelGZoltarFaultLocalization();

			FaultLocalizationResult result = gzoltarFaultLocalization.searchSuspicious(projectFacade, testsToRun);

			System.out.println("FL results: " + result);

			save(result, FaultLocalization.GZOLTAR1_7);

			results.add(result);

		}

		if (!executed) {

			log.error("Could not execute any mode at " + faultLocalizationMode + " Options "
					+ Arrays.toString(FaultLocalization.values()));
		}

		long endT = System.currentTimeMillis();
		log.info("Time Total(s): " + (endT - startT) / 1000d);

		if (results.getResults().size() > 1) {
			return results;
		} else if (results.getResults().size() == 1) {
			// Don't need to send a composite result as there is only 1
			return results.getResults().get(0);
		}
		return null;

	}

	public void save(FaultLocalizationResult result, FaultLocalization approach) {

		String output = this.projectFacade.getProperties().getWorkingDirRoot();

		String noout = (ConfigurationProperties.hasProperty("outfl") ? ConfigurationProperties.getProperty("outfl")
				: output);
		File f = (new File(noout));
		if (!f.exists()) {
			f.mkdirs();
		}

		saveSuspicious(result, approach, noout);
		saveFailing(result, approach, noout);
		saveExecuted(result, approach, noout);

	}

	private void saveSuspicious(FaultLocalizationResult result, FaultLocalization approach, String noout) {
		List<SuspiciousCode> susp = result.getCandidates();
		try {

			String fileName = noout + File.separator + approach + "_suspicious_"
					+ this.projectFacade.getProperties().getFixid() + ".csv";
			FileWriter fw = new FileWriter(fileName);
			for (SuspiciousCode suspiciousCode : susp) {
				fw.append(suspiciousCode.getClassName() + "," + suspiciousCode.getLineNumber() + ","
						+ suspiciousCode.getSuspiciousValueString());
				fw.append("\n");
			}
			fw.flush();
			fw.close();
			System.out.println("Saving Results at " + fileName);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	private void saveExecuted(FaultLocalizationResult result, FaultLocalization approach, String noout) {
		List<String> tests = result.getExecutedTestCasesMethods();
		String key = "_executed_tests_";

		saveListTests(approach, noout, tests, key);
	}

	private void saveFailing(FaultLocalizationResult result, FaultLocalization approach, String noout) {
		List<String> tests = result.getFailingTestCasesMethods();
		String key = "_failing_tests_";

		saveListTests(approach, noout, tests, key);
	}

	private void saveListTests(FaultLocalization approach, String noout, List<String> tests, String key) {
		try {

			String fileName = noout + File.separator + approach + key + this.projectFacade.getProperties().getFixid()
					+ ".csv";
			FileWriter fw = new FileWriter(fileName);
			for (String aTest : tests) {
				fw.append(aTest);
				fw.append("\n");
			}
			fw.flush();
			fw.close();
			System.out.println("Saving Results at " + fileName);

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

}