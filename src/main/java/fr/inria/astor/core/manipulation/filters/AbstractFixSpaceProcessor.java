package fr.inria.astor.core.manipulation.filters;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCodeElement;

/**
 * Spoon processor to create a Fix Space for a given CtElement <T> Update: we
 * clone the original element before adding
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 * @param <T>
 */
public abstract class AbstractFixSpaceProcessor<T extends CtCodeElement> extends AbstractProcessor<T> {

	
	public static boolean mustClone = true;
	public static boolean allowsDuplicateIngredients = true;
	
	/**
	 * This list saves the result
	 */
	public static List<CtCodeElement> spaceElements = new ArrayList<CtCodeElement>();

	public void add(CtCodeElement st) {
		CtCodeElement code = st; 
		if (mustClone()) {
			code = MutationSupporter.clone(st);
		}
		if (allowsDuplicateIngredients ||  !contains(st)) {
			spaceElements.add(code);
		}
	}

	public boolean contains(CtCodeElement st) {
		for (CtCodeElement ce : spaceElements) {
			if (ce.toString().equals(st.toString())) {
				return true;
			}
		}
		return false;
	}

	public boolean mustClone() {
		return mustClone;
	}
	
			
}
