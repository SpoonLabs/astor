package fr.inria.astor.approaches.deeprepair;

import fr.inria.astor.core.ingredientbased.IngredientBasedRepairApproachImpl;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.main.evolution.ExtensionPoints;
import fr.inria.main.evolution.PlugInLoader;
import spoon.reflect.declaration.CtType;

/**
 * DeepRepair engine.
 * 
 * @author Matias Martinez
 *
 */
public class DeepRepairEngine extends IngredientBasedRepairApproachImpl {

	public DeepRepairEngine(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws Exception {
		super(mutatorExecutor, projFacade);
		this.pluginLoaded = new DeepLearningPlugInLoader();
		ConfigurationProperties.setProperty("transformingredient", Boolean.TRUE.toString());
		setPropertyIfNotDefined(ExtensionPoints.INGREDIENT_PROCESSOR.identifier, "statements");
		setPropertyIfNotDefined(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier, "name-cluster-based");
		setPropertyIfNotDefined(ExtensionPoints.INGREDIENT_SEARCH_STRATEGY.identifier, "code-similarity-based");
		
		
		String learningdir = ConfigurationProperties.getProperty("learningdir");
		if (learningdir == null || learningdir.isEmpty()) {
			log.error("Error, missing argument: -learningdir ");
			throw new IllegalArgumentException("Missing argument -learningdir");
		}
		
		ExtensionPoints cloneGranularityEP = ExtensionPoints.CLONE_GRANULARITY;
		Class cloneGranularity = PlugInLoader.loadClassFromProperty(cloneGranularityEP);
		if (cloneGranularity == null) {
			cloneGranularity = CtType.class;
		}
		ConfigurationProperties.setProperty("clonegranularity", cloneGranularity.getName());

	}


	
}
