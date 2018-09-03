package fr.inria.astor.approaches.scaffold.scaffoldgeneration;

import java.io.File;
import java.util.List;

import fr.inria.astor.approaches.scaffold.ScaffoldRepairEngine;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtWhile;

public class ConditionRemoveTransform extends TransformStrategy {
	
	public ConditionRemoveTransform (ModificationPoint modPoint, int modificationPointIndex, MutationSupporter supporter, ProjectRepairFacade facade,
			ScaffoldRepairEngine engine) {
		super(modPoint, modificationPointIndex, supporter,facade,engine);
		pre = "CRT";
	}
	
	@Override
	public List<String> transform() {
		CtStatement targetStmt = (CtStatement) this.modificationPoint.getCodeElement();
		if (targetStmt instanceof CtIf)
			this.visitCtIf ((CtIf)targetStmt);
		else if (targetStmt instanceof CtWhile)
			this.visitCtWhile((CtWhile) targetStmt);
		return list;
	}
	
	@SuppressWarnings({ "rawtypes", "static-access" })
	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		super.visitCtBinaryOperator(operator);
		
		if(operator.getKind()==BinaryOperatorKind.AND||operator.getKind()==BinaryOperatorKind.OR) {
			CtExpression right = operator.getRightHandOperand();
			operator.setKind(BinaryOperatorKind.AND);
			CtLiteral<Boolean> literalvalue = this.mutSupporter.getFactory().Core().createLiteral();
			Boolean bval=true;
			literalvalue.setValue(bval);
			operator.setRightHandOperand(literalvalue);
			saveSketchAndSynthesize();
			operator.setRightHandOperand(right);
			resoreDiskFile();
			
			CtExpression left = operator.getLeftHandOperand();
			operator.setKind(BinaryOperatorKind.AND);
			operator.setLeftHandOperand(literalvalue);
			saveSketchAndSynthesize();
			operator.setLeftHandOperand(left);
			resoreDiskFile();
		}
	}

	@SuppressWarnings({ "static-access", "rawtypes", "unchecked" })
	public void visitCtIf(CtIf ifElement) {
		 
		super.visitCtIf(ifElement);
		CtExpression  cond = ifElement.getCondition();
		
		CtLiteral<Boolean> literalvalue = this.mutSupporter.getFactory().Core().createLiteral();
		Boolean bval=false;
		literalvalue.setValue(bval);
		
		CtBinaryOperator<?> newcond = this.mutSupporter.getFactory().Core().createBinaryOperator();
		newcond.setKind(BinaryOperatorKind.AND);
		newcond.setRightHandOperand(literalvalue);
		newcond.setLeftHandOperand(cond);
		
		ifElement.setCondition((CtExpression<Boolean>) newcond);
		saveSketchAndSynthesize();
		ifElement.setCondition(cond);
		resoreDiskFile();
	}
	
	@Override
	public void visitCtWhile(CtWhile whileLoop) {
		super.visitCtWhile(whileLoop);
	}
}