package fr.inria.astor.approaches.tos.entity.transf;

import fr.inria.astor.approaches.tos.entity.placeholders.InvocationPlaceholder;
import spoon.reflect.reference.CtExecutableReference;

/**
 * 
 * @author Matias Martinez
 *
 */
@SuppressWarnings("rawtypes")
public class InvocationTransformation implements Transformation {

	CtExecutableReference selectedExecutableTarget;
	InvocationPlaceholder varplaceholder;	
	protected String placeholderName;
	public InvocationTransformation(CtExecutableReference selectedExecutableTarget, InvocationPlaceholder varplaceholder) {
		this.selectedExecutableTarget = selectedExecutableTarget;
		this.varplaceholder = varplaceholder;
	}

	@Override
	public void apply() {
		this.placeholderName = varplaceholder.getName();
		this.varplaceholder.getInvocation().getExecutable().setSimpleName(this.selectedExecutableTarget.getSimpleName());
	}

	@Override
	public void revert() {
		this.varplaceholder.getInvocation().getExecutable().setSimpleName(placeholderName);

	}

}
