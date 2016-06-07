package fr.inria.astor.core.validation.executors;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.validation.entity.TestResult;
import fr.inria.astor.junitexec.JUnitTestExecutor;

/**
 * Process-based program variant validation, used for executing EvoSuite test cases
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */@Deprecated
public abstract class JUnitExecutorProcessWait extends JUnitExecutorProcess {

	@Override
	public TestResult execute(String jvmPath, String path, List<String> classesToExecute, int waitTime) {
		Process p = null;

		String javaPath = ConfigurationProperties.getProperty("jvm4evosuitetestexecution");
		javaPath += File.separator + "java";
		String systemcp = System.getProperty("java.class.path");

		path = systemcp + File.pathSeparator + path;

		List<String> cls = new ArrayList<>(classesToExecute);

		try {
			List<String> command = new ArrayList<String>();
			command.add(javaPath);
			command.add("-Xmx2048m");
			command.add("-cp");
			command.add(path);
			command.add(JUnitTestExecutor.class.getName());

			command.addAll(cls);

			ProcessBuilder pb = new ProcessBuilder(command.toArray(new String[command.size()]));
			pb.redirectOutput();
			pb.redirectErrorStream(true);
			pb.directory(new File((ConfigurationProperties.getProperty("location"))));
			long t_start = System.currentTimeMillis();
			p = pb.start();

			String commandString = command.toString().replace("[", "").replace("]", "").replace(",", " ");
			int trunk = ConfigurationProperties.getPropertyInt("commandTrunk");
			String commandToPrint = (trunk != 0 && commandString.length() > trunk) ? (commandString.substring(0, trunk)
					+ "..AND " + (commandString.length() - trunk) + " CHARS MORE...") : commandString;
			log.debug("Executing process: \n" + commandToPrint);
			// Here, we do not use the worker
			// jdk 8 p.waitFor(waitTime,TimeUnit.MILLISECONDS);
			// Exec: p.wait(waitTime);
			WorkerThreadHelper worker = new WorkerThreadHelper(p);
			worker.start();
			worker.join(waitTime);
			
			// ==
			long t_end = System.currentTimeMillis();

			TestResult tr = getTestResult(p);
			p.destroy();
			log.debug("Execution time " + ((t_end - t_start) / 1000) + " seconds");

			return tr;
		} catch (IOException | InterruptedException | IllegalThreadStateException ex) {
			log.error("The Process that runs JUnit test cases had problems: " + ex.getMessage());
			if (p != null)
				p.destroy();
		}
		return null;
	}

}
