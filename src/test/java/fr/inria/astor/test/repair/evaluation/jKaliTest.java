package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.AbstractMain;
import fr.inria.main.evolution.AstorMain;
/**
 * Execution of multiples kali scenarios.
 * @author Matias Martinez matias.martinez@inria.fr
 *
 */
public class jKaliTest extends BaseEvolutionaryTest{
	
	File out = null;
	public jKaliTest(){
		out = new File(ConfigurationProperties.getProperty("workingDirectory"));

	}
	
	protected void testSeedExampleKali(String projectpath) throws Exception {
		AstorMain main1 = new AstorMain();
		File out = new File("./outputMutation/");
		String[] args = new String[] {
				"-dependencies", new File(projectpath+File.separator+"lib/").getAbsolutePath(),
				"-mode","statement-remove",
				"-failing", 
				"mooctest.TestSuite_all", 
				"-location",new File(projectpath).getAbsolutePath(),							
				"-package", "mooctest",
				"-srcjavafolder", "/src/",
				"-srctestfolder", "/junit/", 
				"-binjavafolder", "/bin/", 
				"-bintestfolder","/bin/",
				"-flthreshold","0.1",
				"-out",out.getAbsolutePath()
				};
		System.out.println(Arrays.toString(args));
		main1.main(args);
		
	}
	@Test
	public void testSeedExampleKaliRemove() throws Exception{
		this.testSeedExampleKali("./examples/testKaliRemoveStatement");
		validatePatchExistence(ConfigurationProperties.getProperty("workingDirectory")+File.separator+"AstorMain-testKaliRemoveStatement/",1);
	}
	@Test
	public void testSeedExampleKaliIfFalse() throws Exception{
		this.testSeedExampleKali("./examples/testKaliIfFalse");
		validatePatchExistence(ConfigurationProperties.getProperty("workingDirectory")+File.separator+"AstorMain-testKaliIfFalse/",1);
		
	}
	
	@Test
	public void testSeedExampleKaliAddReturnInt() throws Exception{
		this.testSeedExampleKali("./examples/testKaliAddReturnPrimitive");
		validatePatchExistence(ConfigurationProperties.getProperty("workingDirectory")+File.separator+"AstorMain-testKaliAddReturnPrimitive/",6);
		
	}
	
