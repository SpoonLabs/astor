package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.evolution.AstorMain;
import spoon.Launcher;
import spoon.SpoonAPI;
import spoon.compiler.SpoonCompiler;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ClosureTest {

	public static Logger log = Logger.getLogger(Thread.currentThread().getName());

	@Test
	public void testClosure1() throws Exception {
		File projectLocation = new File("./examples/closure_1");
		AstorMain main1 = new AstorMain();
		File dirLibs = new File(projectLocation.getAbsolutePath()+File.separator+"/lib/");
		String dep = getDependencies(projectLocation, dirLibs);
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] {//
				"-dependencies", dep,//
				"-mode", "statement", //
				"-location", projectLocation.getAbsolutePath(), //
				//"-package", "com.google",
				"-srcjavafolder", "/src/:/test/", //
				"-srctestfolder", "/test/", //
				"-binjavafolder", "/build/classes/", //
				"-bintestfolder", "/build/test/", //
				"-javacompliancelevel", "6", //
				"-flthreshold", "0.5", //
				"-out", out.getAbsolutePath(), //
				"-scope", "local", //
				"-seed", "10", //
				"-maxgen", "0", //No run
				"-stopfirst", "true", //
				"-maxtime", "100"//

		};
		System.out.println(Arrays.toString(args));
		main1.execute(args);

	}

	private String getDependencies(File projectLocation, File dirLibs) {
		String dep = "";
		System.out.println(dirLibs);
		//Adding dependencies included in the JSON file as "libs"
		for (File depend : dirLibs.listFiles()) {
			if(!depend.isDirectory())
				log.debug(depend.getName());
				dep+=depend.getAbsolutePath() + File.pathSeparator;
			
		}
		// Adding dependencies included in the JSON file as "classpath"
		dep+= projectLocation.getAbsolutePath()  + File.separator+"build"+ File.separator+"classes"+File.separator;
		dep+= File.pathSeparator+projectLocation.getAbsolutePath()  + File.separator+"build"+ File.separator+"test"+File.separator;
		dep+= File.pathSeparator+projectLocation.getAbsolutePath()  + File.separator+"build"+ File.separator+"lib"+File.separator+"rhino.jar";
		
		log.debug("dep: "+dep);
		return dep;
	}
	@Test
	public void testSpoonClosure1(){
		File projectLocation = new File("./examples/closure_1");
		AstorMain main1 = new AstorMain();
		File dirLibs = new File(projectLocation.getAbsolutePath()+File.separator+"/lib/");
		String dep = getDependencies(projectLocation, dirLibs);
		Launcher launcher = new Launcher();
		launcher.addInputResource("");
	
		
		Factory factory = launcher.createFactory();
		factory.getEnvironment().setComplianceLevel(6);
		SpoonCompiler compiler  = launcher.createCompiler(factory);
		compiler.setSourceClasspath(dep.split(File.pathSeparator));
		compiler.addInputSource(new File(projectLocation.getAbsolutePath()+File.separator+"src"));
		compiler.build();
    
 
		List<CtType<?>> types = factory.Type().getAll();
		assertTrue(types.size() > 0);
		log.debug(types.get(0).toString());
	
	}
	@Test
	public void test2(){
		SpoonAPI spoon = new Launcher();
		File projectLocation = new File("./examples/closure_1");
		
		configureCompiledClasspath(projectLocation.getAbsolutePath()+File.separator, spoon);
		
		spoon.getEnvironment().setComplianceLevel(6);
		spoon.addInputResource(projectLocation.getAbsolutePath() + File.separator+"src");
		spoon.buildModel();
		
	}
	
	
	private static void configureCompiledClasspath(String srcRoot, SpoonAPI spoon){
		
		String libDir = srcRoot+"lib";
		String buildClasses = srcRoot+"build/classes";
		String buildTest = srcRoot+"build/test";
		String buildLib = srcRoot+"build/lib";

		ArrayList<String> paths = new ArrayList<>();
		addIfExists(paths, buildClasses);
		addIfExists(paths, buildTest);
		
		addFilesInDir(paths, buildLib);
		addFilesInDir(paths, libDir);

		String[] classpath = paths.toArray(new String[paths.size()]);

		spoon.getEnvironment().setSourceClasspath(classpath);
	}
	
	private static void addIfExists(List<String> paths, String path){
		File file = new File(path);
		if(file.exists()){
			paths.add(path);
		}
	}

	
	private static void addFilesInDir(List<String> paths, String path){
		File dir = new File(path);
		if(dir.exists()){
			File[] libs = dir.listFiles();
			for(File l : libs){
				paths.add(l.getAbsolutePath());
			}
		}
	}
	
/*
 *From thomas'experiment 
 * /tmp/closure_1_NopolC/build/classes:/tmp/closure_1_NopolC/build/test
 * :/tmp/closure_1_NopolC/build/lib/rhino.jar[NO]:/tmp/closure_1_NopolC/lib/ant.jar[OK]:
 * /tmp/closure_1_NopolC/lib/ant-launcher.jar[ok]:/tmp/closure_1_NopolC/lib/args4j.jar[OK]:
 * /tmp/closure_1_NopolC/lib/guava.jar[OK]:/tmp/closure_1_NopolC/lib/jarjar.ja[OK]r:
 * /tmp/closure_1_NopolC/lib/json.jar[OK]:/tmp/closure_1_NopolC/lib/jsr305.jar[ok]:
 * /tmp/closure_1_NopolC/lib/junit.jar:[OK]
 * /tmp/closure_1_NopolC/lib/caja-r4314.jar[OK]:/tmp/closure_1_NopolC/lib/protobuf-java.jar	[OK]
 */
	
/*
 *  From script
 *  "ant.jar", 
        "ant_deploy.jar", 
        "ant-launcher.jar", 
        "args4j.jar", 
        "args4j_deploy.jar", 
        "guava.jar", 
        "guava-r06.jar", 
        "jarjar.jar", 
        "json.jar", 
        "jsr305.jar", 
        "junit.jar", 
        "caja-r4314.jar", 
        "protobuf_deploy.jar", 
        "protobuf-java.jar", 
        "protobuf-java-2.3.0.jar", 
        "google_common_deploy.jar", 
        "hamcrest-core-1.1.jar", 
        "junit4-core.jar", 
        "junit4-legacy.jar", 
        "libtrunk_rhino_parser_jarjared.jar", 
        "google_compiled_protos_deploy.jar"
 */
}
