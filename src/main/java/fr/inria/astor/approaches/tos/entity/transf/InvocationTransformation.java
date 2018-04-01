package fr.inria.astor.approaches.tos.entity.transf;

import fr.inria.astor.approaches.tos.entity.placeholders.InvocationPlaceholder;
import spoon.reflect.reference.CtExecutableReference;

/**
 * 
 * @author Matias Martinez
 *
 */
public class InvocationTransformation implements Transformation {

	CtExecutableReference executableTarget;
	InvocationPlaceholder varplaceholder;	
	
	public InvocationTransformation(CtExecutableReference executableTarget, InvocationPlaceholder varplaceholder) {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void apply() {

	}

	@Override
	public void revert() {

	}

}
