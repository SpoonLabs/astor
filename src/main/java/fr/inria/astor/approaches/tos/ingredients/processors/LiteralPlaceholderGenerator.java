package fr.inria.astor.approaches.tos.ingredients.processors;

import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.tos.core.LiteralsProcessor;
import fr.inria.astor.approaches.tos.entity.placeholders.LiteralPlaceholder;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientProcessor;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;

/**
 * 
 * @author Matias Martinez
 *
 */
@SuppressWarnings("rawtypes")
public class LiteralPlaceholderGenerator implements PlaceholderGenerator {
	
	IngredientProcessor<?, CtLiteral> ip = null;

	public LiteralPlaceholderGenerator() {

		try {
			ip = new IngredientProcessor<>(new LiteralsProcessor());
		} catch (JSAPException e) {
			e.printStackTrace();
		}
	}

	@Override
	public List<LiteralPlaceholder> createTOS(CtStatement ingredientStatement) {
		List<LiteralPlaceholder> results = new ArrayList<>(); 
		boolean mustclone = false;
		List<CtLiteral> literals = ip.createFixSpace(ingredientStatement, mustclone);
		for (CtLiteral ctLiteral : literals) {
			//TODO:
			int i = 0;
			LiteralPlaceholder lp = new LiteralPlaceholder("_l_"+ctLiteral.getType().getSimpleName()+"_"+i, ctLiteral);
			results.add(lp);
		}
		
		return results;
	}

}
