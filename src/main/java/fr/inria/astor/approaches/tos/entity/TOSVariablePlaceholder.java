package fr.inria.astor.approaches.tos.entity;

import java.util.List;
import java.util.Map;

import fr.inria.astor.core.loop.spaces.ingredients.scopes.IngredientSpaceScope;
import fr.inria.astor.util.MapList;
import spoon.reflect.code.CtVariableAccess;
import spoon.reflect.declaration.CtElement;

/**
 * Entity that represents a TOS with 1+ placeholders in var Name
 * 
 * @author Matias Martinez
 *
 */
@SuppressWarnings("rawtypes")
public class TOSVariablePlaceholder extends TOSEntity {
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

	public TOSVariablePlaceholder(CtElement code, IngredientSpaceScope scope, CtElement derivedFrom) {
		super(code, scope, derivedFrom);
	}

	public TOSVariablePlaceholder(CtElement code, IngredientSpaceScope scope, CtElement derivedFrom,
			MapList<String, CtVariableAccess> palceholders, Map<String, String> placeholderVarNamesMappings,
			List<CtVariableAccess> variablesNotModified) {
		super(code, scope, derivedFrom);
		this.palceholdersToVariables = palceholders;
		this.placeholderVarNamesMappings = placeholderVarNamesMappings;
		this.variablesNotModified = variablesNotModified;
	}

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

	@Override
	public String toString() {
		return "TOSVarName [placeholderVarNamesMappings=" + placeholderVarNamesMappings + ", palceholdersToVariables="
				+ palceholdersToVariables + ", variablesNotModified=" + variablesNotModified + ", code=" + code
				+ ", derivedFrom=" + derivedFrom + "]";
	}

}
