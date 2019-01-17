package fr.inria.astor.core.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.manipulation.synthesis.dynamoth.spoon.StaSynthBuilder;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.util.StringDistance;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtConditional;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtLiteral;
import spoon.reflect.code.CtLocalVariable;
import spoon.reflect.code.CtLoop;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtUnaryOperator;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.code.CtVariableRead;
import spoon.reflect.code.CtWhile;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtEnum;
import spoon.reflect.declaration.CtEnumValue;
import spoon.reflect.declaration.CtExecutable;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtPath;
import spoon.reflect.path.impl.CtPathElement;
import spoon.reflect.path.impl.CtPathImpl;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.reference.CtTypeReference;
import spoon.reflect.visitor.CtScanner;
import spoon.reflect.visitor.filter.LineFilter;
import spoon.reflect.visitor.filter.TypeFilter;
import spoon.support.reflect.code.CtVariableReadImpl;

/**
 * 
 * @author Matias Martinez
 *
 */
public class CntxResolver {

	private final class ExpressionCapturerScanner extends CtScanner {
		public CtElement toScan = null;

		@Override
		public void visitCtDo(CtDo doLoop) {
			toScan = doLoop.getLoopingExpression();
		}

		@Override
		public void visitCtFor(CtFor forLoop) {
			toScan = forLoop.getExpression();
		}

		@Override
		public void visitCtIf(CtIf ifElement) {
			toScan = ifElement.getCondition();
		}

		@Override
		public void visitCtWhile(CtWhile whileLoop) {
			toScan = whileLoop.getLoopingExpression();
		}
	}

	protected static Logger log = Logger.getLogger(Thread.currentThread().getName());

	public Cntx<?> retrieveCntx(ModificationPoint modificationPoint) {
		return retrieveCntx(modificationPoint.getCodeElement());
	}

	public Cntx<?> retrievePatchCntx(CtElement element) {
		Cntx<Object> patchcontext = new Cntx<>(determineKey(element));

		patchcontext.put(CNTX_Property.PATCH_CODE_ELEMENT, element.toString());

		CtElement stmt = element.getParent(CtStatement.class);
		if (stmt == null)
			stmt = element.getParent(CtMethod.class);
		patchcontext.put(CNTX_Property.PATCH_CODE_STATEMENT, (stmt != null) ? element.toString() : null);

		retrieveType(element, patchcontext);
		retrievePath(element, patchcontext);

		return patchcontext;
	}

	@SuppressWarnings("unused")
	public Cntx<?> retrieveBuggy(CtElement element) {

		Cntx<Object> context = new Cntx<>(determineKey(element));

		retrievePath(element, context);
		retrieveType(element, context);

		//
		context.put(CNTX_Property.CODE, element.toString());

		CtElement stmt = element.getParent(CtStatement.class);
		if (stmt == null)
			stmt = element.getParent(CtMethod.class);
		context.put(CNTX_Property.BUGGY_STATEMENT, (stmt != null) ? element.toString() : null);

		//
		Cntx<Object> buggyPositionCntx = new Cntx<>();
		retrievePosition(element, buggyPositionCntx);
		context.put(CNTX_Property.POSITION, buggyPositionCntx);

		return context;
	}

	@SuppressWarnings("unused")
	public Cntx<?> retrieveBuggyInfo(CtElement element) {

		Cntx<Object> context = new Cntx<>(determineKey(element));

		retrievePath(element, context);
		retrieveType(element, context);

		context.put(CNTX_Property.CODE, element.toString());

		Cntx<Object> buggyPositionCntx = new Cntx<>();
		retrievePosition(element, buggyPositionCntx);
		context.put(CNTX_Property.POSITION, buggyPositionCntx);

		return context;
	}

