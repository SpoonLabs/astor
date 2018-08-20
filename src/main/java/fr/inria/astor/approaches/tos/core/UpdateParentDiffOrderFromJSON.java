package fr.inria.astor.approaches.tos.core;

import spoon.reflect.code.CtCodeElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class UpdateParentDiffOrderFromJSON extends SimpleDiffOrderFromJSON {

	@Override
	public String getKey(CtCodeElement element) {

		return element.getClass().getSimpleName() + "_" + element.getParent().getClass().getSimpleName();
	}

	@Override
	public String getKeyFromJSON(Object type) {
		String[] comp = type.toString().split("_");

		return comp[1] + "_" + comp[2];
	}

	@Override
	public String tagName() {
		return "frequencyParent";
	}

	@Override
	public boolean accept(Object type) {

		return type.toString().startsWith("UPD");
	}
}
