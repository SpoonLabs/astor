package fr.inria.astor.approaches.extensions.rt.elements;

/**
 * 
 */
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;

public class AsAssertion extends TestElement {

	CtInvocation assertion = null;

	public AsAssertion(CtInvocation assertion) {
		super("Assertion");
		this.assertion = assertion;
	}

	public CtInvocation getCtAssertion() {
		return assertion;
	}

	public void setAssertion(CtInvocation assertion) {
		this.assertion = assertion;
	}

	@Override
	public String toString() {
		return assertion.toString();
	}

	@Override
	public CtElement getElement() {

		return assertion;
	}
}