package fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes;

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

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPoolLocationType;
import fr.inria.astor.util.MapList;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ExpressionTypeIngredientSpace
		extends IngredientPoolLocationType<CtElement, String, Ingredient, String, CtCodeElement> {

	public IngredientPoolScope scope;

	public MultiKeyMap mkp = new MultiKeyMap();

	public List<Ingredient> allElementsFromSpace = new ArrayList<>();

	public MapList<String, Ingredient> linkTemplateElements = new MapList<>();

	protected Logger log = Logger.getLogger(this.getClass().getName());

	public ExpressionTypeIngredientSpace(List<TargetElementProcessor<?>> processors) throws JSAPException {
		super(processors);
	}

	@Override
	public void defineSpace(ProgramVariant variant) {

		List<CtType<?>> affected = obtainClassesFromScope(variant);
		log.debug("Creating Expression Ingredient space: ");
		for (CtType<?> classToProcess : affected) {

			List<CtCodeElement> ingredients = this.ingredientProcessor.createFixSpace(classToProcess);
			TargetElementProcessor.mustClone = true;

			for (CtCodeElement originalIngredient : ingredients) {
				Ingredient ingredientOriginal = new Ingredient(originalIngredient);
				String keyLocation = mapKey(originalIngredient);
				if (originalIngredient instanceof CtExpression) {
					CtExpression ctExpr = (CtExpression) originalIngredient;
					// String typeExpression =
					// ctExpr.getClass().getSimpleName();

					if (ctExpr.getType() == null) {
						continue;
					}
					List<Ingredient> ingredientsKey = getIngrediedientsFromKey(keyLocation, ctExpr);

					if (ConfigurationProperties.getPropertyBool("cleantemplates")) {
						MutationSupporter.getEnvironment().setNoClasspath(true);// ?

						CtCodeElement templateElement = MutationSupporter.clone(ctExpr);
						formatIngredient(templateElement);

						Ingredient templateIngredient = new Ingredient(templateElement);

						if (ConfigurationProperties.getPropertyBool("duplicateingredientsinspace")
								|| !ingredientsKey.contains(templateIngredient)) {
							ingredientsKey.add(templateIngredient);
							this.allElementsFromSpace.add(templateIngredient);
						}
						// We must always link elements, beyond the template is
						// duplicate or new
						// linking
						this.linkTemplateElements.add(templateElement.toString(), ingredientOriginal);

					} else {

						if (ConfigurationProperties.getPropertyBool("duplicateingredientsinspace")
								|| !ingredientsKey.contains(originalIngredient)) {
							// log.debug("Adding ingredient: " +
							// originalIngredient);
							ingredientsKey.add(ingredientOriginal);
							// all
							this.allElementsFromSpace.add(ingredientOriginal);
						}
					}
				}
			}
		}
		int nrIng = 0;
		// Printing summary:
		for (Object ingList : mkp.values()) {
			nrIng += ((List) ingList).size();
		}

		// sort links
		this.linkTemplateElements = this.linkTemplateElements.getSorted();
		log.info(String.format("Ingredient search space info : number keys %d , number values %d ", mkp.keySet().size(),
				nrIng));

		// this.linkTemplateElements.forEach((e,v) ->
		// log.debug(String.format("k: %s v: %d ", e,v.size())));

	}

	protected List<CtType<?>> obtainClassesFromScope(ProgramVariant variant) {

		if (IngredientPoolScope.PACKAGE.equals(scope)) {
			return variant.getAffectedClasses();
		}
		if (IngredientPoolScope.LOCAL.equals(scope)) {
			return variant.getAffectedClasses();
		}
		if (IngredientPoolScope.GLOBAL.equals(scope)) {
			return MutationSupporter.getFactory().Type().getAll();
		}
		return null;
	}

	@Override
	public IngredientPoolScope spaceScope() {
		return null;
	}

	@Override
	public String calculateLocation(CtElement elementToModify) {

		if (IngredientPoolScope.PACKAGE.equals(scope)) {
			return elementToModify.getParent(CtPackage.class).getQualifiedName();
		} else if (IngredientPoolScope.LOCAL.equals(scope)) {
			return elementToModify.getParent(CtType.class).getQualifiedName();
		} else if (IngredientPoolScope.GLOBAL.equals(scope))
			return "Global";

		return null;

	}

	@Override
	public String getType(Ingredient element) {

		return element.getCode().getClass().getSimpleName();
	}

	@Override
	public List<Ingredient> getIngredients(CtElement element) {
		if (element instanceof CtExpression) {

			String keyLocation = mapKey(element);
			CtExpression ctExpr = (CtExpression) element;
			String returnTypeExpression = (ctExpr.getType() == null) ? "null" : ctExpr.getType().getSimpleName();
			List<Ingredient> ingredients = (List<Ingredient>) mkp.get(keyLocation, returnTypeExpression);

			return ingredients;
		}
		log.error("Element is not a expression: " + element.getClass().getCanonicalName());
		return null;
	}

	@Override
	public List<Ingredient> getIngredients(CtElement element, String type) {
		return getIngredients(element);
	}

	@Override
	public List<String> getLocations() {
		List<String> keys = new ArrayList<>(mkp.keySet());
		return keys;
	}

	@Override
	public List<Ingredient> getAllIngredients() {
		List<Ingredient> allIngredients = new ArrayList<>();
		for (Iterator iterator = mkp.values().iterator(); iterator.hasNext();) {
			allIngredients.addAll((List) iterator.next());
		}
		return allIngredients;
	}

	@SuppressWarnings("unchecked")
	public void toJSON(String output) {
		JSONObject space = new JSONObject();

		JSONArray list = new JSONArray();
		space.put("nrall", this.allElementsFromSpace.size());
		space.put("space", list);

		for (Object key : mkp.keySet()) {
			JSONObject keyjson = new JSONObject();
			MultiKey mk = (MultiKey) key;
			keyjson.put("key", Arrays.toString(mk.getKeys()));
			list.add(keyjson);
			JSONArray ingredients = new JSONArray();
			keyjson.put("ingredients", ingredients);
			List ings = (List) mkp.get(key);
			keyjson.put("nringredients", ings.size());

			for (Object v : ings) {
				ingredients.add(v.toString());
			}
			;

		}

		String fileName = output + "ingredients.json";
		try (FileWriter file = new FileWriter(fileName)) {

			file.write(space.toJSONString());
			file.flush();
			log.info("Storing ing JSON at " + fileName);

		} catch (IOException e) {
			e.printStackTrace();
			log.error("Problem storing ing json file" + e.toString());
		}

	}

	@SuppressWarnings("unchecked")
	public void formatIngredient(CtElement ingredientCtElement) {

		// log.debug("\n------" + ingredientCtElement);
		List<CtVariableAccess> varAccessCollected = VariableResolver.collectVariableAccess(ingredientCtElement, true);
		Map<String, String> varMappings = new HashMap<>();
		int nrvar = 0;
		for (int i = 0; i < varAccessCollected.size(); i++) {
			CtVariableAccess var = varAccessCollected.get(i);

			if (VariableResolver.isStatic(var.getVariable())) {
				continue;
			}

			String abstractName = "";
			if (!varMappings.containsKey(var.getVariable().getSimpleName())) {
				String currentTypeName = var.getVariable().getType().getSimpleName();
				if (currentTypeName.contains("?")) {
					// Any change in case of ?
					abstractName = var.getVariable().getSimpleName();
				} else {
					abstractName = "_" + currentTypeName + "_" + nrvar;
				}
				varMappings.put(var.getVariable().getSimpleName(), abstractName);
				nrvar++;
			} else {
				abstractName = varMappings.get(var.getVariable().getSimpleName());
			}

			var.getVariable().setSimpleName(abstractName);
			// workaround: Problems with var Shadowing
			var.getFactory().getEnvironment().setNoClasspath(true);
			if (var instanceof CtFieldAccess) {
				CtFieldAccess fieldAccess = (CtFieldAccess) var;
				fieldAccess.getVariable().setDeclaringType(null);
			}

		}

	}

	protected List<Ingredient> getIngrediedientsFromKey(String keyLocation, CtExpression ctExpr) {

		String returnTypeExpression = (ctExpr.getType() != null) ? ctExpr.getType().getSimpleName() : "null";

		List<Ingredient> ingredientsKey = (List<Ingredient>) mkp.get(keyLocation, returnTypeExpression);

		if (!mkp.containsKey(keyLocation, returnTypeExpression)) {
			ingredientsKey = new CacheList<Ingredient>();
			mkp.put(keyLocation, returnTypeExpression, ingredientsKey);

		}
		return ingredientsKey;
	}
}
