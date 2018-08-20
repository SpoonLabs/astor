package fr.inria.astor.approaches.tos.core;

import java.util.List;

import fr.inria.astor.core.solutionsearch.extension.AstorExtensionPoint;
import spoon.reflect.code.CtCodeElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public interface HoleOrder extends AstorExtensionPoint {

	List<CtCodeElement> orderHoleElements(List<CtCodeElement> holes);
}
