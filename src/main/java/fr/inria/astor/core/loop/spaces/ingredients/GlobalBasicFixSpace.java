package fr.inria.astor.core.loop.spaces.ingredients;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.AbstractFixSpaceProcessor;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;

/**
 * This Fix Space takes uniform randomly elements from the the search space. It
 * requires that all classes to parser (i.e., that provide material to the
 * space) be processed to the use of the space.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class GlobalBasicFixSpace extends LocalFixSpace {

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
	 * Ignored the class, returns all elements from the space
	 */
	@Override
	public List<CtCodeElement> getFixSpace(CtElement element) {
		List result = 	new ArrayList();
		for(String type : fixSpaceByType.keySet()){
			result.addAll(this.fixSpaceByType.get(type));
		}
		
		return result;
	}
	/**
	 * Ignores the element, returns all types
	 */
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
	public IngredientSpaceScope spaceScope() {
		return IngredientSpaceScope.GLOBAL;
	}

	public String toString() {
		String s ="--Space: "+this.spaceScope() +"\n";
		int totalIng = 0;	
		for (String l : this.fixSpaceByType.keySet()) {
			List ing = this.fixSpaceByType.get(l);
			s += "\t " + l + ": (" + ing.size() + ") " +/* ing +*/ "\n";
			totalIng += ing.size();
		}
		s = " All ingredients: "+totalIng + s;
		return s;
	}
	
	@Override
	public void defineSpace(ProgramVariant variant) {
		List<CtType<?>> affected =  MutationSupporter.getFactory().Type().getAll();
			for (CtType<?> classToProcess : affected) {
					this.createFixSpaceFromAClass(classToProcess, classToProcess);
			}
				
	}

}
