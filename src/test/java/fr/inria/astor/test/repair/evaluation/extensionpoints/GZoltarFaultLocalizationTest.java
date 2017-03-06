package fr.inria.astor.test.repair.evaluation.extensionpoints;

import static org.junit.Assert.*;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.faultlocalization.FaultLocalizationResult;
import fr.inria.astor.core.faultlocalization.GZoltarClientMasterFaultLocalization;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.evolution.AstorMain;
/**
 * 
 * @author Matias Martinez
 *
 */
public class GZoltarFaultLocalizationTest {

	@Before
	public void setUp() throws Exception {
	}

	@Test
	public void testNewGzoltarMath70() throws Exception {

		AstorMain main1 = new AstorMain();
		String dep = new File("./examples/libs/junit-4.4.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "10", "-maxgen", "50", "-stopfirst", "true",
				"-maxtime", "100", "-faultlocalization", GZoltarClientMasterFaultLocalization.class.getCanonicalName(),
				"-jvm4testexecution", "/Library/Java/JavaVirtualMachines/jdk1.7.0_79.jdk/Contents/Home/bin/",
				"-loglevel", "DEBUG" };
		System.out.println(Arrays.toString(args));
		main1.execute(args);

	}

	public String[] commandLang1(File out, boolean step) {
		String libsdir = new File("./examples/libs/lang_common_lib").getAbsolutePath();
		String dep = libsdir + File.separator + "cglib.jar" + File.pathSeparator //
				+ libsdir + File.separator + "commons-io.jar" + File.pathSeparator //
				+ File.separator + libsdir + File.separator + "asm.jar" + File.pathSeparator //
				+ File.separator + libsdir + File.separator + "easymock.jar";//
		String[] args = new String[] {
				///
				"-dependencies", dep, "-mode", "statement", // "-failing",
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
				"-regressiontestcases",
				"org.apache.commons.lang3.BooleanUtilsTest:org.apache.commons.lang3.math.NumberUtilsTest:org.apache.commons.lang3.reflect.ConstructorUtilsTest:org.apache.commons.lang3.reflect.MethodUtilsTest",

		};
		return args;
	}

	@Test
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
	public void testParseLine() {
		GZoltarClientMasterFaultLocalization fl = new GZoltarClientMasterFaultLocalization();
		String line = "org.apache.commons.lang3[StringUtils.java<org.apache.commons.lang3.StringUtils{splitWorker"
				+ "(Ljava/lang/String;Ljava/lang/String;IZ)[Ljava/lang/String;#2973,0.00000000000000000000000000000000";
		SuspiciousCode sc = fl.parseLine(line);
		System.out.println(sc);
		Assert.assertEquals("org.apache.commons.lang3.StringUtils", sc.getClassName());

	}

	@Test
	public void testParseLine2() {
		GZoltarClientMasterFaultLocalization fl = new GZoltarClientMasterFaultLocalization();
		String line = "org.apache.commons.lang3.math[NumberUtils.java<org.apache.commons.lang3.math.NumberUtils{<clinit>()"
				+ "V#34,0.10660035817780521000000000000000";
		SuspiciousCode sc = fl.parseLine(line);
		System.out.println(sc);
		Assert.assertEquals("org.apache.commons.lang3.math.NumberUtils", sc.getClassName());

	}

	@Test
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
				Arrays.asList("org.apache.commons.lang3.BooleanUtilsTest:org.apache.commons.lang3.math.NumberUtilsTest:org.apache.commons.lang3.reflect.ConstructorUtilsTest:org.apache.commons.lang3.reflect.MethodUtilsTest"),
				null, classPath, null

		);

		List<SuspiciousCode> susp = flr.getCandidates();
		assertTrue(susp.size() > 0);
		assertTrue(susp.get(0).getSuspiciousValue() > 0);

	}

}
