package fr.inria.astor.approaches.tos.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.inria.astor.approaches.tos.entity.placeholders.InvocationPlaceholder;
import fr.inria.astor.approaches.tos.entity.placeholders.VariablePlaceholder;
import fr.inria.astor.approaches.tos.entity.transf.Transformation;
import fr.inria.astor.approaches.tos.entity.transf.VariableTransformation;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.manipulation.sourcecode.VarAccessWrapper;
import fr.inria.astor.core.manipulation.sourcecode.VarCombinationForIngredient;
import fr.inria.astor.core.manipulation.sourcecode.VarMapping;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.RandomManager;
import fr.inria.astor.util.MapList;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtVariable;

/**
 * 
 * @author Matias Martinez
 *
 */
public class PatchGenerator {

	protected Logger logger = Logger.getLogger(PatchGenerator.class.getName());

	public List<Transformation> process(ModificationPoint modificationPoint, InvocationPlaceholder varplaceholder) {
		List<Transformation> transformation = new ArrayList<>();
		return transformation;
	}

	@SuppressWarnings("rawtypes")
	public List<Transformation> process(ModificationPoint modificationPoint, VariablePlaceholder varplaceholder) {

		// Vars in scope at the modification point
		List<CtVariable> variablesInScope = modificationPoint.getContextOfModificationPoint();
		List<Transformation> transformation = new ArrayList<>();
		// Check Those vars not transformed must exist in context
		List<CtVariableAccess> concreteVars = varplaceholder.getVariablesNotModified();
		List<CtVariableAccess> outOfContext = VariableResolver.retriveVariablesOutOfContext(variablesInScope,
				concreteVars);
		if (outOfContext != null && !outOfContext.isEmpty()) {
			logger.debug("Concrete vars could not be mapped  " + outOfContext + "\nin context: " + variablesInScope);
			return transformation;

		}

		// Once we mapped all concrete variables (i.e., not transformed), and we
		// are sure they exist in
		// context.

		// Now we map placeholders with vars in scope:
		MapList<String, CtVariableAccess> placeholders = varplaceholder.getPalceholders();

		List<CtVariableAccess> placeholdersVariables = new ArrayList<>();
		for (List<CtVariableAccess> pvs : placeholders.values()) {
			placeholdersVariables.addAll(pvs);
		}

		logger.debug("Placeholder variables to map: " + placeholdersVariables);
		VarMapping mapping = VariableResolver.mapVariablesFromContext(variablesInScope, placeholdersVariables);

		// if we map all placeholder variables
		if (mapping.getNotMappedVariables().isEmpty()) {
			if (mapping.getMappedVariables().isEmpty()) {
				// nothing to transform, accept the ingredient
				logger.debug("Something is wrong: Any placeholder var was mapped ");

			} else {

				List<VarCombinationForIngredient> allCombinations = findAllVarMappingCombinationUsingRandom(
						mapping.getMappedVariables());

				if (allCombinations.size() > 0) {

					for (VarCombinationForIngredient varCombinationForIngredient : allCombinations) {
						transformation.add(new VariableTransformation(varCombinationForIngredient, mapping));
					}
				}
			}
		} else {

			// Placeholders without mapping: we discart it.
			logger.debug(
					String.format("Placeholders without mapping (%d/%d): %s ", mapping.getNotMappedVariables().size(),
							placeholdersVariables.size(), mapping.getNotMappedVariables().toString()));
			String varContext = "";
			for (CtVariable context : variablesInScope) {
				varContext += context.getSimpleName() + " " + context.getType().getQualifiedName() + ", ";
			}
			logger.debug("Context: " + varContext);
			for (CtVariableAccess ingredient : mapping.getNotMappedVariables()) {
				logger.debug("---out_of_context: " + ingredient.getVariable().getSimpleName() + ": "
						+ ingredient.getVariable().getType().getQualifiedName());
			}
		}

		return transformation;
	}

	@SuppressWarnings("rawtypes")
	public List<VarCombinationForIngredient> findAllVarMappingCombinationUsingRandom(
			Map<VarAccessWrapper, List<CtVariable>> mappedVars) {

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
