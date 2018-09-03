package fr.inria.astor.approaches.scaffold.scaffoldgeneration;

import java.io.File;
import java.util.List;
import java.util.Set;
import java.util.Arrays;
import java.util.HashSet;

import fr.inria.astor.approaches.scaffold.ScaffoldRepairEngine;
import fr.inria.astor.approaches.scaffold.scaffoldgeneration.generator.OperatorGenerator;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;

public class OperatorTransform extends TransformStrategy {
	
	private Set<BinaryOperatorKind> rop = new HashSet<BinaryOperatorKind>(Arrays.asList(BinaryOperatorKind.EQ, 
			BinaryOperatorKind.NE, BinaryOperatorKind.LT, BinaryOperatorKind.GT, BinaryOperatorKind.LE, 
			BinaryOperatorKind.GE));
	
	private Set<BinaryOperatorKind> aop = new HashSet<BinaryOperatorKind>(Arrays.asList(BinaryOperatorKind.PLUS, 
			BinaryOperatorKind.MINUS, BinaryOperatorKind.MUL, BinaryOperatorKind.DIV, BinaryOperatorKind.MOD));
	
	public OperatorTransform (ModificationPoint modPoint, int modificationPointIndex, MutationSupporter supporter, ProjectRepairFacade facade
			,ScaffoldRepairEngine engine) {
		super(modPoint, modificationPointIndex, supporter,facade,engine);
		pre = "OT";
	}
	
	public List<String> transform() {
        return super.transform();
	}
	
	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		super.visitCtBinaryOperator(operator);
		@SuppressWarnings("rawtypes")
		CtExpression left = operator.getLeftHandOperand();
		
		String typelefthand=left.getType().getQualifiedName();
		String typeoperator=operator.getType().getQualifiedName();
		typelefthand = typelefthand.replaceAll("\\d","");
		typeoperator = typeoperator.replaceAll("\\d","");
		@SuppressWarnings("rawtypes")
		CtExpression exp = null;
		
		BinaryOperatorKind kind=operator.getKind();
		if(rop.contains(kind))
			exp = OperatorGenerator.fetchROP(operator, this.mutSupporter, this.modificationPoint, typelefthand, "ROP");
		else if(aop.contains(kind))
			exp = OperatorGenerator.fetchROP(operator, this.mutSupporter, this.modificationPoint, typeoperator, "AOP");
				
		if (exp != null)
			candidates.put(operator, exp);
		
		if (candidates.containsKey(left)) {
			operator.setLeftHandOperand(candidates.get(left));
			saveSketchAndSynthesize();
			operator.setLeftHandOperand(left);
			resoreDiskFile();
		}
		
		@SuppressWarnings("rawtypes")
		CtExpression right = operator.getRightHandOperand();
		if (candidates.containsKey(right)) {
			operator.setRightHandOperand(candidates.get(right));
			saveSketchAndSynthesize();
			operator.setRightHandOperand(right);
			resoreDiskFile();
		}
	}
}
