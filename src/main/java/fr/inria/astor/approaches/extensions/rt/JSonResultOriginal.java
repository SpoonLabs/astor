package fr.inria.astor.approaches.extensions.rt;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;

import com.google.common.collect.Lists;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import fr.inria.astor.approaches.extensions.rt.RtEngine.AsAssertion;
import fr.inria.astor.approaches.extensions.rt.RtEngine.Helper;
import fr.inria.astor.approaches.extensions.rt.RtEngine.TestRottenAnalysisResult;
import fr.inria.astor.approaches.extensions.rt.RtEngine.Skip;
import fr.inria.astor.approaches.extensions.rt.RtEngine.TestInspectionResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import spoon.reflect.code.CtDo;
import spoon.reflect.code.CtFor;
import spoon.reflect.code.CtForEach;
import spoon.reflect.code.CtIf;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtReturn;
import spoon.reflect.code.CtSwitch;
import spoon.reflect.code.CtWhile;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.visitor.filter.TypeFilter;

/**
 * 
 * @author Matias Martinez
 *
 */
public class JSonResultOriginal {
	private static final String FULL_ROTTEN_TEST = "Full_Rotten_Test";

	private static final String SMOKE_TEST = "Smoke_Test";

	private static final String ROTTEN_MISSED = "Rotten_Missed";

	private static final String ROTTEN_SKIP = "Rotten_Skip";

	private static final String ROTTEN_CONTEXT_DEP_HELPERS_CALL = "Context_Dep_Rotten_Helpers_Call";

	private static final String ROTTEN_CONTEXT_DEP_HELPERS_ASSERTION = "Context_Dep_Rotten_Helpers_Assertion";

	private static final String ROTTEN_CONTEXT_DEP_ASSERTIONS = "Context_Dep_Rotten_Assertions";

	// The fulls
	private static final String FULL_ROTTEN_TEST_HELPERS_CALL = "Full_Rotten_Test_Rotten_Helpers_Call";

	private static final String FULL_ROTTEN_TEST_HELPERS_ASSERTION = "Full_Rotten_Test_Rotten_Helpers_Assertion";

	private static final String FULL_ROTTEN_TEST_ASSERTIONS = "Full_Rotten_Test_Rotten_Assertions";

	private static final String TEST_HAS_CONTROL_FLOW_STMT = "Test_with_Control_flow_stmt";
	private static final String TEST_HAS_HELPER_CALL = "Test_with_Helper";
	protected static Logger log = Logger.getLogger(Thread.currentThread().getName());

	ProjectRepairFacade projectFacade = null;

