package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.junit.Ignore;
import org.junit.Test;

import fr.inria.astor.approaches.extensions.rt.RtEngine;
import fr.inria.astor.approaches.extensions.rt.RtEngine.AsAssertion;
import fr.inria.astor.approaches.extensions.rt.RtEngine.Helper;
import fr.inria.astor.approaches.extensions.rt.RtEngine.TestInspectionResult;
import fr.inria.astor.approaches.extensions.rt.RtEngine.TestRottenAnalysisResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.code.CtInvocation;

/**
 * 
 * @author Matias Martinez
 *
 */
public class RtTestExamples {

	public static Logger log = Logger.getLogger(Thread.currentThread().getName());

	// self
//	assert: rottenTestsFound rottenTests isEmpty
	@Test
	public void testRow01() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();

		assertNotNull(resultByTest);
		List<TestInspectionResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass()
						.equals("RTFRow01HelperExecutedAssertionExecutedContainsHelperContainsAssertion"))
				.collect(Collectors.toList());

		assertTrue(tc.isEmpty());
	}

//	self
//	assert: rottenTestsFound rottenTests size equals: 1;
//	assert: (rottenTestsFound rottenCompiledMethods anySatisfy: [ :m | 
//		m methodClass = RTFRow02HelperNotExecutedAssertionExecutedContainsHelperContainsAssertion
//			and: [ m selector = #test ] ])
	@Test
	public void testRow02() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass()
						.contains("RTFRow02HelperNotExecutedAssertionExecutedContainsHelperContainsAssertion"))
				.collect(Collectors.toList());

		assertFalse(tc.isEmpty());
		// self
		// assert: rottenTestsFound rottenTests size equals: 1;
		// assertEquals(tc.stream().map(e ->
		// e.getTestMethodFromClass()).collect(Collectors.toList()).toString(), 1,
		// tc.size());

		Optional<TestInspectionResult> rotten0OP = tc.stream()
				.filter(e -> e.getTestMethodFromClass().equals("test0") && e.isRotten()).findFirst();

		assertTrue(rotten0OP.isPresent());

		TestInspectionResult rottenTest0 = rotten0OP.get();

		assertEquals(0, rottenTest0.getClassificationAssert().getResultNotExecuted().size());
		assertEquals(1, rottenTest0.getClassificationHelperAssertion().getResultNotExecuted().size());
		Helper helperNotExecuted = rottenTest0.getClassificationHelperAssertion().getResultNotExecuted().get(0);
		assertEquals("org.junit.Assert.assertTrue(1 > 0)", helperNotExecuted.getAssertion().toString());

		assertEquals(1, helperNotExecuted.getCalls().size());
		assertEquals("this.goodHelper()", helperNotExecuted.getCalls().get(0).toString());

		assertEquals(0, rottenTest0.getClassificationHelperAssertion().getResultExecuted().size());

	}
//	{ #category : #tests }
//	RottenTestsFinderTestForPaper >> testRow03 [
//		| rottenTestsFound |
//		rottenTestsFound := RottenTestsFinder analyze: RTFRow03HelperExecutedAssertionNotExecutedContainsHelperContainsAssertion suite.
//		
//		self
//			assert: rottenTestsFound rottenTests size equals: 2;
//			assert: (rottenTestsFound rottenCompiledMethods anySatisfy: [ :m | 
//				m methodClass = RTFRow03HelperExecutedAssertionNotExecutedContainsHelperContainsAssertion
//					and: [ m selector = #test ] ]);
//			assert: (rottenTestsFound rottenCompiledMethods anySatisfy: [ :m | 
//				m methodClass = RTFAbstractTestCaseForPaper
//					and: [ m selector = #rottenHelper ] ])
//	]

	@Test
	public void testRow03() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass()
						.contains("RTFRow03HelperExecutedAssertionNotExecutedContainsHelperContainsAssertion"))
				.collect(Collectors.toList());

		assertFalse(tc.isEmpty());
		// self
		// assert: rottenTestsFound rottenTests size equals: 1;
		// assertEquals(tc.stream().map(e ->
		// e.getTestMethodFromClass()).collect(Collectors.toList()).toString(), 1,
		// tc.size());

		Optional<TestInspectionResult> rotten0OP = tc.stream()
				.filter(e -> e.getTestMethodFromClass().equals("test0") && e.isRotten()).findFirst();

		assertTrue(rotten0OP.isPresent());

		TestInspectionResult rottenTest0 = rotten0OP.get();

		assertEquals(1, rottenTest0.getClassificationAssert().getResultNotExecuted().size());

		CtInvocation assertionNotExecuted = rottenTest0.getClassificationAssert().getResultNotExecuted().get(0)
				.getCtAssertion();
		assertEquals("org.junit.Assert.assertTrue(3 > 1)", assertionNotExecuted.toString());

		assertEquals(1, rottenTest0.getClassificationHelperAssertion().getResultNotExecuted().size());
		Helper helperAssertionNotExec = rottenTest0.getClassificationHelperAssertion().getResultNotExecuted().get(0);
		assertEquals("org.junit.Assert.assertTrue(1 > 0)", helperAssertionNotExec.getAssertion().toString());
		assertEquals("this.rottenHelper()", helperAssertionNotExec.getCalls().get(0).toString());

		assertEquals(0, rottenTest0.getClassificationHelperAssertion().getResultExecuted().size());

	}

