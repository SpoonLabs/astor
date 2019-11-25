package fr.inria.astor.approaches.extensions.rt.elements;

import java.util.ArrayList;
import java.util.List;

import spoon.reflect.code.CtReturn;

/**
 * 
 * @author Matias Martinez
 *
 */
public class Skip {

	protected CtReturn executedReturn;
	protected List<TestElement> notExecutedTestElements = new ArrayList<>();

	public Skip(CtReturn executedReturn) {
		super();
		this.executedReturn = executedReturn;
	}

	public CtReturn getExecutedReturn() {
		return executedReturn;
	}

	public void setExecutedReturn(CtReturn executedReturn) {
		this.executedReturn = executedReturn;
	}

	public List<TestElement> getNotExecutedTestElements() {
		return notExecutedTestElements;
	}

	public void setNotExecutedTestElements(List<TestElement> notExecutedTestElements) {
		this.notExecutedTestElements = notExecutedTestElements;
	}

}