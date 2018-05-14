package fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes;

import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

/**
 * 
 * @author Matias Martinez matias.martinez@inria.fr
 *
 */
public class PackageBasicFixSpace extends LocalIngredientSpace {

	public PackageBasicFixSpace(TargetElementProcessor<?> processor) throws JSAPException {
		super(processor);

	}

	public PackageBasicFixSpace(List<TargetElementProcessor<?>> processor) throws JSAPException {
		super(processor);

	}

	@Override
	public String calculateLocation(CtElement original) {

		return original.getParent(CtPackage.class).getQualifiedName();
	}

	@Override
	public void defineSpace(ProgramVariant variant) {
		List<CtType<?>> affected = variant.getAffectedClasses();

		List<CtPackage> packageAnalyzed = new ArrayList<>();
		for (CtType<?> ing : affected) {

			CtPackage p = ing.getParent(CtPackage.class);
			if (!packageAnalyzed.contains(p)) {
				packageAnalyzed.add(p);
				for (CtType<?> t : p.getTypes()) {
					this.createFixSpaceFromAClass(t);
				}

			}

		}

	}

	@Override
	public IngredientPoolScope spaceScope() {
		return IngredientPoolScope.PACKAGE;
	}
	
	

}
