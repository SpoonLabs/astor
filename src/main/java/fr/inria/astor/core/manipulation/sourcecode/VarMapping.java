package fr.inria.astor.core.manipulation.sourcecode;

import java.util.List;
import java.util.Map;

import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtVariable;

/**
 * We store the mapping between variables of a context and those from an
 * ingredient
 * 
 * @author Matias Martinez
 *
 */
public class VarMapping {

	private Map<VarAccessWrapper, List<CtVariable>> mappedVariables = null;
	private List<CtVariableAccess> notMappedVariables = null;

	public VarMapping(Map<VarAccessWrapper, List<CtVariable>> mapsVariables,
			List<CtVariableAccess> notMappedVariables) {
		super();
		this.mappedVariables = mapsVariables;
		this.notMappedVariables = notMappedVariables;
	}

	public Map<VarAccessWrapper, List<CtVariable>> getMappedVariables() {
		return mappedVariables;
	}

	public void setMappedVariables(Map<VarAccessWrapper, List<CtVariable>> mapsVariables) {
		this.mappedVariables = mapsVariables;
	}

	public List<CtVariableAccess> getNotMappedVariables() {
		return notMappedVariables;
	}

	public void setNotMappedVariables(List<CtVariableAccess> notMappedVariables) {
		this.notMappedVariables = notMappedVariables;
	}

}
