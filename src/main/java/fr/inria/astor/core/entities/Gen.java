package fr.inria.astor.core.entities;

import java.util.List;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;

/**
 * Gen of the program variant. It represents an element (i.e. spoon element, CtElement) of
 * the program under analysis.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class Gen {

	protected ProgramVariant programVariant;
	
	protected CtElement rootElement;

	protected CtClass ctClass;
	
	List<CtVariable> contextOfGen;

	public int identified = 0;
	// TODO: to set a value.
	protected int generation = -1;

	public Gen() {
	}


	public Gen(CtElement rootElement, CtClass ctClass, List<CtVariable> contextOfGen) {
		super();
		this.rootElement = rootElement;
		this.ctClass = ctClass;
		this.contextOfGen = contextOfGen;
	}

	public CtElement getRootElement() {
		return rootElement;
	}

	public void setRootElement(CtElement rootElement) {
		this.rootElement = rootElement;
	}

	public CtClass getCtClass() {
		return ctClass;
	}

	public void setClonedClass(CtClass clonedClass) {
		this.ctClass = clonedClass;
	}

	public String toString() {
		return "[" + rootElement.getClass().getSimpleName() + ", in " + ctClass.getSimpleName() + "]";
	}

	public List<CtVariable> getContextOfGen() {
		return contextOfGen;
	}

	public void setContextOfGen(List<CtVariable> contextOfGen) {
		this.contextOfGen = contextOfGen;
	}

	public ProgramVariant getProgramVariant() {
		return programVariant;
	}

	public void setProgramVariant(ProgramVariant programVariant) {
		this.programVariant = programVariant;
	}

}