//
//	{ #category : #tests }
//	RottenTestsFinderTestForPaper >> testRow04 [
//		| rottenTestsFound |
//		rottenTestsFound := RottenTestsFinder analyze: RTFRow04HelperNotExecutedAssertionNotExecutedContainsHelperContainsAssertion suite.
//		
//		self
//			assert: rottenTestsFound rottenTests size equals: 1;
//			assert: (rottenTestsFound rottenCompiledMethods anySatisfy: [ :m | 
//				m methodClass = RTFRow04HelperNotExecutedAssertionNotExecutedContainsHelperContainsAssertion
//					and: [ m selector = #test ] ])
	@Test
	public void testRow04() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass()
						.contains("RTFRow04HelperNotExecutedAssertionNotExecutedContainsHelperContainsAssertion"))
				.collect(Collectors.toList());

		assertFalse(tc.isEmpty());
		// self
		// assert: rottenTestsFound rottenTests size equals: 1;
		// assertEquals(1, tc.size());

		Optional<TestInspectionResult> rotten0OP = tc.stream()
				.filter(e -> e.getTestMethodFromClass().equals("test0") && e.isRotten()).findFirst();

		assertTrue(rotten0OP.isPresent());

		TestInspectionResult rottenTest0 = rotten0OP.get();

		assertEquals(1, rottenTest0.getClassificationAssert().getResultNotExecuted().size());

		CtInvocation assertionNotExecuted = rottenTest0.getClassificationAssert().getResultNotExecuted().get(0)
				.getCtAssertion();
		assertEquals("org.junit.Assert.assertTrue(4 > 1)", assertionNotExecuted.toString());

		assertEquals(1, rottenTest0.getClassificationHelperAssertion().getResultNotExecuted().size());
		Helper helperNotExecuted = rottenTest0.getClassificationHelperAssertion().getResultNotExecuted().get(0);
		assertEquals("org.junit.Assert.assertTrue(1 > 0)", helperNotExecuted.getAssertion().toString());

		assertEquals(1, helperNotExecuted.getCalls().size());
		assertEquals("this.goodHelper()", helperNotExecuted.getCalls().get(0).toString());

		assertEquals(0, rottenTest0.getClassificationHelperAssertion().getResultExecuted().size());

	}
//	self
//	assert: rottenTestsFound rottenTests isEmpty

	@Test
	public void testRow05() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream().filter(e -> e.getNameOfTestClass().equals(
				"RottenTestsFinder.FakePaperTests.RTFRow05HelperExecutedAssertionExecutedContainsNoHelperContainsAssertion"))
				.collect(Collectors.toList());

		assertFalse(tc.isEmpty());
		// self
		// assert: rottenTestsFound rottenTests size equals: 1;
		// assertEquals(1, tc.size());

		Optional<TestInspectionResult> rotten0OP = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test0"))
				.findFirst();

		assertTrue(rotten0OP.isPresent());

		TestInspectionResult rottenTest0 = rotten0OP.get();
		assertFalse(rottenTest0.isRotten());
		assertFalse(rottenTest0.isSmokeTest());
		assertEquals(0, rottenTest0.getClassificationAssert().getResultNotExecuted().size());
		assertEquals(1, rottenTest0.getClassificationAssert().getResultExecuted().size());

		assertEquals(0, rottenTest0.getClassificationHelperAssertion().getResultNotExecuted().size());
		assertEquals(0, rottenTest0.getClassificationHelperCall().getResultNotExecuted().size());

		assertEquals(1, rottenTest0.getClassificationHelperAssertion().getResultExecuted().size());
		assertEquals(1, rottenTest0.getClassificationHelperCall().getResultExecuted().size());

	}

	@Test
	public void testRow05_perform() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass()
						.contains("RTFRow05HelperExecutedAssertionExecutedContainsNoHelperContainsAssertionPerform"))
				.collect(Collectors.toList());

		assertFalse(tc.isEmpty());
		// self
		// assert: rottenTestsFound rottenTests size equals: 1;
		// assertEquals(1, tc.size());
		Optional<TestInspectionResult> rotten0OP = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test0"))
				.findFirst();

		assertTrue(rotten0OP.isPresent());

		TestInspectionResult rottenTest0 = rotten0OP.get();

		assertFalse(rottenTest0.isRotten());
		assertFalse(rottenTest0.isSmokeTest());
		assertEquals(0, rottenTest0.getClassificationAssert().getResultNotExecuted().size());
		assertEquals(1, rottenTest0.getClassificationAssert().getResultExecuted().size());

		assertEquals(0, rottenTest0.getClassificationHelperAssertion().getResultNotExecuted().size());
		assertEquals(0, rottenTest0.getClassificationHelperCall().getResultNotExecuted().size());
//The helper is not explicitly written in the code.
		assertEquals(0, rottenTest0.getClassificationHelperAssertion().getResultExecuted().size());
		assertEquals(0, rottenTest0.getClassificationHelperCall().getResultExecuted().size());

	}

//self
//	assert: rottenTestsFound rottenTests isEmpty
	@Test
	public void testRow06() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass()
						.contains("RTFRow06HelperNotExecutedAssertionExecutedContainsNoHelperContainsAssertion"))
				.collect(Collectors.toList());

		assertFalse(tc.isEmpty());
		// self
		// assert: rottenTestsFound rottenTests size equals: 1;
		// assertEquals(1, tc.size());

		Optional<TestInspectionResult> rotten0OP = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test0"))
				.findFirst();

		assertTrue(rotten0OP.isPresent());

		TestInspectionResult norotten = rotten0OP.get();

		assertFalse(norotten.isRotten());

		// For the moment we dont store the information if it's not rotten
		// CtInvocation assertionNotExecuted =
		// norotten.getClassificationAssert().getResultNotExecuted().get(0);
		// assertEquals("org.junit.Assert.assertTrue((6 > 1))",
		// assertionNotExecuted.toString());

	}

	// self
//	assert: rottenTestsFound rottenTests size equals: 1;
//	assert: (rottenTestsFound rottenCompiledMethods anySatisfy: [ :m | 
//		m methodClass = RTFRow07HelperExecutedAssertionNotExecutedContainsNoHelperContainsAssertion
	// and: [ m selector = #test ] ])

	@Test
	public void testRow07() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass()
						.contains("RTFRow07HelperExecutedAssertionNotExecutedContainsNoHelperContainsAssertion"))
				.collect(Collectors.toList());

		assertFalse(tc.isEmpty());
		// self
		// assert: rottenTestsFound rottenTests size equals: 1;
		// assertEquals(1, tc.size());

		Optional<TestInspectionResult> rotten0OP = tc.stream()
				.filter(e -> e.getTestMethodFromClass().equals("test0") && e.isRotten()).findFirst();

		assertTrue(rotten0OP.isPresent());

		TestInspectionResult rottenTest0 = rotten0OP.get();

		assertEquals(1, rottenTest0.getClassificationAssert().getResultNotExecuted().size());

		CtInvocation assertionNotExecuted = rottenTest0.getClassificationAssert().getResultNotExecuted().get(0)
				.getCtAssertion();
		assertEquals("org.junit.Assert.assertTrue(7 > 1)", assertionNotExecuted.toString());

		assertEquals(0, rottenTest0.getClassificationHelperAssertion().getResultNotExecuted().size());
		assertEquals(0, rottenTest0.getClassificationHelperAssertion().getResultExecuted().size());

	}

