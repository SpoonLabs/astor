package fr.inria.astor.core.validation.junit;

import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

import com.google.common.reflect.ClassPath;
import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;

import fr.inria.astor.core.validation.junit.filters.TestFilter;

/**
 * This class runs a JUnit test suite i.e., a set of test cases.
 *
 * This is a separated program / main due to concepts in the repair-approach:
 * The Executor is used upon the mutants/variants which share the naming of their origins.
 * Hence, there is big potential for Classpath-Collisions, and the executor is used as a simple program
 * to run with the same dependencies but different java pieces.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class JUnitExternalExecutor {

	List<String> successTest = new ArrayList<String>();
	List<String> failTest = new ArrayList<String>();

	public final static String OUTSEP = "astoroutdel";

	public String createOutput(Result r) {
		String out = "[";
		int count = 0;
		int failures = 0;
		try {
			for (Failure f : r.getFailures()) {
				String s = failureMessage(f);
				if (!s.startsWith("warning")) {
					failures++;
				}
				out += s + "-,";
				count++;
				if (count > 10) {
					out += "...and " + (r.getFailureCount() - 10) + " failures more,";
					// break;
				}
			}
		} catch (Exception e) {
			// We do not care about this exception,
		}
		out = out + "]";
		return (OUTSEP + r.getRunCount() + OUTSEP + failures + OUTSEP + out + OUTSEP);
	}

	protected String failureMessage(Failure f) {
		try {
			return f.toString();
		} catch (Exception e) {
			return f.getTestHeader();
		}
	}

	public static void main(String[] arg) throws Exception {
		JUnitExternalExecutor re = new JUnitExternalExecutor();

		Result result = re.run(arg);
		// This sysout is necessary for the communication between process...
		// Any other sysout is purposefully silenced (see run(String[] arg))
		System.out.println(re.createOutput(result));

		System.exit(0);
	}

	/*
	This Args should only contain the tests to run within the Classpath.
	Any ClassPath from the JunitLauncher is "consumed" at this point.
	It fails silently if the methods are not found.
	 */
	public Result run(String[] args) throws Exception {
		PrintStream original = System.out;
		// This part sets a new output stream
		// It helps to keep the output "clean" as maybe there are prints or logs in the tests.
		// The results of the tests are needed as "pure" output on the console.
		// Everything until the reset of Sys.out is silenced - hence comment out the lines below if you need to debug.
		System.setOut(new PrintStream(new OutputStream() {
			public void write(int b) {
			}
		}));

		/*
		I think this can be safely kept for information on errors
		System.setErr(new PrintStream(new OutputStream() {
			public void write(int b) {
			}
		}));
		*/
		List<Class> classes = getClassesToRun(args);
		// Exit early if no class was found.
		if (classes.isEmpty()){
			throw new UnsupportedOperationException(
					"None of the given args could be resolved to a class in the classpath. " +
					"Please check validity of classnames and the classpath." +
					"Maybe all of them were filtered out - but that is unlikely." +
					"Given Args were: "
					+ Arrays.stream(args).sequential().collect(Collectors.joining(";","[","]")));
		}

		JUnitCore runner = new JUnitCore();
		Logger.getGlobal().setLevel(Level.OFF);

		Result resultjUnit = runner.run(classes.toArray(new Class[classes.size()]));
		System.setOut(original);

		return resultjUnit;
	}

	/**
	 * This method takes a list of strings and tries to resolve them to classes.
	 * After the "raw" classes are resolved if possible,
	 * they are filtered using a TestFilter.
	 * @param arg the system args which classes to look for in the CP
	 * @return The List of resolved,non-filtered classes if any are found/left
	 */
	protected List<Class> getClassesToRun(String[] arg) {
		TestFilter tf = new TestFilter();
		List<Class> classes = new ArrayList<Class>();

		for (int i = 0; i < arg.length; i++) {
			String classString = arg[i];

			Optional<Class> cMaybe = resolveFullyQualifiedClassname(classString);
			// If the class was not found, just go to next arg
			if (!cMaybe.isPresent()) {
				System.out.println("Couldn't resolve:\t " + classString);
				continue;
			}
			// Else resolve the class to work with as beforehand
			Class c = cMaybe.get();

			if (tf.acceptClass(c)) {
				if (!classes.contains(c)) {
					System.out.println("Did accept:\t" + classString);

					classes.add(c);
				}
			} else {
				System.out.println("Did discard :\t" + classString);
			}
		}
		return classes;
	}

	/**
	 * This method tries to find a fitting fully qualified classname for a given simple class name.
	 * If multiple are found (and pray to god that it doesn't), the first one is returned.
	 * If you are looking at this documentation, there are two likely sources for your error:
	 * 		a) Some items are missing in your classpath
	 * 		b) Some items have the same name in your classpath
	 * @param simpleName A simple class name without package prefix.
	 * @return The resolved class if there is any found. Empty otherwise.
	 */
	private Optional<Class> resolveFullyQualifiedClassname(String simpleName){
		try {
			// Get all elements in the classpath
			ClassPath cp = ClassPath.from(ClassLoader.getSystemClassLoader());
			// Check for every element if it contains the simple element
			for (ClassPath.ClassInfo existingClass : cp.getAllClasses()) {
				if (existingClass.getName().toLowerCase().contains(simpleName.toLowerCase())){
					// If yes, return a filled optional
					// Old way: (Failing)
					//Class c = Class.forName(existingClass.getName());
					Class c = existingClass.load();
					return Optional.of(c);
				}
			}
			// No element found:
			return Optional.empty();
		} catch (Exception e) {
			e.printStackTrace();
			// If an error occurred,
			// return an empty optional
			return Optional.empty();
		}
	}

}
