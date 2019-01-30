package fr.inria.astor.core.solutionsearch.spaces.ingredients.ingredientSearch;

import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.RandomManager;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPool;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.core.stats.Stats;
import fr.inria.astor.util.MapList;
import fr.inria.astor.util.StringUtil;

/**
 * Strategy based on {@link UniformRandomIngredientSearch}, which stores the
 * ingredient already used by the algorithm.
 * 
 * @author Matias Martinez
 *
 */
public class SimpleRandomSelectionIngredientStrategy extends IngredientSearchStrategy {

	private static final Boolean DESACTIVATE_CACHE = ConfigurationProperties
			.getPropertyBool("desactivateingredientcache");

	protected Logger log = Logger.getLogger(this.getClass().getName());

	public SimpleRandomSelectionIngredientStrategy(IngredientPool space) {
		super(space);

	}

	public MapList<String, String> cache = new MapList<>();

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

		int attemptsBaseIngredients = 0;

		List<Ingredient> baseElements = geIngredientsFromSpace(modificationPoint, operationType);

		if (baseElements == null || baseElements.isEmpty()) {
			log.debug("Any element available for mp " + modificationPoint);
			return null;
		}

		int elementsFromFixSpace = baseElements.size();
		log.debug("Templates availables" + elementsFromFixSpace);

		Stats.currentStat.getIngredientsStats().addSize(Stats.currentStat.getIngredientsStats().ingredientSpaceSize,
				baseElements.size());

		while (attemptsBaseIngredients < elementsFromFixSpace) {

			attemptsBaseIngredients++;
			log.debug(String.format("Attempts Base Ingredients  %d total %d", attemptsBaseIngredients,
					elementsFromFixSpace));

			Ingredient baseIngredient = getRandomStatementFromSpace(baseElements);

			String newingredientkey = getKey(modificationPoint, operationType);

			if (baseIngredient != null && baseIngredient.getCode() != null) {

				// check if the element was already used
				if (DESACTIVATE_CACHE || !this.cache.containsKey(newingredientkey)
						|| !this.cache.get(newingredientkey).contains(baseIngredient.getChacheCodeString())) {
					this.cache.add(newingredientkey, baseIngredient.getChacheCodeString());
					return baseIngredient;
				}

			}

		} // End while

		log.debug("--- no mutation left to apply in element "
				+ StringUtil.trunc(modificationPoint.getCodeElement().getShortRepresentation())
				+ ", search space size: " + elementsFromFixSpace);
		return null;

	}

	public String getKey(ModificationPoint modPoint, AstorOperator operator) {
		String lockey = modPoint.getCodeElement().getPosition().toString() + "-" + modPoint.getCodeElement() + "-"
				+ operator.toString();
		return lockey;
	}

	/**
	 * 
	 * @param fixSpace
	 * @return
	 */
	protected Ingredient getRandomStatementFromSpace(List<Ingredient> fixSpace) {
		if (fixSpace == null)
			return null;
		int size = fixSpace.size();
		int index = RandomManager.nextInt(size);
		return fixSpace.get(index);

	}

	public List<Ingredient> geIngredientsFromSpace(ModificationPoint modificationPoint, AstorOperator operationType) {

		String type = null;
		if (operationType instanceof ReplaceOp) {
			type = modificationPoint.getCodeElement().getClass().getSimpleName();
		}

		List<Ingredient> elements = null;
		if (type == null) {
			elements = this.ingredientSpace.getIngredients(modificationPoint.getCodeElement());

		} else {
			elements = this.ingredientSpace.getIngredients(modificationPoint.getCodeElement(), type);
		}

		return elements;
	}
}
