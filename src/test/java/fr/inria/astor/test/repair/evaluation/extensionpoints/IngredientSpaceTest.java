package fr.inria.astor.test.repair.evaluation.extensionpoints;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Before;
import org.junit.Test;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.loop.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.CtLocationIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtClassIngredientSpace;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtGlobalIngredientScope;
import fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtPackageIngredientScope;
import fr.inria.astor.core.manipulation.filters.SingleStatementFixSpaceProcessor;
import fr.inria.astor.test.repair.evaluation.dpl.ExecutableCloneIngredientStrategyTest;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.declaration.CtType;

/**
 * Testing ingredient spaces with CtElement as keys
 * @author Matias Martinez
 *
 */
public class IngredientSpaceTest {

	JGenProg engine = null;
	protected Logger log = Logger.getLogger(this.getClass().getName());

	SingleStatementFixSpaceProcessor  sproc = new SingleStatementFixSpaceProcessor();
	
	@Before
	public void setup() throws Exception {

		AstorMain main1 = new AstorMain();
		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		Class typeCloneGranularityClass = CtType.class;
		
		String[] args = ExecutableCloneIngredientStrategyTest.createCommandM70(learningDir,typeCloneGranularityClass);

	
		main1.execute(args);
		engine = (JGenProg) main1.getEngine();

		
	}
	
	@Test
	public void testClassScope() throws JSAPException{
		
		
		CtLocationIngredientSpace locIngSpace = new CtLocationIngredientSpace(sproc);
		locIngSpace.setCtElementForSplitSpace(CtClass.class);
		ProgramVariant pv = engine.getVariants().get(0);
		
		locIngSpace.defineSpace(pv);
		
		List<?> locations =  locIngSpace.getLocations();
		
		assertNotNull(locations);
		log.debug(((CtClass)locations.get(0)).getSimpleName());
		assertTrue(locations.size()>0);
		assertEquals(2,locations.size());
		assertTrue(locations.get(0) instanceof CtClass);
		
		locations.forEach(item->log.debug(((CtClass)item).getSimpleName()));
		locations = null;
		
		//Now, the same but using the new class
		CtClassIngredientSpace classIngSpace = new CtClassIngredientSpace(sproc);
		classIngSpace.defineSpace(pv);
		locations =  locIngSpace.getLocations();
		assertNotNull(locations);
		log.debug(((CtClass)locations.get(0)).getSimpleName());
		assertTrue(locations.size()>0);
		assertEquals(2,locations.size());
		assertTrue(locations.get(0) instanceof CtClass);
		
		CtElement point0 = pv.getModificationPoints().get(0).getCodeElement();
		CtElement point1 = pv.getModificationPoints().get(1).getCodeElement();
		CtElement point8 = pv.getModificationPoints().get(8).getCodeElement();
		
		CtElement key0 = locIngSpace.calculateLocation(point0);
		CtElement key1 = locIngSpace.calculateLocation(point1);
		CtElement key8 = locIngSpace.calculateLocation(point8);
	
		//modif point 0 and 1 are from the same class, so they must be mapped to the same key
		assertEquals(key0,key1);
		
		//modif point 0 and 8 are from different classes, so they must be mapped to the different keys
		assertNotEquals(key0,key8);
		
		List<CtCodeElement> ingredientsfromK0 = locIngSpace.getIngredients(point1);
		List<CtCodeElement> ingredientsfromK8 = locIngSpace.getIngredients(point8);
		
		
		assertTrue(ingredientsfromK0.contains(point0));

		assertTrue(ingredientsfromK0.contains(point1));

		assertFalse(ingredientsfromK0.contains(point8));
		
		
		assertTrue(ingredientsfromK8.contains(point8));

		assertFalse(ingredientsfromK8.contains(point1));

		assertFalse(ingredientsfromK8.contains(point0));
	}
	