//	self
//	assert: rottenTestsFound rottenTests size equals: 1;
//	assert: (rottenTestsFound rottenCompiledMethods anySatisfy: [ :m | 
//		m methodClass = RTFRow08HelperNotExecutedAssertionNotExecutedContainsNoHelperContainsAssertion
//			and: [ m selector = #test ] ])

	@Test
	@Ignore
	public void testRow08() throws Exception {
		RtEngine etEn = detectRt();
		System.out.println("Running case 8");
		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass()
						.contains("RTFRow08HelperNotExecutedAssertionNotExecutedContainsNoHelperContainsAssertion"))
				.collect(Collectors.toList());

		assertNotNull("any test found", tc);

		assertFalse(tc.stream().map(e -> e.getTestMethodFromClass()).collect(Collectors.toList()).toString(),
				tc.isEmpty());
		// self
		// assert: rottenTestsFound rottenTests size equals: 1;
		// assertEquals(1, tc.size());

		Optional<TestInspectionResult> rotten0OP = tc.stream()
				.filter(e -> e.getTestMethodFromClass().equals("test0") && e.isRotten()).findFirst();

		assertTrue(rotten0OP.isPresent());

		TestInspectionResult rottenTest0 = rotten0OP.get();

		assertEquals(1, rottenTest0.getClassificationAssert().getResultNotExecuted().size());

		CtInvocation assertionNotExecuted = rottenTest0.getClassificationAssert().getResultNotExecuted().get(0)
				.getCtAssertion();
		assertEquals("junit.framework.Assert.assertTrue((8 > 1))", assertionNotExecuted.toString());

		assertEquals(0, rottenTest0.getClassificationHelperAssertion().getResultNotExecuted().size());
		assertEquals(0, rottenTest0.getClassificationHelperAssertion().getResultExecuted().size());

	}

	@Test
	public void testRow09() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass()
						.contains("RTFRow09HelperExecutedAssertionExecutedContainsHelperContainsNoAssertion"))
				.collect(Collectors.toList());

		assertFalse(tc.isEmpty());

		// assertEquals(1, tc.size());

		Optional<TestInspectionResult> rotten0OP = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test0"))
				.findFirst();

		assertTrue(rotten0OP.isPresent());

		TestInspectionResult rottenTest0 = rotten0OP.get();

		assertFalse(rottenTest0.isRotten());
		assertEquals(0, rottenTest0.getClassificationAssert().getResultNotExecuted().size());
		assertEquals(0, rottenTest0.getClassificationHelperAssertion().getResultNotExecuted().size());
		assertEquals(1, rottenTest0.getClassificationHelperAssertion().getResultExecuted().size());

		Helper helperAssertionExecuted = rottenTest0.getClassificationHelperAssertion().getResultExecuted().get(0);
		assertEquals("org.junit.Assert.assertTrue(1 > 0)", helperAssertionExecuted.getAssertion().toString());
		// Let's check the caller of the assertion
		assertEquals(1, helperAssertionExecuted.getCalls().size());
		assertEquals("this.goodHelper()", helperAssertionExecuted.getCalls().get(0).toString());

	}

	// assert: rottenTestsFound rottenTests size equals: 1;
	// assert: (rottenTestsFound rottenCompiledMethods anySatisfy: [ :m |
	// m methodClass =
	// RTFRow10HelperNotExecutedAssertionExecutedContainsHelperContainsNoAssertion
	// and: [ m selector = #test ] ])
	@Test
	public void testRow10() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass()
						.contains("RTFRow10HelperNotExecutedAssertionExecutedContainsHelperContainsNoAssertion"))
				.collect(Collectors.toList());

		assertFalse(tc.isEmpty());

		// assertEquals(1, tc.size());

		Optional<TestInspectionResult> rotten0OP = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test0"))
				.findFirst();

		assertTrue(rotten0OP.isPresent());

		TestInspectionResult rottenTest0 = rotten0OP.get();
		assertTrue(rottenTest0.isRotten());

		// Not assertion found in code (even it's executed via 'perform')
		assertEquals(0, rottenTest0.getClassificationAssert().getResultNotExecuted().size());

		assertEquals(1, rottenTest0.getClassificationHelperAssertion().getResultNotExecuted().size());

		Helper helperAssertionNotExecuted = rottenTest0.getClassificationHelperAssertion().getResultNotExecuted()
				.get(0);
		assertEquals("org.junit.Assert.assertTrue(1 > 0)", helperAssertionNotExecuted.getAssertion().toString());
		// Let's check the caller of the assertion
		assertEquals(1, helperAssertionNotExecuted.getCalls().size());
		assertEquals("this.goodHelper()", helperAssertionNotExecuted.getCalls().get(0).toString());

		assertEquals(0, rottenTest0.getClassificationHelperAssertion().getResultExecuted().size());

	}

