package fr.inria.astor.approaches.extensions.rt.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.inria.astor.approaches.extensions.rt.elements.AsAssertion;
import fr.inria.astor.approaches.extensions.rt.elements.Helper;
import fr.inria.astor.approaches.extensions.rt.elements.Skip;
import fr.inria.astor.approaches.extensions.rt.elements.TestElement;
import spoon.reflect.code.CtInvocation;

public class TestAnalysisResult {

	public List<Helper> fullRottenHelperCall = Collections.EMPTY_LIST;
	public List<Helper> fullRottenHelperAssert = Collections.EMPTY_LIST;
	public List<AsAssertion> fullRottenAssert = Collections.EMPTY_LIST;
	public boolean smokeTest = false;
	public List<AsAssertion> missedFail = Collections.EMPTY_LIST;
	public List<Skip> skip = Collections.EMPTY_LIST;
	public List<Helper> contextHelperCall = Collections.EMPTY_LIST;
	public List<Helper> contextHelperAssertion = Collections.EMPTY_LIST;
	public List<AsAssertion> contextAssertion = Collections.EMPTY_LIST;

	public List<CtInvocation> otherMethodInvocations = Collections.EMPTY_LIST;
	public List<AsAssertion> redundantAssertion = Collections.EMPTY_LIST;

	public TestAnalysisResult(
			//
			List<Helper> fullRottenHelperCall, List<Helper> fullRottenHelperAssert, //
			List<AsAssertion> fullRottenAssert, //
			boolean smokeTest, List<AsAssertion> missed, //
			List<AsAssertion> allRedundantFromTest, List<Helper> contextHelperCall,
			List<Helper> contextHelperAssertion, List<AsAssertion> contextAssertion,
			List<CtInvocation> allAssertionsFromTest) {
		super();
		this.fullRottenHelperCall = fullRottenHelperCall;
		this.fullRottenHelperAssert = fullRottenHelperAssert;
		this.fullRottenAssert = fullRottenAssert;
		this.smokeTest = smokeTest;
		this.missedFail = missed;
		this.redundantAssertion = allRedundantFromTest;
		// this.skip = skip;
		this.contextHelperCall = contextHelperCall;
		this.contextHelperAssertion = contextHelperAssertion;
		this.contextAssertion = contextAssertion;
		this.otherMethodInvocations = allAssertionsFromTest;

	}

	public TestAnalysisResult(List<Skip> skip) {
		this.skip = skip;
	}

	public List<TestElement> getFullRotten() {
		List<TestElement> allRT = new ArrayList<>();
		allRT.addAll(this.fullRottenAssert);
		allRT.addAll(this.fullRottenHelperCall);
		allRT.addAll(this.fullRottenHelperAssert);
		return allRT;
	}

	public List<CtInvocation> getOtherMethodInvocations() {
		return otherMethodInvocations;
	}

	public void setOtherMethodInvocations(List<CtInvocation> otherMethodInvocations) {
		this.otherMethodInvocations = otherMethodInvocations;
	}
}