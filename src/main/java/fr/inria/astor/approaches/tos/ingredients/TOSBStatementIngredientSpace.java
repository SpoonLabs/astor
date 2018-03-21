package fr.inria.astor.approaches.tos.ingredients;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import com.google.common.collect.Sets;
import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.tos.entity.TOSCounter;
import fr.inria.astor.approaches.tos.entity.TOSEntity;
import fr.inria.astor.approaches.tos.entity.TOSVariablePlaceholder;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.spaces.ingredients.AstorIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.CacheList;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.IngredientSpaceScope;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.util.MapList;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

/**
 * Ingredient space (pool) which content are "TOSs"
 * 
 * @author Matias Martinez
 *
 */
public class TOSBStatementIngredientSpace
		extends AstorIngredientSpace<CtElement, String, Ingredient, String, CtCodeElement> {

	public static String PLACEHOLDER_VAR = "_%s_%d";

	// TODO:
	public IngredientSpaceScope scope;

	protected TOSCounter tosCounter = null;

	protected Logger log = Logger.getLogger(this.getClass().getName());

	public TOSBStatementIngredientSpace(List<TargetElementProcessor<?>> processors) throws JSAPException {
		super(processors);
	}

	@Override
	public void defineSpace(ProgramVariant variant) {

		tosCounter = new TOSCounter();

		List<CtType<?>> affected = obtainClassesFromScope(variant);
		log.debug("Creating Expression Ingredient space: affected ");
		for (CtType<?> classToProcess : affected) {

			List<CtCodeElement> ingredients = this.ingredientProcessor.createFixSpace(classToProcess);
			TargetElementProcessor.mustClone = true;

			for (Object originalIngredient : ingredients) {

				log.debug(String.format("Ingredient to process: %s", originalIngredient.toString()));

				if (originalIngredient instanceof CtStatement) {
					CtStatement originalIngredientStatement = (CtStatement) originalIngredient;

					String keyLocation = mapKey(originalIngredientStatement);
					MutationSupporter.getEnvironment().setNoClasspath(true);
					List<TOSEntity> xTemplates = createAllTOS(originalIngredientStatement);

					List<Ingredient> ingredientPoolForLocation = this.retrieveIngredients(keyLocation);
					for (TOSEntity templateElement : xTemplates) {
						if (!ingredientPoolForLocation.contains(templateElement)) {
							log.debug("Adding tos " + templateElement + " to " + ingredientPoolForLocation);
							ingredientPoolForLocation.add(templateElement);
						} else {
							log.debug("Existing template");
						}
						tosCounter.saveStatisticsOfTos(templateElement, originalIngredientStatement);
					}
				} else {
					log.debug("Ingredient is not a statement " + originalIngredient);
				}
			}

		}
		// once we finish creating the space, we split ingredients by type
		this.recreateTypesStructures();
	}

	private List<TOSEntity> createAllTOS(CtStatement ingredientStatement) {
		List<TOSEntity> xTemplates = new ArrayList<>();
		int nrPlaceholders = ConfigurationProperties.getPropertyInt("nrPlaceholders");
		List<TOSVariablePlaceholder> xNames = createTOSVarPlaceholder(ingredientStatement, nrPlaceholders);

		xTemplates.addAll(xNames);

		return xTemplates;
	}

	public List<Ingredient> retrieveIngredients(String key) {

		List<Ingredient> ingredientsKey = getFixSpace().get(key);
		if (!getFixSpace().containsKey(key)) {
			ingredientsKey = new CacheList();
			getFixSpace().put(key, ingredientsKey);
		}
		return ingredientsKey;
	}

	protected List<CtType<?>> obtainClassesFromScope(ProgramVariant variant) {

		if (IngredientSpaceScope.PACKAGE.equals(scope)) {
			return variant.getAffectedClasses();
		}
		if (IngredientSpaceScope.LOCAL.equals(scope)) {
			return variant.getAffectedClasses();
		}
		if (IngredientSpaceScope.GLOBAL.equals(scope)) {
			return MutationSupporter.getFactory().Type().getAll();
		}
		return null;
	}

	@Override
	public IngredientSpaceScope spaceScope() {
		return this.scope;
	}

	@Override
	public String calculateLocation(CtElement elementToModify) {

		if (IngredientSpaceScope.PACKAGE.equals(scope)) {
			return elementToModify.getParent(CtPackage.class).getQualifiedName();
		} else if (IngredientSpaceScope.LOCAL.equals(scope)) {
			return elementToModify.getParent(CtType.class).getQualifiedName();
		} else if (IngredientSpaceScope.GLOBAL.equals(scope))
			return "Global";

		return null;

	}

	@SuppressWarnings("unchecked")
	public List<TOSVariablePlaceholder> createTOSVarPlaceholder(CtStatement ingredientStatement, int nrPlaceholders) {

		List<TOSVariablePlaceholder> createdTemplates = new ArrayList<>();

		List<CtVariableAccess> varAccessCollected = VariableResolver.collectVariableAccess(ingredientStatement, true);
		List<String> varsNames = varAccessCollected.stream().map(e -> e.getVariable().getSimpleName()).distinct()
				.collect(Collectors.toList());
		List<Set<String>> variableNamesCombinations = Sets.powerSet(new HashSet<>(varsNames)).stream()
				.filter(e -> e.size() == nrPlaceholders && !e.isEmpty()).collect(Collectors.toList());
		log.debug("Names " + varsNames);
		log.debug("combinations " + variableNamesCombinations);

		for (Set<String> targetPlaceholders : variableNamesCombinations) {

			log.debug("analyzing target Placeholders: " + targetPlaceholders);

			TOSVariablePlaceholder tosCreated = createParticularTOS(ingredientStatement, targetPlaceholders);

			if (tosCreated != null) {
				log.debug("Adding  generated TOS: " + tosCreated);
				createdTemplates.add(tosCreated);
			}

		}
		return createdTemplates;
	}

	@SuppressWarnings("unchecked")
	private TOSVariablePlaceholder createParticularTOS(CtStatement ingredientStatement,
			Set<String> targetPlaceholders) {

		// Vars name mapped to placeholders
		Map<String, String> placeholderVarNamesMappings = new HashMap<>();
		MapList<String, CtVariableAccess> placeholdersToVariables = new MapList<>();
		List<CtVariableAccess> variablesNotModified = new ArrayList<>();
		//

		CtElement original = ingredientStatement;
		CtElement ingredientElement = MutationSupporter.clone(ingredientStatement);// ingredientStatement.clone();

		// We collect all variables
		List<CtVariableAccess> varAccessCollected = VariableResolver.collectVariableAccess(ingredientElement, true);

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

			variableUnderAnalysis.getVariable().setSimpleName(abstractName);
			// workaround: Problems with var Shadowing
			variableUnderAnalysis.getFactory().getEnvironment().setNoClasspath(true);
			if (variableUnderAnalysis instanceof CtFieldAccess) {
				CtFieldAccess fieldAccess = (CtFieldAccess) variableUnderAnalysis;
				fieldAccess.getVariable().setDeclaringType(null);
			}

		}

		TOSVariablePlaceholder ingredient = new TOSVariablePlaceholder(ingredientElement, null, original,
				placeholdersToVariables, placeholderVarNamesMappings, variablesNotModified);

		return ingredient;
	}

	@Override
	protected String getType(Ingredient ingredient) {

		return ingredient.getCode().getClass().getSimpleName();
	}

	public TOSCounter getTosCounter() {
		return tosCounter;
	}

}
