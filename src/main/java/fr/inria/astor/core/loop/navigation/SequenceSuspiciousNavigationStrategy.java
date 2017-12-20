package fr.inria.astor.core.loop.navigation;

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
	public List<ModificationPoint> getSortedModificationPointsList(ProgramVariant variant) {
		List<ModificationPoint> modificationPoints = variant.getModificationPoints();
		int i = variant.getLastGenAnalyzed();
		if (i < modificationPoints.size()) {
			variant.setLastModificationPointAnalyzed(i + 1);
			return modificationPoints.subList(i, i + 1);
		}
		return Collections.EMPTY_LIST;
	}

}
