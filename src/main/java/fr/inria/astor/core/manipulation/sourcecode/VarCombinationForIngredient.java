package fr.inria.astor.core.manipulation.sourcecode;

import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import spoon.reflect.declaration.CtVariable;

/**
 * 
 * @author Matias Martinez
 *
 */
public class VarCombinationForIngredient {

	private Map<String, CtVariable> combination;
	private String combinationString = null;
	private Double probality;
	private int size;

	public VarCombinationForIngredient(Map<String, CtVariable> combination) {
		this.combination = combination;
		combination2String(combination);
	}

	public void combination2String(Map<String, CtVariable> combination) {

		List<String> flatCombination = combination.values().stream().map(CtVariable::getSimpleName)
				.collect(Collectors.toList()).stream().sorted(Comparator.comparing(n -> n.toString())).distinct()
				.collect(Collectors.toList());

		this.combinationString = flatCombination.stream().collect(Collectors.joining(" "));
		this.size = flatCombination.size();

	}

	public Map<String, CtVariable> getCombination() {
		return combination;
	}

	public void setCombination(Map<String, CtVariable> combination) {
		this.combination = combination;
	}

	public String getCombinationString() {
		return combinationString;
	}

	public void setCombinationString(String combinationString) {
		this.combinationString = combinationString;
	}

	public Double getProbality() {
		return probality;
	}

	public void setProbality(Double probality) {
		this.probality = probality;
	}

	@Override
	public String toString() {
		return String.format("VarCombinationForIngredient [ %s, p= %.2f ]", combinationString, probality);
	}

	public int getSize() {
		return size;
	}

}
