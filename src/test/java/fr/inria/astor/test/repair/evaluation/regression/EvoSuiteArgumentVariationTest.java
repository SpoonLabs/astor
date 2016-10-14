package fr.inria.astor.test.repair.evaluation.regression;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.ProgramVariantValidationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.validation.validators.EvoSuiteValidationResult;
import fr.inria.astor.test.repair.evaluation.BaseEvolutionaryTest;
import fr.inria.main.evolution.AstorMain;
/**
 * 
 * @author Matias Martinez
 *
 */
public class EvoSuiteArgumentVariationTest  extends BaseEvolutionaryTest{


	@SuppressWarnings("rawtypes")
	//@Test
	public void testMath70WithTimeBudget() throws Exception {
		AstorMain main1 = new AstorMain();

		// Running Astor
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "package", "-seed", "10",
				"-maxgen", "250", "-population", "1", "-stopfirst", "true", "-maxtime", "100",
				"-validation", "evosuite",
				//Argument to test
			
		};
		
		System.out.println(Arrays.toString(args));

		main1.execute(args);

		assertEquals(1, main1.getEngine().getSolutions().size());


		ProgramVariant variantSolution = main1.getEngine().getSolutions().get(0);
		ProgramVariantValidationResult validationResult = variantSolution.getValidationResult();
		
		assertNotNull("Without validation",validationResult);
		//As we execute jgp in evosuite validation mode, we expect eSvalidationResult
		assertTrue(validationResult instanceof EvoSuiteValidationResult);
		EvoSuiteValidationResult esvalidationresult = (EvoSuiteValidationResult) validationResult;
		//The main validation must be true (due it is a solution)
		assertTrue(esvalidationresult.wasSuccessful());
		//Now, the extended validation must fail
		assertFalse(esvalidationresult.getEvoValidation().wasSuccessful());
		
		assertTrue(esvalidationresult.getEvoValidation().getFailureCount() > 0);
		
		assertTrue(esvalidationresult.getEvoValidation().getCasesExecuted() > 0);
		///
		///Now, we trigger the second execution we different time budget for ES.
		///////
		
		String[] argsExtendedBudget = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "package", "-seed", "10",
				"-maxgen", "250", "-population", "1", "-stopfirst", "true", "-maxtime", "100",
				"-validation", "evosuite",
				//Argument to test
				"-evosuitetimeout","120"//"300"

		};
		System.out.println(Arrays.toString(argsExtendedBudget));

		main1.execute(argsExtendedBudget);

		assertEquals(1, main1.getEngine().getSolutions().size());


		ProgramVariant variantSolution2 = main1.getEngine().getSolutions().get(0);
		ProgramVariantValidationResult validationResult2 = variantSolution2.getValidationResult();
		
		assertNotNull("Without validation",validationResult2);
		//As we execute jgp in evosuite validation mode, we expect eSvalidationResult
		assertTrue(validationResult2 instanceof EvoSuiteValidationResult);
		EvoSuiteValidationResult esvalidationresultExtendedBudget = (EvoSuiteValidationResult) validationResult2;
		//The main validation must be true (due it is a solution)
		assertTrue(esvalidationresultExtendedBudget.wasSuccessful());
		//Now, the extended validation must fail
		assertFalse(esvalidationresultExtendedBudget.getEvoValidation().wasSuccessful());
		
		assertTrue(esvalidationresultExtendedBudget.getEvoValidation().getFailureCount() > 0);
		
		assertTrue(esvalidationresultExtendedBudget.getEvoValidation().getCasesExecuted() > 0);
		///
		///Now comparison of cases executed, it must be greater for the extended budget version
		
		log.info("Execution times: "+esvalidationresultExtendedBudget.getEvoValidation().getCasesExecuted() + " "+  
				esvalidationresult.getEvoValidation().getCasesExecuted());
		
		//assertTrue(esvalidationresultExtendedBudget.getEvoValidation().getCasesExecuted() !=  
		//esvalidationresult.getEvoValidation().getCasesExecuted() );
		
	}
	
	
	/**
	 * This test assert Astor when it runs ES over the patched version.
	 * @throws Exception
	 */
	@SuppressWarnings("rawtypes")
	@Test
	public void testMath70WithEvosuiteTestsPostValid() throws Exception {
		AstorMain main1 = new AstorMain();

		
		// Running Astor
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "package", "-seed", "10",
				"-maxgen", "250", "-population", "1", "-stopfirst", "true", "-maxtime", "100",
				"-validation", "evosuite",
				//parameter to test
				"-esoverpatched",
				

		};
		System.out.println(Arrays.toString(args));

		main1.execute(args);

		assertEquals(1, main1.getEngine().getSolutions().size());


		ProgramVariant variantSolution = main1.getEngine().getSolutions().get(0);
		ProgramVariantValidationResult validationResult = variantSolution.getValidationResult();
		
		assertNotNull("Without validation",validationResult);
		//As we execute jgp in evosuite validation mode, we expect eSvalidationResult
		assertTrue(validationResult instanceof EvoSuiteValidationResult);
		EvoSuiteValidationResult esvalidationresult = (EvoSuiteValidationResult) validationResult;
		//The main validation must be true (due it is a solution)
		assertTrue(esvalidationresult.wasSuccessful());
		//Now, the extended validation must fail
		assertTrue(esvalidationresult.getEvoValidation().wasSuccessful());
		
		assertTrue(esvalidationresult.getEvoValidation().getCasesExecuted() > 0);
		
		assertEquals(0,esvalidationresult.getEvoValidation().getFailureCount());
		
	}

}
