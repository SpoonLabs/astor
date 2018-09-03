package fr.inria.astor.approaches.scaffold.scaffoldsynthesis.request;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.generator.ScaffoldGenerator;
import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.generator.candidate.ExpressionCreator;
import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.generator.candidate.ExpressionValueCandidate;

public abstract class AbstractRequest {
	
	protected ExpressionValueCandidate[] candidates;
	protected ScaffoldGenerator generators;
	protected boolean hasInit = false;
	protected ExpressionCreator exprGenerator = new ExpressionCreator();
	protected List<String> inputNames = new ArrayList<>();
	protected List<Object> inputValues = new ArrayList<>();
	protected Class<?> targetType = null;

	public AbstractRequest() {
		
	}
	
	public AbstractRequest(Object[] inputValues, String[] inputNames) {
		this.inputNames = Arrays.asList(inputNames);
		this.inputValues = Arrays.asList(inputValues);
	}
	
	public AbstractRequest(Object[] inputValues, String[] inputNames, Class<?> targetType) {
		this.inputNames = Arrays.asList(inputNames);
		this.inputValues = Arrays.asList(inputValues);
		this.targetType = targetType;
	}

	public abstract Object invoke();

	protected void initCandidates(Class candClass, String[] names, Object[] vals, boolean useDefaulValues) {
		exprGenerator.addTypeVals(candClass, names, vals);
		candidates = exprGenerator.getSJCandidates(candClass, useDefaulValues);
	}

	public void reset() {
		if (generators != null)
			generators.reset();
	}

	public String toString() {
		return generators == null ? "" : generators.toString();
	}

	public boolean hasInit() {
		return hasInit;
	}
}
