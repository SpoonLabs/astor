package fr.inria.astor.core.loop;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.IngredientBasedRepairApproach;
import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;

/**
 * Exhaustive Search Engine
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class ExhaustiveSearchEngine extends IngredientBasedRepairApproach {

	public ExhaustiveSearchEngine(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade)
			throws JSAPException {
		super(mutatorExecutor, projFacade);
	}

	@Override
	public void startEvolution() throws Exception {

		dateInitEvolution = new Date();
		// We don't evolve variants, so the generation is always one.
		generationsExecuted = 1;
		// For each variant (one is enough)
		int maxMinutes = ConfigurationProperties.getPropertyInt("maxtime");
		
		int v = 0;
		for (ProgramVariant parentVariant : variants) {

			log.debug("\n****\nanalyzing variant #"+ (++v)+" out of "+variants.size());
			// We analyze each modifpoint of the variant i.e. suspicious statement
			for (ModificationPoint modifPoint : parentVariant.getModificationPoints()) {
				// We create all operators to apply in the modifpoint
				List<OperatorInstance> operatorInstances = createInstancesOfOperators((SuspiciousModificationPoint) modifPoint);

				if (operatorInstances == null || operatorInstances.isEmpty())
					continue;

				for (OperatorInstance pointOperation : operatorInstances) {

					try {
						log.info("mod_point " + modifPoint);
						log.info("-->op: " + pointOperation);
					} catch (Exception e) {
					}

					// We validate the variant after applying the operator
					ProgramVariant solutionVariant = variantFactory.createProgramVariantFromAnother(parentVariant,
							generationsExecuted);
					solutionVariant.getOperations().put(generationsExecuted, Arrays.asList(pointOperation));

					applyNewMutationOperationToSpoonElement(pointOperation);

					boolean solution = processCreatedVariant(solutionVariant, generationsExecuted);

					if (solution) {
						this.solutions.add(solutionVariant);
						if (ConfigurationProperties.getPropertyBool("stopfirst"))
							return;
					}

					// We undo the operator (for try the next one)
					undoOperationToSpoonElement(pointOperation);

					if (!belowMaxTime(dateInitEvolution, maxMinutes)) {
						log.debug("Max time reached");
						return;
					}
				}
			}
		}

	}

	/**
	 * @param modificationPoint
	 * @return
	 */
	protected List<OperatorInstance> createInstancesOfOperators(SuspiciousModificationPoint modificationPoint) {
		List<OperatorInstance> ops = new ArrayList<>();
		AstorOperator[] operators = getOperatorSpace().values();
		for (AstorOperator astorOperator : operators) {
			if (astorOperator.canBeAppliedToPoint(modificationPoint)) {
				List<OperatorInstance> instances = astorOperator.createOperatorInstance(modificationPoint);
				if (instances != null && instances.size() > 0) {
					ops.addAll(instances);
				}
			}
		}

		return ops;

	}
	
	

}
