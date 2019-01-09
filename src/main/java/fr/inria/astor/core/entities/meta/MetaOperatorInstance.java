package fr.inria.astor.core.entities.meta;

import java.util.List;
import java.util.Map;

import fr.inria.astor.core.entities.CompositeOperatorInstance;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.OperatorInstance;

/**
 * 
 * @author Matias Martinez
 *
 */
public class MetaOperatorInstance extends CompositeOperatorInstance {

	protected Map<Integer, Ingredient> allIngredientsingredients;

	public MetaOperatorInstance(List<OperatorInstance> instances) {
		super(instances);

	}

	public Map<Integer, Ingredient> getAllIngredients() {
		return allIngredientsingredients;
	}

	public void setAllIngredients(Map<Integer, Ingredient> ingredients) {
		this.allIngredientsingredients = ingredients;
	}

}
