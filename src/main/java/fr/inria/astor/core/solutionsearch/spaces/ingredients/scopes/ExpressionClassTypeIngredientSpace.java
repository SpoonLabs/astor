package fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes;

import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ExpressionClassTypeIngredientSpace extends ExpressionTypeIngredientSpace {

	public ExpressionClassTypeIngredientSpace(List<TargetElementProcessor<?>> processors) throws JSAPException {
		super(processors);
	}

	@Override
	public List<Ingredient> getIngredients(CtElement element) {
		if (element instanceof CtExpression) {

			String keyLocation = mapKey(element);
			CtExpression ctExpr = (CtExpression) element;
			String typeExpression = ctExpr.getClass().getSimpleName();
			String returnTypeExpression = (ctExpr.getType() == null) ? "null" : ctExpr.getType().getSimpleName();
			List ingredients = (List<Ingredient>) mkp.get(keyLocation, typeExpression, returnTypeExpression);
			return ingredients;
		}
		log.error("Element is not a expression: " + element.getClass().getCanonicalName());
		return null;
	}

	@Override
	protected List<Ingredient> getIngrediedientsFromKey(String keyLocation, CtExpression ctExpr) {
		String typeExpression = ctExpr.getClass().getSimpleName();

		String returnTypeExpression = (ctExpr.getType() != null) ? ctExpr.getType().getSimpleName() : "null";

		List<Ingredient> ingredientsKey = (List<Ingredient>) mkp.get(keyLocation, typeExpression,
				returnTypeExpression);

		if (!mkp.containsKey(keyLocation, typeExpression, returnTypeExpression)) {
			ingredientsKey = new CacheList<Ingredient>();
			mkp.put(keyLocation, typeExpression, returnTypeExpression, ingredientsKey);

		}
		return ingredientsKey;
	}
}
