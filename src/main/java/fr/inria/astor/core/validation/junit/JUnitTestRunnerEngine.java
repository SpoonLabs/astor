package fr.inria.astor.core.validation.junit;

import java.io.File;
import java.io.FileNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

import junit.framework.Test;
import junit.textui.TestRunner;

import org.junit.runner.JUnitCore;
import org.junit.runner.Result;
import org.junit.runners.model.InitializationError;
/**
 * Test Runner for JUnit4
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class JUnitTestRunnerEngine {
	
	
	/**
	 * Execute the test without save the results in file. The result are returned.
	 * @param classpathToTest
	 * @param srcToTest
	 * @param classToTest
	 * @return
	 * @throws InitializationError
	 * @throws FileNotFoundException
	 */
	public Result runSimple(String classpathToTest, String srcToTest, String classToTest)
			throws InitializationError, FileNotFoundException {

		
		String systemcp = System.getProperty("java.class.path");
		System.out.println(System.getProperty("java.class.path"));

		Class cReflect = null;
		cReflect = loadURL(classToTest, classpathToTest);

		JUnitCore runner = new JUnitCore();

		Result r = runner.run(cReflect);
		return r;

	}
	
	public Result runSimple(ClassLoader cl, String classToTest)
			throws InitializationError, FileNotFoundException, ClassNotFoundException {

	
		Class cReflect = null;
		cReflect =  Class.forName(classToTest);
			
		JUnitCore runner = new JUnitCore();

		Result r = runner.run(cReflect);
		return r;

	}
	
	public Result runSimple(String classToTest)
			throws InitializationError, FileNotFoundException, ClassNotFoundException {
	
		Class cReflect = null;
		cReflect =  Thread.currentThread().getContextClassLoader().loadClass(classToTest);
			
		JUnitCore runner = new JUnitCore();
		Result r = runner.run(cReflect);
		Result r2 = new Result();
		
		//Result r = new Result();
		return r;

	}

	public void runJunitSIR(String classToTest){
		TestRunner tr = new TestRunner();
		Class cReflect = null;
		
		try {
			cReflect =  Thread.currentThread().getContextClassLoader().loadClass(classToTest);
			
			tr.doRun((Test) cReflect.newInstance());
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	
}
