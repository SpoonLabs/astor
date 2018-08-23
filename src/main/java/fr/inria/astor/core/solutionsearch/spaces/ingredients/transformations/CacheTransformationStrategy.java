package fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.util.StringUtil;

/**
 * 
 * @author Matias Martinez
 *
 */
public abstract class CacheTransformationStrategy implements IngredientTransformationStrategy {

	public Map<String, List<Ingredient>> appliedIngredientsCache = new HashMap<String, List<Ingredient>>();

	protected Logger log = Logger.getLogger(this.getClass().getName());

	public String getKey(ModificationPoint modPoint) {
		String lockey = modPoint.getCodeElement().getPosition().toString() + "-" + modPoint.getCodeElement();
		return lockey;
	}

	private String getBaseIngredientKey(ModificationPoint modificationPoint, Ingredient baseIngredient) {
		return getKey(modificationPoint) + baseIngredient.toString();
	}

	public boolean alreadyTransformed(ModificationPoint modificationPoint, Ingredient baseIngredient) {

		String keyBaseIngredient = getBaseIngredientKey(modificationPoint, baseIngredient);

		return appliedIngredientsCache.containsKey(keyBaseIngredient);

	}

	public List<Ingredient> getCachedTransformations(ModificationPoint modificationPoint, Ingredient baseIngredient) {

		String keyBaseIngredient = getBaseIngredientKey(modificationPoint, baseIngredient);

		return appliedIngredientsCache.get(keyBaseIngredient);

	}

	public void storingIngredients(ModificationPoint modificationPoint, Ingredient baseIngredient,
			List<Ingredient> ingredientsAfterTransformation) {

		log.debug("storing transformations");
		String baseIngredientKey = getBaseIngredientKey(modificationPoint, baseIngredient);

		if (ingredientsAfterTransformation != null && !ingredientsAfterTransformation.isEmpty()) {

			appliedIngredientsCache.put(baseIngredientKey, ingredientsAfterTransformation);
			Stats.currentStat.getIngredientsStats().addSize(
					Stats.currentStat.getIngredientsStats().combinationByIngredientSize,
					ingredientsAfterTransformation.size());

		} else {
			log.debug(
					"The transformation strategy has not returned any Valid transformed ingredient for ingredient base "
							+ StringUtil.trunc(baseIngredient.getCode()));

			appliedIngredientsCache.put(baseIngredientKey, null);
			Stats.currentStat.getIngredientsStats()
					.addSize(Stats.currentStat.getIngredientsStats().combinationByIngredientSize, 0);
		}

	}

}
