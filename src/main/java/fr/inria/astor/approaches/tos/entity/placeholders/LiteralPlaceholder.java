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
	Object newValue = null;

	CtLiteral affected = null;

	public LiteralPlaceholder(Object newValue, CtLiteral affected) {
		super();
		this.newValue = newValue;
		this.affected = affected;
	}

	@Override
	public void apply() {

		previousValue = affected.getValue();
		affected.setValue(newValue);

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

}
