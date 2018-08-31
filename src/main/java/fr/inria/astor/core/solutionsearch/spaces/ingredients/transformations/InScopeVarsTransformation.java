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
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtVariable;

/**
 * 
 * @author Matias Martinez
 *
 */
public class InScopeVarsTransformation extends ClusterIngredientTransformation
		implements IngredientTransformationStrategy {

	protected Logger logger = Logger.getLogger(InScopeVarsTransformation.class.getName());

	@Override
	public List<Ingredient> transform(ModificationPoint modificationPoint, Ingredient baseIngredient) {

		List<Ingredient> result = new ArrayList<>();

		CtCodeElement codeElementToModify = (CtCodeElement) baseIngredient.getCode();

		if (modificationPoint.getContextOfModificationPoint().isEmpty()) {
			logger.debug("The modification point  has not any var in scope");
		}

		VarMapping mapping = VariableResolver.mapVariablesFromContext(modificationPoint.getContextOfModificationPoint(),
				codeElementToModify);
		// if we map all variables
		if (mapping.getNotMappedVariables().isEmpty()) {
			if (mapping.getMappedVariables().isEmpty()) {
				// nothing to transform, accept the ingredient
				logger.debug("Any transf sucessful: The var Mapping is empty, we keep the ingredient");
				result.add(new Ingredient(codeElementToModify));

			} else {// We have mappings between variables
				logger.debug("Ingredient before transformation: " + baseIngredient);
				List<Map<String, CtVariable>> allCombinations = VariableResolver
						.findAllVarMappingCombination(mapping.getMappedVariables());

				if (allCombinations.size() > 0) {

					for (Map<String, CtVariable> selectedTransformation : allCombinations) {

						// logger.debug("Transformation proposed: " + selectedTransformation);
						// The ingredient is cloned, so we can modify
						// its variables
						Map<VarAccessWrapper, CtVariableAccess> originalMap = VariableResolver
								.convertIngredient(mapping, selectedTransformation);
						// Cloned transformed element
						CtCodeElement codeElementCloned = MutationSupporter.clone(codeElementToModify);
						Ingredient ingredient4Combination = new Ingredient(codeElementCloned, null);
						result.add(ingredient4Combination);

						VariableResolver.resetIngredient(originalMap);
					}
				}
			}
		} else {
			logger.debug("Any transformation was sucessful: Vars not mapped: " + mapping.getNotMappedVariables());
		}

		return result;

	}

}