	public JsonObject toJson(ProjectRepairFacade projectFacade, List<TestInspectionResult> resultByTest) {

		this.projectFacade = projectFacade;

		JsonObject root = new JsonObject();
		root.addProperty("project", projectFacade.getProperties().getFixid());
		JsonObject summary = new JsonObject();
		root.add("project", summary);
		String location = ConfigurationProperties.getProperty("location");
		String commitid = executeCommand(location, "git rev-parse HEAD");
		String branch = executeCommand(location, "git rev-parse --abbrev-ref HEAD");
		String remote = executeCommand(location, "git config --get remote.origin.url");
		String projectsubfolder = ConfigurationProperties.getProperty("projectsubfolder");
		summary.addProperty("commitid", commitid);

		int nrRtest = 0, nrRtAssertion = 0, nrRtHelperCall = 0, nrRttHelperAssert = 0, nrSkip = 0, nrAllMissed = 0,
				nrSmokeTest = 0, nrRtFull = 0, nrTestWithControlStruct = 0, nrTestWithHelper = 0;

		JsonArray testsAssertionArray = new JsonArray();
		root.add("tests", testsAssertionArray);
		Set<String> rTestclasses = new HashSet<>();
		for (TestInspectionResult tr : resultByTest) {

			TestRottenAnalysisResult resultClassification = tr.generateFinalResult();

			JsonObject testjson = new JsonObject();
			JsonArray typesRottens = new JsonArray();
			Set<String> uniquesTypesRottern = new HashSet();
			testjson.add("types", typesRottens);
			testjson.addProperty("testclass", tr.getNameOfTestClass());
			testjson.addProperty("testname", tr.getTestMethodFromClass());
			testjson.addProperty("expectsexception", (tr.getExpectException().size() > 0) ? "true" : "false");
			writeJsonLink(commitid, branch, remote, projectsubfolder, tr.getTestMethodModel(), testjson);

			boolean onerotten = false;

			boolean hasControlFlow = tr.testMethodModel.getElements(new TypeFilter<>(CtIf.class)).size() > 0
					//
					|| tr.testMethodModel.getElements(new TypeFilter<>(CtWhile.class)).size() > 0
					//
					|| tr.testMethodModel.getElements(new TypeFilter<>(CtFor.class)).size() > 0
					//
					|| tr.testMethodModel.getElements(new TypeFilter<>(CtForEach.class)).size() > 0
					//
					|| tr.testMethodModel.getElements(new TypeFilter<>(CtSwitch.class)).size() > 0
					//
					|| tr.testMethodModel.getElements(new TypeFilter<>(CtDo.class)).size() > 0;
			nrTestWithControlStruct += (hasControlFlow) ? 1 : 0;

			boolean hasHelperCall = !tr.getClassificationHelperCall().getResultExecuted().isEmpty()
					|| !tr.getClassificationHelperCall().getResultNotExecuted().isEmpty();

			nrTestWithHelper += (hasHelperCall) ? 1 : 0;

			// Here the complex:

			// Asserts

			int r = add_ASSERTIONS(projectFacade, commitid, branch, remote, projectsubfolder, tr, testjson,
					uniquesTypesRottern, resultClassification.contextAssertion, ROTTEN_CONTEXT_DEP_ASSERTIONS);
			onerotten = onerotten || (r > 0);
			nrRtAssertion += r;
			//

			r = add_HELPERS_CALL(commitid, branch, remote, projectsubfolder, tr, testjson, uniquesTypesRottern,
					resultClassification.contextHelperCall, ROTTEN_CONTEXT_DEP_HELPERS_CALL);
			onerotten = onerotten || (r > 0);
			nrRtHelperCall += r;

			//

			r = add_HELPERS_ASSERTION(commitid, branch, remote, projectsubfolder, tr, testjson, uniquesTypesRottern,
					resultClassification.contextHelperAssertion, ROTTEN_CONTEXT_DEP_ASSERTIONS);
			onerotten = onerotten || (r > 0);
			nrRttHelperAssert += r;
			//
			/// ------Now the full-----

			r = add_ASSERTIONS(projectFacade, commitid, branch, remote, projectsubfolder, tr, testjson,
					uniquesTypesRottern, resultClassification.fullRottenAssert, FULL_ROTTEN_TEST_ASSERTIONS);
			onerotten = onerotten || (r > 0);
			nrRtFull += r;
			//

			r = add_HELPERS_CALL(commitid, branch, remote, projectsubfolder, tr, testjson, uniquesTypesRottern,
					resultClassification.fullRottenHelperCall, FULL_ROTTEN_TEST_HELPERS_CALL);
			onerotten = onerotten || (r > 0);
			nrRtFull += r;

			//
			r = add_HELPERS_ASSERTION(commitid, branch, remote, projectsubfolder, tr, testjson, uniquesTypesRottern,
					resultClassification.fullRottenHelperAssert, FULL_ROTTEN_TEST_HELPERS_ASSERTION);
			onerotten = onerotten || (r > 0);
			nrRtFull += r;

			//
			if (!resultClassification.skip.isEmpty()) {
				JsonArray skiprarray = new JsonArray();
				testjson.add(ROTTEN_SKIP, skiprarray);
				for (Skip iSkip : resultClassification.skip) {
					CtReturn skip = iSkip.executedReturn;
					JsonObject singleSkip = new JsonObject();
					singleSkip.addProperty("code", skip.toString().toString());
					singleSkip.addProperty("line", skip.getPosition().getLine());
					singleSkip.add("parent_types", getParentTypes(skip));
					onerotten = true;
					skiprarray.add(singleSkip);
					writeJsonLink(commitid, branch, remote, projectsubfolder, skip, singleSkip);
					nrSkip++;
					uniquesTypesRottern.add(ROTTEN_SKIP);
				}
			}

			//
			if (!resultClassification.missed.isEmpty()) {
				JsonArray missrarray = new JsonArray();
				testjson.add(ROTTEN_MISSED, missrarray);
				for (AsAssertion missedInv : resultClassification.missed) {
					JsonObject missedJson = new JsonObject();
					missedJson.addProperty("code_assertion", missedInv.toString().toString());
					missedJson.addProperty("line_assertion", missedInv.getCtAssertion().getPosition().getLine());
					missedJson.addProperty("path_assertion",
							getRelativePath(missedInv.getCtAssertion(), projectFacade));
					writeJsonLink(commitid, branch, remote, projectsubfolder, missedInv.getCtAssertion(), missedJson);
					onerotten = true;
					missrarray.add(missedJson);
					nrAllMissed++;
					uniquesTypesRottern.add(ROTTEN_MISSED);
				}
			}

			if (tr.isSmokeTest() && tr.getExpectException().isEmpty()
					&& tr.getAllExpectedExceptionFromTest().isEmpty()) {

				List<CtInvocation> allAssertionsFromTest = tr.getTestMethodModel().getBody()
						.getElements(new TypeFilter<>(CtInvocation.class));

				// TODO: Move verification to RTEngine
				// if (hasFail(allAssertionsFromTest)) {
				// continue;
				// }

				testjson.addProperty(SMOKE_TEST, "true");
				testsAssertionArray.add(testjson);
				rTestclasses.add(tr.getNameOfTestClass());
				nrSmokeTest += 1;

				JsonArray missrarray = new JsonArray();
				testjson.add("other_method_invocation", missrarray);
				for (CtInvocation otherinv : allAssertionsFromTest) {
					missrarray.add(createMethodSignature(otherinv));
				}
				uniquesTypesRottern.add(SMOKE_TEST);
			}

			/// Dont include smoke
			if (onerotten) {
				testsAssertionArray.add(testjson);
				nrRtest++;
				rTestclasses.add(tr.getNameOfTestClass());
			}

			// Some stats
			if (testjson != null) {
				testjson.addProperty(TEST_HAS_CONTROL_FLOW_STMT, hasControlFlow);
				testjson.addProperty(TEST_HAS_HELPER_CALL, hasHelperCall);
			}
			// We put the the types found in "types"
			for (String types : uniquesTypesRottern) {
				typesRottens.add(types);
			}
		}

		summary.addProperty("remote", remote);
		summary.addProperty("localLocation", location);
		summary.addProperty("nr_Test_With_Control_Flow_Stmt", nrTestWithControlStruct);
		summary.addProperty("nr_Test_With_Helper", nrTestWithHelper);
		summary.addProperty("nr_All_Test", resultByTest.size());
		summary.addProperty("nr_Rotten_Test_Units", nrRtest);
		summary.addProperty("nr_" + this.ROTTEN_CONTEXT_DEP_ASSERTIONS, nrRtAssertion);
		summary.addProperty("nr_" + this.ROTTEN_CONTEXT_DEP_HELPERS_CALL, nrRtHelperCall);
		summary.addProperty("nr_" + this.ROTTEN_CONTEXT_DEP_HELPERS_ASSERTION, nrRttHelperAssert);
		summary.addProperty("nr_" + this.ROTTEN_SKIP, nrSkip);
		summary.addProperty("nr_" + this.ROTTEN_MISSED, nrAllMissed);
		summary.addProperty("nr_" + this.SMOKE_TEST, nrSmokeTest);
		summary.addProperty("nr_" + this.FULL_ROTTEN_TEST, nrRtFull);
		return root;
	}

