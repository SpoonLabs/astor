package fr.inria.astor.test.repair.evaluation.regression;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.log4j.Level;
import org.junit.Before;
import org.junit.Test;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.main.FileLauncher;
import fr.inria.main.evolution.AstorMain;

/**
 * 
 * @author Matias Martinez
 *
 */
public class MockitoTest {

	
	String exampleRoot = "./examples/";
	ClassLoader classLoader = null;
	File dataDir = null;
	
	@Before
	public void setUp(){
		classLoader = getClass().getClassLoader();
		dataDir = new File(classLoader.getResource("data_projects").getPath());
	}
	
	
	
	public List<String> getParticularIssueLibs(File f ){
		if(!f.exists())
			throw new IllegalAccessError(f.getAbsolutePath() + " not found");
		List<String> otherlibs = new ArrayList<>();
		if(f.isFile())
			return otherlibs;
		for(File folder : f.listFiles()){
			if(folder.getName().endsWith(".jar")){
				otherlibs.add(folder.getAbsolutePath());
			}
			else{
				otherlibs.addAll(getParticularIssueLibs(folder));
			}
		}
		return otherlibs;
	}
	
	@Test
	public void testMockito29() throws Exception{
		FileLauncher l = new FileLauncher();
		
		int id = 29;
		System.out.println("##Testing "+29);
		String location = new File(exampleRoot + "/mockito_"+id).getAbsolutePath();
		
		
		String[] args = new String[] { "-mode", "statement", 
				//
				"-location", location, "-flthreshold", "0.5", 
				//
				"-seed", "1", "-maxgen", "50", "-stopfirst", "true", "-maxtime", "100", 
				//
				"-scope", "package",
				
				"-loglevel",Level.ERROR.toString(),
				//"
		};
	
		String sharedlibs = "/Users/matias/develop/defects4j/framework/projects/Mockito/lib/";

		File f = new File(location+File.separator+"lib");
		List<String> otherlibs = getParticularIssueLibs(f);
		
		
		String[] command = l.getCommand(args, new File(dataDir.getAbsolutePath()+"/mockito.json"), id, sharedlibs,otherlibs);
		System.out.println(Arrays.toString(args));

		AstorMain main1 = new AstorMain();

		main1.execute(command);

		List<ProgramVariant> solutions = main1.getEngine().getSolutions();
		assertTrue(solutions.size() > 0);
		assertEquals(1, solutions.size());
	}
}
