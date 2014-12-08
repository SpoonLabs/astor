package fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor;

import java.util.ArrayList;
import java.util.List;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtCodeElement;
import fr.inria.astor.core.loop.evolutionary.transformators.ModelTransformator;
import fr.inria.astor.core.setup.MutationSupporter;

/**
 * Spoon processor to create a Fix Space for a given CtElement <T> Update: we
 * clone the original element before adding
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 * @param <T>
 */
public abstract class AbstractFixSpaceProcessor<T extends CtCodeElement> extends AbstractProcessor<T> {

	
	public ModelTransformator transformator;
	
	public static boolean mustClone = true;
	
	/**
	 * This list saves the result
	 */
	public static List<CtCodeElement> spaceElements = new ArrayList<CtCodeElement>();

	public void add(CtCodeElement st) {
		if (mustClone()) {
			if (!contains(st)) {
				CtCodeElement cloned = MutationSupporter.clone(st);
				spaceElements.add(cloned);
			}
		} else {
			spaceElements.add(st);
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
	
	public void canTransform(){
		
	}

	public ModelTransformator getTransformator() {
		return transformator;
	}

	public void setTransformator(ModelTransformator transformator) {
		this.transformator = transformator;
	}
			
}
