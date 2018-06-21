package fr.inria.astor.test.repair.evaluation.extensionpoints.faultlocalization;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.faultlocalization.FaultLocalizationResult;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.faultlocalization.gzoltar.GZoltarClientMasterFaultLocalization;
import fr.inria.astor.core.faultlocalization.gzoltar.TestCaseResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.validation.results.TestCasesProgramValidationResult;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */

public class GZoltarFaultLocalizationTest {

	@Test
	@Ignore
	public void testNewGzoltarMath70() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = commandMath70(dep, out, 50, 0.5, false);
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		ProgramVariant pv = main1.getEngine().getVariants().get(0);

		assertTrue(pv.getModificationPoints().size() > 0);

		boolean hasUniv = false;
		boolean hasBisection = false;
		for (ModificationPoint mp : pv.getModificationPoints()) {
			if ("UnivariateRealSolverImpl".equals(mp.getCtClass().getSimpleName()))
				hasUniv = true;
			;
			if ("BisectionSolver".equals(mp.getCtClass().getSimpleName()))
				hasBisection = true;
		}
		assertTrue(hasBisection);
		assertTrue(hasUniv);

		assertTrue(((TestCasesProgramValidationResult) pv.getValidationResult()).getCasesExecuted() > 1000);

	}

	@Test

	public void testCoverageMath70() throws Exception {

		AstorMain main1 = new AstorMain();
		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-maxgen", "0");
		main1.execute(cs.flat());
		ProgramVariant pv = main1.getEngine().getVariants().get(0);

		assertTrue(pv.getModificationPoints().size() > 0);

		for (ModificationPoint mp : pv.getModificationPoints()) {
			SuspiciousModificationPoint smp = (SuspiciousModificationPoint) mp;
			List<TestCaseResult> coverage = smp.getSuspicious().getCoveredByTests();
			assertTrue(coverage.size() > 0);
			Optional<TestCaseResult> failing = coverage.stream().filter(e -> !e.isCorrect()).findAny();
			assertTrue(failing.isPresent());
			assertEquals("org.apache.commons.math.analysis.solvers.BisectionSolverTest#testMath369",
					failing.get().getTestCaseCompleteName());
		}

	}

	@Test
	@Ignore
	public void testNewGzoltarMath70IncludeSuper() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = commandMath70(dep, out, 0, 0.1, false);
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		ProgramVariant pv = main1.getEngine().getVariants().get(0);
		int v1 = pv.getModificationPoints().size();
		System.out.println("suspicious v1 " + v1);

		//
		args = commandMath70(dep, out, 0, 0.1, true);
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		pv = main1.getEngine().getVariants().get(0);
		int v2 = pv.getModificationPoints().size();
		System.out.println("suspicious v2 " + v2);

		assertTrue(v2 > v1);

		boolean findsuper = false;
		for (ModificationPoint mp : main1.getEngine().getVariants().get(0).getModificationPoints()) {
			if (mp.getCodeElement().toString().startsWith("super"))
				findsuper = true;
		}
		assertTrue(findsuper);

	}

	private String[] commandMath70(String dep, File out, int generation, Double tr, boolean mansuper) {
		String[] args = new String[] { "-dependencies", dep, "-mode", "jgenprog", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", Double.toString(tr), "-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "10", "-maxgen", Integer.toString(generation),
				"-stopfirst", "true", "-maxtime", "100", "-faultlocalization",
				GZoltarClientMasterFaultLocalization.class.getCanonicalName(), "-jvm4testexecution",
				"/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/bin/", "-loglevel", "DEBUG", //
				"-population", "1", "-tmax2", "1920000", "-regressiontestcases4fl",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest:org.apache.commons.math.analysis.solvers.UnivariateRealSolverFactoryImplTest",
				(mansuper) ? "-manipulatesuper" : "", };
		return args;
	}

	public String[] commandLang1(File out, boolean step) {
		String libsdir = new File("./examples/libs/lang_common_lib").getAbsolutePath();
		String dep = libsdir + File.separator + "cglib.jar" + File.pathSeparator //
				+ libsdir + File.separator + "commons-io.jar" + File.pathSeparator //
				+ File.separator + libsdir + File.separator + "asm.jar" + File.pathSeparator //
				+ File.separator + libsdir + File.separator + "easymock.jar";//
		String[] args = new String[] {
				///
				"-dependencies", dep, "-mode", "jgenprog", // "-failing",
															// "org.apache.commons.lang3.math.NumberUtilsTest",
															// //
				"-location", new File("./examples/lang_1/").getAbsolutePath(),
				//
				"-package", "org.apache.commons",
				//
				"-srcjavafolder", "/src/main/java/",
				//
				"-srctestfolder", "/src/main/test/",
				//
				"-binjavafolder", "/target/classes/",
				//
				"-bintestfolder", "/target/test-classes/",
				//
				"-javacompliancelevel", "6",
				//
				"-flthreshold", "0.1",
				//
				"-population", "1",
				//
				"-out", out.getAbsolutePath(), "-scope", "package", "-seed", "6320", "-maxgen", "50",
				//
				"-stopfirst", "true", "-maxtime", "5", (step) ? "-testbystep" : "",
				//
				"-loglevel", "DEBUG",

				//
				"-faultlocalization", GZoltarClientMasterFaultLocalization.class.getCanonicalName(),

				//
				"-regressiontestcases4fl",
				"org.apache.commons.lang3.BooleanUtilsTest:org.apache.commons.lang3.math.NumberUtilsTest:org.apache.commons.lang3.reflect.ConstructorUtilsTest:org.apache.commons.lang3.reflect.MethodUtilsTest",

		};
		return args;
	}

	@Test
	@Ignore
	public void testLang1FLNewGZoltar() throws Exception {
		AstorMain main1 = new AstorMain();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		boolean stepbystep = false;
		String[] args = commandLang1(out, stepbystep);
		System.out.println(Arrays.toString(args));
		main1.execute(args);
		Assert.assertFalse(main1.getEngine().getMutatorSupporter().getFactory().Type().getAll().isEmpty());

	}

	@Test
	@Ignore
	public void testParseLine() {
		GZoltarClientMasterFaultLocalization fl = new GZoltarClientMasterFaultLocalization();
		String line = "org.apache.commons.lang3[StringUtils.java<org.apache.commons.lang3.StringUtils{splitWorker"
				+ "(Ljava/lang/String;Ljava/lang/String;IZ)[Ljava/lang/String;#2973,0.00000000000000000000000000000000";
		SuspiciousCode sc = fl.parseLine(line);
		System.out.println(sc);
		Assert.assertEquals("org.apache.commons.lang3.StringUtils", sc.getClassName());

	}

	@Test
	@Ignore
	public void testParseLine2() {
		GZoltarClientMasterFaultLocalization fl = new GZoltarClientMasterFaultLocalization();
		String line = "org.apache.commons.lang3.math[NumberUtils.java<org.apache.commons.lang3.math.NumberUtils{<clinit>()"
				+ "V#34,0.10660035817780521000000000000000";
		SuspiciousCode sc = fl.parseLine(line);
		System.out.println(sc);
		Assert.assertEquals("org.apache.commons.lang3.math.NumberUtils", sc.getClassName());

	}

	@Test
	@Ignore
	public void testFLCM() throws Exception {
		// command.add("-Dproject_cp=/private/tmp/Lang-1b/target/classes:/private/tmp/Lang-1b/target/tests:/Users/matias/develop/defects4j/framework/projects/lib/junit-4.11.jar:/Users/matias/develop/defects4j/framework/projects/Lang/lib/easymock.jar:/Users/matias/develop/defects4j/framework/projects/Lang/lib/commons-io.jar:/Users/matias/develop/defects4j/framework/projects/Lang/lib/cglib.jar:/Users/matias/develop/defects4j/framework/projects/Lang/lib/asm.jar");
		// command.add("-Dtargetclasses=org.apache.commons.lang3.StringUtils:org.apache.commons.lang3.math.NumberUtils:");
		// command.add("-Dtestclasses=org.apache.commons.lang3.BooleanUtilsTest:org.apache.commons.lang3.math.NumberUtilsTest:org.apache.commons.lang3.reflect.ConstructorUtilsTest:org.apache.commons.lang3.reflect.MethodUtilsTest:");

		AstorMain main1 = new AstorMain();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		boolean stepbystep = false;
		String[] args = commandLang1(out, stepbystep);
		System.out.println(Arrays.toString(args));
		main1.execute(args);

		Set<String> classPath = new HashSet<>();
		for (URL dep : main1.getEngine().getProjectFacade().getProperties().getDependencies()) {
			classPath.add(dep.getPath());
		}

		GZoltarClientMasterFaultLocalization fl = new GZoltarClientMasterFaultLocalization();

		FaultLocalizationResult flr = fl.searchSuspicious(
				main1.getEngine().getProjectFacade().getOutDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT),
				//
				Arrays.asList(
						"org.apache.commons.lang3.BooleanUtilsTest:org.apache.commons.lang3.math.NumberUtilsTest:org.apache.commons.lang3.reflect.ConstructorUtilsTest:org.apache.commons.lang3.reflect.MethodUtilsTest"),
				null, classPath, null

		);

		List<SuspiciousCode> susp = flr.getCandidates();
		assertTrue(susp.size() > 0);
		assertTrue(susp.get(0).getSuspiciousValue() > 0);

	}

	@Test
	@Ignore
	public void testNewGzoltarMath70TestToInstrument() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = commandMath70(dep, out, 50, 0.5, false);
		int initialSize = args.length;
		args = Arrays.copyOf(args, args.length + 2);
		args[initialSize] = "-classestoinstrument";
		args[initialSize + 1] = "org.apache.commons.math.analysis.solvers.UnivariateRealSolverImpl";

		System.out.println(Arrays.toString(args));
		main1.execute(args);

		ProgramVariant pv = main1.getEngine().getVariants().get(0);

		assertTrue(pv.getModificationPoints().size() > 0);

		for (ModificationPoint mp : pv.getModificationPoints()) {
			assertEquals("UnivariateRealSolverImpl", mp.getCtClass().getSimpleName());
		}
	}

}
