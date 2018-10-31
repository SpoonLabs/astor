package fr.inria.astor.core.setup;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

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

		copyOriginalCodeToAstorWorkspace(currentMutatorIdentifier);

		try {
			copyOriginalBin(getProperties().getOriginalAppBinDir(), currentMutatorIdentifier);
			copyOriginalBin(getProperties().getOriginalTestBinDir(), currentMutatorIdentifier);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		copyData(currentMutatorIdentifier);

	}

	public void copyOriginalCodeToAstorWorkspace(String mutIdentifier) throws IOException {
		List<String> dirs = getProperties().getOriginalDirSrc();
		for (String srcApp : dirs) {

			copyOriginalSourceCode(srcApp, mutIdentifier);

		}

	}

	/**
	 * Copy the original code -from the path passed by parameter- to the mutation
	 * folder
	 * 
	 * @param pathOriginalCode
	 * @throws IOException
	 */
	public void copyOriginalSourceCode(String pathOriginalCode, String currentMutatorIdentifier) throws IOException {
		File destination = new File(
				getProperties().getWorkingDirForSource() + File.separator + currentMutatorIdentifier);
		destination.mkdirs();
		FileUtils.copyDirectory(new File(pathOriginalCode), destination);
	}

	/**
	 * Remove dir for a given mutation
	 * 
	 * @throws IOException
	 */
	public void cleanMutationResultDirectories(String currentMutatorIdentifier) throws IOException {

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

	public boolean copyOriginalBin(List<String> inDirs, String mutatorIdentifier) throws IOException {
		if (inDirs == null) {
			logger.debug("Original Bin folder does not exist");
			return false;
		}

		boolean copied = false;
		for (String inDir : inDirs) {
			if (inDir != null) {
				File original = new File(inDir);
				File dest = new File(getOutDirWithPrefix(mutatorIdentifier));
				dest.mkdirs();
				FileUtils.copyDirectory(original, dest);
				copied = true;
			}
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
		if (resourcesDir == null)
			return;

		String[] resources = resourcesDir.split(File.pathSeparator);
		File destFile = new File(getOutDirWithPrefix(currentMutatorIdentifier));

		for (String r : resources) {
			String path = ConfigurationProperties.getProperty("location");
			File source = new File(path + File.separator + r);
			if (!source.exists())
				return;
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

	public ProjectConfiguration getProperties() {
		return setUpProperties;
	}

	public void setProperties(ProjectConfiguration properties) {
		this.setUpProperties = properties;
	}

}
