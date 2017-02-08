package fr.inria.astor.test.repair.evaluation.dpl;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.approaches.jgenprog.operators.InsertAfterOp;
import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.CloneIngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.operators.AstorOperator;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtType;
/**
 * 
 * @author matias
 *
 */
public class TypeCloneIngredientStrategyTestTest {

	protected Logger log = Logger.getLogger(this.getClass().getName());

	@Test
	public void testTypeCloneStrategyIsolatedMath70() throws Exception {
		AstorMain main1 = new AstorMain();
		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		Class typeCloneGranularityClass = CtType.class;

		String[] args = ExecutableCloneIngredientStrategyTest.createCommandM70(learningDir, typeCloneGranularityClass);

		log.debug(Arrays.toString(args));

		main1.execute(args);
		JGenProg engine = (JGenProg) main1.getEngine();

		IngredientSpace ingredientSpace = engine.getIngredientStrategy().getIngredientSpace();

		assertTrue(ingredientSpace.getLocations().size() > 0);

		IngredientSearchStrategy ingStrategy = engine.getIngredientStrategy();

		assertNotNull(ingStrategy);

		assertTrue(ingStrategy instanceof CloneIngredientSearchStrategy<?>);

		CloneIngredientSearchStrategy<CtType> cloneStrategy = (CloneIngredientSearchStrategy<CtType>) ingStrategy;

		Optional<?> elements = cloneStrategy.queryelements();

		assertNotNull(elements);

		Map<String, ?> mapKeys = (Map<String, ?>) elements.get();

		assertEquals(1, mapKeys.keySet().size());

		// Let's take the Program Variant
		ProgramVariant pvariant = engine.getVariants().get(0);

		ModificationPoint buggyStatementModPoint = pvariant.getModificationPoints().get(0);

		assertEquals(72, buggyStatementModPoint.getCodeElement().getPosition().getEndLine());

		AstorOperator testOperator = new ReplaceOp(); // We dont care about the
														// operator here

		CtType buggyClass = buggyStatementModPoint.getCtClass();

		// Important:I had to invoaue this method to execute computesimlist
		// (it's private) before calling getfixspace()
		Ingredient ingredientSelected = cloneStrategy.getFixIngredient(buggyStatementModPoint, testOperator);

		assertNotNull(ingredientSelected);
		
		Queue<CtCodeElement> ingredientsToConsider = cloneStrategy.getfixspace(buggyStatementModPoint, testOperator,
				buggyClass);

		assertNotNull(ingredientsToConsider);

		assertFalse(ingredientsToConsider.isEmpty());

		//The suspicious class has 4 return statements.
		assertEquals("Wrong number of ingredients collected", 4, ingredientsToConsider.size());

		log.debug("Ingredients to considers: "+ ingredientsToConsider);
		
		Queue<CtCodeElement> ingredientsToConsiderInser = cloneStrategy.getfixspace(buggyStatementModPoint,new InsertAfterOp(),
				buggyClass);
		//the suspicious class has 22 statements (it does not include super calls)
		assertEquals("Wrong number of ingredients collected", 22, ingredientsToConsiderInser.size());
		
	}
}
