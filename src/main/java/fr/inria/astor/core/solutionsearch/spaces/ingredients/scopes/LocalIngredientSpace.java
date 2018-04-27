package fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes;

import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtType;
/**
 * Create the space using classes affected by the variants
 * @author Matias Martinez
 *
 */
public class LocalIngredientSpace extends AstorCtIngredientSpace{

	public LocalIngredientSpace(TargetElementProcessor<?> processor)
			throws JSAPException {
		super(processor);
	}

	public LocalIngredientSpace(List<TargetElementProcessor<?>> processors)
			throws JSAPException {
		super(processors);
	}

	public LocalIngredientSpace() throws JSAPException {
		super();
	
	}
	

	@Override
	public void defineSpace(ProgramVariant variant) {
		List<CtType<?>> affected = variant.getAffectedClasses();
			for (CtType<?> CtType : affected) {
					this.createFixSpaceFromAClass(CtType);
			}
				
	}

	@Override
	protected String getType(CtCodeElement element) {
		
		return element.getClass().getSimpleName();
	}

	@Override
	public IngredientSpaceScope spaceScope() {
		return IngredientSpaceScope.LOCAL;
	}

	@Override
	public String calculateLocation(CtElement original) {
		
		if(original instanceof CtType<?>)
			return ((CtType) original).getQualifiedName();
		return original.getParent(CtType.class).getQualifiedName();
		
	}
	

}
