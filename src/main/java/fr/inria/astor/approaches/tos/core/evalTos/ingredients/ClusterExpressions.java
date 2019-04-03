package fr.inria.astor.approaches.tos.core.evalTos.ingredients;

import java.util.ArrayList;

import fr.inria.astor.core.manipulation.synthesis.dynamoth.EvaluatedExpression;

/**
 * Collection of Expressions put together due to a given criterion.
 * 
 * @author Matias Martinez
 *
 */

public class ClusterExpressions extends ArrayList<EvaluatedExpression> {

	String clusterType = null;

	public ClusterExpressions(String clusterType) {
		super();
		this.clusterType = clusterType;
	}

	public String getClusterType() {
		return clusterType;
	}

	public void setClusterType(String clusterType) {
		this.clusterType = clusterType;
	}

	@Override
	public String toString() {
		return "members: " + super.toString();
	}

}
