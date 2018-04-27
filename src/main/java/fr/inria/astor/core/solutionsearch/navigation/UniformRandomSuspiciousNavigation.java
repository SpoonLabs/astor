package fr.inria.astor.core.solutionsearch.navigation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
/**
 * 
 * @author Matias Martinez
 *
 */
public class UniformRandomSuspiciousNavigation implements SuspiciousNavigationStrategy {

	@Override
	public List<ModificationPoint> getSortedModificationPointsList(ProgramVariant variant) {
		List<ModificationPoint> shuffList = new ArrayList<ModificationPoint>(variant.getModificationPoints());
		Collections.shuffle(shuffList);
		return shuffList;
	}

}
