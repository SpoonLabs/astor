package fr.inria.astor.approaches.exhaustive;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.loop.ExhaustiveSearchEngine;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import spoon.reflect.code.CtCodeElement;

/**
 * Exhaustive Search Engine
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class ExhaustiveAstorEngine extends ExhaustiveSearchEngine {

	protected IngredientSpace ingredientSpace = null;

	public ExhaustiveAstorEngine(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade)
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
		int maxGenerations = ConfigurationProperties.getPropertyInt("maxGeneration");
		//for stats
		int modifPointsAnalyzed = 0;
		int operatorExecuted = 0;
		
		int totalmodfpoints = variants.get(0).getModificationPoints().size();
		for (ProgramVariant parentVariant : variants) {

			// We analyze each modifpoint of the variant i.e. suspicious
			// statement
			// TODO: let's be sure that is order by suspicousness
			for (ModificationPoint modifPoint : parentVariant.getModificationPoints()) {
				
				modifPointsAnalyzed ++;
				
				log.debug("location to modify: "+modifPoint);

				// We create all operators to apply in the modifpoint
				List<OperatorInstance> operatorInstances = createInstancesOfOperators(
						(SuspiciousModificationPoint) modifPoint);

				System.out.println("List of operators ("+operatorInstances.size()+") : "+operatorInstances);
				
				if (operatorInstances == null || operatorInstances.isEmpty())
					continue;
				
				
				for (OperatorInstance pointOperation : operatorInstances) {
					
					operatorExecuted++;
					
					// We validate the variant after applying the operator
					ProgramVariant solutionVariant = variantFactory.createProgramVariantFromAnother(parentVariant,
							generationsExecuted);
					solutionVariant.getOperations().put(generationsExecuted, Arrays.asList(pointOperation));

					applyNewMutationOperationToSpoonElement(pointOperation);

					boolean solution = processCreatedVariant(solutionVariant, generationsExecuted);

					if (solution) {
						this.solutions.add(solutionVariant);
						if (ConfigurationProperties.getPropertyBool("stopfirst")){
							log.debug(" modpoint analyzed "+modifPointsAnalyzed + ", operators "+operatorExecuted);
							return;
						}
					}

					// We undo the operator (for try the next one)
					undoOperationToSpoonElement(pointOperation);

					if (!belowMaxTime(dateInitEvolution, maxMinutes)) {
						log.debug("Max time reached");
							return;
					}
					
					if (maxGenerations <= operatorExecuted) {
						log.debug("Max operator Applied "+operatorExecuted);
						log.debug("modpoint:"+modifPointsAnalyzed+ ":all:"+totalmodfpoints + ":operators:"+operatorExecuted);
							return;
					}
				}
			}
		}
		System.out.println("modpoint:"+modifPointsAnalyzed+ ":all:"+totalmodfpoints + ":operators:"+operatorExecuted);
		

	}

	/**
	 * @param modificationPoint
	 * @return
	 */
	protected List<OperatorInstance> createInstancesOfOperators(SuspiciousModificationPoint modificationPoint) {
		
		ingredientSpace.defineSpace(originalVariant);
		
		List<OperatorInstance> ops = new ArrayList<>();
		AstorOperator[] operators = getOperatorSpace().values();
		for (AstorOperator astorOperator : operators) {
			if (astorOperator.canBeAppliedToPoint(modificationPoint)) {

				
				if (astorOperator.needIngredient()) {

					List<CtCodeElement>  ingredients = null;
					if (astorOperator instanceof ReplaceOp) {
						String type = modificationPoint.getCodeElement().getClass().getSimpleName();
						ingredients = ingredientSpace.getIngredients(modificationPoint.getCodeElement(), type);

					} else {
						ingredients = ingredientSpace.getIngredients(modificationPoint.getCodeElement());

					}
					
					log.debug("Number of ingredients " + ingredients.size());
					for (CtCodeElement ingredient : ingredients) {

						List<OperatorInstance> instances = astorOperator.createOperatorInstance(modificationPoint);
						
						if(!VariableResolver.fitInPlace(modificationPoint.getContextOfModificationPoint(),
								ingredient))
							continue;
						
						if (instances != null && instances.size() > 0) {
							
							for (OperatorInstance operatorInstance : instances) {
								
								operatorInstance.setModified(ingredient);
								//operatorInstance.setIngredientScope(ingredient.getScope());
								
								//System.out.println("-->"+operatorInstance);
								ops.add(operatorInstance);
							}
						}
					}
				}else{//if does not need ingredients
					List<OperatorInstance> operatorInstances = astorOperator.createOperatorInstance(modificationPoint);
					ops.addAll(operatorInstances);
					
				}
			}
		}
	/*	int opcount = 0;
		for (AstorOperator astorOperator : operators) {
			System.out.println(++opcount + " -aop->"+astorOperator);
		}opcount = 0;
		for (OperatorInstance operatorInstance : ops) {
			System.out.println(++opcount + " -inop->"+operatorInstance);
				
		}
		*/
		//log.debug("Number modif "+ops.size());
		return ops;

	}
	
	public IngredientSpace getIngredientSpace() {
		return ingredientSpace;
	}

	public void setIngredientSpace(IngredientSpace ingredientSpace) {
		this.ingredientSpace = ingredientSpace;
	}

}
