package fr.inria.astor.core.loop;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.core.entities.ModificationInstance;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;

/**
 * Exhaustive Search Engine
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public abstract class ExhaustiveSearchEngine extends JGenProg {

	
	public ExhaustiveSearchEngine(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);
	}

	@Override
	public void startEvolution() throws Exception {
		
		dateInitEvolution = new Date();
		// We don't evolve variants, so the generation is always one.
		generationsExecuted = 1;
		// For each variant (one is enough)
		int maxMinutes = ConfigurationProperties.getPropertyInt("maxtime");
		
		for (ProgramVariant parentVariant : variants) {
		
			// We analyze each Gen of the variant i.e. suspicious statement
			for (ModificationPoint gen : parentVariant.getModificationPoints()) {
				// We create all operators to apply in the gen
				List<ModificationInstance> genOperations = createOperators((SuspiciousModificationPoint) gen);

				if(genOperations == null || genOperations.isEmpty())
					continue;
				
				for (ModificationInstance pointOperation : genOperations) {

					try{
						log.info("mod_point "+((SuspiciousModificationPoint)gen).getSuspicious());
						log.info("--> " + pointOperation);
					}catch(Exception e){}
																		
					// We validate the variant after applying the operator
					ProgramVariant solutionVariant = variantFactory.createProgramVariantFromAnother(parentVariant,
							generationsExecuted);
					solutionVariant.getOperations().put(generationsExecuted, Arrays.asList(pointOperation));

					applyNewMutationOperationToSpoonElement(pointOperation);
					
					boolean solution = processCreatedVariant(solutionVariant, generationsExecuted);

					if (solution) {
						this.solutions.add(solutionVariant);
						if(ConfigurationProperties.getPropertyBool("stopfirst"))
							return;
					}

					// We undo the operator (for try the next one)
					undoOperationToSpoonElement(pointOperation);
					
					if(!belowMaxTime(dateInitEvolution, maxMinutes)){
						log.debug("Max time reached");
						return;
					}
				}
			}
		}
		
	}

	protected abstract List<ModificationInstance> createOperators(SuspiciousModificationPoint gen) ;

}
