package fr.inria.astor.approaches.tos.ingredients;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.tos.entity.TOSCounter;
import fr.inria.astor.approaches.tos.entity.TOSEntity;
import fr.inria.astor.approaches.tos.entity.placeholders.Placeholder;
import fr.inria.astor.approaches.tos.ingredients.processors.PlaceholderGenerator;
import fr.inria.astor.approaches.tos.ingredients.processors.VariablePlaceholderGenerator;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.spaces.ingredients.AstorIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.CacheList;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.IngredientSpaceScope;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtStatement;
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

	// TODO:
	public IngredientSpaceScope scope;
	/**
	 * Entity that stores the stats of TOS.
	 */
	protected TOSCounter tosCounter = null;

	protected Logger log = Logger.getLogger(this.getClass().getName());
	/**
	 * Generators used for creating the TOS
	 */
	protected List<PlaceholderGenerator> tosGenerators = new ArrayList<>();

	public TOSBStatementIngredientSpace(List<TargetElementProcessor<?>> processors) throws JSAPException {
		super(processors);
		tosGenerators.add(new VariablePlaceholderGenerator());
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
						templateElement.generateCodeofTOS();
						if (!ingredientPoolForLocation.contains(templateElement)) {
							log.debug("Adding tos " + templateElement.getCode() + " to" + ingredientPoolForLocation);
							ingredientPoolForLocation.add(templateElement);
						} else {
							log.debug("Existing template");
						}
						// TODO: remove comment
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
		List<TOSEntity> xGeneratedTos = new ArrayList<>();

		for (PlaceholderGenerator tosGenerator : tosGenerators) {
			List<? extends Placeholder> xpalceholders = tosGenerator.createTOS(ingredientStatement);
			for (Placeholder placeholder : xpalceholders) {
				TOSEntity tos = new TOSEntity();
				tos.setDerivedFrom(ingredientStatement);
				tos.getPlaceholders().add(placeholder);

				// single or combined
				// TODO:
				xGeneratedTos.add(tos);
			}

		}

		return xGeneratedTos;
	}

	public List<Ingredient> retrieveIngredients(String key) {

		List<Ingredient> ingredientsKey = getFixSpace().get(key);
		if (!getFixSpace().containsKey(key)) {
			ingredientsKey = new CacheList<>();
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

	@Override
	protected String getType(Ingredient ingredient) {
		// before was new code
		return ingredient.getDerivedFrom().getClass().getSimpleName();
	}

	public TOSCounter getTosCounter() {
		return tosCounter;
	}

}
