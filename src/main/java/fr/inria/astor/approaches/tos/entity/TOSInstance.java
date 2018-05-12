package fr.inria.astor.approaches.tos.entity;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.tos.entity.transf.Transformation;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;

/**
 * Represents an ingredient that cames from a TOS
 * 
 * @author Matias Martinez
 *
 */
public class TOSInstance extends Ingredient {

	protected TOSEntity tosDerived = null;

	protected List<Transformation> transformations = new ArrayList<>();

	public TOSInstance(CtElement derivedFrom, TOSEntity tosDerived) {
		super(null, null, derivedFrom);
		this.tosDerived = tosDerived;
	}

	public TOSEntity getTosDerived() {
		return tosDerived;
	}

	public void setTosDerived(TOSEntity tosDerived) {
		this.tosDerived = tosDerived;
	}

	public List<Transformation> getTransformations() {
		return transformations;
	}

	public void setTransformations(List<Transformation> transformations) {
		this.transformations = transformations;
	}

	public CtElement generatePatch() {
		// System.out.println("Synthesizing patch: ");

		for (Transformation transformation : this.transformations) {

			transformation.apply();

		}
		CtCodeElement cloned = MutationSupporter.clone((CtCodeElement) this.getTosDerived().getDerivedFrom());

		this.setCode(cloned);

		for (Transformation transformation : this.transformations) {

			transformation.revert();

		}
		return cloned;
	}

	@Override
	public CtElement getCode() {
		if (this.ingredientCode == null) {
			this.ingredientCode = this.generatePatch();
		}
		return this.ingredientCode;
	}
}
