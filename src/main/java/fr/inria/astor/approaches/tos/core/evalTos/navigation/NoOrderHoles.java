package fr.inria.astor.approaches.tos.core.evalTos.navigation;

import java.util.List;

import spoon.reflect.code.CtCodeElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class NoOrderHoles implements HoleOrder {

	@Override
	public List<CtCodeElement> orderHoleElements(List<CtCodeElement> holes) {

		return holes;
	}

}
