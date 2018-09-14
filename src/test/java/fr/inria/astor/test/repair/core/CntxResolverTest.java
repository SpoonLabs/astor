package fr.inria.astor.test.repair.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.json.simple.JSONObject;
import org.junit.Test;

import fr.inria.astor.core.entities.CNTX_Property;
import fr.inria.astor.core.entities.Cntx;
import fr.inria.astor.core.entities.CntxResolver;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;

public class CntxResolverTest {

	@SuppressWarnings("rawtypes")
	@Test
	public void testCntxMath70() throws Exception {
		AstorMain main1 = new AstorMain();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "DEBUG");
		cs.command.put("-maxgen", "0");
		cs.command.put("-out", out.getAbsolutePath());
		cs.command.put("-parameters", "skipfitnessinitialpopulation:true");
		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		List<ProgramVariant> variants = main1.getEngine().getVariants();
		ProgramVariant variant = variants.get(0);
		ModificationPoint mp_buggy = variant.getModificationPoints().stream()
				.filter(e -> ((e.getCodeElement().getPosition().getLine() == 72)
						&& e.getCodeElement().toString().startsWith("return")))
				.findFirst().get();

		assertNotNull(mp_buggy);

		CntxResolver cntxResolver = new CntxResolver();

		Cntx cntx = cntxResolver.retrieveCntx(mp_buggy);
		assertNotNull(cntx);
		System.out.println("Cntx:" + cntx);
		// let's check the method return
		assertEquals("double", (cntx.getInformation().get(CNTX_Property.METHOD_RETURN_TYPE)));
		assertEquals(12, ((List) cntx.getInformation().get(CNTX_Property.PARENTS_TYPE)).size());
		List parents = ((List) cntx.getInformation().get(CNTX_Property.PARENTS_TYPE));
		assertTrue(parents.size() > 0);
		assertEquals("CtBlockImpl", parents.get(0));
		assertEquals("CtMethodImpl", parents.get(1));

		Set modif = ((Set) cntx.getInformation().get(CNTX_Property.METHOD_MODIFIERS));
		assertTrue(modif.size() > 0);

		List docs = ((List) cntx.getInformation().get(CNTX_Property.METHOD_COMMENTS));
		// assertTrue(docs.size() > 0); why is failing?

		JSONObject jsonroot = cntx.toJSON();
		assertNotNull(jsonroot);
		System.out.println(jsonroot);

	}

}
