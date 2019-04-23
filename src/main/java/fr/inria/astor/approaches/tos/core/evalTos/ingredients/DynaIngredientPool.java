package fr.inria.astor.approaches.tos.core.evalTos.ingredients;

import java.util.List;

/**
 * A pool with all ingredients from Dynamod
 * 
 * @author Matias Martinez
 *
 */
public class DynaIngredientPool {

	// Key: test name, value list of clusters, each cluster is a list of
	// evaluated expressions
//	public MapList<String, ClusterExpressions> clusterEvaluatedExpressionsByTests;

	List<ClusterExpressions> clusterEvaluatedExpressionsByTests;

	public DynaIngredientPool(List<ClusterExpressions> cluster) {
		this.clusterEvaluatedExpressionsByTests = cluster;
	}

	public List<ClusterExpressions> getClusterEvaluatedExpressions() {
		return clusterEvaluatedExpressionsByTests;
	}

	public void setClusterEvaluatedExpressions(List<ClusterExpressions> clusterEvaluatedExpressions) {
		this.clusterEvaluatedExpressionsByTests = clusterEvaluatedExpressions;
	}

}
