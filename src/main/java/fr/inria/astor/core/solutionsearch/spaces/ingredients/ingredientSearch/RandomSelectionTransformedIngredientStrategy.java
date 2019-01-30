package fr.inria.astor.core.solutionsearch.spaces.ingredients.ingredientSearch;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.ingredientbased.IngredientBasedEvolutionaryRepairApproachImpl;
import fr.inria.astor.core.setup.RandomManager;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPool;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.IngredientTransformationStrategy;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.util.MapList;
import fr.inria.astor.util.StringUtil;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;

/**
 * Strategy based on {@link UniformRandomIngredientSearch}, which stores the
 * ingredient already used by the algorithm.
 * 
 * @author Matias Martinez
 *
 */
public class RandomSelectionTransformedIngredientStrategy extends IngredientSearchStrategy {

	IngredientTransformationStrategy ingredientTransformationStrategy;

	protected Logger log = Logger.getLogger(this.getClass().getName());

	public RandomSelectionTransformedIngredientStrategy(IngredientPool space) {
		super(space);

		try {
			this.ingredientTransformationStrategy = IngredientBasedEvolutionaryRepairApproachImpl
					.retrieveIngredientTransformationStrategy();
		} catch (Exception e) {
			log.error(e);
		}

	}

	/**
	 * Ingredients already selected
	 */
	public Map<String, List<String>> appliedCache = new HashMap<String, List<String>>();
	public Map<String, List<Ingredient>> appliedIngredientsCache = new HashMap<String, List<Ingredient>>();
	public MapList<String, Ingredient> exhaustTemplates = new MapList<>();

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

		int attemptsBaseIngredients = 0;

		List<Ingredient> baseElements = getNotExhaustedBaseElements(modificationPoint, operationType);

		if (baseElements == null || baseElements.isEmpty()) {
			log.debug("Any template available for mp " + modificationPoint);
			List usedElements = this.exhaustTemplates.get(getKey(modificationPoint, operationType));
			if (usedElements != null)
				log.debug("#templates already used: " + usedElements.size());
			return null;
		}

		int elementsFromFixSpace = baseElements.size();
		log.debug("Templates availables: " + elementsFromFixSpace);

		Stats.currentStat.getIngredientsStats().addSize(Stats.currentStat.getIngredientsStats().ingredientSpaceSize,
				baseElements.size());