	public int add_ASSERTIONS(ProjectRepairFacade projectFacade, String commitid, String branch, String remote,
			String projectsubfolder, TestInspectionResult tr, JsonObject testjson, Set<String> uniquesTypesRottern,
			List<AsAssertion> notExecutedAssert, String ROTTEN_CONTEXT_DEP_ASSERTIONS) {
		int nrRtAssertion = 0;
		if (!notExecutedAssert.isEmpty()) {

			log.debug("-- Test  " + tr.getNameOfTestClass() + ": " + tr.getTestMethodFromClass());

			JsonArray assertionarray = new JsonArray();
			testjson.add(ROTTEN_CONTEXT_DEP_ASSERTIONS, assertionarray);
			for (AsAssertion assertion : notExecutedAssert) {
				CtInvocation anInvocation = assertion.getCtAssertion();
				log.debug("-R-Assertion:-> " + anInvocation);
				JsonObject jsonsingleAssertion = new JsonObject();
				jsonsingleAssertion.addProperty("code", anInvocation.toString());
				jsonsingleAssertion.addProperty("line", anInvocation.getPosition().getLine());
				jsonsingleAssertion.addProperty("path", getRelativePath(anInvocation, projectFacade));
				jsonsingleAssertion.addProperty("inbranch", assertion.isFp());

				writeJsonLink(commitid, branch, remote, projectsubfolder, anInvocation, jsonsingleAssertion);
				assertionarray.add(jsonsingleAssertion);
				// onerotten = true;
				jsonsingleAssertion.add("parent_types", getParentTypes(anInvocation));
				nrRtAssertion++;

				uniquesTypesRottern.add(ROTTEN_CONTEXT_DEP_ASSERTIONS);
			}
		}
		return nrRtAssertion;
	}

