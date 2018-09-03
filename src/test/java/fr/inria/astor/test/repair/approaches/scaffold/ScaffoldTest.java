package fr.inria.astor.test.repair.approaches.scaffold;

import static org.junit.Assert.assertTrue;

import java.io.File;
import org.apache.log4j.Logger;
import org.junit.Test;

import fr.inria.astor.approaches.scaffold.ScaffoldRepairEngine;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;

public class ScaffoldTest {

	public static Logger log = Logger.getLogger(ScaffoldTest.class.getName());
	
	File out = null;

	public ScaffoldTest() {
		out = new File(ConfigurationProperties.getProperty("workingDirectory"));
	}

	@Test
	public void testExample288() throws Exception {

		AstorMain main1 = new AstorMain();

		CommandSummary cs = new CommandSummary();
		cs.command.put("-mode", "scaffold");
		cs.command.put("-srcjavafolder", "/src/main/");
		cs.command.put("-srctestfolder", "/src/test/");
		cs.command.put("-binjavafolder", "/target/classes");
		cs.command.put("-bintestfolder", "/target/test-classes/");
		cs.command.put("-location", new File("./examples/Math-issue-288/").getCanonicalPath());
		cs.command.put("-dependencies", new File("./examples/Math-issue-288/lib").getCanonicalPath());
		cs.command.put("-maxgen", "0"); 
		cs.command.put("-autocompile", "true"); 

		main1.execute(cs.flat());
		
		ScaffoldRepairEngine repairemgine=(ScaffoldRepairEngine)main1.getEngine();
		
		assertTrue(repairemgine.getVariant().getModificationPoints().size() > 0);
		
		ModificationPoint toExplore=repairemgine.getVariant().getModificationPoints().get(84);
		
		assertTrue(repairemgine.scaffoldGenerationSpecific("OverloadMethodTransform",toExplore,84).size()>1);
		assertTrue(repairemgine.scaffoldGenerationSpecific("ConditionRemoveTransform",toExplore,84).size()>=0);
		assertTrue(repairemgine.scaffoldGenerationSpecific("ConditionMutationTransform",toExplore,84).size()>=0);
		assertTrue(repairemgine.scaffoldGenerationSpecific("ConditionAddTransform",toExplore,84).size()>1);
		assertTrue(repairemgine.scaffoldGenerationSpecific("OperatorTransform",toExplore,84).size()>=0);
		assertTrue(repairemgine.scaffoldGenerationSpecific("ExpressionTransform",toExplore,84).size()>=1);
		
		assertTrue(repairemgine.scaffoldGenerationSpecific("OverloadMethodTransform",toExplore,84).toString().
				indexOf("OMT")!=-1);
		assertTrue(repairemgine.scaffoldGenerationSpecific("ConditionAddTransform",toExplore,84).toString().
				indexOf("CAT")!=-1);
		assertTrue(repairemgine.scaffoldGenerationSpecific("ExpressionTransform",toExplore,84).toString().
				indexOf("ET")!=-1);
	}
	
}
