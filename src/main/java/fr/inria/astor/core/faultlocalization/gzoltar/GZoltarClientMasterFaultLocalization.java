package fr.inria.astor.core.faultlocalization.gzoltar;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import fr.inria.astor.core.faultlocalization.FaultLocalizationResult;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;

/**
 * GZoltar using Client Master Architecture
 * 
 * @author Matias Martinez
 *
 */
public class GZoltarClientMasterFaultLocalization extends GZoltarFaultLocalization {

	static Logger logger = Logger.getLogger(GZoltarClientMasterFaultLocalization.class.getName());

	@Override
	public FaultLocalizationResult searchSuspicious(String locationBin, List<String> testsToExecute,
			List<String> toInstrument, List<String> cp, String srcFolder) throws Exception {

		logger.info("GZoltar CM location " + locationBin);
		Double thr = ConfigurationProperties.getPropertyDouble("flthreshold");
		logger.info("Gzoltar fault localization: min susp value parameter: " + thr);

		Set<String> tcset = new HashSet<String>(testsToExecute);

		String ncp = cp.stream().collect(Collectors.joining(File.pathSeparator)) + File.pathSeparator + locationBin;

		String targetClasses = ConfigurationProperties.getProperty("classestoinstrument");

		if (targetClasses == null || targetClasses.trim().isEmpty())
			targetClasses = MutationSupporter.currentSupporter.getFactory().Type().getAll().stream()
					.map(t -> t.getQualifiedName()).collect(Collectors.joining(File.pathSeparator));

		String testNames = testsToExecute.stream().collect(Collectors.joining(File.pathSeparator));
		logger.debug("#test before: " + testsToExecute.size() + " #after " + tcset.size());
		logger.debug("#Target classes: " + MutationSupporter.currentSupporter.getFactory().Type().getAll().size());

		String jvmPath = ConfigurationProperties.getProperty("jvm4testexecution");

		Process p = null;
		jvmPath += File.separator + "java";

		try {

			List<String> command = new ArrayList<String>();

			long init = new Date().getTime();
			command.add(jvmPath);
			// command.add("-Xmx2048m");
			command.add("-jar");
			command.add(new File("./lib/com.gzoltar-1.6.1-java7-jar-with-dependencies.jar").getAbsolutePath());

			command.add("-Dproject_cp=" + ncp);
			command.add("-Dtargetclasses=" + targetClasses);
			command.add("-Dtestclasses=" + testNames);
			command.add("-diagnose");
			command.add("-Dtimelimit=" + (long) (ConfigurationProperties.getPropertyDouble("tmax2") / 1000));// milliseconds
																												// to
																												// seconds
			// command.add("-Dloglevel=None");

			Path foutput = Files.createTempDirectory("tempgz");
			command.add("-Dgzoltar_data_dir=" + foutput.toAbsolutePath());

			printCommandToExecute(command);

			ProcessBuilder pb = new ProcessBuilder("/bin/bash");
			pb.redirectOutput();
			pb.redirectErrorStream(true);
			pb.directory(new File(((ConfigurationProperties.getProperty("location")))));

			long t_start = System.currentTimeMillis();
			p = pb.start();

			BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));

			try {
				// Set up the timezone
				String timeZone = ConfigurationProperties.getProperty("timezone");
				p_stdin.write("TZ=\"" + timeZone + "\"");
				p_stdin.newLine();
				p_stdin.flush();
				p_stdin.write("export TZ");
				p_stdin.newLine();
				p_stdin.flush();
				p_stdin.write("echo $TZ");
				p_stdin.newLine();
				p_stdin.flush();
				// Writing the command
				p_stdin.write(toString(command));
				p_stdin.newLine();
				p_stdin.flush();

				// end
				p_stdin.write("exit");
				p_stdin.newLine();
				p_stdin.flush();

			} catch (IOException e) {
				logger.error(e);
			}

