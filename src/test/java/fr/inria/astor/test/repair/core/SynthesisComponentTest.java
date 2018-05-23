package fr.inria.astor.test.repair.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.faultlocalization.gzoltar.TestCaseResult;
import fr.inria.astor.core.manipulation.synthesis.DynamothCollector;
import fr.inria.astor.core.manipulation.synthesis.SynthesisComponent;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.lille.repair.common.Candidates;
import fr.inria.lille.repair.synthesis.DynamothCodeGenesisImpl;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class SynthesisComponentTest {
	protected static Logger log = Logger.getLogger(Thread.currentThread().getName());

	@Test
	public void testValueCollection1() throws Exception {
		AstorMain main1 = new AstorMain();
		CommandSummary cs = MathCommandsTests.getMath70Command();

		cs.command.put("-flthreshold", "0.1");
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "DEBUG");
		cs.command.put("-saveall", "true");
		cs.command.put("-maxgen", "0");
		cs.append("-parameters", ("logtestexecution:trueL"));

		log.info(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		assertEquals(1, main1.getEngine().getVariants().size());
		ProgramVariant variant = main1.getEngine().getVariants().get(0);

		SynthesisComponent sc = new SynthesisComponent();

		log.info("***First mp to test: ");
		SuspiciousModificationPoint mp0 = (SuspiciousModificationPoint) variant.getModificationPoints().get(0);
		assertEquals("org.apache.commons.math.analysis.solvers.BisectionSolver", mp0.getCtClass().getQualifiedName());
		assertEquals(72, mp0.getSuspicious().getLineNumber());

		valuesOfModificationPoint(main1, sc, mp0);

		log.info("***Second mp to test: ");

		SuspiciousModificationPoint mp8 = (SuspiciousModificationPoint) variant.getModificationPoints().get(8);
		assertEquals("org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils",
				mp8.getCtClass().getQualifiedName());
		assertEquals(223, mp8.getSuspicious().getLineNumber());
		valuesOfModificationPoint(main1, sc, mp8);

	}

	private void valuesOfModificationPoint(AstorMain main1, SynthesisComponent sc, SuspiciousModificationPoint mp0) {

		log.info("-mp-> " + mp0.getCodeElement());
		String[] tests = new String[mp0.getSuspicious().getCoveredByTests().size()];
		int i = 0;
		int nrfailing = 0;
		int nrpassing = 0;
		for (TestCaseResult tr : mp0.getSuspicious().getCoveredByTests()) {
			tests[i++] = tr.getTestCaseCompleteName();
			if (tr.isCorrect())
				nrpassing++;
			else
				nrfailing++;
		}
		log.info("nr passing " + nrpassing + " nr failing " + nrfailing);

		DynamothCollector dynamothCodeGenesis = sc.createSynthesizer(main1.getEngine().getProjectFacade(), mp0, tests);

		Map<String, List<Candidates>> values = dynamothCodeGenesis.getValues();
		assertTrue(!values.isEmpty());
		int nrtest = 0;
		for (String key : values.keySet()) {
			log.info("test " + nrtest++ + " :" + key);
			List<Candidates> candidates1 = values.get(key);
			log.info("nr candidates 1: " + candidates1.size());
			i = 0;
			for (Candidates candidates2 : candidates1) {
				log.info("--Nr of candidates of " + (i++) + ": " + candidates2.size());
				int j = 0;
				for (fr.inria.lille.repair.expression.Expression expression : candidates2) {

					log.info("--*-->" + i + " " + (j++) + " " + expression.asPatch() + " " + expression.getValue());

				}
			}

		}
	}
}
