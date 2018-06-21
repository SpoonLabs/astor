package fr.inria.astor.test.repair.evaluation.regression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.main.CommandSummary;
import fr.inria.main.FileLauncher;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
@Ignore
public class MockitoTest {

	
	String exampleRoot = "./examples/";
	ClassLoader classLoader = null;
	File dataDir = null;
	
	@Before
	public void setUp(){
		classLoader = getClass().getClassLoader();
		dataDir = new File(classLoader.getResource("data_projects").getPath());
	}
	
	
	
	public List<String> getParticularIssueLibs(File f ){
		if(!f.exists())
			throw new IllegalAccessError(f.getAbsolutePath() + " not found");
		List<String> otherlibs = new ArrayList<>();
		if(f.isFile())
			return otherlibs;
		for(File folder : f.listFiles()){
			if(folder.getName().endsWith(".jar")){
				otherlibs.add(folder.getAbsolutePath());
			}
			else{
				otherlibs.addAll(getParticularIssueLibs(folder));
			}
		}
		return otherlibs;
	}
	
	@Test
	public void testMockito29() throws Exception{
		int id = 29;
		testMockito(id);
	}
	
	@Test
	public void testMockito22() throws Exception{
		/*fr.inria.main.AbstractMain.processArguments(AbstractMain.java:286) -
		//command line arguments: [-out  /local/scr/mtufano/Mockito/22/b/random-global-default/1  
		 * -jvm4testexecution  /usr/local/jdk1.7.0_80/bin/  -maxgen  1000000 
		 *  -population  1  -maxtime  180  -timezone  America/New_York  
		 *  -customop  fr.inria.astor.approaches.jgenprog.operators.InsertAfterOp:fr.inria.astor.approaches.jgenprog.operators.InsertBeforeOp:fr.inria.astor.approaches.jgenprog.operators.ReplaceOp  
		 *  -seed  1  -javacompliancelevel  6  -package  org.mockito  -binjavafolder  target/classes  -bintestfolder  target/test-classes  
		 *  -srcjavafolder  src  -srctestfolder  test  -learningdir  ../../out/learningdir/Mockito/22/b/ 
		 *   -location  ../../dat/defects4j/Mockito/22/b/  -failing  org.mockito.internal.matchers.EqualityTest  
		 *   -dependencies  ../../dat/defects4j/Mockito/22/b/lib/build/ant-googlecode-0.0.3.jar:
		 *   ../../dat/defects4j/Mockito/22/b/lib/build/bnd-0.0.313.jar:
		 *   ../../dat/defects4j/Mockito/22/b/lib/build/ant4hg-V0.07.jar:
		 *   ../../dat/defects4j/Mockito/22/b/lib/build/jaxen-1.1.1.jar:.
		 *   ./../dat/defects4j/Mockito/22/b/lib/build/asm-3.1.jar:
		 *   ../../dat/defects4j/Mockito/22/b/lib/build/sorcerer.jar:
		 *   ../../dat/defects4j/Mockito/22/b/lib/build/pmd-4.1.jar:
		 *   ../../dat/defects4j/Mockito/22/b/lib/build/maven-ant-tasks-2.0.9.jar:
		 *   ../../dat/defects4j/Mockito/22/b/lib/build/jarjar-1.0.jar:
		 *   ../../dat/defects4j/Mockito/22/b/lib/compile/junit-4.10.jar:
		 *   ../../dat/defects4j/Mockito/22/b/lib/test/fest-util-1.1.4.jar:
		 *   ../../dat/defects4j/Mockito/22/b/lib/test/fest-assert-1.3.jar:
		 *   ../../dat/defects4j/Mockito/22/b/lib/test/powermock-reflect-1.2.5.jar:
		 *   ../../dat/defects4j/Mockito/22/b/lib/sources/com.springsource.org.hamcrest.core-1.1.0-sources.jar:
		 *   ../../dat/defects4j/Mockito/22/b/lib/sources/cglib-and-asm-1.0-sources.jar:
		 *   ../../dat/defects4j/Mockito/22/b/lib/sources/objenesis-2.1-sources.jar:
		 *   ../../dat/defects4j/Mockito/22/b/lib/repackaged/cglib-and-asm-1.0.jar:
		 *   ../../dat/defects4j/Mockito/22/b/lib/run/com.springsource.org.hamcrest.core-1.1.0.jar:
		 *   ../../dat/defects4j/Mockito/22/b/lib/run/objenesis-2.1.jar  
		 *   -scope  fr.inria.astor.core.loop.spaces.ingredients.scopes.ctscopes.CtGlobalIngredientScope  
		 *   -ingredientstrategy  fr.inria.astor.core.loop.spaces.ingredients.ingredientSearch.EfficientIngredientStrategy  
		 *   -classestoinstrument  org.mockito.internal.progress.ThreadSafeMockingProgress:org.mockito.MockitoAnnotations:org.mockito.exceptions.misusing.MockitoConfigurationException:org.mockito.internal.configuration.injection.filter.MockCandidateFilter:org.mockito.internal.invocation.realmethod.RealMethod:org.mockito.exceptions.misusing.NullInsteadOfMockException:org.mockito.Mock:org.mockito.internal.configuration.ClassPathLoader:org.mockito.exceptions.base.MockitoException:org.mockito.internal.configuration.InjectingAnnotationEngine:org.mockito.exceptions.base.MockitoAssertionError:org.mockito.exceptions.verification.TooLittleActualInvocations:org.mockito.invocation.MockHandler:org.mockito.internal.configuration.SpyAnnotationEngine:org.mockito.exceptions.verification.NeverWantedButInvoked:org.mockito.internal.invocation.MockitoMethod:org.mockito.internal.configuration.injection.ConstructorInjection:org.mockito.stubbing.Answer:org.mockito.exceptions.misusing.FriendlyReminderException:org.mockito.exceptions.misusing.InvalidUseOfMatchersException:org.mockito.internal.configuration.injection.scanner.InjectMocksScanner:org.mockito.exceptions.verification.NoInteractionsWanted:org.mockito.internal.matchers.Equality:org.mockito.invocation.Invocation:org.mockito.internal.configuration.GlobalConfiguration:org.mockito.exceptions.PrintableInvocation:org.mockito.plugins.MockMaker:org.mockito.configuration.IMockitoConfiguration:org.mockito.internal.configuration.injection.MockInjection:org.mockito.internal.util.collections.Sets:org.mockito.exceptions.misusing.WrongTypeOfReturnValue:org.mockito.invocation.InvocationOnMock:org.mockito.internal.configuration.MockAnnotationProcessor:org.mockito.exceptions.misusing.UnfinishedStubbingException:org.mockito.internal.util.MockUtil:org.mockito.exceptions.Reporter:org.mockito.exceptions.misusing.MissingMethodInvocationException:org.mockito.internal.configuration.injection.scanner.MockScanner:org.mockito.exceptions.misusing.UnfinishedVerificationException:org.mockito.internal.configuration.injection.filter.FinalMockCandidateFilter:org.mockito.mock.MockName:org.mockito.internal.configuration.injection.filter.NameBasedCandidateFilter:org.mockito.exceptions.verification.WantedButNotInvoked:org.mockito.configuration.DefaultMockitoConfiguration:org.mockito.internal.configuration.injection.filter.TypeBasedCandidateFilter:org.mockito.internal.util.reflection.FieldInitializer:org.mockito.exceptions.misusing.CannotVerifyStubOnlyMock:org.mockito.internal.progress.ArgumentMatcherStorageImpl:org.mockito.internal.configuration.MockitoAnnotationsMockAnnotationProcessor:org.mockito.exceptions.verification.TooManyActualInvocations:org.mockito.internal.util.Checks:org.mockito.Captor:org.mockito.internal.invocation.AbstractAwareMethod:org.mockito.internal.util.collections.ListUtil:org.mockito.internal.progress.ArgumentMatcherStorage:org.mockito.internal.configuration.injection.MockInjectionStrategy:org.mockito.exceptions.verification.VerificationInOrderFailure:org.mockito.internal.configuration.FieldAnnotationProcessor:org.mockito.internal.configuration.injection.PropertyAndSetterInjection:org.mockito.internal.configuration.DefaultAnnotationEngine:org.mockito.internal.creation.CglibMockMaker:org.mockito.invocation.DescribedInvocation:org.mockito.internal.progress.MockingProgress:org.mockito.internal.exceptions.stacktrace.DefaultStackTraceCleanerProvider:org.mockito.exceptions.misusing.NotAMockException:org.mockito.configuration.AnnotationEngine:org.mockito.exceptions.verification.ArgumentsAreDifferent:org.mockito.internal.configuration.injection.SpyOnInjectedFieldsHandler:org.mockito.internal.configuration.CaptorAnnotationProcessor:org.mockito.plugins.StackTraceCleanerProvider:org.mockito.invocation.Location:org.mockito.internal.progress.MockingProgressImpl:org.mockito.internal.util.reflection.FieldInitializationReport:org.mockito.internal.configuration.DefaultInjectionEngine:org.mockito.internal.configuration.injection.filter.OngoingInjecter:org.mockito.internal.util.collections.HashCodeAndEqualsSafeSet:org.mockito.exceptions.verification.SmartNullPointerException:  -regressiontestcases4fl  org.concurrentmockito.ThreadsShareAMockTest:org.concurrentmockito.ThreadsShareGenerouslyStubbedMockTest:org.mockito.internal.InvalidStateDetectionTest:org.mockito.internal.debugging.WarningsFinderTest:org.mockito.internal.handler.InvocationNotifierHandlerTest:org.mockito.internal.invocation.ArgumentsComparatorTest:org.mockito.internal.invocation.InvocationMatcherTest:org.mockito.internal.matchers.EqualityTest:org.mockito.internal.matchers.EqualsTest:org.mockito.internal.progress.MockingProgressImplTest:org.mockito.internal.stubbing.defaultanswers.ReturnsGenericDeepStubsTest:org.mockito.internal.verification.argumentmatching.ArgumentMatchingToolTest:org.mockito.internal.verification.checkers.AtLeastXNumberOfInvocationsCheckerTest:org.mockito.verification.TimeoutTest:org.mockitousage.PlaygroundWithDemoOfUnclonedParametersProblemTest:org.mockitousage.annotation.MockInjectionUsingConstructorTest$ATest:org.mockitousage.annotation.MockInjectionUsingConstructorTest:org.mockitousage.annotation.SpyAnnotationTest:org.mockitousage.basicapi.MocksSerializationForAnnotationTest:org.mockitousage.basicapi.MocksSerializationTest:org.mockitousage.basicapi.UsingVarargsTest:org.mockitousage.bugs.ActualInvocationHasNullArgumentNPEBugTest:org.mockitousage.bugs.ConcurrentModificationExceptionOnMultiThreadedVerificationTest:org.mockitousage.bugs.MultipleInOrdersTest:org.mockitousage.bugs.NPEWithCertainMatchersTest:org.mockitousage.bugs.ShouldAllowInlineMockCreationTest:org.mockitousage.bugs.SpyShouldHaveNiceNameTest:org.mockitousage.bugs.VerifyingWithAnExtraCallToADifferentMockTest:org.mockitousage.bugs.varargs.VarargsAndAnyObjectPicksUpExtraInvocationsTest:org.mockitousage.bugs.varargs.VarargsNotPlayingWithAnyObjectTest:org.mockitousage.configuration.CustomizedAnnotationForSmartMockTest:org.mockitousage.customization.BDDMockitoTest:org.mockitousage.debugging.InvocationListenerCallbackTest:org.mockitousage.debugging.PrintingInvocationsDetectsUnusedStubTest:org.mockitousage.debugging.PrintingInvocationsWhenEverythingOkTest:org.mockitousage.debugging.PrintingInvocationsWhenStubNotUsedTest:org.mockitousage.debugging.VerboseLoggingOfInvocationsOnMockTest:org.mockitousage.examples.use.ExampleTest:org.mockitousage.junitrunner.JUnit44RunnerTest:org.mockitousage.junitrunner.JUnit45RunnerTest:org.mockitousage.matchers.CapturingArgumentsTest:org.mockitousage.matchers.GenericMatchersTest:org.mockitousage.matchers.MatchersTest:org.mockitousage.matchers.VerificationAndStubbingUsingMatchersTest:org.mockitousage.puzzlers.BridgeMethodPuzzleTest:org.mockitousage.puzzlers.OverloadingPuzzleTest:org.mockitousage.serialization.DeepStubsSerializableTest:org.mockitousage.spies.SpyingOnInterfacesTest:org.mockitousage.spies.SpyingOnRealObjectsTest:org.mockitousage.stacktrace.ClickableStackTracesTest:org.mockitousage.stacktrace.PointingStackTraceToActualInvocationChunkInOrderTest:org.mockitousage.stacktrace.PointingStackTraceToActualInvocationInOrderTest:org.mockitousage.stacktrace.PointingStackTraceToActualInvocationTest:org.mockitousage.stacktrace.StackTraceFilteringTest:org.mockitousage.stubbing.BasicStubbingTest:org.mockitousage.stubbing.CloningParameterTest:org.mockitousage.stubbing.DeepStubbingTest:org.mockitousage.stubbing.DeprecatedStubbingTest:org.mockitousage.stubbing.StubbingUsingDoReturnTest:org.mockitousage.stubbing.StubbingWithThrowablesTest:org.mockitousage.verification.AtLeastXVerificationTest:org.mockitousage.verification.BasicVerificationInOrderTest:org.mockitousage.verification.BasicVerificationTest:org.mockitousage.verification.DescriptiveMessagesOnVerificationInOrderErrorsTest:org.mockitousage.verification.DescriptiveMessagesWhenVerificationFailsTest:org.mockitousage.verification.ExactNumberOfTimesVerificationTest:org.mockitousage.verification.FindingRedundantInvocationsInOrderTest:org.mockitousage.verification.NoMoreInteractionsVerificationTest:org.mockitousage.verification.OnlyVerificationTest:org.mockitousage.verification.PrintingVerboseTypesWithArgumentsTest:org.mockitousage.verification.RelaxedVerificationInOrderTest:org.mockitousage.verification.SelectedMocksInOrderVerificationTest:org.mockitousage.verification.VerificationExcludingStubsTest:org.mockitousage.verification.VerificationInOrderMixedWithOrdiraryVerificationTest:org.mockitousage.verification.VerificationInOrderTest:org.mockitousage.verification.VerificationInOrderWithCallsTest:org.mockitousage.verification.VerificationOnMultipleMocksUsingMatchersTest:org.mockitousage.verification.VerificationUsingMatchersTest:]
*/
		int id = 22;
		testMockito(id);
	}
	
