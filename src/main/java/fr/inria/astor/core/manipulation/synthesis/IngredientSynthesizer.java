package fr.inria.astor.core.manipulation.synthesis;

import java.util.List;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.solutionsearch.extension.AstorExtensionPoint;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;

/**
 * Interface for synthesizing ingredients
 * 
 * Called by SynthesisBasedTransformationStrategy
 *
 * @author Matias Martinez
 *
 */
public interface IngredientSynthesizer extends AstorExtensionPoint {

	/**
	 * Performs a synthesis for a given hole.
	 *
	 * expectedtype is null in case of CtStatement
	 *
	 */
	@SuppressWarnings("rawtypes")
	public List<CtElement> executeSynthesis(ModificationPoint modificationPoint, CtElement hole, CtType expectedtype,
			List<CtVariable> contextOfModificationPoint, ExecutionContext values);

}
