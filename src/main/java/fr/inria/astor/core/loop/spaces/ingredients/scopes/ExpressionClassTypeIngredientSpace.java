package fr.inria.astor.core.loop.spaces.ingredients.scopes;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.collections.keyvalue.MultiKey;
import org.apache.commons.collections.map.MultiKeyMap;
import org.apache.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.AbstractFixSpaceProcessor;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.util.MapList;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ExpressionClassTypeIngredientSpace extends ExpressionTypeIngredientSpace {

	public ExpressionClassTypeIngredientSpace(List<AbstractFixSpaceProcessor<?>> processors) throws JSAPException {
		super(processors);
	}

	@Override
	public List<CtCodeElement> getIngredients(CtElement element) {
		if (element instanceof CtExpression) {

			String keyLocation = mapKey(element);
			CtExpression ctExpr = (CtExpression) element;
			String typeExpression = ctExpr.getClass().getSimpleName();
			String returnTypeExpression = (ctExpr.getType() == null) ? "null" : ctExpr.getType().getSimpleName();
			List ingredients = (List<CtCodeElement>) mkp.get(keyLocation, typeExpression, returnTypeExpression);
			return ingredients;
		}
		log.error("Element is not a expression: " + element.getClass().getCanonicalName());
		return null;
	}

	@Override
	protected List<CtCodeElement> getIngrediedientsFromKey(String keyLocation, CtExpression ctExpr) {
		String typeExpression = ctExpr.getClass().getSimpleName();

		String returnTypeExpression = (ctExpr.getType() != null) ? ctExpr.getType().getSimpleName() : "null";

		List<CtCodeElement> ingredientsKey = (List<CtCodeElement>) mkp.get(keyLocation, typeExpression,
				returnTypeExpression);

		if (!mkp.containsKey(keyLocation, typeExpression, returnTypeExpression)) {
			ingredientsKey = new CacheList<CtCodeElement>();
			mkp.put(keyLocation, typeExpression, returnTypeExpression, ingredientsKey);

		}
		return ingredientsKey;
	}
}
