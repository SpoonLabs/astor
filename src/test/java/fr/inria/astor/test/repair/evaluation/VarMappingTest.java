package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.AstorCoreEngine;
import fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.CloneIngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtClassIngredientSpace;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.sourcecode.VarMapping;
import fr.inria.astor.core.manipulation.sourcecode.VarAccessWrapper;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtStatement;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;

/**
 * 
 * @author Matias Martinez
 *
 */
public class VarMappingTest {

	protected Logger log = Logger.getLogger(this.getClass().getName());
	private File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

	JGenProg engine = null;
	CtElement c1 = null;
	CtElement c3 = null;
	CtElement ingredientCtElementC7 = null;
	CtElement otherClassElementC8 = null;

	@Before
	public void setup() throws Exception {
		engine = null;
		c1 = null;
		c3 = null;
		ingredientCtElementC7 = null;
		otherClassElementC8 = null;

		AstorMain main1 = new AstorMain();
		MutationSupporter.factory = null;
		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		Class typeCloneGranularityClass = CtType.class;

		ConfigurationProperties.setProperty("clusteringfilename", "clustering_test.csv");
		String[] args = createCommandM70(learningDir, typeCloneGranularityClass, 100, true,
				CtClassIngredientSpace.class.getCanonicalName())

		;

		log.debug(Arrays.toString(args));

		main1.execute(args);
		engine = (JGenProg) main1.getEngine();

		ProgramVariant pv = engine.getVariants().get(0);
		// C1:.BisectionSolver l: 66,
		c1 = pv.getModificationPoints().get(0).getCodeElement();
		// C3: clearResult(); BisectionSolver l: 80,
		c3 = pv.getModificationPoints().get(2).getCodeElement();
		ingredientCtElementC7 = pv.getModificationPoints().get(7).getCodeElement();
		// C8 return (a + b) * .5; from line 223, file
		// UnivariateRealSolverUtils.java

		otherClassElementC8 = pv.getModificationPoints().get(8).getCodeElement();
		VariableResolver.cluster.getClusters().clear();
	}

	@Test
	public void testSameVariableTwiceOnIngredient() {
		// a modified clustering for facilitating testing task
		ConfigurationProperties.setProperty("clusteringfilename", "clustering_test.csv");

		// we take a ingredient from QuinticFunction
		// return (x-1)*(x-0.5)*x*(x+0.5)*(x+1); line 32.
		CtMethod cm = getMethodFromClass(engine, "org.apache.commons.math.analysis.QuinticFunction", "value");
		assertNotNull(cm);
		CtStatement stmMultiplesXs = cm.getBody().getStatement(0);
		assertNotNull(stmMultiplesXs);

		log.debug("ingredient:  " + stmMultiplesXs);

		List<CtVariable> varContextC3 = VariableResolver.searchVariablesInScope(c3);
		// the ingredient has 5 Xs
		VarMapping vmappingOnevar = VariableResolver.mapVariables(varContextC3, stmMultiplesXs);
		assertEquals(5, vmappingOnevar.getMappedVariables().size());
		assertEquals(5, vmappingOnevar.getMappedVariables().keySet().size());
		assertTrue(vmappingOnevar.getNotMappedVariables().isEmpty());

		List<Map<String, CtVariable>> allCombinationsOne = VariableResolver
				.findAllVarMappingCombination(vmappingOnevar.getMappedVariables());

		log.debug(allCombinationsOne);
		assertFalse(allCombinationsOne.isEmpty());

		CtElement cloned = engine.getMutatorSupporter().clone((CtCodeElement) stmMultiplesXs);

		log.debug("Trying mapping " + allCombinationsOne.get(0));
		Map<VarAccessWrapper, CtVariableAccess> originalMapOnevarTomap = VariableResolver
				.convertIngredient(vmappingOnevar, allCombinationsOne.get(0));

		assertEquals(5, originalMapOnevarTomap.size());//Modified

		assertFalse(cloned.equals(stmMultiplesXs));

		log.debug("5Xs after transformation: " + stmMultiplesXs);

		VariableResolver.resetIngredient(originalMapOnevarTomap);

		log.debug("5Xs again original : " + stmMultiplesXs);

		assertTrue(cloned.equals(stmMultiplesXs));

	}

