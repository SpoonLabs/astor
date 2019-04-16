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

	protected MetaOperator metaoperator;

	public MetaOperatorInstance(MetaOperator metaoperator, List<OperatorInstance> instances) {
		super(instances);
		this.metaoperator = metaoperator;

	}

	public Map<Integer, Ingredient> getAllIngredients() {
		return allIngredientsingredients;
	}

	public void setAllIngredients(Map<Integer, Ingredient> ingredients) {
		this.allIngredientsingredients = ingredients;
	}

	public MetaOperator getMetaoperator() {
		return metaoperator;
	}

	public void setMetaoperator(MetaOperator metaoperator) {
		this.metaoperator = metaoperator;
	}

}
