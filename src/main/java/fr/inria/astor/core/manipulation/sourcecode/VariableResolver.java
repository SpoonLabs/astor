package fr.inria.astor.core.manipulation.sourcecode;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtTypeAccess;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtVariableWrite;
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
					if ( (ctFieldReference.getDeclaration() != null) && !"serialVersionUID".equals(ctFieldReference.getDeclaration().getSimpleName()))
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
