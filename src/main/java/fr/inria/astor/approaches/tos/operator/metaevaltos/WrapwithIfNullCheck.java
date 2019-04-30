package fr.inria.astor.approaches.tos.operator.metaevaltos;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.approaches.tos.operator.metaevaltos.simple.SingleWrapIfOperator;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.meta.MetaOperator;
import fr.inria.astor.core.entities.meta.MetaOperatorInstance;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.cu.position.NoSourcePosition;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtParameter;
import spoon.reflect.reference.CtTypeReference;
import spoon.support.reflect.code.CtBinaryOperatorImpl;

/**
 * 
 * @author Matias Martinez
 *
 */
public class WrapwithIfNullCheck extends ReplaceOp implements MetaOperator, IOperatorWithTargetElement {

	private CtElement targetElement = null;

	@Override
	public List<MetaOperatorInstance> createMetaOperatorInstances(ModificationPoint modificationPoint) {

		//
		CtElement codeElement = (targetElement == null) ? modificationPoint.getCodeElement() : targetElement;

		List<CtVariableAccess> varAccessInModificationPoints = VariableResolver.collectVariableAccess(codeElement);

		List<Ingredient> ingredients = this.computeIngredientsNullCheck(modificationPoint,
				varAccessInModificationPoints);

		// The parameters to be included in the new method
		List<CtVariableAccess> varsToBeParameters = varAccessInModificationPoints;

		// List of parameters
		List<CtParameter<?>> parameters = new ArrayList<>();
		List<CtExpression<?>> realParameters = new ArrayList<>();
		for (CtVariableAccess ctVariableAccess : varsToBeParameters) {
			// the parent is null, it is setter latter
			CtParameter pari = MutationSupporter.getFactory().createParameter(null, ctVariableAccess.getType(),
					ctVariableAccess.getVariable().getSimpleName());
			parameters.add(pari);
			realParameters.add(ctVariableAccess.clone().setPositions(new NoSourcePosition()));
		}
///
		// let's start with one, and let's keep the Zero for the default (all ifs are
		// false)
		int candidateNumber = 0;
		CtTypeReference returnType = MutationSupporter.getFactory().createCtTypeReference(Boolean.class);

		MetaOperatorInstance megaOp = MetaGenerator.createMetaStatementReplacement(modificationPoint, codeElement,
				MutationSupporter.getFactory().createCodeSnippetExpression("true"), candidateNumber, ingredients,
				parameters, realParameters, this, returnType);
		List<MetaOperatorInstance> opsMega = new ArrayList();
		opsMega.add(megaOp);

		return opsMega;
	}

	private List<Ingredient> computeIngredientsNullCheck(ModificationPoint modificationPoint,
			List<CtVariableAccess> varAccessInModificationPoints) {

		List<Ingredient> ingredients = new ArrayList();

		for (CtVariableAccess iVariableAccess : varAccessInModificationPoints) {

			// Let's check the type, if primitive discard it
			if (iVariableAccess.getVariable() != null && iVariableAccess.getVariable().getType() != null
					&& iVariableAccess.getVariable().getType().isPrimitive())
				continue;

			CtVariableAccess iVariableAccessC = iVariableAccess.clone();
			MutationSupporter.clearPosition(iVariableAccessC);

			CtBinaryOperator<Boolean> binaryOp = new CtBinaryOperatorImpl<>();
			binaryOp.setLeftHandOperand(iVariableAccessC);
			binaryOp.setRightHandOperand(MutationSupporter.getFactory().createCodeSnippetExpression("null"));
			binaryOp.setKind(BinaryOperatorKind.NE);

			ingredients.add(new Ingredient(binaryOp));

		}

		return ingredients;
	}

	@Override
	public OperatorInstance getConcreteOperatorInstance(MetaOperatorInstance operatorInstance, int metaIdentifier) {

		// We retrieve the information from the operator instance

		Ingredient ingredient = operatorInstance.getAllIngredients().get(metaIdentifier);

		ModificationPoint modificationPoint = operatorInstance.getModificationPoint();

		CtStatement statementPointed = (CtStatement) modificationPoint.getCodeElement();

		SingleWrapIfOperator opIfOperator = new SingleWrapIfOperator(modificationPoint,
				(CtExpression<Boolean>) ingredient.getCode(), statementPointed, this);

		return opIfOperator;
	}

	@Override
	public void setTargetElement(CtElement target) {
		this.targetElement = target;

	}

	@Override
	public boolean checkTargetCompatibility(CtElement target) {

		return true;
	}

	@Override
	public String identifier() {

		return "wrapsIf_NULL";
	}
}
