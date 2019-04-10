package fr.inria.astor.approaches.tos.core.evalTos;

import java.util.List;

import com.google.gson.JsonObject;

import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import spoon.reflect.declaration.CtElement;

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
	public List<CtElement> getElementsWithPrediction();

	public List<AstorOperator> getPrediction(CtElement element);

	public JsonObject toJson();

}
