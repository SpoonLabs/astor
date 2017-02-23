package fr.inria.astor.test.repair.evaluation.regression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;

import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.TestCaseVariantValidationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.validation.validators.EvoSuiteValidationResult;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class EvoSuiteDSETest {

	
	@Test
	public void testM70_lsdse() throws Exception{
		AstorMain main1 = new AstorMain();
		ConfigurationProperties.properties.setProperty("evosuitetimeout", "240");
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
				//PARAMETER TO TEST
				"-validation", "evosuite",
				"-dse"
		};
	
		//ConfigurationProperties.properties.setProperty("evoDSE", "true");
		
		main1.execute(args);

		assertTrue(ConfigurationProperties.getPropertyBool("evoDSE")); 
		
		
		assertEquals(1, main1.getEngine().getSolutions().size());

		
		ProgramVariant variantSolutionDSE = main1.getEngine().getSolutions().get(0);
		TestCaseVariantValidationResult validationResultDSE = (TestCaseVariantValidationResult) variantSolutionDSE.getValidationResult();
	
		
		assertNotNull("Without validation",validationResultDSE);
		//As we execute jgp in evosuite validation mode, we expect eSvalidationResult
		assertTrue(validationResultDSE instanceof EvoSuiteValidationResult);
		EvoSuiteValidationResult esvalidationresultDSE = (EvoSuiteValidationResult) validationResultDSE;
		//The main validation must be true (due it is a solution)
		assertTrue(esvalidationresultDSE.isSuccessful());
		//Now, the extended validation must fail
		assertFalse(esvalidationresultDSE.getEvoValidation().isSuccessful());
		
		//Results ES-DSE: evo_regression: |false|3|21|[test07(org.apache.commons.math.analysis.solvers.BisectionSolver_ESTest): Expecting exception: NullPointerException-, test18(org.apache.commons.math.analysis.solvers.BisectionSolver_ESTest): endpoints do not specify an interval: 4, 369.837, 4, 369.837-, test06(org.apache.commons.math.analysis.solvers.BisectionSolver_ESTest): Exception was not thrown in org.apache.commons.math.analysis.solvers.BisectionSolver but in org.apache.commons.math.MathRuntimeException.createIllegalArgumentException(MathRuntimeException.java:305): org.evosuite.runtime.mock.java.lang.MockThrowable-]|

		
		//Now, we disactivate evoDSE.
		
	args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "package", "-seed", "10",
				"-maxgen", "250", "-population", "1", "-stopfirst", "true", "-maxtime", "100",
				//PARAMETER TO TEST
				"-validation", "evosuite",
				//"-dse"//we avoid dse
		};
		main1.execute(args);

		ConfigurationProperties.properties.setProperty("evoDSE", "false");
		
		assertEquals(1, main1.getEngine().getSolutions().size());

		assertFalse(ConfigurationProperties.getPropertyBool("evoDSE")); 
		
		//
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
		
		
	//	assertNotEquals(esvalidationresult.getEvoValidation().getFailureCount(),
	//			esvalidationresultDSE.getEvoValidation().getFailureCount());
		
		System.out.println("LS: "+esvalidationresult.getEvoValidation().getFailureCount()
				+", DSE: "+esvalidationresultDSE.getEvoValidation().getFailureCount());
		
		//evo_regression (LS (NoDSE)): |false|5|22|[test01(org.apache.commons.math.analysis.solvers.BisectionSolver_ESTest): endpoints do not specify an interval: 1, 850, 0-, test00(org.apache.commons.math.analysis.solvers.BisectionSolver_ESTest): endpoints do not specify an interval: 1, 850, 0-, test07(org.apache.commons.math.analysis.solvers.BisectionSolver_ESTest): Expecting exception: NullPointerException-, test18(org.apache.commons.math.analysis.solvers.BisectionSolver_ESTest): endpoints do not specify an interval: 4, 369.837, 4, 369.837-, test06(org.apache.commons.math.analysis.solvers.BisectionSolver_ESTest): Expecting exception: Exception-]|

		}
	
}
