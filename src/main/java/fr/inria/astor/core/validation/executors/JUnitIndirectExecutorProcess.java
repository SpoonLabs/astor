package fr.inria.astor.core.validation.executors;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;

import fr.inria.astor.core.validation.entity.TestResult;
import fr.inria.astor.junitexec.JUnitTestExecutor;

/**
 * 
 * @author Matias Martinez
 *
 */
public class JUnitIndirectExecutorProcess extends JUnitExecutorProcess{

	public JUnitIndirectExecutorProcess(boolean avoidInterrupt) {
		super(avoidInterrupt);
	}

	public String classNameToCall(){
		return	("JUnitExternalExecutor");
		//return JUnitTestExecutor.class.getName();
	}

	@Override
	public String defineInitialClasspath(){
		return (new File("./lib/jtestex-0.0.1.jar").getAbsolutePath());
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
			while ((line = in.readLine()) != null) {
				out += line + "\n";
				if (line.startsWith(JUnitTestExecutor.OUTSEP)) {
					String[] s = line.split(JUnitTestExecutor.OUTSEP);
					int nrtc = Integer.valueOf(s[1]);
					tr.casesExecuted = nrtc;
					int failing = Integer.valueOf(s[2]);
					tr.failures = failing;
					if (!"".equals(s[3])) {
						String[] falinglist = s[3].replace("[", "").replace("]", "").split(",");
						for (String string : falinglist) {
							if (!string.trim().isEmpty())
								tr.failTest.add(string.trim());
						}
					}
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
			log.error("Error reading the validation process\n output: \n"+
			out +" \n error: "+getProcessError(p.getErrorStream()));
			
			return null;
		}
	}
}
