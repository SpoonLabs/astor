package fr.inria.astor.core.loop.evolutionary;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.Gen;
import fr.inria.astor.core.entities.GenOperationInstance;
import fr.inria.astor.core.entities.GenSuspicious;
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
		final int generation = 1;
		// For each variant (one is enough)
		outerloop:
		for (ProgramVariant parentVariant : variants) {
			// We analyze each Gen of the variant i.e. suspicious statement
			for (Gen gen : parentVariant.getGenList()) {
				// We create all operators to apply in the gen
				List<GenOperationInstance> genOperations = createOperators((GenSuspicious) gen);

				if(genOperations == null || genOperations.isEmpty())
					continue;
				
				for (GenOperationInstance genOperation : genOperations) {

					try{
						log.info("gen "+((GenSuspicious)gen).getSuspicious());
						log.info("--> " + genOperation);
					}catch(Exception e){}
																		
					// We validate the variant after applying the operator
					ProgramVariant solutionVariant = variantFactory.createProgramVariantFromAnother(parentVariant,
							generation);
					solutionVariant.getOperations().put(generation, Arrays.asList(genOperation));

					applyNewMutationOperationToSpoonElement(genOperation);
					
					boolean solution = processCreatedVariant(solutionVariant, generation);

					if (solution) {
						this.solutions.add(solutionVariant);
						if(ConfigurationProperties.getPropertyBool("stopfirst"))
							break;
					}

					// We undo the operator (for try the next one)
					undoOperationToSpoonElement(genOperation);
				}
			}
		}
		showResults(generation);
	}

	protected abstract List<GenOperationInstance> createOperators(GenSuspicious gen) ;

}
