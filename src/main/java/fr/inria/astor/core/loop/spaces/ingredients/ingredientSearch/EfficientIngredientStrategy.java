package fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.DefaultIngredientTransformation;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.IngredientTransformationStrategy;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.RandomManager;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.util.StringUtil;
import fr.inria.main.evolution.ExtensionPoints;
import fr.inria.main.evolution.PlugInLoader;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;

/**
 * Strategy based on {@link UniformRandomIngredientSearch}, which stores the
 * ingredient already used by the algorithm.
 * 
 * @author Matias Martinez
 *
 */
public class EfficientIngredientStrategy extends IngredientSearchStrategy {

	IngredientTransformationStrategy ingredientTransformationStrategy;

	public EfficientIngredientStrategy(IngredientSpace space) {
		super(space);
		ExtensionPoints ep = ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY;
		String ingredientTransformationStrategyClassName = ConfigurationProperties.properties
				.getProperty(ep.identifier);

		if (ingredientTransformationStrategyClassName == null) {
			this.ingredientTransformationStrategy = new DefaultIngredientTransformation();
		} else {
			try {
				this.ingredientTransformationStrategy = (IngredientTransformationStrategy) PlugInLoader
						.loadPlugin(ExtensionPoints.INGREDIENT_TRANSFORM_STRATEGY);
			} catch (Exception e) {
				log.error(e);
			}
		}
	}

	protected Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * Ingredients already selected
	 */
	protected Map<String, List<String>> appliedCache = new HashMap<String, List<String>>();
	protected Map<String, List<Ingredient>> appliedIngredientsCache = new HashMap<String, List<Ingredient>>();

	/**
	 * Return an ingredient. As it has a cache, it never returns twice the same
	 * ingredient.
	 * 
	 * @param modificationPoint
	 * @param targetStmt
	 * @param operationType
	 * @param elementsFromFixSpace
	 * @return
	 */
	@Override
	public Ingredient getFixIngredient(ModificationPoint modificationPoint, AstorOperator operationType) {

		int attempts = 0;

		int elementsFromFixSpace = getSpaceSize(modificationPoint, operationType);

		while (attempts < elementsFromFixSpace) {

			log.debug(String.format("\n****getIng******\nAttempts Ingredients  %d total %d", attempts,
					elementsFromFixSpace));

			Ingredient baseIngredient = this.getRandomFixIngredient(modificationPoint, operationType);

			if (baseIngredient == null || baseIngredient.getCode() == null) {
				return null;
			}

			Ingredient refinedIngredient = getNotUsedTransformedElement(modificationPoint, operationType,
					baseIngredient);

			attempts++;
			if (attempts > (elementsFromFixSpace * 3)) {
				log.error("Error: breaking loop in efficient ingredient search after # attempts " + attempts);
				return null;// break
			}

			if (refinedIngredient != null) {

				refinedIngredient.setDerivedFrom(baseIngredient.getCode());
				return refinedIngredient;
			}

		} // End while

		log.debug("--- no mutation left to apply in element "
				+ StringUtil.trunc(modificationPoint.getCodeElement().getShortRepresentation())
				+ ", search space size: " + elementsFromFixSpace);
		return null;

	}

