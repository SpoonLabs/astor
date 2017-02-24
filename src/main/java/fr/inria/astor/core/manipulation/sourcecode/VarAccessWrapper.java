package fr.inria.astor.core.manipulation.sourcecode;

import spoon.reflect.code.CtVariableAccess;

/**
 * 
 * @author Matias Martinez
 *
 */
public class VarAccessWrapper {
	
	private CtVariableAccess var;

	public VarAccessWrapper(CtVariableAccess var) {
		this.var = var;
	}

	public CtVariableAccess getVar() {
		return var;
	}

	@Override
	public String toString() {
		return var.toString();
	}

}