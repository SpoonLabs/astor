package fr.inria.main;

import java.io.File;
import java.io.FileWriter;
import java.util.List;

import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.faultlocalization.flacoco.FlacocoFaultLocalization;
import fr.inria.astor.core.faultlocalization.gzoltar.GZoltarFaultLocalization;
import fr.inria.astor.core.setup.ConfigurationProperties;

public class FaultLocalizationMain extends AbstractMain {

	public static void main(String[] args) throws Exception {
		FaultLocalizationMain m = new FaultLocalizationMain();
		m.execute(args);
	}

	@Override
	public void run(String location, String projectName, String dependencies, String packageToInstrument, double thfl,
			String failing) throws Exception {

		long startT = System.currentTimeMillis();
		initProject(location, projectName, dependencies, packageToInstrument, thfl, failing);

		String fl = ConfigurationProperties.getProperty("faultlocalization").toLowerCase();

		if (fl.equals("gzoltar") || fl.equals("both")) {
			GZoltarFaultLocalization gZoltarFaultLocalization = new GZoltarFaultLocalization();
			List<String> regressionTestForFaultLocalization = gZoltarFaultLocalization
					.findTestCasesToExecute(projectFacade);

			List<SuspiciousCode> susp = gZoltarFaultLocalization
					.searchSuspicious(this.projectFacade, regressionTestForFaultLocalization).getCandidates();

			save(susp, "gzoltar");
		}

		if (fl.equals("flacoco") || fl.equals("both")) {

			FlacocoFaultLocalization flacocoFaultLocalization = new FlacocoFaultLocalization();

			List<SuspiciousCode> susp = flacocoFaultLocalization.searchSuspicious(this.projectFacade, null)
					.getCandidates();
			save(susp, "flacoco");

		}

		long endT = System.currentTimeMillis();
		log.info("Time Total(s): " + (endT - startT) / 1000d);

	}

	public void save(List<SuspiciousCode> susp, String approach) {
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