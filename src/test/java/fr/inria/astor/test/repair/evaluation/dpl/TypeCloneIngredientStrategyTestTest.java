package fr.inria.astor.test.repair.evaluation.dpl;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.Queue;

import org.apache.log4j.Level;
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
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtClassIngredientSpace;
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

	
	static public String[] createCommandM70(File learningDir, Class cloneGranularityClass, int generations,
			boolean transformIngredient, String scope, Double flt) {
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		String[] args = new String[] { "-dependencies", dep, "-mode", "statement",
				//
				"-failing", "org.apache.commons.math.analysis.solvers.BisectionSolverTest",
				//
				"-location", new File("./examples/math_70").getAbsolutePath(),
				//
				"-package", "org.apache.commons", "-srcjavafolder",
				//
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes",
				//
				"-javacompliancelevel", "7", "-flthreshold", flt.toString(), "-out", out.getAbsolutePath(),
				//
				"-scope", scope,
				//
				"-seed", "10",

				"-maxgen", Integer.toString(generations),
				//
				"-population", "1",
				//
				"-stopfirst", "true",
				//
				"-maxtime", "100",
				//
				// Learning Arguments
				"-learningdir", learningDir.getAbsolutePath(),
				//
				"-clonegranularity", cloneGranularityClass.getCanonicalName(),
				//
				"-ingredientstrategy", CloneIngredientSearchStrategy.class.getName(),
				//
				"-transformingredient",
				//
				"-loglevel", Level.DEBUG.toString() };
		return args;
	}
	
	@Test
	public void sup1testTypeCloneStrategyIsolatedMath70OneSuspicious() throws Exception {
		AstorMain main1 = new AstorMain();
		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		Class typeCloneGranularityClass = CtType.class;

		String[] args = 
				createCommandM70(learningDir, typeCloneGranularityClass, 0, false, CtClassIngredientSpace.class.getCanonicalName(), 0.5);

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

		//Using 0.1 of thr we have two locations. Using 0.5 we have only 1.
		//assertEquals(Double.valueOf(0.1), ConfigurationProperties.getPropertyDouble("flthreshold"));
		
		mapKeys.keySet().forEach(e-> System.out.println("key :"+e));
		
		assertEquals(1, mapKeys.keySet().size());

		// Let's take the Program Variant
		ProgramVariant pvariant = engine.getVariants().get(0);

		ModificationPoint buggyStatementModPoint = pvariant.getModificationPoints().get(0);

		assertEquals(72, buggyStatementModPoint.getCodeElement().getPosition().getEndLine());

		AstorOperator testOperator = new ReplaceOp(); // We dont care about the
														// operator here

		CtType buggyClass = buggyStatementModPoint.getCtClass();

		// Important:I had to invoke this method to execute computesimlist
		// (it's private) before calling getfixspace()
		Ingredient ingredientSelected = cloneStrategy.getFixIngredient(buggyStatementModPoint, testOperator);

		assertNotNull(ingredientSelected);
		
		Queue<CtCodeElement> ingredientsToConsider = cloneStrategy.getfixspace(buggyStatementModPoint, testOperator,
				buggyClass);

		assertNotNull(ingredientsToConsider);

		assertFalse(ingredientsToConsider.isEmpty());
		
		System.out.println(ingredientsToConsider);

		//The suspicious class has 4 return statements.
		//They are: [return solve(f, min, max), return solve(f, min, max), return solve(min, max), return m]
		assertEquals("Wrong number of ingredients collected", 4, ingredientsToConsider.size());

		log.debug("Ingredients to considers: "+ ingredientsToConsider);
		
		Queue<CtCodeElement> ingredientsToConsiderInser = cloneStrategy.getfixspace(buggyStatementModPoint,new InsertAfterOp(),
				buggyClass);
		//the suspicious class has 22 statements (it does not include super calls)
		assertEquals("Wrong number of ingredients collected", 22, ingredientsToConsiderInser.size());
		
	}
	
	
	@Test
	public void susp2testTypeCloneStrategyIsolatedMath70TwoSuspicious() throws Exception {
		AstorMain main1 = new AstorMain();
		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		Class typeCloneGranularityClass = CtType.class;

		String[] args = 
				createCommandM70(learningDir, typeCloneGranularityClass, 0, false, CtClassIngredientSpace.class.getCanonicalName(), 0.1);

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

		//Using 0.1 of thr we have two locations. Using 0.5 we have only 1.
		assertEquals(Double.valueOf(0.1), ConfigurationProperties.getPropertyDouble("flthreshold"));
		
		mapKeys.keySet().forEach(e-> System.out.println("key :"+e));
		
		assertEquals(2, mapKeys.keySet().size());

		// Let's take the Program Variant
		ProgramVariant pvariant = engine.getVariants().get(0);

		ModificationPoint buggyStatementModPoint = pvariant.getModificationPoints().get(0);

		assertEquals(72, buggyStatementModPoint.getCodeElement().getPosition().getEndLine());

		AstorOperator testOperator = new ReplaceOp(); // We dont care about the
														// operator here

		CtType buggyClass = buggyStatementModPoint.getCtClass();

		// Important:I had to invoke this method to execute computesimlist
		// (it's private) before calling getfixspace()
		Ingredient ingredientSelected = cloneStrategy.getFixIngredient(buggyStatementModPoint, testOperator);

		assertNotNull(ingredientSelected);
		
		Queue<CtCodeElement> ingredientsToConsider = cloneStrategy.getfixspace(buggyStatementModPoint, testOperator,
				buggyClass);

		assertNotNull(ingredientsToConsider);

		assertFalse(ingredientsToConsider.isEmpty());
		
		System.out.println("Buggy mp "+ buggyStatementModPoint);
		System.out.println(ingredientsToConsider);

		//The suspicious class has 9 return statements.
		//4 returns from Bisection and 5 from Univariate
		assertEquals("Wrong number of ingredients collected", 9, ingredientsToConsider.size());

		log.debug("Ingredients to considers: "+ ingredientsToConsider);
		
		//Now, Insert operator
		
		Queue<CtCodeElement> ingredientsToConsiderInser = cloneStrategy.getfixspace(buggyStatementModPoint,new InsertAfterOp(),
				buggyClass);
		System.out.println(ingredientsToConsiderInser );
		//the suspicious classes has 52 statements (it does not include super calls)
		assertEquals("Wrong number of ingredients collected", 52, ingredientsToConsiderInser.size());
		
	}
	
	
	
	@Test
	public void susp2testTypeCloneStrategyComplete() throws Exception {
		AstorMain main1 = new AstorMain();
		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		Class typeCloneGranularityClass = CtType.class;

		String[] args = 
				createCommandM70(learningDir, typeCloneGranularityClass, 0, false, CtClassIngredientSpace.class.getCanonicalName(), 0.1);

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

		//Using 0.1 of thr we have two locations. Using 0.5 we have only 1.
		assertEquals(Double.valueOf(0.1), ConfigurationProperties.getPropertyDouble("flthreshold"));
		
		mapKeys.keySet().forEach(e-> System.out.println("key :"+e));
		
		assertEquals(2, mapKeys.keySet().size());

		// Let's take the Program Variant
		ProgramVariant pvariant = engine.getVariants().get(0);

		ModificationPoint buggyStatementModPoint = pvariant.getModificationPoints().get(0);

		assertEquals(72, buggyStatementModPoint.getCodeElement().getPosition().getEndLine());

		AstorOperator testOperator = new ReplaceOp(); // We dont care about the
														// operator here

		CtType buggyClass = buggyStatementModPoint.getCtClass();

		// Important:I had to invoke this method to execute computesimlist
		// (it's private) before calling getfixspace()
		Ingredient ingredientSelected = cloneStrategy.getFixIngredient(buggyStatementModPoint, testOperator);

		assertNotNull(ingredientSelected);
		
		Queue<CtCodeElement> ingredientsToConsider = cloneStrategy.getfixspace(buggyStatementModPoint, testOperator,
				buggyClass);

		assertNotNull(ingredientsToConsider);

		assertFalse(ingredientsToConsider.isEmpty());
		
		System.out.println("Buggy mp "+ buggyStatementModPoint);
		System.out.println(ingredientsToConsider);

		//The suspicious class has 9 return statements.
		//4 returns from Bisection and 5 from Univariate
		assertEquals("Wrong number of ingredients collected", 9, ingredientsToConsider.size());

		log.debug("Ingredients to considers: "+ ingredientsToConsider);
		
		//Now, Insert operator
		
		Queue<CtCodeElement> ingredientsToConsiderInser = cloneStrategy.getfixspace(buggyStatementModPoint,new InsertAfterOp(),
				buggyClass);
		System.out.println(ingredientsToConsiderInser );
		//the suspicious classes has 52 statements (it does not include super calls)
		assertEquals("Wrong number of ingredients collected", 52, ingredientsToConsiderInser.size());
		
	}
}
