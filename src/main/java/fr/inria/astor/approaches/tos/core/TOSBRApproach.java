package fr.inria.astor.approaches.tos.core;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.ingredientbased.IngredientBasedApproach;
import fr.inria.astor.core.ingredientbased.IngredientBasedRepairApproachImpl;
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
public class TOSBRApproach extends IngredientBasedRepairApproachImpl implements IngredientBasedApproach {

	public TOSBRApproach(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);

		this.pluginLoaded = new TOSBRPlugInLoader();

		if (ConfigurationProperties.hasProperty("probabilistictransformation")) {
			if (ConfigurationProperties.getPropertyBool("probabilistictransformation")) {
				ConfigurationProperties.setProperty(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier,
						"name-probability-based");
			} else
				ConfigurationProperties.setProperty(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier,
						"random-variable-replacement");
		}

	}

}
