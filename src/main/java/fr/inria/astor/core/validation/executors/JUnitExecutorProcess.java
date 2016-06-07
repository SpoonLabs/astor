package fr.inria.astor.core.validation.executors;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.validation.entity.TestResult;
import fr.inria.astor.junitexec.JUnitTestExecutor;

/**
 * Process-based program variant validation
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public abstract class  JUnitExecutorProcess {

	protected Logger log = Logger.getLogger(Thread.currentThread().getName());
	boolean avoidInterruption = false;
	
	public JUnitExecutorProcess(boolean avoidInterruption ) {
		this();
		this.avoidInterruption = avoidInterruption;
	}
	
	public JUnitExecutorProcess() {
		super();
	}

	public TestResult execute(String jvmPath,URL[] classpath, List<String> classesToExecute, int waitTime) {
		return execute(jvmPath,urlArrayToString(classpath), classesToExecute, waitTime);
	}

	public TestResult execute(String jvmPath, String classpath, List<String> classesToExecute, int waitTime) {
		Process p = null;
		jvmPath += File.separator + "java";
		String systemcp = 	defineInitialClasspath();

		classpath = systemcp + File.pathSeparator + classpath;

		List<String> cls = new ArrayList<>(classesToExecute);

		try {

			List<String> command = new ArrayList<String>();
			command.add(jvmPath);
			command.add("-Xmx2048m");
			command.add("-cp");
			command.add(classpath);
			command.add(classNameToCall());
			command.addAll(cls);

			printCommandToExecute(command);
			
			ProcessBuilder pb = new ProcessBuilder(command.toArray(new String[command.size()]));
			pb.redirectOutput();
			pb.redirectErrorStream(true);
			pb.directory(new File((ConfigurationProperties.getProperty("location"))));
			long t_start = System.currentTimeMillis();
			p = pb.start();

			//WorkerThreadHelper worker = new WorkerThreadHelper(p);
			//worker.start();
			//worker.join(waitTime);
			//log.debug("Launched for " + waitTime+" milliseconds");
			p.waitFor(waitTime,TimeUnit.MILLISECONDS);
			long t_end = System.currentTimeMillis();
			log.debug("Execution time " + ((t_end - t_start) / 1000) + " seconds");
		
			if(!avoidInterruption){
				log.debug("Running Exit Value");
				//We force obtaining the exit value.
				p.exitValue();
			}
			
			TestResult tr = getTestResult(p);
			p.destroy();
			
			return tr;
		} catch ( IOException |InterruptedException |IllegalThreadStateException  ex) {
			log.info("The Process that runs JUnit test cases had problems: " + ex.getMessage());
			if (p != null)
				p.destroy();
		}
		return null;
	}

	public abstract String defineInitialClasspath();

	public abstract String classNameToCall();

	/**
	 * This method analyze the output of the junit executor (i.e.,
	 * {@link JUnitTestExecutor}) and return an entity called TestResult with
	 * the result of the test execution
	 * 
	 * @param p
	 * @return
	 */
	protected abstract TestResult getTestResult(Process p);

	protected String urlArrayToString(URL[] urls) {
		String s = "";
		for (int i = 0; i < urls.length; i++) {
			URL url = urls[i];
			s += url.getPath() + File.pathSeparator;
		}
		return s;
	}
	protected String getProcessError(InputStream str){
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
	
	private void printCommandToExecute(List<String> command) {
		String commandString = command.toString().replace("[", "").replace("]", "").replace(",", " ");
		int trunk = ConfigurationProperties.getPropertyInt("commandTrunk");
		String commandToPrint = (trunk != 0 && commandString.length() > trunk)
				? (commandString.substring(0, trunk) + "..AND " + (commandString.length() - trunk) + " CHARS MORE...")
				: commandString;
		log.debug("Executing process: \n" + commandToPrint);
	}

}
