package fr.inria.astor.approaches.tos.ingredients.processors;

import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtMethod;

/**
 * Processor that retrieves Statements, but excludes blocks, class method and
 * class. This processor is used for creating modification points and retrieve
 * elements that will conform the search space.
 * 
 * @author Matias Martinez
 *
 */
public class StatementFixSpaceProcessor extends TargetElementProcessor<CtStatement> {

	/**
	 * The default statement transformator is CTStamemeny
	 */
	public StatementFixSpaceProcessor() {
		super();
	}

	@Override
	public void process(CtStatement element) {
		if (!(element instanceof CtClass || element instanceof CtMethod) && (element.getParent() instanceof CtBlock)) {
			add(element);
		}
	}

}
