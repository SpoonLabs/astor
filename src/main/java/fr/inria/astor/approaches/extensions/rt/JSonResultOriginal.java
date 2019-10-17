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
import fr.inria.astor.approaches.extensions.rt.RtEngine.Skip;
import fr.inria.astor.approaches.extensions.rt.RtEngine.TestInspectionResult;
import fr.inria.astor.approaches.extensions.rt.RtEngine.TestRottenAnalysisResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import spoon.reflect.code.CtInvocation;
import spoon.reflect.code.CtReturn;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;

/**
 * 
 * @author Matias Martinez
 *
 */
public class JSonResultOriginal {
	public static final String TYPE_ROTTEN = "type";

	private static final String FULL_ROTTEN_TEST = "Full_Rotten_Test";

	private static final String SMOKE_TEST = "Smoke_Test";

	private static final String TEST_MISSED_FAIL = "Rotten_Missed_Fail";

	private static final String TEST_WITH_REDUNDANT_ASSERTION = "Test_Redundant_Assertion";

	private static final String TEST_WITH_EXCEPTION = "Test_with_Exception";

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

	public JsonObject toJsonError(String name, ProjectRepairFacade projectFacade, Exception e) {

		this.projectFacade = projectFacade;

		JsonObject root = new JsonObject();
		root.addProperty("project", projectFacade.getProperties().getFixid());
		JsonObject summary = new JsonObject();
		root.add("project", summary);
		String location = ConfigurationProperties.getProperty("location");
		String commitid = executeCommand(location, "git rev-parse HEAD");

		summary.addProperty("commitid", commitid);

		root.addProperty("error", e.getMessage());
		JsonArray testsArray = new JsonArray();
		root.add("tests", testsArray);

		return root;
	}

