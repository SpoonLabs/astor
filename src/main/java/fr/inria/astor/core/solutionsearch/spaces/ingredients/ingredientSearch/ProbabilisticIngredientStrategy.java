package fr.inria.astor.core.solutionsearch.spaces.ingredients.ingredientSearch;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.RandomManager;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPool;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.ExpressionTypeIngredientSpace;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.util.MapList;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ProbabilisticIngredientStrategy extends RandomSelectionTransformedIngredientStrategy {

	public ProbabilisticIngredientStrategy(IngredientPool space) {
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

	private Ingredient getTemplateByWeighted(List<Ingredient> elements, List<String> elements2String,
			Map<String, Double> probs) {
		// Random value
		Double randomElement = RandomManager.nextDouble();

		int i = 0;
		for (String template : probs.keySet()) {
			double probTemplate = probs.get(template);
			if (randomElement <= probTemplate) {
				int index = elements2String.indexOf(template);
				Ingredient templateElement = elements.get(index);
				log.debug("BI with prob "+probTemplate+" "+(i++) +" "+templateElement);
				return templateElement;
			}
		}
		return null;
	}

	List<String> elements2String = null;
	Map<String, Double> probs = null;

	@Override
	public List<Ingredient> getNotExhaustedBaseElements(ModificationPoint modificationPoint,
			AstorOperator operationType) {

		List<Ingredient> elements = super.getNotExhaustedBaseElements(modificationPoint, operationType);

		if(elements == null){
			return null;
		}
		if (ConfigurationProperties.getPropertyBool("frequenttemplate")) {
			log.debug("Defining template order for "+modificationPoint);
			ExpressionTypeIngredientSpace space = (ExpressionTypeIngredientSpace) this.getIngredientSpace();

			// Ingredients from space
			// ingredients to string
			elements2String = new ArrayList<>();
			for (Ingredient cm : elements) {
				elements2String.add(cm.toString());
			}
			// Obtaining counting of templates from the space
			MapList mp = new MapList<>();
			mp.putAll(space.linkTemplateElements);
			mp.keySet().removeIf(e -> !elements2String.contains(e));

			// Obtaining accumulate frequency of elements
			probs = mp.getProb().getProbAccumulative();

		}
		return elements;

	}

	@Override
	protected Ingredient getRandomStatementFromSpace(List<Ingredient> fixSpace) {

		if (ConfigurationProperties.getPropertyBool("frequenttemplate"))

			return getTemplateByWeighted(fixSpace, elements2String, probs);
		else
			return super.getRandomStatementFromSpace(fixSpace);
	}

}
