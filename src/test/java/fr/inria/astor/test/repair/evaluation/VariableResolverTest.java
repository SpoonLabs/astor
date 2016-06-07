package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.manipulation.sourcecode.VariableResolver;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.declaration.CtVariable;

/**
 * 
 * @author Matias Martinez
 *
 */
public class VariableResolverTest {

	@Test
	public void test1VariablesInScope() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "10",
				// Force not evolution
				"-maxgen", "0",
				//
				"-stopfirst", "true", "-maxtime", "100"

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> variants = main1.getEngine().getVariants();

		ProgramVariant pv = variants.get(0);
		ModificationPoint mp = pv.getModificationPoints().get(0);
		System.out.println(mp);
		List<CtVariable> vars = VariableResolver.getVariablesScope(mp.getCodeElement());
		System.out.println(vars);

		// 0 line 72, file BisectionSolver.java
		// final UnivariateRealFunction f, double min, double max, double
		// initial
		// 7 from class + 6 parent + 4 param
		assertEquals((7 + 6 + 4), vars.size());

		ModificationPoint mp1 = pv.getModificationPoints().get(1);

		List<CtVariable> vars1 = VariableResolver.getVariablesScope(mp1.getCodeElement());
		// Now, two locals
		assertEquals((7 + 6 + 2), vars1.size());

		// 0 line 66, file BisectionSolver.java
		// (double min, double max)

		// 7 line 89, file BisectionSolver.java
		// (final UnivariateRealFunction f, double min, double max)

	}

	@Test
	public void test2VariablesInScope() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
			//Force not running
				"-maxgen", "0", "-scope", "package", "-seed", "10" };
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		List<ProgramVariant> variants = main1.getEngine().getVariants();

		ProgramVariant pv = variants.get(0);
		ModificationPoint mp = pv.getModificationPoints().get(0);
		assertEquals(97, ((SuspiciousModificationPoint)mp).getSuspicious().getLineNumber() );
		List<CtVariable> vars = VariableResolver.getVariablesScope(mp.getCodeElement());
		//System.out.println(vars);
		//remember that we exclude serialId fields
		assertEquals((0 +4 + 1), vars.size());

		ModificationPoint mp4 = pv.getModificationPoints().get(4);
		assertEquals(181, ((SuspiciousModificationPoint)mp4).getSuspicious().getLineNumber() );
	
		List<CtVariable> vars4 = VariableResolver.getVariablesScope(mp4.getCodeElement());
		//method local + block local + par + fields
		assertEquals((1 + 3 +4 + 6), vars4.size());
	}

}
