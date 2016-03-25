package fr.inria.astor.approaches.jkali.operators;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.ModificationInstance;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;

public class ReplaceIfBooleanOp extends ReplaceOp{
	
	
	public ReplaceIfBooleanOp() {
		super();
	}
	@Override
	public boolean applyToPoint(ModificationPoint point) {
		
		return (point.getCodeElement() instanceof CtIf);
	}
	
	
	@Override
	public List<ModificationInstance> createModificationInstance(SuspiciousModificationPoint  modificationPoint){
		List<ModificationInstance> instances = new ArrayList<>();
		
		ModificationInstance opChangeIftrue = new ModificationInstance(modificationPoint, this,
				modificationPoint.getCodeElement(), createIf((CtIf) modificationPoint.getCodeElement(), true));
		opChangeIftrue.defineParentInformation(modificationPoint);
		
		instances.add(opChangeIftrue);
		
		ModificationInstance opChangeIffalse = new ModificationInstance(modificationPoint, this,
				modificationPoint.getCodeElement(), createIf((CtIf) modificationPoint.getCodeElement(), false));
		opChangeIffalse.defineParentInformation(modificationPoint);
		
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
	@SuppressWarnings({ "static-access", "rawtypes", "unchecked", })
	private CtIf createIf(CtIf ifElement, boolean thenBranch) {

		CtIf clonedIf = MutationSupporter.getFactory().Core().clone(ifElement);
		CtExpression ifExpression = MutationSupporter.getFactory().Code()
				.createCodeSnippetExpression(Boolean.toString(thenBranch));

		clonedIf.setCondition(ifExpression);

		return clonedIf;
	}
	
	
}
