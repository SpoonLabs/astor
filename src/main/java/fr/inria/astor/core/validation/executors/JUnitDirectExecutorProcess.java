package fr.inria.astor.core.validation.executors;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import fr.inria.astor.core.validation.entity.TestResult;
import fr.inria.astor.junitexec.JUnitTestExecutor;

/**
 * Process-based program variant validation. This class calls directly to JUnit
 * into a separate process and parses its output
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
@Deprecated
public class JUnitDirectExecutorProcess extends JUnitExecutorProcess {

	

	public JUnitDirectExecutorProcess() {
		super();
	}

	public JUnitDirectExecutorProcess(boolean avoidInterrupt) {
		super(avoidInterrupt);
	}

	/**
	 * This method analyze the output of the junit executor (i.e.,
	 * {@link JUnitTestExecutor}) and return an entity called TestResult with
	 * the result of the test execution
	 * 
	 * @param p
	 * @return
	 */
	@Override
	protected TestResult getTestResult(Process p) {
		TestResult tr = new TestResult();
		boolean success = false;

		String out = "";
		try {
			BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
			String line;
			while (in.ready() && (line = in.readLine()) != null) {
				out += line + "\n";
				if (line.startsWith("Tests run:")) {
					String[] s = line.split(":");
					int nrtc = Integer.valueOf(s[1].split(",")[0].trim());
					tr.casesExecuted = nrtc;
					int failing = Integer.valueOf(s[2].trim());
					tr.failures = failing;
					// TODO: put the names of the failing test cases
					/*
					 * if (!"".equals(s[3])) { String[] falinglist =
					 * s[3].replace("[", "").replace("]", "").split(","); for
					 * (String string : falinglist) { if
					 * (!string.trim().isEmpty())
					 * tr.failTest.add(string.trim()); } }
					 */
					success = true;
				} else if (line.startsWith("OK (")) { //
					String[] s = line.split("\\(");
					int executed = Integer.valueOf(s[1].split("tests")[0].trim());
					tr.casesExecuted = executed;
					tr.failures = 0;
					success = true;
				}

			}
			in.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (success)
			return tr;
		else {
			log.error("Error reading the validation process\n output: \n" + out + " \n error: "
					+ getProcessError(p.getErrorStream()));

			return null;
		}
	}



	@Override
	public String classNameToCall() {
	
		return org.junit.runner.JUnitCore.class.getName();
	}
	
	@Override
	public String defineInitialClasspath() {
		return System.getProperty("java.class.path");
	}


}