//	self
//	assert: rottenTestsFound rottenTests size equals: 1;
//	assert: (rottenTestsFound rottenCompiledMethods anySatisfy: [ :m | 
//		m methodClass = RTFAbstractTestCaseForPaper
//			and: [ m selector = #rottenHelper ] ])
//]
	@Test
	public void testRow011() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass()
						.contains("RTFRow11HelperExecutedAssertionNotExecutedContainsHelperContainsNoAssertion"))
				.collect(Collectors.toList());

		assertFalse(tc.isEmpty());
		// self
		// assert: rottenTestsFound rottenTests size equals: 1;
		// assertEquals(1, tc.size());

		Optional<TestInspectionResult> rotten0OP = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test0"))
				.findFirst();

		assertTrue(rotten0OP.isPresent());

		TestInspectionResult rottenTest0 = rotten0OP.get();

		assertEquals(0, rottenTest0.getClassificationAssert().getResultNotExecuted().size());
		assertEquals(0, rottenTest0.getClassificationHelperAssertion().getResultExecuted().size());
		assertEquals(1, rottenTest0.getClassificationHelperCall().getResultExecuted().size());
		Helper helperAssertionNotExecuted = rottenTest0.getClassificationHelperAssertion().getResultNotExecuted()
				.get(0);
		assertEquals("org.junit.Assert.assertTrue(1 > 0)", helperAssertionNotExecuted.getAssertion().toString());

		// Let's check who calls the assertion
		assertEquals(1, helperAssertionNotExecuted.getCalls().size());
		assertEquals("this.rottenHelper()", helperAssertionNotExecuted.getCalls().get(0).toString());

		// Now, let's check that this helper was called
		assertEquals(1, rottenTest0.getClassificationHelperCall().getResultExecuted().size());
		Helper helperExecuted = rottenTest0.getClassificationHelperCall().getResultExecuted().get(0);
		assertEquals("this.rottenHelper()", helperExecuted.getCalls().get(0).toString());

	}

	// RTFRow12HelperNotExecutedAssertionNotExecutedContainsHelperContainsNoAssertion
	// assert: rottenTestsFound rottenTests size equals: 1;
//	assert: (rottenTestsFound rottenCompiledMethods anySatisfy: [ :m | 
//	        			m methodClass = RTFRow12HelperNotExecutedAssertionNotExecutedContainsHelperContainsNoAssertion
//	                   				and: [ m selector = #test ] ])
	//
	@Test
	public void testRow012() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass()
						.contains("RTFRow12HelperNotExecutedAssertionNotExecutedContainsHelperContainsNoAssertion"))
				.collect(Collectors.toList());

		assertFalse(tc.isEmpty());
		// self
		// assert: rottenTestsFound rottenTests size equals: 1;
		// assertEquals(1, tc.size());

		Optional<TestInspectionResult> rotten0OP = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test0"))
				.findFirst();

		assertTrue(rotten0OP.isPresent());

		TestInspectionResult rottenTest0 = rotten0OP.get();

		assertEquals(0, rottenTest0.getClassificationAssert().getResultNotExecuted().size());
		assertEquals(0, rottenTest0.getClassificationHelperAssertion().getResultExecuted().size());
		assertEquals(0, rottenTest0.getClassificationHelperCall().getResultExecuted().size());
		Helper helperAssertionNotExecuted = rottenTest0.getClassificationHelperAssertion().getResultNotExecuted()
				.get(0);
		assertEquals("org.junit.Assert.assertTrue(1 > 0)", helperAssertionNotExecuted.getAssertion().toString());
		// Let's check the caller of the assertion
		assertEquals(1, helperAssertionNotExecuted.getCalls().size());
		assertEquals("this.goodHelper()", helperAssertionNotExecuted.getCalls().get(0).toString());

		// Now, check the caller is not invoked
		Helper helperNotExecuted = rottenTest0.getClassificationHelperAssertion().getResultNotExecuted().get(0);
		assertEquals("this.goodHelper()", helperNotExecuted.getCalls().get(0).toString());

	}

	// { #category : #tests }
	// self
	// assert: rottenTestsFound rottenTests isEmpty
	@Test
	public void testRow013() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass()
						.contains("RTFRow13HelperExecutedAssertionExecutedContainsNoHelperContainsNoAssertion"))
				.collect(Collectors.toList());

		assertFalse(tc.isEmpty());
		// self
		// assert: rottenTestsFound rottenTests size equals: 1;
		// assertEquals(1, tc.size());

		Optional<TestInspectionResult> rotten0OP = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test0"))
				.findFirst();

		assertTrue(rotten0OP.isPresent());

		TestInspectionResult rottenTest0 = rotten0OP.get();
		// TODO: must be detected as rotten?
		assertFalse(rottenTest0.isRotten());

		checkIsEmpty(rottenTest0);

	}

	// { #category : #tests }
	// self
	// assert: rottenTestsFound rottenTests isEmpty
	@Test
	public void testRow014() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass()
						.contains("RTFRow14HelperNotExecutedAssertionExecutedContainsNoHelperContainsNoAssertion"))
				.collect(Collectors.toList());

		assertFalse(tc.isEmpty());
		// self
		// assert: rottenTestsFound rottenTests size equals: 1;
		// assertEquals(1, tc.size());

		Optional<TestInspectionResult> rotten0OP = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test0"))
				.findFirst();

		assertTrue(rotten0OP.isPresent());

		TestInspectionResult rottenTest0 = rotten0OP.get();
		// TODO: must be detected as rotten?
		assertFalse(rottenTest0.isRotten());
		assertTrue(rottenTest0.isSmokeTest());

		checkIsEmpty(rottenTest0);

	}

//	self
	// assert: rottenTestsFound rottenTests isEmpty
	@Test
	public void testRow015() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass()
						.contains("RTFRow15HelperExecutedAssertionNotExecutedContainsNoHelperContainsNoAssertion"))
				.collect(Collectors.toList());

		assertFalse(tc.isEmpty());
		// self
		// assert: rottenTestsFound rottenTests size equals: 1;
		// assertEquals(2, tc.size());

		Optional<TestInspectionResult> rotten0OP = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test0"))
				.findFirst();

		assertTrue(rotten0OP.isPresent());

		TestInspectionResult rottenTest0 = rotten0OP.get();
		// TODO: must be detected as rotten?
		assertFalse(rottenTest0.isRotten());
		assertTrue(rottenTest0.isSmokeTest());

		checkIsEmpty(rottenTest0);

		Optional<TestInspectionResult> rotten1OP = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test2"))
				.findFirst();

		assertTrue(rotten0OP.isPresent());

		TestInspectionResult rottenTest1 = rotten1OP.get();
		// TODO: must be detected as rotten?
		assertFalse(rottenTest1.isRotten());
		assertTrue(rottenTest1.isSmokeTest());

		checkIsEmpty(rottenTest1);

	}

	public void checkIsEmpty(TestInspectionResult rottenTest0) {
		assertEquals(0, rottenTest0.getClassificationAssert().getResultNotExecuted().size());
		assertEquals(0, rottenTest0.getClassificationHelperAssertion().getResultExecuted().size());
		assertEquals(0, rottenTest0.getClassificationHelperCall().getResultExecuted().size());
		assertEquals(0, rottenTest0.getClassificationHelperAssertion().getResultNotExecuted().size());
		assertEquals(0, rottenTest0.getClassificationHelperCall().getResultNotExecuted().size());
	}

	// self
