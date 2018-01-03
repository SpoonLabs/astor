package fr.inria.astor.core.manipulation.filters;

import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCFlowBreak;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtWhile;

/**
 * Processor that retrieves Statements, but excludes blocks, class method and
 * class. Udpate: I improved this by only adding statements with a Block parent
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class SpecialStatementFixSpaceProcessor extends TargetElementProcessor<CtStatement> {

	/**
	 * The default statement transformator is CTStamemeny
	 */
	public SpecialStatementFixSpaceProcessor() {
		super();
	}

	@Override
	public void process(CtStatement element) {

		if (element instanceof CtIf) {
			add(((CtIf) element).getCondition());
		} else if (element instanceof CtFor) {
			add(((CtFor) element).getExpression());
		} else if (element instanceof CtWhile) {
			add(((CtWhile) element).getLoopingExpression());
		} else if (element instanceof CtDo) {
			add(((CtDo) element).getLoopingExpression());
		} else if (element instanceof CtThrow) {
			add(((CtThrow) element).getThrownExpression());
		} else if (element instanceof CtInvocation && (element.getParent() instanceof CtBlock)) {
			add(element);
		} else if (element instanceof CtAssignment || element instanceof CtConstructorCall
				|| element instanceof CtCFlowBreak || element instanceof CtLocalVariable) {
			add(element);
		}

	}

	@Override
	public void add(CtCodeElement st) {

		if (st == null || st.getParent() == null) {
			return;
		}

		if (allowsDuplicateIngredients || !contains(st)) {
			CtCodeElement code = st;

			if (mustClone()) {
				code = MutationSupporter.clone(st);
			}
			spaceElements.add(code);
		}
	}
}
