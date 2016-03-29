package fr.inria.astor.core.entities;

import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class MutantCtElement {

	CtElement element;
	double probabilistic;
	
	public MutantCtElement(CtElement element, double probabilistic) {
		super();
		this.element = element;
		this.probabilistic = probabilistic;
	}

	public CtElement getElement() {
		return element;
	}

	public void setElement(CtElement element) {
		this.element = element;
	}

	public double getProbabilistic() {
		return probabilistic;
	}

	public void setProbabilistic(double probabilistic) {
		this.probabilistic = probabilistic;
	}

	@Override
	public String toString() {
		return  element + "[p=" + probabilistic + "]";
	}
	
}
