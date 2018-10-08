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
import org.junit.Ignore;
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
import spoon.SpoonModelBuilder;
import spoon.compiler.SpoonResource;
import spoon.compiler.SpoonResourceHelper;
import spoon.reflect.code.BinaryOperatorKind;
import spoon.reflect.code.CtAssignment;
import spoon.reflect.code.CtBlock;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.UnaryOperatorKind;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtMethod;
import spoon.reflect.declaration.CtType;
import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import spoon.reflect.path.CtPath;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.VirtualFile;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

public class CntxResolverTest {

	@SuppressWarnings("rawtypes")
	@Test
	public void testCntxMath70_return_stm() throws Exception {
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

	@SuppressWarnings("rawtypes")
	@Test
	public void testCntxMath70_bin_ops() throws Exception {
		AstorMain main1 = new AstorMain();
		File out = new File(ConfigurationProperties.getProperty("workingDirectory"));

		CommandSummary cs = MathCommandsTests.getMath70Command();
		cs.command.put("-stopfirst", "true");
		cs.command.put("-loglevel", "DEBUG");
		cs.command.put("-maxgen", "0");
		cs.command.put("-out", out.getAbsolutePath());
		cs.command.put("-flthreshold", "0.1");
		cs.command.put("-parameters", "skipfitnessinitialpopulation:true");
		System.out.println(Arrays.toString(cs.flat()));
		main1.execute(cs.flat());

		List<ProgramVariant> variants = main1.getEngine().getVariants();
		ProgramVariant variant = variants.get(0);
		ModificationPoint mp_buggy = variant.getModificationPoints().stream()
				.filter(e -> ((e.getCodeElement().getPosition().getLine() == 87)
						&& e.getCodeElement().toString().startsWith("while")))
				.findFirst().get();

		assertNotNull(mp_buggy);

		CntxResolver cntxResolver = new CntxResolver();

		Cntx cntx = cntxResolver.retrieveCntx(mp_buggy);
		assertNotNull(cntx);
		System.out.println("Cntx:" + cntx);
		// let's check the method return
		assertEquals("CtWhileImpl", (cntx.getInformation().get(CNTX_Property.TYPE)));
		List opsBin = ((List) cntx.getInformation().get(CNTX_Property.involved_relation_bin_operators));
		assertTrue(opsBin.size() > 0);
		assertTrue(opsBin.size() == 1);

		assertTrue(Boolean
				.parseBoolean(cntx.getInformation().get(CNTX_Property.involve_LT_relation_operators).toString()));
		assertFalse(Boolean
				.parseBoolean(cntx.getInformation().get(CNTX_Property.involve_LE_relation_operators).toString()));

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

	@Test
	public void testPropertyBinop() {

		String content = "" + "class X {" + "public Object foo() {" + " Integer.toString(10);"
				+ " int a = 1,b = 1,c = 1,d = 1;" + " a = a + b / c +d  ; " + " return null;" + "}};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);
		CtElement stassig = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("a ="))
				.findFirst().get();
		System.out.println(stassig);
		assertTrue(stassig instanceof CtAssignment);

		CntxResolver cntxResolver = new CntxResolver();

		Cntx cntx = cntxResolver.retrieveCntx(stassig);

		assertEquals(Boolean.TRUE, cntx.getInformation().get(CNTX_Property.involve_PLUS_relation_operators));
		assertEquals(Boolean.FALSE, cntx.getInformation().get(CNTX_Property.involve_MINUS_relation_operators));
		assertEquals(Boolean.TRUE, cntx.getInformation().get(CNTX_Property.involve_DIV_relation_operators));
		assertEquals(Boolean.FALSE, cntx.getInformation().get(CNTX_Property.involve_MUL_relation_operators));

		List<String> ops = (List<String>) cntx.getInformation().get(CNTX_Property.involved_relation_bin_operators);
		assertTrue(ops.contains(BinaryOperatorKind.PLUS.toString()));
		assertFalse(ops.contains(BinaryOperatorKind.MINUS.toString()));
	}

	@Test
	public void testPropertyUnaryOp() {

		String content = "" + "class X {" + "public Object foo() {" + " Integer.toString(10);"
				+ " int a = 1,b = 1,c = 1,d = 1;" + " a = a + b / c +d  ; if (!(a>0)){a++;} " + " return null;" + "}};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);
		CtElement stassig = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("if"))
				.findFirst().get();
		System.out.println(stassig);
		assertTrue(stassig instanceof CtIf);

		CntxResolver cntxResolver = new CntxResolver();

		Cntx cntx = cntxResolver.retrieveCntx(stassig);

		List<String> ops = (List<String>) cntx.getInformation().get(CNTX_Property.involved_relation_unary_operators);

		assertTrue(ops.contains(UnaryOperatorKind.NOT.toString()));
		assertFalse(ops.contains(UnaryOperatorKind.POSTDEC.toString()));

		assertEquals(Boolean.TRUE, cntx.getInformation().get(CNTX_Property.involve_NOT_relation_operators));
		assertEquals(Boolean.FALSE, cntx.getInformation().get(CNTX_Property.involve_INSTANCEOF_relation_operators));
		assertEquals(Boolean.FALSE, cntx.getInformation().get(CNTX_Property.involve_POSTINC_relation_operators));

		CtElement postin = ((CtIf) stassig).getThenStatement();
		Cntx cntxposting = cntxResolver.retrieveCntx((CtElement) ((CtBlock) postin).getStatement(0));

		assertEquals(Boolean.FALSE, cntxposting.getInformation().get(CNTX_Property.involve_NOT_relation_operators));
		assertEquals(Boolean.FALSE,
				cntxposting.getInformation().get(CNTX_Property.involve_INSTANCEOF_relation_operators));
		assertEquals(Boolean.TRUE, cntxposting.getInformation().get(CNTX_Property.involve_POSTINC_relation_operators));

		// postin.getClass().get
	}

