package fr.inria.astor.approaches.scaffold.scaffoldgeneration;

import java.io.File;
import java.util.List;
import java.util.Map;

import fr.inria.astor.approaches.scaffold.ScaffoldRepairEngine;
import fr.inria.astor.approaches.scaffold.scaffoldgeneration.generator.ExpressionGenerator;
import fr.inria.astor.approaches.scaffold.scaffoldgeneration.libinfo.LibParser;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.declaration.CtEnum;

public class ExpressionTransform extends TransformStrategy {
	String querytype;
	private LibParser parser;
	Map<String, CtEnum> enummap;

	public ExpressionTransform(ModificationPoint modPoint, int modificationPointIndex, MutationSupporter supporter, ProjectRepairFacade facade
			,ScaffoldRepairEngine engine) {
		super(modPoint, modificationPointIndex, supporter,facade,engine);
		pre = "ET";
		querytype="EXP";
		parser=new LibParser(modPoint, supporter, facade);
		parser.analyzeLib();
		enummap = parser.getEnumMap();
	}
	
	public List<String> transform() {
		
		return super.transform();
	}
	
	@Override
	public <T> void visitCtVariableRead(CtVariableRead<T> variableRead) {
		
		String type = variableRead.getType().getQualifiedName();
		type = type.replaceAll("\\d","");
		@SuppressWarnings("rawtypes")
		CtExpression exp = null;
		exp = ExpressionGenerator.fetchEXP(this.mutSupporter, this.modificationPoint, type, querytype);
				
		if (exp != null)
			candidates.put(variableRead, exp);
	}
	
	@Override
	public <T> void visitCtVariableWrite(CtVariableWrite<T> variableWrite) {
		
		String type = variableWrite.getType().getQualifiedName();
		type = type.replaceAll("\\d","");
		@SuppressWarnings("rawtypes")
		CtExpression exp = null;
		exp = ExpressionGenerator.fetchEXP(this.mutSupporter, this.modificationPoint, type, querytype);
				
		if (exp != null)
			candidates.put(variableWrite, exp);
	}
	
	@Override
	public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
		
		String type = fieldWrite.getType().getQualifiedName();
		type = type.replaceAll("\\d","");
		@SuppressWarnings("rawtypes")
		CtExpression exp = null;
		exp = ExpressionGenerator.fetchEXP(this.mutSupporter, this.modificationPoint, type, querytype);
				
		if (exp != null)
			candidates.put(fieldWrite, exp);
	}
	
	@Override
	public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
		
		String type = fieldRead.getType().getQualifiedName();
		type = type.replaceAll("\\d","");
		
		CtEnum enumRead=enummap.get(type);
		
		@SuppressWarnings("rawtypes")
		CtExpression exp = null;
		if(enumRead==null)
		   exp = ExpressionGenerator.fetchEXP(this.mutSupporter, this.modificationPoint, type, querytype);
		else 		
		   exp = ExpressionGenerator.fetchENUM(enumRead, this.mutSupporter, type, querytype);
			
		if (exp != null)
			candidates.put(fieldRead, exp);
	}
	
	@Override
	public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
		super.visitCtBinaryOperator(operator);
		@SuppressWarnings("rawtypes")
		CtExpression left = operator.getLeftHandOperand();
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
	
	@SuppressWarnings("rawtypes")
	@Override
	public <T> void visitCtLiteral(CtLiteral<T> literal) {
		CtExpression exp = null; 
		if(literal.getValue()!=null) {
		   if(literal.getValue().toString().toLowerCase().equals("true")||
				literal.getValue().toString().toLowerCase().equals("false")) {
			  exp = ExpressionGenerator.fetchEXP(this.mutSupporter, this.modificationPoint, "boolean", querytype);
			  if (exp != null)
				candidates.put(literal, exp);
		   }
		}
	}
}
