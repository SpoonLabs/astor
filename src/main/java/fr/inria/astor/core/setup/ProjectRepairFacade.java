package fr.inria.astor.core.setup;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.faultlocalization.FaultLocalizationResult;
import fr.inria.astor.core.faultlocalization.FaultLocalizationStrategy;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;

/**
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class ProjectRepairFacade {

	Logger logger = Logger.getLogger(Thread.currentThread().getName());

	protected ProjectConfiguration setUpProperties = new ProjectConfiguration();

	public ProjectRepairFacade(ProjectConfiguration properties) throws Exception {

		setProperties(properties);

	}

	/**
	 * Set up a project for a given mutator identifier.
	 * 
	 * @param currentMutatorIdentifier
	 * @throws IOException
	 */
	public synchronized void setupWorkingDirectories(String currentMutatorIdentifier) throws IOException {

		cleanMutationResultDirectories();

		copyOriginalCode(currentMutatorIdentifier);

		try {

			String originalAppBinDir = getProperties().getOriginalAppBinDir();

			copyOriginalBin(originalAppBinDir, currentMutatorIdentifier);// NEW
																			// ADDED
			copyOriginalBin(getProperties().getOriginalTestBinDir(), currentMutatorIdentifier);// NEW

		} catch (Exception e) {
			throw new RuntimeException(e);
		} 
		copyData(currentMutatorIdentifier);

	}

	public void copyOriginalCode(String mutIdentifier) throws IOException {
			List<String>  dirs = getProperties().getOriginalDirSrc();
			//The first element corresponds to application source
			String srcApp = dirs.get(0);
			//the second to the test folder
			String srctest = dirs.get(1);
			//we only want to generate the model of the application, so, we copy only it code, ignoring tests
			copyOriginalSourceCode(srcApp, mutIdentifier);
	}

	/**
	 * Copy the original code -from the path passed by parameter- to the
	 * mutation folder
	 * 
	 * @param pathOriginalCode
	 * @throws IOException
	 */
	public void copyOriginalSourceCode(String pathOriginalCode, String currentMutatorIdentifier) throws IOException {
		File destination = new File(getProperties().getWorkingDirForSource() + File.separator + currentMutatorIdentifier);
		destination.mkdirs();
		FileUtils.copyDirectory(new File(pathOriginalCode), destination);
	}

	/**
	 * Remove dir for a given mutation
	 * 
	 * @throws IOException
	 */
	public void cleanMutationResultDirectories(String currentMutatorIdentifier)
			throws IOException {

		removeDir(getProperties().getWorkingDirForSource() + File.separator + currentMutatorIdentifier);
		removeDir(getProperties().getWorkingDirForBytecode() + File.separator + currentMutatorIdentifier);
	}

	public void cleanMutationResultDirectories() throws IOException {

		removeDir(getProperties().getWorkingDirForSource());
		removeDir(getProperties().getWorkingDirForBytecode());
	}

	private void removeDir(String dir) throws IOException {
		File dirin = new File(dir);
		try {
			FileUtils.deleteDirectory(dirin);
		} catch (Exception ex) {
			logger.error("ex: " + ex.getMessage());
		}
		dirin.mkdir();
	}

	public boolean copyOriginalBin(String inDir, String mutatorIdentifier) throws IOException {
		boolean copied = false;
		if (inDir != null) {
			File original = new File(inDir);
			File dest = new File(getOutDirWithPrefix(mutatorIdentifier));
			dest.mkdirs();
			FileUtils.copyDirectory(original, dest);
			copied = true;
		}
		return copied;
	}

	public String getOutDirWithPrefix(String currentMutatorIdentifier) {
		return getProperties().getWorkingDirForBytecode() + File.separator + currentMutatorIdentifier;
	}

	public String getInDirWithPrefix(String currentMutatorIdentifier) {
		return getProperties().getWorkingDirForSource() + File.separator + currentMutatorIdentifier;
	}

	public void copyData(String currentMutatorIdentifier) throws IOException {
		
		String resourcesDir = getProperties().getDataFolder();
		if ( resourcesDir == null)
			return;
		
		String[] resources = resourcesDir.split(File.pathSeparator);
		File destFile = new File(getOutDirWithPrefix(currentMutatorIdentifier));
		
		for (String r : resources) {
			String path = ConfigurationProperties.getProperty("location");
			File source = new File(path + File.separator + r);
			if(!source.exists())
				return;
			//destFile.mkdirs();
			//for(File f:source.listFiles())
			FileUtils.copyDirectory(source, destFile);

		}
	
	}

	/**
	 * Return classpath form mutated variant.
	 * 
	 * @param currentMutatorIdentifier
	 * @return
	 * @throws MalformedURLException
	 */
	public URL[] getClassPathURLforProgramVariant(String currentMutatorIdentifier) throws MalformedURLException {

		List<URL> classpath = new ArrayList<URL>(getProperties().getDependencies());
		// bin
		URL urlBin = new File(getOutDirWithPrefix(currentMutatorIdentifier)).toURI().toURL();
		classpath.add(urlBin);

		URL[] cp = classpath.toArray(new URL[0]);
		return cp;
	}

	public List<SuspiciousCode> calculateSuspicious(FaultLocalizationStrategy faultLocalization) throws Exception {
		List<SuspiciousCode> candidates = this.calculateSuspicious(faultLocalization,
				ConfigurationProperties.getProperty("location") + File.separator
						+ ConfigurationProperties.getProperty("srcjavafolder"),
				getOutDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT),
				ConfigurationProperties.getProperty("packageToInstrument"), ProgramVariant.DEFAULT_ORIGINAL_VARIANT,
				getProperties().getFailingTestCases(), getProperties().getRegressionTestCases(),
				ConfigurationProperties.getPropertyBool("regressionforfaultlocalization"));
		return candidates;
	}

	public List<SuspiciousCode> calculateSuspicious(FaultLocalizationStrategy faultLocalization, String locationSrc,
			String locationBytecode, String packageToInst, String mutatorIdentifier, List<String> failingTest,
			List<String> allTest, boolean mustRunAllTest) throws Exception {

		if (faultLocalization == null)
			throw new IllegalArgumentException("Fault localization is null");

		List<String> testcasesToExecute = null;

		if (mustRunAllTest) {
			testcasesToExecute = allTest;
		} else {
			testcasesToExecute = failingTest;
		}

		if (testcasesToExecute == null || testcasesToExecute.isEmpty()) {
			new IllegalArgumentException("Astor needs at least one test case for running");
		}

		logger.info("-Executing Gzoltar classpath: " + locationBytecode + " from " + +testcasesToExecute.size()
				+ " classes with test cases");

		List<String> listTOInst = new ArrayList<String>();
		listTOInst.add(packageToInst);

		Set<String> classPath = new HashSet<String>();
		classPath.add(locationBytecode);
		for (URL dep : getProperties().getDependencies()) {
			classPath.add(dep.getPath());
		}

		FaultLocalizationResult flResult = faultLocalization.searchSuspicious(locationBytecode,
				testcasesToExecute, listTOInst, classPath, locationSrc);

		List<SuspiciousCode> suspiciousStatemens = flResult.getCandidates();
		 
		if (suspiciousStatemens == null || suspiciousStatemens.isEmpty())
			throw new IllegalArgumentException("No suspicious gen for analyze");
		
		List<String> failingTestCases = flResult.getFailingTestCases();
		if(ConfigurationProperties.getPropertyBool("ignoreflakyinfl")){
			addFlakyFailingTestToIgnoredList(failingTestCases);
		}
		
		if(this.getProperties().getFailingTestCases().isEmpty()){
			logger.debug("Failing test cases was not pass as argument: we use failings from FL "+flResult.getFailingTestCases());
			getProperties().setFailingTestCases(failingTestCases);
		}
		
		List<SuspiciousCode> filtercandidates = new ArrayList<SuspiciousCode>();

		for (SuspiciousCode suspiciousCode : suspiciousStatemens) {
			if (!suspiciousCode.getClassName().endsWith("Exception")) {
				filtercandidates.add(suspiciousCode);
			}
		}
		
		return filtercandidates;
	}

	/**
	 * It adds to the ignore list all failing TC that were not passed as argument. \
	 * They are probably flaky test.
	 * @param failingTestCases
	 */
	private void addFlakyFailingTestToIgnoredList(List<String> failingTestCases) {
		//
		if(this.getProperties().getFailingTestCases() == null)
			return;
		List<String> originalFailing = this.getProperties().getFailingTestCases();
		List<String> onlyFailingInFL = new ArrayList<>(failingTestCases);
		//we remove those that we already know that fail
		onlyFailingInFL.removeAll(originalFailing);
		logger.debug("failing before "+ onlyFailingInFL+ ", added to the ignored list");
		String ignoredTestCases =  ConfigurationProperties.getProperty("ignoredTestCases");
		for (String failingFL : onlyFailingInFL) {
			ignoredTestCases+=File.pathSeparator+failingFL;
		}
		ConfigurationProperties.properties.setProperty("ignoredTestCases",ignoredTestCases);
	}

	public ProjectConfiguration getProperties() {
		return setUpProperties;
	}

	public void setProperties(ProjectConfiguration properties) {
		this.setUpProperties = properties;
	}

}
