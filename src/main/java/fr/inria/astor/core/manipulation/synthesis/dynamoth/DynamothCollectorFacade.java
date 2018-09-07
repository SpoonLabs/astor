package fr.inria.astor.core.manipulation.synthesis.dynamoth;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;
import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.faultlocalization.gzoltar.TestCaseResult;
import fr.inria.astor.core.manipulation.synthesis.ExecutionContextCollector;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.FinderTestCases;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.nopol.synth.AngelicExecution;

/**
 * Class that collects values using Dynamoth engine.
 * 
 * @author Matias Martinez
 *
 */
public class DynamothCollectorFacade implements ExecutionContextCollector {

	protected static Logger log = Logger.getLogger(Thread.currentThread().getName());

	public DynamothCollector createCollector(ProjectRepairFacade facade, ModificationPoint mp) {

		SuspiciousModificationPoint smp = (SuspiciousModificationPoint) mp;
		String[] tests = getCoverTest(smp);
		Map<String, Object[]> oracle = new HashMap<>();

		for (String testCase : tests) {
			oracle.put(testCase, new Boolean[] { true });
		}
		return this.createCollector(facade, mp, oracle, tests);
	}

	public DynamothCollector createCollector(ProjectRepairFacade facade, ModificationPoint mp,
			Map<String, Object[]> oracle, String[] testClasses) {

		SuspiciousModificationPoint smp = (SuspiciousModificationPoint) mp;

		String classPath = facade.getOutDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT) + File.pathSeparator
				+ facade.getProperties().getDependenciesString();
		URL[] urls = (FinderTestCases.classpathFrom(classPath));

		File[] sources = new File[facade.getProperties().getOriginalDirSrc().size()];
		int i = 0;
		for (String s : facade.getProperties().getOriginalDirSrc()) {
			sources[i++] = new File(s);
		}

		log.info("Astor-Dynamoth config: ");

		NopolContext nopolContext = new NopolContext(sources, urls, testClasses);
		nopolContext.setCollectOnlyUsedMethod(ConfigurationProperties.getPropertyBool("collectonlyusedmethod"));
		nopolContext.setDataCollectionTimeoutInSecondForSynthesis(5);
		nopolContext.setOnlyOneSynthesisResult(false);
		log.info("-sources: " + Arrays.toString(sources));
		log.info("-url: " + Arrays.toString(urls));
		log.info("-testClasses: " + Arrays.toString(testClasses));

		DynamothCollector dynamothCodeGenesis = new DynamothCollector(oracle, smp, sources, urls, testClasses,
				nopolContext);

		dynamothCodeGenesis.run(TimeUnit.MINUTES.toMillis(15));

		return dynamothCodeGenesis;
	}

	public String[] getCoverTest(SuspiciousModificationPoint mp0) {
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
		return tests;
	}

	public DynamothSynthesisContext collectValues(ProjectRepairFacade facade, ModificationPoint mp) {

		DynamothCollector collector = createCollector(facade, mp);

		return new DynamothSynthesisContext(collector.getValues(), collector.getNopolContext(), collector.getOracle());
	}

	@SuppressWarnings("unused")
	private class TestRunListener<T> extends RunListener {
		private Map<String, List<T>> failedTests = new HashMap<>();
		private Map<String, List<T>> passedTests = new HashMap<>();

		@Override
		public void testFailure(Failure failure) throws Exception {
			Description description = failure.getDescription();
			String key = description.getClassName() + "#" + description.getMethodName();
			failedTests.put(key, AngelicExecution.previousValue);
		}

		@Override
		public void testFinished(Description description) throws Exception {
			String key = description.getClassName() + "#" + description.getMethodName();
			if (!failedTests.containsKey(key)) {
				passedTests.put(key, AngelicExecution.previousValue);
			}
			AngelicExecution.previousValue = new ArrayList<>();
		}
	}
}
