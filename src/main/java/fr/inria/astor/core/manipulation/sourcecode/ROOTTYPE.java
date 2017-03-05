package fr.inria.astor.core.manipulation.sourcecode;

import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtVisitor;
import spoon.support.reflect.declaration.CtTypeImpl;

public class ROOTTYPE extends CtTypeImpl {

	@Override
	public void accept(CtVisitor visitor) {

		
	}

	@Override
	public String getSimpleName() {
		
		return "";
	}

	//@Override
	public boolean isSubtypeOf(CtTypeReference arg0) {
		return false;
	}
	
	

}