	@SuppressWarnings("unused")
	public Cntx<?> retrieveCntx(CtElement element) {
		Cntx<Object> context = new Cntx<>(determineKey(element));

		analyzeFeatures(element, context);

		return context;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void retrieveDM(CtElement element, Cntx<Object> context, List<CtVariable> varsInScope) {

		List<CtLiteral> literals = VariableResolver.collectLiteralsNoString(element.getParent(CtClass.class));

		List<CtExpression> ctexpressions = new ArrayList<>();
		List<CtVariableRead> cteVarReadList = new ArrayList<>();
		for (CtVariable ctVariable : varsInScope) {

			CtVariableReadImpl vr = new CtVariableReadImpl<>();
			vr.setVariable(ctVariable.getReference());
			vr.setType(ctVariable.getType());
			ctexpressions.add(vr);
			cteVarReadList.add(vr);
		}

		for (CtLiteral ctLiteral : literals) {
			ctexpressions.add(ctLiteral);
		}

		StaSynthBuilder ib = new StaSynthBuilder();
		try {
			List<CtExpression> result = ib.synthesizer(ctexpressions, cteVarReadList);
			List<String> resultstring = result.stream().map(e -> e.toString()).collect(Collectors.toList());
			context.put(CNTX_Property.PSPACE, resultstring);
		} catch (Exception e) {
			e.printStackTrace();
			context.put(CNTX_Property.PSPACE, null);
		}
	}

	private void analyzeUseEnumAndConstants(CtElement element, Cntx<Object> context) {

		List enumsValues = new ArrayList();
		List literalsValues = new ArrayList();

		CtScanner assignmentScanner = new CtScanner() {

			@Override
			public <T> void visitCtEnumValue(CtEnumValue<T> enumValue) {

				super.visitCtEnumValue(enumValue);
				enumsValues.add(enumValue);
			}

			@Override
			public <T> void visitCtLiteral(CtLiteral<T> literal) {

				super.visitCtLiteral(literal);
				literalsValues.add(literal);
			}

			@Override
			public <T extends Enum<?>> void visitCtEnum(CtEnum<T> ctEnum) {
				super.visitCtEnum(ctEnum);
				enumsValues.add(ctEnum);
			}

		};

		assignmentScanner.scan(element);
		context.put(CNTX_Property.USES_ENUM, enumsValues.size() > 0);
		context.put(CNTX_Property.USES_CONSTANT, literalsValues.size() > 0);
	}

	private void analyzeV4(List<CtVariableAccess> varsAffected, CtElement element, Cntx<Object> context) {

		boolean hasOneVarAppearsMultiple = false;
		for (CtVariableAccess varInFaulty : varsAffected) {

			CtInvocation parentInvocation = varInFaulty.getParent(CtInvocation.class);
			int appearsInParams = 0;
			if (parentInvocation != null) {
				List<CtElement> arguments = parentInvocation.getArguments();
				for (CtElement i_Argument : arguments) {
					List<CtVariableAccess> varsAccessInParameter = VariableResolver.collectVariableRead(i_Argument);
					if (varsAccessInParameter.contains(varInFaulty)) {
						appearsInParams++;
					}

				}
			}
			if (appearsInParams > 1) {
				hasOneVarAppearsMultiple = true;
			}
			writeDetailedInformationFromVariables(context, varInFaulty.getVariable().getSimpleName(),
					CNTX_Property.V4_FIRST_TIME_PARAMETER, (appearsInParams > 1));

		}
		context.put(CNTX_Property.V4_FIRST_TIME_PARAMETER, hasOneVarAppearsMultiple);
	}

	/**
	 * Besides the variables involved in a logical expression,whether there exist
	 * other local boolean variables in scope?
	 * 
	 * @param varsAffectedInStatement
	 * @param varsInScope
	 * @param element
	 * @param context
	 */
	private void analyzeLE4_BooleanVarNotUsed(List<CtVariableAccess> varsAffectedInStatement,
			List<CtVariable> varsInScope, CtElement element, Cntx<Object> context) {

		boolean hasBooleanVarNotPresent = false;
		/**
		 * For each var in scope
		 */
		for (CtVariable aVarInScope : varsInScope) {

			if (aVarInScope.getType() != null && aVarInScope.getType().unbox().toString().equals("boolean")) {

				// Check if the var in scope is present in the list of var from the expression.
				boolean isPresentVar = varsAffectedInStatement.stream()
						.filter(e -> e.getVariable().getSimpleName().equals(aVarInScope.getSimpleName())).findFirst()
						.isPresent();
				if (!isPresentVar) {
					hasBooleanVarNotPresent = true;
					break;
				}
			}
		}
		context.put(CNTX_Property.LE4_EXISTS_LOCAL_UNUSED_VARIABLES, hasBooleanVarNotPresent);

	}

	/**
	 * For a logical expression, if the logical expression involves comparison over
	 * primitive type variables (that is, some boolean expressions are comparing the
	 * primitive values), is there any other visible local primitive type variables
	 * that are not included in the logical expression (–chart 9). (returns a single
	 * binary value)
	 * 
	 * @param varsAffectedInStatement
	 * @param varsInScope
	 * @param element
	 * @param context
	 */
	private void analyzeLE3_PrimitiveWithCompatibleNotUsed(List<CtVariableAccess> varsAffectedInStatement,
			List<CtVariable> varsInScope, CtElement element, Cntx<Object> context) {

		boolean hasCompatibleVarNoPresent = false;

		for (CtVariableAccess aVarFromAffected : varsAffectedInStatement) {

			if (aVarFromAffected.getType() == null || !aVarFromAffected.getType().isPrimitive()
			// parent is binary operator
					|| // !isparentBinaryComparison(aVarFromAffected))
					aVarFromAffected.getParent(CtBinaryOperator.class) == null)
				continue;

			// For each var in scope
			for (CtVariable aVarFromScope : varsInScope) {
				// If the var name are different
				if (!aVarFromScope.getSimpleName().equals(aVarFromAffected.getVariable().getSimpleName())) {

					// Let's check if the type are compatible (i.e., the same primitive type)
					if (compareTypes(aVarFromScope.getType(), aVarFromAffected.getType())) {

						boolean presentInExpression = varsAffectedInStatement.stream()
								.filter(e -> e.getVariable().getSimpleName().equals(aVarFromScope.getSimpleName()))
								.findFirst().isPresent();
						if (!presentInExpression) {
							hasCompatibleVarNoPresent = true;
							context.put(CNTX_Property.LE3_IS_COMPATIBLE_VAR_NOT_INCLUDED, hasCompatibleVarNoPresent);
							return;
						}
					}

				}
			}

		}
		context.put(CNTX_Property.LE3_IS_COMPATIBLE_VAR_NOT_INCLUDED, hasCompatibleVarNoPresent);

	}

	@Deprecated
	private boolean isparentBinaryComparison(CtElement element) {

		CtBinaryOperator binParent = element.getParent(CtBinaryOperator.class);

		if (binParent == null)
			return false;
		CtBinaryOperator binop = (CtBinaryOperator) binParent;
		if (binop.getKind().equals(BinaryOperatorKind.AND) || binop.getKind().equals(BinaryOperatorKind.OR)
				|| binop.getKind().equals(BinaryOperatorKind.EQ) || binop.getKind().equals(BinaryOperatorKind.NE)
				|| (binop.getType() != null && binop.getType().unbox().getSimpleName().equals("boolean")))
			return true;

//			return isLogicalExpressionInParent(currentElement.getParent(CtBinaryOperator.class));
//		}
		return false;
	}

	/**
	 * For the logical expression, whether there exists a boolean expression which
	 * is simply a boolean variable (i.e., not function call, equality comparison,
	 * etc.
	 * 
	 * @param varsAffectedInStatement
	 * @param varsInScope
	 * @param element
	 * @param context
	 */
	private void analyzeLE7_VarDirectlyUsed(List<CtVariableAccess> varsAffectedInStatement,
			List<CtVariable> varsInScope, CtElement element, Cntx<Object> context) {

		boolean hasVarDirectlyUsed = false;

		for (CtVariableAccess aVarFromAffected : varsAffectedInStatement) {

			CtElement parent = aVarFromAffected.getParent();
			if (parent instanceof CtExpression) {
				// First case: the parent is a binary
				if (isLogicalExpression((CtExpression) parent)) {
					hasVarDirectlyUsed = true;
					break;

				} else {

					// Second case: the parent is a negation
					if (parent instanceof CtUnaryOperator) {
						if (isLogicalExpression(((CtUnaryOperator) parent).getParent())) {
							hasVarDirectlyUsed = true;
							break;
						}

					}

				}
			}
		}
		context.put(CNTX_Property.LE7_SIMPLE_VAR_IN_LOGIC, hasVarDirectlyUsed);

	}

	private void analyzeAffectedWithCompatibleTypes(List<CtVariableAccess> varsAffected, List<CtVariable> varsInScope,
			CtElement element, Cntx<Object> context) {

		boolean hasSimType = false;
		for (CtVariableAccess aVariableAccessInStatement : varsAffected) {
			for (CtVariable aVariableInScope : varsInScope) {
				if (!aVariableInScope.getSimpleName()
						.equals(aVariableAccessInStatement.getVariable().getSimpleName())) {

					try {
						if (compareTypes(aVariableInScope.getType(), aVariableAccessInStatement.getType())) {
							hasSimType = true;
							context.put(CNTX_Property.HAS_VAR_SIM_TYPE, hasSimType);
							return;
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		}
		context.put(CNTX_Property.HAS_VAR_SIM_TYPE, hasSimType);
	}

	/**
	 * For each involved variable, is there any other variable in scope that is a
	 * certain function transformation of the involved variable
	 * 
	 * @param varsAffected
	 * @param element
	 * @param context
	 */
	@SuppressWarnings("rawtypes")
	private void analyzeV5_AffectedVariablesInTransformation(List<CtVariableAccess> varsAffected, CtElement element,
			Cntx<Object> context) {

		CtMethod methodParent = element.getParent(CtMethod.class);

		List<CtExpression> assignments = new ArrayList<>();

		CtScanner assignmentScanner = new CtScanner() {

			@Override
			public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> assignement) {
				if (assignement.getAssignment() != null)
					assignments.add(assignement.getAssignment());
			}

			@Override
			public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {
				if (localVariable.getAssignment() != null)
					assignments.add(localVariable.getAssignment());
			}

		};
		// Collect Assignments and var declaration (local)
		assignmentScanner.scan(methodParent);

		boolean v5_anyhasvar = false;
		// For each variable affected
		for (CtVariableAccess variableAffected : varsAffected) {

			boolean v5_currentVarHasvar = false;

			// For each assignment in the methid
			for (CtExpression assignment : assignments) {

				if (!isElementBeforeVariable(variableAffected, assignment))
					continue;

				// let's collect the var access in the right part
				List<CtVariableAccess> varsInRightPart = VariableResolver.collectVariableRead(assignment); // VariableResolver.collectVariableAccess(assignment);

				// if the var access in the right is the same that the affected
				for (CtVariableAccess varInAssign : varsInRightPart) {
					if (hasSameName(variableAffected, varInAssign)) {

						v5_anyhasvar = true;
						v5_currentVarHasvar = true;
						break;
					}
				}
			}
			writeDetailedInformationFromVariables(context, variableAffected.getVariable().getSimpleName(),
					CNTX_Property.V5_HAS_VAR_IN_TRANSFORMATION, (v5_currentVarHasvar));

		}
		context.put(CNTX_Property.V5_HAS_VAR_IN_TRANSFORMATION, v5_anyhasvar);

	}

	/**
	 * Return if one element is before the variable
	 * 
	 * @param variableAffected
	 * @param element
	 * @return
	 */
	private boolean isElementBeforeVariable(CtVariableAccess variableAffected, CtElement element) {

		try {
			CtStatement stst = (element instanceof CtStatement) ? (CtStatement) element
					: element.getParent(CtStatement.class);

			CtStatement target = (variableAffected instanceof CtStatement) ? (CtStatement) variableAffected
					: variableAffected.getParent(CtStatement.class);

			return target.getPosition() != null && getParentNotBlock(stst) != null
					&& target.getPosition().getSourceStart() > stst.getPosition().getSourceStart();
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return false;

	}

	/**
	 * :For any variablevinvolved in a logical expression, whetherexist other
	 * boolean expressions that involve using variablewhose type is same withv—note
	 * it is OK for the booleanexpression to also use some other variable types, we
	 * justrequire variable of typevis involved (as we do not assumhe availability
	 * of the whole program, we confine the searchof boolean expression in the same
	 * class) (–closure 20, theinvolved variable in the expression is value, whose
	 * type isNode, we can find there are other boolean expressions in thefaulty
	 * class that involve using variables of Node type, likearg.getNext() !=
	 * null–arg is Node type, callTarget.isName()–callTarget is Node type). (returns
	 * a single binary value,
	 * 
	 * @param varsAffected
	 * @param element
	 * @param context
	 */
	@SuppressWarnings("rawtypes")
	private void analyzeLE1_AffectedVariablesUsed(List<CtVariableAccess> varsAffected, CtElement element,
			Cntx<Object> context) {

		CtClass classParent = element.getParent(CtClass.class);

		if (classParent == null)
			return;

		List<CtStatement> statements = classParent.getElements(new LineFilter());

		int similarUsedBefore = 0;

		// For each variable affected
		for (CtVariableAccess variableAffected : varsAffected) {

			// boolean used = false;
			boolean foundSimilarVarUsed = false;

			boolean isInBinaryExpression = isLogicalExpressionInParent(variableAffected);

			if (!isInBinaryExpression)
				continue;

			// let's find other boolean expressions in the statements
			for (CtStatement aStatement : statements) {

				// let's find all binary expressions in the statement
				List<CtBinaryOperator> binaryOps = aStatement.getElements(e -> isLogicalExpression(e)).stream()
						.map(CtBinaryOperator.class::cast).collect(Collectors.toList());

				for (CtBinaryOperator ctBinaryOperator : binaryOps) {

					// retrieve all variables
					List<CtVariableAccess> varsInOtherExpressions = VariableResolver
							.collectVariableRead(ctBinaryOperator);
					for (CtVariableAccess varInAnotherExpression : varsInOtherExpressions) {
						if (!hasSameName(variableAffected, varInAnotherExpression)) {
							// Different name, so it's another variable

							// involve using variable whose type is same with v
							if (compareTypes(variableAffected.getVariable().getType(),
									varInAnotherExpression.getVariable().getType())) {
								foundSimilarVarUsed = true;
							}

						}

					}

				}

			}

			if (foundSimilarVarUsed)
				similarUsedBefore++;

		}

		context.put(CNTX_Property.LE1_EXISTS_RELATED_BOOLEAN_EXPRESSION, (similarUsedBefore) > 0);

	}

	/**
	 * // If the logical expression only uses local variables,whether all of the
	 * local variables have been used in other statements (exclude statements inside
	 * control flow structure) since the introduction
	 * 
	 * @param varsAffected
	 * @param element
	 * @param context
	 */
	@SuppressWarnings("rawtypes")
	private void analyzeLE8_LocalVariablesVariablesUsed(List<CtVariableAccess> varsAffected, CtElement element,
			Cntx<Object> context) {

		CtExecutable methodParent = element.getParent(CtExecutable.class);

		if (methodParent == null)
			return;

		List<CtStatement> statements = methodParent.getBody().getStatements();// methodParent.getElements(new
																				// LineFilter());

		// int similarUsedBefore = 0;
		boolean allLocalVariableUsed = true;
		// For each variable affected
		for (CtVariableAccess variableAffected : varsAffected) {

			boolean aVarUsed = false;

			if (variableAffected.getVariable().getType() != null
					&& !(variableAffected.getVariable().getDeclaration() instanceof CtLocalVariable)) {
				continue;
			}

			boolean isInBinaryExpression = isLogicalExpressionInParent(variableAffected);

			// For any variable involved in a logical expression,
			if (!isInBinaryExpression)
				continue;

			// For each assignment in the methid
			for (CtStatement aStatement : statements) {

				// ignoring control flow
				if (aStatement instanceof CtIf || aStatement instanceof CtLoop)
					continue;

				// ignoring statements after the faulty
				if (!isElementBeforeVariable(variableAffected, aStatement))
					continue;

				// let's collect the var access in the statement
				List<CtVariableAccess> varsReadInStatement = VariableResolver.collectVariableRead(aStatement);
				// if the var access in the right is the same that the affected
				for (CtVariableAccess varInStatement : varsReadInStatement) {
					if (hasSameName(variableAffected, varInStatement)) {
						aVarUsed = true;
					}
				}

			}
			// one variable is not used before the faulty
			if (!aVarUsed) {
				allLocalVariableUsed = false;
				break;
			}
		}

		context.put(CNTX_Property.LE_8_LOGICAL_WITH_USED_LOCAL_VARS, allLocalVariableUsed);

	}

	/**
	 * If the faulty statement involves object reference to local variables (i.e.,
	 * use object type local variables), do there exist certain referenced local
	 * variable(s) that have never been referenced in other statements (exclude
	 * statements inside control flow structure) before the faulty statement since
	 * its introduction (declaration) (chart-4)
	 * 
	 * @param varsAffected
	 * @param element
	 * @param context
	 */
	@SuppressWarnings("rawtypes")
	private void analyzeS1_AffectedVariablesUsed(List<CtVariableAccess> varsAffected, CtElement element,
			Cntx<Object> context) {

		CtExecutable methodParent = element.getParent(CtExecutable.class);

		if (methodParent == null)
			// the element is not in a method.
			return;

		List<CtStatement> statements = methodParent.getElements(new LineFilter());

		int usedObjects = 0;
		int notUsedObjects = 0;

		int usedObjectsLocal = 0;
		int usedPrimitiveLocal = 0;
		int notUsedObjectsLocal = 0;
		int notUsedPrimitiveLocal = 0;

		// For each variable affected
		for (CtVariableAccess variableAffected : varsAffected) {

			boolean aVarUsed = false;
			// boolean foundSimilarVarUsedBefore = false;

			// For each assignment in the methid
			for (CtStatement aStatement : statements) {

				if (!isElementBeforeVariable(variableAffected, aStatement))
					continue;

				// let's collect the var access in the right part
				List<CtVariableAccess> varsInRightPart = VariableResolver.collectVariableRead(aStatement);
				// if the var access in the right is the same that the affected
				for (CtVariableAccess varInStatement : varsInRightPart) {
					if (hasSameName(variableAffected, varInStatement)
							&& !(varInStatement.getVariable().getSimpleName() + " != null")
									.equals(varInStatement.getParent().toString())) {
						aVarUsed = true;
					}
				}
				if (aVarUsed)
					break;
			}
			// Now, let's check the type of the var to see if it's local or not
			if (variableAffected.getVariable().getType() != null) {

				if (!variableAffected.getVariable().getType().isPrimitive()) {
					if (aVarUsed)
						usedObjects++;
					else
						notUsedObjects++;

					if (variableAffected.getVariable().getDeclaration() instanceof CtLocalVariable) {
						if (aVarUsed)
							usedObjectsLocal++;
						else
							notUsedObjectsLocal++;
					}
				} else {

					if (variableAffected.getVariable().getType().isPrimitive()
							&& (variableAffected.getVariable().getDeclaration() instanceof CtLocalVariable))
						if (aVarUsed)
							usedPrimitiveLocal++;
						else
							notUsedPrimitiveLocal++;
				}
			}

		}
		context.put(CNTX_Property.NR_OBJECT_USED, usedObjects);
		context.put(CNTX_Property.NR_OBJECT_NOT_USED, notUsedObjects);

		context.put(CNTX_Property.NR_OBJECT_USED_LOCAL_VAR, usedObjectsLocal);
		context.put(CNTX_Property.NR_OBJECT_NOT_USED_LOCAL_VAR, notUsedObjectsLocal);

		context.put(CNTX_Property.NR_PRIMITIVE_USED_LOCAL_VAR, usedPrimitiveLocal);
		context.put(CNTX_Property.NR_PRIMITIVE_NOT_USED_LOCAL_VAR, notUsedPrimitiveLocal);

		context.put(CNTX_Property.S1_LOCAL_VAR_NOT_USED, (notUsedObjectsLocal) > 0);

	}

	private void analyzeS2_S5_SametypewithGuard(List<CtVariableAccess> varsAffected, CtElement element,
			Cntx<Object> context) {

		CtClass classParent = element.getParent(CtClass.class);
		CtExecutable faultyMethodParent = element.getParent(CtExecutable.class);

		if (classParent == null)
			// the element is not in a method.
			return;

		List<CtStatement> statements = classParent.getElements(new LineFilter());
		boolean hasPrimitiveSimilarTypeWithGuard = false;
		boolean hasObjectSimilarTypeWithGuard = false;

		// For each variable affected
		for (CtVariableAccess variableAffected : varsAffected) {

			// For each statement in the method (it includes the statements inside the
			// blocks (then, while)!)
			for (CtStatement aStatement : statements) {

				CtExecutable anotherStatmentMethodParent = aStatement.getParent(CtExecutable.class);

				if (anotherStatmentMethodParent.equals(faultyMethodParent)
						&& !isElementBeforeVariable(variableAffected, aStatement))
					continue;

				// let's collect the var access in the statement
				List<CtVariableAccess> varsFromStatement = VariableResolver
						.collectVariableReadIgnoringBlocks(aStatement);
				// if the var access is the same that the affected
				for (CtVariableAccess varInStatement : varsFromStatement) {
					// Has similar type but different name
					if (compareTypes(variableAffected.getVariable().getType(), varInStatement.getVariable().getType())
							&& !hasSameName(variableAffected, varInStatement)) {
						// Now, let's check if the parent is a guard
						// if (isGuard(getParentNotBlock(aStatement))) {
						if (isGuard(varInStatement, (aStatement))) {

							// it's ok, now let's check the type
							if (variableAffected.getType() != null) {

								if (variableAffected.getType().isPrimitive())
									hasPrimitiveSimilarTypeWithGuard = true;
								else
									hasObjectSimilarTypeWithGuard = true;
							}
						}

					}
				}
				// If we find both cases, we can stop
				if (hasPrimitiveSimilarTypeWithGuard && hasObjectSimilarTypeWithGuard)
					break;
			}
		}

		context.put(CNTX_Property.S2_SIMILAR_OBJECT_TYPE_WITH_GUARD, hasObjectSimilarTypeWithGuard);
		context.put(CNTX_Property.S5_SIMILAR_PRIMITIVE_TYPE_WITH_GUARD, hasPrimitiveSimilarTypeWithGuard);
	}

	/**
	 * Returns a parent that is not a block
	 * 
	 * @param aStatement
	 * @return
	 */
	public CtElement getParentNotBlock(CtElement aStatement) {

		if (aStatement == null)
			return null;
		if (aStatement.getParent() instanceof CtBlock)
			return getParentNotBlock(aStatement.getParent());

		return aStatement.getParent();
	}

	/**
	 * Return if the element is a guard
	 * 
	 * @param element
	 * @return
	 */
	private boolean isGuard(CtElement element) {

		// First, find the condition
		CtExpression condition = null;
		if (element instanceof CtIf) {

			CtIf guardCandidateIf = (CtIf) element;

			condition = guardCandidateIf.getCondition();

		} else if (element instanceof CtConditional) {
			CtConditional cond = (CtConditional) element;
			condition = cond.getCondition();

		}
		checkGuardCondition(condition);
		return false;
	}

	/**
	 * Return if the element is a guard
	 * 
	 * @param element
	 * @return
	 */
	private boolean isGuard(CtElement element, CtStatement parentStatement) {

		// Two cases: if and conditional
		CtExpression condition = null;
		CtConditional parentConditional = element.getParent(CtConditional.class);

		if (parentConditional != null) {// TODO, maybe force that the var must be in the condition, or not.
			CtConditional cond = (CtConditional) parentConditional;
			condition = cond.getCondition();
			return checkGuardCondition(condition);

		} else {
			CtElement parentElement = getParentNotBlock(parentStatement);
			// First, find the condition

			if (parentElement instanceof CtIf) {

				CtIf guardCandidateIf = (CtIf) parentElement;

				condition = guardCandidateIf.getCondition();

				boolean isConditionAGuard = checkGuardCondition(condition);
				return isConditionAGuard;
			}
		}
		return false;
	}

	/**
	 * Return if the Condition is a guard
	 * 
	 * @param condition
	 * @return
	 */
	public boolean checkGuardCondition(CtExpression condition) {
		if (condition != null) {
			List<CtBinaryOperator> binOp = condition.getElements(new TypeFilter<>(CtBinaryOperator.class));
			if (binOp != null && binOp.size() > 0) {

				for (CtBinaryOperator ctBinaryOperator : binOp) {
					if (ctBinaryOperator.getRightHandOperand().toString().equals("null")
							|| ctBinaryOperator.getLeftHandOperand().toString().equals("null")) {

						return true;
					}
				}
			}
			// If it's a unary, we keep the operand
			if (condition instanceof CtUnaryOperator) {
				condition = ((CtUnaryOperator) condition).getOperand();
			}
			// check if the if is a a boolean invocation
			if (condition instanceof CtInvocation) {

				// CtInvocation invocation = (CtInvocation) condition;
				// the method invocation must return a boolean, so not necessary to
				// check
				// if (invocation.getType() != null &&
				// invocation.getType().unbox().toString().equals("boolean"))
				return true;
			}

		}
		return false;
	}

	public boolean hasSameName(CtVariableAccess variableAffected, CtVariableAccess varInStatement) {
		return varInStatement.getVariable().getSimpleName().equals(variableAffected.getVariable().getSimpleName())
				|| varInStatement.equals(variableAffected);
	}

	public boolean isParentBooleanExpression(CtVariableAccess varInStatement) {

		CtExpression currentElement = varInStatement;
		CtExpression expressionsParent = null;
		do {
			expressionsParent = currentElement.getParent(CtExpression.class);
			if (expressionsParent != null) {
				currentElement = expressionsParent;

				// If it's binary, the result is Boolean
				boolean isLogical = isLogicalExpressionInParent(currentElement);
				if (isLogical)
					return true;

				// If we have the type, check if it's boolean
				if ((currentElement.getType() != null
						&& currentElement.getType().unbox().toString().equals("boolean"))) {
					return true;
				}
			}
		} while (expressionsParent != null);

		return false;

	}

	public boolean isLogicalExpressionInParent(CtElement currentElement) {
		if (currentElement == null)
			return false;
		if (isLogicalExpression(currentElement)) {
			return true;

		}
		return isLogicalExpressionInParent(currentElement.getParent(CtBinaryOperator.class));
	}

	public boolean isLogicalExpression(CtElement currentElement) {
		if (currentElement == null)
			return false;
		if ((currentElement instanceof CtBinaryOperator)) {
			CtBinaryOperator binop = (CtBinaryOperator) currentElement;
			if (binop.getKind().equals(BinaryOperatorKind.AND) || binop.getKind().equals(BinaryOperatorKind.OR)
					|| binop.getKind().equals(BinaryOperatorKind.EQ) || binop.getKind().equals(BinaryOperatorKind.NE)
			// || (binop.getType() != null &&
			// binop.getType().unbox().getSimpleName().equals("boolean"))
			)
				return true;

		}
		return false;
	}

	/**
	 * If the faulty statement involves object reference to local variables (i.e.,
	 * use object type local variables), do there exist certain referenced local
	 * variable(s) that have never been referenced in other statements (exclude
	 * statements inside control flow structure) before the faulty statement since
	 * its introduction (declaration)(chart-4)
	 * 
	 * @param varsAffected
	 * @param element
	 * @param context
	 */
	@SuppressWarnings("rawtypes")
	private void analyzeS1_AffectedAssigned(List<CtVariableAccess> varsAffected, CtElement element,
			Cntx<Object> context) {

		CtMethod methodParent = element.getParent(CtMethod.class);

		List<CtAssignment> assignments = new ArrayList<>();
		List<CtLocalVariable> localsVariable = new ArrayList<>();

		// Get all vars from variables
		CtScanner assignmentScanner = new CtScanner() {

			@Override
			public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> assignement) {

				assignments.add(assignement);
			}

			@Override
			public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {

				localsVariable.add(localVariable);
			}

		};

		assignmentScanner.scan(methodParent);
		boolean hasIncomplete = false;
		int nrOfVarWithAssignment = 0;
		int nrOfVarWithoutAssignment = 0;

		int nrOfLocalVarWithAssignment = 0;
		int nrOfLocalVarWithoutAssignment = 0;

		// For each variable affected
		for (CtVariableAccess variableAffected : varsAffected) {

			boolean varHasAssig = false;
			// For each assignment in the method
			for (CtAssignment assignment : assignments) {

				if (!isElementBeforeVariable(variableAffected, assignment))
					continue;

				if (assignment.getAssigned().toString().equals(variableAffected.getVariable().getSimpleName())) {
					varHasAssig = true;
				}
				boolean incomplete = retrieveNotAllInitialized(variableAffected, assignment, assignments);
				if (incomplete) {
					hasIncomplete = true;
				}
			}
			// Let's find in local declaration
			// if it was not assigned before
			if (!varHasAssig) {

				for (CtLocalVariable ctLocalVariable : localsVariable) {

					if (!isElementBeforeVariable(variableAffected, ctLocalVariable))
						continue;

					if (ctLocalVariable.getReference().getSimpleName()
							.equals(variableAffected.getVariable().getSimpleName())
							&& ctLocalVariable.getDefaultExpression() != null
							&& !"null".equals(ctLocalVariable.getDefaultExpression().toString()))
						varHasAssig = true;
				}

			}

			if (varHasAssig)
				nrOfVarWithAssignment++;
			else
				nrOfVarWithoutAssignment++;

			if (variableAffected.getVariable().getDeclaration() instanceof CtLocalVariable) {
				if (varHasAssig)
					nrOfLocalVarWithAssignment++;
				else
					nrOfLocalVarWithoutAssignment++;
			}

		}
		context.put(CNTX_Property.NR_VARIABLE_ASSIGNED, nrOfVarWithAssignment);
		context.put(CNTX_Property.NR_VARIABLE_NOT_ASSIGNED, nrOfVarWithoutAssignment);
		context.put(CNTX_Property.NR_FIELD_INCOMPLETE_INIT, hasIncomplete);
		context.put(CNTX_Property.NR_OBJECT_ASSIGNED_LOCAL, nrOfLocalVarWithAssignment);
		context.put(CNTX_Property.NR_OBJECT_NOT_ASSIGNED_LOCAL, nrOfLocalVarWithoutAssignment);

		// S1 is if NR_OBJECT_ASSIGNED_LOCAL > 0 then
		// if NR_VARIABLE_NOT_ASSIGNED = 0 then S1 = false else S1 = true
		// Else S1= false

		context.put(CNTX_Property.S1_LOCAL_VAR_NOT_ASSIGNED, (nrOfLocalVarWithoutAssignment > 0));
	}

	/**
	 * // If the faulty statement involves object reference to field (i.e., use
	 * object type class field), do there exist certain field(s) that have never
	 * been referenced in other methods of the faulty class.
	 * 
	 * @param varsAffected
	 * @param element
	 * @param context
	 */
	@SuppressWarnings("rawtypes")
	private void analyzeS4_AffectedFielfs(List<CtVariableAccess> varsAffected, CtElement element,
			Cntx<Object> context) {

		CtMethod methodParent = element.getParent(CtMethod.class);
		CtClass xclass = element.getParent(CtClass.class);

		boolean hasFieldNeverUsedOutside = false;
		// For each variable affected in the faulty statement
		for (CtVariableAccess variableAffected : varsAffected) {

			// if it's a field
			if (variableAffected instanceof CtFieldAccess) {

				boolean isFieldUsed = false;

				// For the other methods
				for (Object amethod : xclass.getAllMethods()) {

					CtMethod anotherMethod = (CtMethod) amethod;
					// ignore current method (where is the faulty)
					if (amethod.equals(methodParent))
						continue;

					// get all field access on the method
					List<CtElement> fieldsaccsess = anotherMethod.getElements(e -> e instanceof CtFieldAccess);
					for (CtElement ef : fieldsaccsess) {
						// check is the access is the same from that one used in the faulty
						CtFieldAccess faccess = (CtFieldAccess) ef;
						if (faccess.getVariable().getSimpleName()
								.equals(variableAffected.getVariable().getSimpleName())) {
							isFieldUsed = true;
						}
					}

				}
				// If the filed is never used
				if (!isFieldUsed)
					hasFieldNeverUsedOutside = true;

			}
		}
		context.put(CNTX_Property.S4_USED_FIELD, hasFieldNeverUsedOutside);
	}

	@SuppressWarnings("rawtypes")
	private boolean retrieveNotAllInitialized(CtVariableAccess variableAffected, CtAssignment assignment,
			List<CtAssignment> assignments) {

		// Let's check if the var is a field reader to an Object
		String varAffected = null;
		if (variableAffected instanceof CtFieldRead) {
			CtFieldRead fr = (CtFieldRead) ((CtFieldRead) variableAffected);

			if (fr.getVariable().getType() == null || fr.getVariable().getType().isPrimitive()
					|| fr.getVariable().getDeclaration() == null) {

				return false;
			}
			varAffected = fr.getVariable().getDeclaration().getSimpleName();
		} else {
			return false;
		}

		// let's part of the assignment that we want to check if it's a field assignment
		CtExpression leftPAssignment = assignment.getAssigned();

		if (leftPAssignment instanceof CtFieldWrite) {
			// Field assignment
			CtFieldWrite fieldW = (CtFieldWrite) leftPAssignment;

			// here lets take fX
			if (fieldW.getTarget() instanceof CtFieldRead) {

				// The object where the assignment is done.
				CtVariableRead targetField = (CtVariableRead) fieldW.getTarget();
				// check if the variable is the same than the affected
				if (targetField.getVariable().getSimpleName().toString().equals(varAffected)) {

					Collection<CtFieldReference<?>> fields = targetField.getVariable().getType().getAllFields();
					String initialVar = targetField.getVariable().getSimpleName();
					for (CtFieldReference<?> otherFieldsFromVar : fields) {

						if (!otherFieldsFromVar.getFieldDeclaration().getVisibility().equals(ModifierKind.PRIVATE)
								&& otherFieldsFromVar.getSimpleName() != initialVar) {
							boolean fieldAssigned = false;
							for (CtAssignment otherAssignment : assignments) {

								// if (otherAssignment != assignment) {

								CtExpression leftOther = otherAssignment.getAssigned();

								if (leftOther instanceof CtFieldWrite) {
									CtFieldWrite fwriteOther = (CtFieldWrite) leftOther;
									// Let's check the
									if (// isElementBeforeVariable(fwriteOther, element)
										// &&
									fwriteOther.getVariable().getSimpleName()
											.equals(otherFieldsFromVar.getSimpleName())) {
										if (fwriteOther.getTarget() instanceof CtVariableRead) {
											CtVariableRead otherVar = (CtVariableRead) fwriteOther.getTarget();

											if (otherVar.getVariable().getSimpleName().equals(varAffected)) {

												fieldAssigned = true;
												break;
											}
										}
									}

								}

							}
							if (!fieldAssigned) {
								return true;
							}
						}
					}

				}
			}
		}
		return false;
	}

	/**
	 * For each involved variable, whether has any other variables in scope that are
	 * similar in identifier name and type compatible
	 * 
	 * @param varsAffected
	 * @param varsInScope
	 * @param element
	 * @param context
	 */
	private void analyzeV2_AffectedDistanceVarName(List<CtVariableAccess> varsAffected, List<CtVariable> varsInScope,
			CtElement element, Cntx<Object> context) {

		boolean anyhasMinDist = false;
		boolean v2SimilarNameCompatibleType = false;

		for (CtVariableAccess aVarAffected : varsAffected) {

			boolean v2VarSimilarNameCompatibleType = false;

			for (CtVariable aVarInScope : varsInScope) {
				if (!aVarInScope.getSimpleName().equals(aVarAffected.getVariable().getSimpleName())) {
					int dist = StringDistance.calculate(aVarInScope.getSimpleName(),
							aVarAffected.getVariable().getSimpleName());
					if (dist > 0 && dist < 3) {
						anyhasMinDist = true;

						if (compareTypes(aVarAffected.getType(), aVarInScope.getType())) {
							v2SimilarNameCompatibleType = true;
							v2VarSimilarNameCompatibleType = true;
							// to save computation
							break;
						}
					}

				}
			}
			writeDetailedInformationFromVariables(context, aVarAffected.getVariable().getSimpleName(),
					CNTX_Property.V2_HAS_VAR_SIM_NAME_COMP_TYPE, (v2VarSimilarNameCompatibleType));

		}
		context.put(CNTX_Property.HAS_VAR_SIM_NAME, anyhasMinDist);
		context.put(CNTX_Property.V2_HAS_VAR_SIM_NAME_COMP_TYPE, v2SimilarNameCompatibleType);

	}

	/**
	 * For each involved variable, is it constant?–can assumevariables whose
	 * identifier names are majorly capital lettersare constant variables
	 * 
	 * @param varsAffected
	 * @param varsInScope
	 * @param element
	 * @param context
	 */
	private void analyzeV3_AffectedHasConstant(List<CtVariableAccess> varsAffected, CtElement element,
			Cntx<Object> context) {

		boolean hasConstant = false;
		for (CtVariableAccess aVarAffected : varsAffected) {
			boolean currentIsConstant = false;
			if (aVarAffected.getVariable() instanceof CtFieldReference &&
			// Check if it's uppercase
					aVarAffected.getVariable().getSimpleName().toUpperCase()
							.equals(aVarAffected.getVariable().getSimpleName())) {
				hasConstant = true;
				currentIsConstant = true;

			}
			writeDetailedInformationFromVariables(context, aVarAffected.getVariable().getSimpleName(),
					CNTX_Property.V3_HAS_CONSTANT, (currentIsConstant));

		}
		context.put(CNTX_Property.V3_HAS_CONSTANT, hasConstant);

	}

	/**
	 * Check is a variable affected is compatible with a method type or parameter
	 * 
	 * @param varsAffected
	 * @param element
	 * @param context
	 */
	private void analyzeV1_VX_V6_AffectedVariablesInMethod(List<CtVariableAccess> varsAffected, CtElement element,
			Cntx<Object> context) {
		try {
			// For each involved variable, whether has method definitions or method calls
			// (in the fault class) that take the type of the involved variable as one of
			// its parameters and the return type of the method is type compatible with the
			// type of the involved variable
			boolean v1CompatibleReturnAndParameterTypes = false;
			// If statement involves variables, whether has methods in scope that take the
			// type of the involved variable as parameter
			boolean v4paramCompatible = false;
			// If statement involves variables, whether has methods in scope that return
			// the type of the involved variable
			boolean v6returnCompatible = false;
			// For any variable involved in a logical expression,whether exist methods
			// (method declaration or method call) in scope (that is in the same faulty
			// class
			// since we do not assume full program) that take variable whose type is same
			// with vas one of its parameters and return boolean
			boolean les2paramCompatibleWithBooleanReturn = false;

			CtClass parentClass = element.getParent(CtClass.class);

			List allMethods = getAllMethodsFromClass(parentClass);

			for (CtVariableAccess varAffected : varsAffected) {

				boolean v6InvReturnCompatible = false;
				boolean v4InvparamCompatible = false;
				boolean les2InvparamCompatibleWithBooleanReturn = false;
				boolean v1CurrentCompatibleReturnAndParameterTypes = false;

				for (Object omethod : allMethods) {
					boolean matchInmethodType = false;
					boolean matchInmethodReturn = false;

					if (!(omethod instanceof CtMethod))
						continue;

					CtMethod anotherMethodInBuggyClass = (CtMethod) omethod;

					// Get the return type
					if (anotherMethodInBuggyClass.getType() != null) {

						if (isSubtype(varAffected, anotherMethodInBuggyClass)) {
							v6returnCompatible = true;
							matchInmethodReturn = true;
							v6InvReturnCompatible = true;

						}

					}
					// Check the parameters
					for (Object oparameter : anotherMethodInBuggyClass.getParameters()) {
						CtParameter parameter = (CtParameter) oparameter;

						if (compareTypes(varAffected.getType(), parameter.getType())) {
							v4paramCompatible = true;
							v4InvparamCompatible = true;
							matchInmethodType = true;
							if (anotherMethodInBuggyClass.getType().unbox().toString().equals("boolean")) {
								les2paramCompatibleWithBooleanReturn = true;
								les2InvparamCompatibleWithBooleanReturn = true;
							}

						}
					}
					// check both return type and parameter
					if (matchInmethodType && matchInmethodReturn) {
						v1CompatibleReturnAndParameterTypes = true;
						v1CurrentCompatibleReturnAndParameterTypes = true;
					}

					if (v4paramCompatible && v6returnCompatible && les2paramCompatibleWithBooleanReturn
							&& v1CompatibleReturnAndParameterTypes) {
						break;
					}

				}

				writeDetailedInformationFromVariables(context, varAffected.getVariable().getSimpleName(),
						CNTX_Property.V1_IS_TYPE_COMPATIBLE_METHOD_CALL_PARAM_RETURN,
						(v1CurrentCompatibleReturnAndParameterTypes));

				writeDetailedInformationFromVariables(context, varAffected.getVariable().getSimpleName(),
						CNTX_Property.V_X_BIS_IS_METHOD_PARAM_TYPE_VAR, v4InvparamCompatible);

				writeDetailedInformationFromVariables(context, varAffected.getVariable().getSimpleName(),
						CNTX_Property.V6_IS_METHOD_RETURN_TYPE_VAR, v6InvReturnCompatible);

				writeDetailedInformationFromVariables(context, varAffected.getVariable().getSimpleName(),
						CNTX_Property.LE2_IS_BOOLEAN_METHOD_PARAM_TYPE_VAR, (les2InvparamCompatibleWithBooleanReturn));

			}

			context.put(CNTX_Property.V1_IS_TYPE_COMPATIBLE_METHOD_CALL_PARAM_RETURN,
					(v1CompatibleReturnAndParameterTypes));

			context.put(CNTX_Property.V_X_BIS_IS_METHOD_PARAM_TYPE_VAR, v4paramCompatible);
			context.put(CNTX_Property.V6_IS_METHOD_RETURN_TYPE_VAR, v6returnCompatible);

			context.put(CNTX_Property.LE2_IS_BOOLEAN_METHOD_PARAM_TYPE_VAR, (les2paramCompatibleWithBooleanReturn));

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * // For any variable involved in a logical expression,whether exist methods //
	 * (method declaration or method call) in scope (that is in the same faulty //
	 * class // since we do not assume full program) that take variable whose type
	 * is same // with vas one of its parameters and return boolean
	 * 
	 * @param varsAffected
	 * @param element
	 * @param context
	 */
	private void analyzeLE2_AffectedVariablesInMethod(List<CtVariableAccess> varsAffected, CtElement element,
			Cntx<Object> context) {
		try {

			boolean les2paramCompatibleWithBooleanReturn = false;
			CtClass parentClass = element.getParent(CtClass.class);

			// First check method declarations

			List<CtInvocation> invocationsFromClass = parentClass.getElements(e -> (e instanceof CtInvocation)).stream()
					.map(CtInvocation.class::cast).collect(Collectors.toList());

			List allMethods = getAllMethodsFromClass(parentClass);

			for (CtVariableAccess varAffected : varsAffected) {

				if (!isParentBooleanExpression(varAffected))
					continue;

				boolean les2InvparamCompatibleWithBooleanReturn = false;
				// First, Let's analyze the method declaration

				if (checkMethodDeclarationWithTypeInParameter(allMethods, varAffected) != null) {
					les2paramCompatibleWithBooleanReturn = true;
					les2InvparamCompatibleWithBooleanReturn = true;
				}
				// Second, let's inspect invocations
				else {

					if (checkInvocationWithTypeInParameter(invocationsFromClass, varAffected) != null) {
						les2paramCompatibleWithBooleanReturn = true;
						les2InvparamCompatibleWithBooleanReturn = true;
					}

				}

				writeDetailedInformationFromVariables(context, varAffected.getVariable().getSimpleName(),
						CNTX_Property.LE2_IS_BOOLEAN_METHOD_PARAM_TYPE_VAR, (les2InvparamCompatibleWithBooleanReturn));

			}

			context.put(CNTX_Property.LE2_IS_BOOLEAN_METHOD_PARAM_TYPE_VAR, (les2paramCompatibleWithBooleanReturn));

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	/**
	 * Check if a method declaration has a parameter compatible with that one from
	 * the var affected
	 * 
	 * @param allMethods
	 * @param varAffected
	 * @return
	 */
	public CtMethod checkMethodDeclarationWithTypeInParameter(List allMethods, CtVariableAccess varAffected) {
		for (Object omethod : allMethods) {

			if (!(omethod instanceof CtMethod))
				continue;

			CtMethod anotherMethodInBuggyClass = (CtMethod) omethod;

			// Check the parameters
			for (Object oparameter : anotherMethodInBuggyClass.getParameters()) {
				CtParameter parameter = (CtParameter) oparameter;

				if (compareTypes(varAffected.getType(), parameter.getType())) {
					if (anotherMethodInBuggyClass.getType().unbox().toString().equals("boolean")) {

						return anotherMethodInBuggyClass;
					}

				}
			}

		}
		return null;
	}

	/**
	 * Return if there is an invocation with an argument of the same type of the Var
	 * Access
	 * 
	 * @param invocationsFromClass
	 * @param varAffected
	 * @return
	 */
	public CtInvocation checkInvocationWithTypeInParameter(List<CtInvocation> invocationsFromClass,
			CtVariableAccess varAffected) {
		// For each invocation found in the class
		for (CtInvocation anInvocation : invocationsFromClass) {

			// For each argument in the invocation
			for (Object anObjArgument : anInvocation.getArguments()) {
				CtExpression anArgument = (CtExpression) anObjArgument;

				// retrieve Var access

				List<CtVariableAccess> varReadFromArguments = VariableResolver.collectVariableRead(anArgument);

				for (CtVariableAccess aVarReadFrmArgument : varReadFromArguments) {

					//
					if (compareTypes(varAffected.getType(), aVarReadFrmArgument.getType())) {
						if (anInvocation.getType() == null
								|| anInvocation.getType().unbox().toString().equals("boolean")) {
							return anInvocation;
						}

					}

				}

			}

		}
		return null;
	}

	private boolean isSubtype(CtVariableAccess var, CtMethod method) {
		try {
			return method.getType().isSubtypeOf(var.getType());
		} catch (Exception e) {
			return false;
		}
	}

	public static boolean compareTypes(CtTypeReference t1, CtTypeReference t2) {
		try {
			return t1 != null && t2 != null && (t1.toString().equals(t2.toString()) || t1.equals(t2)
					|| t1.isSubtypeOf(t2) || t2.isSubtypeOf(t1));
		} catch (Exception e) {
			System.out.println("Error comparing types");
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * For the logical expression, whether there exists a boolean expression that
	 * starts with the "not" operator! (an exclamation mark)
	 * 
	 * @param element
	 * @param context
	 * @param parentContext
	 */
	private void analyzeLE6_UnaryInvolved(CtElement element, Cntx<Object> parentContext) {

		Cntx<Object> context = new Cntx<>();
		parentContext.put(CNTX_Property.UNARY_PROPERTIES, context);

		List<String> binOps = new ArrayList();
		CtScanner scanner = new CtScanner() {

			@Override
			public <T> void visitCtUnaryOperator(CtUnaryOperator<T> operator) {

				super.visitCtUnaryOperator(operator);
				binOps.add(operator.getKind().toString());
			}

		};

		ExpressionCapturerScanner expressionScanner = new ExpressionCapturerScanner();
		expressionScanner.scan(element);
		if (expressionScanner.toScan != null) {
			scanner.scan(expressionScanner.toScan);
		} else {
			scanner.scan(element);
		}
		context.put(CNTX_Property.involved_relation_unary_operators, binOps);

		context.put(CNTX_Property.involve_POS_relation_operators, binOps.contains(UnaryOperatorKind.POS.toString()));
		context.put(CNTX_Property.involve_NEG_relation_operators, binOps.contains(UnaryOperatorKind.NEG.toString()));
		boolean containsNot = binOps.contains(UnaryOperatorKind.NOT.toString());
		context.put(CNTX_Property.involve_NOT_relation_operators, containsNot);
		context.put(CNTX_Property.involve_COMPL_relation_operators,
				binOps.contains(UnaryOperatorKind.COMPL.toString()));
		context.put(CNTX_Property.involve_PREINC_relation_operators,
				binOps.contains(UnaryOperatorKind.PREINC.toString()));
		context.put(CNTX_Property.involve_PREDEC_relation_operators,
				binOps.contains(UnaryOperatorKind.PREDEC.toString()));
		context.put(CNTX_Property.involve_POSTINC_relation_operators,
				binOps.contains(UnaryOperatorKind.POSTINC.toString()));
		context.put(CNTX_Property.involve_POSTDEC_relation_operators,
				binOps.contains(UnaryOperatorKind.POSTDEC.toString()));

		parentContext.put(CNTX_Property.LE6_HAS_NEGATION, containsNot);

	}

	/**
	 * Whether the number of boolean expressions in the logical expression is larger
	 * than 1
	 * 
	 * @param element
	 * @param context
	 * @param parentContext
	 */
	private void analyzeLE5_BinaryInvolved(CtElement element, Cntx<Object> parentContext) {

		Cntx<Object> context = new Cntx<>();
		parentContext.put(CNTX_Property.BIN_PROPERTIES, context);

		List<String> binOps = new ArrayList();
		CtScanner scanner = new CtScanner() {

			@Override
			public <T> void visitCtBinaryOperator(CtBinaryOperator<T> operator) {
				super.visitCtBinaryOperator(operator);
				binOps.add(operator.getKind().toString());
			}

		};
		// CtElement toScan = null;
		ExpressionCapturerScanner scanner2 = new ExpressionCapturerScanner();
		scanner2.scan(element);
		if (scanner2.toScan != null) {
			scanner.scan(scanner2.toScan);
		} else {
			scanner.scan(element);
		}
		context.put(CNTX_Property.involved_relation_bin_operators, binOps);

		context.put(CNTX_Property.involve_GE_relation_operators, binOps.contains(BinaryOperatorKind.GE.toString()));
		boolean containsAnd = binOps.contains(BinaryOperatorKind.AND.toString());
		context.put(CNTX_Property.involve_AND_relation_operators, containsAnd);
		boolean containsOr = binOps.contains(BinaryOperatorKind.OR.toString());
		context.put(CNTX_Property.involve_OR_relation_operators, containsOr);
		boolean containsBitor = binOps.contains(BinaryOperatorKind.BITOR.toString());
		context.put(CNTX_Property.involve_BITOR_relation_operators, containsBitor);
		boolean containsBitxor = binOps.contains(BinaryOperatorKind.BITXOR.toString());
		context.put(CNTX_Property.involve_BITXOR_relation_operators, containsBitxor);
		boolean containsBitand = binOps.contains(BinaryOperatorKind.BITAND.toString());
		context.put(CNTX_Property.involve_BITAND_relation_operators, containsBitand);
		context.put(CNTX_Property.involve_EQ_relation_operators, binOps.contains(BinaryOperatorKind.EQ.toString()));
		context.put(CNTX_Property.involve_NE_relation_operators, binOps.contains(BinaryOperatorKind.NE.toString()));
		context.put(CNTX_Property.involve_LT_relation_operators, binOps.contains(BinaryOperatorKind.LT.toString()));
		context.put(CNTX_Property.involve_GT_relation_operators, binOps.contains(BinaryOperatorKind.GT.toString()));
		context.put(CNTX_Property.involve_LE_relation_operators, binOps.contains(BinaryOperatorKind.LE.toString()));
		context.put(CNTX_Property.involve_SL_relation_operators, binOps.contains(BinaryOperatorKind.SL.toString()));
		context.put(CNTX_Property.involve_SR_relation_operators, binOps.contains(BinaryOperatorKind.SR.toString()));
		context.put(CNTX_Property.involve_USR_relation_operators, binOps.contains(BinaryOperatorKind.USR.toString()));
		context.put(CNTX_Property.involve_PLUS_relation_operators, binOps.contains(BinaryOperatorKind.PLUS.toString()));
		context.put(CNTX_Property.involve_MINUS_relation_operators,
				binOps.contains(BinaryOperatorKind.MINUS.toString()));
		context.put(CNTX_Property.involve_MUL_relation_operators, binOps.contains(BinaryOperatorKind.MUL.toString()));
		context.put(CNTX_Property.involve_DIV_relation_operators, binOps.contains(BinaryOperatorKind.DIV.toString()));
		context.put(CNTX_Property.involve_MOD_relation_operators, binOps.contains(BinaryOperatorKind.MOD.toString()));

		context.put(CNTX_Property.involve_INSTANCEOF_relation_operators,
				binOps.contains(BinaryOperatorKind.INSTANCEOF.toString()));

		parentContext.put(CNTX_Property.LE5_BOOLEAN_EXPRESSIONS_IN_FAULTY,
				(containsAnd || containsBitand || containsBitor || containsBitxor || containsOr));

	}

	private void retrieveType(CtElement element, Cntx<Object> context) {
		context.put(CNTX_Property.TYPE, element.getClass().getSimpleName());

	}

	private void retrievePosition(CtElement element, Cntx<Object> context) {
		if (element.getPosition() != null && element.getPosition().getFile() != null) {
			context.put(CNTX_Property.FILE_LOCATION, element.getPosition().getFile().getAbsolutePath());

			context.put(CNTX_Property.LINE_LOCATION, element.getPosition().getLine());
		} else {
			context.put(CNTX_Property.FILE_LOCATION, "");
			context.put(CNTX_Property.LINE_LOCATION, "");

		}
		CtType parentClass = element.getParent(spoon.reflect.declaration.CtType.class);

		context.put(CNTX_Property.PARENT_CLASS, (parentClass != null) ? parentClass.getQualifiedName() : "");

	}

	public Object determineKey(CtElement element) {
		String key = null;
		if (element.getPosition() != null && element.getPosition().getFile() != null) {
			key = element.getPosition().getFile().getName().toString();
		} else {
			key = element.getShortRepresentation();// To see.
		}
		return key;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void analyzeFeatures(CtElement element, Cntx<Object> context) {
		// Vars in scope at the position of element
		List<CtVariable> varsInScope = VariableResolver.searchVariablesInScope(element);
		putVarInContextInformation(context, varsInScope);

		List<CtVariableAccess> varsAffected = VariableResolver.collectVariableRead(element);
		analyzeTypesVarsAffected(varsAffected, element, context);
		analyzeS1_AffectedAssigned(varsAffected, element, context);
		analyzeS1_AffectedVariablesUsed(varsAffected, element, context);
		analyzeS2_S5_SametypewithGuard(varsAffected, element, context);
		analyzeS3_TypeOfFaulty(element, context);
		analyzeS4_AffectedFielfs(varsAffected, element, context);
		analyzeS6_Method_ExceptionAndInformation(element, context);

		analyzeV1_VX_V6_AffectedVariablesInMethod(varsAffected, element, context);
		analyzeV2_AffectedDistanceVarName(varsAffected, varsInScope, element, context);
		analyzeV3_AffectedHasConstant(varsAffected, element, context);
		analyzeV4(varsAffected, element, context);
		analyzeV5_AffectedVariablesInTransformation(varsAffected, element, context);

		analyzeM1_eM2_M3_M4_SimilarMethod(element, context);

		analyzeLE1_AffectedVariablesUsed(varsAffected, element, context);
		analyzeLE2_AffectedVariablesInMethod(varsAffected, element, context);
		analyzeLE3_PrimitiveWithCompatibleNotUsed(varsAffected, varsInScope, element, context);
		analyzeLE4_BooleanVarNotUsed(varsAffected, varsInScope, element, context);
		analyzeLE5_BinaryInvolved(element, context);
		analyzeLE6_UnaryInvolved(element, context);
		analyzeLE7_VarDirectlyUsed(varsAffected, varsInScope, element, context);
		analyzeLE8_LocalVariablesVariablesUsed(varsAffected, element, context);
		// Other features not enumerated
		analyzeAffectedWithCompatibleTypes(varsAffected, varsInScope, element, context);
		analyzeParentTypes(element, context);
		analyzeUseEnumAndConstants(element, context);

	}

	public void analyzeS3_TypeOfFaulty(CtElement element, Cntx<Object> context) {
		String type = element.getClass().getSimpleName().replaceAll("Ct", "").replaceAll("Impl", "");
		context.put(CNTX_Property.S3_TYPE_OF_FAULTY_STATEMENT, type);
	}

	/**
	 * Puts in the context's object the information of each var in scope
	 * 
	 * @param context
	 * @param varsInScope
	 */
	public void putVarInContextInformation(Cntx<Object> context, List<CtVariable> varsInScope) {
		context.put(CNTX_Property.VARS_IN_SCOPE, varsInScope);
		List<Cntx> children = new ArrayList();
		for (CtVariable ctVariable : varsInScope) {
			Cntx c = new Cntx<>();
			c.put(CNTX_Property.VAR_VISIB,
					(ctVariable.getVisibility() == null) ? "" : (ctVariable.getVisibility()).toString());
			c.put(CNTX_Property.VAR_TYPE, ctVariable.getType().getQualifiedName());
			c.put(CNTX_Property.VAR_MODIF, ctVariable.getModifiers());
			c.put(CNTX_Property.VAR_NAME, ctVariable.getSimpleName());
			children.add(c);

		}
		context.put(CNTX_Property.VARS, children);
	}

	/**
	 * Similar methods
	 * 
	 * @param element
	 * @param context
	 */
	private void analyzeM1_eM2_M3_M4_SimilarMethod(CtElement element, Cntx<Object> context) {

		CtClass parentClass = element.getParent(CtClass.class);
		// For each method invocation, whether the method has overloaded method
		boolean m1anyhasSameName = false;
		// For each method invocation, whether there exist methods that return the same
		// type (or type compatible) and are similar in identifier name with the called
		// method (again, we limit the search to the faulty class, search both method
		// definition and method invocations in the faulty class
		boolean m2anyhasMinDist = false;
		// For each method invocation, whether has method definitions or method calls
		// (in the fault class) that take the return type of the method invocation as
		// one
		// of its parameters and the return type of the method is type compatible with
		// the return type of the method invocation.
		boolean m3anyhasCompatibleParameterAndReturnWithOtherMethod = false;
		// For each method invocation, whether the types of some of its parameters are
		// same or compatible with the return type of the method.
		boolean m4anyhasCompatibleParameterAndReturnSameMethod = false;

		// Get all invocations inside the faulty element
		List<CtInvocation> invocations = element.getElements(e -> (e instanceof CtInvocation)).stream()
				.map(CtInvocation.class::cast).collect(Collectors.toList());

		for (CtInvocation invocation : invocations) {
			CtExecutable minvokedInAffected = invocation.getExecutable().getDeclaration();

			if (minvokedInAffected == null || !(minvokedInAffected instanceof CtMethod))
				continue;

			boolean m1methodHasSameName = false;
			boolean m2methodhasMinDist = false;
			boolean m3methodhasCompatibleParameterAndReturnWithOtherMethod = false;
			boolean m4methodHasCompatibleParameterAndReturnSameMethod = false;

			// Get the method that is invoked
			CtMethod affectedMethod = (CtMethod) minvokedInAffected;

			// Check parameters
			for (Object oparameter : affectedMethod.getParameters()) {
				CtParameter parameter = (CtParameter) oparameter;

				if (affectedMethod != null && compareTypes(affectedMethod.getType(), parameter.getType())) {
					m4anyhasCompatibleParameterAndReturnSameMethod = true;
					m4methodHasCompatibleParameterAndReturnSameMethod = true;
				}
			}

			List allMethodsFromClass = getAllMethodsFromClass(parentClass);

			// For each method in the class
			for (Object omethod : allMethodsFromClass) {

				if (!(omethod instanceof CtMethod))
					continue;

				CtMethod anotherMethod = (CtMethod) omethod;
				// Ignoring if it's the same
				if (anotherMethod == null || anotherMethod.getSignature().equals(affectedMethod.getSignature()))
					continue;

				if (anotherMethod.getSimpleName().equals(affectedMethod.getSimpleName())) {
					// It's override
					m1methodHasSameName = true;
					m1anyhasSameName = true;
				}
				// If the return types are compatibles
				if (anotherMethod.getType() != null && minvokedInAffected.getType() != null) {

					// Check if the method has the return type compatible with the affected
					// invocation
					boolean compatibleReturnTypes = compareTypes(anotherMethod.getType(), minvokedInAffected.getType());
					if (compatibleReturnTypes) {
						// Check name similarity:
						int dist = StringDistance.calculate(anotherMethod.getSimpleName(),
								minvokedInAffected.getSimpleName());
						if (dist > 0 && dist < 3) {
							m2anyhasMinDist = true;
							context.put(CNTX_Property.M2_SIMILAR_METHOD_WITH_SAME_RETURN, m2anyhasMinDist);
							m2methodhasMinDist = true;
						}

						// Check if the method has a parameter compatible with the affected invocation
						boolean hasSameParam = checkTypeInParameter(anotherMethod, minvokedInAffected);
						if (hasSameParam) {
							m3anyhasCompatibleParameterAndReturnWithOtherMethod = true;
							m3methodhasCompatibleParameterAndReturnWithOtherMethod = true;
						}
					}
				}
				// if the other method is not similar method for M3, let's find in the
				// invocation inside the .
				if (!m3methodhasCompatibleParameterAndReturnWithOtherMethod) {

					List<CtInvocation> invocationsFromAnotherMethod = anotherMethod
							.getElements(e -> (e instanceof CtInvocation)).stream().map(CtInvocation.class::cast)
							.collect(Collectors.toList());
					for (CtInvocation ctInvocation : invocationsFromAnotherMethod) {
						CtExecutable methodInvokedInAnotherMethod = ctInvocation.getExecutable().getDeclaration();

						if (methodInvokedInAnotherMethod != null) {

							if (compareTypes(anotherMethod.getType(), minvokedInAffected.getType())
									&& checkTypeInParameter(anotherMethod, minvokedInAffected)) {
								m3anyhasCompatibleParameterAndReturnWithOtherMethod = true;
								m3methodhasCompatibleParameterAndReturnWithOtherMethod = true;
							}

						}

					}

				}

			}
			writeDetailedInformationFromMethod(context, affectedMethod, CNTX_Property.M4_PARAMETER_RETURN_COMPABILITY,
					m4methodHasCompatibleParameterAndReturnSameMethod);

			writeDetailedInformationFromMethod(context, affectedMethod, CNTX_Property.M1_OVERLOADED_METHOD,
					m1methodHasSameName);

			writeDetailedInformationFromMethod(context, affectedMethod,
					CNTX_Property.M2_SIMILAR_METHOD_WITH_SAME_RETURN, m2methodhasMinDist);

			writeDetailedInformationFromMethod(context, affectedMethod,
					CNTX_Property.M3_ANOTHER_METHOD_WITH_PARAMETER_RETURN_COMP,
					m3methodhasCompatibleParameterAndReturnWithOtherMethod);

		} // end invocation
		context.put(CNTX_Property.M1_OVERLOADED_METHOD, m1anyhasSameName);
		context.put(CNTX_Property.M2_SIMILAR_METHOD_WITH_SAME_RETURN, m2anyhasMinDist);
		context.put(CNTX_Property.M3_ANOTHER_METHOD_WITH_PARAMETER_RETURN_COMP,
				m3anyhasCompatibleParameterAndReturnWithOtherMethod);
		context.put(CNTX_Property.M4_PARAMETER_RETURN_COMPABILITY, m4anyhasCompatibleParameterAndReturnSameMethod);

	}

	private boolean checkTypeInParameter(CtMethod anotherMethod, CtExecutable minvokedInAffected) {
		for (Object oparameter : anotherMethod.getParameters()) {
			CtParameter parameter = (CtParameter) oparameter;

			if (compareTypes(minvokedInAffected.getType(), parameter.getType())) {
				return true;
			}
		}
		return false;
	}

	public static List getAllMethodsFromClass(CtClass parentClass) {
		List allMethods = new ArrayList(parentClass.getAllMethods());

		if (parentClass != null && parentClass.getParent() instanceof CtClass) {
			CtClass parentParentClass = (CtClass) parentClass.getParent();
			allMethods.addAll(parentParentClass.getAllMethods());

		}
		return allMethods;
	}

	private void writeDetailedInformationFromMethod(Cntx<Object> context, CtMethod affectedMethod,
			CNTX_Property property, Boolean value) {

		if (ConfigurationProperties.getPropertyBool("write_composed_feature"))
			context.getInformation().put(property.name() + "_" + affectedMethod.getSignature(), value);
		writeGroupedByVar(context, affectedMethod.getSignature(), property, value, "FEATURES_METHODS");

	}

	private void writeDetailedInformationFromVariables(Cntx<Object> context, String key, CNTX_Property property,
			Boolean value) {

		if (ConfigurationProperties.getPropertyBool("write_composed_feature"))
			context.getInformation().put(property.name() + "_" + key, value);
		writeGroupedByVar(context, key, property, value, "FEATURES_VARS");

	}

	private void writeGroupedByVar(Cntx<Object> context, String key, CNTX_Property property, Boolean value,
			String type) {

		Cntx<Object> featuresVar = (Cntx<Object>) context.getInformation().get(type);
		if (featuresVar == null) {
			featuresVar = new Cntx<>();
			context.getInformation().put(type, featuresVar);
		}
		Cntx<Object> particularVar = (Cntx<Object>) featuresVar.getInformation().get(key);
		if (particularVar == null) {
			particularVar = new Cntx<>();
			featuresVar.getInformation().put(key, particularVar);
		}
		particularVar.getInformation().put(property.name(), value);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void analyzeTypesVarsAffected(List<CtVariableAccess> varsAffected, CtElement element,
			Cntx<Object> context) {
		// Vars in scope at the position of element

		int nrPrimitives = 0;
		int nrObjectRef = 0;

		List<CtVariableAccess> objectAccess = new ArrayList<>();

		for (CtVariableAccess ctVariableAccess : varsAffected) {

			CtVariable ctVariable = ctVariableAccess.getVariable().getDeclaration();

			if (ctVariable != null && ctVariable.getReference() != null
					&& ctVariable.getReference().getType() != null) {
				if (ctVariable.getReference().getType().isPrimitive()) {
					nrPrimitives++;
				} else {
					nrObjectRef++;
					objectAccess.add(ctVariableAccess);
				}
			}
		}
		context.put(CNTX_Property.NUMBER_PRIMITIVE_VARS_IN_STMT, nrPrimitives);
		context.put(CNTX_Property.NUMBER_OBJECT_REFERENCE_VARS_IN_STMT, nrObjectRef);
		context.put(CNTX_Property.NUMBER_TOTAL_VARS_IN_STMT, nrPrimitives + nrObjectRef);

	}

	/**
	 * whether the associated method or class for the faulty line throws exception
	 * 
	 * @param element
	 * @param context
	 */
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void analyzeS6_Method_ExceptionAndInformation(CtElement element, Cntx<Object> context) {
		//
		CtMethod parentMethod = element.getParent(CtMethod.class);
		if (parentMethod != null) {
			// Return
			context.put(CNTX_Property.METHOD_RETURN_TYPE,
					(parentMethod.getType() != null) ? parentMethod.getType().getQualifiedName() : null);
			// Param
			List<CtParameter> parameters = parentMethod.getParameters();
			List<String> parametersTypes = new ArrayList<>();
			for (CtParameter ctParameter : parameters) {
				parametersTypes.add(ctParameter.getType().getSimpleName());
			}
			context.put(CNTX_Property.METHOD_PARAMETERS, parametersTypes);

			// Modif
			context.put(CNTX_Property.METHOD_MODIFIERS, parentMethod.getModifiers());

			// Comments
			context.put(CNTX_Property.METHOD_COMMENTS, parentMethod.getComments());

			// Exception
			context.put(CNTX_Property.S6_METHOD_THROWS_EXCEPTION, parentMethod.getThrownTypes().size() > 0);

		}
	}

	private void retrievePath(CtElement element, Cntx<Object> context) {
		try {
			CtPath path = element.getPath();

			context.put(CNTX_Property.SPOON_PATH, path.toString());
			if (path instanceof CtPathImpl) {
				CtPathImpl pi = (CtPathImpl) path;
				List<CtPathElement> elements = pi.getElements();
				List<String> paths = elements.stream().map(e -> e.toString()).collect(Collectors.toList());
				context.put(CNTX_Property.PATH_ELEMENTS, paths);
			}
		} catch (Throwable e) {
		}

	}

	private void analyzeParentTypes(CtElement element, Cntx<Object> context) {
		CtElement parent = element.getParent();
		List<String> parentNames = new ArrayList<>();
		try {
			do {
				parentNames.add(parent.getClass().getSimpleName());
				parent = parent.getParent();
			} while (parent != null);
		} catch (Exception e) {
		}

		context.put(CNTX_Property.PARENTS_TYPE, parentNames);

	}

}
