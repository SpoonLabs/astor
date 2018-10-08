package fr.inria.astor.core.entities;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;

import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.util.StringDistance;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtFieldRead;
import spoon.reflect.code.CtFieldWrite;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtIf;
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
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import spoon.reflect.declaration.ModifierKind;
import spoon.reflect.path.CtPath;
import spoon.reflect.path.impl.CtPathElement;
import spoon.reflect.path.impl.CtPathImpl;
import spoon.reflect.reference.CtFieldReference;
import spoon.reflect.visitor.CtScanner;

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

	@SuppressWarnings("unused")
	public Cntx<?> retrieveCntx(CtElement element) {
		Cntx<Object> context = new Cntx<>(determineKey(element));
		retrievePosition(element, context);
		retrieveVarsInScope(element, context);
		retrieveMethodInformation(element, context);
		retrieveParentTypes(element, context);
		retrievePath(element, context);
		//
		context.getInformation().put(CNTX_Property.CODE_ELEMENT, element.toString());
		//
		retrieveType(element, context);
		retrieveBinaryInvolved(element, context);
		retrieveUnaryInvolved(element, context);
		retrieveUseEnumAndConstants(element, context);

		return context;
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
		context.getInformation().put(CNTX_Property.USES_ENUM, enumsValues.size() > 0);
		context.getInformation().put(CNTX_Property.USES_CONSTANT, literalsValues.size() > 0);
	}

	private void retrieveAffectedVars(CtElement element, Cntx<Object> context, List<CtVariable> varsInScope) {
		List<CtVariableAccess> varsAffected = VariableResolver.collectVariableRead(element);
		retrieveTypesVarsAffected(varsAffected, element, context);
		retrieveAffectedVariablesInTransformation(varsAffected, element, context);
		retrieveAffectedVariablesInMethod(varsAffected, element, context);
		retrieveAffectedDistance(varsAffected, varsInScope, element, context);
		retrieveAffectedAssigned(varsAffected, element, context);
		retrieveAffectedVariablesUsed(varsAffected, element, context);
		retrieveAffectedWithCompatibleTypes(varsAffected, varsInScope, element, context);

	}

	private void retrieveAffectedWithCompatibleTypes(List<CtVariableAccess> varsAffected, List<CtVariable> varsInScope,
			CtElement element, Cntx<Object> context) {

		boolean hasSimType = false;
		for (CtVariableAccess ctVariableAccess : varsAffected) {
			for (CtVariable ctVariable : varsInScope) {
				if (!ctVariable.getSimpleName().equals(ctVariableAccess.getVariable().getSimpleName())) {

					if (ctVariable.getType().equals(ctVariableAccess.getType())) {
						hasSimType = true;
						context.getInformation().put(CNTX_Property.HAS_VAR_SIM_TYPE, hasSimType);
						return;
					}
				}
			}
		}
		context.getInformation().put(CNTX_Property.HAS_VAR_SIM_TYPE, hasSimType);

	}

	@SuppressWarnings("rawtypes")
	private void retrieveAffectedVariablesInTransformation(List<CtVariableAccess> varsAffected, CtElement element,
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

		// For each variable affected
		for (CtVariableAccess variableAffected : varsAffected) {

			// For each assignment in the methid
			for (CtExpression assignment : assignments) {

				if (!isElementBeforeVariable(variableAffected, assignment))
					continue;

				// let's collect the var access in the right part
				List<CtVariableAccess> varsInRightPart = VariableResolver.collectVariableRead(assignment); // VariableResolver.collectVariableAccess(assignment);

				// if the var access in the right is the same that the affected
				for (CtVariableAccess varInAssign : varsInRightPart) {
					if (varInAssign.getVariable().getSimpleName().equals(variableAffected.getVariable().getSimpleName())
							|| varInAssign.equals(variableAffected)) {

						context.getInformation().put(CNTX_Property.HAS_VAR_IN_TRANSFORMATION, true);
						return;
					}
				}
			}

		}
		context.getInformation().put(CNTX_Property.HAS_VAR_IN_TRANSFORMATION, false);

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
			e.printStackTrace();
		}
		return false;

	}

	@SuppressWarnings("rawtypes")
	private void retrieveAffectedVariablesUsed(List<CtVariableAccess> varsAffected, CtElement element,
			Cntx<Object> context) {

		CtMethod methodParent = element.getParent(CtMethod.class);

		List<CtStatement> statements = new ArrayList<>();

		CtScanner assignmentScanner = new CtScanner() {

			@Override
			public void scan(CtElement element) {
				super.scan(element);
				if (element instanceof CtStatement && !(element instanceof CtBlock)) {
					statements.add((CtStatement) element);
				}
			}
		};

		assignmentScanner.scan(methodParent);
		int usedObjects = 0;
		int notUsedObjects = 0;
		// For each variable affected
		for (CtVariableAccess variableAffected : varsAffected) {

			if (variableAffected.getVariable().getType() != null
					&& variableAffected.getVariable().getType().isPrimitive())
				continue;

			boolean used = false;
			// For each assignment in the methid
			for (CtStatement assignment : statements) {

				if (!isElementBeforeVariable(variableAffected, assignment))
					continue;

				// let's collect the var access in the right part
				List<CtVariableAccess> varsInRightPart = VariableResolver.collectVariableRead(assignment);
				// if the var access in the right is the same that the affected
				for (CtVariableAccess varInAssign : varsInRightPart) {
					if (varInAssign.getVariable().getSimpleName().equals(variableAffected.getVariable().getSimpleName())
							|| varInAssign.equals(variableAffected)) {

						used = true;
						break;
					}

				}
				if (used)
					break;
			}
			if (used)
				usedObjects++;
			else
				notUsedObjects++;

		}
		context.getInformation().put(CNTX_Property.NR_OBJECT_USED, usedObjects);
		context.getInformation().put(CNTX_Property.NR_OBJECT_NOT_USED, notUsedObjects);

	}

	@SuppressWarnings("rawtypes")
	private void retrieveAffectedAssigned(List<CtVariableAccess> varsAffected, CtElement element,
			Cntx<Object> context) {

		CtMethod methodParent = element.getParent(CtMethod.class);

		List<CtAssignment> assignments = new ArrayList<>();

		CtScanner assignmentScanner = new CtScanner() {

			@Override
			public <T, A extends T> void visitCtAssignment(CtAssignment<T, A> assignement) {

				assignments.add(assignement);
			}

		};

		assignmentScanner.scan(methodParent);
		boolean hasIncomplete = false;
		int nrFieldsWithIncompleteInit = 0;
		int nrOfVarWithAssignment = 0;
		int nrOfVarWithoutAssignment = 0;
		// For each variable affected
		for (CtVariableAccess variableAffected : varsAffected) {

			boolean hasassig = false;
			// For each assignment in the methid
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
			if (hasassig)
				nrOfVarWithAssignment++;
			else
				nrOfVarWithoutAssignment++;

		}
		context.getInformation().put(CNTX_Property.NR_OBJECT_ASSIGNED, nrOfVarWithAssignment);
		context.getInformation().put(CNTX_Property.NR_OBJECT_NOT_ASSIGNED, nrOfVarWithoutAssignment);
		context.getInformation().put(CNTX_Property.NR_FIELD_INCOMPLETE_INIT, hasIncomplete);
	}

	@SuppressWarnings("rawtypes")
	private boolean retrieveNotAllInitialized(CtVariableAccess variableAffected, CtAssignment assignment,
			List<CtAssignment> assignments) {

		// Let's check if the var is a field reader to an Object
		String varAffected = null;
		if (variableAffected instanceof CtFieldRead) {
			CtFieldRead fr = (CtFieldRead) ((CtFieldRead) variableAffected);
			varAffected = fr.getVariable().getDeclaration().getSimpleName();
			if (fr.getVariable().getType().isPrimitive()) {

				return false;
			}

		} else {
			return false;
		}

		System.out.println("\n----******");
		System.out.println("var access " + variableAffected);
		// let's part of the assignment that we want to check if it's a field assignment
		CtExpression leftPAssignment = assignment.getAssigned();

		if (leftPAssignment instanceof CtFieldWrite) {
			// Field assignment
			CtFieldWrite fieldW = (CtFieldWrite) leftPAssignment;

			// here lets take fX
			if (fieldW.getTarget() instanceof CtFieldRead) {

				System.out.println("Assignment fields " + assignment);

				// The object where the assignment is done.
				CtVariableRead targetField = (CtVariableRead) fieldW.getTarget();
				// check if the variable is the same than the affected
				if (targetField.getVariable().getSimpleName().toString().equals(varAffected)) {

					System.out.println("Same var affected " + assignment);

					Collection<CtFieldReference<?>> fields = targetField.getVariable().getType().getAllFields();
					String initialVar = targetField.getVariable().getSimpleName();
					for (CtFieldReference<?> otherFieldsFromVar : fields) {

						System.out.println("Field: " + otherFieldsFromVar.getSimpleName());

						if (!otherFieldsFromVar.getFieldDeclaration().getVisibility().equals(ModifierKind.PRIVATE)
								&& otherFieldsFromVar.getSimpleName() != initialVar) {
							System.out.println("--Other field " + otherFieldsFromVar.getSimpleName());
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
							System.out.println("field assigned? " + otherFieldsFromVar + " " + fieldAssigned);
							if (!fieldAssigned) {
								System.out.println("field not assigned " + otherFieldsFromVar);
								return true;
							}
						}
					}

				}
			}
		}
		return false;
	}

	private void retrieveAffectedDistance(List<CtVariableAccess> varsAffected, List<CtVariable> varsInScope,
			CtElement element, Cntx<Object> context) {

		boolean hasMinDist = false;
		for (CtVariableAccess ctVariableAccess : varsAffected) {
			for (CtVariable ctVariable : varsInScope) {
				if (!ctVariable.getSimpleName().equals(ctVariableAccess.getVariable().getSimpleName())) {
					int dist = StringDistance.calculate(ctVariable.getSimpleName(),
							ctVariableAccess.getVariable().getSimpleName());
					if (dist > 0 && dist < 3) {
						hasMinDist = true;
						context.getInformation().put(CNTX_Property.HAS_VAR_SIM_NAME, hasMinDist);
						return;
					}
				}
			}
		}
		context.getInformation().put(CNTX_Property.HAS_VAR_SIM_NAME, hasMinDist);

	}

	/**
	 * Check is a variable affected is compatible with a method type or parameter
	 * 
	 * @param varsAffected
	 * @param element
	 * @param context
	 */
	private void retrieveAffectedVariablesInMethod(List<CtVariableAccess> varsAffected, CtElement element,
			Cntx<Object> context) {

		boolean returnCompatible = false;
		boolean paramCompatible = false;
		CtClass parentMethod = element.getParent(CtClass.class);
		for (CtVariableAccess var : varsAffected) {

			for (Object omethod : parentMethod.getAllMethods()) {
				CtMethod method = (CtMethod) omethod;
				if (!returnCompatible && method.getType() != null) {
					if (isSubtype(var, method)) {
						returnCompatible = true;
						break;
					}

				}
				for (Object oparameter : method.getParameters()) {
					CtParameter parameter = (CtParameter) oparameter;

					if (var.getType() != null && parameter.getType() != null && !paramCompatible
							&& parameter.getType().isSubtypeOf(var.getType())) {
						paramCompatible = true;
						break;
					}
				}
				if (paramCompatible && returnCompatible)
					break;
			}

		}
		context.getInformation().put(CNTX_Property.IS_METHOD_RETURN_TYPE_VAR, returnCompatible);
		context.getInformation().put(CNTX_Property.IS_METHOD_PARAM_TYPE_VAR, paramCompatible);

	}

	private boolean isSubtype(CtVariableAccess var, CtMethod method) {
		try {
			return method.getType().isSubtypeOf(var.getType());
		} catch (Exception e) {
			return false;
		}
	}

	private void retrieveUnaryInvolved(CtElement element, Cntx<Object> context) {
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
		context.getInformation().put(CNTX_Property.involved_relation_unary_operators, binOps);

		context.getInformation().put(CNTX_Property.involve_POS_relation_operators,
				binOps.contains(UnaryOperatorKind.POS.toString()));
		context.getInformation().put(CNTX_Property.involve_NEG_relation_operators,
				binOps.contains(UnaryOperatorKind.NEG.toString()));
		context.getInformation().put(CNTX_Property.involve_NOT_relation_operators,
				binOps.contains(UnaryOperatorKind.NOT.toString()));
		context.getInformation().put(CNTX_Property.involve_COMPL_relation_operators,
				binOps.contains(UnaryOperatorKind.COMPL.toString()));
		context.getInformation().put(CNTX_Property.involve_PREINC_relation_operators,
				binOps.contains(UnaryOperatorKind.PREINC.toString()));
		context.getInformation().put(CNTX_Property.involve_PREDEC_relation_operators,
				binOps.contains(UnaryOperatorKind.PREDEC.toString()));
		context.getInformation().put(CNTX_Property.involve_POSTINC_relation_operators,
				binOps.contains(UnaryOperatorKind.POSTINC.toString()));
		context.getInformation().put(CNTX_Property.involve_POSTDEC_relation_operators,
				binOps.contains(UnaryOperatorKind.POSTDEC.toString()));
	}

	private void retrieveBinaryInvolved(CtElement element, Cntx<Object> context) {

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
		context.getInformation().put(CNTX_Property.involved_relation_bin_operators, binOps);

		context.getInformation().put(CNTX_Property.involve_GE_relation_operators,
				binOps.contains(BinaryOperatorKind.GE.toString()));
		context.getInformation().put(CNTX_Property.involve_AND_relation_operators,
				binOps.contains(BinaryOperatorKind.AND.toString()));
		context.getInformation().put(CNTX_Property.involve_OR_relation_operators,
				binOps.contains(BinaryOperatorKind.OR.toString()));
		context.getInformation().put(CNTX_Property.involve_BITOR_relation_operators,
				binOps.contains(BinaryOperatorKind.BITOR.toString()));
		context.getInformation().put(CNTX_Property.involve_BITXOR_relation_operators,
				binOps.contains(BinaryOperatorKind.BITXOR.toString()));
		context.getInformation().put(CNTX_Property.involve_BITAND_relation_operators,
				binOps.contains(BinaryOperatorKind.BITAND.toString()));
		context.getInformation().put(CNTX_Property.involve_EQ_relation_operators,
				binOps.contains(BinaryOperatorKind.EQ.toString()));
		context.getInformation().put(CNTX_Property.involve_NE_relation_operators,
				binOps.contains(BinaryOperatorKind.NE.toString()));
		context.getInformation().put(CNTX_Property.involve_LT_relation_operators,
				binOps.contains(BinaryOperatorKind.LT.toString()));
		context.getInformation().put(CNTX_Property.involve_GT_relation_operators,
				binOps.contains(BinaryOperatorKind.GT.toString()));
		context.getInformation().put(CNTX_Property.involve_LE_relation_operators,
				binOps.contains(BinaryOperatorKind.LE.toString()));
		context.getInformation().put(CNTX_Property.involve_SL_relation_operators,
				binOps.contains(BinaryOperatorKind.SL.toString()));
		context.getInformation().put(CNTX_Property.involve_SR_relation_operators,
				binOps.contains(BinaryOperatorKind.SR.toString()));
		context.getInformation().put(CNTX_Property.involve_USR_relation_operators,
				binOps.contains(BinaryOperatorKind.USR.toString()));
		context.getInformation().put(CNTX_Property.involve_PLUS_relation_operators,
				binOps.contains(BinaryOperatorKind.PLUS.toString()));
		context.getInformation().put(CNTX_Property.involve_MINUS_relation_operators,
				binOps.contains(BinaryOperatorKind.MINUS.toString()));
		context.getInformation().put(CNTX_Property.involve_MUL_relation_operators,
				binOps.contains(BinaryOperatorKind.MUL.toString()));
		context.getInformation().put(CNTX_Property.involve_DIV_relation_operators,
				binOps.contains(BinaryOperatorKind.DIV.toString()));
		context.getInformation().put(CNTX_Property.involve_MOD_relation_operators,
				binOps.contains(BinaryOperatorKind.MOD.toString()));

		context.getInformation().put(CNTX_Property.involve_INSTANCEOF_relation_operators,
				binOps.contains(BinaryOperatorKind.INSTANCEOF.toString()));

	}

	private void retrieveType(CtElement element, Cntx<Object> context) {
		context.getInformation().put(CNTX_Property.TYPE, element.getClass().getSimpleName());

	}

	private void retrievePosition(CtElement element, Cntx<Object> context) {
		if (element.getPosition() != null && element.getPosition().getFile() != null) {
			context.getInformation().put(CNTX_Property.FILE_LOCATION,
					element.getPosition().getFile().getAbsolutePath());

			context.getInformation().put(CNTX_Property.LINE_LOCATION, element.getPosition().getLine());
		} else {
			context.getInformation().put(CNTX_Property.FILE_LOCATION, "");
			context.getInformation().put(CNTX_Property.LINE_LOCATION, "");

		}
		CtType parentClass = element.getParent(spoon.reflect.declaration.CtType.class);

		context.getInformation().put(CNTX_Property.PARENT_CLASS,
				(parentClass != null) ? parentClass.getQualifiedName() : "");

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
	private void retrieveVarsInScope(CtElement element, Cntx<Object> context) {
		// Vars in scope at the position of element

		List<CtVariable> varsInScope = VariableResolver.searchVariablesInScope(element);
		context.getInformation().put(CNTX_Property.VARS_IN_SCOPE, varsInScope);
		List<Cntx> children = new ArrayList();
		int nrPrimitives = 0;
		int nrObjectRef = 0;
		for (CtVariable ctVariable : varsInScope) {
			Cntx c = new Cntx<>();
			c.getInformation().put(CNTX_Property.VAR_VISIB,
					(ctVariable.getVisibility() == null) ? "" : (ctVariable.getVisibility()).toString());
			c.getInformation().put(CNTX_Property.VAR_TYPE, ctVariable.getType().getQualifiedName());
			c.getInformation().put(CNTX_Property.VAR_MODIF, ctVariable.getModifiers());
			c.getInformation().put(CNTX_Property.VAR_NAME, ctVariable.getSimpleName());
			children.add(c);

		}
		context.getInformation().put(CNTX_Property.VARS, children);

		retrieveAffectedVars(element, context, varsInScope);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void retrieveTypesVarsAffected(List<CtVariableAccess> varsAffected, CtElement element,
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
		context.getInformation().put(CNTX_Property.NUMBER_PRIMITIVE_VARS_IN_STMT, nrPrimitives);
		context.getInformation().put(CNTX_Property.NUMBER_OBJECT_REFERENCE_VARS_IN_STMT, nrObjectRef);
		context.getInformation().put(CNTX_Property.NUMBER_TOTAL_VARS_IN_STMT, nrPrimitives + nrObjectRef);

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void retrieveMethodInformation(CtElement element, Cntx<Object> context) {
		//
		CtMethod parentMethod = element.getParent(CtMethod.class);
		if (parentMethod != null) {
			// Return
			context.getInformation().put(CNTX_Property.METHOD_RETURN_TYPE,
					(parentMethod.getType() != null) ? parentMethod.getType().getQualifiedName() : null);
			// Param
			List<CtParameter> parameters = parentMethod.getParameters();
			List<String> parametersTypes = new ArrayList<>();
			for (CtParameter ctParameter : parameters) {
				parametersTypes.add(ctParameter.getType().getSimpleName());
			}
			context.getInformation().put(CNTX_Property.METHOD_PARAMETERS, parametersTypes);

			// Modif
			context.getInformation().put(CNTX_Property.METHOD_MODIFIERS, parentMethod.getModifiers());

			// Comments
			context.getInformation().put(CNTX_Property.METHOD_COMMENTS, parentMethod.getComments());

		}
	}

	private void retrievePath(CtElement element, Cntx<Object> context) {
		CtPath path = element.getPath();

		context.getInformation().put(CNTX_Property.SPOON_PATH, path.toString());
		if (path instanceof CtPathImpl) {
			CtPathImpl pi = (CtPathImpl) path;
			List<CtPathElement> elements = pi.getElements();
			List<String> paths = elements.stream().map(e -> e.toString()).collect(Collectors.toList());
			context.getInformation().put(CNTX_Property.PATH_ELEMENTS, paths);
		}

	}

	private void retrieveParentTypes(CtElement element, Cntx<Object> context) {
		CtElement parent = element.getParent();
		List<String> parentNames = new ArrayList<>();
		do {
			parentNames.add(parent.getClass().getSimpleName());
			parent = parent.getParent();
		} while (parent != null);

		if (!parentNames.isEmpty()) {
			context.getInformation().put(CNTX_Property.PARENTS_TYPE, parentNames);
		}
	}

}
