package fr.inria.astor.core.validation.validators;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.ProgramVariantValidationResult;
import fr.inria.astor.core.setup.ProjectRepairFacade;
/**
 * 
 * @author Matias Martinez
 *
 */
public class RegressionValidation extends ProcessValidator {

	ProcessEvoSuiteValidator evoValidator = new ProcessEvoSuiteValidator();

	@Override
	public ProgramVariantValidationResult validate(ProgramVariant mutatedVariant, ProjectRepairFacade projectFacade) {

		try {
			ProgramVariantValidationResult failingValidation = super.runFailing(mutatedVariant, projectFacade);
			log.debug("Failing Val: "+failingValidation);
			if (failingValidation != null && failingValidation.wasSuccessful()) {
				
				// Now, the complete regression
				ProgramVariantValidationResult regressionValidation = super.runRegression(mutatedVariant,
						projectFacade);
				log.debug("Manual Regression: "+regressionValidation);

				// Now, Evosuite
				ProgramVariantValidationResult evoSuiteRegressionValidation = evoValidator
						.runTestFromEvoSuite(mutatedVariant, projectFacade);

				log.debug("Evo Regression: "+evoSuiteRegressionValidation);
				EvoSuiteValidationResult evoResult = new EvoSuiteValidationResult(failingValidation.getTestResult());
				evoResult.setEvoValidation(evoSuiteRegressionValidation);
				evoResult.setManualTestValidation(regressionValidation);
				return evoResult;
			} else
				return failingValidation;
		} catch (Exception e) {
			log.error(e.getMessage(),e);
			
		}

		return null;
	}

}
