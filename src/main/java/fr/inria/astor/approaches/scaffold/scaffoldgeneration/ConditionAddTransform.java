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
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtVariable;

public class ConditionAddTransform extends TransformStrategy {
	String querytype;
	
	public ConditionAddTransform (ModificationPoint modPoint,  int modificationPointIndex, MutationSupporter supporter, ProjectRepairFacade facade
			,ScaffoldRepairEngine engine) {
		super(modPoint, modificationPointIndex, supporter,facade,engine);
		pre = "CAT";
		querytype="COND";
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public List<String> transform() {
		
		try {
			CtStatement targetStmt = (CtStatement) this.modificationPoint.getCodeElement().getParent();
			if (targetStmt instanceof CtBlock)
				visitCtBlock((CtBlock) targetStmt);
		 }catch (Exception e) {
				log.debug("Exception happens when geting the parent statement of the suspicious statement");
		 }
		 return list;
	}

	@SuppressWarnings("rawtypes")
	@Override
	public <R> void visitCtBlock(CtBlock<R> block) {
		int id = 0;
		List<CtStatement>  statlist = block.getStatements();
		boolean flag = false;
		for (; id < statlist.size(); id++)
			if (statlist.get(id).equals((CtStatement) this.modificationPoint.getCodeElement())) {
				flag = true;
				break;
			}
		if (!flag)
			return;
		
		List<CtVariable> varsavilable = modificationPoint.getContextOfModificationPoint();
		PriorityQueue<CtVariable> queue = new PriorityQueue<CtVariable>(new Comparator<CtVariable>() {
			@Override
			public int compare(CtVariable o1, CtVariable o2) {
				return o2.getPosition().getLine()-o1.getPosition().getLine();
			}
		});	
		queue.addAll(varsavilable);
		
		List<String> types = new ArrayList<String>();
		while (!queue.isEmpty()) {
			CtVariable var = queue.poll();
		    String type = var.getType().getQualifiedName();
			type = type.replaceAll("\\d","");
			if (!types.contains(type)) {
				types.add(type);
				log.info(type);
				writeCondition(type, block, id);
				writeIfReturn(type, block, id);
			}
		}
	}
	
	@SuppressWarnings({ "static-access", "unchecked", "rawtypes" })
	private void writeCondition(String type, CtBlock parent, int id) {
		CtExpression conditionExp = ExpressionGenerator.fetchEXP(this.mutSupporter, this.modificationPoint, type, querytype);
        
		CtIf ifStmt = this.mutSupporter.getFactory().Core().createIf();
		ifStmt.setCondition(conditionExp);
		ifStmt.setThenStatement((CtStatement)this.modificationPoint.getCodeElement());
		parent.addStatement(id, ifStmt);
		parent.removeStatement((CtStatement)this.modificationPoint.getCodeElement());
		saveSketchAndSynthesize();
		parent.addStatement(id, (CtStatement)this.modificationPoint.getCodeElement());
		parent.removeStatement(ifStmt);
		resoreDiskFile();
	}

	@SuppressWarnings({ "rawtypes", "static-access", "unchecked" })
	private void writeIfReturn(String type, CtBlock parent, int id) {
		
		CtExpression conditionExp = ExpressionGenerator.fetchEXP(this.mutSupporter, this.modificationPoint, type, querytype);

		CtIf ifstmt = this.mutSupporter.getFactory().Core().createIf();
		ifstmt.setCondition(conditionExp);
		
		CtElement parentmethod=parent;
		
		do {
			parentmethod=parentmethod.getParent();
		} while (!(parentmethod instanceof CtMethod));
		
		String returntype="void";
		returntype=((CtMethod)parentmethod).getType().getQualifiedName();
		returntype = returntype.replaceAll("\\d","");
		
		CtReturn returnstmt = this.mutSupporter.getFactory().Core().createReturn();

		if (!returntype.toLowerCase().equals("void")) {
			CtExpression returnexp = ExpressionGenerator.fetchEXP(this.mutSupporter, this.modificationPoint, returntype, "EXP");
			returnstmt.setReturnedExpression(returnexp);
		}
		
		ifstmt.setThenStatement(returnstmt);
		
		parent.addStatement(id, ifstmt);
		saveSketchAndSynthesize();
		parent.removeStatement(ifstmt);
		resoreDiskFile();
	}
}