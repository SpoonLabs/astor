package fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ExpressionTypeIngredientSpace;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.RandomManager;
import fr.inria.astor.util.MapList;
import spoon.reflect.code.CtCodeElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ProbabilisticIngredientStrategy extends EfficientIngredientStrategy {

	public ProbabilisticIngredientStrategy(IngredientSpace space) {
		super(space);
	}

	@Override
	protected Ingredient getOneIngredientFromList(List<Ingredient> ingredientsAfterTransformation) {

		if (ingredientsAfterTransformation.isEmpty()) {
			log.debug("No more elements from the ingredients space");
			return null;
		}
		log.debug(String.format("Obtaining the best element out of %d: %s", ingredientsAfterTransformation.size(),
				ingredientsAfterTransformation.get(0).getCode()));
		// Return the first one
		return ingredientsAfterTransformation.get(0);
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public Ingredient getFixIngredient(ModificationPoint modificationPoint, AstorOperator operationType) {

		if (ConfigurationProperties.getPropertyBool("frequenttemplate")) {
			ExpressionTypeIngredientSpace space = (ExpressionTypeIngredientSpace) this.getIngredientSpace();

			// Ingredients from space
			List<CtCodeElement> elements = space.getIngredients(modificationPoint.getCodeElement());

			// ingredients to string

			List<String> elements2String = new ArrayList<>();
			for (CtCodeElement cm : elements) {
				elements2String.add(cm.toString());
			}
			// Obtaining counting of templates from the space
			MapList mp = new MapList<>();
			mp.putAll(space.linkTemplateElements);
			mp.keySet().removeIf(e -> !elements2String.contains(e));

			// Obtaining accumulate frequency of elements
			LinkedHashMap<String, Double> probs = mp.getProb();

			// Random value
			Double randomElement = RandomManager.nextDouble();

			for (String template : probs.keySet()) {
				double probTemplate = probs.get(template);
				if (randomElement <= probTemplate) {
					int index = elements2String.indexOf(template);

					CtCodeElement templateElement = elements.get(index);

					Ingredient refinedIngredient = getNotUsedTransformedElement(modificationPoint, operationType,
							new Ingredient(templateElement));
					
					return refinedIngredient;
				}
			}

		} else {
			// By default
			return super.getFixIngredient(modificationPoint, operationType);

		}

		return null;
	}

}
