package fr.inria.astor.approaches.tos.ingredients.processors;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.tos.entity.placeholders.VarLiPlaceholder;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;

/**
 * 
 * @author Matias Martinez
 *
 */
public class VarLiPlaceholderGenerator implements PlaceholderGenerator {

	@Override
	public List<VarLiPlaceholder> createTOS(CtStatement ingredientStatement) {
		List<VarLiPlaceholder> results = new ArrayList<>(); 
		List<CtVariableAccess> varAccessCollected = VariableResolver.collectVariableAccess(ingredientStatement, true);
		for (CtVariableAccess ctVariableAccess : varAccessCollected) {
			int i = 0;
			if(ctVariableAccess instanceof CtFieldRead || ctVariableAccess instanceof CtVariableRead){
				VarLiPlaceholder pc = new VarLiPlaceholder(ctVariableAccess, "_lit_"+ctVariableAccess.getType().getSimpleName()+ "_"+i);
				results.add(pc);
			}
		}
		return results;
	}

}
