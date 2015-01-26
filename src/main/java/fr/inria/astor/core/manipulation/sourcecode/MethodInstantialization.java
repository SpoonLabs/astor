package fr.inria.astor.core.manipulation.sourcecode;

import java.util.List;
import java.util.Map;

import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
/**
 * 
 *  * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class MethodInstantialization{

	CtMethod method;
	Map<CtParameter,List> candidates;
	
	public MethodInstantialization(CtMethod method,
			Map<CtParameter, List> candidates) {
		super();
		this.method = method;
		this.candidates = candidates;
	}
	public CtMethod getMethod() {
		return method;
	}
	public void setMethod(CtMethod method) {
		this.method = method;
	}
	public Map<CtParameter, List> getCandidates() {
		return candidates;
	}
	public void setCandidates(Map<CtParameter, List> candidates) {
		this.candidates = candidates;
	}
	@Override
	public String toString() {
		return "MethodInstantialization [method=" + method.getSignature() + ", candidates="
				+ candidates + "]";
	} 
	
}