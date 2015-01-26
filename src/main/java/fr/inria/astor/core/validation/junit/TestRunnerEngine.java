package fr.inria.astor.core.validation.junit;

import java.io.FileNotFoundException;
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
	

	public void runTest(String outputFilePath, List<Class> testToRun) throws InitializationError, FileNotFoundException {

		JUnitCore runner = new JUnitCore();
		Result r = runner.run(testToRun.toArray(new Class[testToRun.size()]));
		String out = "[";
		int count = 0;
		for (Failure f : r.getFailures()) {
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

		List<Class> classes = new ArrayList<Class>();
		for (int i = 0; i < arg.length; i++) {
			String classString = arg[i];
			Class c = Class.forName(classString);
			classes.add(c);
		}
		re.runTest(arg[0].toString(), classes);
		
	}

}