	public int add_HELPERS_ASSERTION(String commitid, String branch, String remote, String projectsubfolder,
			TestInspectionResult tr, JsonObject testjson, Set<String> uniquesTypesRottern,
			List<Helper> notExecutedHelper, String ROTTEN_CONTEXT_DEP_HELPERS_ASSERTION) {
		int nrRttHelperAssert = 0;
		if (!notExecutedHelper.isEmpty()) {
			log.debug("-R Helper assertion- " + tr.getNameOfTestClass() + ": " + tr.getTestMethodFromClass());

			List<JsonObject> result = helperToJson(notExecutedHelper,
					tr.getClassificationHelperCall().getResultNotExecuted(), commitid, branch, remote, projectsubfolder,
					false);

			if (!result.isEmpty()) {
				JsonArray helperarray = new JsonArray();

				testjson.add(ROTTEN_CONTEXT_DEP_HELPERS_ASSERTION, helperarray);

				// onerotten = true;
				for (JsonObject jsonObject : result) {
					helperarray.add(jsonObject);
				}
				uniquesTypesRottern.add(ROTTEN_CONTEXT_DEP_HELPERS_ASSERTION);
			}

			nrRttHelperAssert += notExecutedHelper.size();
		}
		return nrRttHelperAssert;
	}

	public int add_HELPERS_CALL(String commitid, String branch, String remote, String projectsubfolder,
			TestInspectionResult tr, JsonObject testjson, Set<String> uniquesTypesRottern,
			List<Helper> notExecutedHelperInvoc, String ROTTEN_CONTEXT_DEP_HELPERS_CALL) {
		int nrRtHelperCall = 0;
		if (!notExecutedHelperInvoc.isEmpty()) {
			System.out.println("-- R Helper call  " + tr.getNameOfTestClass() + ": " + tr.getTestMethodFromClass());

			List<JsonObject> result = helperToJson(notExecutedHelperInvoc, Lists.newArrayList(), commitid, branch,
					remote, projectsubfolder, true);

			if (!result.isEmpty()) {
				JsonArray helperarray = new JsonArray();

				testjson.add(ROTTEN_CONTEXT_DEP_HELPERS_CALL, helperarray);

				// onerotten = true;
				for (JsonObject jsonObject : result) {
					helperarray.add(jsonObject);
				}
				uniquesTypesRottern.add(ROTTEN_CONTEXT_DEP_HELPERS_CALL);
			}
			nrRtHelperCall += notExecutedHelperInvoc.size();
		}
		return nrRtHelperCall;
	}

	private String executeCommand(String location, String command) {

		log.debug("Running command  " + command + " at " + location);
		ProcessBuilder builder = new ProcessBuilder();

		builder.command(command.split(" "));

		builder.directory(new File(location));

		try {

			Process process = builder.start();

			BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String content = "";
			String line;
			while ((line = reader.readLine()) != null) {
				content += line + "\n";
			}
			log.info("Command result " + content);
			return content.trim();
		} catch (Exception ex) {
			ex.printStackTrace();
			return null;
		}
	}

