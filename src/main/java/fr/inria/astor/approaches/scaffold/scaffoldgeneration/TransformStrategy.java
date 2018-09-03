package fr.inria.astor.approaches.scaffold.scaffoldgeneration;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import fr.inria.astor.approaches.scaffold.ScaffoldRepairEngine;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.bytecode.OutputWritter;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import spoon.reflect.code.CtArrayRead;
import spoon.reflect.code.CtArrayWrite;
import spoon.reflect.code.CtAssert;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtCase;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtNewArray;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtSynchronized;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtWhile;
import spoon.reflect.cu.CompilationUnit;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtImport;
import spoon.reflect.visitor.CtScanner;

public abstract class TransformStrategy extends CtScanner {
	
	protected Logger log = Logger.getLogger(TransformStrategy.class.getName());

	ModificationPoint modificationPoint;
	MutationSupporter mutSupporter;
	ProjectRepairFacade projFacade;
	OutputWritter outputWritter;
	ScaffoldRepairEngine repairEngine;
	
	protected List<String> list = new ArrayList<String>();
	@SuppressWarnings("rawtypes")
	protected Map<CtExpression, CtExpression> candidates = new HashMap<CtExpression, CtExpression>();
	protected int index = 0;
	protected int indexOfModPoint = 0;
	protected String pre = "";
	private String source = "";

	public TransformStrategy(ModificationPoint modPoint, int modificationPointIndex, MutationSupporter supporter, ProjectRepairFacade facade
			, ScaffoldRepairEngine engine) {
		
		this.mutSupporter=supporter;
		this.modificationPoint=modPoint;
		this.projFacade=facade;
		this.outputWritter=this.mutSupporter.getOutput();
		this.outputWritter.getFactory().getEnvironment().setAutoImports(true);
		this.indexOfModPoint=modificationPointIndex;
		this.repairEngine=engine;
		
		source = this.projFacade.getInDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
	//	source = this.projFacade.getProperties().getOriginalProjectRootDir()+File.separator+"Sketches"+File.separator;
		
		File file = new File(source);
		if (!file.exists())
			file.mkdirs();
	//	addImportsForSketch();
	}
	
	@SuppressWarnings({ "rawtypes", "static-access" })
	protected void addImportsForSketch() {
		 CtClass classA = this.modificationPoint.getCtClass();
		 CompilationUnit unitA = this.mutSupporter.getFactory().CompilationUnit().getMap().get(
				 classA.getPosition().getFile().getPath());
		 Collection<CtImport> imports = unitA.getImports();
		 CtImport sketchlib=this.mutSupporter.getFactory().Type().createImport(this.mutSupporter.getFactory().Type().createReference
				 ("fr.inria.astor.approaches.scaffold.scaffoldsynthesis.ScaffoldSynthesisEntry"));
		 imports.add(sketchlib);
	//	 unitA.setImports(imports);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public List<String> transform () {
		CtStatement targetStmt = (CtStatement) this.modificationPoint.getCodeElement();
		if (targetStmt instanceof CtInvocation)
			this.visitCtInvocation((CtInvocation) targetStmt);
		else if (targetStmt instanceof CtConstructorCall)
			this.visitCtConstructorCall((CtConstructorCall) targetStmt);
		else if (targetStmt instanceof CtIf)
			this.visitCtIf ((CtIf)targetStmt);
		else if (targetStmt instanceof CtReturn)
			this.visitCtReturn((CtReturn) targetStmt);
		else if (targetStmt instanceof CtSwitch)
			this.visitCtSwitch((CtSwitch) targetStmt);
		else if (targetStmt instanceof CtCase)
			this.visitCtCase((CtCase) targetStmt);
		else if (targetStmt instanceof CtAssignment)
			this.visitCtAssignment((CtAssignment) targetStmt);
		else if (targetStmt instanceof CtAssert)
			this.visitCtAssert((CtAssert) targetStmt);
		else if (targetStmt instanceof CtFor)
			this.visitCtFor((CtFor) targetStmt);
		else if (targetStmt instanceof CtForEach)
			this.visitCtForEach((CtForEach) targetStmt);
		else if (targetStmt instanceof CtWhile)
			this.visitCtWhile((CtWhile) targetStmt);
		else if (targetStmt instanceof CtUnaryOperator)
			this.visitCtUnaryOperator((CtUnaryOperator) targetStmt);
		else if (targetStmt instanceof CtSynchronized)
			this.visitCtSynchronized((CtSynchronized) targetStmt);

		return list;
	}

