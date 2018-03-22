package fr.inria.astor.test.repair.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;

import fr.inria.astor.approaches.ingredientbased.IngredientBasedApproach;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.manipulation.sourcecode.InvocationResolver;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.astor.util.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtAbstractInvocation;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class InvocationResolverTest {

	@SuppressWarnings({ "rawtypes", "static-access" })
	@Test
	public void testFunctionResolver() throws Exception {
		CommandSummary command = MathCommandsTests.getMath70Command();
		command.command.put("-maxgen", "0");
		command.command.put("-scope", "package");
		command.command.put("-stopfirst", "true");
		command.command.put("-parameters", "skipfitnessinitialpopulation:true:"
				+ "maxmodificationpoints:1000000:maxsuspcandidates:1000000:maxnumbersolutions:30:nomodificationconvergence:500:limitbysuspicious:false");

		AstorMain main = new AstorMain();
		main.execute(command.flat());

		InvocationResolver fr = new InvocationResolver();
		for (ModificationPoint mp : main.getEngine().getVariants().get(0).getModificationPoints()) {
			System.out.println("-->" + mp.getCodeElement());
		}
		ModificationPoint modificationPoint0 = main.getEngine().getVariants().get(0).getModificationPoints().get(0);
		CtElement code = modificationPoint0.getCodeElement();
		assertEquals("return solve(min, max)", code.toString());
		List<CtAbstractInvocation> inv = fr.collectInvocation(code, true);

		assertTrue(inv.size() > 0);

		CtAbstractInvocation inv0 = inv.get(0);
		CtClass ctClassMP = modificationPoint0.getCtClass();

		assertTrue(InvocationResolver.fitImplicitInvocation(ctClassMP, inv0));

		ModificationPoint modificationPoint7 = main.getEngine().getVariants().get(0).getModificationPoints().get(7);
		CtElement code7 = modificationPoint7.getCodeElement();

		assertEquals("fmin = f.value(min)", code7.toString());
		List<CtAbstractInvocation> inv7 = fr.collectInvocation(code7, true);

		assertTrue(inv7.size() > 0);
		CtAbstractInvocation inv7e = inv7.get(0);

		assertTrue(InvocationResolver.fitImplicitInvocation(ctClassMP, inv7e));

		IngredientBasedApproach iba = (IngredientBasedApproach) main.getEngine();
		List ingredients = iba.getIngredientPool().getIngredients(modificationPoint0.getCodeElement());
		int i = 0;
		for (Object object : ingredients) {
			System.out.println(Integer.valueOf(i++) + " " + object.toString());
		}
		
		// 358 fb = function.value(b)
		CtElement i358 = (CtElement) ingredients.get(358);
		assertEquals("fb = function.value(b)", i358.toString());
		List<CtAbstractInvocation> ingredients358 = fr.collectInvocation(i358, true);
		assertTrue(ingredients358.size() > 0);
		CtAbstractInvocation ingrediet358 = ingredients358.get(0);
		assertTrue(InvocationResolver.fitImplicitInvocation(ctClassMP, ingrediet358));

		// 23 double yInitial = f.value(initial)
		CtElement i23 = (CtElement) ingredients.get(23);
		assertEquals("double yInitial = f.value(initial)", i23.toString());
		List<CtAbstractInvocation> ingredients23 = fr.collectInvocation(i23, true);
		assertTrue(ingredients23.size() > 0);
		CtAbstractInvocation ingrediet23 = ingredients23.get(0);
		assertTrue(InvocationResolver.fitImplicitInvocation(ctClassMP, ingrediet23));

		// invocation to method from another class
		CtElement i130 = (CtElement) ingredients.get(130);
		assertEquals("return solveAll(c, z)", i130.toString());
		List<CtAbstractInvocation> ingredients130 = fr.collectInvocation(i130, true);
		assertTrue(ingredients130.size() > 0);
		CtAbstractInvocation ingrediet130 = ingredients130.get(0);
		assertFalse(InvocationResolver.fitImplicitInvocation(ctClassMP, ingrediet130));

		// static method inv
		// 140 java.lang.System.arraycopy(c, 0, subarray, 0, subarray.length)
		CtExpression i140 = (CtExpression) ingredients.get(140);
		assertEquals("java.lang.System.arraycopy(c, 0, subarray, 0, subarray.length)", i140.toString());
		List<CtAbstractInvocation> ingredients140 = fr.collectInvocation(i140, true);
		assertTrue(ingredients140.size() > 0);
		CtAbstractInvocation ingrediet140 = ingredients140.get(0);
		assertTrue(InvocationResolver.fitImplicitInvocation(ctClassMP, ingrediet140));

		// Same method name, different arg types
		// 141 root[i] = solve(subarray, initial)
		CtElement i141 = (CtElement) ingredients.get(141);
		assertEquals("root[i] = solve(subarray, initial)", i141.toString());
		// assertEquals("solve(org.apache.commons.math.complex.Complex[],org.apache.commons.math.complex.Complex)",
		// actual);
		List<CtAbstractInvocation> ingredients141 = fr.collectInvocation(i141, true);
		assertTrue(ingredients141.size() > 0);
		CtAbstractInvocation ingrediet141 = ingredients141.get(0);
		assertFalse(InvocationResolver.fitImplicitInvocation(ctClassMP, ingrediet141));

		// Same signature
		// 108 return solve(f, initial, max)
		CtElement i108 = (CtElement) ingredients.get(108);

		assertEquals("return solve(f, initial, max)", i108.toString());
		List<CtAbstractInvocation> ingredients108 = fr.collectInvocation(i108, true);
		// It comes from another class:
		// org.apache.commons.math.analysis.solvers.LaguerreSolver
		assertEquals("LaguerreSolver", i108.getParent(CtClass.class).getSimpleName());
		assertTrue(ingredients108.size() > 0);
		CtAbstractInvocation ingrediet108 = ingredients108.get(0);
		assertTrue(InvocationResolver.fitImplicitInvocation(ctClassMP, ingrediet108));

	}

}
