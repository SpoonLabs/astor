package fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.VarAccessWrapper;
import fr.inria.astor.core.manipulation.sourcecode.VarMapping;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.RandomManager;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtVariable;

/**
 * Transformation strategy that uses var clustering
 * 
 * @author Matias Martinez
 *
 */
public class ClusterIngredientTransformation extends CacheTransformationStrategy
		implements IngredientTransformationStrategy {

	protected Logger log = Logger.getLogger(this.getClass().getName());

	@Override
	public List<Ingredient> transform(ModificationPoint modificationPoint, Ingredient ingredient) {

		if (this.alreadyTransformed(modificationPoint, ingredient)) {
			return getCachedTransformations(modificationPoint, ingredient);
		}

		List<Ingredient> transformedIngredientResult = new ArrayList<>();

		CtCodeElement ctIngredient = (CtCodeElement) ingredient.getCode();

		if (modificationPoint.getContextOfModificationPoint().isEmpty()) {
			log.debug("The modification point  has not any var in scope");
		}
		// I wrote all branches even they are not necessaries to easily
		// observe all cases.
		VarMapping mapping = VariableResolver
				.mapVariablesUsingCluster(modificationPoint.getContextOfModificationPoint(), ctIngredient);
		// if we map all variables
		if (mapping.getNotMappedVariables().isEmpty()) {
			if (mapping.getMappedVariables().isEmpty()) {
				// nothing to transform, accept the ingredient
				log.debug("Any transf sucessful: The var Mapping is empty, we keep the ingredient");
				transformedIngredientResult.add(new Ingredient(ctIngredient));

			} else {// We have mappings between variables
				log.debug("Ingredient before transformation: " + ingredient);
				List<Map<String, CtVariable>> allCombinations = VariableResolver
						.findAllVarMappingCombination(mapping.getMappedVariables());

				if (allCombinations.size() > 0) {
					// TODO: Here we take one.
					Map<String, CtVariable> selectedTransformation = getOneCombination(allCombinations);
					log.debug("Transformation proposed: " + selectedTransformation);
					// The ingredient is cloned, so we can modify
					// its variables
					Map<VarAccessWrapper, CtVariableAccess> originalMap = VariableResolver.convertIngredient(mapping,
							selectedTransformation);
					log.debug("Ingredient after transformation: " + ingredient);
					// Cloned transformed element
					CtCodeElement codeElementCloned = MutationSupporter.clone(ctIngredient);
					Ingredient ingredient4Combination = new Ingredient(codeElementCloned, null);
					transformedIngredientResult.add(ingredient4Combination);

					VariableResolver.resetIngredient(originalMap);

				}
			}
		} else {
			log.debug("Any transformation was sucessful: Vars not mapped: " + mapping.getNotMappedVariables());
		}
		this.storingIngredients(modificationPoint, ingredient, transformedIngredientResult);
		return transformedIngredientResult;
	}

	/**
	 * Return a combination of variables.
	 * 
	 * @param allCombinations
	 * @return
	 */
	public Map<String, CtVariable> getOneCombination(List<Map<String, CtVariable>> allCombinations) {

		if (ConfigurationProperties.getPropertyBool("randomcombination")) {
			int value = RandomManager.nextInt(allCombinations.size());
			return allCombinations.get(value);
		} else {
			return allCombinations.get(0);
		}
	}
}
