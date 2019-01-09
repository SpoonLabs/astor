package fr.inria.astor.approaches.tos.core.evalTos.ingredients;

import fr.inria.astor.util.MapList;

/**
 * A pool with all ingredients from Dynamod
 * 
 * @author Matias Martinez
 *
 */
public class DynaIngredientPool {

	// Key: test name, value list of clusters, each cluster is a list of
	// evaluated expressions
	public MapList<String, ClusterExpressions> clusterEvaluatedExpressionsByTests;

	public DynaIngredientPool(MapList<String, ClusterExpressions> cluster) {
		this.clusterEvaluatedExpressionsByTests = cluster;
	}

	public MapList<String, ClusterExpressions> getClusterEvaluatedExpressions() {
		return clusterEvaluatedExpressionsByTests;
	}

	public void setClusterEvaluatedExpressions(MapList<String, ClusterExpressions> clusterEvaluatedExpressions) {
		this.clusterEvaluatedExpressionsByTests = clusterEvaluatedExpressions;
	}

}