	protected void saveSketchAndSynthesize() {
		
	    list.add(source + pre +"-"+ this.indexOfModPoint+"-"+index++);
		saveToWorkSpace();
		
		Boolean whetherFondPatch = false;
		try {
			whetherFondPatch = this.repairEngine.synthesizeSketch(pre, this.modificationPoint);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if (whetherFondPatch) {
			System.exit(0);
		} 
	}
	
//   protected void saveSketchAndSynthesize() {
//		
//		String path = source + pre +"-"+ this.indexOfModPoint+"-"+index++;
//		File file = new File(path);
//		if (!file.exists())
//			file.mkdirs();
//		
//		this.outputWritter.updateOutput(path);
//		this.outputWritter.saveSourceCode(this.modificationPoint.getCtClass());
//	}
	
	protected void resoreDiskFile() {
		saveToWorkSpace();
	}

	protected void saveToWorkSpace() {
		File file = new File(source);
		if (!file.exists())
			file.mkdirs();
		
		this.outputWritter.updateOutput(source);
		this.outputWritter.saveSourceCode(this.modificationPoint.getCtClass());
	}
	
	@Override
	public <T> void visitCtInvocation(CtInvocation<T> invocation) {
		super.visitCtInvocation(invocation);

		List<CtExpression<?>>  argumentlist = invocation.getArguments();
		for (int i = 0; i < argumentlist.size(); i++) {
			@SuppressWarnings("rawtypes")
			CtExpression p = argumentlist.get(i);
			if (candidates.containsKey(p)) {
				argumentlist.set(i, candidates.get(p));
			//	invocation.setArguments(argumentlist);
				saveSketchAndSynthesize();
				argumentlist.set(i, p);
				resoreDiskFile();
			//	invocation.setArguments(argumentlist);
			}
		}
	}
	
	@Override
	public <T> void visitCtConstructorCall(CtConstructorCall<T> ctConstructorCall) {
		super.visitCtConstructorCall(ctConstructorCall);

		List<CtExpression<?>>  argumentlist = ctConstructorCall.getArguments();
		for (int i = 0; i < argumentlist.size(); i++) {
			@SuppressWarnings("rawtypes")
			CtExpression p = argumentlist.get(i);
			if (candidates.containsKey(p)) {
				argumentlist.set(i, candidates.get(p));
			//	ctConstructorCall.setArguments(argumentlist);
				saveSketchAndSynthesize();
				argumentlist.set(i, p);
				resoreDiskFile();
			//	ctConstructorCall.setArguments(argumentlist);
			}
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <R> void visitCtReturn(CtReturn<R> returnStatement) {
		super.visitCtReturn(returnStatement);
		
		CtExpression exper=returnStatement.getReturnedExpression();
		if (candidates.containsKey(exper)) {
			returnStatement.setReturnedExpression(candidates.get(exper));
			saveSketchAndSynthesize();
			returnStatement.setReturnedExpression(exper);
			resoreDiskFile();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> void visitCtAssert(CtAssert<T> asserted) {
		super.visitCtAssert(asserted);

		CtExpression exper=asserted.getExpression();
		if (candidates.containsKey(exper)) {
			asserted.setExpression(candidates.get(exper));
			saveSketchAndSynthesize();
			asserted.setExpression(exper);
			resoreDiskFile();
		}
		
		CtExpression assertexper=asserted.getAssertExpression();
		if (candidates.containsKey(assertexper)) {
			asserted.setAssertExpression(candidates.get(assertexper));
			saveSketchAndSynthesize();
			asserted.setAssertExpression(assertexper);
			resoreDiskFile();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <S> void visitCtSwitch(CtSwitch<S> switchStatement) {
		super.visitCtSwitch(switchStatement);
		
		CtExpression exper=switchStatement.getSelector();
		if (candidates.containsKey(exper)) {
			switchStatement.setSelector(candidates.get(exper));
			saveSketchAndSynthesize();
			switchStatement.setSelector(exper);
			resoreDiskFile();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <S> void visitCtCase(CtCase<S> caseStatement) {
		super.visitCtCase(caseStatement);
		
		CtExpression exper=caseStatement.getCaseExpression();
		if (candidates.containsKey(exper)) {
			caseStatement.setCaseExpression(candidates.get(exper));
			saveSketchAndSynthesize();
			caseStatement.setCaseExpression(exper);
			resoreDiskFile();
		}
	}
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> assignement) {
		super.visitCtAssignment(assignement);
		
		CtExpression exper=assignement.getAssigned();
		if (candidates.containsKey(exper)) {
			assignement.setAssigned(candidates.get(exper));
			saveSketchAndSynthesize();
			assignement.setAssigned(exper);
			resoreDiskFile();
		}
		
		exper=assignement.getAssignment();
		if (candidates.containsKey(exper)) {
			assignement.setAssignment(candidates.get(exper));
			saveSketchAndSynthesize();
			assignement.setAssignment(exper);
			resoreDiskFile();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void visitCtFor(CtFor forLoop) {
		super.visitCtFor(forLoop);

		@SuppressWarnings("rawtypes")
		CtExpression exper=forLoop.getExpression();
		if (candidates.containsKey(exper)) {
			forLoop.setExpression(candidates.get(exper));
			saveSketchAndSynthesize();
			forLoop.setExpression(exper);
			resoreDiskFile();
		}
	}
	
	@Override
	public void visitCtForEach(CtForEach foreach) {
		super.visitCtForEach(foreach);
		
		@SuppressWarnings("rawtypes")
		CtExpression exper=foreach.getExpression();
		if (candidates.containsKey(exper)) {
			foreach.setExpression(candidates.get(exper));
			saveSketchAndSynthesize();
			foreach.setExpression(exper);
			resoreDiskFile();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void visitCtWhile(CtWhile whileLoop) {
		super.visitCtWhile(whileLoop);
		
		@SuppressWarnings("rawtypes")
		CtExpression exper=whileLoop.getLoopingExpression();
		if (candidates.containsKey(exper)) {
			whileLoop.setLoopingExpression(candidates.get(exper));
			saveSketchAndSynthesize();
			whileLoop.setLoopingExpression(exper);
			resoreDiskFile();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {
		super.visitCtUnaryOperator(operator);
		
		CtExpression exper=operator.getOperand();
		if (candidates.containsKey(exper)) {
			operator.setOperand(candidates.get(exper));
			saveSketchAndSynthesize();
			operator.setOperand(exper);
			resoreDiskFile();
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void visitCtIf(CtIf ifElement) {
		super.visitCtIf(ifElement);
		
		@SuppressWarnings("rawtypes")
		CtExpression exper=ifElement.getCondition();
		if (candidates.containsKey(exper)) {
			ifElement.setCondition(candidates.get(exper));
			saveSketchAndSynthesize();
			ifElement.setCondition(exper);
			resoreDiskFile();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> void visitCtConditional(CtConditional<T> conditional) {
		super.visitCtConditional(conditional);
		
		CtExpression exp = conditional.getCondition();
		if (candidates.containsKey(exp)) {
			conditional.setCondition(candidates.get(exp));
			saveSketchAndSynthesize();
			conditional.setCondition(exp);
			resoreDiskFile();
		}
		exp = conditional.getElseExpression();
		if (candidates.containsKey(exp)) {
			conditional.setElseExpression(candidates.get(exp));
			saveSketchAndSynthesize();
			conditional.setElseExpression(exp);
			resoreDiskFile();
		}
		exp = conditional.getThenExpression();
		if (candidates.containsKey(exp)) {
			conditional.setThenExpression(candidates.get(exp));
			saveSketchAndSynthesize();
			conditional.setThenExpression(exp);
			resoreDiskFile();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> void visitCtArrayRead(CtArrayRead<T> arrayRead) {
		super.visitCtArrayRead(arrayRead);
		
		CtExpression exper=arrayRead.getIndexExpression();
		if (candidates.containsKey(exper)) {
			arrayRead.setIndexExpression(candidates.get(exper));
			saveSketchAndSynthesize();
			arrayRead.setIndexExpression(exper);
			resoreDiskFile();
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Override
	public <T> void visitCtArrayWrite(CtArrayWrite<T> arrayWrite) {
		super.visitCtArrayWrite(arrayWrite);
		
		CtExpression exper=arrayWrite.getIndexExpression();
		if (candidates.containsKey(exper)) {
			arrayWrite.setIndexExpression(candidates.get(exper));
			saveSketchAndSynthesize();
			arrayWrite.setIndexExpression(exper);
			resoreDiskFile();
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public <T> void visitCtNewArray(CtNewArray<T> newArray) {
		super.visitCtNewArray(newArray);
		
		List<CtExpression<Integer>> dimension = newArray.getDimensionExpressions();
		for (int i = 0; i < dimension.size(); i++) {
			CtExpression p = dimension.get(i);
			if (candidates.containsKey(p)) {
				dimension.set(i, candidates.get(p));
				newArray.setDimensionExpressions(dimension);
				saveSketchAndSynthesize();
				dimension.set(i, p);
				newArray.setDimensionExpressions(dimension);
				resoreDiskFile();
			}
		}
		
		List<CtExpression<?>> iniexper = newArray.getElements();
		for (int i = 0; i < iniexper.size(); i++) {
			CtExpression p = iniexper.get(i);
			if (candidates.containsKey(p)) {
				iniexper.set(i, candidates.get(p));
				newArray.setElements(iniexper);
				saveSketchAndSynthesize();
				iniexper.set(i, p);
				newArray.setElements(iniexper);
				resoreDiskFile();
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	public void visitCtSynchronized(CtSynchronized synchro) {
		super.visitCtSynchronized(synchro);
		
		CtExpression exper=synchro.getExpression();
		if (candidates.containsKey(exper)) {
			synchro.setExpression(candidates.get(exper));
			saveSketchAndSynthesize();
			synchro.setExpression(exper);
			resoreDiskFile();
		}
	}
}
