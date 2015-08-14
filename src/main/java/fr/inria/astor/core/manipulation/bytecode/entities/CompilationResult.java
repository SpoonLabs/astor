package fr.inria.astor.core.manipulation.bytecode.entities;

import java.util.List;
import java.util.Map;
/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class CompilationResult {

	private Map<String, byte[]> byteCodes;

	private List<String> errorList = null;
	
	public CompilationResult(Map<String, byte[]> bytecodes2,List<String> errorList) {
		byteCodes = bytecodes2;
		this.errorList = errorList; 
	}

	public Map<String, byte[]> getByteCodes() {
		return byteCodes;
	}

	public void setByteCodes(Map<String, byte[]> byteCodes) {
		this.byteCodes = byteCodes;
	}

	public boolean compiles() {
		return errorList == null || errorList.isEmpty();
	}
	
	@Override
	public String toString() {
		return "CompilationResult: byteCodes=" + byteCodes.size()+" errors (" + errorList.size()+ ") "+errorList + "]";
	}

	public List<String> getErrorList() {
		return errorList;
	}

	public void setErrorList(List<String> errorList) {
		this.errorList = errorList;
	}

}
