package fr.inria.astor.approaches.tos.ingredients.processors;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.tos.entity.placeholders.VarLiPlaceholder;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtFieldReference;

/**
 * 
 * @author Matias Martinez
 *
 */
public class VarLiPlaceholderGenerator<T extends CtElement> implements PlaceholderGenerator<T> {

	@Override
	public List<VarLiPlaceholder> createTOS(T ingredientStatement) {
		List<VarLiPlaceholder> results = new ArrayList<>();
		List<CtVariableAccess> varAccessCollected = VariableResolver.collectVariableAccess(ingredientStatement, true);
		for (CtVariableAccess ctVariableAccess : varAccessCollected) {

			int i = 0;

			if (ctVariableAccess instanceof CtVariableRead) {

				if (!(ctVariableAccess instanceof CtFieldRead)
						|| !((CtFieldReference) ctVariableAccess.getVariable()).isStatic()) {

					VarLiPlaceholder pc = new VarLiPlaceholder(ctVariableAccess,
							"_lit_" + ctVariableAccess.getType().getSimpleName() + "_" + i);
					results.add(pc);
				}
			}
		}
		return results;
	}

}
