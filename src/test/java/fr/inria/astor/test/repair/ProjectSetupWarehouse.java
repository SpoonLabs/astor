package fr.inria.astor.test.repair;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectConfiguration;
import fr.inria.astor.core.setup.ProjectRepairFacade;
/**
 * This Class keeps all the parameters of the project to be manipulated
 * 
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class ProjectSetupWarehouse {



	public static ProjectRepairFacade getXMLSecurityConfiguration() throws Exception {
		String inResult = ConfigurationProperties.getProperty("workingDirectory")+"/src/";
		String outResult = ConfigurationProperties.getProperty("workingDirectory")+"/bin/";
		String originalProjectRoot = "C:/Personal/develop/workspaceProjectTest/testProjectXMLV2/";
		List<String> src = Arrays.asList(new String[]{"src","src_samples","src_unitTests"});
		String libdir = originalProjectRoot+"lib";
		String originalBin = originalProjectRoot + "classes";
		String mainClassTest = "org.apache.xml.security.test.AllTests";
		
		ProjectConfiguration properties = new ProjectConfiguration();
		properties.setInDir(inResult);
		properties.setOutDir(outResult);
		properties.setTestClass(mainClassTest);
		properties.setOriginalAppBinDir(originalBin);
		properties.setOriginalProjectRootDir(originalProjectRoot);
		properties.setOriginalDirSrc(src);
		properties.setLibPath(libdir);
		
		ProjectRepairFacade ce = new ProjectRepairFacade(properties);
		
		return ce;
	}
	
	public static ProjectRepairFacade getXMLSecurityConfigurationSP() throws Exception {
		String inResult = ConfigurationProperties.getProperty("workingDirectory")+"/src/";
		String outResult = ConfigurationProperties.getProperty("workingDirectory")+"/bin/";
		String originalProjectRoot = "C:/Personal/develop/workspaceProjectTest/testProjectXMLV2-sp/";
		List<String> src = Arrays.asList(new String[]{"src"});
		String libdir = originalProjectRoot+"lib";
		String originalBin = originalProjectRoot + "classes";
		String mainClassTest = "org.apache.xml.security.test.AllTests";
		
		ProjectConfiguration properties = new ProjectConfiguration();
		properties.setInDir(inResult);
		properties.setOutDir(outResult);
		properties.setTestClass(mainClassTest);
		properties.setOriginalAppBinDir(originalBin);
		properties.setOriginalProjectRootDir(originalProjectRoot);
		properties.setOriginalDirSrc(src);
		properties.setLibPath(libdir);
		
		ProjectRepairFacade ce = new ProjectRepairFacade(properties);
		
		return ce;
	}
	
	public static ProjectRepairFacade getDummyDependenciesProject() throws Exception {
		String inResult = ConfigurationProperties.getProperty("workingDirectory")+"/src/";
		String outResult = ConfigurationProperties.getProperty("workingDirectory")+"/bin/";
		URL f = ClassLoader.getSystemResource("testProject2/");
		String originalProjectRoot = f.getFile();
		
		List<String> src = Arrays.asList(new String[]{"src/main/java/","src/test/java"});
		String libdir = originalProjectRoot+File.separator+"lib";
		String mainClassTest = "org.test1.AllTests";
		
		ProjectConfiguration properties = new ProjectConfiguration();
		properties.setInDir(inResult);
		properties.setOutDir(outResult);
		properties.setTestClass(mainClassTest);
		//properties.setOriginalDirBin(originalbin);
		properties.setOriginalProjectRootDir(originalProjectRoot);
		properties.setOriginalDirSrc(src);
		properties.setLibPath(libdir);
		ProjectRepairFacade ce = new ProjectRepairFacade(properties);
		
		return ce;
	}
	
	
  public static ProjectRepairFacade getDummySingleProject() throws Exception {
    return getDummySingleProject("genprog", ClassLoader.getSystemResource("testProject/"));
  }
    
  public static ProjectRepairFacade getDummySingleProject(String name) throws Exception {
    return getDummySingleProject(name, ClassLoader.getSystemResource(name));
  }

  public static ProjectRepairFacade getDummySingleProject(String prefix, URL appCodeUrl) throws Exception {
		String inResult = ConfigurationProperties.getProperty("workingDirectory")+"/"+prefix+"/in/";
		String outResult = ConfigurationProperties.getProperty("workingDirectory")+"/"+prefix+"/out/";
		String originalProjectRoot = appCodeUrl.getFile();
		System.out.println(originalProjectRoot);
		// Maven convention
		List<String> src = Arrays.asList(new String[]{"src/main/java/","src/test/java"});
		String libdir = originalProjectRoot+"lib";
		String mainClassTest = "org.test1.AllTests";//"test.AllTests";
		
		ProjectConfiguration properties = new ProjectConfiguration();
		properties.setInDir(inResult);
		properties.setOutDir(outResult);
		properties.setTestClass(mainClassTest);
		properties.setOriginalAppBinDir(originalProjectRoot+ "/target/classes");
		properties.setOriginalTestBinDir(originalProjectRoot+ "/target/test-classes");
		properties.setOriginalProjectRootDir(originalProjectRoot);
		properties.setOriginalDirSrc(src);
		properties.setLibPath(libdir);
		ProjectRepairFacade ce = new ProjectRepairFacade(properties);
		
		return ce;
	}
	
	
	public static ProjectRepairFacade getRhinoProject184107() throws Exception {
		String inResult = ConfigurationProperties.getProperty("workingDirectory")+"/src/";
		String outResult = ConfigurationProperties.getProperty("workingDirectory")+"/bin/";
		String originalProjectRoot = "C:/Personal/develop/workspaceIbugs/rhinopre184107/";
	
		List<String> src = Arrays.asList(new String[]{"src/","toolsrc/"});
		String libdir = originalProjectRoot+"lib";
		String mainClassTest = "";
	
		ProjectConfiguration properties = new ProjectConfiguration();
		properties.setInDir(inResult);
		properties.setOutDir(outResult);
		properties.setTestClass(mainClassTest);
		//properties.setOriginalDirBin(binfolder);
		
		properties.setOriginalProjectRootDir(originalProjectRoot);
		properties.setOriginalDirSrc(src);
		properties.setLibPath(libdir);
		ProjectRepairFacade ce = new ProjectRepairFacade(properties);
		
		return ce;
	}
	
	public static ProjectRepairFacade getRhinoProject(String location,String fixid, String singletest) throws Exception {
		String inResult = ConfigurationProperties.getProperty("workingDirectory")+"/src/";
		String outResult = ConfigurationProperties.getProperty("workingDirectory")+"/bin/";
		String originalProjectRoot = location + File.separator+"rhinopre"+fixid + File.separator;//"C:/Personal/develop/workspaceIbugs/rhinopre184107/";
	
		List<String> src = Arrays.asList(new String[]{"src/","toolsrc/"});
		String libdir = originalProjectRoot+"lib";
		String mainClassTest = originalProjectRoot+File.separator+singletest;
	
		ProjectConfiguration properties = new ProjectConfiguration();
		properties.setInDir(inResult);
		properties.setOutDir(outResult);
		properties.setTestClass(mainClassTest);
		//properties.setOriginalDirBin(binfolder);
		properties.setOriginalProjectRootDir(originalProjectRoot);
		properties.setOriginalDirSrc(src);
		properties.setLibPath(libdir);
		ProjectRepairFacade ce = new ProjectRepairFacade(properties);
		
		return ce;
	}
	public static ProjectRepairFacade getProject(
			String location, 
			String method,
			String regressiontest,
			List<String> failingTestCases,
			String dependencies, boolean srcWithMain) throws Exception {
		//String location = ConfigurationProperties.getProperty("version-location");
		File locFile = new File(location);
		String projectname = locFile.getName();
		
		return getProject(location, projectname, method, regressiontest, failingTestCases, dependencies, srcWithMain);
	}
	
	
	public static ProjectRepairFacade getProject(
			String location, 
			String projectIdentifier, 
			String method,
			String regressionTest,
			List<String> failingTestCases,
			String dependencies, boolean srcWithMain) 
					throws Exception {
		
		if(projectIdentifier == null){
			File locFile = new File(location);
			projectIdentifier = locFile.getName();
		}
		
		
		String key = File.separator+method+ "-"+projectIdentifier +File.separator;
		String inResult = ConfigurationProperties.getProperty("workingDirectory") + key + "/src/";
		String outResult = ConfigurationProperties.getProperty("workingDirectory") + key + "/bin/";
		String originalProjectRoot = location + File.separator+ projectIdentifier + File.separator;
	
		List<String> src = determineMavenFolders(srcWithMain,originalProjectRoot);
		
		String libdir = dependencies;
	
		String mainClassTest = regressionTest;
		
		ProjectConfiguration properties = new ProjectConfiguration();
		properties.setInDir(inResult);
		properties.setOutDir(outResult);
		properties.setTestClass(mainClassTest);
		properties.setOriginalAppBinDir(originalProjectRoot+ "/target/classes");
		properties.setOriginalTestBinDir(originalProjectRoot+ "/target/test-classes");
		properties.setFixid(projectIdentifier);
		
		properties.setOriginalProjectRootDir(originalProjectRoot);
		properties.setOriginalDirSrc(src);
		properties.setLibPath(libdir);
		properties.setFailingTestCases(failingTestCases);
		
		properties.setPackageToInstrument(ConfigurationProperties.getProperty("packageToInstrument"));
		
		ProjectRepairFacade ce = new ProjectRepairFacade(properties);
				
		return ce;
	}

	public static List<String> determineMavenFolders(boolean srcWithMain, String originalProjectRoot) {
		File src = new File(originalProjectRoot+File.separator+"src/main/java");
		if(src.exists())
			return Arrays.asList(new String[]{"src/main/java","src/test/java"});
		else{
			return Arrays.asList(new String[]{"src/java","src/test"});
		}
	}
		
	
}
