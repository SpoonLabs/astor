package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.StatementOperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtCatch;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtTry;
import spoon.support.reflect.code.CtBlockImpl;

/**
 * 
 * @author Matias Martinez
 *
 */
public class WrapwithTrySingleStatementOp extends ReplaceOp {

	@Override
	public List<OperatorInstance> createOperatorInstances(ModificationPoint modificationPoint) {
		List<OperatorInstance> opInstances = new ArrayList<>();

		CtStatement statementPointed = (CtStatement) modificationPoint.getCodeElement();

		CtTry tryNew = MutationSupporter.getFactory().createTry();
		List<CtCatch> catchers = new ArrayList<>();
		CtCatch catchEx1 = MutationSupporter.getFactory().createCtCatch("e", Exception.class, new CtBlockImpl());
		catchers.add(catchEx1);
		tryNew.setCatchers(catchers);
		CtBlock tryBoddy = new CtBlockImpl();
		tryNew.setBody(tryBoddy);

		CtStatement stmtC = statementPointed.clone();

		MutationSupporter.clearPosition(stmtC);
		tryBoddy.addStatement(stmtC);

		OperatorInstance opInstace = new StatementOperatorInstance(modificationPoint, this, statementPointed, tryNew);
		opInstances.add(opInstace);

		return opInstances;
	}

	@Override
	public boolean needIngredient() {
		return false;
	}

}
