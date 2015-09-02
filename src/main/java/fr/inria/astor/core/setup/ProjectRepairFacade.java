package fr.inria.astor.core.setup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import fr.inria.astor.core.faultlocalization.FaultLocalizationFacade;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.faultlocalization.entity.TestClassesFinder;
/**
 *  
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class ProjectRepairFacade {

	Logger logger = Logger.getLogger(Thread.currentThread().getName());

	protected ProjectConfiguration setUpProperties = new ProjectConfiguration();
	
	protected FaultLocalizationFacade faultLocalizationFacade = new FaultLocalizationFacade();
	
	public ProjectRepairFacade(ProjectConfiguration properties) throws Exception {
		
		
		setProperties(properties);
	
	}

	
	/**
	 * Set up a project for a given mutator identifier.
	 * @param currentMutatorIdentifier
	 * @throws IOException
	 */
	public synchronized void setupTempDirectories(String currentMutatorIdentifier) throws IOException {
		
		cleanMutationResultDirectories();
		
		copyOriginalCode(currentMutatorIdentifier);
		try{
		boolean copied1 = copyOriginalBin(getProperties().getOriginalAppBinDir(),currentMutatorIdentifier);// NEW
																									// ADDED
		boolean copied2 = copyOriginalBin(getProperties().getOriginalTestBinDir(),currentMutatorIdentifier);// NEW
		}catch(Exception e){
			e.printStackTrace();
		}																							// ADDED

		copyData(currentMutatorIdentifier);

	}
	public void copyOriginalCode(String mutIdentifier) throws IOException {
		for(String dirSourceOrigin : getProperties().getOriginalDirSrc()){
			copyOriginalSourceCode(dirSourceOrigin,mutIdentifier);
		}
	}
	/**
	 * Copy the original code -from the path passed by parameter- to the mutation folder
	 * @param pathOriginalCode
	 * @throws IOException
	 */
	public void copyOriginalSourceCode(String pathOriginalCode, String currentMutatorIdentifier ) throws IOException {
		File destination = new File(getProperties().getInDir() + File.separator + currentMutatorIdentifier);
		destination.mkdirs();
		FileUtils.copyDirectory(new File(pathOriginalCode), destination);
	}

	
	/**
	 * Remove dir for a given mutation
	 * @throws IOException
	 */
	public void cleanMutationResultDirectories(String currentMutatorIdentifier /*currentMutatorIdentifier*/) throws IOException {

		//= currentMutatorIdentifier();
		removeDir(getProperties().getInDir() +File.separator+ currentMutatorIdentifier);
		removeDir(getProperties().getOutDir() +File.separator+ currentMutatorIdentifier);
	}
	
	public void cleanMutationResultDirectories() throws IOException {

		removeDir(getProperties().getInDir());
		removeDir(getProperties().getOutDir());
	}

		
	private void removeDir(String dir) throws IOException{
		File dirin = new File(dir);
		try{
		FileUtils.deleteDirectory(dirin);
		}catch(Exception ex){
			//Retry
			//FileUtils.deleteDirectory(dirin);
			logger.error("ex: "+ex.getMessage());
		}
		dirin.mkdir();
	}

	public boolean copyOriginalBin(String inDir,String mutatorIdentifier) throws IOException {
		boolean copied = false;
		if(inDir != null){
			File original = new File(inDir);
			File dest = new File(getOutDirWithPrefix(mutatorIdentifier));
			dest.mkdirs();
			FileUtils.copyDirectory(original, dest);
			copied = true;
		}
		return copied;
	}
	
	public String getOutDirWithPrefix(String currentMutatorIdentifier) {
		return getProperties().getOutDir() + File.separator+ currentMutatorIdentifier;
	}
	public String getInDirWithPrefix(String currentMutatorIdentifier) {
		return getProperties().getInDir() +File.separator+ currentMutatorIdentifier;
	}
	
	public void copyData(String currentMutatorIdentifier) throws IOException{
		if(getProperties().getDataFolder() == null)
			return;
		File source = new File(	getProperties().getDataFolder() );
		File destFile = new File(getOutDirWithPrefix(currentMutatorIdentifier)+File.separator+source.getName());
		destFile.mkdirs();
		FileUtils.copyDirectory(source, destFile);
		
	}
	
	public URL[] getURLforMutation(String currentMutatorIdentifier) throws MalformedURLException{
		
		List<URL> classpath = new ArrayList<URL>(getProperties().getDependencies());
		//bin
		URL urlBin = new File(getOutDirWithPrefix(currentMutatorIdentifier)).toURI().toURL();
		classpath.add(urlBin);

		URL[] cp = classpath.toArray(new URL[0]);
		return cp;
	}
	
	
	/**
	 * TODO: maybe we can filter some of them. Only the Parent?
	 * */
	public List<SuspiciousCode> getSuspicious(String packageToInst, String mutatorIdentifier) throws FileNotFoundException, IOException {

		if (getProperties().getFailingTestCases() == null
				|| "".equals(getProperties().getFailingTestCases().isEmpty())) {
			new IllegalArgumentException("Test Class can not be empty");
		}
		
		String pathOutDir = getOutDirWithPrefix(mutatorIdentifier);
		
		
		List<String> testcasesToExecute = null;
				
		if(ConfigurationProperties.getPropertyBool("regressionforfaultlocalization")){
			testcasesToExecute = getProperties().getRegressionTestCases();
		}
		else{
			testcasesToExecute = getProperties().getFailingTestCases();
		}
		logger.info("-Executing Gzoltar classpath: " + pathOutDir+ " from "+
				+ testcasesToExecute.size() +" classes with test cases");

		List<String> listTOInst = new ArrayList<String>();
		listTOInst.add(packageToInst);

		HashSet<String> classPathForGZoltar = new HashSet<String>();
		classPathForGZoltar.add(pathOutDir);
		for(URL dep : getProperties().getDependencies()){
			classPathForGZoltar.add(dep.getPath());
		};
		
		
		List<SuspiciousCode> suspiciousStatemens = faultLocalizationFacade.searchGZoltar(
				pathOutDir, testcasesToExecute, listTOInst, classPathForGZoltar, 
				ConfigurationProperties.getProperty("location")+"/"+ConfigurationProperties.getProperty("srcjavafolder"));
		return suspiciousStatemens;
	}

	public void premutationSetupDirs(String srcInput) throws Exception {
		this.cleanMutationResultDirectories(srcInput);
		this.copyOriginalCode(srcInput);
		
	}
	/**
	 * 
	 * @param mutatorIdentifier
	 * @throws IOException
	 */
	public void setupDirProgramVariable(String mutatorIdentifier) throws IOException {
		this.copyOriginalCode(mutatorIdentifier);// NEW ADDED
		// TODO: modify, only create directory, empty
		this.copyOriginalBin(getProperties().getOriginalAppBinDir(),mutatorIdentifier);// NEW ADDED
	    this.copyOriginalBin(getProperties().getOriginalTestBinDir(),mutatorIdentifier);// NEW ADDED
		this.copyData(mutatorIdentifier);

	}
	
	public ProjectConfiguration getProperties() {
		return setUpProperties;
	}

	public void setProperties(ProjectConfiguration properties) {
		this.setUpProperties = properties;
	}

	public FaultLocalizationFacade getFaultLocalizationFacade() {
		return faultLocalizationFacade;
	}
	public static URL[] classpathFrom(String classpath) {
		String[] folderNames = classpath.split(File.pathSeparator);
		URL[] folders = new URL[folderNames.length];
		int index = 0;
		for (String folderName : folderNames) {
			folders[index] = urlFrom(folderName);
			index += 1;
		}
		return folders;
	}
	
	public static URL urlFrom(String path) {
		URL url = null;
		try {
			url = new File(path).toURI().toURL();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return url;
	}


	public void calculateRegression(String defaultVariant) {
		String classPath = getOutDirWithPrefix(defaultVariant);
		String[] testClassesRegression = new TestClassesFinder().findIn(classpathFrom(classPath), false);
		List<String> tr = new ArrayList<>();
		String s = "";
		for (String tcr : testClassesRegression) {
			s+=tcr+File.pathSeparator;
			tr.add(tcr);
		}
		ConfigurationProperties.properties.setProperty("regressionTest", s);
		getProperties().setRegressionCases(tr);
	}
}
