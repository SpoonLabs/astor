package fr.inria.astor.approaches.extensions.rt.core;

import java.util.ArrayList;
import java.util.List;

public class Classification<T> {

	public List<T> resultNotExecuted = new ArrayList<>();
	public List<T> resultExecuted = new ArrayList<>();

	public List<T> getResultNotExecuted() {
		return resultNotExecuted;
	}

	public List<T> getResultExecuted() {
		return resultExecuted;
	}

	public List<T> getAll() {

		List<T> resultAll = new ArrayList<>();
		resultAll.addAll(resultExecuted);
		resultAll.addAll(resultNotExecuted);
		return resultAll;
	}

	@Override
	public String toString() {
		return "Classification [#resultNotExecuted=" + resultNotExecuted.size() + ", #resultExecuted="
				+ resultExecuted.size() + "]";
	}

}