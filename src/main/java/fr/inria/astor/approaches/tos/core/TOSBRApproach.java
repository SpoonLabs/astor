package fr.inria.astor.approaches.tos.core;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.ingredientbased.IngredientBasedApproach;
import fr.inria.astor.core.ingredientbased.IngredientBasedEvolutionaryRepairApproachImpl;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.main.evolution.ExtensionPoints;

/**
 * Class that represents the TOS approach
 * 
 * @author Matias Martinez
 *
 */
public class TOSBRApproach extends IngredientBasedEvolutionaryRepairApproachImpl implements IngredientBasedApproach {

	public TOSBRApproach(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);
		if (!ConfigurationProperties.hasProperty(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier)) {
			ConfigurationProperties.setProperty(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier,
					"random-variable-replacement");
		}
	}

	@Override
	protected void loadTargetElements() throws Exception {
		TOSBRPlugInLoader.loadTargetElements(this);
	}

	@Override
	protected void loadIngredientPool() throws JSAPException, Exception {
		TOSBRPlugInLoader.loadIngredientPool(this);
	}

	@Override
	protected void loadIngredientSearchStrategy() throws Exception {

		TOSBRPlugInLoader.loadIngredientSearchStrategy(this);
	}

	@Override
	protected void loadOperatorSpaceDefinition() throws Exception {

		TOSBRPlugInLoader.loadOperatorSpaceDefinition(this);

	}

}
