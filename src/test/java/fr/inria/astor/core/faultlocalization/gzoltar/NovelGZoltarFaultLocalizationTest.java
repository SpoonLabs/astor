package fr.inria.astor.core.faultlocalization.gzoltar;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;

/**
 * 
 * @author Matias Martinez
 *
 */
public class NovelGZoltarFaultLocalizationTest {

	@Test
	void testParseLine() {

		String line = "org.apache.commons.math.analysis.solvers$BisectionSolver#solve(org.apache.commons.math.analysis.UnivariateRealFunction,double,double,double):72;0.7071067811865475";

		NovelGZoltarFaultLocalization ng = new NovelGZoltarFaultLocalization();
		SuspiciousCode sc = ng.parseLine(line);

		assertEquals("org.apache.commons.math.analysis.solvers.BisectionSolver", sc.getClassName());
		assertEquals("solve(org.apache.commons.math.analysis.UnivariateRealFunction,double,double,double)",
				sc.getMethodName());
		assertEquals(72, sc.getLineNumber());
		assertEquals("0.707", sc.getSuspiciousValueString());

	}

	@Test
	void testAll() {

		String line = "org.apache.commons.math.analysis.solvers$BisectionSolver#solve(org.apache.commons.math.analysis.UnivariateRealFunction,double,double,double):72;0.7071067811865475";

		NovelGZoltarFaultLocalization ng = new NovelGZoltarFaultLocalization();
		SuspiciousCode sc = ng.parseLine(line);

		assertEquals("org.apache.commons.math.analysis.solvers.BisectionSolver", sc.getClassName());
		assertEquals("solve(org.apache.commons.math.analysis.UnivariateRealFunction,double,double,double)",
				sc.getMethodName());
		assertEquals(72, sc.getLineNumber());
		assertEquals("0.707", sc.getSuspiciousValueString());

	}
}
