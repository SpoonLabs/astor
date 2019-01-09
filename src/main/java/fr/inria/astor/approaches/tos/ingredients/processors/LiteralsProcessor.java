package fr.inria.astor.approaches.tos.ingredients.processors;

import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import spoon.reflect.code.CtLiteral;

/**
 * 
 * @author Matias Martinez
 *
 */
@SuppressWarnings("rawtypes")
public class LiteralsProcessor extends TargetElementProcessor<CtLiteral> {

	@Override
	public void process(CtLiteral element) {
		add(element);
	}

}
