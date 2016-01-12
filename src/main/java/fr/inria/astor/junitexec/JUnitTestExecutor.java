package fr.inria.astor.junitexec;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
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

	public static String OUTSEP = "mmout";

	private void runinBackground(List<Class> testToRun) throws InitializationError, FileNotFoundException {

		JUnitCore runner = new JUnitCore();
		Result r = runner.run(testToRun.toArray(new Class[testToRun.size()]));
		String out = "[";
		int count = 0;
		try {
			for (Failure f : r.getFailures()) {
				String s = failureMessage(f);
				//if(s.startsWith("warning"))
				out += s + "-,";
				count++;
				if (count > 10) {
					out += "...and " + (r.getFailureCount() - 10) + " failures more,";
					break;
				}
			}
		} catch (Exception e) {
			//We do not care about this exception, 
		}
		out = out.substring(0, out.length() - 1) + "]";
		// This sysout is necessary for the communication between process...
		System.out.println(OUTSEP + r.getRunCount() + OUTSEP + r.getFailureCount() + OUTSEP + out + OUTSEP);

	}

	private String failureMessage(Failure f) {
		try {
			return f.toString();
		} catch (Exception e) {
			return f.getTestHeader();
		}
	}

	public Result runTest(String testName) throws FileNotFoundException, ClassNotFoundException, InitializationError {

		Result resultTestRun;
		try {

			Class cReflect = null;
			cReflect = Thread.currentThread().getContextClassLoader().loadClass(testName);

			JUnitCore runner = new JUnitCore();
			resultTestRun = runner.run(cReflect);

			return resultTestRun;
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw (e);
		}
	}

	/**
	 * @return the class with the given qualified name.
	 */
	@SuppressWarnings("unchecked")
	public <T> Class<T> loadURL(String qualifiedName, String pathClasspath) {
		try {
			URL url = new File(pathClasspath).toURI().toURL();
			URLClassLoader cl = new URLClassLoader(new URL[] { url });
			Thread.currentThread().setContextClassLoader(cl);
			return (Class<T>) (cl.loadClass(qualifiedName));
		} catch (MalformedURLException e) {
			throw new RuntimeException(e);
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public static void main(String[] arg) throws Exception, InitializationError {

		JUnitTestExecutor re = new JUnitTestExecutor();
		TestFilter tf = new TestFilter();
		List<Class> classes = new ArrayList<Class>();
		for (int i = 0; i < arg.length; i++) {
			String classString = arg[i];
			Class c = Class.forName(classString);
			if (tf.acceptClass(c)) {
				classes.add(c);
			}

		}
		re.runinBackground(classes);
		System.exit(0);
	}

}