	@Test
	public void testVarsOutOfScope() throws Exception {

		List<CtVariable> varContextC1 = VariableResolver.searchVariablesInScope(c1);
		List<CtVariable> varContextC3 = VariableResolver.searchVariablesInScope(c3);//

		List<CtVariableAccess> variablesOutOfScope = VariableResolver.retriveVariablesOutOfContext(varContextC1,
				ingredientCtElementC7);
		assertNotNull(variablesOutOfScope);
		assertEquals(1, variablesOutOfScope.size());
		log.debug("Out scope: " + variablesOutOfScope);
		assertEquals("fmin", variablesOutOfScope.get(0).getVariable().getSimpleName());

	}

	@Test
	public void testMapOther2vars() {

		List<CtVariable> varContextC1 = VariableResolver.searchVariablesInScope(c1);
		// ingredient return (a + b) * .5
		VarMapping vmapping1 = VariableResolver.mapVariables(varContextC1, otherClassElementC8);
		Map<VarAccessWrapper, List<CtVariable>> mapsVariablesOutC1 = vmapping1.getMappedVariables();
		log.debug("mapping 1 -->" + mapsVariablesOutC1);
		assertFalse(mapsVariablesOutC1.values().isEmpty());
		assertEquals(2, mapsVariablesOutC1.keySet().size());

	}

	@Test
	public void testNotMappedVars() {
		// Testing Not Mapped variables
		// We get a method setup(UnivariateRealFunction f) for testing the
		// insertion of a ingredient out of scope
		CtMethod mSetup = getMethod4Test1(engine);
		assertNotNull(mSetup);
		CtStatement stmSetup = mSetup.getBody().getStatement(0);
		List<CtVariable> varsScopeStmSetup = VariableResolver.searchVariablesInScope(stmSetup);
		assertFalse(varsScopeStmSetup.isEmpty());
		// field: private static final String NULL_FUNCTION_MESSAGE, parameter
		// UnivariateRealFunction f
		assertEquals(2, varsScopeStmSetup.size());
		log.debug("context of Setup method " + varsScopeStmSetup);

		VarMapping vmapping3 = VariableResolver.mapVariables(varsScopeStmSetup, otherClassElementC8);
		assertTrue(vmapping3.getMappedVariables().isEmpty());
		assertFalse(vmapping3.getNotMappedVariables().isEmpty());
		assertEquals(2, vmapping3.getNotMappedVariables().size());

	}

	@Test
	public void testMappingTwoVars() {
		// We try to insert C8 (a + b ...) in the place ofC3 (clearResult)
		List<CtVariable> varContextC3 = VariableResolver.searchVariablesInScope(c3);
		VarMapping vmapping2 = VariableResolver.mapVariables(varContextC3, otherClassElementC8);

		Map<VarAccessWrapper, List<CtVariable>> mapsVariablesOutC2 = vmapping2.getMappedVariables();

		log.debug("mapping 2 -->" + mapsVariablesOutC2 + "\n to we put in context: " + varContextC3);
		// Here, the mapping must not be empty
		assertFalse(mapsVariablesOutC2.values().isEmpty());
		// one key for each unmapped var
		assertTrue(mapsVariablesOutC2.keySet().size() == 2);
		// all vars were mapped
		log.debug("not mapped " + vmapping2.getNotMappedVariables());
		assertTrue(vmapping2.getNotMappedVariables().isEmpty());

		// We get a method setup(UnivariateRealFunction f) for testing the
		// insertion of a ingredient out of scope
		// CtMethod mSetup = getMethod4Test1(engine);
		// assertNotNull(mSetup);

	}

