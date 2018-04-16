package fr.inria.astor.approaches.tos.entity.placeholders;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.inria.astor.approaches.tos.core.PatchGenerator;
import fr.inria.astor.approaches.tos.entity.transf.Transformation;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.util.MapList;
import spoon.reflect.code.CtCodeElement;
import spoon.reflect.code.CtFieldAccess;
import spoon.reflect.code.CtVariableAccess;

/**
 * Entity that represents a TOS with 1+ placeholders in var Name
 * 
 * @author Matias Martinez
 *
 */
@SuppressWarnings("rawtypes")
public class VariablePlaceholder extends Placeholder {
	/**
	 * Maps a variable name with a placeholder name. Used for replacing
	 * different variables with the same name
	 */
	Map<String, String> placeholderVarNamesMappings = null;
	/**
	 * Links a placeholder name with a ctVariable
	 */
	MapList<String, CtVariableAccess> palceholdersToVariables = null;
	/**
	 * List with all variables that are not modified i.e., are not a TOS.
	 */
	List<CtVariableAccess> variablesNotModified = null;

	public VariablePlaceholder(MapList<String, CtVariableAccess> palceholders,
			Map<String, String> placeholderVarNamesMappings, List<CtVariableAccess> variablesNotModified) {
		this.palceholdersToVariables = palceholders;
		this.placeholderVarNamesMappings = placeholderVarNamesMappings;
		this.variablesNotModified = variablesNotModified;
	}

	@Override
	public String toString() {
		String r = "";
		for (String ph : this.palceholdersToVariables.keySet()) {
			r += ph + " --> " + this.palceholdersToVariables.get(ph).get(0).getVariable().getSimpleName();
			r += ", ";
		}

		return this.getClass().getSimpleName() + ":  (" + r + ")";
	}

	@SuppressWarnings("unchecked")
	@Override
	public void apply() {
		olderNameOfVariable.clear();
		for (String placeholder : palceholdersToVariables.keySet()) {

			List<CtVariableAccess> variables = palceholdersToVariables.get(placeholder);
			for (CtVariableAccess variableUnderAnalysis : variables) {
				this.olderNameOfVariable.put(variableUnderAnalysis,
						variableUnderAnalysis.getVariable().getSimpleName());
				variableUnderAnalysis.getVariable().setSimpleName(placeholder);
				// workaround: Problems with var Shadowing
				variableUnderAnalysis.getFactory().getEnvironment().setNoClasspath(true);
				if (variableUnderAnalysis instanceof CtFieldAccess) {
					CtFieldAccess fieldAccess = (CtFieldAccess) variableUnderAnalysis;
					fieldAccess.getVariable().setDeclaringType(null);

				}
			}
		}
	}

	Map<CtVariableAccess, String> olderNameOfVariable = new HashMap<>();

	public void revert() {

		for (CtVariableAccess va : olderNameOfVariable.keySet()) {
			String oldname = olderNameOfVariable.get(va);
			va.getVariable().setSimpleName(oldname);
		}
		olderNameOfVariable.clear();
	}

	@Override
	public List<CtCodeElement> getAffectedElements() {
		List<CtCodeElement> ces = new ArrayList<>();

		for (List vars : this.palceholdersToVariables.values()) {
			ces.addAll(vars);
		}
		return ces;
	}

	@Override
	public List<Transformation> visit(ModificationPoint modificationPoint, PatchGenerator visitor) {

		return visitor.process(modificationPoint, this);

	};

	public MapList<String, CtVariableAccess> getPalceholders() {
		return palceholdersToVariables;
	}

	public void setPalceholders(MapList<String, CtVariableAccess> palceholders) {
		this.palceholdersToVariables = palceholders;
	}

	public List<CtVariableAccess> getVariablesNotModified() {
		return variablesNotModified;
	}

	public void setVariablesNotModified(List<CtVariableAccess> variablesNotModified) {
		this.variablesNotModified = variablesNotModified;
	}

	public Map<String, String> getPlaceholderVarNamesMappings() {
		return placeholderVarNamesMappings;
	}

	public void setPlaceholderVarNamesMappings(Map<String, String> placeholderVarNamesMappings) {
		this.placeholderVarNamesMappings = placeholderVarNamesMappings;
	}

}
