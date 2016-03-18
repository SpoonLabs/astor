package fr.inria.astor.core.loop.evolutionary;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.ModificationPoint;
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
	
		for (ProgramVariant parentVariant : variants) {
		
			// We analyze each Gen of the variant i.e. suspicious statement
			for (ModificationPoint gen : parentVariant.getModificationPoints()) {
				// We create all operators to apply in the gen
				List<ModificationInstance> genOperations = createOperators((SuspiciousModificationPoint) gen);

				if(genOperations == null || genOperations.isEmpty())
					continue;
				
				for (ModificationInstance genOperation : genOperations) {

					try{
						log.info("gen "+((SuspiciousModificationPoint)gen).getSuspicious());
						log.info("--> " + genOperation);
					}catch(Exception e){}
																		
					// We validate the variant after applying the operator
					ProgramVariant solutionVariant = variantFactory.createProgramVariantFromAnother(parentVariant,
							generationsExecuted);
					solutionVariant.getOperations().put(generationsExecuted, Arrays.asList(genOperation));

					applyNewMutationOperationToSpoonElement(genOperation);
					
					boolean solution = processCreatedVariant(solutionVariant, generationsExecuted);

					if (solution) {
						this.solutions.add(solutionVariant);
						if(ConfigurationProperties.getPropertyBool("stopfirst"))
							return;
					}

					// We undo the operator (for try the next one)
					undoOperationToSpoonElement(genOperation);
				}
			}
		}
		
	}

	protected abstract List<ModificationInstance> createOperators(SuspiciousModificationPoint gen) ;

}
