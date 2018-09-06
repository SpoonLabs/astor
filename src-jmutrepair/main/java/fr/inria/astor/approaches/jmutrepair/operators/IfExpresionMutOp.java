package fr.inria.astor.approaches.jmutrepair.operators;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.jmutrepair.MutantCtElement;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.solutionsearch.spaces.operators.AutonomousOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;

/**
 * 
 * @author Matias Martinez
 *
 */
@SuppressWarnings("rawtypes")
public class IfExpresionMutOp extends AutonomousOperator {

	MutatorComposite mutatorBinary = null;

	public IfExpresionMutOp() {
		super();
		this.mutatorBinary = new MutatorComposite(MutationSupporter.getFactory());
		this.mutatorBinary.getMutators().add(new RelationalBinaryOperatorMutator(MutationSupporter.getFactory()));
		this.mutatorBinary.getMutators().add(new LogicalBinaryOperatorMutator(MutationSupporter.getFactory()));
		this.mutatorBinary.getMutators().add(new NegationUnaryOperatorConditionMutator(MutationSupporter.getFactory()));

	}

	@Override
	public boolean applyChangesInModel(OperatorInstance operation, ProgramVariant p) {
		boolean successful = false;
		CtExpression rightTerm = null, leftTerm = null;
		try {

			CtExpression ctst = (CtExpression) operation.getOriginal();
			CtExpression fix = (CtExpression) operation.getModified();

			ctst.replace((CtExpression) fix);
			successful = true;
			operation.setSuccessfulyApplied((successful));

			log.debug(" applied: " + ctst.getParent().toString());

		} catch (Exception ex) {
			log.error("Error applying an operation, exception: " + ex.getMessage());
			operation.setExceptionAtApplied(ex);
			operation.setSuccessfulyApplied(false);
		}
		return true;
	}

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {
		return (point.getCodeElement() instanceof CtIf);
	}

	protected OperatorInstance createModificationInstance(ModificationPoint point, MutantCtElement fix)
			throws IllegalAccessException {
		CtIf targetIF = (CtIf) point.getCodeElement();
		OperatorInstance operation = new OperatorInstance();
		operation.setOriginal(targetIF.getCondition());
		operation.setOperationApplied(this);
		operation.setModificationPoint(point);
		operation.setModified(fix.getElement());

		return operation;
	}

	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {
		List<OperatorInstance> ops = new ArrayList<>();

		CtIf targetIF = (CtIf) modificationPoint.getCodeElement();

		List<MutantCtElement> mutations = getMutants(targetIF);

		for (MutantCtElement mutantCtElement : mutations) {
			OperatorInstance opInstance;
			try {
				opInstance = createModificationInstance(modificationPoint, mutantCtElement);
				if (opInstance != null)
					ops.add(opInstance);
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			}

		}
		return ops;
	}

	/** Return the list of CtElements Mutanted */
	private List<MutantCtElement> getMutants(CtIf targetIF) {
		List<MutantCtElement> mutations = null;
		mutations = this.mutatorBinary.execute(targetIF.getCondition());
		return mutations;
	}

	@Override
	public boolean undoChangesInModel(OperatorInstance opInstance, ProgramVariant p) {
		try {
			CtExpression ctst = (CtExpression) opInstance.getOriginal();
			CtExpression fix = (CtExpression) opInstance.getModified();
			fix.replace(ctst);

			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean updateProgramVariant(OperatorInstance opInstance, ProgramVariant p) {
		// TODO Auto-generated method stub
		return false;
	}

}
