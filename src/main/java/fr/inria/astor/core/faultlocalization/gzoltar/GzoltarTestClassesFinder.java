
package fr.inria.astor.core.faultlocalization.gzoltar;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.log4j.Logger;
import org.jacoco.core.runtime.WildcardMatcher;

import com.gzoltar.core.test.FindTestMethods;
import com.gzoltar.core.test.TestMethod;

import fr.inria.astor.core.faultlocalization.entity.CustomClassLoaderThreadFactory;
import fr.inria.astor.core.setup.ProjectRepairFacade;

public final class GzoltarTestClassesFinder implements Callable<List<String>> {

	private final Logger logger = Logger.getLogger(this.getClass());

	ProjectRepairFacade projectFacade = null;

	public GzoltarTestClassesFinder(ProjectRepairFacade projectFacade2) {
		this.projectFacade = projectFacade2;
	}

	public List<String> call() throws Exception {

		List<String> testName = new ArrayList<>();
		String includes = "*";

		for (String testClassesDirString : projectFacade.getProperties().getOriginalTestBinDir()) {

			File testClassesDir = new File(testClassesDirString);
			System.out.println("-->" + testClassesDir.getAbsolutePath());
			try {
				List<TestMethod> findTestMethodsInPath = FindTestMethods.findTestMethodsInPath(testClassesDir,
						new WildcardMatcher(includes));
				System.out.println("Results " + findTestMethodsInPath);
				for (TestMethod testMethod : findTestMethodsInPath) {
					String testId = testMethod.getClassType().name() + "," + testMethod.getLongName();
					testName.add(testId);
					System.out.println("NGzoltar retrieved test: " + testId);

				}
			} catch (Exception e) {

				e.printStackTrace();
			}
		}

		return testName;

	}

	public static List<String> findIn(ProjectRepairFacade projectFacade) throws MalformedURLException {

		URL[] both = getPath(projectFacade);

		ClassLoader dumpedToClassLoader = new URLClassLoader(both, Thread.currentThread().getContextClassLoader());

		ExecutorService executor = Executors
				.newSingleThreadExecutor(new CustomClassLoaderThreadFactory(dumpedToClassLoader));
		try {
			List<String> classes = executor.submit(new GzoltarTestClassesFinder(projectFacade)).get();
			return classes;
		} catch (InterruptedException ie) {
			throw new RuntimeException(ie);
		} catch (ExecutionException ee) {
			ee.printStackTrace();
			return null;
		} finally {
			executor.shutdown();
		}

	}

	public static URL[] getPath(ProjectRepairFacade projectFacade) throws MalformedURLException {
		List<String> originalTestBinDir = new ArrayList();
		originalTestBinDir.addAll(projectFacade.getProperties().getOriginalTestBinDir());
		originalTestBinDir.addAll(projectFacade.getProperties().getOriginalAppBinDir());

		System.out.println(originalTestBinDir);
		URL[] array = new URL[originalTestBinDir.size()];

		for (int i = 0; i < originalTestBinDir.size(); i++) {
			String url = originalTestBinDir.get(i);
			array[i] = new File(url).toURL();

		}

		List<URL> dep = projectFacade.getProperties().getDependencies();

		URL[] arrayDep = dep.toArray(new URL[0]);

		URL[] both = org.apache.commons.lang3.ArrayUtils.addAll(array, arrayDep);
		System.out.println("Classpath " + Arrays.toString(both));
		return both;
	}

}
