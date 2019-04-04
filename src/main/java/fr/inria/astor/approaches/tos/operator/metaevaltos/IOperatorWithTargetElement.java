package fr.inria.astor.approaches.tos.operator.metaevaltos;

import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public interface IOperatorWithTargetElement {

	public void setTargetElement(CtElement target);

	public boolean checkTargetCompatibility(CtElement target);
}
