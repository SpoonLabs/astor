package fr.inria.astor.core.loop.evolutionary.spaces.ingredients;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
import fr.inria.astor.core.manipulation.MutationSupporter;

/**
 * This Fix Space takes uniform randomly elements from the the search space. It
 * requires that all classes to parser (i.e., that provide material to the
 * space) be processed to the use of the space.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class GlobalBasicFixSpace extends BasicFixSpace {

	private Logger logger = Logger.getLogger(GlobalBasicFixSpace.class
			.getName());

	public GlobalBasicFixSpace(AbstractFixSpaceProcessor<?> processor)
			throws JSAPException {
		super(processor);

	}

	public GlobalBasicFixSpace(List<AbstractFixSpaceProcessor<?>> processor)
			throws JSAPException {
		super(processor);

	}

	@Override
	public CtCodeElement getElementFromSpace(CtElement element) {
		CtCodeElement originalPicked = getRandomStatementFromSpace(getFixSpace(null));
		return MutationSupporter.clone(originalPicked);

	}

	/**
	 * Ignored the class
	 */
	@Override
	public List<CtCodeElement> getFixSpace(CtElement element) {
		List result = 	new ArrayList();
		for(String type : fixSpaceByType.keySet()){
			result.addAll(this.fixSpaceByType.get(type));
		}
		
		return result;
	}

	@Override
	public List<CtCodeElement> getFixSpace(CtElement element, String type) {

		return this.fixSpaceByType.get(type);

	}

	@Override
	public CtCodeElement getElementFromSpace(CtElement element, String type) {
		CtCodeElement originalPicked = getRandomStatementFromSpace(getFixSpace(
				null, type));
		return MutationSupporter.clone(originalPicked);
	}
	
	@Override
	public IngredientSpaceStrategy strategy() {
		return IngredientSpaceStrategy.GLOBAL;
	}

	public String toString() {
		String s = "";
		for (String l : this.fixSpaceByType.keySet()) {
			List ing = this.fixSpaceByType.get(l);
			s += "\t " + l + ": (" + ing.size() + ") " + ing + "\n";

		}
		return s;
	}

}
