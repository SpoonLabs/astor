package fr.inria.astor.approaches.tos.ingredients;

import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.tos.core.LiteralsProcessor;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientProcessor;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.AstorCtIngredientSpace;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientSpaceScope;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

/**
 * 
 * @author Matias Martinez
 *
 */
public class LiteralsSpace extends AstorCtIngredientSpace {
	protected IngredientSpaceScope scope;

	public LiteralsSpace(IngredientSpaceScope scope) throws JSAPException {
		super();
		this.scope = scope;
		this.setIngredientProcessor(new IngredientProcessor<>(new LiteralsProcessor()));
	}

	@Override
	public void defineSpace(ProgramVariant variant) {
		List<CtType<?>> types = obtainClassesFromScope(variant);
		for (CtType type : types) {
			this.createFixSpaceFromAClass(type);
		}
	}

	protected List<CtType<?>> obtainClassesFromScope(ProgramVariant variant) {

		if (IngredientSpaceScope.PACKAGE.equals(scope)) {
			List<CtType<?>> affected = variant.getAffectedClasses();
			List<CtType<?>> types = new ArrayList<>();
			List<CtPackage> packageAnalyzed = new ArrayList<>();
			for (CtType<?> ing : affected) {

				CtPackage p = ing.getParent(CtPackage.class);
				if (!packageAnalyzed.contains(p)) {
					packageAnalyzed.add(p);
					for (CtType<?> type : p.getTypes()) {
						types.add(type);
					}
				}
			}
			return types;
		}
		if (IngredientSpaceScope.LOCAL.equals(scope)) {
			return variant.getAffectedClasses();
		}
		if (IngredientSpaceScope.GLOBAL.equals(scope)) {
			return MutationSupporter.getFactory().Type().getAll();
		}
		return null;
	}

	@Override
	public IngredientSpaceScope spaceScope() {
		return this.scope;
	}

	@Override
	public String calculateLocation(CtElement elementToModify) {

		if (IngredientSpaceScope.PACKAGE.equals(scope)) {
			return elementToModify.getParent(CtPackage.class).getQualifiedName();
		} else if (IngredientSpaceScope.LOCAL.equals(scope)) {
			return elementToModify.getParent(CtType.class).getQualifiedName();
		} else if (IngredientSpaceScope.GLOBAL.equals(scope))
			return "Global";

		return null;

	}

	@Override
	protected String getType(Ingredient ingredient) {
		// before was new code
		return ingredient.getCode().getClass().getSimpleName();
	}

}
