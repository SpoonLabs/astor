package fr.inria.astor.approaches.deepRepepair;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.ingredientbased.IngredientBasedRepairApproach;
import fr.inria.astor.approaches.jgenprog.jGenProgSpace;
import fr.inria.astor.core.loop.population.ProgramVariantFactory;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.CloneIngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.CtLocationIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtClassIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtGlobalIngredientScope;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtPackageIngredientScope;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.ClusterIngredientTransformation;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.IngredientTransformationStrategy;
import fr.inria.astor.core.loop.spaces.operators.OperatorSpace;
import fr.inria.astor.core.loop.spaces.operators.UniformRandomRepairOperatorSpace;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.manipulation.filters.SingleStatementFixSpaceProcessor;
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
public class DeepRepairEngine extends IngredientBasedRepairApproach {

	public DeepRepairEngine(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws Exception {
		super(mutatorExecutor, projFacade);
		this.pluginLoaded = new DeepLearningPlugInLoader();
		
		ConfigurationProperties.setProperty("transformingredient", Boolean.TRUE.toString());
		ConfigurationProperties.properties.setProperty(ExtensionPoints.INGREDIENT_PROCESSOR.identifier, "statements");
		ConfigurationProperties.properties.setProperty(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY.identifier, "name-cluster-based");
		ConfigurationProperties.properties.setProperty(ExtensionPoints.INGREDIENT_SEARCH_STRATEGY.identifier, "code-similarity-based");
		
		
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
