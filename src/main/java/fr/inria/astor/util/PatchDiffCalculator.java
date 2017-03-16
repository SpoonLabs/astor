package fr.inria.astor.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ProjectRepairFacade;

/**
 * Diff creator
 * 
 * @author Matias Martinez
 *
 */
public class PatchDiffCalculator {

	protected Logger log = Logger.getLogger(PatchDiffCalculator.class.getName());

	/**
	 * Calculates the diff between a solution and the original program variant.
	 * 
	 * @param projectFacade
	 * @param programVariant
	 * @return
	 */
	public String getDiff(ProjectRepairFacade projectFacade, ProgramVariant programVariant) {
		String diffResults = "";

		for (List<OperatorInstance> oppsGeneration : programVariant.getOperations().values()) {

			for (OperatorInstance opi : oppsGeneration) {

				ModificationPoint mp = opi.getModificationPoint();

				String fileName = mp.getCtClass().getQualifiedName().replace(".", File.separator) + ".java";
				File foriginal = new File(projectFacade.getInDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT)
						+ File.separator + fileName);
				File ffixed = new File(projectFacade.getInDirWithPrefix(programVariant.currentMutatorIdentifier())
						+ File.separator + fileName);

				log.debug(foriginal.getAbsolutePath());
				log.debug(ffixed.getAbsolutePath());
				if (!foriginal.exists() || !ffixed.exists()) {
					log.error("A file with a solution does not exist");
					return null;
				}

				String diff = getDiff(foriginal, ffixed);
				diffResults += diff + '\n';
			}
		}
		return diffResults;
	}

	public String getDiff(File original, File newvariant) {

		try {
			String line = "";
			ProcessBuilder builder = new ProcessBuilder("/bin/bash");
			builder.redirectErrorStream(true);

			Process process = builder.start();

			BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));

			try {
				// Set up the timezone
				p_stdin.write("diff -w -b -u " + original.getAbsolutePath() + " " + newvariant.getAbsolutePath());
				p_stdin.newLine();
				p_stdin.flush();

				// end
				p_stdin.write("exit");
				p_stdin.newLine();
				p_stdin.flush();

			} catch (IOException e) {
				e.printStackTrace();
			}

			process.waitFor(30, TimeUnit.SECONDS);

			InputStream stderr = process.getErrorStream();
			InputStream stdout = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(stdout));
			String dd = "";
			while ((line = reader.readLine()) != null) {

				if (line.startsWith("---") || line.startsWith("+++"))
					dd += line.split("2017-")[0] + "\n";
				else
					dd += line + "\n";

			}
			process.destroyForcibly();
			return dd;
		} catch (Exception e) {
			e.printStackTrace();
			log.error(e);
		}
		return null;
	}

}
