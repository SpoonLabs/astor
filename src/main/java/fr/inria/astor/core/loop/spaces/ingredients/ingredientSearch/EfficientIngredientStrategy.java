package fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.IngredientSpaceScope;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.manipulation.sourcecode.VarAccessWrapper;
import fr.inria.astor.core.manipulation.sourcecode.VarMapping;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.stats.Stats;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;

/**
 * Strategy based on {@link UniformRandomIngredientSearch}, which stores the
 * ingredient already used by the algorithm.
 * 
 * @author Matias Martinez
 *
 */
public class EfficientIngredientStrategy extends UniformRandomIngredientSearch {

	public EfficientIngredientStrategy(IngredientSpace space) {
		super(space);
	}

	protected Logger log = Logger.getLogger(this.getClass().getName());

	/**
	 * Ingredients already selected
	 */
	protected Map<String, List<String>> appliedCache = new HashMap<String, List<String>>();

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

		int variant_id = modificationPoint.getProgramVariant().getId();

		int attempts = 0;

		boolean continueSearching = true;

		int elementsFromFixSpace = getSpaceSize(modificationPoint, operationType);

		Stats.currentStat.initializeIngCounter(variant_id);

		while (continueSearching && attempts < elementsFromFixSpace) {

			Ingredient randomIngredient = super.getFixIngredient(modificationPoint, operationType);

			if (randomIngredient == null || randomIngredient.getCode() == null) {
				return null;
			}
			CtElement elementFromIngredient = randomIngredient.getCode();

			attempts++;

			boolean alreadyApplied = alreadySelected(modificationPoint, elementFromIngredient, operationType);

			if (alreadyApplied) {
				log.debug("Ingredient Already applied");
				continue;
			}

			boolean sameCode = elementFromIngredient.toString().equals(modificationPoint.getCodeElement().toString());

			if (sameCode) {
				log.debug("Ingredient same that the mod point");
				continue;
			}

			boolean transformIngredient = ConfigurationProperties.getPropertyBool("transformingredient");
			if (transformIngredient) {
				if (modificationPoint.getContextOfModificationPoint().isEmpty()) {
					log.debug("The modification point  has not any var in scope");
				}
				VarMapping mapping = VariableResolver.mapVariables(modificationPoint.getContextOfModificationPoint(),
						elementFromIngredient);
				// if we map all variables
				if (mapping.getNotMappedVariables().isEmpty()) {
					if (mapping.getMappedVariables().isEmpty()) {
						// nothing to transform, accept the ingredient
						log.debug("The var Mapping is empty, we keep the ingredient");
						continueSearching = false;
					} else {// We have mappings between variables
						log.debug("Ingredient before transformation: " + elementFromIngredient);
						List<Map<String, CtVariable>> allCombinations = VariableResolver
								.findAllVarMappingCombination(mapping.getMappedVariables());
						// TODO: here, we take the first one, what should we do
						// with the rest?
						if (allCombinations.size() > 0) {
							Map<String, CtVariable> selectedTransformation = allCombinations.get(0);
							log.debug("Transformation proposed: " + selectedTransformation);
							// The ingredient is cloned, so we can modify its
							// variables
							Map<VarAccessWrapper, CtVariableAccess> originalMap = VariableResolver
									.convertIngredient(mapping, selectedTransformation);
							log.debug("Ingredient after transformation: " + elementFromIngredient);
							// TODO: do we need to revert the ingredient. If we
							// try another var combination -> yes.
							// Otherwise -> no
							// VariableResolver.resetIngredient(mapping,
							// originalMap);
							continueSearching = !VariableResolver.fitInPlace(
									modificationPoint.getContextOfModificationPoint(), elementFromIngredient);
						}
					}
				} else {
					// here maybe we can put one counter of not mapped
					// ingredients
					log.debug("Vars not mapped: " + mapping.getNotMappedVariables());
				}
			} else {
				// default behavior
				continueSearching = !VariableResolver.fitInPlace(modificationPoint.getContextOfModificationPoint(),
						elementFromIngredient);
			}

			Stats.currentStat.incrementIngCounter(variant_id);

			if (!continueSearching) {
				IngredientSpaceScope scope = determineIngredientScope(modificationPoint.getCodeElement(),
						elementFromIngredient);

				// int ingCounter =
				// Stats.currentStat.saveIngCounter(variant_id);
				int ingCounter = Stats.currentStat.temporalIngCounter.get(variant_id);
				log.debug("---attempts on ingredient space: " + ingCounter);

				return new Ingredient(elementFromIngredient, scope);
			}
		}

		log.debug("--- no mutation left to apply in element "
				+ modificationPoint.getCodeElement().getShortRepresentation() + ", search space size: "
				+ elementsFromFixSpace);
		return null;

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

		if (operationType instanceof ReplaceOp) {
			type = modificationPoint.getCodeElement().getClass().getSimpleName();
		}

		List<?> allIng = null;
		if (type == null) {
			allIng = this.ingredientSpace.getIngredients(modificationPoint.getCodeElement());
		} else {
			allIng = this.ingredientSpace.getIngredients(modificationPoint.getCodeElement(), type);
		}

		if (allIng == null || allIng.isEmpty()) {
			return 0;
		}

		return allIng.size();
	}

	@Deprecated
	protected IngredientSpaceScope determineIngredientScope(CtElement modificationpoint, CtElement selectedFix,
			List<?> ingredients) {
		// This is the original ingredient scope
		IngredientSpaceScope orig = determineIngredientScope(modificationpoint, selectedFix);

		String fixStr = selectedFix.toString();

		// Now, we search for equivalent fixes with different scopes
		for (Object ing : ingredients) {
			try {
				ing.toString();
			} catch (Exception e) {
				// if we cannot print the ingredient, we return
				log.error(e.toString());
				continue;
			}
			// if it's the same fix
			if (ing.toString().equals(fixStr)) {
				IngredientSpaceScope n = determineIngredientScope(modificationpoint, (CtElement) ing);
				// if the scope of the ingredient ing is narrower than the fix,
				// we keep it.
				if (n.ordinal() < orig.ordinal()) {
					orig = n;
					// if it's local, we return
					if (IngredientSpaceScope.values()[0].equals(orig))
						return orig;
				}

			}
		}
		return orig;
	}

	protected IngredientSpaceScope determineIngredientScope(CtElement ingredient, CtElement fix) {

		File ingp = ingredient.getPosition().getFile();
		File fixp = fix.getPosition().getFile();

		if (ingp.getAbsolutePath().equals(fixp.getAbsolutePath())) {
			return IngredientSpaceScope.LOCAL;
		}
		if (ingp.getParentFile().getAbsolutePath().equals(fixp.getParentFile().getAbsolutePath())) {
			return IngredientSpaceScope.PACKAGE;
		}
		return IngredientSpaceScope.GLOBAL;
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
		String lockey = gen.getCodeElement().getPosition().toString() + "-" + gen.getCodeElement() + "-"
				+ operator.toString();
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
			return false;
		} else {
			// The element has mutation applied
			if (prev.contains(fix))
				return true;
			else {
				prev.add(fix);
				return false;
			}
		}
	}

}
