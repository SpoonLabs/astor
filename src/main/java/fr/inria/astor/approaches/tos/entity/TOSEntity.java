package fr.inria.astor.approaches.tos.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import fr.inria.astor.approaches.tos.entity.placeholders.Placeholder;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;

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

			placeholder.apply();

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

	public boolean canBeApplied(ModificationPoint modificationPoint) {
		return getVarsOutOfContext(modificationPoint).isEmpty();
	}

	public List<CtVariableAccess> getVarsOutOfContext(ModificationPoint modificationPoint) {

		List<CtVariable> variablesInScope = modificationPoint.getContextOfModificationPoint();

		Set<CtCodeElement> affected = getAffectedElements();

		// Check Those vars not transformed must exist in context
		List<CtVariableAccess> outOfContext = VariableResolver.retriveVariablesOutOfContext(variablesInScope,
				this.derivedFrom);

		// remove the affected
		boolean removed = outOfContext.removeAll(affected);

		return outOfContext;

	}

	public Set<CtCodeElement> getAffectedElements() {
		Set<CtCodeElement> affected = new HashSet<>();

		for (Placeholder placeholder : this.getPlaceholders()) {

			List<CtCodeElement> affected_i = placeholder.getAffectedElements();
			affected.addAll(affected_i);

		}
		return affected;
	}
}
