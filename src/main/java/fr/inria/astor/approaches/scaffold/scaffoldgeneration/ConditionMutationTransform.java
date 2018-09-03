package fr.inria.astor.approaches.scaffold.scaffoldgeneration;

import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

import fr.inria.astor.approaches.scaffold.ScaffoldRepairEngine;
import fr.inria.astor.approaches.scaffold.scaffoldgeneration.generator.ExpressionGenerator;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtVariable;

public class ConditionMutationTransform extends TransformStrategy {
	String querytype;
	ArrayList<CtBinaryOperator<?>> usedBinExper;
	
	public ConditionMutationTransform (ModificationPoint modPoint, int modificationPointIndex, MutationSupporter supporter, ProjectRepairFacade facade
			,ScaffoldRepairEngine engine) {
		super(modPoint, modificationPointIndex, supporter,facade,engine);
		pre = "CMT";
		querytype="COND";
		usedBinExper=new ArrayList<CtBinaryOperator<?>>();
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
	
	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		super.visitCtBinaryOperator(operator);
		
		if(operator.getKind()==BinaryOperatorKind.AND||operator.getKind()==BinaryOperatorKind.OR) {
			fetchCOND(operator);
		}
	}

	@Override
	public void visitCtIf(CtIf ifElement) {
		super.visitCtIf(ifElement);
		fetchCOND(ifElement);
	}
	
	@Override
	public void visitCtWhile(CtWhile whileLoop) {
		super.visitCtWhile(whileLoop);
		fetchCOND(whileLoop);
	}

	@SuppressWarnings("rawtypes")
	private void fetchCOND(CtElement node) {
		List<String> types = new ArrayList<String>();
		List<CtVariable> vars = modificationPoint.getContextOfModificationPoint();
		PriorityQueue<CtVariable> queue = new PriorityQueue<CtVariable>(new Comparator<CtVariable>() {
			@Override
			public int compare(CtVariable o1, CtVariable o2) {
				return o2.getPosition().getLine()-o1.getPosition().getLine();
			}
		});	
		queue.addAll(vars);
				
		while (!queue.isEmpty()) {
			CtVariable var = queue.poll();
		    String type = var.getType().getQualifiedName();
			type = type.replaceAll("\\d","");
			if (!types.contains(type)) {
				types.add(type);
				log.info(type);
				writeCondition(type, node);
			}
		}
	}

	@SuppressWarnings({ "rawtypes", "static-access", "unchecked" })
	private void writeCondition(String type, CtElement node) {
		
		CtExpression methdodinvocation = ExpressionGenerator.fetchEXP(this.mutSupporter, this.modificationPoint, type, querytype);
		
		if (node instanceof CtIf) {
			CtIf stmt = (CtIf) node;
			CtExpression cond = stmt.getCondition();
			
			CtBinaryOperator<?> newcond = this.mutSupporter.getFactory().Core().createBinaryOperator();
			newcond.setKind(BinaryOperatorKind.AND);
			newcond.setRightHandOperand(methdodinvocation);
			newcond.setLeftHandOperand(cond);
			if(!usedBinExper.contains(newcond)) {
			   usedBinExper.add(newcond);
			   stmt.setCondition((CtExpression<Boolean>) newcond);
			   saveSketchAndSynthesize();
			   stmt.setCondition(cond);
			   resoreDiskFile();
			}
			
			newcond.setKind(BinaryOperatorKind.OR);
			newcond.setRightHandOperand(methdodinvocation);
			newcond.setLeftHandOperand(cond);
			if(!usedBinExper.contains(newcond)) {
				usedBinExper.add(newcond);
			    stmt.setCondition((CtExpression<Boolean>) newcond);
			    saveSketchAndSynthesize();
			    stmt.setCondition(cond);
				resoreDiskFile();
			}
		} else if (node instanceof CtWhile) {
			CtWhile stmt = (CtWhile) node;
			CtExpression cond = stmt.getLoopingExpression();
	
			CtBinaryOperator<?> newcond = this.mutSupporter.getFactory().Core().createBinaryOperator();
			newcond.setKind(BinaryOperatorKind.AND);
			newcond.setRightHandOperand(methdodinvocation);
			newcond.setLeftHandOperand(cond);
			if(!usedBinExper.contains(newcond)) {
			   usedBinExper.add(newcond);
			   stmt.setLoopingExpression((CtExpression<Boolean>) newcond);
			   saveSketchAndSynthesize();
			   stmt.setLoopingExpression(cond);
				resoreDiskFile();
			}
			
			newcond.setKind(BinaryOperatorKind.OR);
			newcond.setRightHandOperand(methdodinvocation);
			newcond.setLeftHandOperand(cond);
			if(!usedBinExper.contains(newcond)) {
				usedBinExper.add(newcond);
			    stmt.setLoopingExpression((CtExpression<Boolean>) newcond);
			    saveSketchAndSynthesize();
			    stmt.setLoopingExpression(cond);
				resoreDiskFile();
			}
		}
		else if (node instanceof CtBinaryOperator) {
			CtBinaryOperator expr = (CtBinaryOperator) node;
			
			CtExpression left = expr.getLeftHandOperand(); // example; a || b to a&&sketch||b
			CtBinaryOperator<?> newcond = this.mutSupporter.getFactory().Core().createBinaryOperator();
			newcond.setKind(BinaryOperatorKind.AND);
			newcond.setRightHandOperand(methdodinvocation);
			newcond.setLeftHandOperand(left);
			if(!usedBinExper.contains(newcond)) {
			   usedBinExper.add(newcond);
			   expr.setLeftHandOperand(newcond);
			   saveSketchAndSynthesize();
			   expr.setLeftHandOperand(left);
				resoreDiskFile();
			}
			
			newcond.setKind(BinaryOperatorKind.OR); // example; a || b to a|| sketch||b
			newcond.setRightHandOperand(methdodinvocation);
			newcond.setLeftHandOperand(left);
			if(!usedBinExper.contains(newcond)) {
			  usedBinExper.add(newcond);
			  expr.setLeftHandOperand(newcond);
			  saveSketchAndSynthesize();
			  expr.setLeftHandOperand(left);
				resoreDiskFile();
			}
			
			CtExpression right = expr.getRightHandOperand(); // example; a || b to a||b&&sketch
			if(expr.getKind()!=BinaryOperatorKind.AND) {
			  newcond.setKind(BinaryOperatorKind.AND);
			  newcond.setRightHandOperand(methdodinvocation);
			  newcond.setLeftHandOperand(right);
			  if(!usedBinExper.contains(newcond)) {
			    usedBinExper.add(newcond);
			    expr.setRightHandOperand(newcond);
			    saveSketchAndSynthesize();
			    expr.setRightHandOperand(right);
				resoreDiskFile();
			  }
			}
			
			if(expr.getKind()!=BinaryOperatorKind.OR) {
			   newcond.setKind(BinaryOperatorKind.OR); // example; a || b to a||b|| sketch
			   newcond.setRightHandOperand(methdodinvocation);
			   newcond.setLeftHandOperand(right);
			   if(!usedBinExper.contains(newcond)) {
				  usedBinExper.add(newcond);
			      expr.setRightHandOperand(newcond);
			      saveSketchAndSynthesize();
			      expr.setRightHandOperand(right);
					resoreDiskFile();
			   }
			}
		}
	}
}