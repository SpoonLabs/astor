package fr.inria.astor.approaches.tos.ingredients.processors;

import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.tos.entity.placeholders.LiteralPlaceholder;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.CodeParserLauncher;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
@SuppressWarnings("rawtypes")
public class LiteralPlaceholderGenerator<T extends CtElement> implements PlaceholderGenerator<T> {
	
	CodeParserLauncher<?, CtLiteral> ip = null;

	public LiteralPlaceholderGenerator() {

		try {
			ip = new CodeParserLauncher<>(new LiteralsProcessor());
		} catch (JSAPException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<LiteralPlaceholder> createTOS(T ingredientSource) {
		List<LiteralPlaceholder> results = new ArrayList<>(); 
		boolean mustclone = false;
		List<CtLiteral> literals = ip.createFixSpace(ingredientSource, mustclone);
		for (CtLiteral ctLiteral : literals) {
			//TODO:
			int i = 0;
			LiteralPlaceholder lp = new LiteralPlaceholder("_l_"+ctLiteral.getType().getSimpleName()+"_"+i, ctLiteral);
			results.add(lp);
		}
		
		return results;
	}

}