	/**
	 * Returns randomly an ingredient
	 * 
	 * @param modificationPoint
	 * @param operator
	 * @param baseIngredient
	 * @return
	 */
	public Ingredient getNotUsedTransformedElement(ModificationPoint modificationPoint, AstorOperator operator,
			Ingredient baseIngredient) {

		log.debug("\n--\nIngredient  base" + baseIngredient + " from "
				+ ((CtType) baseIngredient.getCode().getParent(CtType.class)).getQualifiedName());
		List<Ingredient> ingredientsAfterTransformation = null;
		if (ingredientTransformationStrategy != null) {
			String key = getKey(modificationPoint, operator) + baseIngredient.toString();
			if (appliedIngredientsCache.containsKey(key)) {
				log.debug("Retrieving already calculated transformations");
				ingredientsAfterTransformation = appliedIngredientsCache.get(key);

				// We try two cases: null (template cannot be instantiated) or
				// empty (all combination were already tested)
				if (ingredientsAfterTransformation == null) {
					log.debug("Already instantiated template byt without valid instance on this MP, update stats "
							+ baseIngredient);
					Stats.currentStat.addSize(Stats.currentStat.combinationByIngredientSize, 0);
					return null;
				} else if (ingredientsAfterTransformation.isEmpty()) {
					log.debug("All instances were already tried, exit without update stats." + baseIngredient);
					return null;
				}

			} else {
				log.debug("Calculating transformations");
				ingredientsAfterTransformation = ingredientTransformationStrategy.transform(modificationPoint,
						baseIngredient);
				if (ingredientsAfterTransformation != null && !ingredientsAfterTransformation.isEmpty())
					appliedIngredientsCache.put(key, ingredientsAfterTransformation);
				else {
					log.debug(
							"The transformation strategy has not returned any Valid transformed ingredient for ingredient base "
									+ baseIngredient);
					appliedIngredientsCache.put(key, null);
					Stats.currentStat.addSize(Stats.currentStat.combinationByIngredientSize, 0);
					return null;
				}
			}

			log.debug(String.format("Valid Transformed ingredients in mp: %s,  base ingr: %s, : size (%d) ",
					modificationPoint.getCodeElement(), baseIngredient, ingredientsAfterTransformation.size()));
			Stats.currentStat.addSize(Stats.currentStat.combinationByIngredientSize,
					ingredientsAfterTransformation.size());

			Ingredient transformedIngredient = null;
			int attempts = 0;
			while (attempts <= ingredientsAfterTransformation.size()) {
				// we select one randomly
				transformedIngredient = getOneIngredientFromList(ingredientsAfterTransformation);

				if (transformedIngredient == null) {
					log.debug("transformed ingredient null");
					continue;
				}

				boolean removed = ingredientsAfterTransformation.remove(transformedIngredient);
				if (!removed) {
					log.debug("Not Removing ingredient from cache");
				}

				attempts++;
				log.debug(String.format("\nAttempts In Transformed Ingredient  %d total %d", attempts,
						ingredientsAfterTransformation.size()));

				if (transformedIngredient.getCode().toString().equals(modificationPoint.getCodeElement().toString())) {
					log.debug("Ingredient idem to buggy statement, discarting it.");
					continue;
				}

				// we check if was applyed
				boolean alreadyApplied = alreadySelected(modificationPoint, transformedIngredient.getCode(), operator);

				if (!alreadyApplied) {
					return transformedIngredient;
				}

			}
			log.debug(String.format("After %d attempts, we could NOT find an ingredient in a space of size %d",
					attempts, ingredientsAfterTransformation.size()));
		}
		return null;
	}

	protected Ingredient getOneIngredientFromList(List<Ingredient> ingredientsAfterTransformation) {
		Ingredient transformedIngredient;
		transformedIngredient = ingredientsAfterTransformation
				.get(RandomManager.nextInt(ingredientsAfterTransformation.size()));
		return transformedIngredient;
	}

	/**
	 * Return the number of ingredients according to: the location and the
	 * operator to apply.
	 * 
	 * @param modificationPoint
	 * @param operationType
	 * @return
	 */
	protected int getSpaceSize(ModificationPoint modificationPoint, AstorOperator operationType) {

		String type = null;

		List<?> allIng = getSpace(modificationPoint, operationType);

		if (allIng == null || allIng.isEmpty()) {
			return 0;
		}

		return allIng.size();
	}

