package fr.inria.astor.test.repair.evaluation.regression;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.TestCaseVariantValidationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.validation.validators.EvoSuiteValidationResult;
import fr.inria.astor.core.validation.validators.TestCasesProgramValidationResult;
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
		TestCaseVariantValidationResult validationResult = (TestCaseVariantValidationResult) variantSolution.getValidationResult();
		
		assertNotNull("Without validation",validationResult);
		//As we execute jgp in evosuite validation mode, we expect eSvalidationResult
		assertTrue(validationResult instanceof EvoSuiteValidationResult);
		EvoSuiteValidationResult esvalidationresult = (EvoSuiteValidationResult) validationResult;
		//The main validation must be true (due it is a solution)
		assertTrue(esvalidationresult.isSuccessful());
		//Now, the extended validation must fail
		assertFalse(esvalidationresult.getEvoValidation().isSuccessful());
		
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
		TestCaseVariantValidationResult validationResult2 = (TestCaseVariantValidationResult) variantSolution2.getValidationResult();
		
		assertNotNull("Without validation",validationResult2);
		//As we execute jgp in evosuite validation mode, we expect eSvalidationResult
		assertTrue(validationResult2 instanceof EvoSuiteValidationResult);
		EvoSuiteValidationResult esvalidationresultExtendedBudget = (EvoSuiteValidationResult) validationResult2;
		//The main validation must be true (due it is a solution)
		assertTrue(esvalidationresultExtendedBudget.isSuccessful());
		//Now, the extended validation must fail
		assertFalse(esvalidationresultExtendedBudget.getEvoValidation().isSuccessful());
		
		assertTrue(esvalidationresultExtendedBudget.getEvoValidation().getFailureCount() > 0);
		
		assertTrue(esvalidationresultExtendedBudget.getEvoValidation().getCasesExecuted() > 0);
		///
		///Now comparison of cases executed, it must be greater for the extended budget version
		
		log.info("Execution times: "+esvalidationresultExtendedBudget.getEvoValidation().getCasesExecuted() + " "+  
				esvalidationresult.getEvoValidation().getCasesExecuted());
		
		//assertTrue(esvalidationresultExtendedBudget.getEvoValidation().getCasesExecuted() !=  
		//esvalidationresult.getEvoValidation().getCasesExecuted() );
		
	}
	// deterministic when deactivating mocking and private reflection (which are currently on by default in the main branch)
	//
	//P_REFLECTION_ON_PRIVATE, REFLECTION_START_PERCENT, P_FUNCTIONAL_MOCKING and FUNCTIONAL_MOCKING_PERCENT, I set them all to 0. According to the previous experiment log when we use 
	//the maxtime mode, the maximal number of statements searched by Evosuite for the 42 buggy versions is 592352, so currently I use 600000 for MAX_STATEMENTS. Of course, we may need to 
	//give a bigger value for MAX_STATEMENTS.
	@Test
	public void testM70withthread() throws Exception{
		
		String[] args = new String[] { "-dependencies", new File("./examples/libs/junit-4.11.jar").getAbsolutePath()
				, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				 (new File("./examples/math_70")).getAbsolutePath()
					, "-scope", "local", "-seed", "10",
				"-maxgen", "250", "-population", "1", "-stopfirst", "true", "-maxtime", "100",
				"-validation", "evosuite",
				//Argument to test
				 " -validation","fr.inria.astor.core.validation.validators.RegressionValidation,"
				// ,"-ESParameters", "-Dclient_on_thread:false:-Dglobal_timeout:240:-seed:64:-Drandom_seed:64:-Dsearch_budget:220:-Dstopping_condition:MAXGENERATIONS:-Dsandbox:false:-Dno_runtime_dependency:true:-mem:3000"
				 ,"-ESParameters",
				 	"-Dglobal_timeout:240:"+ 
				 	//		"-seed:64:"+
				// "-Drandom_seed:64:" +//
				 		 "-Dsandbox:false:"//
				 		+ "-Dno_runtime_dependency:true:"//
				 		+ "-mem:3000:"//
				 		//From gordon
				 		+ "-Dstopping_condition:MaxStatements:"
				 		+ "-Dsearch_budget:10000000:"
				 	//	+ "-Dsearch_budget:2:"
				 		
				 		+ "-Dp_functional_mocking:0:"
				 		+ "-Dp_reflection_on_private:0:"
				 		//
				 		+"-Dreflection_start_percent:0:"
				 		+"-Dfunctional_mocking_percent:0:"
				 		+ "-generateSuite:"
				 		+ ""
				 		//
		};
		AstorMain main1 = new AstorMain();
		main1.execute(args);
		
		ProgramVariant variantSolution = main1.getEngine().getSolutions().get(0);
		TestCaseVariantValidationResult validationResult = (TestCaseVariantValidationResult) variantSolution.getValidationResult();
		
		assertNotNull("Without validation",validationResult);
		//As we execute jgp in evosuite validation mode, we expect eSvalidationResult
		assertTrue(validationResult instanceof EvoSuiteValidationResult);
		EvoSuiteValidationResult esvalidationresult = (EvoSuiteValidationResult) validationResult;
		//The main validation must be true (due it is a solution)
		assertTrue(esvalidationresult.isSuccessful());
		//Now, the extended validation must fail
		
		TestCasesProgramValidationResult evoValidation = (TestCasesProgramValidationResult) esvalidationresult.getEvoValidation();
		assertFalse(evoValidation.isSuccessful());
		
		assertTrue(evoValidation.getCasesExecuted() > 0);
		
		assertEquals(1,evoValidation.getFailureCount());
		
		//assertEquals("test07(org.apache.commons.math.analysis.solvers.BisectionSolver_ESTest): Expecting exception: NullPointerException-]",//
		//		evoValidation.getTestResult().getFailures());
		//assertTrue("Another test case name failing",evoValidation.getTestResult().getFailures().contains("test07(org.apache.commons.math.analysis.solvers.BisectionSolver_ESTest): Expecting exception: NullPointerException-"));
		
	}
	
	@Test
	public void testM70withthread2() throws Exception{
		
		String[] args = new String[] { "-dependencies", new File("./examples/libs/junit-4.11.jar").getAbsolutePath()
				, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				 (new File("./examples/math_70")).getAbsolutePath()
					, "-scope", "local", "-seed", "10",
				"-maxgen", "250", "-population", "1", "-stopfirst", "true", "-maxtime", "100",
				"-validation", "evosuite",
				//Argument to test
				 " -validation","fr.inria.astor.core.validation.validators.RegressionValidation,"
				// ,"-ESParameters", "-Dclient_on_thread:false:-Dglobal_timeout:240:-seed:64:-Drandom_seed:64:-Dsearch_budget:220:-Dstopping_condition:MAXGENERATIONS:-Dsandbox:false:-Dno_runtime_dependency:true:-mem:3000"
				 ,"-ESParameters",
				 "-Dassertion_timeout=1800:-Dminimization_timeout=1800:-Djunit_check_timeout=1800:-Dwrite_junit_timeout=300:-Dinitialization_timeout=300:-Dglobal_timeout=18000:-Dsearch_budget=100000:-Dstopping_condition=MaxStatements:-Dno_runtime_dependency=true:-Dsandbox=false:-Dp_reflection_on_private=0.0:-Dreflection_start_percent=1.0:-Dp_functional_mocking=0.0:-Dfunctional_mocking_percent=1.0:-mem=2000"
				 		//
		};
		AstorMain main1 = new AstorMain();
		main1.execute(args);
		
		ProgramVariant variantSolution = main1.getEngine().getSolutions().get(0);
		TestCaseVariantValidationResult validationResult = (TestCaseVariantValidationResult) variantSolution.getValidationResult();
		
		assertNotNull("Without validation",validationResult);
		//As we execute jgp in evosuite validation mode, we expect eSvalidationResult
		assertTrue(validationResult instanceof EvoSuiteValidationResult);
		EvoSuiteValidationResult esvalidationresult = (EvoSuiteValidationResult) validationResult;
		//The main validation must be true (due it is a solution)
		assertTrue(esvalidationresult.isSuccessful());
		//Now, the extended validation must fail
		
		TestCasesProgramValidationResult evoValidation = (TestCasesProgramValidationResult) esvalidationresult.getEvoValidation();
		assertFalse(evoValidation.isSuccessful());
		
		assertTrue(evoValidation.getCasesExecuted() > 0);
		
		assertEquals(1,evoValidation.getFailureCount());
		
		//assertEquals("test07(org.apache.commons.math.analysis.solvers.BisectionSolver_ESTest): Expecting exception: NullPointerException-]",//
		//		evoValidation.getTestResult().getFailures());
		//assertTrue("Another test case name failing",evoValidation.getTestResult().getFailures().contains("test07(org.apache.commons.math.analysis.solvers.BisectionSolver_ESTest): Expecting exception: NullPointerException-"));
		
	}
	public void testDirectArg(){
		//String args = "-ESParameters  -Dglobal_timeout:240:-seed:64:-Drandom_seed:64:-Dsearch_budget:220:-Dstopping_condition:MAXGENERATIONS:-Dsandbox:false:-Dno_runtime_dependency:true:-mem:3000 '";
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
				out.getAbsolutePath(), "-scope", "local", "-seed", "10",
				"-maxgen", "250", "-population", "1", "-stopfirst", "true", "-maxtime", "100",
				"-validation", "evosuite",
				//parameter to test
				"-esoverpatched",
				

		};
		System.out.println(Arrays.toString(args));

		main1.execute(args);

		assertEquals(1, main1.getEngine().getSolutions().size());


		ProgramVariant variantSolution = main1.getEngine().getSolutions().get(0);
		TestCaseVariantValidationResult validationResult = (TestCaseVariantValidationResult) variantSolution.getValidationResult();
		
		assertNotNull("Without validation",validationResult);
		//As we execute jgp in evosuite validation mode, we expect eSvalidationResult
		assertTrue(validationResult instanceof EvoSuiteValidationResult);
		EvoSuiteValidationResult esvalidationresult = (EvoSuiteValidationResult) validationResult;
		//The main validation must be true (due it is a solution)
		assertTrue(esvalidationresult.isSuccessful());
		//Now, the extended validation must fail
		assertTrue(esvalidationresult.getEvoValidation().isSuccessful());
		
		assertTrue(esvalidationresult.getEvoValidation().getCasesExecuted() > 0);
		
		assertEquals(0,esvalidationresult.getEvoValidation().getFailureCount());
		
	}

}
