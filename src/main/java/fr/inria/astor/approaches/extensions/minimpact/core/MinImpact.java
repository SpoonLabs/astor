package fr.inria.astor.approaches.extensions.minimpact.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.approaches.extensions.minimpact.validator.EvoSuiteValidationResult;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.validation.VariantValidationResult;
import fr.inria.astor.core.solutionsearch.extension.SolutionVariantSortCriterion;

/**
 * Sorts patches according to the MinImpact Algorithm described in
 * https://arxiv.org/abs/1703.00198
 * 
 * @author Matias Martinez
 *
 */
public class MinImpact implements SolutionVariantSortCriterion {

	protected Logger log = Logger.getLogger(MinImpact.class.getCanonicalName());
	
	@Override
	public List<ProgramVariant> priorize(List<ProgramVariant> patches) {
		
		log.info("MinImpact Sorting patches, \ninput: "+patches);
		List<ProgramVariant> sortedVariants = new ArrayList<>(patches);
		Collections.sort(sortedVariants, new Comparator<ProgramVariant>() {

			@Override
			public int compare(ProgramVariant variant1, ProgramVariant variant2) {

				// We retrive the results of the two variants
				VariantValidationResult validationResult1 = variant1.getValidationResult();
				VariantValidationResult validationResult2 = variant2.getValidationResult();

				// We check if both variants have been validated using evosuite.
				if (validationResult1 instanceof EvoSuiteValidationResult && validationResult2 instanceof EvoSuiteValidationResult) {

					EvoSuiteValidationResult resultEvosuite1 = (EvoSuiteValidationResult) validationResult1;
					EvoSuiteValidationResult resultEvosuite2 = (EvoSuiteValidationResult) validationResult2;

					// Compare nr of failings from the original failing tests
					int compareFailing = Integer.compare(resultEvosuite1.getFailureCount(),
							resultEvosuite2.getFailureCount());
					if (compareFailing != 0)
						// We return, a patch fails less than the other in the
						// set of original failing test cases
						return compareFailing;

					// Now, compare regression (i.e., tests that initially do not fail)
					int compareRegression = Integer.compare(resultEvosuite1.getManualTestValidation().getFailureCount(),
							resultEvosuite2.getManualTestValidation().getFailureCount());
					if (compareRegression != 0)
						return compareRegression;

					// If we arrive here is due to the two patches has the same
					// results in the regression, so we compare the Evosuite
					// execution results
					int compareEvosuiteExecution = Integer.compare(resultEvosuite1.getEvoValidation().getFailureCount(),
							resultEvosuite2.getEvoValidation().getFailureCount());

					if (compareEvosuiteExecution != 0)
						// we return the patch with less minImpact first
						return compareEvosuiteExecution;

					// if all the test execution (initial failing test,
					// regression, evosuite) produce the same results, we order
					// by creation time
					
					int compareCreationTime  = variant1.getBornDate().compareTo(variant2.getBornDate());
					if(compareCreationTime != 0)
						return compareCreationTime;
					
					//Otherwise, we compare the Ids of each variant
					int compareCreationIds = Integer.compare(variant1.getId(), variant2.getId());
					return compareCreationIds;
				}

				return 0;
			}
		});
		log.info("End MinImpact Sorting patches, \noutput: "+sortedVariants);
		return sortedVariants;
	}

}
