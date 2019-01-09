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

		//
		context.put(CNTX_Property.CODE, element.toString());

		//
		Cntx<Object> buggyPositionCntx = new Cntx<>();
		retrievePosition(element, buggyPositionCntx);
		context.put(CNTX_Property.POSITION, buggyPositionCntx);

		return context;
	}

	@SuppressWarnings("unused")
	public Cntx<?> retrieveCntx(CtElement element) {
		Cntx<Object> context = new Cntx<>(determineKey(element));
		analyzeVarsInScope(element, context);
		retrieveMethodInformation(element, context);
		retrieveParentTypes(element, context);

		String type = element.getClass().getSimpleName().replaceAll("Ct", "").replaceAll("Impl", "");
		context.put(CNTX_Property.S3_TYPE_OF_FAULTY_STATEMENT, type);
		for (CntxEntity entity : CntxEntity.values()) {
			boolean equals = type.equals(entity.name());
			// writeDetailedInformation(context, entity.name(),
			// CNTX_Property.S3_TYPE_OF_FAULTY_STATEMENT, equals);
		}
		//

		Cntx<Object> binCntx = new Cntx<>();
		context.put(CNTX_Property.BIN_PROPERTIES, binCntx);
		retrieveBinaryInvolved(element, binCntx, context);

		Cntx<Object> unaryCntx = new Cntx<>();
		context.put(CNTX_Property.UNARY_PROPERTIES, unaryCntx);
		retrieveUnaryInvolved(element, unaryCntx, context);

		retrieveUseEnumAndConstants(element, context);

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

	private void retrieveUseEnumAndConstants(CtElement element, Cntx<Object> context) {

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

	private void analyzeBasedOnAffectedVars(CtElement element, Cntx<Object> context, List<CtVariable> varsInScope) {
		List<CtVariableAccess> varsAffected = VariableResolver.collectVariableRead(element);
		analyzeTypesVarsAffected(varsAffected, element, context);
		analyzeAffectedVariablesInTransformation(varsAffected, element, context);
		analyzeAffectedVariablesInMethod(varsAffected, element, context);
		analyzeAffectedDistanceVarName(varsAffected, varsInScope, element, context);
		analyzeAffectedAssigned(varsAffected, element, context);
		analyzeAffectedFielfsF4(varsAffected, element, context);
		analyzeAffectedVariablesUsedS1(varsAffected, element, context);
		analyzeAffectedVariablesUsedLE1(varsAffected, element, context);
		analyzeAffectedWithCompatibleTypes(varsAffected, varsInScope, element, context);
		analyzeVarDirectlyUsed(varsAffected, varsInScope, element, context);
		analyzePrimitiveWithCompatibleNotUsed(varsAffected, varsInScope, element, context);
		analyzeBooleanVarNotUsed(varsAffected, varsInScope, element, context);
		analyzeAffectedHasConstant(varsAffected, varsInScope, element, context);

	}

	private void analyzeBooleanVarNotUsed(List<CtVariableAccess> varsAffectedInStatement, List<CtVariable> varsInScope,
			CtElement element, Cntx<Object> context) {

		boolean hasBooleanVarNotPresent = false;

		for (CtVariable aVarInScope : varsInScope) {

			if (aVarInScope.getType() != null && aVarInScope.getType().unbox().toString().equals("boolean")) {

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

	private void analyzePrimitiveWithCompatibleNotUsed(List<CtVariableAccess> varsAffectedInStatement,
			List<CtVariable> varsInScope, CtElement element, Cntx<Object> context) {

		boolean hasCompatibleVarNoPresent = false;

		for (CtVariableAccess aVarFromAffected : varsAffectedInStatement) {

			if (aVarFromAffected.getType() == null || !aVarFromAffected.getType().isPrimitive()
			// parent is binary operator
					|| aVarFromAffected.getParent(CtBinaryOperator.class) == null)
				continue;

			for (CtVariable aVarFromScope : varsInScope) {
				if (!aVarFromScope.getSimpleName().equals(aVarFromAffected.getVariable().getSimpleName())) {

					try {
						if (compareTypes(aVarFromScope.getType(), aVarFromAffected.getType())) {

							boolean presentInExpression = varsAffectedInStatement.stream()
									.filter(e -> e.getVariable().getSimpleName().equals(aVarFromScope.getSimpleName()))
									.findFirst().isPresent();
							if (!presentInExpression) {
								hasCompatibleVarNoPresent = true;
								context.put(CNTX_Property.LE3_IS_COMPATIBLE_VAR_NOT_INCLUDED,
										hasCompatibleVarNoPresent);
								return;
							}
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}

		}
		context.put(CNTX_Property.LE3_IS_COMPATIBLE_VAR_NOT_INCLUDED, hasCompatibleVarNoPresent);

	}

	private void analyzeVarDirectlyUsed(List<CtVariableAccess> varsAffectedInStatement, List<CtVariable> varsInScope,
			CtElement element, Cntx<Object> context) {

		boolean hasVarDirectlyUsed = false;

		for (CtVariableAccess aVarFromAffected : varsAffectedInStatement) {

			CtElement parent = aVarFromAffected.getParent();
			if (parent instanceof CtExpression)
				if (isLogicalExpression((CtExpression) parent)) {
					hasVarDirectlyUsed = true;
					break;
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

	@SuppressWarnings("rawtypes")
	private void analyzeAffectedVariablesInTransformation(List<CtVariableAccess> varsAffected, CtElement element,
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

		assignmentScanner.scan(methodParent);

		boolean v5_anyhasvar = false;
		// For each variable affected
		for (CtVariableAccess variableAffected : varsAffected) {

			boolean v5_VarHasvar = false;

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
						v5_VarHasvar = true;
						break;
					}
				}
			}
			writeDetailedInformationFromVariables(context, variableAffected.getVariable().getSimpleName(),
					CNTX_Property.V5_HAS_VAR_IN_TRANSFORMATION, (v5_VarHasvar));

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

			return target.getPosition() != null && stst.getParent() != null
					&& target.getPosition().getSourceStart() > stst.getPosition().getSourceStart();
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return false;

	}

	@SuppressWarnings("rawtypes")
	private void analyzeAffectedVariablesUsedLE1(List<CtVariableAccess> varsAffected, CtElement element,
			Cntx<Object> context) {

		CtExecutable methodParent = element.getParent(CtExecutable.class);

		if (methodParent == null)
			return;

		List<CtStatement> statements = methodParent.getElements(new LineFilter());

		int similarUsedBefore = 0;
		// int notSimilarUsedBefore = 0;

		// For each variable affected
		for (CtVariableAccess variableAffected : varsAffected) {

			boolean used = false;
			boolean foundSimilarVarUsedBefore = false;

			boolean isInBinaryExpression = isLogicalExpressionInParent(variableAffected);// .getParent(CtBinaryOperator.class)

			// For any variable involved in a logical expression,
			if (!isInBinaryExpression)
				continue;

			// For each assignment in the methid
			for (CtStatement aStatement : statements) {

				if (!isElementBeforeVariable(variableAffected, aStatement))
					continue;

				// let's collect the var access in the right part
				List<CtVariableAccess> varsInRightPart = VariableResolver.collectVariableRead(aStatement);
				// if the var access in the right is the same that the affected
				for (CtVariableAccess varInStatement : varsInRightPart) {
					if (hasSameName(variableAffected, varInStatement)) {
						used = true;
					} else {
						// Different name, so it's another variable

						try {
							// whether exist other boolean expressions
							boolean hasBooleanExpressionParent = isParentBooleanExpression(varInStatement);
							if (!hasBooleanExpressionParent)
								continue;
							// involve using variable whose type is same with v
							if (compareTypes(variableAffected.getType(), varInStatement.getType())) {
								foundSimilarVarUsedBefore = true;
							}
						} catch (Exception e) {
							System.out.println("Problems with type");
							e.printStackTrace();
						}
					}
				}
				if (used && foundSimilarVarUsedBefore)
					break;
			}

			///
			if (foundSimilarVarUsedBefore)
				similarUsedBefore++;
			// else
			// notSimilarUsedBefore++;
		}

		context.put(CNTX_Property.LE1_EXISTS_RELATED_BOOLEAN_EXPRESSION, (similarUsedBefore) > 0);

		// context.put(CNTX_Property.LE_8_LOGICAL_WITH_USED_LOCAL_VARS,
		// isParentBooleanExpression(varInStatement)&& (notUsedObjectsLocal == 0
		// && notUsedPrimitiveLocal == 0 && (usedPrimitiveLocal > 0 || usedObjectsLocal
		// > 0)));

	}

	@SuppressWarnings("rawtypes")
	private void analyzeAffectedVariablesUsedS1(List<CtVariableAccess> varsAffected, CtElement element,
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

			boolean used = false;
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
						used = true;
					}
				}
				if (used)
					break;
			}

			if (variableAffected.getVariable().getType() != null) {

				if (!variableAffected.getVariable().getType().isPrimitive()) {
					if (used)
						usedObjects++;
					else
						notUsedObjects++;

					if (variableAffected.getVariable().getDeclaration() instanceof CtLocalVariable) {
						if (used)
							usedObjectsLocal++;
						else
							notUsedObjectsLocal++;
					}
				} else {

					if (variableAffected.getVariable().getType().isPrimitive()
							&& (variableAffected.getVariable().getDeclaration() instanceof CtLocalVariable))
						if (used)
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

	@SuppressWarnings("rawtypes")
	private void analyzeAffectedVariablesUsedOLD(List<CtVariableAccess> varsAffected, CtElement element,
			Cntx<Object> context) {

		CtMethod methodParent = element.getParent(CtMethod.class);

		List<CtStatement> statements = methodParent.getElements(new LineFilter());

		int usedObjects = 0;
		int notUsedObjects = 0;

		int usedObjectsLocal = 0;
		int usedPrimitiveLocal = 0;
		int notUsedObjectsLocal = 0;
		int notUsedPrimitiveLocal = 0;

		int similarUsedBefore = 0;
		int notSimilarUsedBefore = 0;

		// For each variable affected
		for (CtVariableAccess variableAffected : varsAffected) {

			// if (variableAffected.getVariable().getType() != null
			// && variableAffected.getVariable().getType().isPrimitive()
			// )
			// continue;

			boolean used = false;
			boolean foundSimilarVarUsedBefore = false;

			boolean isInBinaryExpression = isLogicalExpression(variableAffected.getParent(CtExpression.class));
			// variableAffected.getParent(CtBinaryOperator.class)
			// != null;

			// For each assignment in the methid
			for (CtStatement aStatement : statements) {

				if (!isElementBeforeVariable(variableAffected, aStatement))
					continue;

				// let's collect the var access in the right part
				List<CtVariableAccess> varsInRightPart = VariableResolver.collectVariableRead(aStatement);
				// if the var access in the right is the same that the affected
				for (CtVariableAccess varInStatement : varsInRightPart) {
					if (hasSameName(variableAffected, varInStatement)) {
						used = true;
					} else {
						// Different name, so it's another variable

						// // For any variable involved in a logical expression,
						// if (!isInBinaryExpression)
						// continue;

						try {
							// whether exist other boolean expressions
							boolean hasBooleanExpressionParent = isParentBooleanExpression(varInStatement);
							if (!hasBooleanExpressionParent)
								continue;
							// involve using variable whose type is same with v
							if (compareTypes(variableAffected.getType(), varInStatement.getType())) {
								foundSimilarVarUsedBefore = true;
							}
						} catch (Exception e) {
							System.out.println("Problems with type");
							e.printStackTrace();
						}
					}
				}
				if (used && foundSimilarVarUsedBefore)
					break;
			}
			// Not sure if include this restriction
			if (variableAffected.getVariable().getType() != null
					&& !variableAffected.getVariable().getType().isPrimitive()) {
				if (used)
					usedObjects++;
				else
					notUsedObjects++;

				if (variableAffected.getVariable().getDeclaration() instanceof CtLocalVariable) {
					if (used)
						usedObjectsLocal++;
					else
						notUsedObjectsLocal++;
				}
			} else {

				if (variableAffected.getVariable().getType().isPrimitive()
						&& (variableAffected.getVariable().getDeclaration() instanceof CtLocalVariable))
					if (used)
						usedPrimitiveLocal++;
					else
						notUsedPrimitiveLocal++;
			}

			///
			if (foundSimilarVarUsedBefore)
				similarUsedBefore++;
			else
				notSimilarUsedBefore++;
		}
		context.put(CNTX_Property.NR_OBJECT_USED, usedObjects);
		context.put(CNTX_Property.NR_OBJECT_NOT_USED, notUsedObjects);

		context.put(CNTX_Property.NR_OBJECT_USED_LOCAL_VAR, usedObjectsLocal);
		context.put(CNTX_Property.NR_OBJECT_NOT_USED_LOCAL_VAR, notUsedObjectsLocal);

		// // For any variable involved in a logical expression,
		// if (!isInBinaryExpression)
		// continue;
		context.put(CNTX_Property.S1_LOCAL_VAR_NOT_USED, (notUsedObjectsLocal) > 0);

		context.put(CNTX_Property.LE1_EXISTS_RELATED_BOOLEAN_EXPRESSION, (similarUsedBefore) > 0);

		// context.put(CNTX_Property.LE_8_LOGICAL_WITH_USED_LOCAL_VARS,
		// isParentBooleanExpression(varInStatement)&& (notUsedObjectsLocal == 0
		// && notUsedPrimitiveLocal == 0 && (usedPrimitiveLocal > 0 || usedObjectsLocal
		// > 0)));

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
		if ((currentElement instanceof CtBinaryOperator)) {
			CtBinaryOperator binop = (CtBinaryOperator) currentElement;
			if (binop.getKind().equals(BinaryOperatorKind.AND) || binop.getKind().equals(BinaryOperatorKind.OR))
				return true;

		}
		return isLogicalExpressionInParent(currentElement.getParent(CtBinaryOperator.class));
	}

	public boolean isLogicalExpression(CtExpression currentElement) {
		if (currentElement == null)
			return false;
		if ((currentElement instanceof CtBinaryOperator)) {
			CtBinaryOperator binop = (CtBinaryOperator) currentElement;
			if (binop.getKind().equals(BinaryOperatorKind.AND) || binop.getKind().equals(BinaryOperatorKind.OR))
				return true;

		}
		return false;
	}

	@SuppressWarnings("rawtypes")
	private void analyzeAffectedAssigned(List<CtVariableAccess> varsAffected, CtElement element, Cntx<Object> context) {

		CtMethod methodParent = element.getParent(CtMethod.class);

		List<CtAssignment> assignments = new ArrayList<>();
		List<CtLocalVariable> locals = new ArrayList<>();

		CtScanner assignmentScanner = new CtScanner() {

			@Override
			public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> assignement) {

				assignments.add(assignement);
			}

			@Override
			public <T> void visitCtLocalVariable(CtLocalVariable<T> localVariable) {

				locals.add(localVariable);
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

			boolean hasassig = false;
			// For each assignment in the meth0d
			for (CtAssignment assignment : assignments) {

				if (!isElementBeforeVariable(variableAffected, assignment))
					continue;

				if (assignment.getAssigned().toString().equals(variableAffected.getVariable().getSimpleName())) {
					hasassig = true;
				}
				boolean incomplete = retrieveNotAllInitialized(variableAffected, assignment, assignments);
				if (incomplete) {
					hasIncomplete = true;
				}
			}
			// Let's find in local declaration
			if (!hasassig) {

				for (CtLocalVariable ctLocalVariable : locals) {

					if (!isElementBeforeVariable(variableAffected, ctLocalVariable))
						continue;

					if (ctLocalVariable.getReference().getSimpleName()
							.equals(variableAffected.getVariable().getSimpleName())
							&& ctLocalVariable.getDefaultExpression() != null
							&& !"null".equals(ctLocalVariable.getDefaultExpression().toString()))
						hasassig = true;
				}

			}

			if (hasassig)
				nrOfVarWithAssignment++;
			else
				nrOfVarWithoutAssignment++;

			if (variableAffected.getVariable().getDeclaration() instanceof CtLocalVariable) {
				if (hasassig)
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

	@SuppressWarnings("rawtypes")
	private void analyzeAffectedFielfsF4(List<CtVariableAccess> varsAffected, CtElement element, Cntx<Object> context) {

		CtMethod methodParent = element.getParent(CtMethod.class);
		CtClass xclass = element.getParent(CtClass.class);

		boolean isMethodUsingField = false;
		// For each variable affected
		for (CtVariableAccess variableAffected : varsAffected) {

			if (variableAffected instanceof CtFieldAccess) {

				for (Object amethod : xclass.getAllMethods()) {

					CtMethod anotherMethod = (CtMethod) amethod;

					if (amethod.equals(methodParent))
						continue;

					List<CtElement> fieldsaccsess = anotherMethod.getElements(e -> e instanceof CtFieldAccess);
					for (CtElement ef : fieldsaccsess) {

						CtFieldAccess faccess = (CtFieldAccess) ef;
						if (faccess.getVariable().getSimpleName()
								.equals(variableAffected.getVariable().getSimpleName())) {
							isMethodUsingField = true;
						}

					}

				}

			}
		}
		context.put(CNTX_Property.S4_USED_FIELD, isMethodUsingField);
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

	private void analyzeAffectedDistanceVarName(List<CtVariableAccess> varsAffected, List<CtVariable> varsInScope,
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

	private void analyzeAffectedHasConstant(List<CtVariableAccess> varsAffected, List<CtVariable> varsInScope,
			CtElement element, Cntx<Object> context) {

		boolean hasConstant = false;
		for (CtVariableAccess aVarAffected : varsAffected) {
			if (aVarAffected.getVariable() instanceof CtFieldReference && aVarAffected.getVariable().getSimpleName()
					.toUpperCase().equals(aVarAffected.getVariable().getSimpleName())) {
				hasConstant = true;
				break;
			}

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
	private void analyzeAffectedVariablesInMethod(List<CtVariableAccess> varsAffected, CtElement element,
			Cntx<Object> context) {
		try {
			boolean v6returnCompatible = false;
			boolean v4paramCompatible = false;
			boolean les2paramCompatibleWithBooleanReturn = false;
			boolean v1compatibleReturnAndParameterTypes = false;
			CtClass parentClass = element.getParent(CtClass.class);
			for (CtVariableAccess var : varsAffected) {

				boolean v6InvReturnCompatible = false;
				boolean v4InvparamCompatible = false;
				boolean les2InvparamCompatibleWithBooleanReturn = false;
				boolean v1InvcompatibleReturnAndParameterTypes = false;

				List allMethods = getAllMethodsFromClass(parentClass);

				for (Object omethod : allMethods) {
					boolean matchInmethodType = false;
					boolean matchInmethodReturn = false;

					if (!(omethod instanceof CtMethod))
						continue;

					CtMethod method = (CtMethod) omethod;

					if (method.getType() != null) {

						if (isSubtype(var, method)) {
							v6returnCompatible = true;
							matchInmethodReturn = true;
							v6InvReturnCompatible = true;

						}

					}
					for (Object oparameter : method.getParameters()) {
						CtParameter parameter = (CtParameter) oparameter;

						if (compareTypes(var.getType(), parameter.getType())) {
							v4paramCompatible = true;
							v4InvparamCompatible = true;
							matchInmethodType = true;
							if (method.getType().unbox().toString().equals("boolean")) {
								les2paramCompatibleWithBooleanReturn = true;
								les2InvparamCompatibleWithBooleanReturn = true;
							}

						}
					}

					if (matchInmethodType && matchInmethodReturn) {
						v1compatibleReturnAndParameterTypes = true;
						v1InvcompatibleReturnAndParameterTypes = true;
					}

					if (v4paramCompatible && v6returnCompatible && les2paramCompatibleWithBooleanReturn
							&& v1compatibleReturnAndParameterTypes) {
						break;
					}

				}

				writeDetailedInformationFromVariables(context, var.getVariable().getSimpleName(),
						CNTX_Property.V1_IS_TYPE_COMPATIBLE_METHOD_CALL_PARAM_RETURN,
						(v1InvcompatibleReturnAndParameterTypes));

				writeDetailedInformationFromVariables(context, var.getVariable().getSimpleName(),
						CNTX_Property.V6_IS_METHOD_RETURN_TYPE_VAR, v6InvReturnCompatible);

				writeDetailedInformationFromVariables(context, var.getVariable().getSimpleName(),
						CNTX_Property.V4_BIS_IS_METHOD_PARAM_TYPE_VAR, v4InvparamCompatible);

				writeDetailedInformationFromVariables(context, var.getVariable().getSimpleName(),
						CNTX_Property.LE2_IS_BOOLEAN_METHOD_PARAM_TYPE_VAR, (les2InvparamCompatibleWithBooleanReturn));

			}

			context.put(CNTX_Property.V1_IS_TYPE_COMPATIBLE_METHOD_CALL_PARAM_RETURN,
					(v1compatibleReturnAndParameterTypes));

			context.put(CNTX_Property.V4_BIS_IS_METHOD_PARAM_TYPE_VAR, v4paramCompatible);
			context.put(CNTX_Property.V6_IS_METHOD_RETURN_TYPE_VAR, v6returnCompatible);

			context.put(CNTX_Property.LE2_IS_BOOLEAN_METHOD_PARAM_TYPE_VAR, (les2paramCompatibleWithBooleanReturn));

		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private boolean isSubtype(CtVariableAccess var, CtMethod method) {
		try {
			return method.getType().isSubtypeOf(var.getType());
		} catch (Exception e) {
			return false;
		}
	}

	public boolean compareTypes(CtTypeReference t1, CtTypeReference t2) {
		try {
			return t1 != null && t2 != null && (t1.toString().equals(t2.toString()) || t1.equals(t2)
					|| t1.isSubtypeOf(t2) || t2.isSubtypeOf(t1));
		} catch (Exception e) {
			System.out.println("Error comparing types");
			e.printStackTrace();
			return false;
		}
	}

	private void retrieveUnaryInvolved(CtElement element, Cntx<Object> context, Cntx<Object> parentContext) {
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

	private void retrieveBinaryInvolved(CtElement element, Cntx<Object> context, Cntx<Object> parentContext) {

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
	private void analyzeVarsInScope(CtElement element, Cntx<Object> context) {
		// Vars in scope at the position of element

		List<CtVariable> varsInScope = VariableResolver.searchVariablesInScope(element);
		context.put(CNTX_Property.VARS_IN_SCOPE, varsInScope);
		List<Cntx> children = new ArrayList();
		int nrPrimitives = 0;
		int nrObjectRef = 0;
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

		analyzeBasedOnAffectedVars(element, context, varsInScope);

		analyzeSimilarMethod(element, context);

	}

	private void analyzeSimilarMethod(CtElement element, Cntx<Object> context) {

		CtClass parentClass = element.getParent(CtClass.class);
		boolean m1anyhasSameName = false;
		boolean m2anyhasMinDist = false;
		boolean m3anyhasCompatibleParameterAndReturnWithOtherMethod = false;
		boolean m4anyhasCompatibleParameterAndReturnSameMethod = false;

		List invocations = element.getElements(e -> (e instanceof CtInvocation));
		for (Object object : invocations) {
			CtInvocation invocation = (CtInvocation) object;
			CtExecutable minvokedInAffected = invocation.getExecutable().getDeclaration();

			if (minvokedInAffected == null || !(minvokedInAffected instanceof CtMethod))
				continue;

			boolean m1methodHasSameName = false;
			boolean m2methodhasMinDist = false;
			boolean m3methodhasCompatibleParameterAndReturnWithOtherMethod = false;
			boolean m4methodHasCompatibleParameterAndReturnSameMethod = false;

			CtMethod affectedMethod = (CtMethod) minvokedInAffected;

			for (Object oparameter : affectedMethod.getParameters()) {
				CtParameter parameter = (CtParameter) oparameter;

				if (affectedMethod != null && compareTypes(affectedMethod.getType(), parameter.getType())) {
					m4anyhasCompatibleParameterAndReturnSameMethod = true;
					m4methodHasCompatibleParameterAndReturnSameMethod = true;
				}
			}

			List allMethods = getAllMethodsFromClass(parentClass);

			for (Object omethod : allMethods) {

				if (!(omethod instanceof CtMethod))
					continue;

				CtMethod anotherMethod = (CtMethod) omethod;

				if (anotherMethod.getSignature().equals(affectedMethod.getSignature()))
					continue;

				// System.out.println("--" + anotherMethod.getSimpleName());

				if (anotherMethod.getSimpleName().equals(affectedMethod.getSimpleName())) {
					// It's overide
					m1methodHasSameName = true;
					m1anyhasSameName = true;
				}

				if (anotherMethod.getType() != null && minvokedInAffected.getType() != null) {

					boolean compatibleReturnTypes = compareTypes(anotherMethod.getType(), minvokedInAffected.getType());
					if (compatibleReturnTypes) {
						int dist = StringDistance.calculate(anotherMethod.getSimpleName(),
								minvokedInAffected.getSimpleName());
						if (dist > 0 && dist < 3) {
							m2anyhasMinDist = true;
							context.put(CNTX_Property.M2_SIMILAR_METHOD_WITH_SAME_RETURN, m2anyhasMinDist);
							m2methodhasMinDist = true;
						}

					}

					for (Object oparameter : anotherMethod.getParameters()) {
						CtParameter parameter = (CtParameter) oparameter;

						if (compareTypes(minvokedInAffected.getType(), parameter.getType())) {
							m3anyhasCompatibleParameterAndReturnWithOtherMethod = true;
							m3methodhasCompatibleParameterAndReturnWithOtherMethod = true;
						}
					}

				}

				writeDetailedInformationFromMethod(context, affectedMethod,
						CNTX_Property.M4_PARAMETER_RETURN_COMPABILITY,
						m4methodHasCompatibleParameterAndReturnSameMethod);

				writeDetailedInformationFromMethod(context, affectedMethod, CNTX_Property.M1_OVERLOADED_METHOD,
						m1methodHasSameName);

				writeDetailedInformationFromMethod(context, affectedMethod,
						CNTX_Property.M2_SIMILAR_METHOD_WITH_SAME_RETURN, m2methodhasMinDist);

				writeDetailedInformationFromMethod(context, affectedMethod,
						CNTX_Property.M3_SIMILAR_METHOD_WITH_PARAMETER_COMP,
						m3methodhasCompatibleParameterAndReturnWithOtherMethod);

			}

		}
		context.put(CNTX_Property.M1_OVERLOADED_METHOD, m1anyhasSameName);
		context.put(CNTX_Property.M2_SIMILAR_METHOD_WITH_SAME_RETURN, m2anyhasMinDist);
		context.put(CNTX_Property.M3_SIMILAR_METHOD_WITH_PARAMETER_COMP,
				m3anyhasCompatibleParameterAndReturnWithOtherMethod);
		context.put(CNTX_Property.M4_PARAMETER_RETURN_COMPABILITY, m4anyhasCompatibleParameterAndReturnSameMethod);

	}

	public List getAllMethodsFromClass(CtClass parentClass) {
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void retrieveMethodInformation(CtElement element, Cntx<Object> context) {
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

	private void retrieveParentTypes(CtElement element, Cntx<Object> context) {
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
