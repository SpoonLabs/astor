package fr.inria.astor.approaches.cardumen;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.ingredientbased.ExhaustiveIngredientBasedEngine;
import fr.inria.astor.core.ingredientbased.IngredientBasedApproach;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.main.evolution.ExtensionPoints;

/**
 * 
 * @author Matias Martinez
 *
 */
public class CardumenExhaustiveApproach extends ExhaustiveIngredientBasedEngine implements IngredientBasedApproach {

	public CardumenExhaustiveApproach(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade)
			throws JSAPException {
		super(mutatorExecutor, projFacade);
		this.pluginLoaded = new CardumenPlugInloader();
		// Default configuration of Cardumen:
		ConfigurationProperties.setProperty("cleantemplates", "true");

		if (ConfigurationProperties.hasProperty("probabilistictransformation")) {
			if (ConfigurationProperties.getPropertyBool("probabilistictransformation")) {
				ConfigurationProperties.setProperty(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier,
						"name-probability-based");
			} else
				ConfigurationProperties.setProperty(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier,
						"random-variable-replacement");
		}
		ConfigurationProperties.setProperty(ExtensionPoints.INGREDIENT_PROCESSOR.identifier, "expression");
		ConfigurationProperties.setProperty(ExtensionPoints.OPERATORS_SPACE.identifier, "r-expression");
		setPropertyIfNotDefined(ExtensionPoints.INGREDIENT_SEARCH_STRATEGY.identifier, "name-probability-based");

	}
	

}
