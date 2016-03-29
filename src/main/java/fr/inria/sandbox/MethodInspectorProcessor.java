package fr.inria.sandbox;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import spoon.processing.AbstractProcessor;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtExecutableReference;


public class MethodInspectorProcessor extends AbstractProcessor<CtMethod<?>> {

	@SuppressWarnings("rawtypes")
	public void process(CtMethod<?> method) {
	
		System.out.println(method.getSignature());
		System.out.println("");
		
		//System.out.println(	method.getParameters());
		System.out.println(	method.getThrownTypes());
		System.out.println(method.getVisibility());
		//System.out.println(	method.getBody());
		System.out.println("-----");
	
		System.out.println("Parameters data:");
		for (CtParameter par : method.getParameters()) {
			System.out.println(par.getType() + " "+par.getSimpleName());
		}
		System.out.println(method.getType() + " "+	method.getType().getSimpleName() );
		CtExecutableReference ref = method.getReference();
		
		System.out.println("ref "+method.getReference());
		
		CtClass parent= (CtClass) method.getParent();
		List<CtField> fields = parent.getFields();
		for (CtField ctField : fields) {
			System.out.println("field "+ctField.getSimpleName()+ " " + ctField.getType());
		}
		System.out.println("Statements");
		List<CtStatement> statements = method.getBody().getStatements();
		int index = 0;
		for (CtStatement ctStatement : statements) {
			index++;
			System.out.println(index+" "+ ctStatement + ctStatement.getClass().getName());
			
			if(ctStatement instanceof CtInvocation){
				CtInvocation cinv = (CtInvocation) ctStatement;
				System.out.println(" "+cinv);
				
				for(Object argo : cinv.getArguments()){
					CtExpression arg = (CtExpression) argo;
					System.out.println("Argument type: "+arg.getType() + ", name: "+ arg.getSignature());
					
				}
				cinv.getArguments().remove(0);
				System.out.println(" "+cinv);	
			}
			
			if(ctStatement instanceof CtLocalVariable){
			
				CtLocalVariable var =(CtLocalVariable) ctStatement;
				CtExpression expression =  var.getDefaultExpression();
				//System.out.println("Local Declaration");
				/*if(expression instanceof CtInvocation){
					CtInvocation cinv = (CtInvocation) expression;
					System.out.println(" "+cinv);
				
				}*/
				
			}
			/*List scope = getVarInScope(ctStatement);
			System.out.println("var in scope "+scope);
			
			
		*/
			if(ctStatement instanceof CtIf){
				CtIf cif = (CtIf) ctStatement;
				//System.out.println(" "+cinv);
				CtBlock blif = (CtBlock) cif.getThenStatement();
				for (Object ctStatement2 : blif.getStatements()) {
					List scope = getVarInScope((CtElement) ctStatement2);
					System.out.println("var in scope "+scope);
				}
			}	
		}
		
				
	/*	CtStatement newStatement = Factory.getLauchingFactory().Code().createCodeSnippetStatement("System.out.println(\"insertedF\")");
		CtStatement newStatement2 = Factory.getLauchingFactory().Core().clone(newStatement);
		
		List<CtStatement> statements = method.getBody().getStatements();	
		//statements.clear();
		
		statements.add(0,newStatement);*/
	}

	public List getVarInScope(CtElement el){
		List r = new ArrayList();
		if(!(el instanceof CtMethod)){
		//	System.out.println("analyzing");
			CtElement parent = el.getParent();
			if(parent instanceof CtBlock){
				CtBlock pb = (CtBlock) parent;
				int positionEl = pb.getStatements().indexOf(el);
			//	System.out.println("posicion "+positionEl + " "+el );
				r.addAll(analizeVar(positionEl,pb.getStatements()));
			}
			r.addAll(getVarInScope(parent));	
		}
		return r;
	}

	private Collection analizeVar(int positionEl, List statements) {
		List r = new ArrayList();
		for(int i = 0; i< positionEl;i++){
			CtElement ct = (CtElement) statements.get(i);
			if(ct instanceof CtLocalVariable){
				r.add(ct);
			}
		}
		return r;
	}
	
}