	@Test
	public void testPackageScope() throws JSAPException{
		
		CtLocationIngredientSpace locIngSpacePackage = new CtLocationIngredientSpace(sproc);
		locIngSpacePackage.setCtElementForSplitSpace(CtPackage.class);
		ProgramVariant pv = engine.getVariants().get(0);
		
		locIngSpacePackage.defineSpace(pv);
		
		List<CtElement> lpack =  locIngSpacePackage.getLocations();
		
		assertNotNull(lpack);
		
		log.debug(((CtPackage)lpack.get(0)).getSimpleName());
		//All suspicious are from org.apache.commons.math.analysis.solvers.
		assertEquals(1,lpack.size());
		
		assertTrue(lpack.get(0) instanceof CtPackage);
		
		lpack.forEach(item->log.debug(((CtPackage)item).getSimpleName()));
		
		//--
		CtPackageIngredientScope packageScope = new CtPackageIngredientScope(sproc);
		
		packageScope.defineSpace(pv);
		
		lpack =  packageScope.getLocations();
		
		assertNotNull(lpack);
		
		log.debug(((CtPackage)lpack.get(0)).getSimpleName());
		//All suspicious are from org.apache.commons.math.analysis.solvers.
		assertEquals(1,lpack.size());
		//here, the key must me a package
		assertTrue(lpack.get(0) instanceof CtPackage);
		assertEquals("solvers",((CtPackage)lpack.get(0)).getSimpleName());
		
		
		lpack.forEach(item->log.debug(((CtPackage)item).getSimpleName()));
		
		CtElement point0 = pv.getModificationPoints().get(0).getCodeElement();
		CtElement point8 = pv.getModificationPoints().get(8).getCodeElement();
		
		CtElement key0 = packageScope.calculateLocation(point0);
		CtElement key8 = packageScope.calculateLocation(point8);
	
		assertEquals(key0,key8);
		
		List<CtCodeElement> ingredientsfromK0 = packageScope.getIngredients(point0);
		List<CtCodeElement> ingredientsfromK8 = packageScope.getIngredients(point8);
		
		
		assertTrue(ingredientsfromK0.contains(point0));

		assertTrue(ingredientsfromK0.contains(point8));
		
		assertTrue(ingredientsfromK8.contains(point8));

		assertTrue(ingredientsfromK8.contains(point0));

	}
	
	@Test
	public void testGlobalScope() throws JSAPException{
		CtGlobalIngredientScope globalSpace = new CtGlobalIngredientScope(sproc);
		globalSpace.setCtElementForSplitSpace(CtPackage.class);
		ProgramVariant pv = engine.getVariants().get(0);
		
		//We expect as key the root package.
		CtElement root = globalSpace.calculateLocation(pv.getModificationPoints().get(0).getCodeElement());
		log.debug(root);
		assertNotNull(root);
		assertEquals(CtPackage.TOP_LEVEL_PACKAGE_NAME,root.toString());
		
		CtElement root2 = globalSpace.calculateLocation(pv.getModificationPoints().get(1).getCodeElement());
		assertNotNull(root2);
		
		assertEquals(root, root2);
		
		globalSpace.defineSpace(pv);
		
		List<CtElement> spaceglobal =  globalSpace.getLocations();
		
		assertNotNull(spaceglobal);
		
		assertEquals(CtPackage.TOP_LEVEL_PACKAGE_NAME,((CtPackage)spaceglobal.get(0)).getSimpleName());
		
		
		log.debug(((CtPackage)spaceglobal.get(0)).getSimpleName());
		//All suspicious are from org.apache.commons.math.analysis.solvers.
		assertEquals(1,spaceglobal.size());
		
		assertTrue(spaceglobal.get(0) instanceof CtPackage);
		
		spaceglobal.forEach(item->System.out.println(((CtPackage)item).getSimpleName()));
		
		
		
		CtElement point0 = pv.getModificationPoints().get(0).getCodeElement();
		CtElement point8 = pv.getModificationPoints().get(8).getCodeElement();
		
		CtElement key0 = globalSpace.calculateLocation(point0);
		CtElement key8 = globalSpace.calculateLocation(point8);
	
		assertEquals(key0,key8);
		
		List<CtCodeElement> ingredientsfromK0 = globalSpace.getIngredients(point0);
		List<CtCodeElement> ingredientsfromK8 = globalSpace.getIngredients(point8);
		
		
		assertTrue(ingredientsfromK0.contains(point0));

		assertTrue(ingredientsfromK0.contains(point8));
		
		assertTrue(ingredientsfromK8.contains(point8));

		assertTrue(ingredientsfromK8.contains(point0));
	}
	
