package fr.inria.astor.core.setup;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;

import spoon.reflect.factory.Factory;



/**
 * Contains the properties of a project
 * All properties are defined in {@link ProjectPropertiesEnum}
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class ProjectConfiguration {

	private Logger logger = Logger.getLogger(ProjectConfiguration.class.getName());

	private Map<ProjectPropertiesEnum,Object> internalProperties = new HashMap<ProjectPropertiesEnum, Object>();
	
	
	public ProjectConfiguration(){
		 this.internalProperties.put(ProjectPropertiesEnum.originalDirSrc,new ArrayList<String>());
		 this.internalProperties.put(ProjectPropertiesEnum.dependencies,new ArrayList<String>());
	}
	public void setProperty(ProjectPropertiesEnum key, Object value){
		this.internalProperties.put(key, value);
	}
	public Object getProperty(ProjectConfiguration key){
		return this.internalProperties.get(key);
	}
	
	public String getStringProperty(ProjectConfiguration key){
		return (String) this.internalProperties.get(key);
	}
	public String getInDir() {
		return (String) this.internalProperties.get(ProjectPropertiesEnum.inDir);
	}

	public void setInDir(String inDir) {
		this.internalProperties.put(ProjectPropertiesEnum.inDir,inDir);
	}


	public String getOutDir() {
		return (String) this.internalProperties.get(ProjectPropertiesEnum.outDir);
	}

	public void setOutDir(String outDir) {
		this.internalProperties.put(ProjectPropertiesEnum.outDir,outDir);
	}

	public String getTestPath() {
		return (String) this.internalProperties.get(ProjectPropertiesEnum.testPath);
	}

	public void setTestPath(String testPath) {
		this.internalProperties.put(ProjectPropertiesEnum.testPath,testPath);
	}

	

	public List<String> getOriginalDirSrc() {
		return (List<String>) this.internalProperties.get(ProjectPropertiesEnum.originalDirSrc);
	}

	public void setOriginalDirSrc(List<String> dirs){
		for (String sub : dirs) {
			this.setOriginalDirSrc(getOriginalProjectRootDir() +sub);
		}
	}
	
	public void setOriginalDirSrc(String originalDir) {
		((List)this.internalProperties.get(ProjectPropertiesEnum.originalDirSrc)).add(originalDir);
	}

	public void setOriginalDir(List<String> originalDir) {
		this.internalProperties.put(ProjectPropertiesEnum.originalDirSrc, originalDir);
	}

	public List<URL> getDependencies() {
		return (List<URL>) this.internalProperties.get(ProjectPropertiesEnum.dependencies);
	}

	public String getDependenciesString() {
		 List<URL> dept =  (List<URL>) this.internalProperties.get(ProjectPropertiesEnum.dependencies);
		 String r = "";
		 for (int i = 0; i < dept.size(); i++) {
			 URL url = dept.get(i);
			 r+= url.getPath();
			 if(i !=  (dept.size()-1)){
				 r+=File.pathSeparator;
			 }
		 }
		 return r;
	}
	
	public void setDependencies(List<URL> dependencies) {
		this.internalProperties.put(ProjectPropertiesEnum.dependencies,dependencies);
	}
	/**
	 * Add the location given as parameters as project dependency.
	 * If the location is a folder it adds all jar contained, if it's a file the method directly add it.
	 * @param path
	 */
	public void addLocationToClasspath(String path){
		File location = new File(path);
		try {
		List cp = ((List)this.internalProperties.get(ProjectPropertiesEnum.dependencies));
		
		if(!location.exists()){
			return;
		}
		if(!location.isDirectory()){
			if(!cp.contains(location.toURI().toURL()))	{	
				cp.add(location.toURI().toURL());
			}
		}
		else{
	
		
		for (File file : location.listFiles()) {
			if(file.getName().endsWith(".jar")){
			
					logger.info("Adding to classpath "+file.getName());
					if(!cp.contains(file.toURI().toURL()))	{	
						cp.add(file.toURI().toURL());
					}
				
			}			
		}
		}
		} catch (MalformedURLException e) {
			throw new IllegalArgumentException(e);
		
		}
		
	}

	public String getTestSuiteClassName() {
		return (String) this.internalProperties.get(ProjectPropertiesEnum.testSuiteClassName);
	}

	public void setTestClass(String testClass) {
		this.internalProperties.put(ProjectPropertiesEnum.testSuiteClassName,testClass);
	}

	public String getOriginalAppBinDir() {
		return (String) this.internalProperties.get(ProjectPropertiesEnum.originalAppBinDir);
	}

	public void setOriginalAppBinDir(String originalDirBin) {
		this.internalProperties.put(ProjectPropertiesEnum.originalAppBinDir,originalDirBin);
	}

  public String getOriginalTestBinDir() {
    return (String) this.internalProperties.get(ProjectPropertiesEnum.originalTestBinDir);
  }

  public void setOriginalTestBinDir(String dir) {
    this.internalProperties.put(ProjectPropertiesEnum.originalTestBinDir,dir);
  }

  public String getLibPath() {
		return (String) this.internalProperties.get(ProjectPropertiesEnum.libPath);
	}
	
	public void setLibPath(String libPath) {
		this.internalProperties.put(ProjectPropertiesEnum.libPath,libPath);
		String[] s = libPath.split(File.pathSeparator);
		for (String pathcomp : s) {
			this.addLocationToClasspath(pathcomp);
		}
	}
	
	

	public String getOriginalProjectRootDir() {
		return (String) this.internalProperties.get(ProjectPropertiesEnum.originalProjectRootDir);
	}

	public void setOriginalProjectRootDir(String originalProjectRootDir) {
		this.internalProperties.put(ProjectPropertiesEnum.originalProjectRootDir,originalProjectRootDir);
	}

	public String getDataFolder() {
		return (String) this.internalProperties.get(ProjectPropertiesEnum.dataFolder);
	}

	public void setDataFolder(String dataFolder) {
		this.internalProperties.put(ProjectPropertiesEnum.dataFolder,dataFolder);
	}

	public List<String> getFailingTestCases() {
		return (List<String>) this.internalProperties.get(ProjectPropertiesEnum.failingTestCases);
	}

	public void setFailingTestCases(List<String> failingTestCases) {
		this.internalProperties.put(ProjectPropertiesEnum.failingTestCases,failingTestCases);
	}

	public void setSpoonFactory(Factory factory) {
		this.internalProperties.put(ProjectPropertiesEnum.spoonFactory,factory);
	}
	
	public Factory getSpoonFactory() {
		return (Factory) this.internalProperties.get(ProjectPropertiesEnum.spoonFactory);
	}
	public String getFixid() {
		return (String) this.internalProperties.get(ProjectPropertiesEnum.fixid);
	}
	public void setFixid(String outDir) {
		this.internalProperties.put(ProjectPropertiesEnum.fixid,outDir);
	}
	public String getExperimentName() {
		return (String) this.internalProperties.get(ProjectPropertiesEnum.experimentName);
	}
	public void setExperimentName(String outDir) {
		this.internalProperties.put(ProjectPropertiesEnum.experimentName,outDir);
	}
	
	public String getPackageToInstrument() {
		return (String) this.internalProperties.get(ProjectPropertiesEnum.packageToInstrument);
	}
	
	public void setPackageToInstrument(String outDir) {
		this.internalProperties.put(ProjectPropertiesEnum.packageToInstrument,outDir);
	}
	
	public static boolean validJDK(){
		String javaPath= ConfigurationProperties.getProperty("jvm4testexecution");
		File javaFolder = new File(javaPath);
		if(javaFolder.exists()){
			return true;
		}
		return false;
	}
	
	
}