	private List<?> getSpace(ModificationPoint modificationPoint, AstorOperator operationType) {
		String typeOperation = null;
		if (operationType instanceof ReplaceOp) {
			typeOperation = modificationPoint.getCodeElement().getClass().getSimpleName();
		}

		List<?> allIng = null;
		if (typeOperation == null) {
			allIng = this.ingredientSpace.getIngredients(modificationPoint.getCodeElement());
		} else {
			allIng = this.ingredientSpace.getIngredients(modificationPoint.getCodeElement(), typeOperation);
		}
		return allIng;
	}

	/**
	 * Check if the ingredient was already used
	 * 
	 * @param id
	 *            program instance id.
	 * @param fix
	 * @param location
	 * @return
	 */
	protected boolean alreadySelected(ModificationPoint gen, CtElement fixElement, AstorOperator operator) {
		// we add the instance identifier to the patch.
		String lockey = getKey(gen, operator);
		String fix = "";
		try {
			fix = fixElement.toString();
		} catch (Exception e) {
			log.error("to string fails");
		}
		List<String> prev = appliedCache.get(lockey);
		// The element does not have any mutation applied
		if (prev == null) {
			prev = new ArrayList<String>();
			prev.add(fix);
			appliedCache.put(lockey, prev);
			log.debug("\nNew1: " + StringUtil.trunc(fix) + " in " + StringUtil.trunc(lockey));
			return false;
		} else {
			// The element has mutation applied
			if (prev.contains(fix)) {
				log.debug("\nAlready: " + StringUtil.trunc(fix) + " in " + (lockey));
				return true;
			} else {
				prev.add(fix);
				log.debug("\nNew2: " + StringUtil.trunc(fix) + " in " + StringUtil.trunc(lockey));
				return false;
			}
		}
	}

	protected String getKey(ModificationPoint gen, AstorOperator operator) {
		String lockey = gen.getCodeElement().getPosition().toString() + "-" + gen.getCodeElement() + "-"
				+ operator.toString();
		return lockey;
	}

	public IngredientTransformationStrategy getIngredientTransformationStrategy() {
		return ingredientTransformationStrategy;
	}

	public void setIngredientTransformationStrategy(IngredientTransformationStrategy ingredientTransformationStrategy) {
		this.ingredientTransformationStrategy = ingredientTransformationStrategy;
	}
	
	
	
	/**
	 * 
	 * @param fixSpace
	 * @return
	 */
	protected CtCodeElement getRandomStatementFromSpace(List<CtCodeElement> fixSpace) {
		int size = fixSpace.size();
		int index = RandomManager.nextInt(size);
		return fixSpace.get(index);

	}

	/**
	 * Return a cloned CtStatement from the fix space in a randomly way
	 * 
	 * @return
	 */
	protected CtCodeElement getRandomElementFromSpace(CtElement location) {
		CtCodeElement originalPicked = getRandomStatementFromSpace(this.ingredientSpace.getIngredients(location));
		CtCodeElement cloned = MutationSupporter.clone(originalPicked);
		return cloned;
	}

	protected CtCodeElement getRandomElementFromSpace(CtElement location, String type) {
		List<CtCodeElement> elements = this.ingredientSpace.getIngredients(location, type);
		//logger.info("IngSpaceSize: "+elements.size());
		Stats.currentStat.addSize(Stats.currentStat.ingredientSpaceSize, elements.size());
		if (elements == null)
			return null;
		return getRandomStatementFromSpace(elements);
	}

	
	public Ingredient getRandomFixIngredient(ModificationPoint modificationPoint, AstorOperator operationType) {

		String type = null;
		if (operationType instanceof ReplaceOp) {
			type = modificationPoint.getCodeElement().getClass().getSimpleName();
		}

		CtElement selectedIngredient = null;
		if (type == null) {
			selectedIngredient = this.getRandomElementFromSpace(modificationPoint.getCodeElement());
		} else {
			selectedIngredient = this.getRandomElementFromSpace(modificationPoint.getCodeElement(), type);
		}
		
		return new Ingredient(selectedIngredient, null);

	}


	
	
	

}
