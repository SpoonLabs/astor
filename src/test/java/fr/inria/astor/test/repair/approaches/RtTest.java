package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.inria.astor.approaches.extensions.rt.RtEngine;
import fr.inria.astor.approaches.extensions.rt.RtEngine.RuntimeInformation;
import fr.inria.astor.approaches.extensions.rt.RtEngine.TestClassificationResult;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.declaration.CtClass;

/**
 * 
 * @author Matias Martinez
 *
 */
public class RtTest {

	public static Logger log = Logger.getLogger(Thread.currentThread().getName());

	@Test
	public void testRTMath70d() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "INFO");
		cs.command.put("-location", new File("./examples/math_70_rt").getAbsolutePath());
		cs.command.put("-mode", "custom");
		cs.command.put("-customengine", RtEngine.class.getCanonicalName());

		main1.execute(cs.flat());
		RtEngine etEn = (RtEngine) main1.getEngine();

		List<TestClassificationResult> resultByTest = etEn.getResultByTest();

		int sumAssertionNotExec = resultByTest.stream()
				.map(e -> e.getClassificationAssert().getResultNotExecuted().size())
				.collect(Collectors.summingInt(i -> i));

		assertEquals(7, sumAssertionNotExec);

		int sumHelperNotExec = resultByTest.stream()
				.map(e -> e.getClassificationHelperAssertion().getResultNotExecuted().size())
				.collect(Collectors.summingInt(i -> i));

		assertEquals(1, sumHelperNotExec);

		int sumReturnNotExec = resultByTest.stream().map(e -> e.getAllSkipFromTest().size())
				.collect(Collectors.summingInt(i -> i));

		assertEquals(1, sumReturnNotExec);

		TestClassificationResult testwithskip = resultByTest.stream().filter(e -> !e.getAllSkipFromTest().isEmpty())
				.findFirst().get();
		assertEquals("testMMRt_return_truePositive", testwithskip.getTestMethodFromClass());
	}

	@Test
	public void testRT1() throws Exception {

		File location = new File("/Users/matias/develop/rt-research/datasetevaluation/javapoet");
		String dep = "/Users/matias/.m2/repository/net/bytebuddy/byte-buddy-agent/1.7.9/byte-buddy-agent-1.7.9.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/com/google/guava/guava/23.4-android/guava-23.4-android.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/org/objenesis/objenesis/2.6/objenesis-2.6.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/eclipse/jdt/core/compiler/ecj/4.6.1/ecj-4.6.1.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/com/google/errorprone/error_prone_annotations/2.1.3/error_prone_annotations-2.1.3.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/junit/junit/4.12/junit-4.12.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/com/google/auto/value/auto-value/1.5.3/auto-value-1.5.3.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/com/google/truth/truth/0.39/truth-0.39.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/com/google/j2objc/j2objc-annotations/1.1/j2objc-annotations-1.1.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/com/google/jimfs/jimfs/1.1/jimfs-1.1.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/com/google/testing/compile/compile-testing/0.15/compile-testing-0.15.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/net/bytebuddy/byte-buddy/1.7.9/byte-buddy-1.7.9.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/com/google/truth/extensions/truth-java8-extension/0.37/truth-java8-extension-0.37.jar"
				+ File.pathSeparator
				+ "/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/../lib/tools.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/com/google/code/findbugs/jsr305/1.3.9/jsr305-1.3.9.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/mockito/mockito-core/2.13.0/mockito-core-2.13.0.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/com/google/auto/auto-common/0.9/auto-common-0.9.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/codehaus/mojo/animal-sniffer-annotations/1.14/animal-sniffer-annotations-1.14.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/com/google/truth/truth/0.39/truth-0.39.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/com/google/guava/guava/23.4-android/guava-23.4-android.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/com/google/code/findbugs/jsr305/1.3.9/jsr305-1.3.9.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/com/google/j2objc/j2objc-annotations/1.1/j2objc-annotations-1.1.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/codehaus/mojo/animal-sniffer-annotations/1.14/animal-sniffer-annotations-1.14.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/com/google/errorprone/error_prone_annotations/2.1.3/error_prone_annotations-2.1.3.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/com/google/testing/compile/compile-testing/0.15/compile-testing-0.15.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/com/google/truth/extensions/truth-java8-extension/0.37/truth-java8-extension-0.37.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/com/google/auto/value/auto-value/1.5.3/auto-value-1.5.3.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/com/google/auto/auto-common/0.9/auto-common-0.9.jar"
				+ File.pathSeparator
				+ "/Library/Java/JavaVirtualMachines/jdk1.8.0_101.jdk/Contents/Home/jre/../lib/tools.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/junit/junit/4.12/junit-4.12.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/com/google/jimfs/jimfs/1.1/jimfs-1.1.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/mockito/mockito-core/2.13.0/mockito-core-2.13.0.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/net/bytebuddy/byte-buddy/1.7.9/byte-buddy-1.7.9.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/net/bytebuddy/byte-buddy-agent/1.7.9/byte-buddy-agent-1.7.9.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/org/objenesis/objenesis/2.6/objenesis-2.6.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/eclipse/jdt/core/compiler/ecj/4.6.1/ecj-4.6.1.jar";

		RtEngine etEn = detectRt(location, dep);

		List<TestClassificationResult> resultByTest = etEn.getResultByTest();

		System.out.println(resultByTest);
	}

	private RtEngine detectRt(File location, String dep) throws Exception {
		return detectRt(location, dep, null, null);

	}

	private RtEngine detectRt(File location, String dep, String name, String subproject) throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary cs = getCommand(location, dep, name, subproject);

		main1.execute(cs.flat());
		RtEngine etEn = (RtEngine) main1.getEngine();
		return etEn;
	}

	public CommandSummary getCommand(File location, String dep, String name, String subproject) {
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		int generations = 500;

		String[] args = new String[] { "-javacompliancelevel", "10", "-flthreshold", "0.001", "-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "10", "-maxgen", Integer.toString(generations),
				"-maxtime", "100",

		};
		CommandSummary cs = new CommandSummary(args);

		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "INFO");
		cs.command.put("-location", location.getAbsolutePath());
		if (dep == null) {
			cs.command.put("-autoconfigure", "true");
		} else {
			cs.command.put("-dependencies", dep);
		}
		cs.command.put("-mode", "custom");
		cs.command.put("-customengine", RtEngine.class.getCanonicalName());
		cs.command.put("-parameters",
				"canhavezerosusp:true:runonoriginalbin:true:continuewhenmodelfail:true" + ":mvndir:/usr/local/bin/mvn"
						+ ((name != null) ? ":id:" + name : "")
						+ ((subproject != null) ? ":projectsubfolder:" + subproject : ""));
		return cs;
	}

	@Test
	public void testRT1_flikcore() throws Exception {

		File location = new File("/Users/matias/develop/rt-research/datasetevaluation/flink/flink-core/");
		String dep = "/Users/matias/.m2/repository/org/apache/commons/commons-compress/1.18/commons-compress-1.18.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/powermock/powermock-reflect/2.0.0-RC.4/powermock-reflect-2.0.0-RC.4.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/apache/flink/flink-shaded-guava/18.0-6.0/flink-shaded-guava-18.0-6.0.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/org/hamcrest/hamcrest-all/1.3/hamcrest-all-1.3.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/slf4j/slf4j-log4j12/1.7.15/slf4j-log4j12-1.7.15.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/commons-io/commons-io/2.4/commons-io-2.4.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/powermock/powermock-api-support/2.0.0-RC.4/powermock-api-support-2.0.0-RC.4.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/junit/junit/4.12/junit-4.12.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/powermock/powermock-api-mockito2/2.0.0-RC.4/powermock-api-mockito2-2.0.0-RC.4.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/apache/flink/flink-annotations/1.9-SNAPSHOT/flink-annotations-1.9-SNAPSHOT.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/org/objenesis/objenesis/2.1/objenesis-2.1.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/apache/commons/commons-lang3/3.3.2/commons-lang3-3.3.2.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/commons-collections/commons-collections/3.2.2/commons-collections-3.2.2.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/mockito/mockito-core/2.21.0/mockito-core-2.21.0.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/apache/flink/flink-shaded-jackson/2.7.9-6.0/flink-shaded-jackson-2.7.9-6.0.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/com/esotericsoftware/minlog/minlog/1.2/minlog-1.2.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/apache/flink/flink-shaded-asm-6/6.2.1-6.0/flink-shaded-asm-6-6.2.1-6.0.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/com/esotericsoftware/kryo/kryo/2.24.0/kryo-2.24.0.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/com/google/code/findbugs/jsr305/1.3.9/jsr305-1.3.9.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/log4j/log4j/1.2.17/log4j-1.2.17.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/apache/flink/force-shading/1.9-SNAPSHOT/force-shading-1.9-SNAPSHOT.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/projectlombok/lombok/1.16.20/lombok-1.16.20.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/powermock/powermock-module-junit4-common/2.0.0-RC.4/powermock-module-junit4-common-2.0.0-RC.4.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/joda-time/joda-time/2.5/joda-time-2.5.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/powermock/powermock-module-junit4/2.0.0-RC.4/powermock-module-junit4-2.0.0-RC.4.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/powermock/powermock-core/2.0.0-RC.4/powermock-core-2.0.0-RC.4.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/org/joda/joda-convert/1.7/joda-convert-1.7.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/apache/flink/flink-metrics-core/1.9-SNAPSHOT/flink-metrics-core-1.9-SNAPSHOT.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/net/bytebuddy/byte-buddy/1.8.15/byte-buddy-1.8.15.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/apache/flink/flink-test-utils-junit/1.9-SNAPSHOT/flink-test-utils-junit-1.9-SNAPSHOT.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/javassist/javassist/3.19.0-GA/javassist-3.19.0-GA.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/org/slf4j/slf4j-api/1.7.15/slf4j-api-1.7.15.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/net/bytebuddy/byte-buddy-agent/1.8.15/byte-buddy-agent-1.8.15.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/apache/flink/flink-annotations/1.9-SNAPSHOT/flink-annotations-1.9-SNAPSHOT.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/apache/flink/flink-metrics-core/1.9-SNAPSHOT/flink-metrics-core-1.9-SNAPSHOT.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/apache/flink/flink-shaded-asm-6/6.2.1-6.0/flink-shaded-asm-6-6.2.1-6.0.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/apache/commons/commons-lang3/3.3.2/commons-lang3-3.3.2.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/com/esotericsoftware/kryo/kryo/2.24.0/kryo-2.24.0.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/com/esotericsoftware/minlog/minlog/1.2/minlog-1.2.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/org/objenesis/objenesis/2.1/objenesis-2.1.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/commons-collections/commons-collections/3.2.2/commons-collections-3.2.2.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/apache/commons/commons-compress/1.18/commons-compress-1.18.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/apache/flink/flink-shaded-guava/18.0-6.0/flink-shaded-guava-18.0-6.0.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/apache/flink/flink-test-utils-junit/1.9-SNAPSHOT/flink-test-utils-junit-1.9-SNAPSHOT.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/commons-io/commons-io/2.4/commons-io-2.4.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/joda-time/joda-time/2.5/joda-time-2.5.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/org/joda/joda-convert/1.7/joda-convert-1.7.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/apache/flink/flink-shaded-jackson/2.7.9-6.0/flink-shaded-jackson-2.7.9-6.0.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/projectlombok/lombok/1.16.20/lombok-1.16.20.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/apache/flink/force-shading/1.9-SNAPSHOT/force-shading-1.9-SNAPSHOT.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/org/slf4j/slf4j-api/1.7.15/slf4j-api-1.7.15.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/com/google/code/findbugs/jsr305/1.3.9/jsr305-1.3.9.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/junit/junit/4.12/junit-4.12.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/hamcrest/hamcrest-core/1.3/hamcrest-core-1.3.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/mockito/mockito-core/2.21.0/mockito-core-2.21.0.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/net/bytebuddy/byte-buddy/1.8.15/byte-buddy-1.8.15.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/net/bytebuddy/byte-buddy-agent/1.8.15/byte-buddy-agent-1.8.15.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/powermock/powermock-module-junit4/2.0.0-RC.4/powermock-module-junit4-2.0.0-RC.4.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/powermock/powermock-module-junit4-common/2.0.0-RC.4/powermock-module-junit4-common-2.0.0-RC.4.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/powermock/powermock-reflect/2.0.0-RC.4/powermock-reflect-2.0.0-RC.4.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/powermock/powermock-core/2.0.0-RC.4/powermock-core-2.0.0-RC.4.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/javassist/javassist/3.19.0-GA/javassist-3.19.0-GA.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/powermock/powermock-api-mockito2/2.0.0-RC.4/powermock-api-mockito2-2.0.0-RC.4.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/powermock/powermock-api-support/2.0.0-RC.4/powermock-api-support-2.0.0-RC.4.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/org/hamcrest/hamcrest-all/1.3/hamcrest-all-1.3.jar"
				+ File.pathSeparator
				+ "/Users/matias/.m2/repository/org/slf4j/slf4j-log4j12/1.7.15/slf4j-log4j12-1.7.15.jar"
				+ File.pathSeparator + "/Users/matias/.m2/repository/log4j/log4j/1.2.17/log4j-1.2.17.jar";
		RtEngine etEn = detectRt(location, dep);

		List<TestClassificationResult> resultByTest = etEn.getResultByTest();

		System.out.println(resultByTest);
	}

	@Test
	public void testRT1_flikcore_ac() throws Exception {

		File location = new File("/Users/matias/develop/rt-research/datasetevaluation/flink/flink-core/");

		RtEngine etEn = detectRt(location, null, "flink-core", "flink-core");

		List<TestClassificationResult> resultByTest = etEn.getResultByTest();

		System.out.println(resultByTest);

		boolean hasrt = resultByTest.stream().anyMatch(e -> e.isRotten());

		// git rev-parse HEAD
		// git config --get remote.origin.url
	}

	@Test
	public void testRT1_flik_root_ac() throws Exception {

		File location = new File("/Users/matias/develop/rt-research/datasetevaluation/flink/");

		RtEngine etEn = detectRt(location, null, "flink", "flink");

		List<TestClassificationResult> resultByTest = etEn.getResultByTest();

		System.out.println(resultByTest);

		boolean hasrt = resultByTest.stream().anyMatch(e -> e.isRotten());

		// git rev-parse HEAD
		// git config --get remote.origin.url
	}
	//

	@Test
	public void testRT1_rocketmq_spring_ac() throws Exception {

		File location = new File("/Users/matias/develop/rt-research/clonedrepos/rocketmq-spring");

		RtEngine etEn = detectRt(location, null, "rocketmq-spring", "rocketmq-spring");

		List<TestClassificationResult> resultByTest = etEn.getResultByTest();

		System.out.println(resultByTest);

		boolean hasrt = resultByTest.stream().anyMatch(e -> e.isRotten());

		// git rev-parse HEAD
		// git config --get remote.origin.url
	}

	@Test
	public void testRT1_NPE_conf() throws Exception {

		String projectName = "blueocean-plugin";

		runRTinProject(projectName);

	}

	public void runRTinProject(String projectName) throws Exception {
		File location = new File("/Users/matias/develop/rt-research/clonedrepos/" + projectName);

		RtEngine etEn = detectRt(location, null, projectName, projectName);

		List<TestClassificationResult> resultByTest = etEn.getResultByTest();

		System.out.println(resultByTest);

		boolean hasrt = resultByTest.stream().anyMatch(e -> e.isRotten());
	}

	@Test
	public void testRT1_flikcore_ac_wrong_classification() throws Exception {

		File location = new File("/Users/matias/develop/rt-research/datasetevaluation/flink/flink-core/");

		AstorMain main1 = new AstorMain();

		CommandSummary cs = getCommand(location, null, "flink-core", "flink-core");
		cs.append("-parameters", "skipanalysis:true");
		main1.execute(cs.flat());
		RtEngine etEn = (RtEngine) main1.getEngine();

		RuntimeInformation ri = etEn.computeDynamicInformation();
		assertNotNull(ri);

		List<TestClassificationResult> resultByTest = etEn.getResultByTest();

		System.out.println(resultByTest);

		String classNameOfTest = "org.apache.flink.api.common.typeutils.base.DoubleComparatorTest";
		CtClass aTestModelCtClass = MutationSupporter.getFactory().Class().get(classNameOfTest);

		assertNotNull(aTestModelCtClass);

		TestClassificationResult tresult = etEn.processTest("testNormalizedKeysEqualsFullLength", classNameOfTest,
				aTestModelCtClass, ri);

		assertNotNull(tresult);

		assertTrue(tresult.isRotten());

		etEn.resultByTest.add(tresult);

		// System.out.println(etEn.toJson());

		// org.apache.flink.api.common.typeutils.base.DoubleComparatorTest
		// testDuplicate
	}

	@Test
	public void testRT1_testExpected() throws Exception {

		File location = new File(
				"/Users/matias/develop/rt-research/datasetevaluation/flink/flink-connectors/flink-connector-filesystem");

		RtEngine etEn = detectRt(location, null, "flink-connector-filesystem",
				"flink-connectors/flink-connector-filesystem");

		List<TestClassificationResult> resultByTest = etEn.getResultByTest();

		System.out.println(resultByTest);

		boolean hasrt = resultByTest.stream().anyMatch(e -> e.isRotten());

	}

	@Test
	public void testRT1_testSkip() throws Exception {

		File location = new File(
				"/Users/matias/develop/rt-research/datasetevaluation/flink/flink-queryable-state/flink-queryable-state-runtime");

		RtEngine etEn = detectRt(location, null, "flink-queryable-state-runtime",
				"flink-queryable-state/flink-queryable-state-runtime");

		List<TestClassificationResult> resultByTest = etEn.getResultByTest();

		System.out.println(resultByTest);

		boolean hasrt = resultByTest.stream().anyMatch(e -> e.isRotten());

	}

	@Test
	public void testRT1_testnotfinish() throws Exception {

		File location = new File("/Users/matias/develop/rt-research/datasetevaluation/gs-collections");
//'/Users/matias/develop/rt-research/datasetevaluation/gs-collections', 'gs-collections', 'gs-collections'
		RtEngine etEn = detectRt(location, null, "gs-collections", "gs-collections");

		List<TestClassificationResult> resultByTest = etEn.getResultByTest();

		System.out.println(resultByTest);

		boolean hasrt = resultByTest.stream().anyMatch(e -> e.isRotten());

	}

	// '/Users/matias/develop/rt-research/datasetevaluation/metrics/metrics-core',
	// 'metrics/metrics-core', 'metrics-core'
	@Test
	public void testRT1_testzerotestt() throws Exception {

		File location = new File("/Users/matias/develop/rt-research/datasetevaluation/metrics/metrics-core");
//'/Users/matias/develop/rt-research/datasetevaluation/gs-collections', 'gs-collections', 'gs-collections'
		RtEngine etEn = detectRt(location, null, "metrics-core", "metrics-core");

		List<TestClassificationResult> resultByTest = etEn.getResultByTest();

		System.out.println(resultByTest);

		boolean hasrt = resultByTest.stream().anyMatch(e -> e.isRotten());

	}
//legacy-jclouds
	/// Users/matias/develop/rt-research/datasetevaluation/legacy-jclouds

	// '/Users/matias/develop/rt-research/datasetevaluation/metrics/metrics-core',
	// 'metrics/metrics-core', 'metrics-core'
	@Test
	public void testRT1_testmultim() throws Exception {

		File location = new File(
				// "/Users/matias/develop/rt-research/datasetevaluation/legacy-jclouds"
				"/Users/matias/develop/rt-research/datasetevaluation/helios/"
		// "/Users/matias/develop/rt-research/datasetevaluation/json-simple/"
		);

		RtEngine etEn = detectRt(location, null, "son-simple", "son-simple");

		List<TestClassificationResult> resultByTest = etEn.getResultByTest();

		System.out.println(resultByTest);

		boolean hasrt = resultByTest.stream().anyMatch(e -> e.isRotten());

	}
}