	@Test
	public void testSeedExampleKaliAddReturnVoid() throws Exception{
		this.testSeedExampleKali("./examples/testKaliAddReturnVoid");
		validatePatchExistence(ConfigurationProperties.getProperty("workingDirectory")+File.separator+"AstorMain-testKaliAddReturnVoid/",3);
		
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
	
	/**
	 * Test Kali mode over bug Math-55
	 * @throws Exception
	 */
	@SuppressWarnings("static-access")
	@Test
	public void testRunMainLang55RemoveKali() throws Exception {
		AstorMain main1 = new AstorMain();
		String pathExample = new File("./examples/lang_55").getAbsolutePath().replace(".", "");
		String[] args = new String[] {
				"-dependencies",
				pathExample + File.separator + "lib" + File.separator + "junit-3.8.1.jar",
				"-failing",
				"org.apache.commons.lang.ArrayUtilsAddTest:org.apache.commons.lang.ArrayUtilsRemoveTest:org.apache.commons.lang.ArrayUtilsTest:org.apache.commons.lang.BitFieldTest:org.apache.commons.lang.BooleanUtilsTest:org.apache.commons.lang.CharEncodingTest:org.apache.commons.lang.CharRangeTest:org.apache.commons.lang.CharSetTest:org.apache.commons.lang.CharSetUtilsTest:org.apache.commons.lang.CharUtilsTest:org.apache.commons.lang.ClassUtilsTest:org.apache.commons.lang.EntitiesPerformanceTest:org.apache.commons.lang.EntitiesTest:org.apache.commons.lang.IllegalClassExceptionTest:org.apache.commons.lang.IncompleteArgumentExceptionTest:org.apache.commons.lang.IntHashMapTest:org.apache.commons.lang.LocaleUtilsTest:org.apache.commons.lang.NotImplementedExceptionTest:org.apache.commons.lang.NullArgumentExceptionTest:org.apache.commons.lang.NumberRangeTest:org.apache.commons.lang.NumberUtilsTest:org.apache.commons.lang.ObjectUtilsTest:org.apache.commons.lang.RandomStringUtilsTest:org.apache.commons.lang.SerializationUtilsTest:org.apache.commons.lang.StringEscapeUtilsTest:org.apache.commons.lang.StringUtilsEqualsIndexOfTest:org.apache.commons.lang.StringUtilsIsTest:org.apache.commons.lang.StringUtilsSubstringTest:org.apache.commons.lang.StringUtilsTest:org.apache.commons.lang.StringUtilsTrimEmptyTest:org.apache.commons.lang.SystemUtilsTest:org.apache.commons.lang.UnhandledExceptionTest:org.apache.commons.lang.ValidateTest:org.apache.commons.lang.WordUtilsTest:org.apache.commons.lang.builder.CompareToBuilderTest:org.apache.commons.lang.builder.DefaultToStringStyleTest:org.apache.commons.lang.builder.EqualsBuilderTest:org.apache.commons.lang.builder.HashCodeBuilderAndEqualsBuilderTest:org.apache.commons.lang.builder.HashCodeBuilderTest:org.apache.commons.lang.builder.MultiLineToStringStyleTest:org.apache.commons.lang.builder.NoFieldNamesToStringStyleTest:org.apache.commons.lang.builder.ReflectionToStringBuilderExcludeTest:org.apache.commons.lang.builder.ShortPrefixToStringStyleTest:org.apache.commons.lang.builder.SimpleToStringStyleTest:org.apache.commons.lang.builder.StandardToStringStyleTest:org.apache.commons.lang.builder.ToStringBuilderTest:org.apache.commons.lang.builder.ToStringStyleTest:org.apache.commons.lang.enum.EnumTest:org.apache.commons.lang.enum.EnumUtilsTest:org.apache.commons.lang.enum.ValuedEnumTest:org.apache.commons.lang.enums.EnumEqualsTest:org.apache.commons.lang.enums.EnumTest:org.apache.commons.lang.enums.EnumUtilsTest:org.apache.commons.lang.enums.ValuedEnumTest:org.apache.commons.lang.exception.ExceptionUtilsTestCase:org.apache.commons.lang.exception.NestableDelegateTestCase:org.apache.commons.lang.exception.NestableErrorTestCase:org.apache.commons.lang.exception.NestableExceptionTestCase:org.apache.commons.lang.exception.NestableRuntimeExceptionTestCase:org.apache.commons.lang.math.DoubleRangeTest:org.apache.commons.lang.math.FloatRangeTest:org.apache.commons.lang.math.FractionTest:org.apache.commons.lang.math.IntRangeTest:org.apache.commons.lang.math.LongRangeTest:org.apache.commons.lang.math.NumberRangeTest:org.apache.commons.lang.math.NumberUtilsTest:org.apache.commons.lang.math.RandomUtilsTest:org.apache.commons.lang.math.RangeTest:org.apache.commons.lang.mutable.MutableBooleanTest:org.apache.commons.lang.mutable.MutableByteTest:org.apache.commons.lang.mutable.MutableDoubleTest:org.apache.commons.lang.mutable.MutableFloatTest:org.apache.commons.lang.mutable.MutableIntTest:org.apache.commons.lang.mutable.MutableLongTest:org.apache.commons.lang.mutable.MutableObjectTest:org.apache.commons.lang.mutable.MutableShortTest:org.apache.commons.lang.text.CompositeFormatTest:org.apache.commons.lang.text.StrBuilderAppendInsertTest:org.apache.commons.lang.text.StrBuilderTest:org.apache.commons.lang.text.StrLookupTest:org.apache.commons.lang.text.StrMatcherTest:org.apache.commons.lang.text.StrSubstitutorTest:org.apache.commons.lang.text.StrTokenizerTest:org.apache.commons.lang.time.DateFormatUtilsTest:org.apache.commons.lang.time.DateUtilsTest:org.apache.commons.lang.time.DurationFormatUtilsTest:org.apache.commons.lang.time.FastDateFormatTest:org.apache.commons.lang.time.StopWatchTest",
				"-location", pathExample, "-package", "org.apache.commons",
				"-srcjavafolder", "/src/java/", "-srctestfolder", "/src/test/",
				"-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes", "-javacompliancelevel",
				"7", "-alternativecompliancelevel", "4", 
				//Here, we specify the Kali mode
				"-mode", "statement-remove", 
				"-population", "1", 
				"-stopfirst", "true",
				"-flthreshold", "0.05",

		};
		log.info(Arrays.toString(args));
		main1.main(args);
		int numberSolution = numberSolutions(out + File.separator + "lang_55");
		assertTrue(numberSolution > 0);
		
	}
	
	@Override
	public AbstractMain createMain() {
			return null;
	}
	@Override
	public void generic(String location, String folder, String regression, String failing, String dependenciespath,
			String packageToInstrument, double thfl) throws Exception {
		
	}
	
	@SuppressWarnings("static-access")
	@Test
	public void testMath2ExampleRemoveMode() throws Exception {
		AstorMain main1 = new AstorMain();

		String[] args = (new String[] { "-dependencies", "examples/math_2/libmvn/", "-mode", "statement-remove",
				"-failing", "org.apache.commons.math3.distribution.HypergeometricDistributionTest", "-location",
				"examples/math_2/", "-package", "org.apache.commons", "-stopfirst", "true", "-srcjavafolder", "/src/main/java/", "-srctestfolder",
				"/src/test/java/", "-binjavafolder", "/target/classes", "-bintestfolder", "/target/test-classes",
				"-stopfirst","true",
		});
		System.out.println("Arguments:\n " + Arrays.toString(args).replace(',', ' '));
		main1.main(args);
		int numberSolution = numberSolutions(out + File.separator + "math_2");
		assertTrue(numberSolution > 0);
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
				"org.apache.commons", "-maxgen", "400", "-population", "2", "-mode", "statement-remove"

		});

	}

}
