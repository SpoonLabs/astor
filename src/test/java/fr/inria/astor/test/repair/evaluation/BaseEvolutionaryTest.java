package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import spoon.support.StandardEnvironment;
import fr.inria.main.AbstractMain;
/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public abstract class BaseEvolutionaryTest  {

	public Logger log = Logger.getLogger(Thread.currentThread().getName());
	
	protected AbstractMain main;
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Before
	public void setUp() throws Exception {

	
	//	createFileLogger("c:/tmp/outs/log.txt");
		
		main = createMain();
		
		main.createFactory();
		
		Logger.getLogger(StandardEnvironment.class).setLevel(Level.ERROR);
		
	}

	public abstract AbstractMain createMain();
	

	public void createFileLogger(String file) throws IOException {
		//----
		ConsoleAppender console = new ConsoleAppender();
		String PATTERN = "%m%n";
		console.setLayout(new PatternLayout(PATTERN));
		console.setThreshold(Level.INFO);
		console.activateOptions();
		Logger.getRootLogger().getLoggerRepository().resetConfiguration();
		Logger.getRootLogger().removeAllAppenders();
		Logger.getRootLogger().addAppender(console);
		//----		
		org.apache.log4j.RollingFileAppender rfa = 
				new RollingFileAppender(new org.apache.log4j.PatternLayout(),file);
		Logger.getRootLogger().addAppender(rfa);
		
	
		
	}
	
	public Logger createCustomFileLogger(String file) throws IOException {
		//----
		ConsoleAppender console = new ConsoleAppender();
		String PATTERN = "%m%n-%c: ";
		console.setLayout(new PatternLayout(PATTERN));
		console.setThreshold(Level.INFO);
		console.activateOptions();
		Logger log = Logger.getLogger(Thread.currentThread().getName());
		
		 log.getLoggerRepository().resetConfiguration();
		 log.removeAllAppenders();
		 log.addAppender(console);
		//----		
		org.apache.log4j.RollingFileAppender rfa = 
				new RollingFileAppender(new org.apache.log4j.PatternLayout(PATTERN),file);
		 log.addAppender(rfa);
		 rfa.setImmediateFlush(true);
		
		return log;
		
	}

	public static void validatePatchExistence(String dir) {
		File out = new File(dir+File.separator+"src");
		assertTrue(out.listFiles().length > 1);
		boolean isSol = false;
		for (File sol : out.listFiles()) {
			 isSol|= sol.getName().startsWith("variant-");
		}
		assertTrue(isSol);
	}
	
	public static void validatePatchExistence(String dir, int numberSolution) {
		File out = new File(dir+File.separator+"src");
		assertTrue(out.listFiles().length > 1);
		int cantSol = 0;
		for (File sol : out.listFiles()) {
			cantSol += (sol.getName().startsWith("variant-"))?1:0;
		}
		Assert.assertEquals(numberSolution,cantSol);
	}
	
	public abstract void generic(
			String location,
			String folder,
			String regression,
			String failing, 
			String dependenciespath,
			String packageToInstrument, 
			double thfl) throws Exception;
	
	protected AbstractMain getMain(){
		return this.main;
	}
	
	
}
