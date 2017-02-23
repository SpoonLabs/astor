package fr.inria.astor.core.validation.validators;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.TestCaseVariantValidationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;

/**
 * Executes different regression mechanism such as EvoSuite regression.
 * @author Matias Martinez
 *
 */
public class RegressionValidation extends ProcessValidator {

	ProcessEvoSuiteValidator evoValidator = new ProcessEvoSuiteValidator();

	@Override
	public TestCaseVariantValidationResult validate(ProgramVariant mutatedVariant, ProjectRepairFacade projectFacade) {

		try {
			boolean runESoverOriginalBuggyClass = ConfigurationProperties.getPropertyBool("evoRunOnBuggyClass");
			TestCaseVariantValidationResult failingValidation = super.runFailing(mutatedVariant, projectFacade);
			log.debug("Failing Val: " + failingValidation);
			if (failingValidation != null && failingValidation.isSuccessful()) {

				// Now, the complete regression
				TestCaseVariantValidationResult regressionValidation = super.runRegression(mutatedVariant,
						projectFacade);
				log.debug("Manual Regression: " + regressionValidation);

				// Now, Evosuite
				TestCaseVariantValidationResult evoSuiteRegressionValidation = evoValidator
						.runTestFromEvoSuite(mutatedVariant, projectFacade,runESoverOriginalBuggyClass);

				log.debug("Evo Regression: " + evoSuiteRegressionValidation);
				EvoSuiteValidationResult evoResult = new EvoSuiteValidationResult();
				evoResult.setFailingTestValidation(failingValidation);
				evoResult.setEvoValidation(evoSuiteRegressionValidation);
				evoResult.setManualTestValidation(regressionValidation);
				return evoResult;
			} else {
				return failingValidation;
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);

		}
		return null;
	}

}
