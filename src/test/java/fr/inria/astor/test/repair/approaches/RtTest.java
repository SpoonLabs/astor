package fr.inria.astor.test.repair.approaches;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.inria.astor.approaches.extensions.rt.RtEngine;
import fr.inria.astor.approaches.extensions.rt.RtEngine.TestClassificationResult;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;

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

		int sumHelperNotExec = resultByTest.stream().map(e -> e.getClassificationHelper().getResultNotExecuted().size())
				.collect(Collectors.summingInt(i -> i));

		assertEquals(1, sumHelperNotExec);

	}

}