	@Test
	public void testProperty_IS_METHOD_RETURN_TYPE_VAR() {

		String content = "" + "class X {" + "public Object foo() {" //
				+ " int a = 1;"//
				+ "int b = a;" + "float f = 0;" + "" + "return f;" + "}" //
				+ "public float getFloat(){return 1.0;}"//
				+ "public double getConvertFloat(int i){return 0.0;}"//
				+ "};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);
		CtElement stassig = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return f"))
				.findFirst().get();
		System.out.println(stassig);
		CntxResolver cntxResolver = new CntxResolver();
		Cntx cntx = cntxResolver.retrieveCntx(stassig);

		assertEquals(Boolean.TRUE, cntx.getInformation().get(CNTX_Property.IS_METHOD_RETURN_TYPE_VAR));
		stassig = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("float f")).findFirst()
				.get();
		cntx = cntxResolver.retrieveCntx(stassig);

		assertEquals(Boolean.FALSE, cntx.getInformation().get(CNTX_Property.IS_METHOD_RETURN_TYPE_VAR));
	}

	@Test
	public void testProperty_IS_METHOD_PARAM_TYPE_VAR() {

		String content = "" + "class X {" + "public Object foo() {" //
				+ " int a = 1;"//
				+ "int b = a;" + "float f = 0; double d = 0;" + "return f;" + "}"
				+ "public float getFloat(){return 1.0;}"//
				+ "public int getConvertFloat(float f){return 1;}"//
				+ "};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);
		CtElement element = method.getBody().getStatement(4);
		System.out.println(element);
		CntxResolver cntxResolver = new CntxResolver();
		Cntx cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.TRUE, cntx.getInformation().get(CNTX_Property.IS_METHOD_PARAM_TYPE_VAR));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("float f")).findFirst()
				.get();
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.FALSE, cntx.getInformation().get(CNTX_Property.IS_METHOD_PARAM_TYPE_VAR));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("double d")).findFirst()
				.get();
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.FALSE, cntx.getInformation().get(CNTX_Property.IS_METHOD_PARAM_TYPE_VAR));

	}

	@Test
	public void testProperty_NUMBER_PRIMITIVE_VARS_IN_STMT() {

		String content = "" + "class X {" + "public Object foo() {" + " String s=null;"//
				+ " int a = 1;"//
				+ "int b = a;" + "b = b+a;" + "s.toString();" + "String d=s;" + "return d.equals(s) || a>b ;" + "}};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);
		CtElement stassig = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("int a"))
				.findFirst().get();
		System.out.println(stassig);
		CntxResolver cntxResolver = new CntxResolver();
		Cntx cntx = cntxResolver.retrieveCntx(stassig);

		assertEquals(0, cntx.getInformation().get(CNTX_Property.NUMBER_PRIMITIVE_VARS_IN_STMT));
		assertEquals(0, cntx.getInformation().get(CNTX_Property.NUMBER_OBJECT_REFERENCE_VARS_IN_STMT));
		assertEquals(0, cntx.getInformation().get(CNTX_Property.NUMBER_TOTAL_VARS_IN_STMT));

		//
		CtElement stm = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("int b"))
				.findFirst().get();

		cntx = cntxResolver.retrieveCntx(stm);

		assertEquals(1, cntx.getInformation().get(CNTX_Property.NUMBER_PRIMITIVE_VARS_IN_STMT));
		assertEquals(0, cntx.getInformation().get(CNTX_Property.NUMBER_OBJECT_REFERENCE_VARS_IN_STMT));
		assertEquals(1, cntx.getInformation().get(CNTX_Property.NUMBER_TOTAL_VARS_IN_STMT));

		stm = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("b =")).findFirst().get();

		cntx = cntxResolver.retrieveCntx(stm);

		assertEquals(2, cntx.getInformation().get(CNTX_Property.NUMBER_PRIMITIVE_VARS_IN_STMT));
		assertEquals(0, cntx.getInformation().get(CNTX_Property.NUMBER_OBJECT_REFERENCE_VARS_IN_STMT));
		assertEquals(2, cntx.getInformation().get(CNTX_Property.NUMBER_TOTAL_VARS_IN_STMT));

		stm = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("s.toString()")).findFirst()
				.get();

		cntx = cntxResolver.retrieveCntx(stm);

		assertEquals(0, cntx.getInformation().get(CNTX_Property.NUMBER_PRIMITIVE_VARS_IN_STMT));
		assertEquals(1, cntx.getInformation().get(CNTX_Property.NUMBER_OBJECT_REFERENCE_VARS_IN_STMT));
		assertEquals(1, cntx.getInformation().get(CNTX_Property.NUMBER_TOTAL_VARS_IN_STMT));

		stm = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return ")).findFirst()
				.get();

		cntx = cntxResolver.retrieveCntx(stm);

		assertEquals(2, cntx.getInformation().get(CNTX_Property.NUMBER_PRIMITIVE_VARS_IN_STMT));
		assertEquals(2, cntx.getInformation().get(CNTX_Property.NUMBER_OBJECT_REFERENCE_VARS_IN_STMT));
		assertEquals(4, cntx.getInformation().get(CNTX_Property.NUMBER_TOTAL_VARS_IN_STMT));

	}

	@Test
	public void testProperty_HAS_VAR_SIM_NAME() {

		String content = "" + "class X {" + "public Object foo() {" //
				+ " int mysimilar = 1;"//
				+ "int myzimilar = 2;" + "float fiii = (float)myzimilar; double dother = 0;" + "return fiii;" + "}"
				+ "public float getFloat(){return 1.0;}"//
				+ "public int getConvertFloat(float f){return 1;}"//
				+ "};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);
		CtElement element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("float fiii"))
				.findFirst().get();
		System.out.println(element);
		CntxResolver cntxResolver = new CntxResolver();
		Cntx cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.TRUE, cntx.getInformation().get(CNTX_Property.HAS_VAR_SIM_NAME));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("double dother"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.FALSE, cntx.getInformation().get(CNTX_Property.HAS_VAR_SIM_NAME));

	}

	@Test
	public void testProperty_HAS_VAR_SIM_TYPE() {

		String content = "" + "class X {" + "public Object foo() {" //
				+ " int mysimilar = 1;"//
				+ "int myzimilar = 2;"//
				+ "float fiii = (float)myzimilar; " + "double dother = 0;" + "return fiii;" + "}"
				+ "public float getFloat(){return 1.0;}"//
				+ "public int getConvertFloat(float f){return 1;}"//
				+ "};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);
		CtElement element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("float fiii"))
				.findFirst().get();
		System.out.println(element);
		CntxResolver cntxResolver = new CntxResolver();
		Cntx cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.TRUE, cntx.getInformation().get(CNTX_Property.HAS_VAR_SIM_TYPE));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return")).findFirst()
				.get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.FALSE, cntx.getInformation().get(CNTX_Property.HAS_VAR_SIM_TYPE));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("double")).findFirst()
				.get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.FALSE, cntx.getInformation().get(CNTX_Property.HAS_VAR_SIM_TYPE));

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testProperty_HAS_VAR_IN_TRANSFORMATION() {

		String content = "" + "class X {" + "public Object foo() {" //
				+ " float mysimilar = 1;"//
				+ "int myzimilar = 2;"
				+ "float fiii =  getConvertFloat(mysimilar); double dother = 0; double ddother = dother;"
				+ "return mysimilar;" + "}" + "public float getFloat(){return 1.0;}"//
				+ "public int getConvertFloat(float f){return 1;}"//
				+ "};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);
		CtElement element = method.getBody().getStatements().stream()
				.filter(e -> e.toString().startsWith("return mysimilar")).findFirst().get();
		System.out.println(element);
		CntxResolver cntxResolver = new CntxResolver();
		Cntx cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.TRUE, cntx.getInformation().get(CNTX_Property.HAS_VAR_IN_TRANSFORMATION));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("double ddother"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.FALSE, cntx.getInformation().get(CNTX_Property.HAS_VAR_SIM_NAME));

	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	@Test
	public void testProperty_USES_CONSTANT() {

		String content = "" + "class X {" + "public Object foo() {" //
				+ " float mysimilar = 1;"//
				+ "int myzimilar = 2;" + "float fiii =  getConvertFloat(mysimilar);"//
				+ " double dother = 0;"//
				+ " double ddother = dother;"//
				+ "return mysimilar;" + "}" + //
				"public float getFloat(){return 1.0;}"//
				+ "public int getConvertFloat(float f){return 1;}"//
				+ "};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);
		CtElement element = method.getBody().getStatements().stream()
				.filter(e -> e.toString().startsWith("int myzimilar")).findFirst().get();
		System.out.println(element);
		CntxResolver cntxResolver = new CntxResolver();
		Cntx cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.TRUE, cntx.getInformation().get(CNTX_Property.USES_CONSTANT));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("float fiii"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.FALSE, cntx.getInformation().get(CNTX_Property.USES_CONSTANT));

	}

	@Test
	@Ignore
	public void testProperty_USES_ENUM() {

		String content = "" + "class X {" + "public enum MYEN  {ENU1, ENU2;}" + "public Object foo() {" //
				+ " float mysimilar = 1;"//
				+ "int myzimilar = 2;" + //
				"float fiii =  getConvertFloat(MYEN.ENU1);"//
				+ " double dother=0l;"//
				+ " double ddother = dother;" + "return mysimilar;" + "}" + "public float getFloat(){return 1.0;}"//
				+ "public int getConvertFloat(MYEN f){return 1;}"//
				+ "};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);
		CtElement element = method.getBody().getStatements().stream()
				.filter(e -> e.toString().startsWith("int myzimilar")).findFirst().get();
		System.out.println(element);
		CntxResolver cntxResolver = new CntxResolver();
		Cntx cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.FALSE, cntx.getInformation().get(CNTX_Property.USES_ENUM));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("float fiii"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);
		// TODO: Failing:
		assertEquals(Boolean.TRUE, cntx.getInformation().get(CNTX_Property.USES_ENUM));

	}

	@Test
	public void testProperty_NR_OBJECT_ASSIGNED() {

		String content = "" + "class X {" + "public enum MYEN  {ENU1, ENU2;}" + "public Object foo() {" //
				+ " float mysimilar = 1;"//
				+ "Object ob = null;" //
				+ "ob = new String();"//
				+ "String t= ob.toString();" //
				+ "boolean com = (ob == t);" //
				+ "com = (t==true);" + "return ob;" + //
				"};};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);

		CntxResolver cntxResolver = new CntxResolver();
		CtElement element = null;
		Cntx cntx = null;

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("com =")).findFirst()
				.get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);
		assertEquals(0, cntx.getInformation().get(CNTX_Property.NR_OBJECT_ASSIGNED));
		assertEquals(1, cntx.getInformation().get(CNTX_Property.NR_OBJECT_NOT_ASSIGNED));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return ob"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(1, cntx.getInformation().get(CNTX_Property.NR_OBJECT_ASSIGNED));
		assertEquals(0, cntx.getInformation().get(CNTX_Property.NR_OBJECT_NOT_ASSIGNED));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("boolean com"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);
		assertEquals(1, cntx.getInformation().get(CNTX_Property.NR_OBJECT_ASSIGNED));
		assertEquals(1, cntx.getInformation().get(CNTX_Property.NR_OBJECT_NOT_ASSIGNED));

	}

	@Test
	public void testProperty_NR_OBJECT_USED() {

		String content = "" + "class X {" + "public enum MYEN  {ENU1, ENU2;}" + "public Object foo() {" //
				+ " float mysimilar = 1;"//
				+ "Object ob = null;" //
				+ "ob = new String();"//
				+ "String t= ob.toString();" //
				+ "String t2 = null;" //
				+ "boolean com = (ob == t) && (t2 == t);" //
				+ "return ob;" + //
				"};};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);

		CntxResolver cntxResolver = new CntxResolver();
		CtElement element = null;
		Cntx cntx = null;

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("boolean com"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);
		assertEquals(1, cntx.getInformation().get(CNTX_Property.NR_OBJECT_USED));
		assertEquals(2, cntx.getInformation().get(CNTX_Property.NR_OBJECT_NOT_USED));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return ob"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(1, cntx.getInformation().get(CNTX_Property.NR_OBJECT_USED));
		assertEquals(0, cntx.getInformation().get(CNTX_Property.NR_OBJECT_NOT_USED));

	}

	@Test
	public void testProperty_NR_FIElD_INIT_INCOMPLETE_1() {
		// Case: fx from fx (recursive reference)
		String content = "" + "class X {" //
				+ "public X fX = null;" + //
				"public int f1 = 0;" + //
				"private int f2 = 0;" + //

				"public Object foo() {" + //
				" fX = new X();"//
				+ "fX.f1 = 0;" //
				+ "f2 = fX.f2;" + //
				"};};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);

		CntxResolver cntxResolver = new CntxResolver();
		CtElement element = null;
		Cntx cntx = null;

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("f2 = ")).findFirst()
				.get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);
		// field not assigned fX
		assertEquals(Boolean.TRUE, cntx.getInformation().get(CNTX_Property.NR_FIELD_INCOMPLETE_INIT));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProperty_NR_FIElD_INIT_INCOMPLETE_2() {
		/// Case 2: all init (f2 is private so we dont initialize it)

		String content = "" + "class X {" //
				+ "public X fX = null;" + //
				"public int f1 = 0;" + //
				"private int f2 = 0;" + //

				"public Object foo() {" + //
				" fX = new X();"// init the field
				+ "fX.fX = null;"//
				+ "fX.f1 = 0;"//
				+ "int mv ;" + //
				"mv = fX.f2;" + //
				"};};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);

		CntxResolver cntxResolver = new CntxResolver();
		CtElement element = null;
		Cntx cntx = null;

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("mv = ")).findFirst()
				.get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);
		assertEquals(Boolean.FALSE, cntx.getInformation().get(CNTX_Property.NR_FIELD_INCOMPLETE_INIT));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProperty_NR_FIElD_INIT_INCOMPLETE_3() {
		/// Case : missing init of f2

		String content = "" + "class X {" //
				+ "public X fX = null;" + //
				"public int f1 = 0;" + //
				"public int f2 = 0;" + //

				"public Object foo() {" + //
				" fX = new X();"// init the field
				+ "fX.fX = null;"//
				+ "fX.f1 = 0;"//
				+ "int mv ;" + //
				"mv = fX.f2;" + //
				"};};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);

		CntxResolver cntxResolver = new CntxResolver();
		CtElement element = null;
		Cntx cntx = null;

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("mv = ")).findFirst()
				.get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);
		assertEquals(Boolean.TRUE, cntx.getInformation().get(CNTX_Property.NR_FIELD_INCOMPLETE_INIT));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProperty_NR_FIElD_INIT_INCOMPLETE_4() {
		/// Case all initialized

		String content = "" + "class X {" //
				+ "public X fX = null;" + //
				"public int f1 = 0;" + //
				"public int f2 = 0;" + //

				"public Object foo() {" + //
				" fX = new X();"// init the field
				+ "fX.fX = null;"//
				+ "fX.f1 = 0;"//
				+ "int mv ;" //
				+ "fX.f2 = 0;"//
				+ "mv = fX.f2;" + //
				"};};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);

		CntxResolver cntxResolver = new CntxResolver();
		CtElement element = null;
		Cntx cntx = null;

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("mv = ")).findFirst()
				.get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);
		assertEquals(Boolean.FALSE, cntx.getInformation().get(CNTX_Property.NR_FIELD_INCOMPLETE_INIT));

	}

	protected CtType getCtType(File file) throws Exception {

		SpoonResource resource = SpoonResourceHelper.createResource(file);
		return getCtType(resource);
	}

	protected CtType getCtType(SpoonResource resource) {
		Factory factory = createFactory();
		factory.getModel().setBuildModelIsFinished(false);
		SpoonModelBuilder compiler = new JDTBasedSpoonCompiler(factory);
		compiler.getFactory().getEnvironment().setLevel("OFF");
		compiler.addInputSource(resource);
		compiler.build();

		if (factory.Type().getAll().size() == 0) {
			return null;
		}

		// let's first take the first type.
		CtType type = factory.Type().getAll().get(0);
		// Now, let's ask to the factory the type (which it will set up the
		// corresponding
		// package)
		return factory.Type().get(type.getQualifiedName());
	}

	protected CtType<?> getCtType(String content) {
		VirtualFile resource = new VirtualFile(content, "/test");
		return getCtType(resource);
	}

	protected Factory createFactory() {
		Factory factory = new FactoryImpl(new DefaultCoreFactory(), new StandardEnvironment());
		factory.getEnvironment().setNoClasspath(true);
		factory.getEnvironment().setCommentEnabled(false);
		return factory;
	}

}
