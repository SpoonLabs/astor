package fr.inria.astor.core.solutionsearch.spaces.operators;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.core.entities.SuspiciousModificationPoint;
import fr.inria.astor.core.entities.WeightElement;
import fr.inria.astor.core.setup.ConfigurationProperties;

/**
 * Weighted selection of operators
 * 
 * @author Matias Martinez
 *
 */
public class WeightedRandomOperatorSelection extends OperatorSelectionStrategy {

	protected static Logger log = Logger.getLogger(WeightedRandomOperatorSelection.class.getName());

	List<WeightElement<?>> weightedOperators = new ArrayList<>();

	public WeightedRandomOperatorSelection(OperatorSpace space) {
		super(space);
		int spacesize = space.size();
		if (ConfigurationProperties.hasProperty("weightsopselection")) {
			String probstring = ConfigurationProperties.getProperty("weightsopselection");
			String[] ps = probstring.split("_");
			if (ps.length != spacesize) {
				log.error("Number of probabilities different than number of operators");
				throw new IllegalArgumentException("wrong number of probabilities");
			}
			for (int i = 0; i < ps.length; i++) {
				double di = new Double(ps[i]);
				weightedOperators.add(new WeightElement<>(this.operatorSpace.getOperators().get(i), di));
			}
		} else {
			// by default
			double uniformprobability = (double) 1 / (double) spacesize;
			for (int i = 0; i < spacesize; i++) {

				weightedOperators
						.add(new WeightElement<>(this.operatorSpace.getOperators().get(i), uniformprobability));
			}
		}
		WeightElement.feedAccumulative(this.weightedOperators);
	}

	@Override
	public AstorOperator getNextOperator() {

		WeightElement<?> selected = WeightElement.selectElementWeightBalanced(this.weightedOperators);
		AstorOperator selectedOp = (AstorOperator) selected.element;
		return selectedOp;
	}

	@Override
	public AstorOperator getNextOperator(SuspiciousModificationPoint modificationPoint) {
		return getNextOperator();
	}

}
