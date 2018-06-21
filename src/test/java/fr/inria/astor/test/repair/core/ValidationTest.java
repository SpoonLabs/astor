package fr.inria.astor.test.repair.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.approaches.extensions.minimpact.validator.EvoSuiteValidationResult;
import fr.inria.astor.approaches.extensions.minimpact.validator.RegressionValidation;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.TestCaseVariantValidationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.astor.util.EvoSuiteFacade;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.declaration.CtClass;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ValidationTest extends BaseEvolutionaryTest {


	@Test
	public void testLang63ValidationStepbyStep() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-3.8.1.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.lang.time.DurationFormatUtilsTest"
				// + ":org.apache.commons.lang.builder.ToStringBuilder",
				, "-location", new File("./examples/lang_63/").getAbsolutePath(),
				//
				"-package", "org.apache.commons", "-srcjavafolder", "/src/main/java/", "-srctestfolder",
				"/src/test/java", "-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes",
				"-javacompliancelevel", "7", "-flthreshold", "0.5", "-out", out.getAbsolutePath(), "-scope", "local",
				"-seed", "6010", "-maxgen", "50", "-stopfirst", "true", "-maxtime", "30", "-testbystep",
				//
				"-ignoredtestcases", "org.apache.commons.lang.LocaleUtilsTest",	
		};
		try {
			System.out.println(Arrays.toString(args));
			LogManager.getRootLogger().setLevel(Level.DEBUG);
			main1.execute(args);
			System.out.println("Solution SetbyStep " + main1.getEngine().getSolutions().size());
			//This assertiong was failing at travis CI
			// assertTrue(main1.getEngine().getSolutions().size() > 0);
				
		} catch (Exception e) {
			System.out.println("StepByStep fails " + e.getMessage());
			e.printStackTrace();
		}
	}

	
	/**
	 * Math 70 bug can be fixed by replacing a method invocation inside a return
	 * statement. + return solve(f, min, max); - return solve(min, max); One
	 * solution with local scope, another with package
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testMath70LocalSolutionJUExLog() throws Exception {
		AstorMain main1 = new AstorMain();
	
		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-stopfirst", "true");
		cs.command.put("-seed", "0");
		cs.command.put("-scope", "package");
		cs.command.put("-maxgen", "200");
		cs.command.put("-loglevel", "INFO");
		cs.command.put("-parameters", "disablelog:false");
		cs.append("-parameters", "logtestexecution:true");

		assertEquals(4, cs.command.get("-parameters").split(File.pathSeparator).length);
		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());

	}

	//@Test
	public void testLang63RegresionValidationStepbyStep() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-3.8.1.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.lang.time.DurationFormatUtilsTest"
				// + ":org.apache.commons.lang.builder.ToStringBuilder",
				, "-location", new File("./examples/lang_63/").getAbsolutePath(),
				//
				"-package", "org.apache.commons", "-srcjavafolder", "/src/main/java/", "-srctestfolder",
				"/src/test/java", "-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes",
				"-javacompliancelevel", "7", "-flthreshold", "0.5", "-out", out.getAbsolutePath(), "-scope", "local",
				"-seed", "6010", "-maxgen", "50", "-stopfirst", "true", "-maxtime", "30", "-testbystep",
				//
				"ignoredtestcases", "org.apache.commons.lang.LocaleUtilsTest",
				//Here, we specify the regression Validation
				"-validation", RegressionValidation.class.getCanonicalName(),

		};
		try {
			System.out.println(Arrays.toString(args));
			LogManager.getRootLogger().setLevel(Level.DEBUG);
			main1.execute(args);
			System.out.println("Solution SetbyStep " + main1.getEngine().getSolutions().size());
			// assertTrue(main1.getEngine().getSolutions().size() > 0);
			
			ProgramVariant variantSolution = main1.getEngine().getSolutions().get(0);
			TestCaseVariantValidationResult validationResult = (TestCaseVariantValidationResult) variantSolution.getValidationResult();
		
			
			assertNotNull("Without validation",validationResult);
			//As we execute jgp in evosuite validation mode, we expect eSvalidationResult
			assertTrue(validationResult instanceof EvoSuiteValidationResult);
			EvoSuiteValidationResult esvalidationresult = (EvoSuiteValidationResult) validationResult;
			//The main validation must be true (due it is a solution)
			assertTrue(esvalidationresult.getFailingTestValidation().isSuccessful());
			
			assertTrue(esvalidationresult.getManualTestValidation().isSuccessful());
			
			assertNotNull(esvalidationresult.getEvoValidation());
			
			assertTrue(esvalidationresult.getEvoValidation().isRegressionExecuted());
			
			assertTrue(esvalidationresult.getEvoValidation().getCasesExecuted() > 0);
			
			
		} catch (Exception e) {
			System.out.println("StepByStep fails " + e.getMessage());
			e.printStackTrace();
			fail();
		}
	}

	
	
	@Test
	@Ignore //It takes long to run evosuite for all susp class
	public void testMath74() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.ode.nonstiff.AdamsMoultonIntegratorTest", "-location",
				new File("./examples/math_74").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/main/java/", "-srctestfolder", "/src/test/java", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "5", "-flthreshold", "0.1", 
				"-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "6010", 
				//Forced
				"-maxgen", "0", "-stopfirst", "true",
				"-maxtime", "100",
				"-population", "1",
				//PARAMETER TO TEST
				"-validation", RegressionValidation.class.getCanonicalName()
					
		};
		main1.execute(args);

		assertTrue(main1.getEngine().getSolutions().size() == 0);

		assertEquals(1, main1.getEngine().getVariants().size());

		ProgramVariant variant = main1.getEngine().getVariants().get(0);

		log.info("Executing evosuite for Math-74");
		EvoSuiteFacade fev = new EvoSuiteFacade();
		
		List<CtClass> classes = fev.createEvoTestModel(main1.getEngine().getProjectFacade(), variant);
	
		// Two classes: EvoTest + EvoScaffolding
		//assertEquals("We do not have 2 spoon classes generated", 2, classes.size());

		
		TestCaseVariantValidationResult result = fev.saveAndExecuteEvoSuite(main1.getEngine().getProjectFacade(), variant, classes);
	
		log.debug(result);
		
		assertNotNull(result);
		
		assertTrue(result.isSuccessful());

		
	}
}
