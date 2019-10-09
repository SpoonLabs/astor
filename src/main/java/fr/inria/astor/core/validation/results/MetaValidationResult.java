package fr.inria.astor.core.validation.results;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.inria.astor.core.entities.validation.VariantValidationResult;

/**
 * Contains a set of Validation Results
 * 
 * @author Matias Martinez
 *
 */
public class MetaValidationResult extends TestCasesProgramValidationResult implements VariantValidationResult {

	protected Map<Integer, VariantValidationResult> validations = new HashMap<>();
	protected Map<Integer, VariantValidationResult> sucessfulValidations = new HashMap<>();

	protected List<Map<Integer, Integer>> allCandidates;

	public MetaValidationResult(List<Map<Integer, Integer>> allCandidates) {
		super(null);
		this.allCandidates = allCandidates;
	}

	public void addValidation(Integer id, VariantValidationResult validation) {
		this.validations.put(id, validation);
		if (validation.isSuccessful())
			sucessfulValidations.put(id, validation);
	}

	public VariantValidationResult getValidation(Integer id) {
		return this.validations.get(id);
	}

	@Override
	public boolean isSuccessful() {
		for (VariantValidationResult pv : this.validations.values()) {
			if (pv.isSuccessful()) {
				return true;
			}
		}
		return false;
	};

	public String toString() {
		String r = "\nMeta-evaluation";
		for (Integer mode : this.validations.keySet()) {

			r += "\n mutant-id " + mode + ": (" + allCandidates.get(mode - 1) + ") "
					+ this.getValidation(mode).toString();
		}
		return r;
	}

	public Map<Integer, VariantValidationResult> getSucessfulValidations() {
		return sucessfulValidations;
	}

	public List<Map<Integer, Integer>> getAllCandidates() {
		return allCandidates;
	}

	public void setAllCandidates(List<Map<Integer, Integer>> allCandidates) {
		this.allCandidates = allCandidates;
	}

}
