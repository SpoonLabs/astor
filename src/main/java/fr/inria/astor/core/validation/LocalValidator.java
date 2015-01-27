package fr.inria.astor.core.validation;

import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.ProgramVariantValidationResult;
import fr.inria.astor.core.setup.ProjectRepairFacade;

public class LocalValidator implements IProgramValidator{

	protected Logger log = Logger.getLogger(Thread.currentThread().getName());

	ProgramValidator programVariantValidator = new ProgramValidator();
	
	@Override
	public ProgramVariantValidationResult validate(ProgramVariant mutatedVariant,ProjectRepairFacade projectFacade) {
		try {
			// Load version (variant) in new thread to execute it
			// Get test cases to execute.
			List<String> failingCases = projectFacade.getProperties().getFailingTestCases();
			String testSuiteClassName = projectFacade.getProperties().getTestSuiteClassName();

			ProgramVariantValidationResult result = this.programVariantValidator.validateVariantTwoPhases(failingCases,
					testSuiteClassName);

			// putting fitness into program variant
			//double fitness = populationControler.getFitnessValue(mutatedVariant, result);
			//mutatedVariant.setFitness(fitness);

			// TODO: result has ignore count
			/*log.debug("Fitness of instance #" + mutatedVariant.getId() + ": " + fitness + " (Totals: "
					+ result.getRunCount() + ", failed: " + result.getFailureCount() + ")");
*/
			return result;
		} catch (Exception e) {
			e.printStackTrace();
		}

		return null;
	}

}
