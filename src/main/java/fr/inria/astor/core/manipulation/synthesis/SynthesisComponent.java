package fr.inria.astor.core.manipulation.synthesis;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.junit.runner.Description;
import org.junit.runner.Result;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunListener;

import fr.inria.astor.core.entities.Ingredient;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.setup.FinderTestCases;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.util.Converters;
import fr.inria.lille.repair.common.Candidates;
import fr.inria.lille.repair.common.config.NopolContext;
import fr.inria.lille.repair.nopol.SourceLocation;
import fr.inria.lille.repair.nopol.synth.AngelicExecution;
import fr.inria.lille.repair.synthesis.DynamothCodeGenesis;
import fr.inria.lille.repair.synthesis.DynamothCodeGenesisImpl;
import spoon.reflect.code.CtExpression;
import spoon.reflect.declaration.CtType;
import spoon.reflect.declaration.CtVariable;
import xxl.java.junit.CompoundResult;
import xxl.java.junit.TestSuiteExecution;

/**
 * 
 * @author Matias Martinez
 *
 */
public class SynthesisComponent {

	@SuppressWarnings("rawtypes")
	public List<Ingredient> runSynthesis(ModificationPoint modificationPoint, CtExpression hole, CtType expectedtype,
			List<CtVariable> contextOfModificationPoint, DynamicCollectedValues values) {

		return null;
	}

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

	public DynamothCollector createSynthesizer(ProjectRepairFacade facade, ModificationPoint mp,
			String[] testClasses) {

		Map<String, Object[]> oracle = new HashMap<>();

		SuspiciousModificationPoint smp = (SuspiciousModificationPoint) mp;

		// Okey
		String classPath = facade.getOutDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT) + File.pathSeparator
				+ facade.getProperties().getDependenciesString();
		URL[] urls = (FinderTestCases.classpathFrom(classPath));

		// Ok, From modification point
		SourceLocation location = new SourceLocation(smp.getCtClass().getQualifiedName(),
				smp.getSuspicious().getLineNumber());

		// Okey
		File[] sources = new File[facade.getProperties().getOriginalDirSrc().size()];
		int i = 0;
		for (String s : facade.getProperties().getOriginalDirSrc()) {
			sources[i++] = new File(s);
		}

		System.out.println("Astor-Dynamoth config: ");

		NopolContext nopolContext = new NopolContext(sources, urls, testClasses);
		nopolContext.setDataCollectionTimeoutInSecondForSynthesis(5);
		System.out.println("-sources: " + Arrays.toString(sources));
		System.out.println("-url: " + Arrays.toString(urls));
		System.out.println("-testClasses: " + Arrays.toString(testClasses));
		System.out.println("-locations: " + (location));
		System.out.println("-classpath: ");

		for (URL url : urls) {
			System.out.println("----> " + url);
		}

		for (String string : testClasses) {
			oracle.put(string, new Boolean[] { true });
		}

		DynamothCollector dynamothCodeGenesis = new DynamothCollector(sources, location, urls, oracle,
				testClasses, nopolContext);

		dynamothCodeGenesis.run(TimeUnit.MINUTES.toMillis(15));

		return dynamothCodeGenesis;
	}
}
