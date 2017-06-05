package fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch;

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
import fr.inria.astor.core.loop.spaces.ingredients.transformations.IngredientTransformationStrategy;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.manipulation.sourcecode.VarAccessWrapper;
import fr.inria.astor.core.manipulation.sourcecode.VarMapping;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.util.StringUtil;
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

		int elementsFromFixSpace = getSpaceSize(modificationPoint, operationType);

		Stats.currentStat.initializeIngCounter(variant_id);

		while (attempts < elementsFromFixSpace) {

			Ingredient randomIngredient = super.getFixIngredient(modificationPoint, operationType);

			if (randomIngredient == null || randomIngredient.getCode() == null) {
				return null;
			}
			CtElement elementFromIngredient = randomIngredient.getCode();

			boolean alreadyApplied = alreadySelected(modificationPoint, elementFromIngredient, operationType);

			attempts = appliedCache.get(getKey(modificationPoint, operationType)).size();
			log.debug(String.format("\nattempts {%d} total %d", attempts, elementsFromFixSpace));
			
			
			if (alreadyApplied) {
				log.debug("Ingredient Already applied");
				continue;
			}

			boolean sameCode = elementFromIngredient.toString().equals(modificationPoint.getCodeElement().toString());

			if (sameCode) {
				log.debug("Ingredient same that the mod point");
				continue;
			}
			
			
			IngredientSpaceScope scope = VariableResolver.determineIngredientScope(modificationPoint.getCodeElement(),
					elementFromIngredient);
			randomIngredient.setScope(scope);
			
			return randomIngredient;

		} // End while

		log.debug("--- no mutation left to apply in element "
				+ StringUtil.trunc(modificationPoint.getCodeElement().getShortRepresentation())
				+ ", search space size: " + elementsFromFixSpace);
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
			log.debug("\nNew1: " + StringUtil.trunc(fix) + " in " + StringUtil.trunc(lockey));
			return false;
		} else {
			// The element has mutation applied
			if (prev.contains(fix)) {
				//log.debug("\nAlready: " + StringUtil.trunc(fix) + " in " + StringUtil.trunc(lockey));
				return true;
			} else {
				prev.add(fix);
				log.debug("\nNew2: " + StringUtil.trunc(fix) + " in " + StringUtil.trunc(lockey));
				return false;
			}
		}
	}

	private String getKey(ModificationPoint gen, AstorOperator operator) {
		String lockey = gen.getCodeElement().getPosition().toString() + "-" + gen.getCodeElement() + "-"
				+ operator.toString();
		return lockey;
	}

}
