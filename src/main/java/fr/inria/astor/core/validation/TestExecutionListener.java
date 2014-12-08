package fr.inria.astor.core.validation;


import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.log4j.Logger;
import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;
/**
 * JUnit Listener that saves the results from the cases execution
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
@Deprecated
public class TestExecutionListener extends RunListener 
{

	 private Writer out;
	 Logger logger = Logger.getLogger(TestExecutionListener.class.getName());
		
	  public TestExecutionListener(OutputStreamWriter out) {
	        try {
	            this.out = new BufferedWriter(out);
	        }
	        catch (Exception e) {
	            throw new RuntimeException("Broken VM does not support UTF-8");
	        }
	    }
	  
	/**
	 * Called before any tests have been run. 
	 * */
	public void testRunStarted(Description description)	throws java.lang.Exception 
	{
		logger.info("Number of testcases to execute : " + description.testCount());
		   try {
	            out.write("<?xml version='1.0' encoding='UTF-8'?>\r\n");
	            out.write("<testsuite>\r\n");
	            out.write("<properties>\r\n");
	            // enumerate properties????
	            out.write("</properties>\r\n");
	        }
	        catch (IOException ex) {
	            throw new RuntimeException(ex);
	        }
		
	}

	/**
	 *  Called when all tests have finished 
	 * */
	public void testRunFinished(Result result) throws java.lang.Exception 
	{
		logger.info("Number of testcases executed : " + result.getRunCount());
		   try {
	         	out.write("</testsuite>\r\n");
	            out.flush();
	            out.close();
	            System.out.println("**fin");
	            
	        }
	        catch (IOException ex) {
	            throw new RuntimeException(ex);
	        }
	}

	/**
	 *  Called when an atomic test is about to be started. 
	 * */
	public void testStarted(Description description) throws java.lang.Exception 
	{
		logger.info("Starting execution of test case : "
				  + description.getClassName()+ "."
				+ description.getMethodName());
		  try {
	            out.write("  <testcase classname='" 
	                    + description.getClassName()
	                    + "' name='" + description.getMethodName()+ "'>");
	        }
	        catch (IOException ex) {
	            throw new RuntimeException(ex);
	        }
	}

	/**
	 *  Called when an atomic test has finished, whether the test succeeds or fails. 
	 * */
	public void testFinished(Description description) throws java.lang.Exception 
	{
		logger.info("Finished execution of test case : "+ description.getMethodName());
		
		   try {
	            out.write("</testcase>\r\n");
	        }
	        catch (IOException ex) {
	            throw new RuntimeException(ex);
	        }     
	}

	/**
	 *  Called when an atomic test fails. 
	 * */
	public void testFailure(Failure failure) throws java.lang.Exception 
	{	
		logger.info("Execution of test case failed : "+ failure.getMessage());
		if("No runnable methods".equals(failure.getMessage())
				|| failure.getDescription().getClassName().startsWith("junit.framework.TestSuite")){
			return;
		}
		try {	String failureString = escape(failure.getMessage());
	            out.write("<failure>");
	            out.write(failureString);
	            out.write("</failure>\r\n");
	        }
	        catch (IOException ex) {
	            throw new RuntimeException(ex);
	        }
		 
	}
	 private String escape(String message) {
		 	if(message == null)
		 		return "null";
	        String result = message.replaceAll("&", "&amp;");
	        result = result.replaceAll("<", "&lt;");
	        result.replaceAll(">", "&gt;");
	        return result;
	    }
	/**
	 *  Called when a test will not be run, generally because a test method is annotated with Ignore. 
	 * */
	public void testIgnored(Description description) throws java.lang.Exception 
	{
		logger.info("Execution of test case ignored : "+ description.getMethodName());
		   try {
	            out.write("<ignored>");
	            out.write(escape(description.getMethodName()));
	            out.write("</ignored>\r\n");
	        }
	        catch (IOException ex) {
	            throw new RuntimeException(ex);
	        }
	}
}