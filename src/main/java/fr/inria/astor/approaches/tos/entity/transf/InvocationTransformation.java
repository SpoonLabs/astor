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
	
	private String previousElementName;

	public InvocationTransformation(CtExecutableReference selectedExecutableTarget,
			InvocationPlaceholder varplaceholder) {
		this.selectedExecutableTarget = selectedExecutableTarget;
		this.varplaceholder = varplaceholder;
	}

	@Override
	public void apply() {
		System.out.println("apply From " + this.varplaceholder.getInvocation().getExecutable().getSimpleName() + "--> "
				+ this.selectedExecutableTarget.getSimpleName());
		this.previousElementName = this.varplaceholder.getInvocation().getExecutable().getSimpleName()/*varplaceholder.getName()*/;
		this.varplaceholder.getInvocation().getExecutable()
				.setSimpleName(this.selectedExecutableTarget.getSimpleName());
		}

	@Override
	public void revert() {
		this.varplaceholder.getInvocation().getExecutable().setSimpleName(previousElementName);
	
	}

	public String toString() {
		return this.getClass().getSimpleName() + " " + selectedExecutableTarget.getSimpleName() + " --> "
				+ previousElementName;
	}
}
