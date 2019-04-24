package fr.inria.astor.core.entities;

import java.util.HashMap;
import java.util.Map;

import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez matias.martinez@inria.fr
 *
 */
public class Ingredient {

	protected CtElement ingredientCode;
	protected IngredientPoolScope scope;
	protected CtElement derivedFrom;
	// Store the value of code var
	protected String cacheString = null;

	protected Map<String, Object> metadata = new HashMap<>();

	public Ingredient(CtElement code, IngredientPoolScope scope, CtElement derivedFrom) {
		super();
		this.ingredientCode = code;
		this.derivedFrom = derivedFrom;
		this.scope = scope;
	}

	public Ingredient(CtElement element) {
		super();
		this.ingredientCode = element;
	}

	public Ingredient(CtElement element, IngredientPoolScope scope) {
		super();
		this.ingredientCode = element;
		this.scope = scope;
	}

	public CtElement getCode() {
		return ingredientCode;
	}

	public void setCode(CtElement element) {
		this.ingredientCode = element;
	}

	public IngredientPoolScope getScope() {
		return scope;
	}

	public void setScope(IngredientPoolScope scope) {
		this.scope = scope;
	}

	public CtElement getDerivedFrom() {
		return derivedFrom;
	}

	public void setDerivedFrom(CtElement derivedFrom) {
		this.derivedFrom = derivedFrom;
	}

	@Override
	public String toString() {
		return this.getCode().toString();
	}

	/**
	 * Stores the value of code value. The first invocation it calculate it.
	 * 
	 * @return
	 */
	public String getChacheCodeString() {
		if (cacheString == null && this.getCode() != null) {
			cacheString = this.getCode().toString();
		}
		return cacheString;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((ingredientCode == null) ? 0 : ingredientCode.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Ingredient other = (Ingredient) obj;
		if (ingredientCode == null) {
			if (other.ingredientCode != null)
				return false;
		} else if (!ingredientCode.equals(other.ingredientCode))
			return false;
		return true;
	}

	public Map<String, Object> getMetadata() {
		return metadata;
	}

}