	@Test
	public void testLocalComplete() throws Exception{
		AstorMain main1 = new AstorMain();
		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		Class typeCloneGranularityClass = CtType.class;
		
		String[] args = ExecutableCloneIngredientStrategyTest.createCommandM70(
				//
				learningDir,//
				typeCloneGranularityClass,//
				100,// 
				true,//
				CtClassIngredientSpace.class.getCanonicalName()//
				);

	
		main1.execute(args);
		engine = (JGenProg) main1.getEngine();

		assertTrue(engine.getSolutions().size() > 0);
		
		IngredientSearchStrategy strategy = engine.getIngredientStrategy();
		CtClassIngredientSpace space = 	(CtClassIngredientSpace) strategy.getIngredientSpace();
		List<CtElement> locations = space.getLocations();
		assertEquals(2, locations.size());
		locations.forEach(e -> log.debug(e.getShortRepresentation()));
		assertTrue(locations.get(0) instanceof CtClass);
		CtClass susp = (CtClass) locations.get(0);
		assertTrue(susp.getQualifiedName().equals("org.apache.commons.math.analysis.solvers.BisectionSolver")||
				susp.getQualifiedName().equals("org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils")
				);
	}
	
	@Test
	public void testPackageComplete() throws Exception{
		AstorMain main1 = new AstorMain();
		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		Class typeCloneGranularityClass = CtType.class;
		
		String[] args = ExecutableCloneIngredientStrategyTest.createCommandM70(
				//
				learningDir,//
				typeCloneGranularityClass,//
				100,// 
				true,//
				CtPackageIngredientScope.class.getCanonicalName()//
				);

	
		main1.execute(args);
		engine = (JGenProg) main1.getEngine();

		assertTrue(engine.getSolutions().size() > 0);
		
		IngredientSearchStrategy strategy = engine.getIngredientStrategy();
		CtPackageIngredientScope space = 	(CtPackageIngredientScope) strategy.getIngredientSpace();
		List<CtElement> locations = space.getLocations();
		assertEquals(1, locations.size());
		locations.forEach(e -> log.debug(e.getShortRepresentation()));
		assertTrue(locations.get(0) instanceof CtPackage);
		CtPackage susp = (CtPackage) locations.get(0);
		assertTrue(susp.getQualifiedName().equals("org.apache.commons.math.analysis.solvers")
			);
	}
	@Test
	public void testGlobalComplete() throws Exception{
		AstorMain main1 = new AstorMain();
		ClassLoader classLoader = getClass().getClassLoader();
		File learningDir = new File(classLoader.getResource("learningm70").getFile());

		Class typeCloneGranularityClass = CtType.class;
		
		String[] args = ExecutableCloneIngredientStrategyTest.createCommandM70(
				//
				learningDir,//
				typeCloneGranularityClass,//
				100,// 
				true,//
				CtGlobalIngredientScope.class.getCanonicalName()//
				);

	
		main1.execute(args);
		engine = (JGenProg) main1.getEngine();

		assertTrue(engine.getSolutions().size() > 0);
		
		IngredientSearchStrategy strategy = engine.getIngredientStrategy();
		CtGlobalIngredientScope space = 	(CtGlobalIngredientScope) strategy.getIngredientSpace();
		List<CtElement> locations = space.getLocations();
		locations.forEach(e -> log.debug(e.getShortRepresentation()));
		
		assertEquals(1, locations.size());
		assertTrue(locations.get(0) instanceof CtPackage);
		//In global also is a package (the root)
		CtPackage susp = (CtPackage) locations.get(0);
		//assertEquals(CtPackage.TOP_LEVEL_PACKAGE_NAME,susp.getQualifiedName());
	}
	
}
