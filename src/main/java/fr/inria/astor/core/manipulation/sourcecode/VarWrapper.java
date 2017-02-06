package fr.inria.astor.core.manipulation.sourcecode;

import spoon.reflect.code.CtVariableAccess;

public class VarWrapper{
	CtVariableAccess var;
	public VarWrapper(CtVariableAccess var){
		this.var = var;
	}
	public CtVariableAccess getVar() {
		return var;
	}
	
}