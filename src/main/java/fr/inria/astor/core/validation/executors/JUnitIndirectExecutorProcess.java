package fr.inria.astor.core.validation.executors;

import java.io.BufferedReader;
import java.io.File;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.validation.entity.TestResult;
import fr.inria.astor.junitexec.JUnitTestExecutor;

/**
 * 
 * @author Matias Martinez
 *
 */
public class JUnitIndirectExecutorProcess extends JUnitExecutorProcess {

	public JUnitIndirectExecutorProcess(boolean avoidInterrupt) {
		super(avoidInterrupt);
	}

	public String classNameToCall() {
		return (ConfigurationProperties.getProperty("testexecutorclass"));
	}

	@Override
	public String defineInitialClasspath() {
		return (new File(ConfigurationProperties.getProperty("executorjar")).getAbsolutePath());
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
	protected TestResult getTestResult(BufferedReader in) {
		log.debug("Analyzing output from process");
		TestResult tr = new TestResult();
		boolean success = false;
		String processOut = "";
		try {
			String line;
			while ((line = in.readLine()) != null) {
				processOut += line + "\n";
				if (line.startsWith(JUnitTestExecutor.OUTSEP)) {
					String[] resultPrinted = line.split(JUnitTestExecutor.OUTSEP);
					int nrtc = Integer.valueOf(resultPrinted[1]);
					tr.casesExecuted = nrtc;
					int nrfailing = Integer.valueOf(resultPrinted[2]);
					tr.failures = nrfailing;
					if (resultPrinted.length > 3 &&!"".equals(resultPrinted[3])) {
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
			//log.debug("Process output:\n"+ out);
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
