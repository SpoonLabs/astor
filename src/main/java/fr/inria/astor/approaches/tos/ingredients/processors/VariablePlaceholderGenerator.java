package fr.inria.astor.approaches.tos.ingredients.processors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;

import fr.inria.astor.approaches.tos.entity.placeholders.Placeholder;
import fr.inria.astor.approaches.tos.entity.placeholders.VariablePlaceholder;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.util.MapList;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class VariablePlaceholderGenerator<T extends CtElement> implements PlaceholderGenerator<T> {

	public static String PLACEHOLDER_VAR = "_%s_%d";

	protected Logger log = Logger.getLogger(this.getClass().getName());

	@SuppressWarnings("rawtypes")
	@Override
	public List<? extends Placeholder> createTOS(T ingredientSource) {

		int nrPlaceholders = ConfigurationProperties.getPropertyInt("nrPlaceholders");
		boolean lessplaceholderontos = ConfigurationProperties.getPropertyBool("lessplaceholderontos");

		List<VariablePlaceholder> createdTemplates = new ArrayList<>();

		List<CtVariableAccess> varAccessCollected = VariableResolver.collectVariableAccess(ingredientSource, true);
		List<String> varsNames = varAccessCollected.stream().map(e -> e.getVariable().getSimpleName()).distinct()
				.collect(Collectors.toList());
		// log.debug("Names (" + varsNames.size() + "): " + varsNames);
		// TODO: Workarround
		if (varsNames.size() > 20) {
			varsNames = varsNames.subList(0, 20);
		}
		List<Set<String>> variableNamesCombinations = Sets.powerSet(new HashSet<>(varsNames)).stream()
				.filter(e -> e.size() == nrPlaceholders && !e.isEmpty()).collect(Collectors.toList());

		// log.debug("combinations " + variableNamesCombinations);

		for (Set<String> targetPlaceholders : variableNamesCombinations) {

			// log.debug("analyzing target Placeholders: " +
			// targetPlaceholders);

			VariablePlaceholder placeholderCreated = createParticularTOS(ingredientSource, targetPlaceholders);

			if (placeholderCreated != null) {

				if (lessplaceholderontos || placeholderCreated.getPlaceholderVarNamesMappings().keySet().size() == nrPlaceholders) {
					// log.debug("Adding generated TOS: " + tosCreated);
					createdTemplates.add(placeholderCreated);
				} else {
					log.debug("Discarding tos with less placeholder than " + nrPlaceholders + " " + placeholderCreated);
				}

			}

		}
		return createdTemplates;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private VariablePlaceholder createParticularTOS(T ingredientStatement, Set<String> targetPlaceholders) {

		// We collect all variables
		List<CtVariableAccess> varAccessCollected = VariableResolver.collectVariableAccess(ingredientStatement, true);

		return this.createParticularTOS(ingredientStatement, targetPlaceholders, varAccessCollected);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private VariablePlaceholder createParticularTOS(T ingredientStatement, Set<String> targetPlaceholders,
			List<CtVariableAccess> varAccessCollected) {

		// Vars name mapped to placeholders
		Map<String, String> placeholderVarNamesMappings = new HashMap<>();
		MapList<String, CtVariableAccess> placeholdersToVariables = new MapList<>();
		List<CtVariableAccess> variablesNotModified = new ArrayList<>();

		int nrvar = 0;
		for (int i = 0; i < varAccessCollected.size(); i++) {

			CtVariableAccess<?> variableUnderAnalysis = varAccessCollected.get(i);

			if (!targetPlaceholders.contains(variableUnderAnalysis.getVariable().getSimpleName())) {
				// The variable name is not in the list of placeholders
				variablesNotModified.add(variableUnderAnalysis);
				continue;
			}

			if (VariableResolver.isStatic(variableUnderAnalysis.getVariable())) {
				variablesNotModified.add(variableUnderAnalysis);
				continue;
			}

			String abstractName = "";
			// We have not transform another variable with the same name
			if (!placeholderVarNamesMappings.containsKey(variableUnderAnalysis.getVariable().getSimpleName())) {
				String currentTypeName = variableUnderAnalysis.getVariable().getType().getSimpleName();
				if (currentTypeName.contains("?")) {
					// Any change in case of ?
					abstractName = variableUnderAnalysis.getVariable().getSimpleName();
				} else {
					abstractName = String.format(PLACEHOLDER_VAR, currentTypeName, nrvar);
				}
				placeholderVarNamesMappings.put(variableUnderAnalysis.getVariable().getSimpleName(), abstractName);
				nrvar++;
			} else {
				// We use the placeholder name previously defined for a variable
				// with similar name
				abstractName = placeholderVarNamesMappings.get(variableUnderAnalysis.getVariable().getSimpleName());
			}
			placeholdersToVariables.add(abstractName, variableUnderAnalysis);

		}
		if (!placeholdersToVariables.isEmpty()) {
			VariablePlaceholder ingredient = new VariablePlaceholder(placeholdersToVariables,
					placeholderVarNamesMappings, variablesNotModified);

			return ingredient;
		} else
			return null;
	}

}
