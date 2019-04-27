package fr.inria.astor.core.entities;

import java.util.List;

import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;

/**
 * ModificationPoint created from a Suspicious code. That means, the
 * ModificationPoint is a suspicious to have a bug.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class SuspiciousModificationPoint extends ModificationPoint {

	protected SuspiciousCode suspicious;

	public SuspiciousModificationPoint() {
		super();
	}

	public SuspiciousModificationPoint(SuspiciousCode suspicious, CtElement rootElement, CtClass clonedClass,
			List<CtVariable> context) {
		super(rootElement, clonedClass, context);
		this.suspicious = suspicious;
	}

	public SuspiciousCode getSuspicious() {
		return suspicious;
	}

	public void setSuspicious(SuspiciousCode suspicious) {
		this.suspicious = suspicious;
	}

	public String toString() {
		return "MP=" + ctClass.getQualifiedName() + " line: " + suspicious.getLineNumber() + ", pointed element: "
				+ codeElement.getClass().getSimpleName() + "";
	}

	@Override
	public ModificationPoint clone() {

		SuspiciousModificationPoint sp = new SuspiciousModificationPoint(suspicious, codeElement, ctClass,
				contextOfModificationPoint);
		sp.identified = this.identified;
		sp.generation = this.generation;
		sp.programVariant = this.programVariant;
		return sp;
	}

}
