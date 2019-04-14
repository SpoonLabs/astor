package fr.inria.astor.approaches.tos.core.evalTos;

import java.util.List;

import com.google.gson.JsonElement;

import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;

/**
 * 
 * @author Matias Martinez
 *
 */
public interface IPrediction {

	/**
	 * 
	 * @return
	 */
	public List<PredictionElement> getElementsWithPrediction();

	public List<AstorOperator> getPrediction(PredictionElement element);

	public JsonElement toJson();

	public List<AstorOperator> getAllOperationsPredicted();

}
