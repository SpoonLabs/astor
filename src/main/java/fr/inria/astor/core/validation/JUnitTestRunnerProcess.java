package fr.inria.astor.core.validation;

import java.io.FileNotFoundException;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.model.InitializationError;
/**
 * Test Runner for JUnit4
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class JUnitTestRunnerProcess {
	

	
	public Result runSimple(String classToTest)
			throws InitializationError, FileNotFoundException, ClassNotFoundException {
	
		Class cReflect = null;
		cReflect =  Thread.currentThread().getContextClassLoader().loadClass(classToTest);
			
		JUnitCore runner = new JUnitCore();
		Result r = runner.run(cReflect);
		
		
		
		return r;

	}

	public static void main(String[] args){
		JUnitTestRunnerProcess p = new JUnitTestRunnerProcess();
	}
	
}
