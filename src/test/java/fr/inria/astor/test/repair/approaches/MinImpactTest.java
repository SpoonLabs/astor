package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.approaches.extensions.minimpact.core.MinImpact;
import fr.inria.astor.approaches.extensions.minimpact.validator.EvoSuiteValidationResult;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.TestCaseVariantValidationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
@Ignore
public class MinImpactTest {

	@SuppressWarnings("rawtypes")
	@Test
	public void testMinImpactMath70WithEvosuiteTests() throws Exception {
		AstorMain main1 = new AstorMain();

		//
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "package", "-seed", "10", "-maxgen", "400", "-population", "1",
				"-stopfirst", "false", "-maxtime", "5",
				// PARAMETER Related to MinImpact
				"-validation", "evosuite",//We indicate that we will generate and run EvoSuite for validating patches.
				//here, we indicates that we use MinImpact criterion for sorting patches 
				"-parameters", "patchprioritization:"+MinImpact.class.getCanonicalName()

		};
		System.out.println(Arrays.toString(args));

		main1.execute(args);

		//We must have to solutions:
		
		assertEquals(2, main1.getEngine().getSolutions().size());

		//the first ranking according to MinImpact
		ProgramVariant variantSolution1 = main1.getEngine().getSolutions().get(0);
		
		//Let's validate the results of Evosuite for  the first solution
		assertEvosuiteValidation4Solutions(variantSolution1);
		assertEquals(244, variantSolution1.getId());
		
		//Let's validate the results of Evosuite for  the first solution
		ProgramVariant variantSolution2 = main1.getEngine().getSolutions().get(1);
		assertEvosuiteValidation4Solutions(variantSolution2);
		
		assertEquals(186, variantSolution2.getId());
		
		EvoSuiteValidationResult esvalidationresult1 = (EvoSuiteValidationResult) variantSolution1
				.getValidationResult();
		EvoSuiteValidationResult esvalidationresult2 = (EvoSuiteValidationResult) variantSolution2
				.getValidationResult();
		
		//THE FIRST variant  has LESS Failing that the second one
		assertTrue(esvalidationresult1.getEvoValidation().getFailureCount() < esvalidationresult2.getEvoValidation().getFailureCount());
		
		//Initial test cases results must be equals
		assertEquals(esvalidationresult1.getManualTestValidation().getFailureCount(),esvalidationresult2.getManualTestValidation().getFailureCount());
		
		//Now, let's check that the second solution was found before the first one and MINIMPACT has correctly sorted them
		assertTrue(variantSolution1.getId() > variantSolution2.getId());
		assertTrue(variantSolution1.getBornDate().after(variantSolution2.getBornDate()));
		
		
	}

	private void assertEvosuiteValidation4Solutions(ProgramVariant variantSolution) {
		TestCaseVariantValidationResult validationResult = (TestCaseVariantValidationResult) variantSolution
				.getValidationResult();

		assertNotNull("Without validation", validationResult);
		// As we execute jgp in evosuite validation mode, we expect
		// eSvalidationResult
		assertTrue(validationResult instanceof EvoSuiteValidationResult);
		EvoSuiteValidationResult esvalidationresult = (EvoSuiteValidationResult) validationResult;
		// The main validation must be true (due it is a solution)
		assertTrue(esvalidationresult.isSuccessful());
		// Now, the extended validation must fail
		assertFalse(esvalidationresult.getEvoValidation().isSuccessful());

		assertTrue(esvalidationresult.getEvoValidation().getFailureCount() > 0);

		assertTrue(esvalidationresult.getEvoValidation().getCasesExecuted() > 0);
	}
}
