package fr.inria.astor.core.validation;

import java.io.FileNotFoundException;
import java.util.Enumeration;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.TestSuite;
import junit.textui.TestRunner;

import org.apache.log4j.Logger;
import org.junit.runner.Result;
import org.junit.runners.model.InitializationError;

/**
 * This class executes a Test Suite. It must be loader in the classloader or referenciated by it.
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class JUnitTestExecutor {
	
	//public Logger logger = Logger.getLogger(JUnitTestExecutor.class.getName());
	public Logger logger = Logger.getLogger(Thread.currentThread().getName());
	
	public Result execute(String testName) throws FileNotFoundException, ClassNotFoundException, InitializationError {
		
		JUnitTestRunnerEngine testrunner = new JUnitTestRunnerEngine();
		Result resultTestRun;
		try {
			resultTestRun = testrunner.runSimple(testName);
			logger.info("Test Result: fails "+resultTestRun.getFailureCount() + ": "+resultTestRun.getFailures());
			
			return resultTestRun;

		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw (e);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			throw (e);
		} catch (InitializationError e) {
			e.printStackTrace();
			throw (e);
		}
	}
	/**
	 * Run a Test Case or a Test Suite
	 * @param classToTest
	 * @throws Exception
	 */
	public void executeJu3(String classToTest) throws Exception{
	//	org.apache.xml.security.test.AllTests t = new org.apache.xml.security.test.AllTests(null);
		TestRunner tr = new TestRunner();
		TestResult res = null;
		
		Class cReflect = null;
		cReflect =  Thread.currentThread().getContextClassLoader().loadClass(classToTest);
	
		java.lang.reflect.Method method;
		if(TestSuite.class.isAssignableFrom(cReflect)){
			TestSuite ts = (TestSuite) cReflect.getConstructor(String.class).newInstance("");;
			res = tr.doRun(ts, false);
		}
		
		if(TestCase.class.isAssignableFrom(cReflect)){
			TestCase tc = (TestCase) cReflect.getConstructor(String.class).newInstance("");;
			res = tr.run(tc);
		}
		
		if(res == null){
			return;
		}
		
		System.out.println("-->successfull: "+res.wasSuccessful());
		System.out.println("-->errors: "+res.errorCount());
		System.out.println("-->fails: "+res.failureCount());
		Enumeration errors = res.errors();
		while(errors.hasMoreElements()){
			Object e = errors.nextElement();
			System.out.println("--"+e);
		}
	}

}
