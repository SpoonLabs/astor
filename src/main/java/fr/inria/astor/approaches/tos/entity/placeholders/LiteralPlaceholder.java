package fr.inria.astor.approaches.tos.entity.placeholders;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.tos.core.PatchGenerator;
import fr.inria.astor.approaches.tos.entity.transf.Transformation;
import fr.inria.astor.core.entities.ModificationPoint;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtLiteral;

/**
 * 
 * @author Matias Martinez
 *
 */
public class LiteralPlaceholder extends Placeholder {

	Object previousValue = null;
	Object placeholder_name = null;

	CtLiteral affected = null;

	public LiteralPlaceholder(Object newValue, CtLiteral affected) {
		super();
		this.placeholder_name = newValue;
		this.affected = affected;
	}

	@Override
	public void apply() {

		previousValue = affected.getValue();
		affected.setValue(placeholder_name);

	}

	@Override
	public void revert() {

		affected.setValue(previousValue);
		previousValue = null;

	}

	@Override
	public List<CtCodeElement> getAffectedElements() {
		List<CtCodeElement> elements = new ArrayList<>();
		elements.add(affected);
		return elements;
	}

	@Override
	public List<Transformation> visit(ModificationPoint modificationPoint, PatchGenerator visitor) {
		
		return visitor.process(modificationPoint, this);
	}

	public CtLiteral getAffected() {
		return affected;
	}
	public String toString(){
		return this.getClass().getSimpleName()+ ": "+placeholder_name + " --> "+affected.getValue();
	}

	public Object getPlaceholder_name() {
		return placeholder_name;
	}

	public void setPlaceholder_name(Object placeholder_name) {
		this.placeholder_name = placeholder_name;
	}
}
