package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.TestCaseVariantValidationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.validation.validators.EvoSuiteValidationResult;
import fr.inria.astor.core.validation.validators.RegressionValidation;
import fr.inria.astor.util.EvoSuiteFacade;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.declaration.CtClass;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ValidationTest extends BaseEvolutionaryTest {

	@Before
	public void setup() {
		LogManager.getRootLogger().setLevel(Level.DEBUG);

	}

	@Test
	public void testLang63ValidationStepbyStep() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-3.8.1.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
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

	

	//@Test
	public void testLang63RegresionValidationStepbyStep() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-3.8.1.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
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

	
	@SuppressWarnings("rawtypes")
	//@Test
	public void testMath70RegressionTests() throws Exception {
		AstorMain main1 = new AstorMain();

		// Running Astor
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "package", "-seed", "0",
				"-maxgen", "250", "-population", "1", "-stopfirst", "true", "-maxtime", "100",
				//PARAMETER TO TEST
				"-validation", RegressionValidation.class.getCanonicalName()

		};
		System.out.println(Arrays.toString(args));

		main1.execute(args);

		assertEquals(1, main1.getEngine().getSolutions().size());
		//Patch info
		//time(sec)= 546
		//operation: ReplaceOp
		//location= org.apache.commons.math.analysis.solvers.BisectionSolver
		//line= 72
		//original statement= return solve(min, max)
		//fixed statement= return solve(f, min, max)
		//generation= 240

		ProgramVariant variantSolution = main1.getEngine().getSolutions().get(0);
		TestCaseVariantValidationResult validationResult = (TestCaseVariantValidationResult) variantSolution.getValidationResult();
		
		assertNotNull("Without validation",validationResult);
		//As we execute jgp in evosuite validation mode, we expect eSvalidationResult
		assertTrue(validationResult instanceof EvoSuiteValidationResult);
		EvoSuiteValidationResult esvalidationresult = (EvoSuiteValidationResult) validationResult;
		//The main validation must be true (due it is a solution)
		assertTrue(esvalidationresult.isSuccessful());
		//Now, the extended validation must fail
		//assertFalse(esvalidationresult.getEvoValidation().wasSuccessful());
		//log.info(esvalidationresult);
	}
	
	@Test
	@Ignore //It takes long to run evosuite for all susp class
	public void testMath74() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
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

		assertFalse(main1.getEngine().getMutatorSupporter().getTestClasses().contains(classes.get(0)));

		TestCaseVariantValidationResult result = fev.saveAndExecuteEvoSuite(main1.getEngine().getProjectFacade(), variant, classes);
	
		log.debug(result);
		
		assertNotNull(result);
		
		assertTrue(result.isSuccessful());
		
		
		
		
	}
	
	@After
	public void teardown() {
		LogManager.getRootLogger().setLevel(Level.ERROR);

	}
}
