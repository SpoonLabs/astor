package fr.inria.astor.approaches.tos.ingredients;

import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.tos.ingredients.processors.LiteralsProcessor;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.CodeParserLauncher;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.AstorCtIngredientPool;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.scopes.IngredientPoolScope;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

/**
 * 
 * @author Matias Martinez
 *
 */
public class LiteralsSpace extends AstorCtIngredientPool {
	protected IngredientPoolScope scope;

	public LiteralsSpace(IngredientPoolScope scope) throws JSAPException {
		super();
		this.scope = scope;
		this.setIngredientProcessor(new CodeParserLauncher<>(new LiteralsProcessor()));
	}

	@Override
	public void defineSpace(ProgramVariant variant) {
		List<CtType<?>> types = obtainClassesFromScope(variant);
		for (CtType type : types) {
			this.createFixSpaceFromAClass(type);
		}
	}

	protected List<CtType<?>> obtainClassesFromScope(ProgramVariant variant) {

		if (IngredientPoolScope.PACKAGE.equals(scope)) {
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
		if (IngredientPoolScope.LOCAL.equals(scope)) {
			return variant.getAffectedClasses();
		}
		if (IngredientPoolScope.GLOBAL.equals(scope)) {
			return MutationSupporter.getFactory().Type().getAll();
		}
		return null;
	}

	@Override
	public IngredientPoolScope spaceScope() {
		return this.scope;
	}

	@Override
	public String calculateLocation(CtElement elementToModify) {

		if (IngredientPoolScope.PACKAGE.equals(scope)) {
			return elementToModify.getParent(CtPackage.class).getQualifiedName();
		} else if (IngredientPoolScope.LOCAL.equals(scope)) {
			return elementToModify.getParent(CtType.class).getQualifiedName();
		} else if (IngredientPoolScope.GLOBAL.equals(scope))
			return "Global";

		return null;

	}

	@Override
	public String getType(Ingredient ingredient) {
		// before was new code
		return ingredient.getCode().getClass().getSimpleName();
	}

}
