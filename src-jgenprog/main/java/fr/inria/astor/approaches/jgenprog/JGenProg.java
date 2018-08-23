package fr.inria.astor.approaches.jgenprog;

import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.ingredientbased.IngredientBasedEvolutionaryRepairApproachImpl;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.setup.RandomManager;
import fr.inria.main.evolution.ExtensionPoints;

/**
 * Core repair approach based on reuse of ingredients.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class JGenProg extends IngredientBasedEvolutionaryRepairApproachImpl {

	public JGenProg(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);
		setPropertyIfNotDefined(ExtensionPoints.OPERATORS_SPACE.identifier, "irr-statements");

		setPropertyIfNotDefined(ExtensionPoints.TARGET_CODE_PROCESSOR.identifier, "statements");
	}

	@Override
	public void prepareNextGeneration(List<ProgramVariant> temporalInstances, int generation) {

		super.prepareNextGeneration(temporalInstances, generation);

		if (ConfigurationProperties.getPropertyBool("applyCrossover")) {
			applyCrossover(generation);
		}
	}

	@Override
	public void loadOperatorSpaceDefinition() throws Exception {

		super.loadOperatorSpaceDefinition();

		if (this.getOperatorSpace() == null) {

			this.setOperatorSpace(new jGenProgSpace());
		}

	}

	private void applyCrossover(int generation) {
		int numberVariants = this.variants.size();
		if (numberVariants <= 1) {
			log.debug("CO|Not Enough variants to apply Crossover");
			return;
		}

		// We randomly choose the two variants to crossover
		ProgramVariant v1 = this.variants.get(RandomManager.nextInt(numberVariants));
		ProgramVariant v2 = this.variants.get(RandomManager.nextInt(numberVariants));
		// Same instance
		if (v1 == v2) {
			log.debug("CO|randomless chosen the same variant to apply crossover");
			return;
		}

		if (v1.getOperations().isEmpty() || v2.getOperations().isEmpty()) {
			log.debug("CO|Not Enough ops to apply Crossover");
			return;
		}
		// we randomly select the generations to apply
		int rgen1index = RandomManager.nextInt(v1.getOperations().keySet().size()) + 1;
		int rgen2index = RandomManager.nextInt(v2.getOperations().keySet().size()) + 1;

		List<OperatorInstance> ops1 = v1.getOperations((int) v1.getOperations().keySet().toArray()[rgen1index]);
		List<OperatorInstance> ops2 = v2.getOperations((int) v2.getOperations().keySet().toArray()[rgen2index]);

		OperatorInstance opinst1 = ops1.remove((int) RandomManager.nextInt(ops1.size()));
		OperatorInstance opinst2 = ops2.remove((int) RandomManager.nextInt(ops2.size()));

		if (opinst1 == null || opinst2 == null) {
			log.debug("CO|We could not retrieve a operator");
			return;
		}

		// The generation of both new operators is the Last one.
		// In the first variant we put the operator taken from the 2 one.
		v1.putModificationInstance(generation, opinst2);
		// In the second variant we put the operator taken from the 1 one.
		v2.putModificationInstance(generation, opinst1);
		//
	}

}
