package fr.inria.astor.core.validation.junit;

import org.junit.runner.*;
import org.junit.runner.notification.Failure;
import org.junit.runners.model.InitializationError;

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
public class JUnitNologExternalExecutor extends JUnitExternalExecutor {

	@Override
	public String createOutput(Result r) {
		String out = "[";
		int nr_failures = 0;
		try {
			for (Failure f : r.getFailures()) {
				String s = failureMessage(f);
				if (!s.startsWith("warning")) {
					nr_failures++;
				}
			}
		} catch (Exception e) {
			// We do not care about this exception,
		}
		out = out + "]";
		return (OUTSEP + r.getRunCount() + OUTSEP + nr_failures + OUTSEP + "" + OUTSEP);
	}

	public static void main(String[] arg) throws Exception {
		JUnitNologExternalExecutor re = new JUnitNologExternalExecutor();
		Result result = re.run(arg);
		// This sysout is necessary for the communication between process...
		// All other System prints are silenced intentionally in "run(String[] args)"
		System.out.println(re.createOutput(result));
		System.exit(0);
	}

}
