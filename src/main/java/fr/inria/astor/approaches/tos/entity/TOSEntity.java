package fr.inria.astor.approaches.tos.entity;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.tos.entity.placeholders.Placeholder;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.IngredientSpaceScope;
import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;

/**
 * Abstract class of a TOS, which, in turns, is an ingredient.
 * 
 * @author Matias Martinez
 *
 */
public class TOSEntity extends Ingredient {

	List<Placeholder> placeholders = new ArrayList<>();

	public TOSEntity() {
		super(null);
	}

	public List<Placeholder> getPlaceholders() {
		return placeholders;
	}

	public void setPlaceholders(List<Placeholder> resolvers) {
		this.placeholders = resolvers;
	}

	public int getNrPlaceholder() {
		return placeholders.size();
	}

	public CtElement generateCodeofTOS() {

		for (Placeholder placeholder : placeholders) {

			placeholder.apply((CtCodeElement) this.derivedFrom);

		}
		CtCodeElement cloned = MutationSupporter.clone((CtCodeElement) this.derivedFrom);

		this.ingredientCode = cloned;

		for (Placeholder placeholder : placeholders) {

			placeholder.revert();

		}

		return this.ingredientCode;

	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((placeholders == null) ? 0 : placeholders.hashCode());
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
		TOSEntity other = (TOSEntity) obj;
		if (this.ingredientCode == null) {
			if (other.getCode() != null)
				return false;
		} else if (!this.getCode().equals(other.getCode()))
			return false;
		return true;
	}

	@Override
	public CtElement getCode() {
		if (this.ingredientCode == null) {
			return this.generateCodeofTOS();
		}
		return super.getCode();
	}

}
