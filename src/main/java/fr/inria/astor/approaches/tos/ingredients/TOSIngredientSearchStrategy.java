package fr.inria.astor.approaches.tos.ingredients;

import java.util.List;

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

	@Override
	public Ingredient getFixIngredient(ModificationPoint modificationPoint, AstorOperator operationType) {
		//TODO:to improve
		List<Ingredient> baseIngedients = this.ingredientSpace.getIngredients(modificationPoint.getCodeElement(),
				modificationPoint.getCodeElement().getClass().getSimpleName());
		if (baseIngedients == null) {
			log.debug("No elements ingredients for mp " + modificationPoint);
			return null;
		}
		log.debug("elements for mp " + modificationPoint + " " + baseIngedients.size());
		int randomIndex = RandomManager.nextInt(baseIngedients.size());
		//
		Ingredient ingredientBase = baseIngedients.get(randomIndex);
		List<Ingredient> ingredientTransformed = this.ingredientTransformationStrategy.transform(modificationPoint,
				ingredientBase);
		
		if(ingredientTransformed == null || ingredientTransformed.isEmpty()){
			log.debug("No instance of ingredients for mp " + modificationPoint);
			return null;
		}
		randomIndex = RandomManager.nextInt(ingredientTransformed.size());
		DynamicIngredient ding =  (DynamicIngredient) ingredientTransformed.get(randomIndex);
		log.debug("selected concrete instantiation "+ding.getCombination());
		return ding;

	}

}
