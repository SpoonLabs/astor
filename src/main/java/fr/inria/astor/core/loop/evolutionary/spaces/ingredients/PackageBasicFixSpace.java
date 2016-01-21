package fr.inria.astor.core.loop.evolutionary.spaces.ingredients;

import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

/**
 * 
 * @author Matias Martinez matias.martinez@inria.fr
 *
 */
public class PackageBasicFixSpace extends LocalFixSpace {

	public PackageBasicFixSpace(AbstractFixSpaceProcessor<?> processor) throws JSAPException {
		super(processor);

	}

	public PackageBasicFixSpace(List<AbstractFixSpaceProcessor<?>> processor) throws JSAPException {
		super(processor);

	}

	@Override
	protected String convertKey(CtElement original) {

		return original.getParent(CtPackage.class).getQualifiedName();
	}

	@Override
	public void defineSpace(List<CtType<?>> affected) {

		List<CtPackage> packageAnalyzed = new ArrayList<>();
		for (CtType<?> ing : affected) {

			CtPackage p = ing.getParent(CtPackage.class);
			if (!packageAnalyzed.contains(p)) {
				packageAnalyzed.add(p);
				List<CtType<?>> sts = new ArrayList<>();
				for(CtType<?> t : p.getTypes()){
					if(!t.getQualifiedName().toLowerCase().contains("test")){
						sts.add(t);
					}
				}
				super.defineSpace(sts);
			}

		}

	}

	@Override
	public IngredientSpaceStrategy strategy() {
		return IngredientSpaceStrategy.PACKAGE;
	}

}
