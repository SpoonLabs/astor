package fr.inria.astor.core.manipulation.bytecode.entities;

import java.util.List;
import java.util.Set;

import org.eclipse.jdt.internal.compiler.ClassFile;

import spoon.reflect.declaration.CtClass;
/**
 * Contains the result of a Program Variant compilation. In particular, the result of one CTClass contained by the variant. 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class ProgramVariantCompilationResult {
	
	private List<ClassFile> compilationList = null; 
	private Set<String> error = null;
	private CtClass compiledCtType = null;
	private String generatedSourceCode = null;
	
	public ProgramVariantCompilationResult(List<ClassFile> comp, Set<String> error, CtClass ctCl) {
		this.compilationList = comp;
		this.error = error; 
		this.compiledCtType = ctCl;
	}
	
	public ProgramVariantCompilationResult(List<ClassFile> comp, Set<String> error, CtClass ctCl,String generatedSourceCode) {
		this.compilationList = comp;
		this.error = error; 
		this.compiledCtType = ctCl;
		this.generatedSourceCode = generatedSourceCode;
	}

	public List<ClassFile> getCompilationList() {
		return compilationList;
	}

	public void setCompilationList(List<ClassFile> compilationList) {
		this.compilationList = compilationList;
	}

	public Set<String> getErrors() {
		return error;
	}

	public void setError(Set<String> error) {
		this.error = error;
	}
	/**
	 * Return if the variant compiles or not.
	 * @return
	 */
	public boolean compiles(){
		if(error == null || error.isEmpty()){
			return true;
		}
		return false;
	}

	public CtClass getCompiledCtType() {
		return compiledCtType;
	}

	public void setCompiledCtType(CtClass compiledCtType) {
		this.compiledCtType = compiledCtType;
	}
}
