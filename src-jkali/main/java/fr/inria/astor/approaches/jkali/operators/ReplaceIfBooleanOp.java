package fr.inria.astor.approaches.jkali.operators;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.approaches.jgenprog.operators.StatementLevelOperator;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.solutionsearch.spaces.operators.AutonomousOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ReplaceIfBooleanOp extends AutonomousOperator implements StatementLevelOperator {

	ReplaceOp replaceOp = new ReplaceOp();

	public ReplaceIfBooleanOp() {
		super();
	}

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {

		return (point.getCodeElement() instanceof CtIf);
	}

	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {
		List<OperatorInstance> instances = new ArrayList<>();

		OperatorInstance opChangeIftrue = new StatementOperatorInstance(modificationPoint, this,
				modificationPoint.getCodeElement(), createIf((CtIf) modificationPoint.getCodeElement(), true));

		instances.add(opChangeIftrue);

		OperatorInstance opChangeIffalse = new StatementOperatorInstance(modificationPoint, this,
				modificationPoint.getCodeElement(), createIf((CtIf) modificationPoint.getCodeElement(), false));

		instances.add(opChangeIffalse);

		return instances;
	}

	/**
	 * Creates a new if from that one passed as parammeter. The next if has a
	 * condition expression expression true or false according to the variable
	 * <b>thenBranch</b>
	 * 
	 * @param ifElement
	 * @param thenBranch
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked", })
	private CtIf createIf(CtIf ifElement, boolean thenBranch) {

		CtIf clonedIf = MutationSupporter.getFactory().Core().clone(ifElement);
		CtExpression ifExpression = MutationSupporter.getFactory().Code()
				.createCodeSnippetExpression(Boolean.toString(thenBranch));

		clonedIf.setCondition(ifExpression);

		return clonedIf;
	}

	@Override
	public boolean applyChangesInModel(OperatorInstance operation, ProgramVariant p) {

		return replaceOp.applyChangesInModel(operation, p);

	}

	@Override
	public boolean undoChangesInModel(OperatorInstance opInstance, ProgramVariant p) {
		return replaceOp.undoChangesInModel(opInstance, p);
	}

	@Override
	public boolean updateProgramVariant(OperatorInstance opInstance, ProgramVariant p) {
		return replaceOp.updateProgramVariant(opInstance, p);
	}

}
