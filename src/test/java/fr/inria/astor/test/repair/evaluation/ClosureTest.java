package fr.inria.astor.test.repair.evaluation;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Arrays;

import org.junit.Test;

import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class ClosureTest {

	@Test
	public void testClosure1() throws Exception {
		File projectLocation = new File("./examples/closure_1");
		AstorMain main1 = new AstorMain();
		File dirLibs = new File(projectLocation.getAbsolutePath()+File.separator+"/lib/");
		String dep = "";
		System.out.println(dirLibs);
		for (File depend : dirLibs.listFiles()) {
			if(!depend.isDirectory())
				dep+=depend.getAbsolutePath() + File.pathSeparator;
			
		}
		System.out.println("dep: "+dep);
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));
		String[] args = new String[] {//
				"-dependencies", dep,//
				"-mode", "statement", //
				"-location", projectLocation.getAbsolutePath(), //
				"-package", "com.google",
				"-srcjavafolder", "/src/", //
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
/*
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
