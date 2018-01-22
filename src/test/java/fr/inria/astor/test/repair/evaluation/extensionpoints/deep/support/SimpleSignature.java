package fr.inria.astor.test.repair.evaluation.extensionpoints.deep.support;

import spoon.reflect.reference.CtTypeReference;
import spoon.support.visitor.SignaturePrinter;

public class SimpleSignature extends SignaturePrinter {

	@Override
	public <T> void visitCtTypeReference(CtTypeReference<T> reference) {
		write(reference.getSimpleName());
		}

}
