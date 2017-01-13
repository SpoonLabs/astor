package fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch;

import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.RandomManager;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;

/**
 * This Fix Space takes uniform randomly elements from the the search space. It
 * creates the space ON DEMAND, that is, it process a CtClass the first time it
 * is required
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * @param <I>
 *
 */
public class UniformRandomIngredientSearch extends AstorCtSearchStrategy {

	public UniformRandomIngredientSearch(IngredientSpace space) {
		super(space);
	}

	private Logger logger = Logger.getLogger(UniformRandomIngredientSearch.class.getName());

	
	/**
	 * 
	 * @param fixSpace
	 * @return
	 */
	protected CtCodeElement getRandomStatementFromSpace(List<CtCodeElement> fixSpace) {
		int size = fixSpace.size();
		int index = RandomManager.nextInt(size);
		return fixSpace.get(index);

	}

	/**
	 * Return a cloned CtStatement from the fix space in a randomly way
	 * 
	 * @return
	 */
	protected CtCodeElement getRandomElementFromSpace(CtElement location) {
		CtCodeElement originalPicked = getRandomStatementFromSpace(this.ingredientSpace.getIngredients(location));
		CtCodeElement cloned = MutationSupporter.clone(originalPicked);
		return cloned;
	}

	protected CtCodeElement getRandomElementFromSpace(CtElement location, String type) {
		List<CtCodeElement> elements = this.ingredientSpace.getIngredients(location, type);
		if (elements == null)
			return null;
		return getRandomStatementFromSpace(elements);
	}

	@Override
	public Ingredient getFixIngredient(ModificationPoint modificationPoint, AstorOperator operationType) {

		String type = null;
		if (operationType instanceof ReplaceOp) {
			type = modificationPoint.getCodeElement().getClass().getSimpleName();
		}

		CtElement selectedIngredient = null;
		if (type == null) {
			selectedIngredient = this.getRandomElementFromSpace(modificationPoint.getCodeElement());
		} else {
			selectedIngredient = this.getRandomElementFromSpace(modificationPoint.getCodeElement(), type);
		}

		return new Ingredient(selectedIngredient, null);

	}


}
