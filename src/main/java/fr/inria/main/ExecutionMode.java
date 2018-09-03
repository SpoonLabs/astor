package fr.inria.main;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Execution modes included in Astor framework.
 * @author Matias Martinez matias.martinez@inria.fr
 *
 */
public enum ExecutionMode {
	DeepRepair(Collections.singletonList("deeprepair")),
	CARDUMEN(Collections.singletonList("cardumen")),
	jGenProg(Collections.singletonList("jgenprog")),
	jKali(Collections.singletonList("jkali")),
	MutRepair(Arrays.asList("mutation","jmutrepair", "mutrepair")),
	EXASTOR(Arrays.asList("exhaustive", "exastor")),
	SCAFFOLD(Collections.singletonList("scaffold")),
	custom(Collections.singletonList("custom"));

	private List<String> acceptedNames;

	ExecutionMode(List<String> acceptedNames) {
		this.acceptedNames = acceptedNames;
	}

	public List<String> getAcceptedNames() {
		return acceptedNames;
	}
}
