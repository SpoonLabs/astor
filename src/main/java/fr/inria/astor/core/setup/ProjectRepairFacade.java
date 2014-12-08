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

import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import fr.inria.astor.core.faultlocalization.FaultLocalizationFacade;
import fr.inria.astor.core.faultlocalization.SuspiciousCode;
/**
 *  
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class ProjectRepairFacade {

	//Logger logger = Logger.getLogger(ProjectRepairFacade.class.getName());
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
	public synchronized void init(String currentMutatorIdentifier) throws IOException {
		
		cleanMutationResultDirectories();
		//cleanMutationResultDirectories(currentMutatorIdentifier);//cleanAllDirectories();//TODO
		copyOriginalCode(currentMutatorIdentifier);

		boolean copied1 = copyOriginalBin(getProperties().getOriginalAppBinDir(),currentMutatorIdentifier);// NEW
																									// ADDED
		boolean copied2 = copyOriginalBin(getProperties().getOriginalTestBinDir(),currentMutatorIdentifier);// NEW
																									// ADDED

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
		//src
	//	URL urlSrc = new File(getInDirWithPrefix(currentMutatorIdentifier)).toURI().toURL();
	//	classpath.add(urlSrc);//
		URL[] cp = classpath.toArray(new URL[0]);
		return cp;
	}
	
	
	/**
	 * TODO: maybe we can filter some of them. Only the Parent?
	 * */
	public List<SuspiciousCode> getSuspicious(String packageToInst, String mutatorIdentifier) throws FileNotFoundException, IOException {

		if (getProperties().getTestSuiteClassName() == null
				|| "".equals(getProperties().getTestSuiteClassName().trim())) {
			new IllegalArgumentException("Test Class can not be empty");
		}
		logger.info("Getting Suspicious from: " + getOutDirWithPrefix(mutatorIdentifier) + " from test "
				+ getProperties().getTestSuiteClassName());
		// FaultLocalizationFacade candFault = new FaultLocalizationFacade();
		List<String> listTOtest = new ArrayList<String>();
		listTOtest.add(getProperties().getTestSuiteClassName());

		List<String> listTOInst = new ArrayList<String>();
		listTOInst.add(packageToInst);

		HashSet<String> hs = new HashSet<String>();
		hs.add(getOutDirWithPrefix(mutatorIdentifier));

		List<SuspiciousCode> suspiciousStatemens = faultLocalizationFacade.searchGZoltar(
				getOutDirWithPrefix(mutatorIdentifier), listTOtest, listTOInst, hs);
		return suspiciousStatemens;
	}

	public void premutationSetupDirs(String srcInput) throws Exception {
		this.cleanMutationResultDirectories(srcInput);
		this.copyOriginalCode(srcInput);
		//this.setupDirs(srcInput);
	
	}
	/**
	 * 
	 * @param mutatorIdentifier
	 * @throws IOException
	 */
	public void setupDirProgramVariable(String mutatorIdentifier) throws IOException {
		// Set the number of instance
		//this.setMutationId(instance.getId());
		this.copyOriginalCode(mutatorIdentifier);// NEW ADDED
		// TODO: modify, only create directory, empty
		this.copyOriginalBin(getProperties().getOriginalAppBinDir(),mutatorIdentifier);// NEW ADDED
	    this.copyOriginalBin(getProperties().getOriginalTestBinDir(),mutatorIdentifier);// NEW ADDED
		this.copyData(mutatorIdentifier);
		//this.setupDirs(mutatorIdentifier);
		//this.createClassLoadInCurrentThread();
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

	public static Factory createFactory() {
		StandardEnvironment env = new StandardEnvironment();
		Factory factory = new FactoryImpl(new DefaultCoreFactory(), env);

		// environment initialization
		env.setComplianceLevel(6);// 6//7//4
		env.setVerbose(false);
		env.setDebug(true);// false
		env.setTabulationSize(5);
		env.useTabulations(true);
		env.useSourceCodeFragments(false);

		return factory;
	}

}
