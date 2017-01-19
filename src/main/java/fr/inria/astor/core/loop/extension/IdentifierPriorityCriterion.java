package fr.inria.astor.core.loop.extension;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.inria.astor.core.entities.ProgramVariant;

/**
 * Sort program variant according to theirs identifiers (ASC).
 * 
 * @author Matias Martinez
 *
 */
public class IdentifierPriorityCriterion implements SolutionVariantSortCriterion {

	/**
	 * Receives a list of patches and returns another list with the patches
	 * sorted by the ID.
	 */
	@Override
	public List<ProgramVariant> priorize(List<ProgramVariant> patches) {
		List<ProgramVariant> toSort = new ArrayList<>(patches);
		Collections.sort(toSort, (o1, o2) -> Integer.compare(o1.getId(), o2.getId()));

		return toSort;
	}
}
