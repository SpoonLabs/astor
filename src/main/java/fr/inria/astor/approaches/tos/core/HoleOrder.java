package fr.inria.astor.approaches.tos.core;

import java.util.List;

import spoon.reflect.code.CtCodeElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public interface HoleOrder {

	List<CtCodeElement> orderHoleElements(List<CtCodeElement> holes);
}
