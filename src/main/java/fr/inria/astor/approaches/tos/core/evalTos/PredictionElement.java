package fr.inria.astor.approaches.tos.core.evalTos;

import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class PredictionElement {

	int index;
	CtElement element;

	public PredictionElement(int index, CtElement element) {
		super();
		this.index = index;
		this.element = element;
	}

	public PredictionElement(CtElement element) {
		super();
		this.index = -1;
		this.element = element;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public CtElement getElement() {
		return element;
	}

	public void setElement(CtElement element) {
		this.element = element;
	}

	@Override
	public String toString() {
		return "PredictionElement [index=" + index + ", element=" + element + "]";
	}

}
