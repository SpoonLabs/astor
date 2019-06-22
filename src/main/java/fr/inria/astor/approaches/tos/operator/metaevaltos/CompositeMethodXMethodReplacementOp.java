package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.meta.MetaOperatorInstance;
import fr.inria.astor.util.MapList;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class CompositeMethodXMethodReplacementOp extends MethodXMethodReplacementOp {

	List<MethodXMethodReplacementOp> composite = new ArrayList<>();

	public CompositeMethodXMethodReplacementOp() {
		composite.add(new MethodXMethodReplacementArgumentRemoveOp());
		composite.add(new MethodXMethodReplacementDiffArgumentsOp());
		composite.add(new MethodXMethodReplacementDiffNameOp());
	}

	@Override
	public List<MetaOperatorInstance> createMetaOperatorInstances(ModificationPoint modificationPoint) {

		List<MetaOperatorInstance> opsMega = new ArrayList();

		for (MethodXMethodReplacementOp methodXMethodReplacementOp : composite) {
			List<MetaOperatorInstance> op1 = methodXMethodReplacementOp.createMetaOperatorInstances(modificationPoint);
			if (op1 != null) {
				opsMega.addAll(op1);
			}
		}

		return opsMega;
	}

	@Override
	public OperatorInstance getConcreteOperatorInstance(MetaOperatorInstance operatorInstance, int metaIdentifier) {

		return super.getConcreteOperatorInstance(operatorInstance, metaIdentifier);
	}

	@Override
	public boolean canBeAppliedToPoint(ModificationPoint point) {

		return super.canBeAppliedToPoint(point);
	}

	@Override
	public void setTargetElement(CtElement target) {
		this.targetElement = target;
		for (MethodXMethodReplacementOp methodXMethodReplacementOp : composite) {
			methodXMethodReplacementOp.setTargetElement(target);
		}

	}

	@Override
	public boolean checkTargetCompatibility(CtElement target) {

		return target instanceof CtInvocation;
	}

	protected List<CtInvocation> getInvocations(CtElement suspiciousElement) {
		List<CtInvocation> invocations = new ArrayList<>();
		for (MethodXMethodReplacementOp methodXMethodReplacementOp : composite) {
			List<CtInvocation> inv = methodXMethodReplacementOp.getInvocations(suspiciousElement);
			if (inv != null) {
				invocations.addAll(inv);
			}
		}
		return invocations;
	}

	@Override
	public String identifier() {

		return "Method_RW_Method";
	}

	@Override
	public MapList<CtInvocation, Ingredient> retrieveInvocationIngredient(ModificationPoint point) {
		// TODO Auto-generated method stub
		return null;
	}

}
