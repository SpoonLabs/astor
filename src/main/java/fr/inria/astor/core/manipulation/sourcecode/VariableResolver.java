package fr.inria.astor.core.manipulation.sourcecode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtArrayAccess;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtTypeReference;
import fr.inria.astor.core.entities.Gen;
import fr.inria.astor.core.entities.GenOperationInstance;

/**
 * Variable manipulations: methods to analyze variables and scope
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
@SuppressWarnings("rawtypes") 
public class VariableResolver {

	private static Logger logger = Logger.getLogger(VariableResolver.class.getName());

	/**
	 * For a given VariableAccess, we search the list of Variables contains
	 * compatible types (i.e. sub types)
	 * 
	 * @param varContext
	 * @param vartofind
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<CtVariable> compatiblesSubType(List<CtVariable> varContext, CtVariableAccess vartofind) {
		
		CtTypeReference typeToFind = vartofind.getType();
	
		return compatiblesSubType(varContext, typeToFind);
	}
	
	public static boolean matchVariable(List<CtVariable> varContext, CtVariableAccess vartofind){
		
		CtTypeReference typeToFind = vartofind.getType();
			//First we search for compatible variables according to the type
		 List<CtVariable> types = compatiblesSubType(varContext, typeToFind);
		 
		 //Then, we search 
		 for (CtVariable ctVariableWithTypes : types) {
			boolean match = ctVariableWithTypes.getSimpleName().equals(vartofind.getVariable().getSimpleName());
			if(match){
				//System.out.println("idem "+ctVariableWithTypes  );
				return true;
			}
		 }
		 
		 return false;
	}
	
	/**
	 * For a given VariableAccess, we search the list of Variables contains
	 * compatible types (i.e. sub types)
	 * 
	 * @param varContext
	 * @param vartofind
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<CtVariable> compatiblesSubType(List<CtVariable> varContext, CtTypeReference typeToFind) {
	
		List<CtVariable> result = new ArrayList<CtVariable>();

		for (CtVariable ctVariable_i : varContext) {

			CtTypeReference typeref_i = ctVariable_i.getType();
			try{
			if (typeref_i.isSubtypeOf(typeToFind)) {
				result.add(ctVariable_i);
			}
			}catch(Exception e){
				//sometimes is not possible to get the subtype
				//	e.printStackTrace();
				//try{
				//if(typeref_i.getDeclaration().getQualifiedName().equals(typeToFind.getDeclaration().getQualifiedName()))
					result.add(ctVariable_i);
				//}catch(Exception e2){
				//	e2.printStackTrace();
				//	System.out.println("--"+typeref_i+" "+typeToFind);
				//}
	
			}

		}
		return result;
	}
	
	
	protected static List<CtVariable> existSameVariable(List<CtVariable> vars, CtVariableAccess vartofind) {

		List<CtVariable> result = new ArrayList<CtVariable>();
		CtTypeReference typeToFind = vartofind.getType();
		for (CtVariable ctVariable_i : vars) {

			CtTypeReference typeref_i = ctVariable_i.getType();

			if (typeref_i.equals((typeToFind))
					&& ctVariable_i.getSimpleName().equals(vartofind.getVariable().getSimpleName())) {
				result.add(ctVariable_i);
			}
		}//return false
		return result;
	}
/**
 * Return true if the CtElement is valid according to a set of variables (the context).
 * In this case valid means all variables referenced by the expression can be REPLACED by one from 
 * the context. 
 * 
 * It not take in account the variable names. Only types
 *  
 * @param varContext
 * @param element
 * @return
 */
	@SuppressWarnings("rawtypes")
	public static boolean fitInPlace(List<CtVariable> varContext, CtElement element) {
		
		if(element == null)
			return true;
		
		if (element instanceof CtVariableAccess) {
			return matchVariable(varContext,(CtVariableAccess) element);
		}
		if (element instanceof CtArrayAccess) {
			CtArrayAccess el = (CtArrayAccess) element;
			boolean fitTarget = fitInPlace(varContext,	el.getIndexExpression());
			if(fitTarget)
				fitTarget = fitInPlace(varContext, el.getTarget());
			return fitTarget;
		}


		//**********If the element is not a variable access, the analyze each case.
		
		if(element instanceof CtReturn<?>){
			return fitInPlace(varContext, ((CtReturn) element).getReturnedExpression());
		}
				
		if (element instanceof CtInvocation) {
			CtInvocation inv = (CtInvocation) element;
			CtExpression target = inv.getTarget();
			boolean fitTarget = true;
			if (target != null) {
				fitTarget = fitInPlace(varContext, target);
			}
			List<CtExpression> args = inv.getArguments();

			for (int i = 0; fitTarget && i < args.size(); i++) {
				fitTarget = fitInPlace(varContext, args.get(i));
			}
			return fitTarget;
		}
		
		if (element instanceof CtConstructorCall) {
			CtConstructorCall ccall = (CtConstructorCall) element;
	
			boolean fitTarget = true;
		
			List<CtExpression> args = ccall.getArguments();

			for (int i = 0; fitTarget && i < args.size(); i++) {
				fitTarget = fitInPlace(varContext, args.get(i));
			}
			return fitTarget;
		}
		
		
		if (element instanceof CtAssignment) {
			CtAssignment assig = (CtAssignment) element;
			CtExpression expleft = assig.getAssigned();
			CtExpression expright = assig.getAssignment();

			return fitInPlace(varContext, expleft) && fitInPlace(varContext, expright);
		}
		if(element instanceof CtBinaryOperator){
			CtBinaryOperator binop = (CtBinaryOperator) element;
			CtExpression expleft = binop.getLeftHandOperand();
			CtExpression expright = binop.getRightHandOperand();
			return fitInPlace(varContext, expleft) && fitInPlace(varContext, expright);
			
		}
		if(element instanceof CtUnaryOperator){
			CtUnaryOperator upnop = (CtUnaryOperator) element;
			CtExpression expleft =upnop.getOperand();
			
			return fitInPlace(varContext, expleft);
			
		}
		if(element instanceof CtBlock){
			
			boolean fitTarget = true;
		
			List<CtStatement> args = ((CtBlock)element).getStatements();

			for (int i = 0; fitTarget && i < args.size(); i++) {
				fitTarget = fitInPlace(varContext, args.get(i));
			}
			return fitTarget;
		}
		
		if(element instanceof CtIf){
			CtIf el = (CtIf) element;
			boolean fitTarget = fitInPlace(varContext, el.getCondition());
			if(fitTarget){
				fitInPlace(varContext, el.getThenStatement());
				fitInPlace(varContext, el.getElseStatement());
			}
			return fitTarget;
		}
				
		if(element instanceof CtWhile){
			CtWhile el = (CtWhile) element;
			boolean fitTarget = fitInPlace(varContext, el.getLoopingExpression());
			if(fitTarget){
				fitInPlace(varContext, el.getBody());
			}
			return fitTarget;
		}
		
		if(element instanceof CtFor){
			CtFor el = (CtFor) element;
			boolean fitTarget = fitInPlace(varContext, el.getExpression());
			if(fitTarget){
				fitInPlace(varContext, el.getBody());
			}
			return fitTarget;
		}
		
		if(element instanceof CtForEach){
			CtForEach el = (CtForEach) element;
			boolean fitTarget = fitInPlace(varContext, el.getExpression());
			if(fitTarget){
				fitInPlace(varContext, el.getBody());
			}
			return fitTarget;
		}
		
		if(element instanceof CtDo){
			CtDo el = (CtDo) element;
			boolean fitTarget = fitInPlace(varContext, el.getLoopingExpression());
			if(fitTarget){
				fitInPlace(varContext, el.getBody());
			}
			return fitTarget;
		}
		
		if(element instanceof CtThrow){
			CtThrow el = (CtThrow) element;
			boolean fitTarget = fitInPlace(varContext, el.getThrownExpression());
			
			return fitTarget;
		}
				
		
		if(element instanceof CtLiteral){
			return true;
		}
		
		if(element instanceof CtLocalVariable){
			CtLocalVariable el = (CtLocalVariable) element;
			fitInPlace(varContext, el.getDefaultExpression());
			return true;
		}
		
		if(element instanceof CtTypeAccess)
			return true;
		
		
				//CtTypeAccessImpl
		logger.error("\nUndefined case "+element.getClass().getName() + " " +element.getSignature() );
				
		//If we can not determine, we continue and accept the element
		return true;
	}


	/**
	 * Return all the variables in scope of the element passed as parameter.
	 * 
	 * @param el
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<CtVariable> getVariablesFromBlockInScope(CtElement el) {
		List<CtVariable> variables = new ArrayList();
	
		if(el instanceof CtField){
			return variables;
		}
		//If is a class, return the fields
		//TODO: parent fields are included?
		if(el instanceof CtClass){
			CtClass ctclass = (CtClass) el;
			variables.addAll(ctclass.getFields());
			return variables;
		}
		
		if (el instanceof CtMethod) {
			// Method PARAMETERS
			CtMethod method = (CtMethod) el;
			List<CtParameter> pars = method.getParameters();
			for (CtParameter ctParameter : pars) {
				variables.add(ctParameter);
			}
			// Class fields:
			CtClass ctclass = (CtClass) method.getParent();
			variables.addAll(ctclass.getFields());
		} else {
			
			CtElement parent = el.getParent();

			if (parent instanceof CtBlock) {
				CtBlock pb = (CtBlock) parent;
				int positionEl = pb.getStatements().indexOf(el);
				variables.addAll(VariableResolver.retrieveLocalVariables(positionEl, pb));
			}
			variables.addAll(getVariablesFromBlockInScope(parent));
		}
		return variables;
	}

	/**
	 * Return the local variables of a block from the begining until the element
	 * located at positionEl.
	 * 
	 * @param positionEl analyze variables from the block until that position.
	 * @param pb
	 * @return
	 */
	static List<CtLocalVariable> retrieveLocalVariables(int positionEl, CtBlock pb) {
		List stmt = pb.getStatements();
		List<CtLocalVariable> r = new ArrayList<CtLocalVariable>();
		for (int i = 0; i < positionEl; i++) {
			CtElement ct = (CtElement) stmt.get(i);
			if (ct instanceof CtLocalVariable) {
				r.add((CtLocalVariable) ct);
			}
		}
		return r;
	}

	public static boolean canBeApplied(GenOperationInstance operationInGen) {
		// If is an expression
		if (operationInGen.getModified() != null){
			if(operationInGen.getModified() instanceof CtExpression)
				return fitInPlace(operationInGen.getGen().getContextOfGen(), (CtExpression) operationInGen.getModified());
		
					
		System.out.println("--->Reject Analysis: "+operationInGen.getModified().getClass().getName() +  " "+operationInGen.getModified());
			
		}
		logger.debug("--> Context not analyzed ");
		// If is a CtLoop
		// TODO:

		// If not we try to insert
		return true;
	}
	

	/**
	 * For one variable (target) the method finds a set of method invocations from the target's CtType that coul be called i using the variables in scope
	 * @param target
	 * @param varScope
	 * @param compatibleReturning
	 * @return 
	 */@Deprecated
	@SuppressWarnings("unused")
	public static List<MethodInstantialization> compatibleMethodInvocationInScope(CtVariable target,List<CtVariable> varScope, CtTypeReference compatibleReturning){
		List<MethodInstantialization> instant = new ArrayList<MethodInstantialization>();
		logger.debug("Target class "+target.getType());
		if(target.getType().getDeclaration() instanceof CtType){
			CtType type = (CtType) target.getType().getDeclaration();
			Collection<CtMethod> methods = type.getAllMethods();
			for (CtMethod ctMethod : methods) {
				CtTypeReference returningMethodType = ctMethod.getType();
				logger.debug("Analyzing Method "+ctMethod.getSimpleName()+" returning "+returningMethodType);
				//Analyze return 
				//TODO: check
				if(compatibleReturning != null && !compatibleReturning.isAssignableFrom(returningMethodType)){
					logger.debug("Method return not compatible with type ref assignement");
					continue;
				}
				
				//Analyze parameters
				boolean compatibles = true;
				List<CtParameter> parameters = ctMethod.getParameters();
				Map<CtParameter,List> candidates = new HashMap<CtParameter,List>();
				for (CtParameter ctParameter : parameters) {
					CtTypeReference typ =  ctParameter.getType();
					List<CtVariable> vari = compatiblesSubType(varScope, typ);
					if(vari.isEmpty()){
						compatibles = false;
						break;
					}else{
						candidates.put(ctParameter, vari);
					}
					logger.debug(typ.getSimpleName()+" ==> "+vari);
				}
				if(compatibles){
					MethodInstantialization mi = new MethodInstantialization(ctMethod, candidates);
					instant.add(mi);
				}
				
			}
		}else{
			//
			//TODO:The declaration is null i.e. there is not representation of the class in the model
		
		}
		return instant;
	}
}

