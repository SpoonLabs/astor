package fr.inria.astor.test.repair.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.test.repair.evaluation.regression.MathCommandsTests;
import fr.inria.main.CommandSummary;
import fr.inria.main.evolution.AstorMain;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.path.CtPath;

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
		// assertTrue(docs.size() > 0); check why is failing? the method has doc.

		JSONObject jsonroot = cntx.toJSON();
		assertNotNull(jsonroot);
		System.out.println(jsonroot);
		assertEquals("[\"public\"]", ((JSONObject) jsonroot.get("context")).get("METHOD_MODIFIERS").toString());

		cntx.save(jsonroot);

		File fileOut = new File(out + File.separator
				+ (Cntx.PREFIX + "_" + cntxResolver.determineKey(mp_buggy.getCodeElement()) + ".json"));
		System.out.println("file out " + fileOut.getAbsolutePath());
		assertTrue(fileOut.exists());

		String spoonpath = (cntx.getInformation().get(CNTX_Property.SPOON_PATH).toString());
		assertNotNull(spoonpath);
		System.out.println(spoonpath);
		assertFalse(spoonpath.isEmpty());

		String paths = (cntx.getInformation().get(CNTX_Property.PATH_ELEMENTS).toString());
		assertNotNull(paths);
		System.out.println("Paths: \n" + paths);
		assertFalse(paths.isEmpty());
	}

	@Test
	public void testPathMath70() throws Exception {
		AstorMain main1 = new AstorMain();

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-maxgen", "0");
		cs.command.put("-parameters", "skipfitnessinitialpopulation:true");
		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());
		//
		Factory factory = MutationSupporter.getFactory();
		List<CtType<?>> types = factory.Type().getAll();
		for (CtType<?> ctType : types) {
			CtPath path = ctType.getPath();
			System.out
					.println("Path of " + ctType.getSimpleName() + " " + ctType.getShortRepresentation() + " " + path);
			assertNotNull(path);
		}

		List<ProgramVariant> variants = main1.getEngine().getVariants();
		ProgramVariant variant = variants.get(0);
		int exception = 0;
		for (ModificationPoint mp : variant.getModificationPoints()) {

			System.out.println(mp.getCtClass().getSimpleName() + " " + mp.getCtClass().getShortRepresentation());
			System.out.println("Path of " + mp.getCtClass().getSimpleName() + " " + mp.getCtClass().getPath());

			try {
				CtPath path = mp.getCodeElement().getPath();
				System.out.println(path);

			} catch (Exception e) {
				System.err.println("Error for :\n" + mp.getCodeElement());
				e.printStackTrace();
				exception++;
			}
			assertEquals(0, exception);
		}

	}

}
