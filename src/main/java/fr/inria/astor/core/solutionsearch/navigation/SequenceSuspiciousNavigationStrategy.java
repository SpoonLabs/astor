package fr.inria.astor.core.solutionsearch.navigation;

import java.util.Collections;
import java.util.List;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;

/**
 * 
 * @author Matias Martinez
 *
 */
public class SequenceSuspiciousNavigationStrategy implements SuspiciousNavigationStrategy {

	@Override
	public List<ModificationPoint> getSortedModificationPointsList(List<ModificationPoint> modificationPoints) {
		if (modificationPoints.isEmpty()) {
			return Collections.EMPTY_LIST;
		}

		ProgramVariant variant = modificationPoints.get(0).getProgramVariant();

		int i = variant.getLastModificationPointAnalyzed();
		if (i < modificationPoints.size()) {
			variant.setLastModificationPointAnalyzed(i + 1);
			return modificationPoints.subList(i, i + 1);
		}
		return Collections.EMPTY_LIST;
	}

}