	private JsonArray getParentTypes(CtElement anElement) {
		JsonArray parentArray = new JsonArray();
		CtElement parent = anElement.getParent();
		while (parent != null) {
			// removing the prefix "Ct" and suffix "Impl"
			parentArray.add(cleanTypeName(parent.getClass().getSimpleName()));
			parent = parent.getParent();
			// we discard parents from package
			if (parent instanceof CtPackage) {
				break;
			}
		}

		return parentArray;
	}

	// private boolean isFail(CtInvocation targetInvocation) {
	// return isInvWithName(targetInvocation, "fail");
	// }

	private boolean hasFail(List<CtInvocation> allAssertionsFromTest) {
		for (CtInvocation ctInvocation : allAssertionsFromTest) {
			// if (isFail(ctInvocation)) {
			return true;
			// }
		}
		return false;
	}

	public void writeJsonLink(String commitid, String branch, String remote, String projectsubfolder,
			CtElement anInvocation, JsonObject singleAssertion) {
		if (remote != null && branch != null && commitid != null) {
			singleAssertion.addProperty("githublink", remote.replace(".git", "")
					// "https://github.com/" + projectname
					+ "/tree/" + commitid// branch
					+ ((projectsubfolder != null) ? "/" + projectsubfolder : "") + "/"
					+ getRelativePath(anInvocation, this.projectFacade) + "#L" + anInvocation.getPosition().getLine());
		}
	}

	public List<JsonObject> helperToJson(List<Helper> notExecutedHelper, List<Helper> others, String commitid,
			String branch, String remote, String projectsubfolder, boolean isCall) {

		List<JsonObject> result = new ArrayList();

		for (Helper anHelper : notExecutedHelper) {

			if (others.contains(anHelper)) {
				continue;
			}
			log.debug("-Helper-> " + anHelper);
			CtInvocation ctAssertion = anHelper.getAssertion().getCtAssertion();
			JsonObject jsonsingleHelper = new JsonObject();

			JsonObject assertionjson = getJsonElement(commitid, branch, remote, projectsubfolder, ctAssertion);
			jsonsingleHelper.add("assertion", assertionjson);
			jsonsingleHelper.addProperty("inbranch", anHelper.isFp());
			JsonArray callsarray = new JsonArray();
			for (CtInvocation call : anHelper.getCalls()) {
				callsarray.add(getJsonElement(commitid, branch, remote, projectsubfolder, call));
			}
			jsonsingleHelper.add("calls", callsarray);

			if (isCall) {

				if (anHelper.getCalls().size() > 0)
					writeJsonLink(commitid, branch, remote, projectsubfolder, anHelper.getCalls().get(0),
							jsonsingleHelper);

			} else {

				writeJsonLink(commitid, branch, remote, projectsubfolder, ctAssertion, jsonsingleHelper);
			}

			result.add(jsonsingleHelper);

		}
		return result;
	}

	public JsonObject getJsonElement(String commitid, String branch, String remote, String projectsubfolder,
			CtInvocation ctAssertion) {
		JsonObject jsonsingleHelper = new JsonObject();
		jsonsingleHelper.addProperty("code", ctAssertion.toString());
		jsonsingleHelper.addProperty("line", ctAssertion.getPosition().getLine());
		writeJsonLink(commitid, branch, remote, projectsubfolder, ctAssertion, jsonsingleHelper);
		return jsonsingleHelper;
	}

	public String cleanTypeName(String parent) {
		if (parent.length() < 6) {
			return parent;
		}
		return parent.substring(2, parent.length() - 4);
	}

	public String getRelativePath(CtElement anInvocation, ProjectRepairFacade projectFacade) {
		return anInvocation.getPosition().getFile().getAbsolutePath().replace("./", "")
				.replace(projectFacade.getProperties().getOriginalProjectRootDir().replace("./", ""), "");
	}

	public String createMethodSignature(CtInvocation anInvocation) {
		String signature = "";

		if (anInvocation.getExecutable() != null)

			signature +=
					//
					((anInvocation.getExecutable().getDeclaringType() != null)
							? anInvocation.getExecutable().getDeclaringType().getQualifiedName()
							: anInvocation.getExecutable().getSimpleName())
							//
							+ "#" + anInvocation.getExecutable().getSignature();

		else {
			signature += anInvocation.getShortRepresentation();
		}
		return signature;
	}

}
