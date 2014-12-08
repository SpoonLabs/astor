package fr.inria.astor.core.validation;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runners.model.InitializationError;

/**
 * This class runs a test suite. Optional, register its coverage
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class TestRunnerEngine {

	List<String> successTest = new ArrayList<String>();

	List<String> failTest = new ArrayList<String>();

	String jmockitDir = "lib\\faultlocalization\\";

	/**
	 * Run test without measuring coverage.
	 * 
	 * @param outputFilePath
	 * @param testToRun
	 * @throws InitializationError
	 * @throws FileNotFoundException
	 */
	public void runTest(String outputFilePath, Class testToRun) throws InitializationError, FileNotFoundException {

		JUnitCore runner = new JUnitCore();
		File file = new File(outputFilePath);
		checkandcreate(file);
		// OutputStreamWriter out = new OutputStreamWriter(new
		// FileOutputStream(file));
		// runner.addListener(new TestExecutionListener(out));
		runner.run(testToRun);
	}

	private void checkandcreate(File file) {

		if (!file.getParentFile().exists())
			file.getParentFile().mkdirs();
	}

	public void runTest(String outputFilePath, List<Class> testToRun) throws InitializationError, FileNotFoundException {

		JUnitCore runner = new JUnitCore();
		Result r = runner.run(testToRun.toArray(new Class[testToRun.size()]));
		String out = "[";
		int count = 0;
		for (Failure f : r.getFailures()) {
			/// System.out.println("--> "+f.getTrace());
			//out+="-,";
			out+=f.toString()+"-,";
			count++;
			if(count>10){
				out+="...and "+(r.getFailureCount()-10)+" failures more,";
				break;	
			}
		}
		out = out.substring(0, out.length()-1)+"]";
		System.out.println(OUTSEP + r.getRunCount() + OUTSEP + r.getFailureCount() + OUTSEP + out + OUTSEP);

	}

	public static String OUTSEP = "mmout";

	public static void main(String[] arg) throws Exception, InitializationError {
		TestRunnerEngine re = new TestRunnerEngine();
		/*
		 * if (arg.length < 2) throw new IllegalArgumentException(
		 * "Use: [0] path of file result, [1..] classes to execute test ");
		 * 
		 * String classToRun = arg[1].toString(); if(arg.length == 2){ Class c =
		 * Class.forName(classToRun); re.runTest(arg[0].toString(), c); }else{
		 */
		List<Class> classes = new ArrayList<Class>();
		for (int i = 0; i < arg.length; i++) {
			String classString = arg[i];
			Class c = Class.forName(classString);
			classes.add(c);
		}
		re.runTest(arg[0].toString(), classes);
		// }
	}

	/**
	 * Run a test and measure the coverage.
	 * 
	 * TODO: problem: listener of the results. To re-test
	 * 
	 * @throws Exception
	 */
	public void runMeasuringCoverage(String testName, String defaultOut, String classpath) throws Exception {

		// TestRunner
		// System.setProperty("jmockit-coverage-output", "merge");
		// System.setProperty("jmockit-coverage-output", "serial");
		// System.setProperty("jmockit-coverage-outputDir","c:/tmp/");
		// System.setProperty("jmockit-coverage-srcDirs", srcToTest);

		ProcessBuilder processBuilder = new ProcessBuilder("java", "-classpath", classpath,
				// --
				"-javaagent:" + jmockitDir + "jmockit.jar",
				"-Djmockit-coverage-output=serial",// serial
				"-Djmockit-coverage-outputDir=" + defaultOut,
				// --
				"-Djmockit-coverage-classes=org\\.apache\\.commons\\.math\\.([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*",
				// "-Djmockit-coverage-classes=org\\.mozilla\\.([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*",
				// "-Djmockit-coverage-excludes=org\\.mozilla\\.([a-zA-Z_$][a-zA-Z\\d_$]*\\.)*[a-zA-Z_$][a-zA-Z\\d_$]*",
				// "-Djmockit-coverage-classes=org\\.mozilla\\.javascript\\.tools\\.shell\\.Main",
				// --
				"org.inria.sacha.faultlocalization.TestRunnerEngine", defaultOut + "testout.xml", testName);
		Process process = processBuilder.start();

		InputStream in = process.getErrorStream();
		BufferedReader brCleanUp = null;
		String line;
		boolean error = false;
		if (in != null) {
			brCleanUp = new BufferedReader(new InputStreamReader(in));

			while ((line = brCleanUp.readLine()) != null) {
				System.out.println("[Sterr] " + line);
				error = true;
			}
		}
		if (in != null) {
			in = process.getInputStream();

			brCleanUp = new BufferedReader(new InputStreamReader(in));
			int count = 0;
			while ((line = brCleanUp.readLine()) != null) {
				System.out.println("[Stdout] " + (count++) + ": " + line);
			}
		}
		if (error) {
			throw new IllegalStateException("Error: the process has failed and logged errors ");
		}
	}

}
