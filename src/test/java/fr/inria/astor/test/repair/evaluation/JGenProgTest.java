package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtStatement;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import fr.inria.astor.core.entities.Gen;
import fr.inria.astor.core.entities.GenOperationInstance;
import fr.inria.astor.core.entities.GenSuspicious;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.taxonomy.GenProgMutationOperation;
import fr.inria.astor.core.loop.evolutionary.JGenProg;
import fr.inria.astor.core.util.ProcessUtil;
import fr.inria.astor.core.util.TimeUtil;
import fr.inria.main.AbstractMain;
import fr.inria.main.ExecutionMode;
import fr.inria.main.evolution.AstorMain;

/**
 * This class executes the experiment from our paper
 * if-conditional-dataset-2014.
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class JGenProgTest extends BaseEvolutionaryTest {

	@Override
	public void generic(String location, String folder, String regression, String failing, String dependenciespath,
			String packageToInstrument, double thfl) throws Exception {

		getMain().run(location, folder, dependenciespath, packageToInstrument, thfl, failing);

	}

	@Override
	public AbstractMain createMain() {
		if (main == null) {
			return new AstorMain();
		}
		return main;
	}

	@Test
	public void testExampleMath280() throws Exception {

		String dependenciespath = "examples/Math-issue-280/lib/junit-4.4.jar";
		String folder = "Math-issue-280";
		String failing = "org.apache.commons.math.distribution.NormalDistributionTest";
		File f = new File("examples/math_85//");
		String location = f.getParent();
		String regression = "org.apache.commons.math.distribution.NormalDistributionTest";
		String packageToInstrument = "org.apache.commons";
		double thfl = 0.5;

		this.generic(location, folder, regression, failing, dependenciespath, packageToInstrument, thfl);

	}

	@Test
	public void testExample280CommandLine() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = "/home/matias/.m2/repository/junit/junit/4.4/junit-4.4.jar";

		String[] args = new String[] { "-bug280", "-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin", };
		System.out.println(Arrays.toString(args));
		main1.main(args);

	}

	@Test
	public void testExampleMath0C1() throws Exception {

		String dependenciespath = "examples/Math-0c1ef/lib/junit-4.11.jar" + File.pathSeparator
				+ "examples/Math-0c1ef/lib/hamcrest-core-1.3.jar";
		String folder = "Math-0c1ef";
		String failing = "org.apache.commons.math3.primes.PrimesTest";
		File f = new File("examples/Math-0c1ef/");
		String location = f.getAbsolutePath();
		String packageToInstrument = "org.apache.commons";
		double thfl = 0.5;

		this.generic(location, folder, "", failing, dependenciespath, packageToInstrument, thfl);

	}

	@Test
	public void testPatchMath0C1() throws Exception {
		// Recompile the example project before executing it.
		String dependenciespath = "examples/Math-0c1ef/lib/junit-4.11.jar" + File.pathSeparator
				+ "examples/Math-0c1ef/lib/hamcrest-core-1.3.jar";
		String folder = "Math-0c1ef";
		String failing = "org.apache.commons.math3.primes.PrimesTest";
		File f = new File("examples/Math-0c1ef/");
		String location = f.getAbsolutePath();// f.getParent();
		String packageToInstrument = "org.apache.commons";
		double thfl = 0.5;

		int processBeforeAll = ProcessUtil.currentNumberProcess();

		AstorMain main = new AstorMain();

		main.initProject(location, folder, dependenciespath, packageToInstrument, thfl, failing);

		JGenProg jgp = main.createEngine(ExecutionMode.JGenProg);

		Assert.assertEquals(1, jgp.getVariants().size());

		ProgramVariant variant = jgp.getVariants().get(0);
		//
		int currentGeneration = 1;
		GenOperationInstance operation1 = createDummyOperation1(variant, currentGeneration);
		System.out.println("operation " + operation1);
		assertNotNull(operation1);

		boolean isSolution = false;
		isSolution = jgp.processCreatedVariant(variant, currentGeneration);
		// The model has not been changed.
		assertFalse(isSolution);

		int afterFirstValidation = ProcessUtil.currentNumberProcess();
		//
		jgp.applyNewOperationsToVariantModel(variant, currentGeneration);

		//
		isSolution = jgp.processCreatedVariant(variant, currentGeneration);

		int afterPatchValidation = ProcessUtil.currentNumberProcess();

		assertTrue(isSolution);

		System.out.println("\nSolutions:\n" + jgp.getSolutionData(jgp.getVariants(), 1));

		jgp.prepareNextGeneration(jgp.getVariants(), 1);

		assertNotNull(jgp.getSolutions());

		assertFalse(jgp.getSolutions().isEmpty());

		assertEquals("Problems with process", processBeforeAll, afterFirstValidation);

		assertEquals("Problems with process", processBeforeAll, afterPatchValidation);

	}

	@Test
	public void testPatchMath0C1TwoFailing() throws Exception {
		// Recompile the example project before executing it.
		String dependenciespath = "examples/Math-0c1ef/lib/junit-4.11.jar" + File.pathSeparator
				+ "examples/Math-0c1ef/lib/hamcrest-core-1.3.jar";
		String folder = "Math-0c1ef";
		// Only the first one fails
		String failing = "org.apache.commons.math3.primes.PrimesTest" + File.pathSeparator
				+ "org.apache.commons.math3.random.BitsStreamGeneratorTest";

		File f = new File("examples/Math-0c1ef/");
		String location = f.getAbsolutePath();
		String packageToInstrument = "org.apache.commons";
		double thfl = 0.5;

		int processBeforeAll = ProcessUtil.currentNumberProcess();

		AstorMain main = new AstorMain();

		main.initProject(location, folder, dependenciespath, packageToInstrument, thfl, failing);

		JGenProg jgp = main.createEngine(ExecutionMode.JGenProg);

		Assert.assertEquals(1, jgp.getVariants().size());

		ProgramVariant variant = jgp.getVariants().get(0);
		//
		int currentGeneration = 1;
		GenOperationInstance operation1 = createDummyOperation1(variant, currentGeneration);
		System.out.println("operation " + operation1);
		assertNotNull(operation1);

		boolean isSolution = false;
		//
		jgp.applyNewOperationsToVariantModel(variant, currentGeneration);
		//
		isSolution = jgp.processCreatedVariant(variant, currentGeneration);
		assertTrue(isSolution);

	}

	public GenSuspicious searchSuspiciousElement(ProgramVariant variant, String snippet, String fileName, int line) {

		for (Gen gen : variant.getGenList()) {

			if (gen.getCodeElement().toString().equals(snippet) && gen.getCodeElement().getPosition().getLine() == line)
				return (GenSuspicious) gen;
		}

		return null;
	}

	public CtStatement createPatchStatementCode(String snippet) {

		Factory factory = new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());
		CtStatement st = (factory).Code().createCodeSnippetStatement(snippet).compile();
		return st;
	}

	private GenOperationInstance createDummyOperation1(ProgramVariant variant, int currentGeneration) {

		GenSuspicious genSusp = searchSuspiciousElement(variant, "n += 3", " ", 93);// TODO:
																					// is
																					// 93
																					// or
																					// 95
		assertNotNull(genSusp);

		CtElement targetStmt = genSusp.getCodeElement();
		CtElement fix = createFix1();
		assertEquals(fix.toString(), "n += 2");

		GenOperationInstance operation = new GenOperationInstance();

		operation.setOperationApplied(GenProgMutationOperation.REPLACE);
		operation.setGen(genSusp);
		operation.setParentBlock((CtBlock) targetStmt.getParent());
		operation.setOriginal(targetStmt);
		operation.setModified(fix);

		variant.putGenOperation(currentGeneration, operation);
		operation.setGen(genSusp);

		return operation;
	}

	@Test
	public void testCreateFix1() {
		assertEquals(createFix1().toString(), "n += 2");
		// return fix;
	}

	public CtElement createFix1() {
		CtElement gen = createPatchStatementCode("int n=0; n += 2");
		CtElement fix = ((CtBlock) gen.getParent()).getStatement(1);
		// assertEquals(fix,"n += 2" );
		return fix;
	}

	@Test
	public void testHelpMain() throws Exception {
		AstorMain main1 = new AstorMain();
		main1.main(new String[] { "help" });
	}

	@SuppressWarnings("static-access")
	@Test
	public void testRunMain() throws Exception {
		AstorMain main1 = new AstorMain();
		String pathExample = new File("./examples/Math-0c1ef").getAbsolutePath();
		String[] args = new String[] {
				"-dependencies",
				"examples/Math-0c1ef/lib/junit-4.11.jar" + File.pathSeparator + pathExample + File.separator
						+ "/lib/hamcrest-core-1.3.jar", "-failing", "org.apache.commons.math3.primes.PrimesTest",
				"-location", pathExample, "-package", "org.apache.commons", "-jvm4testexecution",
				"/home/matias/develop/jdk1.7.0_71/bin" };
		System.out.println(Arrays.toString(args));
		main1.main(args);

	}

	@SuppressWarnings("static-access")
	@Test
	public void testRunMain2() throws Exception {
		AstorMain main1 = new AstorMain();
		String pathExample = new File("./examples/Math-0c1ef").getAbsolutePath();

		main1.main(new String[] {
				"-dependencies",
				"examples/Math-0c1ef/lib/junit-4.11.jar" + File.pathSeparator + pathExample + File.separator
						+ "/lib/hamcrest-core-1.3.jar", "-id", "tttMath-0c1ef", "-failing",
				"org.apache.commons.math3.primes.PrimesTest", "-location", pathExample, "-package",
				"org.apache.commons", "-maxgen", "400", "-population", "2", "-saveall",

		});

	}

	@SuppressWarnings("static-access")
	@Test
	public void testRunMainRemove() throws Exception {
		AstorMain main1 = new AstorMain();
		String pathExample = new File("./examples/Math-0c1ef").getAbsolutePath();
		main1.main(new String[] {
				"-dependencies",
				"examples/Math-0c1ef/lib/junit-4.11.jar" + File.pathSeparator + pathExample + File.separator
						+ "lib/hamcrest-core-1.3.jar", "-id", "tttMath-0c1ef", "-failing",
				"org.apache.commons.math3.primes.PrimesTest", "-location", pathExample, "-package",
				"org.apache.commons", "-maxgen", "400", "-population", "2", "-saveall", "-mode", "statement-remove"

		});

	}

	@SuppressWarnings("static-access")
	@Test
	public void testRunMainTime() throws Exception {

		Date init = new Date();
		AstorMain main1 = new AstorMain();
		String pathExample = new File("./examples/Math-0c1ef").getAbsolutePath();
		main1.main(new String[] {
				"-dependencies",
				"examples/Math-0c1ef/lib/junit-4.11.jar" + File.pathSeparator + pathExample + File.separator
						+ "lib/hamcrest-core-1.3.jar", "-id", "tttMath-0c1ef", "-failing",
				"org.apache.commons.math3.primes.PrimesTest", "-location", pathExample, "-package",
				"org.apache.commons", "-maxgen", "400", "-population", "2", "-saveall", "-maxtime", "1" });
		long t = TimeUtil.deltaInMinutes(init);
		assertTrue(t > 1);// more than one minute
		assertFalse(t < 2);// less than two minutes
	}

	@SuppressWarnings("static-access")
	@Test
	public void testRunMainSrcTestFolder() throws Exception {
		AstorMain main1 = new AstorMain();
		String pathExample = new File("./examples/Math-0c1ef").getAbsolutePath();
		main1.main(new String[] {
				"-dependencies",
				"examples/Math-0c1ef/lib/junit-4.11.jar" + File.pathSeparator + pathExample + File.separator
						+ "lib/hamcrest-core-1.3.jar", "-failing", "org.apache.commons.math3.primes.PrimesTest",
				"-location", pathExample, "-package", "org.apache.commons", "-jvm4testexecution",
				"/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder", "/src/main/java/", "-srctestfolder",
				"/src/test/java/", "-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes" });

	}

	/**
	 * Astor finds the patch in remove mode, with susp = 0.5 (with only failing
	 * for FL) 29 gens. The last two fixes
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	@Test
	public void testMath2ExampleRemoveMode() throws Exception {
		AstorMain main1 = new AstorMain();

		String[] args = (new String[] { "-dependencies", "examples/math_2/libmvn/", "-mode", "statement-remove",
				"-failing", "org.apache.commons.math3.distribution.HypergeometricDistributionTest", "-location",
				"examples/math_2/", "-package", "org.apache.commons", "-jvm4testexecution",
				"/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder", "/src/main/java/", "-srctestfolder",
				"/src/test/java/", "-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes",

		});
		System.out.println("Arguments:\n " + Arrays.toString(args).replace(',', ' '));
		main1.main(args);

	}

	@SuppressWarnings("static-access")
	@Test
	public void testRunMainLang55() throws Exception {
		AstorMain main1 = new AstorMain();
		String pathExample = new File("./examples/lang_55").getAbsolutePath();
		String[] args = new String[] { "-dependencies", pathExample + File.separator + "junit-3.8.1.jar", "-failing",
				"org.apache.commons.lang.time.StopWatchTest", "-location", pathExample, "-package",
				"org.apache.commons", "-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-alternativecompliancelevel", "4",
				"-genlistnavigation", "inorder" };
		System.out.println(Arrays.toString(args));
		main1.main(args);

	}

	@SuppressWarnings("static-access")
	@Test
	public void testRunMainLang55FormDefects4j() throws Exception {
		AstorMain main1 = new AstorMain();
		String pathExample = new File("./examples/lang_55").getAbsolutePath();
		String[] args = new String[] { "-dependencies",
				pathExample + File.separator + "lib" + File.separator + "junit-3.8.1.jar", "-failing",
				"org.apache.commons.lang.time.StopWatchTest", "-location", pathExample, "-package",
				"org.apache.commons", "-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/",
				// compiled using Defect4J
				"-binjavafolder", "/target/classes", "-bintestfolder", "/target/tests", "-javacompliancelevel", "7",
				"-alternativecompliancelevel", "4", "-genlistnavigation", "inorder" };
		System.out.println(Arrays.toString(args));
		main1.main(args);

	}

	@SuppressWarnings("static-access")
	@Test
	public void testRunMainLang55Remove() throws Exception {
		AstorMain main1 = new AstorMain();
		String pathExample = new File("./examples/lang_55").getAbsolutePath().replace(".", "");
		String[] args = new String[] {
				"-dependencies",
				pathExample + File.separator + "lib" + File.separator + "junit-3.8.1.jar",
				"-failing",
				"org.apache.commons.lang.ArrayUtilsAddTest:org.apache.commons.lang.ArrayUtilsRemoveTest:org.apache.commons.lang.ArrayUtilsTest:org.apache.commons.lang.BitFieldTest:org.apache.commons.lang.BooleanUtilsTest:org.apache.commons.lang.CharEncodingTest:org.apache.commons.lang.CharRangeTest:org.apache.commons.lang.CharSetTest:org.apache.commons.lang.CharSetUtilsTest:org.apache.commons.lang.CharUtilsTest:org.apache.commons.lang.ClassUtilsTest:org.apache.commons.lang.EntitiesPerformanceTest:org.apache.commons.lang.EntitiesTest:org.apache.commons.lang.IllegalClassExceptionTest:org.apache.commons.lang.IncompleteArgumentExceptionTest:org.apache.commons.lang.IntHashMapTest:org.apache.commons.lang.LocaleUtilsTest:org.apache.commons.lang.NotImplementedExceptionTest:org.apache.commons.lang.NullArgumentExceptionTest:org.apache.commons.lang.NumberRangeTest:org.apache.commons.lang.NumberUtilsTest:org.apache.commons.lang.ObjectUtilsTest:org.apache.commons.lang.RandomStringUtilsTest:org.apache.commons.lang.SerializationUtilsTest:org.apache.commons.lang.StringEscapeUtilsTest:org.apache.commons.lang.StringUtilsEqualsIndexOfTest:org.apache.commons.lang.StringUtilsIsTest:org.apache.commons.lang.StringUtilsSubstringTest:org.apache.commons.lang.StringUtilsTest:org.apache.commons.lang.StringUtilsTrimEmptyTest:org.apache.commons.lang.SystemUtilsTest:org.apache.commons.lang.UnhandledExceptionTest:org.apache.commons.lang.ValidateTest:org.apache.commons.lang.WordUtilsTest:org.apache.commons.lang.builder.CompareToBuilderTest:org.apache.commons.lang.builder.DefaultToStringStyleTest:org.apache.commons.lang.builder.EqualsBuilderTest:org.apache.commons.lang.builder.HashCodeBuilderAndEqualsBuilderTest:org.apache.commons.lang.builder.HashCodeBuilderTest:org.apache.commons.lang.builder.MultiLineToStringStyleTest:org.apache.commons.lang.builder.NoFieldNamesToStringStyleTest:org.apache.commons.lang.builder.ReflectionToStringBuilderExcludeTest:org.apache.commons.lang.builder.ShortPrefixToStringStyleTest:org.apache.commons.lang.builder.SimpleToStringStyleTest:org.apache.commons.lang.builder.StandardToStringStyleTest:org.apache.commons.lang.builder.ToStringBuilderTest:org.apache.commons.lang.builder.ToStringStyleTest:org.apache.commons.lang.enum.EnumTest:org.apache.commons.lang.enum.EnumUtilsTest:org.apache.commons.lang.enum.ValuedEnumTest:org.apache.commons.lang.enums.EnumEqualsTest:org.apache.commons.lang.enums.EnumTest:org.apache.commons.lang.enums.EnumUtilsTest:org.apache.commons.lang.enums.ValuedEnumTest:org.apache.commons.lang.exception.ExceptionUtilsTestCase:org.apache.commons.lang.exception.NestableDelegateTestCase:org.apache.commons.lang.exception.NestableErrorTestCase:org.apache.commons.lang.exception.NestableExceptionTestCase:org.apache.commons.lang.exception.NestableRuntimeExceptionTestCase:org.apache.commons.lang.math.DoubleRangeTest:org.apache.commons.lang.math.FloatRangeTest:org.apache.commons.lang.math.FractionTest:org.apache.commons.lang.math.IntRangeTest:org.apache.commons.lang.math.LongRangeTest:org.apache.commons.lang.math.NumberRangeTest:org.apache.commons.lang.math.NumberUtilsTest:org.apache.commons.lang.math.RandomUtilsTest:org.apache.commons.lang.math.RangeTest:org.apache.commons.lang.mutable.MutableBooleanTest:org.apache.commons.lang.mutable.MutableByteTest:org.apache.commons.lang.mutable.MutableDoubleTest:org.apache.commons.lang.mutable.MutableFloatTest:org.apache.commons.lang.mutable.MutableIntTest:org.apache.commons.lang.mutable.MutableLongTest:org.apache.commons.lang.mutable.MutableObjectTest:org.apache.commons.lang.mutable.MutableShortTest:org.apache.commons.lang.text.CompositeFormatTest:org.apache.commons.lang.text.StrBuilderAppendInsertTest:org.apache.commons.lang.text.StrBuilderTest:org.apache.commons.lang.text.StrLookupTest:org.apache.commons.lang.text.StrMatcherTest:org.apache.commons.lang.text.StrSubstitutorTest:org.apache.commons.lang.text.StrTokenizerTest:org.apache.commons.lang.time.DateFormatUtilsTest:org.apache.commons.lang.time.DateUtilsTest:org.apache.commons.lang.time.DurationFormatUtilsTest:org.apache.commons.lang.time.FastDateFormatTest:org.apache.commons.lang.time.StopWatchTest",
				"-location", pathExample, "-package", "org.apache.commons", "-jvm4testexecution",
				"/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder", "/src/java/", "-srctestfolder", "/src/test/",
				"-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes", "-javacompliancelevel",
				"7", "-alternativecompliancelevel", "4", "-mode", "statement-remove", "-population", "1", "-saveall",
				"-flthreshold", "0.05",

		};
		System.out.println(Arrays.toString(args));
		main1.main(args);

	}

	@SuppressWarnings("static-access")
	@Test
	public void testRunMainLang55RemoveKali() throws Exception {
		AstorMain main1 = new AstorMain();
		String pathExample = new File("./examples/lang_55_kali").getAbsolutePath().replace(".", "");
		String[] args = new String[] {
				"-dependencies",
				pathExample + File.separator + "lib" + File.separator + "junit-3.8.1.jar",
				"-failing",
				"org.apache.commons.lang.ArrayUtilsAddTest:org.apache.commons.lang.ArrayUtilsRemoveTest:org.apache.commons.lang.ArrayUtilsTest:org.apache.commons.lang.BitFieldTest:org.apache.commons.lang.BooleanUtilsTest:org.apache.commons.lang.CharEncodingTest:org.apache.commons.lang.CharRangeTest:org.apache.commons.lang.CharSetTest:org.apache.commons.lang.CharSetUtilsTest:org.apache.commons.lang.CharUtilsTest:org.apache.commons.lang.ClassUtilsTest:org.apache.commons.lang.EntitiesPerformanceTest:org.apache.commons.lang.EntitiesTest:org.apache.commons.lang.IllegalClassExceptionTest:org.apache.commons.lang.IncompleteArgumentExceptionTest:org.apache.commons.lang.IntHashMapTest:org.apache.commons.lang.LocaleUtilsTest:org.apache.commons.lang.NotImplementedExceptionTest:org.apache.commons.lang.NullArgumentExceptionTest:org.apache.commons.lang.NumberRangeTest:org.apache.commons.lang.NumberUtilsTest:org.apache.commons.lang.ObjectUtilsTest:org.apache.commons.lang.RandomStringUtilsTest:org.apache.commons.lang.SerializationUtilsTest:org.apache.commons.lang.StringEscapeUtilsTest:org.apache.commons.lang.StringUtilsEqualsIndexOfTest:org.apache.commons.lang.StringUtilsIsTest:org.apache.commons.lang.StringUtilsSubstringTest:org.apache.commons.lang.StringUtilsTest:org.apache.commons.lang.StringUtilsTrimEmptyTest:org.apache.commons.lang.SystemUtilsTest:org.apache.commons.lang.UnhandledExceptionTest:org.apache.commons.lang.ValidateTest:org.apache.commons.lang.WordUtilsTest:org.apache.commons.lang.builder.CompareToBuilderTest:org.apache.commons.lang.builder.DefaultToStringStyleTest:org.apache.commons.lang.builder.EqualsBuilderTest:org.apache.commons.lang.builder.HashCodeBuilderAndEqualsBuilderTest:org.apache.commons.lang.builder.HashCodeBuilderTest:org.apache.commons.lang.builder.MultiLineToStringStyleTest:org.apache.commons.lang.builder.NoFieldNamesToStringStyleTest:org.apache.commons.lang.builder.ReflectionToStringBuilderExcludeTest:org.apache.commons.lang.builder.ShortPrefixToStringStyleTest:org.apache.commons.lang.builder.SimpleToStringStyleTest:org.apache.commons.lang.builder.StandardToStringStyleTest:org.apache.commons.lang.builder.ToStringBuilderTest:org.apache.commons.lang.builder.ToStringStyleTest:org.apache.commons.lang.enum.EnumTest:org.apache.commons.lang.enum.EnumUtilsTest:org.apache.commons.lang.enum.ValuedEnumTest:org.apache.commons.lang.enums.EnumEqualsTest:org.apache.commons.lang.enums.EnumTest:org.apache.commons.lang.enums.EnumUtilsTest:org.apache.commons.lang.enums.ValuedEnumTest:org.apache.commons.lang.exception.ExceptionUtilsTestCase:org.apache.commons.lang.exception.NestableDelegateTestCase:org.apache.commons.lang.exception.NestableErrorTestCase:org.apache.commons.lang.exception.NestableExceptionTestCase:org.apache.commons.lang.exception.NestableRuntimeExceptionTestCase:org.apache.commons.lang.math.DoubleRangeTest:org.apache.commons.lang.math.FloatRangeTest:org.apache.commons.lang.math.FractionTest:org.apache.commons.lang.math.IntRangeTest:org.apache.commons.lang.math.LongRangeTest:org.apache.commons.lang.math.NumberRangeTest:org.apache.commons.lang.math.NumberUtilsTest:org.apache.commons.lang.math.RandomUtilsTest:org.apache.commons.lang.math.RangeTest:org.apache.commons.lang.mutable.MutableBooleanTest:org.apache.commons.lang.mutable.MutableByteTest:org.apache.commons.lang.mutable.MutableDoubleTest:org.apache.commons.lang.mutable.MutableFloatTest:org.apache.commons.lang.mutable.MutableIntTest:org.apache.commons.lang.mutable.MutableLongTest:org.apache.commons.lang.mutable.MutableObjectTest:org.apache.commons.lang.mutable.MutableShortTest:org.apache.commons.lang.text.CompositeFormatTest:org.apache.commons.lang.text.StrBuilderAppendInsertTest:org.apache.commons.lang.text.StrBuilderTest:org.apache.commons.lang.text.StrLookupTest:org.apache.commons.lang.text.StrMatcherTest:org.apache.commons.lang.text.StrSubstitutorTest:org.apache.commons.lang.text.StrTokenizerTest:org.apache.commons.lang.time.DateFormatUtilsTest:org.apache.commons.lang.time.DateUtilsTest:org.apache.commons.lang.time.DurationFormatUtilsTest:org.apache.commons.lang.time.FastDateFormatTest:org.apache.commons.lang.time.StopWatchTest",
				"-location", pathExample, "-package", "org.apache.commons", "-jvm4testexecution",
				"/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder", "/src/java/", "-srctestfolder", "/src/test/",
				"-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes", "-javacompliancelevel",
				"7", "-alternativecompliancelevel", "4", "-mode", "statement-remove", "-population", "1", "-saveall",
				"-flthreshold", "0.05",

		};
		System.out.println(Arrays.toString(args));
		main1.main(args);

	}

	/**
	 * We find a repair for this bug (Replacement mehotd invocation)
	 * 
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	@Test
	public void testMath73ModeStatement() throws Exception {
		AstorMain main1 = new AstorMain();
		String pathExample = new File("./examples/math_73").getAbsolutePath().replace(".", "");
		String[] args = new String[] { "-dependencies",
				pathExample + File.separator + "lib" + File.separator + "junit-4.4.jar", "-mode", "statement",
				"-failing", "org.apache.commons.math.analysis.solvers.BrentSolverTest", "-location", pathExample,
				"-package", "org.apache.commons", "-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin",
				"-srcjavafolder", "/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", };
		System.out.println(Arrays.toString(args));
		main1.main(args);

	}

	@Test
	public void testLang2ModeStatement() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = "/home/matias/.m2/repository/junit/junit/4.11/junit-4.11.jar" + File.pathSeparator
				+ "/home/matias/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar"
				+ File.pathSeparator + "/home/matias/.m2/repository/commons-io/commons-io/2.4/commons-io-2.4.jar"
				+ File.pathSeparator + "/home/matias/.m2/repository/org/easymock/easymock/3.1/easymock-3.1.jar"
				+ File.pathSeparator + "/home/matias/.m2/repository/cglib/cglib-nodep/2.2.2/cglib-nodep-2.2.2.jar"
				+ File.pathSeparator + "/home/matias/.m2/repository/org/objenesis/objenesis/1.2/objenesis-1.2.jar";

		// java -cp
		// /home/tdurieux/astor/astor-0.0.2-SNAPSHOT-jar-with-dependencies.jar
		// fr.inria.main.evolution.AstorMain -mode statement-remove -location
		// . -dependencies lib/ -failing
		// org.apache.commons.lang3.LocaleUtilsTest: -package org.apache.commons
		// -jvm4testexecution /usr/lib/jvm/java-1.7.0-openjdk-amd64/bin/
		// -javacompliancelevel 5 -maxgen 1000000 -population 1 -srcjavafolder
		// src/main/java/ -srctestfolder src/test/java/ -binjavafolder
		// target/classes/ -bintestfolder target/tests/;
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.lang3.LocaleUtilsTest", "-location", "/tmp/lang_2/", "-package",
				"org.apache.commons", "-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "6" // -maxgen
																	// 1000000
																	// -population
																	// 1
		};
		System.out.println(Arrays.toString(args));
		main1.main(args);

	}

	@Test
	public void testMath92ModeStatement() throws Exception {
		AstorMain main1 = new AstorMain();
		String pathExample = new File("./examples/math_92").getAbsolutePath().replace(".", "");
		// java -cp
		// /home/tdurieux/astor/astor-0.0.2-SNAPSHOT-jar-with-dependencies.jar
		// fr.inria.main.evolution.AstorMain -mode statement-remove -location
		// . -dependencies lib/ -failing
		// org.apache.commons.lang3.LocaleUtilsTest: -package org.apache.commons
		// -jvm4testexecution /usr/lib/jvm/java-1.7.0-openjdk-amd64/bin/
		// -javacompliancelevel 5 -maxgen 1000000 -population 1 -srcjavafolder
		// src/main/java/ -srctestfolder src/test/java/ -binjavafolder
		// target/classes/ -bintestfolder target/tests/;
		String[] args = new String[] { "-dependencies",
				pathExample + File.separator + "lib" + File.separator + "junit-4.4.jar", "-mode", "statement",
				"-failing", "org.apache.commons.math.util.MathUtilsTest", "-location", pathExample, "-package",
				"org.apache.commons", "-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "6" };
		System.out.println(Arrays.toString(args));
		main1.main(args);

	}

	@Test
	public void testMath93ModeStatement() throws Exception {
		AstorMain main1 = new AstorMain();
		// String dep =
		// "/home/matias/.m2/repository/junit/junit/4.4/junit-4.4.jar";
		String pathExample = new File("./examples/math_92").getAbsolutePath().replace(".", "");

		// java -cp
		// /home/tdurieux/astor/astor-0.0.2-SNAPSHOT-jar-with-dependencies.jar
		// fr.inria.main.evolution.AstorMain -mode statement-remove -location
		// . -dependencies lib/ -failing
		// org.apache.commons.lang3.LocaleUtilsTest: -package org.apache.commons
		// -jvm4testexecution /usr/lib/jvm/java-1.7.0-openjdk-amd64/bin/
		// -javacompliancelevel 5 -maxgen 1000000 -population 1 -srcjavafolder
		// src/main/java/ -srctestfolder src/test/java/ -binjavafolder
		// target/classes/ -bintestfolder target/tests/;
		String[] args = new String[] { "-dependencies",
				pathExample + File.separator + "lib" + File.separator + "junit-4.4.jar", "-mode", "statement",
				"-failing", "org.apache.commons.math.util.MathUtilsTest", "-location", pathExample, "-package",
				"org.apache.commons", "-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes",
		// "-javacompliancelevel", "5"
		};
		System.out.println(Arrays.toString(args));
		main1.main(args);

	}

	@Test
	public void testMath1ModeStatement() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = "/home/matias/.m2/repository/junit/junit/4.11/junit-4.11.jar" + File.pathSeparator
				+ "/home/matias/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar";

		String pathExample = new File("./examples/math_92").getAbsolutePath().replace(".", "");

		// java -cp
		// /home/tdurieux/astor/astor-0.0.2-SNAPSHOT-jar-with-dependencies.jar
		// fr.inria.main.evolution.AstorMain -mode statement-remove -location
		// . -dependencies lib/ -failing
		// org.apache.commons.lang3.LocaleUtilsTest: -package org.apache.commons
		// -jvm4testexecution /usr/lib/jvm/java-1.7.0-openjdk-amd64/bin/
		// -javacompliancelevel 5 -maxgen 1000000 -population 1 -srcjavafolder
		// src/main/java/ -srctestfolder src/test/java/ -binjavafolder
		// target/classes/ -bintestfolder target/tests/;
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math3.fraction.BigFractionTest", "-location", "/tmp/math_1/", "-package",
				"org.apache.commons", "-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder",
				"/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes",
		// "-javacompliancelevel", "5"
		};
		System.out.println(Arrays.toString(args));
		main1.main(args);

	}

	@Test
	public void testMath20ModeStatement() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = "/home/matias/.m2/repository/junit/junit/4.10/junit-4.10.jar" + File.pathSeparator
				+ "/home/matias/.m2/repository/org/hamcrest/hamcrest-core/1.1/hamcrest-core-1.1.jar";

		// java -cp
		// /home/tdurieux/astor/astor-0.0.2-SNAPSHOT-jar-with-dependencies.jar
		// fr.inria.main.evolution.AstorMain -mode statement-remove -location
		// . -dependencies lib/ -failing
		// org.apache.commons.lang3.LocaleUtilsTest: -package org.apache.commons
		// -jvm4testexecution /usr/lib/jvm/java-1.7.0-openjdk-amd64/bin/
		// -javacompliancelevel 5 -maxgen 1000000 -population 1 -srcjavafolder
		// src/main/java/ -srctestfolder src/test/java/ -binjavafolder
		// target/classes/ -bintestfolder target/tests/;
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math3.optimization.direct.CMAESOptimizerTest", "-location", "/tmp/math_20/",
				"-package", "org.apache.commons", "-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin",
				"-srcjavafolder", "/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "7" };
		System.out.println(Arrays.toString(args));
		main1.main(args);

	}

	/**
	 * The fix is the addition of an Assignment: signChangeIndex = 2
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMath40ModeStatement() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = "/home/matias/.m2/repository/junit/junit/4.8.2/junit-4.8.2.jar";
		File out = new File("./outputMutation/");
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BracketingNthOrderBrentSolverTest", "-location",
				new File("./examples/math_40/").getAbsolutePath(), "-package", "org.apache.commons",
				"-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder", "/src/main/java/",
				"-srctestfolder", "/src/test/java/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-seed", "10", "-out", out.getAbsolutePath(), };
		System.out.println(Arrays.toString(args));
		main1.main(args);
		validatePatchExistence(out.getPath() + File.separator + "AstorMain-math_40", 1);
	}

	@Test
	public void testMath64ModeStatement() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = "/home/matias/.m2/repository/junit/junit/4.4/junit-4.4.jar";
		File out = new File("./outputMutation/");
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.optimization.general.MinpackTest", "-location",
				new File("./examples/math_64/").getAbsolutePath(), "-package", "org.apache.commons",
				"-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder", "/src/main/java/",
				"-srctestfolder", "/src/test/java/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-seed", "10", "-maxgen", "200", "-out",
				out.getAbsolutePath(), };
		System.out.println(Arrays.toString(args));
		main1.main(args);
		validatePatchExistence(out.getPath() + File.separator + "AstorMain-math_64", 1);
	}

	/**
	 * The fix is a replacement of an return statement
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMath85issue280() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = "/home/matias/.m2/repository/junit/junit/4.4/junit-4.4.jar";

		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.distribution.NormalDistributionTest", "-location",
				new File("./examples/math_85").getAbsolutePath(), "-package", "org.apache.commons",
				"-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder", "/src/java/",
				"-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-stopfirst", "false",
				"-maxgen", "100", "-scope", "package" };
		System.out.println(Arrays.toString(args));
		main1.main(args);

	}

	/**
	 * Math 70 bug can be fixed by replacing a method invocation. + return
	 * solve(f, min, max); - return solve(min, max);
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMath70() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = "/home/matias/.m2/repository/junit/junit/4.4/junit-4.4.jar";
		File out = new File("./outputMutation/");
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package", "org.apache.commons",
				"-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder", "/src/java/",
				"-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "package",// "local",//"package"
				"-seed", "10", "-stopfirst", "false", "-maxgen", "100", };
		System.out.println(Arrays.toString(args));
		main1.main(args);

		validatePatchExistence(out + File.separator + "AstorMain-math_70/");

	}

	/**
	 * One solution with local, other with package
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMath70TwoSolutions() throws Exception {
		System.out.println(System.getProperty("java.class.path"));
		AstorMain main1 = new AstorMain();
		String dep = "/home/matias/.m2/repository/junit/junit/4.4/junit-4.4.jar";
		File out = new File("./outputMutation/");
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", "-location",
				new File("./examples/math_70").getAbsolutePath(), "-package",
				"org.apache.commons",
				"-jvm4testexecution",
				"/usr/lib/jvm/java-7-openjdk-amd64/bin/", // "/home/matias/develop/jdk1.7.0_71/bin",
				"-srcjavafolder", "/src/java/", "-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes",
				"-bintestfolder", "/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "package",
				// "-maxsuspcandidates","2",
				"-seed", "10",
				// "-stopfirst","false",
				"-maxgen", "200", };
		System.out.println(Arrays.toString(args));
		main1.main(args);

		validatePatchExistence(out + File.separator + "AstorMain-math_70/", 2);

	}
	
	@Test
	public void testAll() throws Exception{
		
		execM2();
		
		execM5();
		
		execM8();
				
		execM24();
		
		execM40();
		
		execM49();
		
		execM50();
		
	//	execM53();
			
		execM70();
			
		execM71();
		
		execM78();
		execM80();
			
	//	execM81();
		
		
						
		execM82();
		
		
		execM84();
		
		
		execM85();
		
		execM95();
		
	}

	private void execM2() throws Exception {
		System.out.println("Running 2");
		testMathSpaceAnalysis( "/home/matias/.m2/repository/junit/junit/4.10/junit-4.10.jar",
		"/home/matias/develop/code/defects4j-fw/extr/math_2/",
		"org.apache.commons.math3.distribution.HypergeometricDistributionTest", 
		"/src/main/java/", 
		 "/src/test/java/", 
		"/target/classes/",
		 "/target/test-classes/");
	}

	private void execM95() throws Exception {
		System.out.println("Running 95");
		testMathSpaceAnalysis( "/home/matias/.m2/repository/junit/junit/4.4/junit-4.4.jar",
				"/home/matias/develop/code/defects4j-fw/extr/math_95/",
				"org.apache.commons.math.distribution.FDistributionTest", 
				"/src/main/java/", 
				 "/src/test/java/", 
				"/target/classes/",
				 "/target/test-classes/");
	}

	private void execM85() throws Exception {
		System.out.println("Running 85");
		testMathSpaceAnalysis( "/home/matias/.m2/repository/junit/junit/4.4/junit-4.4.jar",
				"/home/matias/develop/code/defects4j-fw/extr/math_85/",
				"org.apache.commons.math.distribution.NormalDistributionTest", 
				"/src/main/java/", 
				 "/src/test/java/", 
				"/target/classes/",
				 "/target/test-classes/");
	}

	private void execM84() throws Exception {
		System.out.println("Running 84");
		testMathSpaceAnalysis( "/home/matias/.m2/repository/junit/junit/4.4/junit-4.4.jar",
				"/home/matias/develop/code/defects4j-fw/extr/math_84/",
				"org.apache.commons.math.optimization.direct.MultiDirectionalTest", 
				"/src/main/java/", 
				 "/src/test/java/", 
				"/target/classes/",
				 "/target/test-classes/");
	}

	private void execM82() throws Exception {
		System.out.println("Running 82");
		testMathSpaceAnalysis( "/home/matias/.m2/repository/junit/junit/4.4/junit-4.4.jar",
				"/home/matias/develop/code/defects4j-fw/extr/math_82/",
				"org.apache.commons.math.optimization.linear.SimplexSolverTest", 
				"/src/main/java/", 
				 "/src/test/java/", 
				"/target/classes/",
				 "/target/test-classes/");
	}

	@Test
	public void execM81() throws Exception {
		System.out.println("Running 81");
		testMathSpaceAnalysis( "/home/matias/.m2/repository/junit/junit/4.4/junit-4.4.jar",
				"/home/matias/develop/code/defects4j-fw/extr/math_81/",
				"org.apache.commons.math.linear.EigenDecompositionImplTest", 
				"/src/main/java/", 
				 "/src/test/java/", 
				"/target/classes/",
				 "/target/test-classes/");
	}

	@Test
	public void execM80() throws Exception {
		System.out.println("Running 80");
		testMathSpaceAnalysis( "/home/matias/.m2/repository/junit/junit/4.4/junit-4.4.jar",
				"/home/matias/develop/code/defects4j-fw/extr/math_80/",
				"org.apache.commons.math.linear.EigenDecompositionImplTest", 
				"/src/main/java/", 
				 "/src/test/java/", 
				"/target/classes/",
				 "/target/test-classes/");
	}

	private void execM78() throws Exception {
		System.out.println("Running 78");
		testMathSpaceAnalysis( "/home/matias/.m2/repository/junit/junit/4.4/junit-4.4.jar",
				"/home/matias/develop/code/defects4j-fw/extr/math_78/",
				"org.apache.commons.math.ode.events.EventStateTest", 
				"/src/main/java/", 
				 "/src/test/java/", 
				"/target/classes/",
				 "/target/test-classes/");
	}

	@Test
	public void execM73() throws Exception {
		System.out.println("Running 73");
		testMathSpaceAnalysis( "/home/matias/.m2/repository/junit/junit/4.4/junit-4.4.jar",
				//"/home/matias/develop/code/defects4j-fw/extr/math_73/",
				"/home/matias/develop/code/astor/examples/math_73/",
				"org.apache.commons.math.analysis.solvers.BrentSolverTest", 
				"/src/main/java/",
				"/src/test/java",
				"/target/classes/",
				 "/target/test-classes/");
	}
	
	private void execM71() throws Exception {
		System.out.println("Running 71");
		testMathSpaceAnalysis( "/home/matias/.m2/repository/junit/junit/4.4/junit-4.4.jar",
				"/home/matias/develop/code/defects4j-fw/extr/math_71/",
				"org.apache.commons.math.ode.nonstiff.ClassicalRungeKuttaIntegratorTest", 
				"/src/main/java/", 
				 "/src/test/java/", 
				"/target/classes/",
				 "/target/test-classes/");
	}
	private void execM70() throws Exception {
		System.out.println("Running 70");
		testMathSpaceAnalysis( "/home/matias/.m2/repository/junit/junit/4.4/junit-4.4.jar",
				"/home/matias/develop/code/defects4j-fw/extr/math_70/",
				"org.apache.commons.math.analysis.solvers.BisectionSolverTest", 
				"/src/java/", 
				 "/src/test/", 
				"/target/classes/",
				 "/target/test-classes/");
	}

	private void execM53() throws Exception {
		System.out.println("Running 53");
		testMathSpaceAnalysis( "/home/matias/.m2/repository/junit/junit/4.8.2/junit-4.8.2.jar",
				"/home/matias/develop/code/defects4j-fw/extr/math_53/",
				"org.apache.commons.math.complex.ComplexTest", 
				"/src/main/java/", 
				 "/src/test/java/", 
				"/target/classes/",
				 "/target/test-classes/");
	}

	private void execM50() throws Exception {
		System.out.println("Running 50");
		testMathSpaceAnalysis( "/home/matias/.m2/repository/junit/junit/4.8.2/junit-4.8.2.jar",
				"/home/matias/develop/code/defects4j-fw/extr/math_50/",
				"org.apache.commons.math.analysis.solvers.RegulaFalsiSolverTest", 
				"/src/main/java/", 
				 "/src/test/java/", 
				"/target/classes/",
				 "/target/test-classes/");
	}

	private void execM49() throws Exception {
		System.out.println("Running 49");
		testMathSpaceAnalysis( "/home/matias/.m2/repository/junit/junit/4.8.2/junit-4.8.2.jar",
				"/home/matias/develop/code/defects4j-fw/extr/math_49/",
				"org.apache.commons.math.linear.SparseRealVectorTest", 
				"/src/main/java/", 
				 "/src/test/java/", 
				"/target/classes/",
				 "/target/test-classes/");
	}

	@Test
	public void execM40() throws Exception {
		System.out.println("Running 40");
		testMathSpaceAnalysis( "/home/matias/.m2/repository/junit/junit/4.10/junit-4.10.jar",
				"/home/matias/develop/code/defects4j-fw/extr/math_40/",
				"org.apache.commons.math.analysis.solvers.BracketingNthOrderBrentSolverTest", 
				"/src/main/java/", 
				 "/src/test/java/", 
				"/target/classes/",
				 "/target/test-classes/");
	}

	private void execM24() throws Exception {
		System.out.println("Running 24");
		testMathSpaceAnalysis( "/home/matias/.m2/repository/junit/junit/4.10/junit-4.10.jar",
				"/home/matias/develop/code/defects4j-fw/extr/math_24/",
				"org.apache.commons.math3.optimization.univariate.BrentOptimizerTest", 
				"/src/main/java/", 
				 "/src/test/java/", 
				"/target/classes/",
				 "/target/test-classes/");
	}

	@Test
	public void execM8() throws Exception {
		System.out.println("Running 8");
		testMathSpaceAnalysis( "/home/matias/.m2/repository/junit/junit/4.10/junit-4.10.jar",
				"/home/matias/develop/code/defects4j-fw/extr/math_8/",
				"org.apache.commons.math3.distribution.DiscreteRealDistributionTest", 
				"/src/main/java/", 
				 "/src/test/java/", 
				"/target/classes/",
				 "/target/test-classes/");
	}

	private void execM5() throws Exception {
		System.out.println("Running 5");
		testMathSpaceAnalysis( "/home/matias/.m2/repository/junit/junit/4.10/junit-4.10.jar",
				"/home/matias/develop/code/defects4j-fw/extr/math_5/",
				"org.apache.commons.math3.complex.ComplexTest", 
				"/src/main/java/", 
				 "/src/test/java/", 
				"/target/classes/",
				 "/target/test-classes/");
	}
	
	
	private void testMathSpaceAnalysis(String dep, String locat, String testf, String srcj, String srct, String clj, String clt) throws Exception {
		AstorMain main1 = new AstorMain();
		
		File out = new File("./outputMutation/");
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				testf, "-location",
				new File(locat).getAbsolutePath(), "-package",
				"org.apache.commons",
				"-jvm4testexecution",
				"/usr/lib/jvm/java-7-openjdk-amd64/bin/", 
				"-srcjavafolder", srcj, 
				"-srctestfolder", srct, 
				"-binjavafolder", clj,
				"-bintestfolder", clt, 
				"-javacompliancelevel", "5", "-flthreshold", "0.00001", 
				"-out",
				out.getAbsolutePath(), "-scope", "local",//"package",
				"-seed", "10",
				"-maxgen", "200",//runn 
				};
		System.out.println(Arrays.toString(args));
		main1.main(args);

		//validatePatchExistence(out + File.separator + "AstorMain-math_70/", 2);

	}

	/**
	 * JDT problem
	 * 
	 * @throws Exception
	 */
	@Test
	public void testMath20() throws Exception {
		AstorMain main1 = new AstorMain();
		String dep = "/home/matias/.m2/repository/junit/junit/4.10/junit-4.10.jar";
		File out = new File("./outputMutation/");
		String[] args = new String[] { "-dependencies",
				dep,
				"-mode",
				"statement",
				"-failing",
				"org.apache.commons.math3.optimization.direct.CMAESOptimizerTest", // math
																					// 6"org.apache.commons.math.analysis.solvers.RegulaFalsiSolverTest",
				"-location", new File("./examples/math_20").getAbsolutePath(), "-package", "org.apache.commons",
				"-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder", "/src/main/java/",
				"-srctestfolder", "/src/test/java/", "-binjavafolder", "/target/classes", "-bintestfolder",
				"/target/test-classes", "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(),
				// NEW ARGUMENT FOR TEST: package scope
				// "-scope","global",//"package",
				"-seed", "10",
				// "-stopfirst","false",
				"-maxgen", "200", };
		System.out.println(Arrays.toString(args));
		main1.main(args);

		validatePatchExistence(out + File.separator + "AstorMain-math_20/", 2);

	}

	@Test
	public void testChart21() throws Exception {
		AstorMain main1 = new AstorMain();
		File location = new File("./examples/chart_21");
		String dep = location + "/lib" + File.separator + "junit.jar:" + location + "/lib" + File.separator
				+ "itext-2.0.2.jar:" + location + "/lib" + File.separator + "servlet.jar:";

		File out = new File("./outputMutation/");
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement-remove", "-failing",
				"org.jfree.data.statistics.junit.DefaultBoxAndWhiskerCategoryDatasetTests", "-location",
				location.getAbsolutePath(), "-package", "", "-jvm4testexecution",
				"/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder", "/source", "-srctestfolder", "/tests",
				"-binjavafolder", "/build/", "-bintestfolder", "/build-tests/", "-javacompliancelevel", "4",
				"-flthreshold", "0.0", "-out", out.getAbsolutePath(), "-seed", "10", "-stopfirst", "true", "-maxgen",
				"50", "-maxsuspcandidates", "80", };
		System.out.println(Arrays.toString(args));
		main1.main(args);

		// validatePatchExistence(out+File.separator+"AstorMain-math_70/",2);

	}

	@Test
	public void testChart6() throws Exception {
		AstorMain main1 = new AstorMain();
		File location = new File("./examples/chart_6Test");
		String dep = location + "/lib" + File.separator + "junit.jar:" + location + "/lib" + File.separator
				+ "itext-2.0.6.jar:" + location + "/lib" + File.separator + "servlet.jar:";

		File out = new File("./outputMutation/");
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement", "-failing",
				"org.jfree.chart.util.junit.ShapeListTests", "-location", location.getAbsolutePath(), "-package", "",
				"-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder", "/source",
				"-srctestfolder", "/tests", "-binjavafolder", "/build/", "-bintestfolder", "/build-tests/",
				"-javacompliancelevel", "4", "-flthreshold", "0.01", "-out", out.getAbsolutePath(), "-seed", "10",
				"-stopfirst", "true",
		// "-maxgen", "50",
		// "-maxsuspcandidates","80",
		};
		System.out.println(Arrays.toString(args));
		main1.main(args);

		// validatePatchExistence(out+File.separator+"AstorMain-math_70/",2);

	}

	@Test
	public void testTime13() throws Exception {
		AstorMain main1 = new AstorMain();
		File location = new File("./examples/time_13");
		String dep = "/home/matias/.m2/repository/junit/junit/3.8.2/junit-3.8.2.jar"
				+ ":/home/matias/.m2/repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar";

		File out = new File("./outputMutation/");
		String[] args = new String[] { "-dependencies",
				dep,
				"-mode",
				"statement-remove",
				"-failing",
				"org.joda.time.format.TestISOPeriodFormat",// testFormatStandard_negative"
				"-location", location.getAbsolutePath(), "-package", "", "-jvm4testexecution",
				"/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder", "/src/main/java/", "-srctestfolder",
				"/src/test/java/", "-binjavafolder", "/build/classes/", "-bintestfolder", "/build/tests/",
				"-javacompliancelevel", "6", "-flthreshold", "0.05", "-out", out.getAbsolutePath(), "-seed", "10",
				"-stopfirst", "true", "-maxgen", "50", "-maxsuspcandidates", "80", };
		System.out.println(Arrays.toString(args));
		main1.main(args);

		// validatePatchExistence(out+File.separator+"AstorMain-math_70/",2);

	}

	@Test
	public void testTime11() throws Exception {
		AstorMain main1 = new AstorMain();
		File location = new File("./examples/time_11");
		String dep = "/home/matias/.m2/repository/junit/junit/3.8.2/junit-3.8.2.jar"
				+ ":/home/matias/.m2/repository/org/joda/joda-convert/1.2/joda-convert-1.2.jar";

		File out = new File("./outputMutation/");
		String[] args = new String[] { "-dependencies",
				dep,
				"-mode",
				"statement-remove",
				"-failing",
				"org.joda.time.tz.TestCompiler",// ::testDateTimeZoneBuilder
				"-location", location.getAbsolutePath(), "-package", "", "-jvm4testexecution",
				"/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder", "/src/main/java/", "-srctestfolder",
				"/src/test/java/", "-binjavafolder", "/target/classes/", "-bintestfolder", "/target/test-classes/",
				"-javacompliancelevel", "6", "-flthreshold", "0.0001", "-out", out.getAbsolutePath(), "-seed", "10",
				"-stopfirst", "true", "-maxgen", "50", "-maxsuspcandidates", "8000", };
		System.out.println(Arrays.toString(args));
		main1.main(args);

		validatePatchExistence(out + File.separator + "AstorMain-time_11/");

	}

	@Test
	public void testLang44() throws Exception {
		AstorMain main1 = new AstorMain();
		File location = new File("./examples/lang_44");
		String dep = "/home/matias/.m2/repository/junit/junit/3.8.1/junit-3.8.1.jar";

		File out = new File("./outputMutation/");
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement-remove", "-failing",
				"org.apache.commons.lang.NumberUtilsTest", "-location", location.getAbsolutePath(), "-package", "",
				"-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder", "/src/java/",
				"-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes/", "-bintestfolder",
				"/target/tests/", "-javacompliancelevel", "7", "-flthreshold", "0.005", "-out", out.getAbsolutePath(),
				"-seed", "10", "-stopfirst", "true", "-maxgen", "50", "-maxsuspcandidates", "80", "-saveall", "true" };
		System.out.println(Arrays.toString(args));
		main1.main(args);

		// validatePatchExistence(out+File.separator+"AstorMain-math_70/",2);

	}

	@Test
	public void testLang58() throws Exception {
		AstorMain main1 = new AstorMain();
		File location = new File("./examples/lang_44");
		String dep = "/home/matias/.m2/repository/junit/junit/3.8.1/junit-3.8.1.jar";

		File out = new File("./outputMutation/");
		String[] args = new String[] { "-dependencies", dep, "-mode", "statement-remove", "-failing",
				"org.apache.commons.lang.math.NumberUtilsTest", "-location", location.getAbsolutePath(), "-package",
				"", "-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder", "/src/java/",
				"-srctestfolder", "/src/test/", "-binjavafolder", "/target/classes/", "-bintestfolder",
				"/target/tests/", "-javacompliancelevel", "7", "-flthreshold", "0.005", "-out", out.getAbsolutePath(),
				"-seed", "10", "-stopfirst", "true", "-maxgen", "50", "-maxsuspcandidates", "80", "-saveall", "true" };
		System.out.println(Arrays.toString(args));
		main1.main(args);

		// validatePatchExistence(out+File.separator+"AstorMain-math_70/",2);

	}

	@Test
	public void testSeedExample() throws Exception {
		AstorMain main1 = new AstorMain();
		// java -cp astor.jar fr.inria.main.evolution.AstorMain
		// -location IntMax -dependencies . -failing
		// mooctest.TestSuite_all#test1_1:mooctest.TestSuite_all#test1_2:mooctest.TestSuite_all#test1_3
		// -package mooctest -maxgen 1000000 -population 2 -
		// srcjavafolder src/ -srctestfolder junit/ -binjavafolder bin/
		// -bintestfolder testbin/ -flthreshold 0
		String[] args = new String[] { "-dependencies",
				new File("./examples/IntMax").getAbsolutePath(),
				// "-mode","statement",
				"-failing",
				"mooctest.TestSuite_all",
				// "mooctest.TestSuite_all#test1_1:mooctest.TestSuite_all#test1_2:mooctest.TestSuite_all#test1_3",
				"-location", new File("./examples/IntMax").getAbsolutePath(), "-package", "mooctest",
				"-jvm4testexecution", "/home/matias/develop/jdk1.7.0_71/bin", "-srcjavafolder", "/src/",
				"-srctestfolder", "/junit/", "-binjavafolder", "/bin/", "-bintestfolder", "/testbin",
				// "-javacompliancelevel", "7",
				"-flthreshold", "0.0",
		// "-scope","local"//"package"
		};
		System.out.println(Arrays.toString(args));
		main1.main(args);

	}

}
