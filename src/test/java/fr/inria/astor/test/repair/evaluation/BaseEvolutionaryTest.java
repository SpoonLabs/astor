package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.apache.log4j.RollingFileAppender;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import spoon.support.StandardEnvironment;
/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public abstract class BaseEvolutionaryTest  {

	public static Logger log = Logger.getLogger(Thread.currentThread().getName());
	
	
	@After
	public void tearDown() throws Exception {
	}
	
	@Before
	public void setUp() throws Exception {

		MutationSupporter.cleanFactory();
		
		Logger.getLogger(StandardEnvironment.class).setLevel(Level.ERROR);
		
	}

	

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
		int cantSol = numberSolutions(dir);
		assertTrue(cantSol > 0);
	}
	/**
	 * This method asserts the number of variants that where store in the disk.
	 * The option "saveall" must be false (with true value it saves solution and not solution variants)
	 * @param dir
	 * @param numberSolution
	 */
	public static void validatePatchExistence(String dir, int numberSolution) {
		int cantSol = numberSolutions(dir);
		Assert.assertEquals(numberSolution,cantSol);
	}

	protected static int numberSolutions(String dir) {
		File out = new File(dir+File.separator+"src");
		log.info("Searching for stored variants at "+out.getParent());
		assertTrue("The directory that store results does not exist: "+out.getAbsolutePath(),out.exists());
		//src folder has a folder with the buggy variant, 
		//and zero or more of solution variants
		//assertTrue(out.listFiles().length > 1);
		log.info("Stored variants: "+Arrays.toString(out.listFiles()));
		int cantSol = 0;
		for (File sol : out.listFiles()) {
			cantSol += (sol.getName().startsWith(ConfigurationProperties.getProperty("pvariantfoldername") ))?1:0;
		}
		return cantSol;
	}
	
	
	protected static boolean comparePatch(ProgramVariant variant,String patch){
		boolean found = false;
		
		for(List<OperatorInstance> modif:  variant.getOperations().values()){
			for (OperatorInstance modificationInstance : modif) {
				if(patch.equals(modificationInstance.getModified())){
					found = true;
				}
			}
		}
		
		return found;
		
	}
	
	
}
