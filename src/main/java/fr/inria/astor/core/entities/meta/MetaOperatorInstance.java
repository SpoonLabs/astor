package fr.inria.astor.core.entities.meta;

import java.util.Map;

import fr.inria.astor.core.entities.CompositeOperatorInstance;
import fr.inria.astor.core.entities.Ingredient;

/**
 * 
 * @author Matias Martinez
 *
 */
public class MetaOperatorInstance extends CompositeOperatorInstance {

	protected Map<Integer, Ingredient> allIngredientsingredients;

	protected MetaOperator metaoperator;

	protected int metaIdentifier = 0;

	public MetaOperatorInstance(MetaOperator metaoperator, int metaIdentifier) {
		super();
		this.metaoperator = metaoperator;
		this.metaIdentifier = metaIdentifier;
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

	public int getIdentifier() {
		return metaIdentifier;
	}

	public void setMetaIdentifier(int metaIdentifier) {
		this.metaIdentifier = metaIdentifier;
	}

}