	@Test
	public void testAllVarCombination() {

		List<CtVariable> varContextC3 = VariableResolver.searchVariablesInScope(c3);//

		// We try to insert C8 (a + b ...) in the place ofC3 (clearResult)
		VarMapping vmapping2 = VariableResolver.mapVariables(varContextC3, otherClassElementC8);

		List<Map<String, CtVariable>> allCombinations = VariableResolver
				.findAllVarMappingCombination(vmapping2.getMappedVariables());

		assertFalse(allCombinations.isEmpty());
		assertEquals(4, allCombinations.size());

		log.debug("C8 before " + otherClassElementC8);

		CtElement cloned = engine.getMutatorSupporter().clone((CtCodeElement) otherClassElementC8);

		log.debug("Trying mapping " + allCombinations.get(0));

		Map<VarAccessWrapper, CtVariableAccess> originalMap = VariableResolver.convertIngredient(vmapping2,
				allCombinations.get(0));

		log.debug("C8 after: " + otherClassElementC8);

		assertFalse(cloned.equals(otherClassElementC8));

		VariableResolver.resetIngredient(originalMap);

		log.debug("C8 after reset: " + otherClassElementC8);

		assertEquals(cloned, otherClassElementC8);

		// Now, let's analyze the second proposed var combination.
		log.debug("Trying mapping " + allCombinations.get(1));

		Map<VarAccessWrapper, CtVariableAccess> originalMap2 = VariableResolver.convertIngredient(vmapping2,
				allCombinations.get(1));

		log.debug("C8 after 2: " + otherClassElementC8);

		assertFalse(cloned.equals(otherClassElementC8));

		// ---
		CtElement clonedMapped1 = engine.getMutatorSupporter().clone((CtCodeElement) otherClassElementC8);

		VariableResolver.resetIngredient(originalMap2);

		log.debug("C8 after reset 2: " + otherClassElementC8);

		assertEquals(cloned, otherClassElementC8);
		assertFalse(clonedMapped1.equals(otherClassElementC8));

		// ------
		log.debug("Trying mapping " + allCombinations.get(2));

		Map<VarAccessWrapper, CtVariableAccess> originalMap3 = VariableResolver.convertIngredient(vmapping2,
				allCombinations.get(2));

		log.debug("C8 after 2: " + otherClassElementC8);

	}

	@Test
	public void testIngredientWithoutVars() {
		// We will try to put c3 (clearResult()) in c1.
		List<CtVariable> varContextC1 = VariableResolver.searchVariablesInScope(c1);

		VarMapping vmapping2 = VariableResolver.mapVariables(varContextC1, c3);
		// As the ingredient has not value, all collections must be empty
		assertTrue(vmapping2.getNotMappedVariables().isEmpty());
		assertTrue(vmapping2.getNotMappedVariables().isEmpty());

		List<Map<String, CtVariable>> allCombinationsOne = VariableResolver
				.findAllVarMappingCombination(vmapping2.getMappedVariables());

		log.debug(allCombinationsOne);
		assertTrue(allCombinationsOne.isEmpty());

	}

	/**
	 * //setup from UnivariateRealSolverUtils
	 * 
	 * @return
	 */
	private CtMethod getMethod4Test1(AstorCoreEngine core) {
		return getMethodFromClass(core, "org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils", "setup");
	}

	protected CtMethod getMethodFromClass(AstorCoreEngine core, String classname, String methodName) {
		//
		log.debug("Searching for class " + classname);
		List<CtType<?>> classes = core.getMutatorSupporter().getFactory().Type().getAll();
		CtType cUniv = classes.stream().filter(x -> x.getQualifiedName().equals(classname)).findFirst().get();
		CtMethod mSetup = (CtMethod) cUniv.getAllMethods().stream()
				.filter(x -> ((CtMethod) x).getSimpleName().equals(methodName)).findFirst().get();
		return mSetup;
		//
	}

	static public String[] createCommandM70(File learningDir, Class cloneGranularityClass, int generations,
			boolean transformIngredient, String scope) {
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();

		String[] args = new String[] { "-dependencies", dep, "-mode", "statement",
				//
				"-failing", "org.apache.commons.math.analysis.solvers.BisectionSolverTest",
				//
				"-location", new File("./examples/math_70_modified").getAbsolutePath(),
				//
				"-package", "org.apache.commons", "-srcjavafolder",
				//
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes",
				//
				"-javacompliancelevel", "7", "-flthreshold", "0.1", "-out", out.getAbsolutePath(),
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

}
