package fr.inria.astor.approaches.tos.ingredients;

import java.util.List;

import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.DynamicIngredient;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.IngredientTransformationStrategy;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.setup.RandomManager;

/**
 * 
 * @author Matias Martinez
 *
 */
public class TOSIngredientSearchStrategy extends IngredientSearchStrategy {

	IngredientTransformationStrategy ingredientTransformationStrategy;
	protected static Logger log = Logger.getLogger(Thread.currentThread().getName());

	public TOSIngredientSearchStrategy(IngredientSpace space) {
		super(space);

		try {
			this.ingredientTransformationStrategy = new TOSRandomTransformationStrategy();
		} catch (Exception e) {

		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public Ingredient getFixIngredient(ModificationPoint modificationPoint, AstorOperator operationType) {

		List<Ingredient> baseIngedients = this.ingredientSpace.getIngredients(modificationPoint.getCodeElement(),
				modificationPoint.getCodeElement().getClass().getSimpleName());

		if (baseIngedients == null) {
			log.debug("No elements ingredients for mp " + modificationPoint);
			return null;
		}
		log.debug("elements for mp " + modificationPoint + " " + baseIngedients.size());

		int randomIndex = RandomManager.nextInt(baseIngedients.size());
		// We have randomly selected one Ingredient base
		Ingredient ingredientBase = baseIngedients.get(randomIndex);
		List<Ingredient> ingredientTransformed = null;

		if (this.cacheInstances.containsKey(modificationPoint, ingredientBase.getChacheCodeString())) {
			ingredientTransformed = (List<Ingredient>) this.cacheInstances.get(modificationPoint,
					ingredientBase.getChacheCodeString());
			log.debug("Ingredients cache of size " + ingredientTransformed.size());

		} else {
			log.debug("Not in cache, generating for " + modificationPoint + " and "
					+ ingredientBase.getChacheCodeString());
			ingredientTransformed = this.ingredientTransformationStrategy.transform(modificationPoint, ingredientBase);

			this.cacheInstances.put(modificationPoint, ingredientBase.getChacheCodeString(), ingredientTransformed);

		}

		if (ingredientTransformed == null || ingredientTransformed.isEmpty()) {
			log.debug("No instance of ingredients for mp " + modificationPoint + " and "
					+ ingredientBase.getChacheCodeString());
			return null;
		}

		randomIndex = RandomManager.nextInt(ingredientTransformed.size());
		DynamicIngredient ding = (DynamicIngredient) ingredientTransformed.remove(randomIndex);

		log.debug("selected concrete instantiation " + ding.getCombination());
		return ding;

	}

	MultiKeyMap cacheInstances = new MultiKeyMap();
}
