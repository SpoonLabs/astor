package fr.inria.astor.core.entities;

import java.util.List;

import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;

/**
 * ModificationPoint of the program variant. It represents an element (i.e. spoon element, CtElement) of
 * the program under analysis.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class ModificationPoint {

	protected ProgramVariant programVariant;
	
	protected CtElement codeElement;

	protected CtClass ctClass;
	
	List<CtVariable> contextOfModificationPoint;

	public int identified = 0;
	
	protected int generation = -1;

	public ModificationPoint() {
	}


	public ModificationPoint(CtElement rootElement, CtClass ctClass, List<CtVariable> contextOfGen) {
		super();
		this.codeElement = rootElement;
		this.ctClass = ctClass;
		this.contextOfModificationPoint = contextOfGen;
	}

	public CtElement getCodeElement() {
		return codeElement;
	}

	public void setCodeElement(CtElement rootElement) {
		this.codeElement = rootElement;
	}

	public CtClass getCtClass() {
		return ctClass;
	}

	public void setClonedClass(CtClass clonedClass) {
		this.ctClass = clonedClass;
	}

	public String toString() {
		return "[" + codeElement.getClass().getSimpleName() + ", in " + ctClass.getQualifiedName()+ "]";
	}

	public List<CtVariable> getContextOfModificationPoint() {
		return contextOfModificationPoint;
	}

	public void setContextOfModificationPoint(List<CtVariable> contextOfModificationPoint) {
		this.contextOfModificationPoint = contextOfModificationPoint;
	}

	public ProgramVariant getProgramVariant() {
		return programVariant;
	}

	public void setProgramVariant(ProgramVariant programVariant) {
		this.programVariant = programVariant;
	}

}
