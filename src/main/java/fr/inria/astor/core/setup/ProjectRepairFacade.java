package fr.inria.astor.core.setup;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.faultlocalization.IFaultLocalization;
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
	 * @param currentMutatorIdentifier
	 * @throws IOException
	 */
	public synchronized void setupTempDirectories(String currentMutatorIdentifier) throws IOException {
		
		cleanMutationResultDirectories();
		
		copyOriginalCode(currentMutatorIdentifier);
		
		try{
			
		String originalAppBinDir = getProperties().getOriginalAppBinDir();
		
		copyOriginalBin(originalAppBinDir,currentMutatorIdentifier);// NEW
																									// ADDED
		copyOriginalBin(getProperties().getOriginalTestBinDir(),currentMutatorIdentifier);// NEW
		
		}catch(Exception e){
			throw new RuntimeException(e);
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
	
	public List<SuspiciousCode> calculateSuspicious(IFaultLocalization faultLocalization) throws Exception{
		List<SuspiciousCode> candidates = this.calculateSuspicious(
				faultLocalization,
				ConfigurationProperties.getProperty("location")+File.separator+ConfigurationProperties.getProperty("srcjavafolder"),
				getOutDirWithPrefix(ProgramVariant.DEFAULT_ORIGINAL_VARIANT),
				ConfigurationProperties.getProperty("packageToInstrument"), 
				ProgramVariant.DEFAULT_ORIGINAL_VARIANT,
				getProperties().getFailingTestCases(),
				getProperties().getRegressionTestCases(),
				ConfigurationProperties.getPropertyBool("regressionforfaultlocalization")
				)
				;
		return candidates;
	}
	
	
	public List<SuspiciousCode> calculateSuspicious(
						IFaultLocalization faultLocalization,	
						String locationSrc,
						String locationBytecode,
						String packageToInst, 
						String mutatorIdentifier,
						List<String> failingTest,
						List<String> allTest,
						boolean mustRunAllTest
					) throws Exception {
		
		if(faultLocalization == null)
			throw new IllegalArgumentException("Fault localization is null");
		
		List<String> testcasesToExecute = null;
				
		if(mustRunAllTest){
			testcasesToExecute = allTest;
		}
		else{
			testcasesToExecute = failingTest;
		}
		
		if (testcasesToExecute == null
				|| testcasesToExecute.isEmpty()) {
			new IllegalArgumentException("Astor needs at least one test case for running");
		}
		
		logger.info("-Executing Gzoltar classpath: " + locationBytecode+ " from "+
				+ testcasesToExecute.size() +" classes with test cases");

		List<String> listTOInst = new ArrayList<String>();
		listTOInst.add(packageToInst);

		Set<String> classPath = new HashSet<String>();
		classPath.add(locationBytecode);
		for(URL dep : getProperties().getDependencies()){
			classPath.add(dep.getPath());
		};
		
		
		List<SuspiciousCode> suspiciousStatemens = faultLocalization.searchSuspicious(
				locationBytecode, testcasesToExecute, listTOInst, classPath, 
				locationSrc);
		
		List<SuspiciousCode> filtercandidates = new ArrayList<SuspiciousCode>();

		if (suspiciousStatemens == null || suspiciousStatemens.isEmpty())
			throw new IllegalArgumentException("No suspicious gen for analyze");
		
		for (SuspiciousCode suspiciousCode : suspiciousStatemens) {
			if (!suspiciousCode.getClassName().endsWith("Exception")) {
				filtercandidates.add(suspiciousCode);
			}
		}
		
		return filtercandidates;
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
	
}
