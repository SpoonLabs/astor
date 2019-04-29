package fr.inria.astor.approaches.tos.operator.metaevaltos.simple;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.inria.astor.approaches.tos.operator.metaevaltos.MetaGenerator;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.reference.CtExecutableReference;

/**
 * 
 * @author Matias Martinez
 *
 */
public class SimpleMethodReplacement extends OperatorInstance {

	CtInvocation previousOriginal = null;
	CtInvocation newExpression = null;

	Map<CtElement, CtElement> previousParent = new HashMap<>();
	List<CtExpression> ps = new ArrayList();

	public SimpleMethodReplacement(ModificationPoint modificationPoint, CtInvocation previousOriginal,
			CtInvocation newExpression, AstorOperator astoroperator) {
		super();
		this.previousOriginal = previousOriginal;
		this.newExpression = newExpression;

		super.setOperationApplied(astoroperator);
		super.setModificationPoint(modificationPoint);
	}

	CtExecutableReference previousExec = null;
	List previousArguments = null;

	@Override
	public boolean applyModification() {
		ps.clear();
		previousParent.clear();
//previousParent
		for (Object obi : newExpression.getArguments()) {
			CtExpression epArg = (CtExpression) obi;

			Object replacement = MetaGenerator.getSourceTarget().get(epArg);
			// Object replacement = MetaGenerator.targetSource.get(epArg);

			if (replacement != null) {
				log.debug("found a mega on invocation par " + epArg);
				ps.add((CtExpression) replacement);
			} else {
				ps.add(epArg);
			}

		}

		for (CtExpression object : ps) {
			try {
				previousParent.put(object, object.getParent());
			} catch (Exception e) {
				log.debug("Parent not init " + object);
			}
		}

		previousExec = this.previousOriginal.getExecutable();
		previousArguments = new ArrayList<>(previousOriginal.getArguments());

		this.previousOriginal.setExecutable(newExpression.getExecutable());

		this.previousOriginal.setArguments(ps);
		// TODO: save previous parent to recover in undo
		// this.previousOriginal.setArguments(newExpression.getArguments());

		super.setOriginal(previousOriginal);
		// we just clone, just in case
		super.setModified(MutationSupporter.clone(previousOriginal));

		return true;
	}

	@Override
	public boolean undoModification() {

		this.previousOriginal.setExecutable(this.previousExec);
		// TODO: this fails
		this.previousOriginal.setArguments(this.previousArguments);

		for (CtElement params : this.previousParent.keySet()) {
			params.setParent(this.previousParent.get(params));
		}

		return true;
	}

}