//	assert: rottenTestsFound rottenTests isEmpty
//]

	@Test
	public void testRow016() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass()
						.contains("RTFRow16HelperNotExecutedAssertionNotExecutedContainsNoHelperContainsNoAssertion"))
				.collect(Collectors.toList());

		assertFalse(tc.isEmpty());
		// self
		// assert: rottenTestsFound rottenTests size equals: 1;
		// assertEquals(2, tc.size());

		List<TestInspectionResult> rottens = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test0"))
				.collect(Collectors.toList());

		assertFalse(rottens.isEmpty());

		TestInspectionResult rottenTest0 = rottens.get(0);
		assertTrue(rottenTest0.isSmokeTest());
		assertEquals(0, rottenTest0.getClassificationAssert().getResultNotExecuted().size());
		assertEquals(0, rottenTest0.getClassificationHelperAssertion().getResultExecuted().size());
		assertEquals(0, rottenTest0.getClassificationHelperCall().getResultExecuted().size());

//now check the second test
		rottens = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test2")).collect(Collectors.toList());

		assertFalse(rottens.isEmpty());

		TestInspectionResult rottenTest2 = rottens.get(0);
		assertTrue(rottenTest2.isSmokeTest());
		assertEquals(0, rottenTest2.getClassificationAssert().getResultNotExecuted().size());
		assertEquals(0, rottenTest2.getClassificationHelperAssertion().getResultExecuted().size());
		assertEquals(0, rottenTest2.getClassificationHelperCall().getResultExecuted().size());

	}

	@Test
	public void testRow017() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream().filter(e -> e.getNameOfTestClass().contains("RTFRow017"))
				.collect(Collectors.toList());

		assertFalse(tc.isEmpty());

		checkFp(tc, true, "test0");

		checkFp(tc, false, "test1");

		checkFp(tc, true, "test2");

		checkFp(tc, false, "test3");

		checkFpHelper(tc, true, "test4");
	}

	@Test
	public void testRow018() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream().filter(e -> e.getNameOfTestClass().contains("RTFRow018"))
				.collect(Collectors.toList());

		tc = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test0")).collect(Collectors.toList());

		assertFalse(tc.isEmpty());

		TestInspectionResult rottenTest0 = tc.get(0);
		assertFalse(rottenTest0.isSmokeTest());

		TestRottenAnalysisResult res = rottenTest0.generateFinalResult();

		assertNotNull(res);

		assertTrue(res.getFullRotten().size() > 0);

		assertTrue(res.contextAssertion.isEmpty());
		assertTrue(res.contextHelperAssertion.isEmpty());
		assertTrue(res.contextHelperCall.isEmpty());
	}

	@Test
	public void testRow019() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream().filter(e -> e.getNameOfTestClass().contains("RTFRow19"))
				.collect(Collectors.toList());

		assertFalse(tc.isEmpty());

		Optional<TestInspectionResult> rotten0OP = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test0"))
				.findFirst();

		assertTrue(rotten0OP.isPresent());

		TestInspectionResult rottenTest0 = rotten0OP.get();

		assertFalse(rottenTest0.isSmokeTest());
		assertFalse(rottenTest0.isRotten());

		assertTrue(rottenTest0.getAllExpectedExceptionFromTest().size() > 0);
		assertTrue(rottenTest0.getExpectException().isEmpty());

	}

	@Test
	public void testRow21() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream().filter(e -> e.getNameOfTestClass().contains("RTFRow21"))
				.collect(Collectors.toList());

		assertFalse(tc.isEmpty());

		Optional<TestInspectionResult> rotten0OP = tc.stream()
				.filter(e -> e.getTestMethodFromClass().equals("testPrimeNumberChecker")).findFirst();

		assertTrue(rotten0OP.isPresent());

		TestInspectionResult rottenTest0 = rotten0OP.get();

		assertFalse(rottenTest0.isSmokeTest());
		assertTrue(rottenTest0.isRotten());

		assertTrue(rottenTest0.getExpectException().isEmpty());

		assertEquals(1, rottenTest0.getClassificationAssert().getResultNotExecuted().size());

		// Second test
		Optional<TestInspectionResult> rotten2P = tc.stream()
				.filter(e -> e.getTestMethodFromClass().equals("testPrimeNumberChecker2")).findFirst();

		assertTrue(rotten2P.isPresent());

		TestInspectionResult rottenTest2 = rotten2P.get();

		assertFalse(rottenTest2.isSmokeTest());
		assertFalse(rottenTest2.isRotten());

		assertTrue(rottenTest2.getExpectException().isEmpty());

		TestRottenAnalysisResult fr2 = rottenTest2.generateFinalResult();

		assertTrue(fr2.getFullRotten().isEmpty());
		assertTrue(fr2.contextAssertion.isEmpty());
		assertTrue(fr2.contextHelperAssertion.isEmpty());
		assertTrue(fr2.contextHelperCall.isEmpty());
		assertTrue(fr2.missedFail.isEmpty());
		assertTrue(fr2.skip.isEmpty());
	}

	@Test
	public void testRow21_Param_RT() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream().filter(e -> e.getNameOfTestClass().contains("RTFRow22"))
				.collect(Collectors.toList());

		// Second test
		Optional<TestInspectionResult> rotten2P = tc.stream()
				.filter(e -> e.getTestMethodFromClass().equals("testPrimeNumberChecker2")).findFirst();

		assertTrue(rotten2P.isPresent());

		TestInspectionResult rottenTest2 = rotten2P.get();

		assertFalse(rottenTest2.isSmokeTest());
		assertTrue(rottenTest2.isRotten());

		assertTrue(rottenTest2.getExpectException().isEmpty());

		TestRottenAnalysisResult fr2 = rottenTest2.generateFinalResult();

		assertTrue(fr2.getFullRotten().isEmpty());
		assertFalse(fr2.contextAssertion.isEmpty());
		assertTrue(fr2.contextHelperAssertion.isEmpty());
		assertTrue(fr2.contextHelperCall.isEmpty());
		assertTrue(fr2.missedFail.isEmpty());
		assertTrue(fr2.skip.isEmpty());
	}

	@Test
	public void testRow23_missing() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass().contains("RTFRow23MissingFail")).collect(Collectors.toList());

		// Second test
		Optional<TestInspectionResult> rotten2P = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test0"))
				.findFirst();

		assertTrue(rotten2P.isPresent());

		TestInspectionResult rottenTest2 = rotten2P.get();

		assertFalse(rottenTest2.isSmokeTest());
		assertTrue(rottenTest2.isRotten());

		assertTrue(rottenTest2.getExpectException().isEmpty());

		TestRottenAnalysisResult fr2 = rottenTest2.generateFinalResult();

		assertTrue(fr2.getFullRotten().isEmpty());
		assertTrue(fr2.contextAssertion.isEmpty());
		assertTrue(fr2.contextHelperAssertion.isEmpty());
		assertTrue(fr2.contextHelperCall.isEmpty());
		assertFalse(fr2.missedFail.isEmpty());
		assertTrue(fr2.skip.isEmpty());
		assertTrue(fr2.redundantAssertion.isEmpty());

	}

	@Test
	public void testRow24_RedundantAssertion() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass().contains("RTFRow24Redundant")).collect(Collectors.toList());

		// Second test
		Optional<TestInspectionResult> rotten2P = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test0"))
				.findFirst();

		assertTrue(rotten2P.isPresent());

		TestInspectionResult rottenTest2 = rotten2P.get();

		assertFalse(rottenTest2.isSmokeTest());
		assertFalse(rottenTest2.isRotten());

		assertTrue(rottenTest2.getExpectException().isEmpty());

		TestRottenAnalysisResult fr2 = rottenTest2.generateFinalResult();

		assertTrue(fr2.getFullRotten().isEmpty());
		assertTrue(fr2.contextAssertion.isEmpty());
		assertTrue(fr2.contextHelperAssertion.isEmpty());
		assertTrue(fr2.contextHelperCall.isEmpty());
		assertTrue(fr2.missedFail.isEmpty());
		assertTrue(fr2.skip.isEmpty());
		assertFalse(fr2.redundantAssertion.isEmpty());

		///

		// second case
		rotten2P = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test1")).findFirst();

		assertTrue(rotten2P.isPresent());

		rottenTest2 = rotten2P.get();

		assertFalse(rottenTest2.isSmokeTest());
		assertFalse(rottenTest2.isRotten());

		assertTrue(rottenTest2.getExpectException().isEmpty());

		fr2 = rottenTest2.generateFinalResult();

		assertTrue(fr2.getFullRotten().isEmpty());
		assertTrue(fr2.contextAssertion.isEmpty());
		assertTrue(fr2.contextHelperAssertion.isEmpty());
		assertTrue(fr2.contextHelperCall.isEmpty());
		assertTrue(fr2.missedFail.isEmpty());
		assertTrue(fr2.skip.isEmpty());
		assertFalse(fr2.redundantAssertion.isEmpty());
		///

		rotten2P = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test2")).findFirst();

		assertTrue(rotten2P.isPresent());

		rottenTest2 = rotten2P.get();

		assertFalse(rottenTest2.isSmokeTest());
		assertFalse(rottenTest2.isRotten());

		assertTrue(rottenTest2.getExpectException().isEmpty());

		fr2 = rottenTest2.generateFinalResult();

		assertTrue(fr2.getFullRotten().isEmpty());
		assertTrue(fr2.contextAssertion.isEmpty());
		assertTrue(fr2.contextHelperAssertion.isEmpty());
		assertTrue(fr2.contextHelperCall.isEmpty());
		assertTrue(fr2.missedFail.isEmpty());
		assertTrue(fr2.skip.isEmpty());
		assertFalse(fr2.redundantAssertion.isEmpty());

		///
		rotten2P = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test3")).findFirst();

		assertTrue(rotten2P.isPresent());

		rottenTest2 = rotten2P.get();

		assertFalse(rottenTest2.isSmokeTest());
		assertFalse(rottenTest2.isRotten());

		assertTrue(rottenTest2.getExpectException().isEmpty());

		fr2 = rottenTest2.generateFinalResult();

		assertTrue(fr2.getFullRotten().isEmpty());
		assertTrue(fr2.contextAssertion.isEmpty());
		assertTrue(fr2.contextHelperAssertion.isEmpty());
		assertTrue(fr2.contextHelperCall.isEmpty());
		assertTrue(fr2.missedFail.isEmpty());
		assertTrue(fr2.skip.isEmpty());
		assertTrue(fr2.redundantAssertion.isEmpty());
	}

	@Test
	public void testRTFRow25Exception() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass().contains("RTFRow25Exception")).collect(Collectors.toList());

		// Second test
		Optional<TestInspectionResult> rotten2P = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test0"))
				.findFirst();

		assertTrue(rotten2P.isPresent());

		TestInspectionResult rottenTest2 = rotten2P.get();

		assertFalse(rottenTest2.isSmokeTest());
		assertFalse(rottenTest2.isRotten());

		// we expect exceptions
		assertFalse(rottenTest2.getExpectException().isEmpty());

		TestRottenAnalysisResult fr2 = rottenTest2.generateFinalResult();

		assertTrue(fr2.getFullRotten().isEmpty());
		assertTrue(fr2.contextAssertion.isEmpty());
		assertTrue(fr2.contextHelperAssertion.isEmpty());
		assertTrue(fr2.contextHelperCall.isEmpty());
		assertTrue(fr2.missedFail.isEmpty());
		assertTrue(fr2.skip.isEmpty());
		assertTrue(fr2.redundantAssertion.isEmpty());

		/// Case 2
		rotten2P = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test1")).findFirst();

		assertTrue(rotten2P.isPresent());

		rottenTest2 = rotten2P.get();
		// TODO: For the moment is the test expect exception is not Smoke test
		assertFalse(rottenTest2.isSmokeTest());
		assertFalse(rottenTest2.isRotten());

		fr2 = rottenTest2.generateFinalResult();

		assertTrue(fr2.getFullRotten().isEmpty());
		assertTrue(fr2.contextAssertion.isEmpty());
		assertTrue(fr2.contextHelperAssertion.isEmpty());
		assertTrue(fr2.contextHelperCall.isEmpty());
		assertTrue(fr2.missedFail.isEmpty());
		assertTrue(fr2.skip.isEmpty());
		assertTrue(fr2.redundantAssertion.isEmpty());
		//
		assertTrue(rottenTest2.hasFailInvocation());
		assertTrue(rottenTest2.hasTryCatch());
		assertTrue(rottenTest2.isExceptionExpected());
		assertTrue(rottenTest2.getExpectException().isEmpty());

		/// Case 2
		rotten2P = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test2")).findFirst();

		assertTrue(rotten2P.isPresent());

		rottenTest2 = rotten2P.get();

		assertFalse(rottenTest2.isSmokeTest());
		assertFalse(rottenTest2.isRotten());

		fr2 = rottenTest2.generateFinalResult();

		assertTrue(fr2.getFullRotten().isEmpty());
		assertTrue(fr2.contextAssertion.isEmpty());
		assertTrue(fr2.contextHelperAssertion.isEmpty());
		assertTrue(fr2.contextHelperCall.isEmpty());
		assertTrue(fr2.missedFail.isEmpty());
		assertTrue(fr2.skip.isEmpty());
		assertTrue(fr2.redundantAssertion.isEmpty());
		//
		assertFalse(rottenTest2.hasFailInvocation());
		assertFalse(rottenTest2.hasTryCatch());
		assertFalse(rottenTest2.isExceptionExpected());
		assertTrue(rottenTest2.getExpectException().isEmpty());

	}

	@Test
	public void testRTFRow25Exception2() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass().contains("RTFRow25Exception2")).collect(Collectors.toList());

		// Second test
		Optional<TestInspectionResult> rotten0 = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test0"))
				.findFirst();

		assertTrue(rotten0.isPresent());

		TestInspectionResult rottenTest0 = rotten0.get();

		assertFalse(rottenTest0.isSmokeTest());
		// Must be rotten, the assertion has logic
		assertTrue(rottenTest0.isRotten());

		// we expect exceptions
		assertFalse(rottenTest0.getExpectException().isEmpty());

		TestRottenAnalysisResult fr0 = rottenTest0.generateFinalResult();

		// We have values
		assertFalse(fr0.getFullRotten().isEmpty());
		assertTrue(fr0.contextAssertion.isEmpty());
		assertTrue(fr0.contextHelperAssertion.isEmpty());
		assertTrue(fr0.contextHelperCall.isEmpty());
		assertTrue(fr0.missedFail.isEmpty());
		assertTrue(fr0.skip.isEmpty());
		assertTrue(fr0.redundantAssertion.isEmpty());

		//// Case two: missedfail

		// Second test
		Optional<TestInspectionResult> rotten1 = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test1"))
				.findFirst();

		assertTrue(rotten1.isPresent());

		TestInspectionResult rottenTest1 = rotten1.get();

		assertFalse(rottenTest1.isSmokeTest());
		assertTrue(rottenTest1.getAllMissedFailFromTest().getAll().size() > 0);

		// Must be rotten, assertion is missed fail
		assertTrue(rottenTest1.isRotten());

		// we expect exceptions
		assertFalse(rottenTest1.getExpectException().isEmpty());

		TestRottenAnalysisResult fr1 = rottenTest1.generateFinalResult();

		assertTrue(fr1.getFullRotten().isEmpty());
		assertTrue(fr1.contextAssertion.isEmpty());
		assertTrue(fr1.contextHelperAssertion.isEmpty());
		assertTrue(fr1.contextHelperCall.isEmpty());
		assertFalse(fr1.missedFail.isEmpty());
		assertTrue(fr1.skip.isEmpty());
		assertTrue(fr1.redundantAssertion.isEmpty());

		//// Case 3: failt

		// Second test
		Optional<TestInspectionResult> rotten2 = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test2"))
				.findFirst();

		assertTrue(rotten2.isPresent());

		TestInspectionResult rottenTest2 = rotten2.get();

		assertFalse(rottenTest2.isSmokeTest());
		assertTrue(rottenTest2.getAllMissedFailFromTest().getAll().isEmpty());

		// Must Not be rotten, is fail
		assertFalse(rottenTest2.isRotten());

		// we expect exceptions
		assertFalse(rottenTest2.getExpectException().isEmpty());

		TestRottenAnalysisResult fr2 = rottenTest2.generateFinalResult();

		assertTrue(fr2.getFullRotten().isEmpty());
		assertTrue(fr2.contextAssertion.isEmpty());
		assertTrue(fr2.contextHelperAssertion.isEmpty());
		assertTrue(fr2.contextHelperCall.isEmpty());
		assertTrue(fr2.missedFail.isEmpty());
		assertTrue(fr2.skip.isEmpty());
		assertTrue(fr2.redundantAssertion.isEmpty());
		assertTrue(rottenTest2.hasFailInvocation());
		assertTrue(rottenTest2.getAllFailsFromTest().size() > 0);
	}

	@Test
	public void testRTFRow26Assume() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass().contains("RTFRow26Assume")).collect(Collectors.toList());

		// Second test
		Optional<TestInspectionResult> rotten2P = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test0"))
				.findFirst();

		assertTrue(rotten2P.isPresent());

		TestInspectionResult rottenTest2 = rotten2P.get();
		assertTrue(rottenTest2.getExpectException().isEmpty());
		assertFalse(rottenTest2.isSmokeTest());
		assertTrue(rottenTest2.isRotten());

		assertTrue(rottenTest2.isOnlyAssumeExecuted());
		assertTrue(rottenTest2.getAllAssumesFromTest().size() > 0);

		// we expect exceptions

		// TestRottenAnalysisResult fr2 = rottenTest2.generateFinalResult();

		// Now test with not rotten

		Optional<TestInspectionResult> rotten1 = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test1"))
				.findFirst();

		assertTrue(rotten1.isPresent());

		TestInspectionResult rottenTest1 = rotten1.get();
		assertTrue(rottenTest1.getExpectException().isEmpty());
		assertFalse(rottenTest1.isSmokeTest());
		assertFalse(rottenTest1.isRotten());

		assertFalse(rottenTest1.isOnlyAssumeExecuted());
		assertTrue(rottenTest1.getAllAssumesFromTest().size() > 0);

	}

	@Test
	public void testRTFRow27HelperDistance() throws Exception {
		RtEngine etEn = detectRt();

		List<TestInspectionResult> resultByTest = etEn.getResultByTest();
		assertNotNull(resultByTest);

		List<TestInspectionResult> tc = resultByTest.stream()
				.filter(e -> e.getNameOfTestClass().contains("RTFRow27HelperDistance")).collect(Collectors.toList());

		Optional<TestInspectionResult> rotten0P = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test0"))
				.findFirst();

		assertTrue(rotten0P.isPresent());

		TestInspectionResult rottenTest0 = rotten0P.get();
		assertFalse(rottenTest0.isSmokeTest());
		assertTrue(rottenTest0.isRotten());

		assertFalse(rottenTest0.getClassificationHelperCall().getResultNotExecuted().isEmpty());
		assertFalse(rottenTest0.getClassificationHelperAssertion().getResultNotExecuted().isEmpty());

		Helper rtHelperParent1 = rottenTest0.getClassificationHelperAssertion().getResultNotExecuted().get(0);
		assertEquals(1, rtHelperParent1.distance);

		// Second:

		Optional<TestInspectionResult> rotten1P = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test1"))
				.findFirst();

		assertTrue(rotten1P.isPresent());

		TestInspectionResult rottenTest1 = rotten1P.get();
		assertFalse(rottenTest1.isSmokeTest());
		assertTrue(rottenTest1.isRotten());

		assertFalse(rottenTest1.getClassificationHelperCall().getResultNotExecuted().isEmpty());
		assertFalse(rottenTest1.getClassificationHelperAssertion().getResultNotExecuted().isEmpty());

		Helper rtHelperParent0 = rottenTest1.getClassificationHelperAssertion().getResultNotExecuted().get(0);
		assertEquals(0, rtHelperParent0.distance);

		//

		// Thirds:

		Optional<TestInspectionResult> rotten2P = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test2"))
				.findFirst();

		assertTrue(rotten2P.isPresent());

		TestInspectionResult rottenTest2 = rotten2P.get();
		assertFalse(rottenTest2.isSmokeTest());
		assertTrue(rottenTest2.isRotten());

		assertFalse(rottenTest2.getClassificationHelperCall().getResultNotExecuted().isEmpty());
		assertFalse(rottenTest2.getClassificationHelperAssertion().getResultNotExecuted().isEmpty());

		Helper rtHelperParent2 = rottenTest2.getClassificationHelperAssertion().getResultNotExecuted().get(0);
		assertEquals(2, rtHelperParent2.distance);

		// Four:

		Optional<TestInspectionResult> rotten3P = tc.stream().filter(e -> e.getTestMethodFromClass().equals("test3"))
				.findFirst();

		assertTrue(rotten3P.isPresent());

		TestInspectionResult rottenTest3 = rotten3P.get();
		assertFalse(rottenTest3.isSmokeTest());
		assertTrue(rottenTest3.isRotten());

		assertFalse(rottenTest3.getClassificationHelperCall().getResultNotExecuted().isEmpty());
		assertFalse(rottenTest3.getClassificationHelperAssertion().getResultNotExecuted().isEmpty());

		Helper rtHelperParent3 = rottenTest3.getClassificationHelperAssertion().getResultNotExecuted().get(0);
		assertEquals(-1, rtHelperParent3.distance);

	}

	private void checkFp(List<TestInspectionResult> tc, boolean toverif, String testname) {
		Optional<TestInspectionResult> rotten01 = tc.stream().filter(e -> e.getTestMethodFromClass().equals(testname))
				.findFirst();

		assertTrue(rotten01.isPresent());

		TestInspectionResult rtest1 = rotten01.get();

		assertEquals(1, rtest1.getClassificationAssert().getResultNotExecuted().size());

		AsAssertion rtas1 = rtest1.getClassificationAssert().getResultNotExecuted().get(0);

		assertEquals("--> FP", toverif, rtas1.isFp());
	}

	private void checkFpHelper(List<TestInspectionResult> tc, boolean toverif, String testname) {
		Optional<TestInspectionResult> rotten01 = tc.stream().filter(e -> e.getTestMethodFromClass().equals(testname))
				.findFirst();

		assertTrue(rotten01.isPresent());

		TestInspectionResult rtest1 = rotten01.get();

		assertEquals(1, rtest1.getClassificationHelperAssertion().getResultNotExecuted().size());

		Helper rtas1 = rtest1.getClassificationHelperAssertion().getResultNotExecuted().get(0);

		assertEquals("--> FP", toverif, rtas1.isFp());
	}

	private RtEngine detectRt() throws Exception {
		AstorMain main1 = new AstorMain();

		String dep = new File("./examples/libs/junit-4.12.jar").getAbsolutePath();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		int generations = 500;

		String[] args = new String[] { "-dependencies", dep, "-javacompliancelevel", "7", "-flthreshold", "0.5", "-out",
				out.getAbsolutePath(), "-scope", "local", "-seed", "10", "-maxgen", Integer.toString(generations),
				"-stopfirst", "true", "-maxtime", "100",

		};
		CommandSummary cs = new CommandSummary(args);
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "INFO");
		cs.command.put("-location", new File("./examples/testMultiMet/rt-project/").getAbsolutePath());
		cs.command.put("-mode", "custom");
		cs.command.put("-customengine", RtEngine.class.getCanonicalName());
		cs.command.put("-parameters", "canhavezerosusp:true");

		main1.execute(cs.flat());
		RtEngine etEn = (RtEngine) main1.getEngine();
		return etEn;
	}
}
