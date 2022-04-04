package fr.inria.main;

import java.io.File;
import java.io.FileWriter;
import java.util.Arrays;
import java.util.List;

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
	public void run(String location, String projectName, String dependencies, String packageToInstrument, double thfl,
			String failing) throws Exception {

		long startT = System.currentTimeMillis();
		initProject(location, projectName, dependencies, packageToInstrument, thfl, failing);

		String faultLocalizationModeS = ConfigurationProperties.getProperty("faultlocalization").toUpperCase();

		FaultLocalization faultLocalizationMode = FaultLocalization.valueOf(faultLocalizationModeS);

		if (faultLocalizationMode == null) {

			log.error(
					"No mode for " + faultLocalizationMode + " Options " + Arrays.toString(FaultLocalization.values()));
		}

		boolean executed = false;

		if (faultLocalizationMode.equals(FaultLocalization.GZOLTAR)
				|| faultLocalizationMode.equals(FaultLocalization.ALL)) {

			executed = true;

			GZoltarFaultLocalization gZoltarFaultLocalization = new GZoltarFaultLocalization();
			List<String> regressionTestForFaultLocalization = gZoltarFaultLocalization
					.findTestCasesToExecute(projectFacade);

			FaultLocalizationResult result = gZoltarFaultLocalization.searchSuspicious(this.projectFacade,
					regressionTestForFaultLocalization);

			save(result, FaultLocalization.GZOLTAR);
		}

		if (faultLocalizationMode.equals(FaultLocalization.FLACOCO)
				|| faultLocalizationMode.equals(FaultLocalization.ALL)) {

			executed = true;

			FlacocoFaultLocalization flacocoFaultLocalization = new FlacocoFaultLocalization();

			FaultLocalizationResult result = flacocoFaultLocalization.searchSuspicious(this.projectFacade, null);

			save(result, FaultLocalization.FLACOCO);

		}

		if (faultLocalizationMode.equals(FaultLocalization.GZOLTAR1_7)
				|| faultLocalizationMode.equals(FaultLocalization.ALL)) {

			executed = true;

			NovelGZoltarFaultLocalization gzoltarFaultLocalization = new NovelGZoltarFaultLocalization();

			List<String> regressionTestForFaultLocalization = gzoltarFaultLocalization
					.findTestCasesToExecute(projectFacade);
			System.out.println("Test to execute: " + regressionTestForFaultLocalization);
			FaultLocalizationResult searchSuspicious = gzoltarFaultLocalization.searchSuspicious(projectFacade,
					regressionTestForFaultLocalization);

			System.out.println("FL results: " + searchSuspicious);

			save(searchSuspicious, FaultLocalization.GZOLTAR1_7);

		}

		if (!executed) {

			log.error("Could not execute any mode at " + faultLocalizationMode + " Options "
					+ Arrays.toString(FaultLocalization.values()));
		}

		long endT = System.currentTimeMillis();
		log.info("Time Total(s): " + (endT - startT) / 1000d);

	}

	public void save(FaultLocalizationResult searchSuspicious, FaultLocalization approach) {
		List<SuspiciousCode> susp = searchSuspicious.getCandidates();
		try {

			String output = this.projectFacade.getProperties().getWorkingDirRoot();

			String noout = (ConfigurationProperties.hasProperty("outfl") ? ConfigurationProperties.getProperty("outfl")
					: output);
			File f = (new File(noout));
			if (!f.exists()) {
				f.mkdirs();
			}

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

}