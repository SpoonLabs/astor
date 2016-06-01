package fr.inria.astor.junitexec;

import java.util.ArrayList;
import java.util.List;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runners.model.InitializationError;

/**
 * This class runs a JUnit test suite i.e., a set of test cases.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class JUnitTestExecutor {

	List<String> successTest = new ArrayList<String>();

	List<String> failTest = new ArrayList<String>();

	public final static String OUTSEP = "mmout";

	

	public String createOutput(Result r) {
		String out = "[";
		int count = 0;
		try {
			for (Failure f : r.getFailures()) {
				String s = failureMessage(f);
				// if(s.startsWith("warning"))
				out += s + "-,";
				count++;
				if (count > 10) {
					out += "...and " + (r.getFailureCount() - 10) + " failures more,";
					break;
				}
			}
		} catch (Exception e) {
			// We do not care about this exception,
		}
		out = out.substring(0, out.length() - 1) + "]";
		return (OUTSEP + r.getRunCount() + OUTSEP + r.getFailureCount() + OUTSEP + out + OUTSEP);
	}

	private String failureMessage(Failure f) {
		try {
			return f.toString();
		} catch (Exception e) {
			return f.getTestHeader();
		}
	}

	public static void main(String[] arg) throws Exception, InitializationError {

		JUnitTestExecutor re = new JUnitTestExecutor();

		Result result = re.run(arg);
		// This sysout is necessary for the communication between process...
		System.out.println(re.createOutput(result));

		System.exit(0);
	}

	public Result run(String[] arg) throws Exception {
	
		List<Class> classes = getClassesToRun(arg);
		JUnitCore runner = new JUnitCore();
		Result resultjUnit = runner.run(classes.toArray(new Class[classes.size()]));

		return resultjUnit;
	}

	private List<Class> getClassesToRun(String[] arg) throws ClassNotFoundException {
		TestFilter tf = new TestFilter();
		List<Class> classes = new ArrayList<Class>();
		for (int i = 0; i < arg.length; i++) {
			String classString = arg[i];
			Class c = Class.forName(classString);
			if (tf.acceptClass(c)) {
				classes.add(c);
			}

		}
		return classes;
	}

}