	public JsonObject toJson(String name, ProjectRepairFacade projectFacade, List<TestInspectionResult> resultByTest) {

		this.projectFacade = projectFacade;

		JsonObject root = new JsonObject();
		root.addProperty("project", projectFacade.getProperties().getFixid());
		JsonObject summary = new JsonObject();
		root.add("project", summary);
		String projectName = name;
		String location = ConfigurationProperties.getProperty("location");
		String commitid = executeCommand(location, "git rev-parse HEAD");
		String branch = executeCommand(location, "git rev-parse --abbrev-ref HEAD");
		String remote = executeCommand(location, "git config --get remote.origin.url");
		String projectsubfolder = ConfigurationProperties.getProperty("projectsubfolder");
		summary.addProperty("commitid", commitid);

		int nrRtest = 0, nrRtAssertion = 0, nrRtHelperCall = 0, nrRttHelperAssert = 0, nrSkip = 0, nrAllMissed = 0,
				nrAllRedundant = 0, nrSmokeTest = 0, nrRtFull = 0, nrTestWithControlStruct = 0, nrTestWithHelper = 0,
				nrWithExceptions = 0;

		JsonArray testsArray = new JsonArray();
		root.add("tests", testsArray);
		Set<String> rTestclasses = new HashSet<>();
		for (TestInspectionResult tr : resultByTest) {

			TestRottenAnalysisResult resultClassification = tr.generateFinalResult();

			JsonObject testjson = new JsonObject();
			JsonArray typesRottens = new JsonArray();
			Set<String> uniquesTypesRottern = new HashSet();
			testjson.add("rotten_types_summary", typesRottens);

			JsonArray summaryRottens = new JsonArray();
			testjson.add("rotten_info", summaryRottens);
			testjson.addProperty("testclass", tr.getNameOfTestClass());
			testjson.addProperty("testname", tr.getTestMethodFromClass());
			testjson.addProperty("expectsexception", (tr.getExpectException().size() > 0) ? "true" : "false");
			testjson.addProperty("isonlyassume", (tr.isOnlyAssumeExecuted()) ? "true" : "false");
			testjson.addProperty("nr_assume", tr.getAllAssumesFromTest().size());

			writeJsonLink(commitid, branch, remote, projectsubfolder, tr.getTestMethodModel(), testjson);

			boolean onerotten = false;

			boolean hasControlFlow = tr.hasControlFlow();
			nrTestWithControlStruct += (hasControlFlow) ? 1 : 0;
			testjson.addProperty("hasControlFlow", hasControlFlow);

			boolean hasHelperCall = tr.hasHelperCall();
			nrTestWithHelper += (hasHelperCall) ? 1 : 0;

			boolean hasFailInvocation = tr.hasFailInvocation();
			testjson.addProperty("hasFailInvocation", hasFailInvocation);

			boolean hasTryCatch = tr.hasTryCatch();
			testjson.addProperty("hasTryCatch", hasTryCatch);

			// Here the complex:

			// Asserts

			int r = add_ASSERTIONS(projectFacade, commitid, branch, remote, projectsubfolder, tr, summaryRottens,
					uniquesTypesRottern, resultClassification.contextAssertion, ROTTEN_CONTEXT_DEP_ASSERTIONS);
			onerotten = onerotten || (r > 0);
			nrRtAssertion += (r > 0) ? 1 : 0;
			//

			r = add_HELPERS_CALL(commitid, branch, remote, projectsubfolder, tr, summaryRottens, uniquesTypesRottern,
					resultClassification.contextHelperCall, ROTTEN_CONTEXT_DEP_HELPERS_CALL);
			onerotten = onerotten || (r > 0);
			nrRtHelperCall += (r > 0) ? 1 : 0;

			//

			r = add_HELPERS_ASSERTION(commitid, branch, remote, projectsubfolder, tr, summaryRottens,
					uniquesTypesRottern, resultClassification.contextHelperAssertion, ROTTEN_CONTEXT_DEP_ASSERTIONS);
			onerotten = onerotten || (r > 0);
			nrRttHelperAssert += (r > 0) ? 1 : 0;
			//
			/// ------Now the full-----

			r = add_ASSERTIONS(projectFacade, commitid, branch, remote, projectsubfolder, tr, summaryRottens,
					uniquesTypesRottern, resultClassification.fullRottenAssert, FULL_ROTTEN_TEST_ASSERTIONS);
			onerotten = onerotten || (r > 0);
			nrRtFull += (r > 0) ? 1 : 0;
			//

			r = add_HELPERS_CALL(commitid, branch, remote, projectsubfolder, tr, summaryRottens, uniquesTypesRottern,
					resultClassification.fullRottenHelperCall, FULL_ROTTEN_TEST_HELPERS_CALL);
			onerotten = onerotten || (r > 0);
			nrRtFull += (r > 0) ? 1 : 0;

			//
			r = add_HELPERS_ASSERTION(commitid, branch, remote, projectsubfolder, tr, summaryRottens,
					uniquesTypesRottern, resultClassification.fullRottenHelperAssert,
					FULL_ROTTEN_TEST_HELPERS_ASSERTION);
			onerotten = onerotten || (r > 0);
			nrRtFull += (r > 0) ? 1 : 0;

			//
			if (!resultClassification.skip.isEmpty()) {

				for (Skip iSkip : resultClassification.skip) {
					CtReturn skip = iSkip.executedReturn;
					JsonObject singleSkip = new JsonObject();
					singleSkip.addProperty("code", skip.toString().toString());
					singleSkip.addProperty("line", getPosition(skip));
					singleSkip.add("parent_types", getParentTypes(skip));
					onerotten = true;
					summaryRottens.add(singleSkip);
					singleSkip.addProperty(TYPE_ROTTEN, ROTTEN_SKIP);
					writeJsonLink(commitid, branch, remote, projectsubfolder, skip, singleSkip);
					uniquesTypesRottern.add(ROTTEN_SKIP);
				}
				nrSkip++;
			}

			//
			if (!resultClassification.missedFail.isEmpty()) {
				for (AsAssertion missedInv : resultClassification.missedFail) {
					JsonObject missedJson = new JsonObject();
					missedJson.addProperty("code_assertion", missedInv.toString().toString());
					missedJson.addProperty("line_assertion", getPosition(missedInv.getCtAssertion()));
					missedJson.addProperty("path_assertion",
							getRelativePath(missedInv.getCtAssertion(), projectFacade));
					writeJsonLink(commitid, branch, remote, projectsubfolder, missedInv.getCtAssertion(), missedJson);
					onerotten = true;
					summaryRottens.add(missedJson);
					missedJson.addProperty(TYPE_ROTTEN, TEST_MISSED_FAIL);
					uniquesTypesRottern.add(TEST_MISSED_FAIL);
				}
				nrAllMissed++;
			}

			//
			if (!resultClassification.redundantAssertion.isEmpty()) {
				for (AsAssertion missedInv : resultClassification.redundantAssertion) {
					JsonObject missedJson = new JsonObject();
					missedJson.addProperty("code_assertion", missedInv.toString().toString());
					missedJson.addProperty("line_assertion", getPosition(missedInv.getCtAssertion()));
					missedJson.addProperty("path_assertion",
							getRelativePath(missedInv.getCtAssertion(), projectFacade));
					writeJsonLink(commitid, branch, remote, projectsubfolder, missedInv.getCtAssertion(), missedJson);
					onerotten = true;
					summaryRottens.add(missedJson);
					missedJson.addProperty(TYPE_ROTTEN, TEST_WITH_REDUNDANT_ASSERTION);
					uniquesTypesRottern.add(TEST_WITH_REDUNDANT_ASSERTION);
				}
				nrAllRedundant++;
			}
			boolean withexception = false;
			if (tr.isExceptionExpected() && tr.testElementsNotPresentInTest()) {
				JsonObject testWithException = new JsonObject();
				summaryRottens.add(testWithException);
				testWithException.addProperty(TYPE_ROTTEN, TEST_WITH_EXCEPTION);

				JsonArray expEx = new JsonArray();
				for (String ee : tr.getExpectException()) {
					expEx.add(ee);
				}
				JsonArray failsAr = new JsonArray();
				//
				testWithException.add("expected_exception", expEx);
				testWithException.add("fails", failsAr);

				for (CtInvocation inv : tr.allFailsFromTest) {

					JsonObject failJson = new JsonObject();
					failJson.addProperty("code_assertion", inv.toString().toString());
					failJson.addProperty("line_assertion", getPosition(inv));
					failJson.addProperty("path_assertion", getRelativePath(inv, projectFacade));
					writeJsonLink(commitid, branch, remote, projectsubfolder, inv, failJson);
					onerotten = true;
					failsAr.add(failJson);
				}

				withexception = true;
				nrWithExceptions++;
				uniquesTypesRottern.add(TEST_WITH_EXCEPTION);
			}

			if (tr.isSmokeTest() && tr.getExpectException().isEmpty()
					&& tr.getAllExpectedExceptionFromTest().isEmpty()) {

				List<CtInvocation> allAssertionsFromTest = resultClassification.getOtherMethodInvocations();

				testsArray.add(testjson);
				rTestclasses.add(tr.getNameOfTestClass());
				nrSmokeTest += 1;
				JsonObject smokeTest = new JsonObject();
				smokeTest.addProperty(TYPE_ROTTEN, SMOKE_TEST);

				JsonArray missrarray = new JsonArray();
				smokeTest.add("other_method_invocation", missrarray);
				for (CtInvocation otherinv : allAssertionsFromTest) {
					missrarray.add(createMethodSignature(otherinv));
				}

				summaryRottens.add(smokeTest);

				uniquesTypesRottern.add(SMOKE_TEST);

			}

			/// Dont include smoke
			if (onerotten) {
				testsArray.add(testjson);
				nrRtest++;
				rTestclasses.add(tr.getNameOfTestClass());
			} else if (withexception) {
				testsArray.add(testjson);
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

		summary.addProperty("name", projectName);
		summary.addProperty("remote", remote);
		summary.addProperty("localLocation", location);
		summary.addProperty("nr_Test_With_Control_Flow_Stmt", nrTestWithControlStruct);
		summary.addProperty("nr_Test_With_Helper", nrTestWithHelper);
		summary.addProperty("nr_All_Test", resultByTest.size());
		summary.addProperty("nr_Rotten_Test_Units", nrRtest);
		summary.addProperty("nr_" + this.ROTTEN_CONTEXT_DEP_ASSERTIONS, nrRtAssertion);
		summary.addProperty("nr_" + this.TEST_WITH_REDUNDANT_ASSERTION, nrAllRedundant);
		summary.addProperty("nr_" + this.ROTTEN_CONTEXT_DEP_HELPERS_CALL, nrRtHelperCall);
		summary.addProperty("nr_" + this.ROTTEN_CONTEXT_DEP_HELPERS_ASSERTION, nrRttHelperAssert);
		summary.addProperty("nr_" + this.ROTTEN_SKIP, nrSkip);
		summary.addProperty("nr_" + this.TEST_MISSED_FAIL, nrAllMissed);
		summary.addProperty("nr_" + this.SMOKE_TEST, nrSmokeTest);
		summary.addProperty("nr_" + this.FULL_ROTTEN_TEST, nrRtFull);
		summary.addProperty("nr_" + this.TEST_WITH_EXCEPTION, nrWithExceptions);

		return root;
	}

	public int getPosition(CtElement inv) {
		try {
			return inv.getPosition().getLine();
		} catch (Exception e) {
			log.error("Error getting position of element");
			e.printStackTrace();
			return -1;
		}
	}

	public int add_ASSERTIONS(ProjectRepairFacade projectFacade, String commitid, String branch, String remote,
			String projectsubfolder, TestInspectionResult tr, JsonArray summaryRottens, Set<String> uniquesTypesRottern,
			List<AsAssertion> notExecutedAssert, String ROTTEN_CONTEXT_DEP_ASSERTIONS) {
		int nrRtAssertion = 0;
		if (!notExecutedAssert.isEmpty()) {

			log.debug("-- Test  " + tr.getNameOfTestClass() + ": " + tr.getTestMethodFromClass());

			for (AsAssertion assertion : notExecutedAssert) {
				CtInvocation anInvocation = assertion.getCtAssertion();
				log.debug("-R-Assertion:-> " + anInvocation);
				JsonObject jsonsingleAssertion = new JsonObject();
				jsonsingleAssertion.addProperty("code", anInvocation.toString());
				jsonsingleAssertion.addProperty("line", getPosition(anInvocation));
				jsonsingleAssertion.addProperty("path", getRelativePath(anInvocation, projectFacade));
				jsonsingleAssertion.addProperty("inbranch", assertion.isFp());

				writeJsonLink(commitid, branch, remote, projectsubfolder, anInvocation, jsonsingleAssertion);
				summaryRottens.add(jsonsingleAssertion);
				jsonsingleAssertion.addProperty(TYPE_ROTTEN, ROTTEN_CONTEXT_DEP_ASSERTIONS);
				jsonsingleAssertion.add("parent_types", getParentTypes(anInvocation));
				nrRtAssertion++;

				uniquesTypesRottern.add(ROTTEN_CONTEXT_DEP_ASSERTIONS);
			}
		}
		return nrRtAssertion;
	}

	public int add_HELPERS_ASSERTION(String commitid, String branch, String remote, String projectsubfolder,
			TestInspectionResult tr, JsonArray summaryRottens, Set<String> uniquesTypesRottern,
			List<Helper> notExecutedHelper, String ROTTEN_CONTEXT_DEP_HELPERS_ASSERTION) {
		int nrRttHelperAssert = 0;
		if (!notExecutedHelper.isEmpty()) {
			log.debug("-R Helper assertion- " + tr.getNameOfTestClass() + ": " + tr.getTestMethodFromClass());

			List<JsonObject> result = helperToJson(notExecutedHelper,
					tr.getClassificationHelperCall().getResultNotExecuted(), commitid, branch, remote, projectsubfolder,
					false);

			if (!result.isEmpty()) {
				for (JsonObject jsonObject : result) {
					summaryRottens.add(jsonObject);
					jsonObject.addProperty(TYPE_ROTTEN, ROTTEN_CONTEXT_DEP_HELPERS_ASSERTION);

				}
				uniquesTypesRottern.add(ROTTEN_CONTEXT_DEP_HELPERS_ASSERTION);
			}

			nrRttHelperAssert += notExecutedHelper.size();
		}
		return nrRttHelperAssert;
	}

	public int add_HELPERS_CALL(String commitid, String branch, String remote, String projectsubfolder,
			TestInspectionResult tr, JsonArray summaryRottens, Set<String> uniquesTypesRottern,
			List<Helper> notExecutedHelperInvoc, String ROTTEN_CONTEXT_DEP_HELPERS_CALL) {
		int nrRtHelperCall = 0;
		if (!notExecutedHelperInvoc.isEmpty()) {
			System.out.println("-- R Helper call  " + tr.getNameOfTestClass() + ": " + tr.getTestMethodFromClass());

			List<JsonObject> result = helperToJson(notExecutedHelperInvoc, Lists.newArrayList(), commitid, branch,
					remote, projectsubfolder, true);

			if (!result.isEmpty()) {

				for (JsonObject jsonObject : result) {
					summaryRottens.add(jsonObject);
					jsonObject.addProperty(TYPE_ROTTEN, ROTTEN_CONTEXT_DEP_HELPERS_CALL);
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
					+ getRelativePath(anInvocation, this.projectFacade) + "#L" + getPosition(anInvocation));
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
		jsonsingleHelper.addProperty("line", getPosition(ctAssertion));
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
		try {
			return anInvocation.getPosition().getFile().getAbsolutePath().replace("./", "")
					.replace(projectFacade.getProperties().getOriginalProjectRootDir().replace("./", ""), "");
		} catch (Exception e) {
			log.error("Error in position relative path");
			e.printStackTrace();
			return "NoPosition";
		}
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
