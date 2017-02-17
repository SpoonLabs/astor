package fr.inria.astor.core.setup;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.faultlocalization.entity.TestClassesFinder;
import spoon.reflect.declaration.CtClass;
import spoon.reflect.declaration.CtType;

/**
 * Class for manipulating information related to test cases.
 * @author Matias
 *
 */
public class FinderTestCases {
	
	protected static Logger log = Logger.getLogger(FinderTestCases.class.getName());
	
	public  static void findTestCasesForRegression(String classPath, ProjectRepairFacade projectFacade) {
		String[] testClassesRegression = new TestClassesFinder().findIn(classpathFrom(classPath), false);
		List<String> tr = new ArrayList<>();
		String s = "";
		for (String tcr : testClassesRegression) {
			s+=tcr+File.pathSeparator;
			tr.add(tcr);
		}
		projectFacade.getProperties().setRegressionCases(tr);
	}

	private static URL[] classpathFrom(String classpath) {
		String[] folderNames = classpath.split(File.pathSeparator);
		URL[] folders = new URL[folderNames.length];
		int index = 0;
		for (String folderName : folderNames) {
			folders[index] = urlFrom(folderName);
			index += 1;
		}
		return folders;
	}
	
	private static URL urlFrom(String path) {
		URL url = null;
		try {
			url = new File(path).toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}
	
	
	public static void updateRegressionTestCases(ProjectRepairFacade projectConfig) {
		List<String> original = projectConfig.getProperties().getRegressionTestCases();
		List<String> refined =	refineListofRegressionTestCases(original);
		if(!refined.isEmpty())
			projectConfig.getProperties().setRegressionCases(refined);
		log.debug("Refining list of test cases: original= "+original.size() 
			+", refined= "+refined.size());
	}
	
	/**
	 * This method refine the list of test cases received as parameter.
	 * I analyze each model of each test to validate whether is a test or not. 
	 */

	private static List<String> refineListofRegressionTestCases(List<String> allTest) {
			List<String> regressionCases = new ArrayList<String>();
			List<String> ignoreTestcases = retriveIgnoreTestCases();
			
			log.debug("Ignored test cases: "+ignoreTestcases);
			
			if(ignoreTestcases.isEmpty())
				return allTest;
			
			
			for (String candidateTest : allTest) {
		
				if(!(isIgnoredTestCase(candidateTest, ignoreTestcases))){
					regressionCases.add(candidateTest);
				}
				
			}
				
			return regressionCases;
	}
	
	private static boolean isValidConstructor(CtType<?> type) {
		if(type instanceof CtClass<?>) {
			CtClass<?> ctClass = ((CtClass<?>)type);
			if (ctClass.getSuperclass() == null || !ctClass.getSuperclass().getSimpleName().equals("TestCase")){
				return true;
			}
			return ((CtClass<?>)type).getConstructor() != null ||
			((CtClass<?>)type).getConstructor(type.getFactory().Class().createReference(String.class)) != null;
		}
		return false;
	}

	private static List<String> retriveIgnoreTestCases() {
		String list = ConfigurationProperties.getProperty("ignoredTestCases");
		log.debug("to ignore "+ list );
		String[] cases = list.split(File.pathSeparator);
		return 	Arrays.asList(cases);
	}

	private static boolean isIgnoredTestCase(String nameTestCase, List<String> ignoredList ){
		
		for (String ignoreTC : ignoredList) {
			
			if(nameTestCase.startsWith(ignoreTC)){
				return true;
			};
		}
		return false;
	}
	
	
}
