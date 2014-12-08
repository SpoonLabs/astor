package fr.inria.astor.core.manipulation.code;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.reference.CtTypeReference;
import fr.inria.astor.core.entities.GenOperationInstance;

/**
 * Variable manipulations: methods to analyze variables and scope
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
@SuppressWarnings("rawtypes") 
public class VariableResolver {

//	private static Logger logger = Logger.getLogger(VariableResolver.class.getName());
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

			if (typeref_i.isSubtypeOf(typeToFind)) {
				result.add(ctVariable_i);
			}

		}
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
	public static boolean fitInPlace(List<CtVariable> varContext, CtExpression element) {
		
		
		if (element instanceof CtVariableAccess) {
			return (!compatiblesSubType(varContext, (CtVariableAccess) element).isEmpty());
		}

		if(element instanceof CtFieldAccess){
			//TODO: See this case 
			return true;
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
				fitTarget = fitInPlace(varContext, args.get(0));
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
		
		if(element instanceof CtLiteral){
			return true;
		}
		logger.error("Undefined case "+element.getClass().getSimpleName());
		
		return false;
	}

	@Deprecated
	private static boolean existVariable(List<CtVariable> vars, CtVariableAccess vartofind) {

		CtTypeReference typeToFind = vartofind.getType();
		for (CtVariable ctVariable_i : vars) {

			CtTypeReference typeref_i = ctVariable_i.getType();

			if (typeref_i.equals((typeToFind))
					&& ctVariable_i.getSimpleName().equals(vartofind.getVariable().getSimpleName())) {
				return true;
			}

		}
		return false;
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
	 * @param positionEl
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
		if (operationInGen.getModified() != null && operationInGen.getModified() instanceof CtExpression)
			return fitInPlace(operationInGen.getGen().getContextOfGen(), (CtExpression) operationInGen.getModified());

		logger.info("--> Context not analyzed " );
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
	 */
	@SuppressWarnings("unused")
	public static List<MethodInstantialization> compatibleMethodInvocationInScope(CtVariable target,List<CtVariable> varScope, CtTypeReference compatibleReturning){
		List<MethodInstantialization> instant = new ArrayList<MethodInstantialization>();
		System.out.println("Target class "+target.getType());
		if(target.getType().getDeclaration() instanceof CtType){
			CtType type = (CtType) target.getType().getDeclaration();
			Collection<CtMethod> methods = type.getAllMethods();
			for (CtMethod ctMethod : methods) {
				CtTypeReference returningMethodType = ctMethod.getType();
				System.out.println("Analyzing Method "+ctMethod.getSimpleName()+" returning "+returningMethodType);
				//Analyze return 
				//TODO: check
				if(compatibleReturning != null && !compatibleReturning.isAssignableFrom(returningMethodType)){
					System.out.println("Method return not compatible with type ref assignement");
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
					System.out.println(typ.getSimpleName()+" ==> "+vari);
				}
				if(compatibles){
					MethodInstantialization mi = new MethodInstantialization(ctMethod, candidates);
					instant.add(mi);
				}
				
			}
		}else{
			//
			//TODO:The declaration is null i.e. there is not representation of the class in the model
			System.out.println("");
		}
		return instant;
	}
}

