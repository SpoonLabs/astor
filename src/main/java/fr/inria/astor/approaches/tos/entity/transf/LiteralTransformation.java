package fr.inria.astor.approaches.tos.entity.transf;

import spoon.reflect.code.CtLiteral;

/**
 * 
 * @author Matias Martinez
 *
 */
@SuppressWarnings("rawtypes")
public class LiteralTransformation implements Transformation {

	CtLiteral target;
	Object newValue;
	Object previousValue;

	public LiteralTransformation(CtLiteral target, Object newValue) {
		super();
		this.target = target;
		this.newValue = newValue;
	}

	@Override
	public void apply() {
		previousValue = target.getValue();
		target.setValue(newValue);
	}

	@Override
	public void revert() {
		target.setValue(previousValue);
		previousValue = null;
	}
	public String toString(){
		return this.getClass().getSimpleName() + " "+newValue+ " --> "+previousValue; 
	}
}
