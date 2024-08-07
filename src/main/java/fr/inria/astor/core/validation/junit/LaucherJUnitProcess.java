package fr.inria.astor.core.validation.junit;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Field;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import fr.inria.astor.approaches.tos.core.MetaGenerator;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectConfiguration;
import fr.inria.astor.core.validation.results.TestResult;

/**
 * Lauches a process and parses its output.
 *
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class LaucherJUnitProcess {

	protected Logger log = Logger.getLogger(Thread.currentThread().getName());
	boolean avoidInterruption = false;

	public LaucherJUnitProcess(boolean avoidInterruption) {
		super();
		this.avoidInterruption = avoidInterruption;
	}

	public LaucherJUnitProcess() {
		this(false);
	}

	public TestResult execute(String jvmPath, URL[] classpath, List<String> classesToExecute, int waitTime) {
		return execute(jvmPath, urlArrayToString(classpath), classesToExecute, waitTime);
	}

	boolean outputInFile = ConfigurationProperties.getPropertyBool("processoutputinfile");

	public TestResult execute(String jvmPath, String classpath, List<String> classesToExecute, int waitTime) {
		String envOS = System.getProperty("os.name");
		String timeZone = ConfigurationProperties.getProperty("timezone");

		Process p = null;
		UUID procWinUUID = null;

		String newJvmPath = jvmPath + File.separator + "java";
		List<String> cls = new ArrayList<>(new HashSet(classesToExecute));
		String newClasspath = classpath;
		if (ConfigurationProperties.getPropertyBool("runjava7code") || ProjectConfiguration.isJDKLowerThan8()) {
			newClasspath = (new File(ConfigurationProperties.getProperty("executorjar")).getAbsolutePath())
					+ File.pathSeparator + classpath;
		}

		try {
			File ftemp = null;
			if (outputInFile)
				ftemp = File.createTempFile("out", "txt");

			List<String> command = new ArrayList<String>();

			command.add("\"" + newJvmPath + "\"");
			command.add("-Xmx2048m");

			String[] ids = ConfigurationProperties.getProperty(MetaGenerator.METALL).split(File.pathSeparator);
			for (String mutid : ids) {
				command.add("-D" + MetaGenerator.MUT_IDENTIFIER + mutid + "="
						+ ConfigurationProperties.getProperty(MetaGenerator.MUT_IDENTIFIER + mutid));
			}
			if (envOS.contains("Windows")) {
				procWinUUID = UUID.randomUUID();
				command.add("-DwinProcUUID=" + procWinUUID);
				System.setProperty("user.timezone", timeZone);
			}

			command.add("-cp");
			command.add("\"" + newClasspath + "\"");
			command.add(laucherClassName().getCanonicalName());
			command.addAll(cls);

			ProcessBuilder pb;
			if (!envOS.contains("Windows")) {
				printCommandToExecute(command, waitTime);
				pb = new ProcessBuilder("/bin/bash");
			} else {
				command.set(0, "'" + newJvmPath + "'");
				command.set(5, "'" + newClasspath + "'");
				// On Windows, BufferedWriter have a problem to write over 8192 characters.
				// We must provide command in ProcessBuilder constructor.
				pb = new ProcessBuilder("powershell", "-Command", "& " + toString(command));
			}

			if (outputInFile) {
				pb.redirectOutput(ftemp);
			} else
				pb.redirectOutput();
			pb.redirectErrorStream(true);
			pb.directory(new File((ConfigurationProperties.getProperty("location"))));
			long t_start = System.currentTimeMillis();
			p = pb.start();

			BufferedWriter p_stdin = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));

			try {
				if (!envOS.contains("Windows")) {
					// Set up the timezone
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
				}

				// end
				p_stdin.write("exit");
				p_stdin.newLine();
				p_stdin.flush();

			} catch (IOException e) {
				log.error(e);
			}

			//
			if (!p.waitFor(waitTime, TimeUnit.MILLISECONDS)) {
				killProcess(p, waitTime, procWinUUID);

				return null;
			}
			long t_end = System.currentTimeMillis();
			log.debug("Execution time " + ((t_end - t_start) / 1000) + "seconds");

			if (!avoidInterruption) {
				// We force obtaining the exit value.
				p.exitValue();
			}

			BufferedReader output = null;
			if (outputInFile)
				output = new BufferedReader(new FileReader(ftemp.getAbsolutePath()));
			else
				output = new BufferedReader(new InputStreamReader(p.getInputStream()));

			TestResult tr = getTestResult(output);
			p.destroyForcibly();
			return tr;
		} catch (IOException | InterruptedException | IllegalThreadStateException ex) {
			log.info("The Process that runs JUnit test cases had problems: " + ex.getMessage());
			killProcess(p, waitTime, procWinUUID);
		}
		return null;
	}

	/**
	 * Workarrond. I will be solved when migrating to java 9.
	 * https://docs.oracle.com/javase/9/docs/api/java/lang/Process.html#descendants--
	 * 
	 * @param waitTime
	 */
	private void killProcess(Process p, int waitTime, UUID procWinUUID) {
		if (p == null)
			return;

		Object pid = null;
		try {
			if (procWinUUID != null) {
				Process survivedPID = Runtime.getRuntime()
						.exec("wmic process where \"commandline like '%-DwinProcUUID=" + procWinUUID
								+ "%' and name like '%java.exe%'\" get processid");
				BufferedReader outputSurvivedPIDs = new BufferedReader(
						new InputStreamReader(survivedPID.getInputStream()));
				String line;
				int i = 0;
				while ((line = outputSurvivedPIDs.readLine()) != null) {
					if (i == 2 && !line.isEmpty()) {
						pid = line.trim();
						break;
					}
					i++;
				} 
			} else {
				Field f = p.getClass().getDeclaredField("pid");
				f.setAccessible(true);
				pid = f.get(p);
			}
		} catch (IOException | NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException e) {
			log.error(e);
		}

		p.destroyForcibly();
		log.info("The Process that runs JUnit test cases did not terminate within waitTime of "
				+ TimeUnit.MILLISECONDS.toSeconds(waitTime) + " seconds");
		log.info("Killing the Process that runs JUnit test cases " + pid);

		// workarrond!!
		if (ConfigurationProperties.getPropertyBool("forcesubprocesskilling")) {
			Integer subprocessid = Integer.valueOf(pid.toString()) + 1;
			try {
				Process process;
				if (procWinUUID != null) {
					log.error("Killing Windows process " + pid);
					process = Runtime.getRuntime().exec("taskkill /T /F /PID " + pid);
				} else {
					log.debug("Killing subprocess " + subprocessid);
					process = new ProcessBuilder(new String[] { "kill", subprocessid.toString() }).start();
				}
				process.waitFor();
			} catch (IOException | InterruptedException e) {
				if (procWinUUID != null) 
					log.error("Problems killing Windows process " + pid);
				else
					log.error("Problems killing subprocess " + subprocessid);
				log.error(e);
			}
		}
	}

	protected String urlArrayToString(URL[] urls) {
		String s = "";
		for (int i = 0; i < urls.length; i++) {
			URL url = urls[i];
			s += "\"" + url.getPath() + "\"" + File.pathSeparator;
		}
		return s;
	}

	protected String getProcessError(InputStream str) {
		String out = "";
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(str));
			String line;
			while ((line = in.readLine()) != null) {
				out += line + "\n";
			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return out;
	}

	private void printCommandToExecute(List<String> command, int waitTime) {
		String commandString = toString(command);
		int trunk = ConfigurationProperties.getPropertyInt("commandTrunk");
		String commandToPrint = (trunk != 0 && commandString.length() > trunk)
				? (commandString.substring(0, trunk) + "..AND " + (commandString.length() - trunk) + " CHARS MORE...")
				: commandString;
		log.debug("Executing process: (timeout" + waitTime / 1000 + "secs) \n" + commandToPrint);
	}

	private String toString(List<String> command) {
		String commandString = command.toString().replace("[", "").replace("]", "").replace(",", " ");
		return commandString;
	}

	public Class laucherClassName() {
		if (ConfigurationProperties.getPropertyBool("logtestexecution"))
			return JUnitExternalExecutor.class;
		else
			return JUnitNologExternalExecutor.class;

	}

	/**
	 * This method analyze the output of the junit executor (i.e.,
	 * {@link JUnitTestExecutor}) and return an entity called TestResult with the
	 * result of the test execution
	 *
	 * @param p
	 * @return
	 */
	protected TestResult getTestResult(BufferedReader in) {
		log.debug("Analyzing output from process");
		TestResult tr = new TestResult();
		boolean success = false;
		String processOut = "";
		try {
			String line;
			while ((line = in.readLine()) != null) {
				processOut += line + "\n";
				if (line.startsWith(JUnitExternalExecutor.OUTSEP)) {
					String[] resultPrinted = line.split(JUnitExternalExecutor.OUTSEP);
					int nrtc = Integer.valueOf(resultPrinted[1]);
					tr.casesExecuted = nrtc;
					int nrfailing = Integer.valueOf(resultPrinted[2]);
					tr.failures = nrfailing;
					if (resultPrinted.length > 3 && !"".equals(resultPrinted[3])) {
						String[] failingTestList = resultPrinted[3].replace("[", "").replace("]", "").split(",");
						for (String failingTest : failingTestList) {
							failingTest = failingTest.trim();
							if (!failingTest.isEmpty() && !failingTest.equals("-"))
								tr.failTest.add(failingTest);
						}
					}
					success = true;
				}
			}
			// log.debug("Process output:\n"+ out);
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (success)
			return tr;
		else {
			log.error("Error reading the validation process\n output: \n" + processOut);
			return null;
		}
	}

}