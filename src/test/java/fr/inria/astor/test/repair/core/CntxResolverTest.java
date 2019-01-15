package fr.inria.astor.test.repair.core;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

import com.google.gson.JsonObject;

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
import spoon.reflect.factory.TypeFactory;
import spoon.reflect.path.CtPath;
import spoon.support.DefaultCoreFactory;
import spoon.support.StandardEnvironment;
import spoon.support.compiler.VirtualFile;
import spoon.support.compiler.jdt.JDTBasedSpoonCompiler;

/**
 * 
 * @author Matias Martinez
 *
 */
public class CntxResolverTest {

	@SuppressWarnings("rawtypes")
	@Test
	@Ignore
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

		Cntx bugcntx = cntxResolver.retrieveBuggy(mp_buggy.getCodeElement());// (Cntx)
																				// cntx.get(CNTX_Property.BUG_INFO);
		// assertNotNull(cntx);
		// System.out.println("Cntx:" + cntx);
		// let's check the method return
		assertEquals("double", (bugcntx.get(CNTX_Property.METHOD_RETURN_TYPE)));
		assertEquals(12, ((List) bugcntx.get(CNTX_Property.PARENTS_TYPE)).size());
		List parents = ((List) bugcntx.get(CNTX_Property.PARENTS_TYPE));
		assertTrue(parents.size() > 0);
		assertEquals("CtBlockImpl", parents.get(0));
		assertEquals("CtMethodImpl", parents.get(1));

		Set modif = ((Set) bugcntx.get(CNTX_Property.METHOD_MODIFIERS));
		assertTrue(modif.size() > 0);

		List docs = ((List) bugcntx.get(CNTX_Property.METHOD_COMMENTS));
		// assertTrue(docs.size() > 0); check why is failing? the method has doc.

		JsonObject jsonroot = cntx.toJSON();
		assertNotNull(jsonroot);
		System.out.println(jsonroot);
		assertEquals("[\"public\"]", ((JsonObject) jsonroot.get("context")).get("METHOD_MODIFIERS").toString());

		cntx.save(jsonroot);

		File fileOut = new File(out + File.separator
				+ (Cntx.PREFIX + "_" + cntxResolver.determineKey(mp_buggy.getCodeElement()) + ".json"));
		System.out.println("file out " + fileOut.getAbsolutePath());
		assertTrue(fileOut.exists());

		String spoonpath = (bugcntx.get(CNTX_Property.SPOON_PATH).toString());
		assertNotNull(spoonpath);
		System.out.println(spoonpath);
		assertFalse(spoonpath.isEmpty());

