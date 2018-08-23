package fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.manipulation.sourcecode.VarAccessWrapper;
import fr.inria.astor.core.manipulation.sourcecode.VarCombinationForIngredient;
import fr.inria.astor.core.manipulation.sourcecode.VarMapping;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.RandomManager;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtVariable;

/**
 * 
 * @author Matias Martinez
 *
 */
public class RandomTransformationStrategy extends CacheTransformationStrategy
		implements IngredientTransformationStrategy {

	protected Logger logger = Logger.getLogger(RandomTransformationStrategy.class.getName());

	public RandomTransformationStrategy() {
		super();
	}

	@Override
	public List<Ingredient> transform(ModificationPoint modificationPoint, Ingredient baseIngredient) {

		if (this.alreadyTransformed(modificationPoint, baseIngredient)) {
			return getCachedTransformations(modificationPoint, baseIngredient);
		}

		List<Ingredient> result = new ArrayList<>();

		CtCodeElement codeElementToModifyFromBase = (CtCodeElement) baseIngredient.getCode();

		if (modificationPoint.getContextOfModificationPoint().isEmpty()) {
			logger.debug("The modification point  has not any var in scope");
		}

		VarMapping mapping = VariableResolver.mapVariablesFromContext(modificationPoint.getContextOfModificationPoint(),
				codeElementToModifyFromBase);
		// if we map all variables
		if (mapping.getNotMappedVariables().isEmpty()) {
			if (mapping.getMappedVariables().isEmpty()) {
				// nothing to transform, accept the ingredient
				logger.debug("Any transf sucessful: The var Mapping is empty, we keep the ingredient");
				result.add(new Ingredient(codeElementToModifyFromBase));

			} else {// We have mappings between variables
				logger.debug("Ingredient before transformation: " + baseIngredient);

				List<VarCombinationForIngredient> allCombinations = findAllVarMappingCombinationUsingRandom(
						mapping.getMappedVariables(), modificationPoint);

				if (allCombinations.size() > 0) {

					for (VarCombinationForIngredient varCombinationForIngredient : allCombinations) {

						DynamicIngredient ding = new DynamicIngredient(varCombinationForIngredient, mapping,
								codeElementToModifyFromBase);
						result.add(ding);
					}
				}
			}
		} else {
			logger.debug("Any transformation was sucessful: Vars not mapped: " + mapping.getNotMappedVariables());
			String varContext = "";
			for (CtVariable context : modificationPoint.getContextOfModificationPoint()) {
				varContext += context.getSimpleName() + " " + context.getType().getQualifiedName() + ", ";
			}
			logger.debug("context " + varContext);
			for (CtVariableAccess ingredient : mapping.getNotMappedVariables()) {
				logger.debug("---out_of_context: " + ingredient.getVariable().getSimpleName() + ": "
						+ ingredient.getVariable().getType().getQualifiedName());
			}
		}

		this.storingIngredients(modificationPoint, baseIngredient, result);

		return result;
	}

	public List<VarCombinationForIngredient> findAllVarMappingCombinationUsingRandom(
			Map<VarAccessWrapper, List<CtVariable>> mappedVars, ModificationPoint mpoint) {

		List<VarCombinationForIngredient> allCom = new ArrayList<>();

		List<Map<String, CtVariable>> allWithoutOrder = VariableResolver.findAllVarMappingCombination(mappedVars, null);

		for (Map<String, CtVariable> varMapping : allWithoutOrder) {
			try {
				VarCombinationForIngredient varCombinationWrapper = new VarCombinationForIngredient(varMapping);
				// In random mode, all same probabilities
				varCombinationWrapper.setProbality((double) 1 / (double) allWithoutOrder.size());
				allCom.add(varCombinationWrapper);
			} catch (Exception e) {
				logger.error("Error for obtaining a string representation of combination with " + varMapping.size()
						+ " variables");
			}
		}
		Collections.shuffle(allCom, RandomManager.getRandom());

		logger.debug("Number combination RANDOMLY sorted : " + allCom.size() + " over " + allWithoutOrder.size());

		return allCom;

	}
}