			//
			p.waitFor((long) (ConfigurationProperties.getPropertyDouble("tmax2") * 2), TimeUnit.MINUTES);
			long t_end = System.currentTimeMillis();
			logger.debug("Execution time " + ((t_end - t_start) / 1000) + " seconds");

			readOut(p);
			p.destroyForcibly();

			long end = new Date().getTime();

			logger.debug("GZ CM time " + (end - init) / 1000 + " seconds");
			//

			//
			FaultLocalizationResult fl = parseOutputFile(foutput.toFile(), thr);
			//
			return fl;
			//
		} catch (IOException | InterruptedException | IllegalThreadStateException ex) {
			logger.info("The Process that runs JUnit test cases had problems: " + ex.getMessage());
			if (p != null)
				p.destroyForcibly();
		}
		return null;
	}

	private void readOut(Process p) {
		boolean success = false;
		String out = "";
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				out += line + "\n";

			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println(out);
	}

	private void printCommandToExecute(List<String> command) {
		String commandString = toString(command);
		int trunk = ConfigurationProperties.getPropertyInt("commandTrunk");
		String commandToPrint = (trunk != 0 && commandString.length() > trunk)
				? (commandString.substring(0, trunk) + "..AND " + (commandString.length() - trunk) + " CHARS MORE...")
				: commandString;
		logger.info("Executing process: \n" + commandToPrint);
	}

	private String toString(List<String> command) {
		String commandString = command.toString().replace("[", "").replace("]", "").replace(",", " ");
		return commandString;
	}

	public SuspiciousCode parseLine(String line) {
		try {
			if (line.equals("Component,OCHIAI"))
				return null;
			SuspiciousCode sc = new SuspiciousCode();
			//
			String[] infoLine = line.split("#");
			String[] linesusp = infoLine[1].split(",");
			sc.setLineNumber(Integer.valueOf(linesusp[0]));
			sc.setSusp(Double.parseDouble(linesusp[1]));

			String[] splitFile = line.split("<");
			String fileName = splitFile[0].replace("[", ".");
			sc.setFileName(fileName);
			String[] classLine = splitFile[1].split("\\{");
			String className = classLine[0];
			sc.setClassName(className);
			String method = "";
			if (classLine.length > 1)
				method = classLine[1];//
			sc.setMethodName(method);

			return sc;
		} catch (Exception e) {
			logger.error("-->" + line);
			logger.error(e);
			e.printStackTrace();
			return null;
		}
	}

	public FaultLocalizationResult parseOutputFile(File path, Double thr) {
		File spectrapath = new File(path.getAbsolutePath() + File.separator + "spectra");
		File testpath = new File(path.getAbsolutePath() + File.separator + "tests");

		List<SuspiciousCode> codes = new ArrayList<>();
		List<String> failingTestCases = new ArrayList<>();
		try (BufferedReader br = new BufferedReader(new FileReader(spectrapath))) {

			String line;
			while ((line = br.readLine()) != null) {
				// System.out.println(line);
				SuspiciousCode sc = parseLine(line);
				if (sc != null && sc.getSuspiciousValue() > 0 && sc.getSuspiciousValue() >= thr)
					codes.add(sc);
			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		// org.apache.commons.lang3.AnnotationUtilsTest#testAnnotationsOfDifferingTypes,PASS,92358174
		try (BufferedReader br = new BufferedReader(new FileReader(testpath))) {

			String line;
			while ((line = br.readLine()) != null) {
				System.out.println(line);
				String[] lineS = line.split(",");
				if (lineS[1].equals("FAIL")) {
					String name = lineS[0].split("#")[0];
					if (!failingTestCases.contains(name))
						failingTestCases.add(name);
				}

			}

		} catch (IOException e) {
			e.printStackTrace();
		}

		///

		codes.forEach(e -> logger.debug(e));
		failingTestCases.forEach(e -> logger.debug(e));
		FaultLocalizationResult result = new FaultLocalizationResult(codes, failingTestCases);

		return result;

	}

}
