package fr.inria.astor.core.solutionsearch.navigation;

import java.util.ArrayList;
import java.util.List;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.entities.WeightElement;

/**
 * Strategy of navigation based on weight random.
 * 
 * @author Matias Martinez
 *
 */
public class WeightRandomSuspiciousNavitation implements SuspiciousNavigationStrategy {

	@Override
	public List<ModificationPoint> getSortedModificationPointsList(List<ModificationPoint> modificationPoints) {

		List<ModificationPoint> remaining = new ArrayList<ModificationPoint>(modificationPoints);
		List<ModificationPoint> solution = new ArrayList<ModificationPoint>();

		for (int i = 0; i < modificationPoints.size(); i++) {
			List<WeightElement<?>> we = new ArrayList<WeightElement<?>>();
			double sum = 0;
			for (ModificationPoint gen : remaining) {
				double susp = ((SuspiciousModificationPoint) gen).getSuspicious().getSuspiciousValue();
				sum += susp;
				WeightElement<?> w = new WeightElement<>(gen, 0);
				w.weight = susp;
				we.add(w);
			}

			if (sum != 0) {

				for (WeightElement<?> weightCtElement : we) {
					weightCtElement.weight = weightCtElement.weight / sum;
				}

				WeightElement.feedAccumulative(we);
				WeightElement<?> selected = WeightElement.selectElementWeightBalanced(we);

				ModificationPoint selectedg = (ModificationPoint) selected.element;
				remaining.remove(selectedg);
				solution.add(selectedg);
			} else {
				solution.addAll(remaining);
				break;
			}
		}
		return solution;

	}

}
