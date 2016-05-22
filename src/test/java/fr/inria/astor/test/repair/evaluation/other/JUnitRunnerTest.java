package fr.inria.astor.test.repair.evaluation.other;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runners.model.InitializationError;

import fr.inria.astor.junitexec.JUnitMethodTestExecutor;
import fr.inria.astor.junitexec.JUnitTestExecutor;

/**
 * Validates the junit runners.
 * 
 * @author Matias Martinez
 *
 */
public class JUnitRunnerTest {

	@Test
	public void testProcessMain() throws InitializationError, Exception {
		JUnitMethodTestExecutor.main(new String[] { Test4Test.class.getName() + "#testT" });
	}

	@Test
	public void testProcessOneTest() throws InitializationError, Exception {
		JUnitMethodTestExecutor junit = new JUnitMethodTestExecutor();

		org.junit.runner.Result result = junit.run(new String[] { Test4Test.class.getName() + "#testT" });

		Assert.assertNotNull(result);
		Assert.assertTrue(result.wasSuccessful());
		Assert.assertEquals(1, result.getRunCount());

	}

	@Test
	public void testProcessFailing() throws InitializationError, Exception {
		JUnitMethodTestExecutor junit = new JUnitMethodTestExecutor();

		try {
			// See the arg is wrong, it misses the method class
			org.junit.runner.Result result = junit.run(new String[] { Test4Test.class.getName() });
			Assert.fail("Exception not reached");
		} catch (IllegalArgumentException ex) {
		}

	}

	@Test
	public void testProcessAllClass() throws InitializationError, Exception {
		JUnitTestExecutor junit = new JUnitTestExecutor();

		org.junit.runner.Result result = junit.run(new String[] { Test4Test.class.getName() });

		Assert.assertNotNull(result);
		Assert.assertTrue(result.wasSuccessful());
		Assert.assertEquals(2, result.getRunCount());

	}

}