	public void testMockito(int id) throws Exception{
		FileLauncher l = new FileLauncher();
		
	
		System.out.println("##Testing "+29);
		String location = new File(exampleRoot + "/mockito_"+id).getAbsolutePath();
		
		
		String[] args = new String[] { "-mode", "jgenprog", 
				//
				"-location", location, "-flthreshold", "0.1", 
				//
				"-seed", "1", "-maxgen", "50", "-stopfirst", "true", "-maxtime", "100", 
				//
				"-scope", "package",
				
				"-loglevel",Level.ERROR.toString(),
				//"
		};
	
		
		String sharedlibs = "/Users/matias/develop/defects4j/framework/projects/Mockito/lib/";

		File f = new File(location+File.separator+"lib");
		List<String> otherlibs = getParticularIssueLibs(f);
		System.out.println("libs from projects: "+otherlibs);
		
		String[] command = l.getCommand(args, new File(dataDir.getAbsolutePath()+"/mockito.json"), id, sharedlibs,location,otherlibs);
		System.out.println(Arrays.toString(args));

		AstorMain main1 = new AstorMain();

		CommandSummary cs = new CommandSummary(command);
		cs.command.put("-javacompliancelevel", "6");
		cs.command.put("-loglevel", "DEBUG");
		main1.execute(cs.flat());

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());
	}
}
