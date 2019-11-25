package fr.inria.astor.approaches.extensions.rt.elements;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class Helper extends TestElement {

	List<CtInvocation> calls = new ArrayList();
	AsAssertion assertion = null;
	public boolean unexecutedAssert = false;
	public boolean unexecutedCall = false;

	public int distance = 0;

	public Helper(AsAssertion assertion) {
		super("Helper");
		this.assertion = assertion;
	}

	public List<CtInvocation> getCalls() {
		return calls;
	}

	public void setCalls(List<CtInvocation> calls) {
		this.calls = calls;
	}

	public AsAssertion getAssertion() {
		return assertion;
	}

	public void setAssertion(AsAssertion assertion) {
		this.assertion = assertion;
	}

	@Override
	public String toString() {
		return "Helper [calls=" + calls + ", assertion=" + assertion + "]";
	}

	@Override
	public CtElement getElement() {

		return (this.calls.size() > 0) ? this.calls.get(0) : null;
	}

	public int getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}
}