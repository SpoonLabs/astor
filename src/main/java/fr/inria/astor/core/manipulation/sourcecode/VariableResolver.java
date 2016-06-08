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
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtConstructorCall;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtThisAccess;
import spoon.reflect.code.CtThrow;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtField;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;

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
	 * Return a list of variables that match with the variable access passed as
	 * parameter. The last argument indicate if we map also the vars name
	 * 
	 * @param varContext
	 * @param vartofind
	 * @param mapName
	 * @return
	 */
	protected static List<CtVariable> matchVariable(List<CtVariable> varContext, CtVariableAccess vartofind,
			boolean mapName) {
		List<CtVariable> varMatched = new ArrayList<>();
		try {
			CtTypeReference typeToFind = vartofind.getType();
			// First we search for compatible variables according to the type
			List<CtVariable> types = compatiblesSubType(varContext, typeToFind);
			if (types.isEmpty()) {
				return varMatched;
			}
			// Then, we search
			for (CtVariable ctVariableWithTypes : types) {
				// comparing name is optional, according to argument.
				boolean matchName = !mapName
						|| ctVariableWithTypes.getSimpleName().equals(vartofind.getVariable().getSimpleName());
				if (matchName) {
					varMatched.add(ctVariableWithTypes);
				}
			}
		} catch (Exception ex) {
			logger.error("Variable verification error", ex);
		}

		return varMatched;
	}

	/**
	 * 
	 * @param varContext
	 * @param vartofind
	 * @return
	 */
	@Deprecated
	protected static boolean matchVariable(List<CtVariable> varContext, CtVariableAccess vartofind) {
		try {
			CtTypeReference typeToFind = vartofind.getType();
			// First we search for compatible variables according to the type
			List<CtVariable> types = compatiblesSubType(varContext, typeToFind);

			// Then, we search
			for (CtVariable ctVariableWithTypes : types) {
				boolean match = ctVariableWithTypes.getSimpleName().equals(vartofind.getVariable().getSimpleName());
				if (match) {
					// System.out.println("idem "+ctVariableWithTypes );
					return true;
				}
			}
		} catch (Exception ex) {
			logger.error("Variable verification error", ex);
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
	protected static List<CtVariable> compatiblesSubType(List<CtVariable> varContext, CtTypeReference typeToFind) {

		List<CtVariable> result = new ArrayList<CtVariable>();

		for (CtVariable ctVariable_i : varContext) {

			CtTypeReference typeref_i = ctVariable_i.getType();
			try {
				if (typeref_i.isSubtypeOf(typeToFind)) {
					result.add(ctVariable_i);
				}
			} catch (Exception e) {
				result.add(ctVariable_i);
			}

		}
		return result;
	}

	/**
	 * Maps a variable access with a variable declaration.
	 * 
	 * @param varContext
	 * @param varacc
	 * @return
	 */
	public static Map<CtVariableAccess, List<CtVariable>> matchVars(List<CtVariable> varContext,
			List<CtVariableAccess> varacc, boolean mapName) {

		Map<CtVariableAccess, List<CtVariable>> mapping = new HashMap<>();

		for (CtVariableAccess ctVariableAccess : varacc) {
			List<CtVariable> matched = matchVariable(varContext, ctVariableAccess, mapName);
			mapping.put(ctVariableAccess, matched);
		}

		return mapping;
	}

	/**
	 * Return all variables related to the element passed as argument
	 * 
	 * @param element
	 * @return
	 */
	public static List<CtVariableAccess> collectVariableAccess(CtElement element) {
		List<CtVariableAccess> varaccess = new ArrayList<>();

		CtScanner sc = new CtScanner() {

			@Override
			public <T> void visitCtVariableRead(CtVariableRead<T> variableRead) {
				super.visitCtVariableRead(variableRead);
				if (!varaccess.contains(variableRead))
					varaccess.add(variableRead);
			}

			@Override
			public <T> void visitCtVariableWrite(CtVariableWrite<T> variableWrite) {
				super.visitCtVariableWrite(variableWrite);
				if (!varaccess.contains(variableWrite))
					varaccess.add(variableWrite);
			}

			@Override
			public <T> void visitCtTypeAccess(CtTypeAccess<T> typeAccess) {
				super.visitCtTypeAccess(typeAccess);
				//varaccess.add(typeAccess);
			}

			@Override
			public <T> void visitCtFieldRead(CtFieldRead<T> fieldRead) {
				super.visitCtFieldRead(fieldRead);
				if(!varaccess.contains(fieldRead))
					varaccess.add(fieldRead);
			}

			@Override
			public <T> void visitCtFieldWrite(CtFieldWrite<T> fieldWrite) {
				super.visitCtFieldWrite(fieldWrite);
				if(!varaccess.contains(fieldWrite))
					varaccess.add(fieldWrite);
			}

		};

		sc.scan(element);

		return varaccess;

	}

	/**
	 * 
	 * This methods determines whether all the variable access contained in a
	 * CtElement passes as parameter match with a variable from a set of
	 * variables given as argument. Both variable Types and Names are compared,
	 * 
	 * @param varContext
	 *            List of variables to match
	 * @param element
	 *            element to extract the var access to match
	 * @return
	 */
	public static boolean fitInPlace(List<CtVariable> varContext, CtElement element) {
		return fitInContext(varContext, element, true);
	}

	/**
	 * This methods determines whether all the variable access contained in a
	 * CtElement passes as parameter match with a variable from a set of
	 * variables given as argument. The argument <code>matchName </code>
	 * indicates whether Type and Names are compared (value true), only type
	 * (false).
	 * 
	 * @param varContext
	 *            List of variables to match
	 * @param element
	 *            element to extract the var access to match
	 * @return
	 */
	public static boolean fitInContext(List<CtVariable> varContext, CtElement element, boolean matchName) {

		List<CtVariableAccess> varAccessCollected = collectVariableAccess(element);

		Map<CtVariableAccess, List<CtVariable>> matched = matchVars(varContext, varAccessCollected, matchName);

		// Now, we analyze if all access were matched
		for (CtVariableAccess ctVariableAccess : matched.keySet()) {
			List<CtVariable> mapped = matched.get(ctVariableAccess);
			if (mapped.isEmpty()) {
				// One var access was not mapped
				return false;
			}
		}
		// All VarAccess were mapped
		return true;

	}

	/**
	 * Return true if the CtElement is valid according to a set of variables
	 * (the context). In this case valid means all variables referenced by the
	 * expression can be REPLACED by one from the context.
	 * 
	 * It does not take in account the variable names. Only types.
	 * 
	 * @param varContext
	 * @param element
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@Deprecated
	public static boolean fitInPlaceOLD(List<CtVariable> varContext, CtElement element) {

		if (element == null)
			return true;

		if (element instanceof CtVariableAccess) {
			return matchVariable(varContext, (CtVariableAccess) element);
		}
		if (element instanceof CtArrayAccess) {
			CtArrayAccess el = (CtArrayAccess) element;
			boolean fitTarget = fitInPlace(varContext, el.getIndexExpression());
			if (fitTarget)
				fitTarget = fitInPlace(varContext, el.getTarget());
			return fitTarget;
		}

		// **********If the element is not a variable access, the analyze each
		// case.

		if (element instanceof CtReturn<?>) {
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
		if (element instanceof CtThisAccess) {

		}

		if (element instanceof CtAssignment) {
			CtAssignment assig = (CtAssignment) element;
			CtExpression expleft = assig.getAssigned();
			CtExpression expright = assig.getAssignment();

			return fitInPlace(varContext, expleft) && fitInPlace(varContext, expright);
		}
		if (element instanceof CtBinaryOperator) {
			CtBinaryOperator binop = (CtBinaryOperator) element;
			CtExpression expleft = binop.getLeftHandOperand();
			CtExpression expright = binop.getRightHandOperand();
			return fitInPlace(varContext, expleft) && fitInPlace(varContext, expright);

		}
		if (element instanceof CtUnaryOperator) {
			CtUnaryOperator upnop = (CtUnaryOperator) element;
			CtExpression expleft = upnop.getOperand();

			return fitInPlace(varContext, expleft);

		}
		if (element instanceof CtBlock) {

			boolean fitTarget = true;

			List<CtStatement> args = ((CtBlock) element).getStatements();

			for (int i = 0; fitTarget && i < args.size(); i++) {
				fitTarget = fitInPlace(varContext, args.get(i));
			}
			return fitTarget;
		}

		if (element instanceof CtIf) {
			CtIf el = (CtIf) element;
			boolean fitTarget = fitInPlace(varContext, el.getCondition());
			if (fitTarget) {
				fitInPlace(varContext, el.getThenStatement());
				fitInPlace(varContext, el.getElseStatement());
			}
			return fitTarget;
		}

		if (element instanceof CtConditional) {
			CtConditional el = (CtConditional) element;
			boolean fitTarget = fitInPlace(varContext, el.getCondition());
			if (fitTarget) {
				fitInPlace(varContext, el.getThenExpression());
				fitInPlace(varContext, el.getElseExpression());
			}
			return fitTarget;
		}

		if (element instanceof CtWhile) {
			CtWhile el = (CtWhile) element;
			boolean fitTarget = fitInPlace(varContext, el.getLoopingExpression());
			if (fitTarget) {
				fitInPlace(varContext, el.getBody());
			}
			return fitTarget;
		}

		if (element instanceof CtFor) {
			CtFor el = (CtFor) element;
			boolean fitTarget = fitInPlace(varContext, el.getExpression());
			if (fitTarget) {
				fitInPlace(varContext, el.getBody());
			}
			return fitTarget;
		}

		if (element instanceof CtForEach) {
			CtForEach el = (CtForEach) element;
			boolean fitTarget = fitInPlace(varContext, el.getExpression());
			if (fitTarget) {
				fitInPlace(varContext, el.getBody());
			}
			return fitTarget;
		}

		if (element instanceof CtDo) {
			CtDo el = (CtDo) element;
			boolean fitTarget = fitInPlace(varContext, el.getLoopingExpression());
			if (fitTarget) {
				fitInPlace(varContext, el.getBody());
			}
			return fitTarget;
		}

		if (element instanceof CtThrow) {
			CtThrow el = (CtThrow) element;
			boolean fitTarget = fitInPlace(varContext, el.getThrownExpression());

			return fitTarget;
		}

		if (element instanceof CtLiteral) {
			return true;
		}

		if (element instanceof CtLocalVariable) {
			CtLocalVariable el = (CtLocalVariable) element;
			fitInPlace(varContext, el.getDefaultExpression());
			return true;
		}

		if (element instanceof CtTypeAccess)
			return true;

		// CtTypeAccessImpl
		logger.error("\nUndefined case " + element.getClass().getName() + " " + element.getSignature());

		// If we can not determine, we continue and accept the element
		return true;
	}

	/**
	 * Returns all variables in scope, reachable from the ctelement passes as
	 * argument
	 * 
	 * @param element
	 * @return
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List<CtVariable> searchVariablesInScope(CtElement element) {
		List<CtVariable> variables = new ArrayList();

		if (element == null) {
			return variables;
		}

		if (element instanceof CtField) {
			return variables;
		}
		// We find the CtClass and returns the fields
		CtClass ctclass = element.getParent(CtClass.class);
		if (ctclass != null) {
			Collection<CtFieldReference<?>> vars = ctclass.getAllFields();
			for (CtFieldReference<?> ctFieldReference : vars) {
				// We dont add private fields from parent classes
				if ((!ctFieldReference.getModifiers().contains(ModifierKind.PRIVATE)
						|| ctclass.getFields().contains(ctFieldReference.getDeclaration()))) {
					// We ignore "serialVersionUID'
					if (!"serialVersionUID".equals(ctFieldReference.getDeclaration().getSimpleName()))
						variables.add(ctFieldReference.getDeclaration());
				}
			}

		}

		// We find the parent method and we extract the parameters
		CtMethod method = element.getParent(CtMethod.class);
		if (method != null) {
			List<CtParameter> pars = method.getParameters();
			for (CtParameter ctParameter : pars) {
				variables.add(ctParameter);
			}
		}

		// We find the parent block and we extract the local variables before
		// the element under analysis
		CtBlock parentblock = element.getParent(CtBlock.class);
		if (parentblock != null) {
			int positionEl = parentblock.getStatements().indexOf(element);
			variables.addAll(VariableResolver.retrieveLocalVariables(positionEl, parentblock));
		}

		return variables;

	}

	/**
	 * Return the local variables of a block from the beginning until the
	 * element located at positionEl.
	 * 
	 * @param positionEl
	 *            analyze variables from the block until that position.
	 * @param pb
	 *            a block to search the local variables
	 * @return
	 */
	protected static List<CtLocalVariable> retrieveLocalVariables(int positionEl, CtBlock pb) {
		List stmt = pb.getStatements();
		List<CtLocalVariable> variables = new ArrayList<CtLocalVariable>();
		for (int i = 0; i < positionEl; i++) {
			CtElement ct = (CtElement) stmt.get(i);
			if (ct instanceof CtLocalVariable) {
				variables.add((CtLocalVariable) ct);
			}
		}
		CtElement beforei = pb;
		CtElement parenti = pb.getParent();
		boolean continueSearch = true;
		// We find the parent block
		while (continueSearch) {

			if (parenti == null) {
				continueSearch = false;
				parenti = null;
			} else if (parenti instanceof CtBlock) {
				continueSearch = false;
			} else {
				beforei = parenti;
				parenti = parenti.getParent();
			}
		}

		if (parenti != null) {
			int pos = ((CtBlock) parenti).getStatements().indexOf(beforei);
			variables.addAll(retrieveLocalVariables(pos, (CtBlock) parenti));
		}
		return variables;
	}

}
