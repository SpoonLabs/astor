package fr.inria.astor.test.repair.evaluation;

import java.io.IOException;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.junit.After;
import org.junit.Before;

import fr.inria.astor.core.stats.Stats;
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

	
		createFileLogger("c:/tmp/outs/log.txt");
		
		main = createMain();
		
		main.createFactory();
		
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

	
	public abstract void generic(
			String location,
			String folder,
			String regression,
			String failing, 
			String dependenciespath,
			String packageToInstrument, 
			double thfl, 
			Stats currentStat) throws Exception;
	
	protected AbstractMain getMain(){
		return this.main;
	}
	
	
}