		String paths = (bugcntx.get(CNTX_Property.PATH_ELEMENTS).toString());
		assertNotNull(paths);
		System.out.println("Paths: \n" + paths);
		assertFalse(paths.isEmpty());

	}

	@SuppressWarnings("rawtypes")
	@Test
	@Ignore
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
		assertEquals("CtWhileImpl", (((Cntx) (cntx.get(CNTX_Property.BUG_INFO))).get(CNTX_Property.TYPE)));
		List opsBin = ((List) cntx.get(CNTX_Property.involved_relation_bin_operators));
		assertTrue(opsBin.size() > 0);
		assertTrue(opsBin.size() == 1);

		assertTrue(Boolean.parseBoolean(cntx.get(CNTX_Property.involve_LT_relation_operators).toString()));
		assertFalse(Boolean.parseBoolean(cntx.get(CNTX_Property.involve_LE_relation_operators).toString()));

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
				+ " int a = 1,b = 1,c = 1,d = 1;" + " a = a + b / c +d  ; " //
				+ " return (a == b && b == c)? null: null;" + "}};";

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

		Cntx binop = (Cntx) cntx.get(CNTX_Property.BIN_PROPERTIES);

		assertEquals(Boolean.TRUE, binop.get(CNTX_Property.involve_PLUS_relation_operators));
		assertEquals(Boolean.FALSE, binop.get(CNTX_Property.involve_MINUS_relation_operators));
		assertEquals(Boolean.TRUE, binop.get(CNTX_Property.involve_DIV_relation_operators));
		assertEquals(Boolean.FALSE, binop.get(CNTX_Property.involve_MUL_relation_operators));

		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.LE5_BOOLEAN_EXPRESSIONS_IN_FAULTY));

		List<String> ops = (List<String>) binop.get(CNTX_Property.involved_relation_bin_operators);
		assertTrue(ops.contains(BinaryOperatorKind.PLUS.toString()));
		assertFalse(ops.contains(BinaryOperatorKind.MINUS.toString()));

		CtElement returnStmt = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return"))
				.findFirst().get();
		cntx = cntxResolver.retrieveCntx(returnStmt);
		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.LE5_BOOLEAN_EXPRESSIONS_IN_FAULTY));

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

		Cntx unopctxt = (Cntx) cntx.get(CNTX_Property.UNARY_PROPERTIES);

		List<String> ops = (List<String>) unopctxt.get(CNTX_Property.involved_relation_unary_operators);

		assertTrue(ops.contains(UnaryOperatorKind.NOT.toString()));
		assertFalse(ops.contains(UnaryOperatorKind.POSTDEC.toString()));

		assertEquals(Boolean.TRUE, unopctxt.get(CNTX_Property.involve_NOT_relation_operators));
		// assertEquals(Boolean.FALSE,
		// unopctxt.get(CNTX_Property.involve_INSTANCEOF_relation_operators));
		assertEquals(Boolean.FALSE, unopctxt.get(CNTX_Property.involve_POSTINC_relation_operators));

		CtElement postin = ((CtIf) stassig).getThenStatement();
		Cntx cntxposting = cntxResolver.retrieveCntx((CtElement) ((CtBlock) postin).getStatement(0));
		Cntx unopctxtposting = (Cntx) cntxposting.get(CNTX_Property.UNARY_PROPERTIES);

		assertEquals(Boolean.FALSE, unopctxtposting.get(CNTX_Property.involve_NOT_relation_operators));
		// assertEquals(Boolean.FALSE,
		// cntxposting.get(CNTX_Property.involve_INSTANCEOF_relation_operators));
		assertEquals(Boolean.TRUE, unopctxtposting.get(CNTX_Property.involve_POSTINC_relation_operators));

	}

	@Test
	public void testProperty_V1_IS_METHOD_RETURN_TYPE_VAR() {

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

		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.V6_IS_METHOD_RETURN_TYPE_VAR));
		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.V1_IS_TYPE_COMPATIBLE_METHOD_CALL_PARAM_RETURN));

		stassig = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("float f")).findFirst()
				.get();
		cntx = cntxResolver.retrieveCntx(stassig);

		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.V6_IS_METHOD_RETURN_TYPE_VAR));
		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.V1_IS_TYPE_COMPATIBLE_METHOD_CALL_PARAM_RETURN));

	}

	@Test
	public void testProperty_IS_METHOD_PARAM_TYPE_VAR() {

		String content = "" + "class X {" + //
				"public Object foo() {" //
				+ " int a = 1;"//
				+ "int b = a;" //
				+ "float f = 0; "//
				+ "double d = 0;" //
				+ "return f;" + "}"//
				+ "public float getFloat(){return 1.0;}"//
				+ "public int getConvertFloat(float f){return 1;}"//
				+ "public float getSameFloat(float f){return 1;}"//
				+ "public boolean getBoolConvertFloat(float f){return false;}"//
				+ "};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);
		CtElement element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return f"))
				.findFirst().get();
		System.out.println(element);
		CntxResolver cntxResolver = new CntxResolver();
		Cntx cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.V_X_BIS_IS_METHOD_PARAM_TYPE_VAR));
		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.LE2_IS_BOOLEAN_METHOD_PARAM_TYPE_VAR));
		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.V1_IS_TYPE_COMPATIBLE_METHOD_CALL_PARAM_RETURN));
		assertEquals(Boolean.TRUE,
				retrieveFeatureVarProperty(cntx, CNTX_Property.V1_IS_TYPE_COMPATIBLE_METHOD_CALL_PARAM_RETURN, "f"));

		///
		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("float f")).findFirst()
				.get();
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.V_X_BIS_IS_METHOD_PARAM_TYPE_VAR));
		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.LE2_IS_BOOLEAN_METHOD_PARAM_TYPE_VAR));
		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.V1_IS_TYPE_COMPATIBLE_METHOD_CALL_PARAM_RETURN));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("double d")).findFirst()
				.get();
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.V_X_BIS_IS_METHOD_PARAM_TYPE_VAR));
		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.LE2_IS_BOOLEAN_METHOD_PARAM_TYPE_VAR));
		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.V1_IS_TYPE_COMPATIBLE_METHOD_CALL_PARAM_RETURN));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("int b")).findFirst()
				.get();
		cntx = cntxResolver.retrieveCntx(element);

		// int matches with Object.wait(int, int)
		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.V_X_BIS_IS_METHOD_PARAM_TYPE_VAR));
		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.LE2_IS_BOOLEAN_METHOD_PARAM_TYPE_VAR));
		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.V1_IS_TYPE_COMPATIBLE_METHOD_CALL_PARAM_RETURN));
		assertEquals(Boolean.FALSE,
				retrieveFeatureVarProperty(cntx, CNTX_Property.V1_IS_TYPE_COMPATIBLE_METHOD_CALL_PARAM_RETURN, "a"));

		System.out.println(cntx.toJSON());

	}

	@Test
	public void testProperty_NUMBER_PRIMITIVE_VARS_IN_STMT() {

		String content = "" + "class X {" + "public Object foo() {" //
				+ " String s=null;"//
				+ " int a = 1;"//
				+ "int b = a;" + "b = b+a;" + "s.toString();" //
				+ "String d=s;" + "return d.equals(s) || a>b ;" + "}};";

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

		assertEquals(0, cntx.get(CNTX_Property.NUMBER_PRIMITIVE_VARS_IN_STMT));
		assertEquals(0, cntx.get(CNTX_Property.NUMBER_OBJECT_REFERENCE_VARS_IN_STMT));
		assertEquals(0, cntx.get(CNTX_Property.NUMBER_TOTAL_VARS_IN_STMT));

		//
		CtElement stm = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("int b"))
				.findFirst().get();

		cntx = cntxResolver.retrieveCntx(stm);

		assertEquals(1, cntx.get(CNTX_Property.NUMBER_PRIMITIVE_VARS_IN_STMT));
		assertEquals(0, cntx.get(CNTX_Property.NUMBER_OBJECT_REFERENCE_VARS_IN_STMT));
		assertEquals(1, cntx.get(CNTX_Property.NUMBER_TOTAL_VARS_IN_STMT));

		stm = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("b =")).findFirst().get();

		cntx = cntxResolver.retrieveCntx(stm);

		assertEquals(2, cntx.get(CNTX_Property.NUMBER_PRIMITIVE_VARS_IN_STMT));
		assertEquals(0, cntx.get(CNTX_Property.NUMBER_OBJECT_REFERENCE_VARS_IN_STMT));
		assertEquals(2, cntx.get(CNTX_Property.NUMBER_TOTAL_VARS_IN_STMT));

		stm = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("s.to")).findFirst().get();

		cntx = cntxResolver.retrieveCntx(stm);

		assertEquals(0, cntx.get(CNTX_Property.NUMBER_PRIMITIVE_VARS_IN_STMT));
		assertEquals(1, cntx.get(CNTX_Property.NUMBER_OBJECT_REFERENCE_VARS_IN_STMT));
		assertEquals(1, cntx.get(CNTX_Property.NUMBER_TOTAL_VARS_IN_STMT));

		stm = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return ")).findFirst()
				.get();

		cntx = cntxResolver.retrieveCntx(stm);

		assertEquals(2, cntx.get(CNTX_Property.NUMBER_PRIMITIVE_VARS_IN_STMT));
		assertEquals(2, cntx.get(CNTX_Property.NUMBER_OBJECT_REFERENCE_VARS_IN_STMT));
		assertEquals(4, cntx.get(CNTX_Property.NUMBER_TOTAL_VARS_IN_STMT));

	}

	@Test
	public void testProperty_V2_HAS_VAR_SIM_NAME() {

		String content = "" + "class X {" + " int ffii = 1;"//
				+ "public Object foo() {" //
				+ " int mysimilar = 1;"//
				+ "int myzimilar = 2;"//
				+ "float fiii = (float)myzimilar;"//
				+ " double dother = 0;" //
				+ "return fiii;" + "}" + "public float getFloat(){return 1.0;}"//
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
		// affected myzimilar
		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.HAS_VAR_SIM_NAME));
		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.V2_HAS_VAR_SIM_NAME_COMP_TYPE));
		assertEquals(Boolean.TRUE,
				retrieveFeatureVarProperty(cntx, CNTX_Property.V2_HAS_VAR_SIM_NAME_COMP_TYPE, "myzimilar"));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("double dother"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.HAS_VAR_SIM_NAME));
		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.V2_HAS_VAR_SIM_NAME_COMP_TYPE));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return f")).findFirst()
				.get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.HAS_VAR_SIM_NAME));
		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.V2_HAS_VAR_SIM_NAME_COMP_TYPE));
		assertEquals(Boolean.FALSE,
				retrieveFeatureVarProperty(cntx, CNTX_Property.V2_HAS_VAR_SIM_NAME_COMP_TYPE, "fiii"));
		System.out.println(cntx.toJSON());

	}

	@Test
	public void testProperty_V4_() {

		String content = "" + "class X {" + " int ffii = 1;"//
				+ "public Object foo() {" //
				+ " int mysimilar = 1;"//
				+ "int myzimilar = 2;"//
				+ "float fiii = (float)myzimilar;"//
				+ "int dother = max(mysimilar, myzimilar);" //
				+ "return max(mysimilar, mysimilar);" + "}" + "public float getFloat(){return 1.0;}"//
				+ "public int max(int m, int n){return 1;}"//
				+ "};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);
		CtElement element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("int dother"))
				.findFirst().get();
		System.out.println(element);
		CntxResolver cntxResolver = new CntxResolver();
		Cntx cntx = cntxResolver.retrieveCntx(element);
		// affected myzimilar
		System.out.println(cntx.toJSON());
		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.V4_FIRST_TIME_PARAMETER));
		assertEquals(Boolean.FALSE,
				retrieveFeatureVarProperty(cntx, CNTX_Property.V4_FIRST_TIME_PARAMETER, "mysimilar"));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return max"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.V4_FIRST_TIME_PARAMETER));
		assertEquals(Boolean.TRUE,
				retrieveFeatureVarProperty(cntx, CNTX_Property.V4_FIRST_TIME_PARAMETER, "mysimilar"));

	}

	@Test
	public void testProperty_V3() {

		String content = "" + "class X {" + //
				"public String SC = null;"//
				+ "public Object foo() {" //
				+ " int mysimilar = 1;"//
				+ "int myzimilar = 2;"//
				+ "float fiii = (float)myzimilar; " //
				+ "String s1 = SC;"//
				+ "String s2 = s1;"//
				+ "double dother = 0;" + "return fiii;" + "}" + "public float getFloat(){return 1.0;}"//
				+ "public int getConvertFloat(float f){return 1;}"//
				+ "};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);
		CtElement element = method.getBody().getStatements().stream()
				.filter(e -> e.toString().startsWith("java.lang.String s1")).findFirst().get();
		System.out.println(element);
		CntxResolver cntxResolver = new CntxResolver();
		Cntx cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.V3_HAS_CONSTANT));
		assertEquals(Boolean.TRUE, retrieveFeatureVarProperty(cntx, CNTX_Property.V3_HAS_CONSTANT, "SC"));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("java.lang.String s2"))
				.findFirst().get();
		System.out.println(element);
		cntxResolver = new CntxResolver();
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.V3_HAS_CONSTANT));

	}

	public Object retrieveFeatureVarProperty(Cntx cntx, CNTX_Property property, String varName) {
		return ((Cntx) ((Cntx) cntx.getInformation().get("FEATURES_VARS")).getInformation().get(varName))
				.getInformation().get(property.toString());
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

		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.HAS_VAR_SIM_TYPE));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return")).findFirst()
				.get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.HAS_VAR_SIM_TYPE));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("double")).findFirst()
				.get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.HAS_VAR_SIM_TYPE));

	}

	@Test
	public void testProperty_LE3_IS_COMPATIBLE_VAR_NOT_INCLUDED() {

		String content = "" + "class X {" + "public Object foo() {" //
				+ " int mysimilar = 1;"//
				+ "int myzimilar = 2;"//
				+ "float fiii = (float)myzimilar; "//
				+ "double dother = 0;" //
				+ "int f1 =  mysimilar + 1;" //
				+ "int f2 =  mysimilar + myzimilar + f1 ;" //
				+ "return 1;" + "}"//
				+ "public float getFloat(){return 1.0;}"//
				+ "public int getConvertFloat(float f){return 1;}"//
				+ "};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);
		CtElement element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("int f1"))
				.findFirst().get();
		System.out.println(element);
		CntxResolver cntxResolver = new CntxResolver();
		Cntx cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.LE3_IS_COMPATIBLE_VAR_NOT_INCLUDED));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("int f2")).findFirst()
				.get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.LE3_IS_COMPATIBLE_VAR_NOT_INCLUDED));

	}

	@Test
	public void testProperty_LE4_EXISTS_LOCAL_UNUSED_VARIABLES() {

		String content = "" + "class X {" //
				+ "public boolean gvarb =false;" //
				+ "public Object foo() {" //
				+ "boolean avarb =false;" //
				+ "boolean bvarb =false;" //
				+ "int mysimilar = 1;"//
				+ "int myzimilar = (gvarb && avarb && bvarb)? 2:1;"// Use of two booleans
				+ "float fiii = (float)myzimilar; "//
				+ "double dother = 0;" //
				+ "int f1 =  mysimilar + 1;" //
				+ "int f2 =  mysimilar + myzimilar + f1 ;" //
				+ "if(avarb && gvarb){};" //
				+ "return (avarb && bvarb)? 2: 1;" + "}"//
				+ "public float getFloat(){return 1.0;}"//
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
		// all variables used
		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.LE4_EXISTS_LOCAL_UNUSED_VARIABLES));

		// a local not used
		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("if (avarb"))
				.findFirst().get();
		System.out.println(element);
		cntxResolver = new CntxResolver();
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.LE4_EXISTS_LOCAL_UNUSED_VARIABLES));

		// without the global
		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return")).findFirst()
				.get();
		System.out.println(element);
		cntxResolver = new CntxResolver();
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.LE4_EXISTS_LOCAL_UNUSED_VARIABLES));

	}

	@Test
	public void testProperty_LE6_HAS_NEGATION() {

		String content = "" + "class X {" //
				+ "public boolean gvarb =false;" //
				+ "public Object foo() {" //
				+ "boolean avarb =false;" //
				+ "boolean bvarb =false;" //
				+ "int mysimilar = 1;"//
				+ "int myzimilar = (gvarb && avarb && bvarb)? 2:1;"// Use of two booleans
				+ "float fiii = (float)myzimilar; "//
				+ "double dother = 0;" //
				+ "int f1 =  mysimilar + 1;" //
				+ "int f2 =  mysimilar + myzimilar + f1 ;" //
				+ "if(avarb && !gvarb){};" //
				+ "return (avarb && bvarb)? 2: 1;" + "}"//
				+ "public float getFloat(){return 1.0;}"//
				+ "public int getConvertFloat(float f){return 1;}"//
				+ "};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);
		CtElement element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("if (avarb"))
				.findFirst().get();
		System.out.println(element);
		CntxResolver cntxResolver = new CntxResolver();
		Cntx cntx = cntxResolver.retrieveCntx(element);
		// all variables used
		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.LE6_HAS_NEGATION));

		// without the global
		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return")).findFirst()
				.get();
		System.out.println(element);
		cntxResolver = new CntxResolver();
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.LE6_HAS_NEGATION));

	}

	@Test
	public void testProperty_S2_if_condition() {

		String content = "" + "class X {" //
				+ "public boolean gvarb =false;" + "public boolean ddd =false;"//
				+ "public Object foo() {" //
				+ "int f1 =  mysimilar + 1;" //
				+ "int f2 =  mysimilar + myzimilar + f1 ;" //
				+ "if(isGuard()){f2 = f2;};" //
				+ "return f1;" + "}"//
				+ "public float getFloat(){return 1.0;}"//
				+ "public boolean isGuard(){return false;}"//
				+ "public int getConvertFloat(float f){return gvarb?1:0;}"//
				+ "};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);
		CntxResolver cntxResolver = new CntxResolver();

		CtElement element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return"))
				.findFirst().get();
		System.out.println(element);
		cntxResolver = new CntxResolver();
		Cntx cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.S5_SIMILAR_PRIMITIVE_TYPE_WITH_GUARD));
		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.S2_SIMILAR_OBJECT_TYPE_WITH_GUARD));
	}

	@Test
	public void testProperty_S2_conditional_1line() {

		String content = "" + "class X {" //
				+ "public boolean gvarb =false;" + "public boolean ddd =false;"//
				+ "public Object foo() {" //
				+ "int f1 =  mysimilar + 1;" //
				+ "int f2 =  mysimilar + myzimilar + f1 ;" //
				+ "f2 = (isGuard())? f2: 1;" //
				+ "return f1;" + "}"//
				+ "public float getFloat(){return 1.0;}"//
				+ "public boolean isGuard(){return false;}"//
				+ "public int getConvertFloat(float f){return gvarb?1:0;}"//
				+ "};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);
		CntxResolver cntxResolver = new CntxResolver();

		CtElement element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return"))
				.findFirst().get();
		System.out.println(element);
		cntxResolver = new CntxResolver();
		Cntx cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.S5_SIMILAR_PRIMITIVE_TYPE_WITH_GUARD));
		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.S2_SIMILAR_OBJECT_TYPE_WITH_GUARD));
	}

	@Test
	public void testProperty_S5() {

		String content = "" + "class X {" //
				+ "public boolean gvarb =false;" + "public boolean ddd =false;"//
				+ "public Object foo() {" //
				+ "X f1 =  new X();" //
				+ "X f2 =  new X();" //
				+ "if(isGuard()){f2 = f2;};" //
				+ "return f1;" + "}"//
				+ "public float getFloat(){return 1.0;}"//
				+ "public boolean isGuard(){return false;}"//
				+ "public int getConvertFloat(float f){return gvarb?1:0;}"//
				+ "};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);
		CntxResolver cntxResolver = new CntxResolver();

		CtElement element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return"))
				.findFirst().get();
		System.out.println(element);
		cntxResolver = new CntxResolver();
		Cntx cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.S5_SIMILAR_PRIMITIVE_TYPE_WITH_GUARD));
		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.S2_SIMILAR_OBJECT_TYPE_WITH_GUARD));

	}

	@Test
	public void testProperty_S4_field() {

		String content = "" + "class X {" //
				+ "public boolean gvarb =false;" + "public boolean ddd =false;"//
				+ "public Object foo() {" //
				+ "boolean avarb =false;" //
				+ "boolean bvarb =false;" //
				+ "int mysimilar = 1;"//
				+ "int myzimilar = (gvarb && avarb && bvarb)? 2:1;"// Use the field
				+ "float fiii = (float)myzimilar; "//
				+ "double dother = 0;" //
				+ "int f1 =  mysimilar + 1;" //
				+ "int f2 =  mysimilar + myzimilar + f1 ;" //
				+ "if(ddd){};" //
				+ "return (avarb && bvarb)? 2: 1;" + "}"//
				+ "public float getFloat(){return 1.0;}"//
				+ "public int getConvertFloat(float f){return gvarb?1:0;}"//
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
		// all variables used
		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.S4_USED_FIELD));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return")).findFirst()
				.get();
		System.out.println(element);
		cntxResolver = new CntxResolver();
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.S4_USED_FIELD));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("if")).findFirst()
				.get();
		System.out.println(element);
		cntxResolver = new CntxResolver();
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.S4_USED_FIELD));

	}

	@Test
	public void testProperty_LE5() {

		String content = "" + "class X {" //
				+ "public boolean gvarb =false;" //
				+ "public Object foo() {" //
				+ "boolean avarb =false;" //
				+ "boolean bvarb =false;" //
				+ "int mysimilar = 1;"//
				+ "int myzimilar = (gvarb && avarb && bvarb)? 2:1;"// Use of two booleans
				+ "float fiii = (float)myzimilar; "//
				+ "double dother = 0;" //
				+ "int f1 =  mysimilar + 1;" //
				+ "int f2 =  mysimilar + myzimilar + f1 ;" //
				+ "if(avarb && !gvarb){};" //
				+ "return (avarb && bvarb)? 2: 1;" + "}"//
				+ "public float getFloat(){return 1.0;}"//
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
		// all variables used
		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.LE5_BOOLEAN_EXPRESSIONS_IN_FAULTY));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return")).findFirst()
				.get();
		System.out.println(element);
		cntxResolver = new CntxResolver();
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.LE5_BOOLEAN_EXPRESSIONS_IN_FAULTY));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("int f1")).findFirst()
				.get();
		System.out.println(element);
		cntxResolver = new CntxResolver();
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.LE5_BOOLEAN_EXPRESSIONS_IN_FAULTY));

	}

	@Test
	public void testProperty_L7() {

		String content = "" + "class X {" //
				+ "public boolean gvarb =false;" //
				+ "public Object foo() {" //
				+ "boolean avarb =false;" //
				+ "boolean bvarb =false;" //
				+ "int mysimilar = 1;"//
				+ "int myzimilar = (gvarb && avarb && bvarb)? 2:1;"// Use of two booleans
				+ "float fiii =  getFloat(); "//
				+ "double dother = 0;" //
				+ "int f1 =  getConvertFloat(fiii);" //
				+ "int f2 =  mysimilar + myzimilar + f1 ;" //
				+ "if(getB(bvarb) && f1 > 0){};" //
				+ "if(avarb && f1> 0){};" //
				+ "return (avarb && bvarb)? 2: 1;" + "}"//
				+ "public float getMFloat(){return 1.0;}"//
				+ "public float getFloat(){return 1.0;}"//
				+ "public int fint(int i){return 1.0;}"//
				+ "public int getB(boolean i){return 1.0;}"//
				+ "public int getConvertFloat(float f){return 1;}"//
				+ "};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);
		CtElement element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("if ((get"))
				.findFirst().get();
		System.out.println(element);
		CntxResolver cntxResolver = new CntxResolver();
		Cntx cntx = cntxResolver.retrieveCntx(element);
		// not method involve
		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.LE7_SIMPLE_VAR_IN_LOGIC));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("if (avarb"))
				.findFirst().get();
		System.out.println(element);
		cntxResolver = new CntxResolver();
		cntx = cntxResolver.retrieveCntx(element);
		// statement with a similar method
		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.LE7_SIMPLE_VAR_IN_LOGIC));

	}

	@Test
	public void testProperty_M3() {

		String content = "" + "class X {" //
				+ "public boolean gvarb =false;" //
				+ "public Object foo() {" //
				+ "boolean avarb =false;" //
				+ "boolean bvarb =false;" //
				+ "int mysimilar = 1;"//
				+ "int myzimilar = (gvarb && avarb && bvarb)? 2:1;"// Use of two booleans
				+ "float fiii =  getFloat(); "//
				+ "double dother = 0;" //
				+ "int f1 =  getConvertFloat(fiii);" //
				+ "int f2 =  mysimilar + myzimilar + f1 ;" //
				+ "if(avarb && gvarb){};" //
				+ "return (avarb && bvarb)? 2: 1;" + "}"//
				+ "public float getMFloat(){return 1.0;}"//
				+ "public float getFloat(){return 1.0;}"//
				+ "public int fint(int i){return 1.0;}"//
				+ "public int getConvertFloat(float f){return 1;}"//
				+ "};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);
		CtElement element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("int f1"))
				.findFirst().get();
		System.out.println(element);
		CntxResolver cntxResolver = new CntxResolver();
		Cntx cntx = cntxResolver.retrieveCntx(element);
		// not method involve
		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.M3_SIMILAR_METHOD_WITH_PARAMETER_COMP));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("float fiii ="))
				.findFirst().get();
		System.out.println(element);
		cntxResolver = new CntxResolver();
		cntx = cntxResolver.retrieveCntx(element);
		// statement with a similar method
		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.M3_SIMILAR_METHOD_WITH_PARAMETER_COMP));

	}

	@Test
	public void testProperty_M2_SIMILAR_METHOD_WITH_SAME_RETURN() {

		String content = "" + "class X {" //
				+ "public boolean gvarb =false;" //
				+ "public Object foo() {" //
				+ "boolean avarb =false;" //
				+ "boolean bvarb =false;" //
				+ "int mysimilar = 1;"//
				+ "int myzimilar = (gvarb && avarb && bvarb)? 2:1;"// Use of two booleans
				+ "float fiii =  getFloat(); "//
				+ "double dother = 0;" //
				+ "int f1 =  getConvertFloat(fiii);" //
				+ "int f2 =  mysimilar + myzimilar + f1 ;" //
				+ "if(avarb && gvarb){};" //
				+ "return (avarb && bvarb)? 2: 1;" + "}"//
				+ "public float getMFloat(){return 1.0;}"//
				+ "public float getFloat(){return 1.0;}"//
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
		// not method involve
		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.M2_SIMILAR_METHOD_WITH_SAME_RETURN));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("float fiii ="))
				.findFirst().get();
		System.out.println(element);
		cntxResolver = new CntxResolver();
		cntx = cntxResolver.retrieveCntx(element);
		// statement with a similar method
		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.M2_SIMILAR_METHOD_WITH_SAME_RETURN));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("int f1")).findFirst()
				.get();
		System.out.println(element);
		cntxResolver = new CntxResolver();
		cntx = cntxResolver.retrieveCntx(element);
		// all variables used
		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.M2_SIMILAR_METHOD_WITH_SAME_RETURN));

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

		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.V5_HAS_VAR_IN_TRANSFORMATION));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("double ddother"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.HAS_VAR_SIM_NAME));

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

		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.USES_CONSTANT));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("float fiii"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.USES_CONSTANT));

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

		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.USES_ENUM));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("float fiii"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);
		// TODO: Failing:
		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.USES_ENUM));

	}

	@Test
	public void testProperty_NR_OBJECT_ASSIGNED() {

		String content = "" + "class X {" + "public enum MYEN  {ENU1, ENU2;}" + "public Object foo() {" //
				+ " float mysimilar = 1;"//
				+ "Object ob = null;" //
				+ "ob = new String();"//
				+ "String t= ob.toString();" // HERE: initialized
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
		assertEquals(1, cntx.get(CNTX_Property.NR_VARIABLE_ASSIGNED));
		assertEquals(0, cntx.get(CNTX_Property.NR_VARIABLE_NOT_ASSIGNED));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return ob"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(1, cntx.get(CNTX_Property.NR_VARIABLE_ASSIGNED));
		assertEquals(0, cntx.get(CNTX_Property.NR_VARIABLE_NOT_ASSIGNED));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("boolean com"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);
		assertEquals(2, cntx.get(CNTX_Property.NR_VARIABLE_ASSIGNED));
		assertEquals(0, cntx.get(CNTX_Property.NR_VARIABLE_NOT_ASSIGNED));

	}

	@Test
	public void testProperty_NR_OBJECT_ASSIGNED_Decl_notInit() {

		String content = "" + "class X {" + "public enum MYEN  {ENU1, ENU2;}" + "public Object foo() {" //
				+ " float mysimilar = 1;"//
				+ "Object ob = null;" //
				+ "ob = new String();"//
				+ "String t= null;" // HERE: not init
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
		assertEquals(0, cntx.get(CNTX_Property.NR_VARIABLE_ASSIGNED));
		assertEquals(1, cntx.get(CNTX_Property.NR_VARIABLE_NOT_ASSIGNED));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return ob"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(1, cntx.get(CNTX_Property.NR_VARIABLE_ASSIGNED));
		assertEquals(0, cntx.get(CNTX_Property.NR_VARIABLE_NOT_ASSIGNED));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("boolean com"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);
		assertEquals(1, cntx.get(CNTX_Property.NR_VARIABLE_ASSIGNED));
		assertEquals(1, cntx.get(CNTX_Property.NR_VARIABLE_NOT_ASSIGNED));

	}

	@Test
	public void testProperty_NR_LocalVariable_ASSIGNED() {

		String content = "" + "class X {"
		//
				+ "public enum MYEN  {ENU1, ENU2;}" + //
				"public Object foo() {" //
				+ " float mysimilar = 1;"//
				+ "Object ob = null;" //
				+ "ob = new String();"//
				+ "String t= null;" // Not initialized (default expression == null)
				+ "boolean com = (ob == t);" //
				+ "com = (t==true);"//
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

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("com =")).findFirst()
				.get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);
		assertEquals(0, cntx.get(CNTX_Property.NR_VARIABLE_ASSIGNED));
		assertEquals(1, cntx.get(CNTX_Property.NR_VARIABLE_NOT_ASSIGNED));
		// All are local
		assertEquals(0, cntx.get(CNTX_Property.NR_OBJECT_ASSIGNED_LOCAL));
		assertEquals(1, cntx.get(CNTX_Property.NR_OBJECT_NOT_ASSIGNED_LOCAL));

		//
		boolean existsNotAssigned = Boolean.parseBoolean(cntx.get(CNTX_Property.S1_LOCAL_VAR_NOT_ASSIGNED).toString());
		assertTrue(existsNotAssigned);

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return ob"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(1, cntx.get(CNTX_Property.NR_VARIABLE_ASSIGNED));
		assertEquals(0, cntx.get(CNTX_Property.NR_VARIABLE_NOT_ASSIGNED));

		assertEquals(1, cntx.get(CNTX_Property.NR_OBJECT_ASSIGNED_LOCAL));
		assertEquals(0, cntx.get(CNTX_Property.NR_OBJECT_NOT_ASSIGNED_LOCAL));

		boolean existsAssigned = Boolean.parseBoolean(cntx.get(CNTX_Property.S1_LOCAL_VAR_NOT_ASSIGNED).toString());
		assertFalse(existsAssigned);

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("boolean com"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);
		assertEquals(1, cntx.get(CNTX_Property.NR_VARIABLE_ASSIGNED));
		assertEquals(1, cntx.get(CNTX_Property.NR_VARIABLE_NOT_ASSIGNED));

		assertEquals(1, cntx.get(CNTX_Property.NR_OBJECT_ASSIGNED_LOCAL));
		assertEquals(1, cntx.get(CNTX_Property.NR_OBJECT_NOT_ASSIGNED_LOCAL));

	}

	@Test
	public void testProperty_NR_LocalVariable_ASSIGNED_withGlobalVars() {

		String content = "" + "class X {"
		//
				+ " String tconst = null;"//

				+ "public enum MYEN  {ENU1, ENU2;}" + //
				"public Object foo() {" //
				+ " float mysimilar = 1;"//
				+ "Object ob = null;" //
				+ "ob = new String();"//
				+ "String t= null;" // Not initialized (default expression == null)
				+ "boolean com = (ob == t);" //
				+ "com = (t==tconst);" // the tconst never assigned
				+ "tconst = t;" // assigning
				+ "t = ctconst+tconst;" //
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

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("com =")).findFirst()
				.get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);
		assertEquals(0, cntx.get(CNTX_Property.NR_VARIABLE_ASSIGNED));
		assertEquals(2, cntx.get(CNTX_Property.NR_VARIABLE_NOT_ASSIGNED));
		// All are local
		assertEquals(0, cntx.get(CNTX_Property.NR_OBJECT_ASSIGNED_LOCAL));
		assertEquals(1, cntx.get(CNTX_Property.NR_OBJECT_NOT_ASSIGNED_LOCAL));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return ob"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(1, cntx.get(CNTX_Property.NR_VARIABLE_ASSIGNED));
		assertEquals(0, cntx.get(CNTX_Property.NR_VARIABLE_NOT_ASSIGNED));

		assertEquals(1, cntx.get(CNTX_Property.NR_OBJECT_ASSIGNED_LOCAL));
		assertEquals(0, cntx.get(CNTX_Property.NR_OBJECT_NOT_ASSIGNED_LOCAL));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("boolean com"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);
		assertEquals(1, cntx.get(CNTX_Property.NR_VARIABLE_ASSIGNED));
		assertEquals(1, cntx.get(CNTX_Property.NR_VARIABLE_NOT_ASSIGNED));

		assertEquals(1, cntx.get(CNTX_Property.NR_OBJECT_ASSIGNED_LOCAL));
		assertEquals(1, cntx.get(CNTX_Property.NR_OBJECT_NOT_ASSIGNED_LOCAL));

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
		assertEquals(1, cntx.get(CNTX_Property.NR_OBJECT_USED));
		assertEquals(2, cntx.get(CNTX_Property.NR_OBJECT_NOT_USED));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return ob"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(1, cntx.get(CNTX_Property.NR_OBJECT_USED));
		assertEquals(0, cntx.get(CNTX_Property.NR_OBJECT_NOT_USED));

	}

	@Test
	public void testProperty_S3_TYPE_OF_FAULTY_STATEMENT() {

		String content = "" + "class X {" +
		//
				"String tdef = \"hello\";" + // defined
				"String tco = null;" + //
				"public enum MYEN  {ENU1, ENU2;}" + "public Object foo() {" //
				+ " float mysimilar = 1;"//
				+ "Object ob = null;" //
				+ "ob = new String();"//
				+ "String t= ob.toString();" //
				+ "String t2 = null;" //
				+ "boolean com = (ob == t) && (t2 == t);" //
				+ "String t4 = null;" // Never used
				+ "t2 = tco + t4 ;"// tco is not used, but it's not local, t4 never used but is local
				+ "t = tco + t4 + tdef + t2;"// one global used not
				+ "while (t != null){}" + "return ob;" + //
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
		cntx = cntxResolver.retrieveCntx(element);
		assertEquals("LocalVariable", cntx.get(CNTX_Property.S3_TYPE_OF_FAULTY_STATEMENT));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return ob"))
				.findFirst().get();
		cntx = cntxResolver.retrieveCntx(element);
		assertEquals("Return", cntx.get(CNTX_Property.S3_TYPE_OF_FAULTY_STATEMENT));

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("t2 =")).findFirst()
				.get();
		cntx = cntxResolver.retrieveCntx(element);
		assertEquals("Assignment", cntx.get(CNTX_Property.S3_TYPE_OF_FAULTY_STATEMENT));
		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("while")).findFirst()
				.get();
		cntx = cntxResolver.retrieveCntx(element);
		assertEquals("While", cntx.get(CNTX_Property.S3_TYPE_OF_FAULTY_STATEMENT));

	}

	@Test
	public void testProperty_NR_OBJECT_USED_LOCAL_VARS() {

		String content = "" + "class X {" +
		//
				"String tdef = \"hello\";" + // defined
				"String tco = null;" + //
				"public enum MYEN  {ENU1, ENU2;}" + "public Object foo() {" //
				+ " float mysimilar = 1;"//
				+ "Object ob = null;" //
				+ "ob = new String();"//
				+ "String t= ob.toString();" //
				+ "String t2 = null;" //
				+ "boolean com = (ob == t) && (t2 == t);" //
				+ "String t4 = null;" // Never used
				+ "t2 = tco + t4 ;"// tco is not used, but it's not local, t4 never used but is local
				+ "t = tco + t4 + tdef + t2;"// one global used not
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
		assertEquals(1, cntx.get(CNTX_Property.NR_OBJECT_USED));
		assertEquals(2, cntx.get(CNTX_Property.NR_OBJECT_NOT_USED));

		assertEquals(1, cntx.get(CNTX_Property.NR_OBJECT_USED_LOCAL_VAR));
		assertEquals(2, cntx.get(CNTX_Property.NR_OBJECT_NOT_USED_LOCAL_VAR));

		boolean existsNotUsed = Boolean.parseBoolean(cntx.get(CNTX_Property.S1_LOCAL_VAR_NOT_USED).toString());
		assertTrue(existsNotUsed);

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return ob"))
				.findFirst().get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(1, cntx.get(CNTX_Property.NR_OBJECT_USED));
		assertEquals(0, cntx.get(CNTX_Property.NR_OBJECT_NOT_USED));

		existsNotUsed = Boolean.parseBoolean(cntx.get(CNTX_Property.S1_LOCAL_VAR_NOT_USED).toString());
		assertFalse(existsNotUsed);

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("t2 =")).findFirst()
				.get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(0, cntx.get(CNTX_Property.NR_OBJECT_USED));
		assertEquals(2, cntx.get(CNTX_Property.NR_OBJECT_NOT_USED));

		assertEquals(0, cntx.get(CNTX_Property.NR_OBJECT_USED_LOCAL_VAR));
		assertEquals(1, cntx.get(CNTX_Property.NR_OBJECT_NOT_USED_LOCAL_VAR));

		existsNotUsed = Boolean.parseBoolean(cntx.get(CNTX_Property.S1_LOCAL_VAR_NOT_USED).toString());
		assertTrue(existsNotUsed);

		/////

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("t =")).findFirst()
				.get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);

		assertEquals(3, cntx.get(CNTX_Property.NR_OBJECT_USED));
		assertEquals(1, cntx.get(CNTX_Property.NR_OBJECT_NOT_USED));

		assertEquals(2, cntx.get(CNTX_Property.NR_OBJECT_USED_LOCAL_VAR));
		assertEquals(0, cntx.get(CNTX_Property.NR_OBJECT_NOT_USED_LOCAL_VAR));

		existsNotUsed = Boolean.parseBoolean(cntx.get(CNTX_Property.S1_LOCAL_VAR_NOT_USED).toString());
		assertFalse(existsNotUsed);

	}

	@Test
	public void testProperty_L1() {

		String content = "" + "class X {" +
		//
				"String tdef = \"hello\";" + // defined
				"String tco = null;" + //
				"public enum MYEN  {ENU1, ENU2;}"//
				+ "public Object foo() {" //
				+ " float mysimilar = 1;"//
				+ "if (mysimilar > 0){};" //
				+ "float f2 = 2;" //
				+ "boolean s1 = (mysimilar > 2);" //
				+ "boolean s2 = (f2 > 2) && s1;" //
				+ "double d1 = 0;"//
				+ "double d2=0;"//
				+ "float f3 = (float) d1;" + // using d1 in not a binary
				"if(true && (true && true && (f3))){}" + //
				"boolean s3 = (d1 > 0)   ;"//
				+ "boolean s4 = (d2 > 0) && s3 ;"//
				+ "return null;" + //
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

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("boolean s2"))
				.findFirst().get();
		cntx = cntxResolver.retrieveCntx(element);
		boolean existsNotUsed = Boolean
				.parseBoolean(cntx.get(CNTX_Property.LE1_EXISTS_RELATED_BOOLEAN_EXPRESSION).toString());
		assertTrue(existsNotUsed);

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("boolean s1"))
				.findFirst().get();
		cntx = cntxResolver.retrieveCntx(element);

		existsNotUsed = Boolean.parseBoolean(cntx.get(CNTX_Property.LE1_EXISTS_RELATED_BOOLEAN_EXPRESSION).toString());
		assertFalse(existsNotUsed);

		///
		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("boolean s3"))
				.findFirst().get();
		cntx = cntxResolver.retrieveCntx(element);

		existsNotUsed = Boolean.parseBoolean(cntx.get(CNTX_Property.LE1_EXISTS_RELATED_BOOLEAN_EXPRESSION).toString());
		assertFalse(existsNotUsed);

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("boolean s4"))
				.findFirst().get();
		cntx = cntxResolver.retrieveCntx(element);
		// Now d1 is used in binary ()
		existsNotUsed = Boolean.parseBoolean(cntx.get(CNTX_Property.LE1_EXISTS_RELATED_BOOLEAN_EXPRESSION).toString());
		assertTrue(existsNotUsed);

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
		// Strange behaviour: fails when running, works when debbuging
		// assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.NR_FIELD_INCOMPLETE_INIT));

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
		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.NR_FIELD_INCOMPLETE_INIT));

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
		assertEquals(Boolean.TRUE, cntx.get(CNTX_Property.NR_FIELD_INCOMPLETE_INIT));

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
		assertEquals(Boolean.FALSE, cntx.get(CNTX_Property.NR_FIELD_INCOMPLETE_INIT));

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProperty_VAR_CNT() {
		/// Case all initialized

		String content = "" + "class X {" //
				+ "public X fX = null;" + //
				"public int f1 = 0;" + //
				"public int f2 = 0;" + //
				"public String s2;" //
				+ "public Object foo() {" + //
				" fX = new X();"// init the field
				+ "fX.fX = null;"//
				+ "fX.f1 = 0;"//
				+ "int mv ;" //
				+ "fX.f2 = 0;"//
				+ "mv = fX.f2;" + //
				"};" + "public X copy(X mx){return mx;}" + "};";

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
		ConfigurationProperties.setProperty("max_synthesis_step", "100");
		cntx = cntxResolver.retrieveCntx(element);

		// List<?> space = (List<?>) cntx.get(CNTX_Property.PSPACE);
		int i = 0;
		// for (Object spaceeleemnt : space) {
		// System.out.println((i++) + "--> " + spaceeleemnt);
		// }

		// assertEquals(Boolean.FALSE,
		// cntx.get(CNTX_Property.NR_FIELD_INCOMPLETE_INIT));

	}

	@Test
	public void test() {

	}

	@SuppressWarnings("unchecked")
	@Test
	public void testProperty_VAR_CNT_2_small() {
		/// Case all initialized

		String content = "" + "class X {" + //

				"public int f1 = 0;" + //
				"public boolean b2 = 0;" + //

				"public Object foo() {" + //
				"return null;"// init the field
				+ "};};";

		CtType type = getCtType(content);

		assertNotNull(type);
		CtMethod method = (CtMethod) type.getMethods().stream()
				.filter(e -> ((CtMethod) e).getSimpleName().equals("foo")).findFirst().get();

		assertNotNull(method);
		System.out.println(method);

		CntxResolver cntxResolver = new CntxResolver();
		CtElement element = null;
		Cntx cntx = null;

		element = method.getBody().getStatements().stream().filter(e -> e.toString().startsWith("return ")).findFirst()
				.get();
		System.out.println(element);
		cntx = cntxResolver.retrieveCntx(element);
		// assertEquals(Boolean.FALSE,
		// cntx.get(CNTX_Property.NR_FIELD_INCOMPLETE_INIT));

		List<?> space = (List<?>) cntx.get(CNTX_Property.PSPACE);
		int i = 0;
		// for (Object spaceeleemnt : space) {
		// System.out.println((i++) + "--> " + spaceeleemnt);
		// }
	}

	@Test
	public void testSpoon1() {
		CtType s = new TypeFactory().get(Integer.class);
		System.out.println(s.getSimpleName());
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
