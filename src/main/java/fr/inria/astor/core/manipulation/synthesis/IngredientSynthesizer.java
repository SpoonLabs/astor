package fr.inria.astor.core.manipulation.synthesis;

import java.util.List;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.solutionsearch.extension.AstorExtensionPoint;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;

/**
 * Interface for synthesizing ingredients
 * 
 * @author Matias Martinez
 *
 */
public interface IngredientSynthesizer extends AstorExtensionPoint {

	@SuppressWarnings("rawtypes")
	public List<Ingredient> executeSynthesis(ModificationPoint modificationPoint, CtElement hole, CtType expectedtype,
			List<CtVariable> contextOfModificationPoint, DynamicCollectedValues values);

}