		while (attemptsBaseIngredients < elementsFromFixSpace) {

			log.debug(String.format("Attempts Base Ingredients  %d total %d", attemptsBaseIngredients,
					elementsFromFixSpace));

			Ingredient baseIngredient = getRandomStatementFromSpace(baseElements);

			if (baseIngredient == null || baseIngredient.getCode() == null) {

				return null;
			}

			Ingredient refinedIngredient = getNotUsedTransformedElement(modificationPoint, operationType,
					baseIngredient);

			attemptsBaseIngredients++;

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

	public List<Ingredient> getInstancesFromBase(ModificationPoint modificationPoint, AstorOperator operator,
			Ingredient baseIngredient) {
		List<Ingredient> ingredientsAfterTransformation = null;
		String keyBaseIngredient = getBaseIngredientKey(modificationPoint, operator, baseIngredient);

		if (appliedIngredientsCache.containsKey(keyBaseIngredient)) {
			log.debug("Retrieving already calculated transformations");
			ingredientsAfterTransformation = appliedIngredientsCache.get(keyBaseIngredient);

			// We try two cases: null (template cannot be instantiated) or
			// empty (all combination were already tested)
			if (ingredientsAfterTransformation == null) {
				log.debug("Already instantiated template but without valid instance on this MP, update stats "
						+ baseIngredient);
				return null;
			} else if (ingredientsAfterTransformation.isEmpty()) {
				log.debug("All instances were already tried, exit without update stats."
						+ StringUtil.trunc(baseIngredient.getCode()));
				return null;
			} else {
				// We have still ingredients to apply
				Stats.currentStat.getIngredientsStats().addSize(
						Stats.currentStat.getIngredientsStats().combinationByIngredientSize,
						ingredientsAfterTransformation.size());
			}

		} else {
			log.debug("Calculating transformations");
			try {
				ingredientsAfterTransformation = ingredientTransformationStrategy.transform(modificationPoint,
						baseIngredient);
				if (ingredientsAfterTransformation != null && !ingredientsAfterTransformation.isEmpty()) {
					appliedIngredientsCache.put(keyBaseIngredient, ingredientsAfterTransformation);
					Stats.currentStat.getIngredientsStats().addSize(
							Stats.currentStat.getIngredientsStats().combinationByIngredientSize,
							ingredientsAfterTransformation.size());

				} else {
					log.debug(
							"The transformation strategy has not returned any Valid transformed ingredient for ingredient base "
									+ StringUtil.trunc(baseIngredient.getCode()));

					appliedIngredientsCache.put(keyBaseIngredient, null);
					Stats.currentStat.getIngredientsStats()
							.addSize(Stats.currentStat.getIngredientsStats().combinationByIngredientSize, 0);
					exhaustTemplates.add(getKey(modificationPoint, operator), baseIngredient);
				}
			} catch (Throwable e) {
				log.equals("errooor mp:" + modificationPoint + " ingredient " + baseIngredient);
			}
		}
		return ingredientsAfterTransformation;
	}

	public Ingredient getNotUsedTransformedElement(ModificationPoint modificationPoint, AstorOperator operator,
			Ingredient baseIngredient) {
		List<Ingredient> ingredientsAfterTransformation = getInstancesFromBase(modificationPoint, operator,
				baseIngredient);
		if (ingredientsAfterTransformation == null) {
			return null;
		}

		return this.getNotUsedTransformedElement(modificationPoint, operator, baseIngredient,
				ingredientsAfterTransformation);
	}

	/**
	 * Returns randomly an ingredient
	 * 
	 * @param modificationPoint
	 * @param operator
	 * @param baseIngredient
	 * @return
	 */
	private Ingredient getNotUsedTransformedElement(ModificationPoint modificationPoint, AstorOperator operator,
			Ingredient baseIngredient, List<Ingredient> ingredientsAfterTransformation) {

		log.debug("\nIngredient  base: " + StringUtil.trunc(baseIngredient.getCode()) + " from "
				+ ((CtType) baseIngredient.getCode().getParent(CtType.class)).getQualifiedName());

		log.debug(String.format("Valid Transformed ingredients in mp: %s,  base ingr: %s, : size (%d) ",
				StringUtil.trunc(modificationPoint.getCodeElement()), StringUtil.trunc(baseIngredient.getCode()),
				ingredientsAfterTransformation.size()));

		if (ingredientsAfterTransformation.isEmpty()) {
			log.debug("No more combination");
			return null;
		}

		Ingredient transformedIngredient = null;
		int attempts = 0;
		while (attempts <= ingredientsAfterTransformation.size()) {

			transformedIngredient = getOneIngredientFromList(ingredientsAfterTransformation);

			if (transformedIngredient == null) {
				log.debug("transformed ingredient null for " + modificationPoint.getCodeElement());
				continue;
			}

			boolean removed = ingredientsAfterTransformation.remove(transformedIngredient);
			if (!removed) {
				log.debug("Not Removing ingredient from cache");
			} else {
				if (ingredientsAfterTransformation.isEmpty()) {
					exhaustTemplates.add(getKey(modificationPoint, operator), baseIngredient);
				}
			}

			attempts++;
			log.debug(String.format("\nAttempts In Transformed Ingredient  %d total %d", attempts,
					ingredientsAfterTransformation.size()));

			if (transformedIngredient.getCode().toString().equals(modificationPoint.getCodeElement().toString())) {
				log.debug("Ingredient idem to buggy statement, discarting it.");
				continue;
			}

			// we check if was applied
			boolean alreadyApplied = alreadySelected(modificationPoint, transformedIngredient.getCode(), operator);

			if (!alreadyApplied) {
				return transformedIngredient;
			}

		}
		log.debug(String.format("After %d attempts, we could NOT find an ingredient in a space of size %d", attempts,
				ingredientsAfterTransformation.size()));
		return null;

	}

	private String getBaseIngredientKey(ModificationPoint modificationPoint, AstorOperator operator,
			Ingredient baseIngredient) {
		return getKey(modificationPoint, operator) + baseIngredient.toString();
	}

	protected Ingredient getOneIngredientFromList(List<Ingredient> ingredientsAfterTransformation) {
		Ingredient transformedIngredient;
		transformedIngredient = ingredientsAfterTransformation
				.get(RandomManager.nextInt(ingredientsAfterTransformation.size()));
		return transformedIngredient;
	}

	/**
	 * Return the number of ingredients according to: the location and the operator
	 * to apply.
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
	 * @param id       program instance id.
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
			log.debug(
					"\nChache: New Element with new Key: " + StringUtil.trunc(fix) + " in " + StringUtil.trunc(lockey));
			return false;
		} else {
			// The element has mutation applied
			if (prev.contains(fix)) {
				log.debug("\nChache: Already stored: " + StringUtil.trunc(fix) + " in " + (lockey));
				return true;
			} else {
				prev.add(fix);
				log.debug("\nChache: New Element with existing Key: " + StringUtil.trunc(fix) + " in "
						+ StringUtil.trunc(lockey));
				return false;
			}
		}
	}

	public String getKey(ModificationPoint modPoint, AstorOperator operator) {
		String lockey = modPoint.getCodeElement().getPosition().toString() + "-" + modPoint.getCodeElement() + "-"
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
	protected Ingredient getRandomStatementFromSpace(List<Ingredient> fixSpace) {
		if (fixSpace == null)
			return null;
		int size = fixSpace.size();
		int index = RandomManager.nextInt(size);
		return fixSpace.get(index);

	}

	public List<Ingredient> getNotExhaustedBaseElements(ModificationPoint modificationPoint,
			AstorOperator operationType) {

		String type = null;
		if (operationType instanceof ReplaceOp) {
			type = modificationPoint.getCodeElement().getClass().getSimpleName();
		}

		List<Ingredient> elements = null;
		if (type == null) {
			elements = this.ingredientSpace.getIngredients(modificationPoint.getCodeElement());

		} else {
			elements = this.ingredientSpace.getIngredients(modificationPoint.getCodeElement(), type);
		}

		if (elements == null)
			return null;

		List<Ingredient> uniques = new ArrayList<>(elements);

		String key = getKey(modificationPoint, operationType);
		List<Ingredient> exhaustives = this.exhaustTemplates.get(key);

		if (exhaustives != null) {
			boolean removed = uniques.removeAll(exhaustives);
		}
		return uniques;
	}
}
