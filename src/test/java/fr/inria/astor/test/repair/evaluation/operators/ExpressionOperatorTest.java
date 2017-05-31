package fr.inria.astor.test.repair.evaluation.operators;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import java.util.function.BinaryOperator;

import org.junit.Before;
import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.approaches.jgenprog.operators.ExpressionOperator;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.OperatorInstance;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.spaces.ingredients.AstorIngredientSpace;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.ExpressionIngredientSpaceProcessor;
import fr.inria.astor.test.repair.evaluation.regression.MathTests;
import fr.inria.astor.util.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import fr.inria.main.evolution.ExtensionPoints;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtBinaryOperator;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtClass;
/**
 * 
 * @author Matias Martinez
 *
 */
public class ExpressionOperatorTest {

	@Test
	public void testM70Expression() throws Exception {

		CommandSummary command = MathTests.getMath70Command();
		command.command.put("-parameters", ExtensionPoints.INGREDIENT_PROCESSOR.identifier + File.pathSeparator
				+ ExpressionIngredientSpaceProcessor.class.getCanonicalName());
		command.command.put("-maxgen", "0");// Avoid evolution

		AstorMain main1 = new AstorMain();
		main1.execute(command.flat());

		List<ProgramVariant> variantss = main1.getEngine().getVariants();
		assertTrue(variantss.size() > 0);

		JGenProg jgp = (JGenProg) main1.getEngine();
		AstorIngredientSpace ingSpace = (AstorIngredientSpace) jgp.getIngredientStrategy().getIngredientSpace();
		
		int i = 0;
		for (ModificationPoint modpoint : variantss.get(0).getModificationPoints()) {
			//System.out.println("--> "+(i++)+" " +modpoint.getCodeElement());
			
		}
		ModificationPoint modificationPoint = variantss.get(0).getModificationPoints().get(14);
		CtExpression originalExp = (CtExpression) modificationPoint.getCodeElement();
		assertEquals("i < (maximalIterationCount)", originalExp.toString());
		CtClass parentClass = originalExp.getParent(CtClass.class);
		String parentClassString = parentClass.toString();
		
		assertNotNull(parentClass);
		//Let's mutate the expression
		CtExpression clonedExp = (CtExpression) MutationSupporter.clone(originalExp);
		CtBinaryOperator binOpExpr = (CtBinaryOperator) clonedExp;
		//Let's check that the operator that will be inserted is not equal to the current.
		assertNotEquals(BinaryOperatorKind.GT, binOpExpr.getKind());
		//Update operator
		binOpExpr.setKind(BinaryOperatorKind.GT);
		

		ExpressionOperator expOperator = new ExpressionOperator();
		OperatorInstance expOperatorInstance = new OperatorInstance(modificationPoint, expOperator, originalExp, clonedExp);
		
		boolean applied = expOperatorInstance.applyModification();
		assertTrue(applied);
		
		assertNotEquals(clonedExp, originalExp);
		assertEquals("i > (maximalIterationCount)", clonedExp.toString());
		assertEquals("i > (maximalIterationCount)", modificationPoint.getCodeElement().toString());
		
		
		//Let's check that the mutated class is different to the original
		String mutatedClassString = parentClass.toString();
		assertNotEquals(parentClassString, mutatedClassString);
		
		//Undo operator, we should have the original class
		boolean undo = expOperatorInstance.undoModification();
		String revertedChangeClassString = parentClass.toString();
		assertTrue(undo);
		assertEquals("i < (maximalIterationCount)", modificationPoint.getCodeElement().toString());
		
		assertEquals(parentClassString, revertedChangeClassString);
		
	
		
		
	}
}
