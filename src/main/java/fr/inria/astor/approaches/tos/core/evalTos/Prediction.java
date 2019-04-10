package fr.inria.astor.approaches.tos.core.evalTos;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonObject;

import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.util.MapList;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class Prediction extends MapList<CtElement, AstorOperator> implements IPrediction {

	@Override
	public List<CtElement> getElementsWithPrediction() {

		return new ArrayList(this.keySet());
	}

	@Override
	public List<AstorOperator> getPrediction(CtElement element) {

		return this.get(element);
	}

	@Override
	public JsonObject toJson() {
		return null;
	}

	@Override
	public List<AstorOperator> getAllOperationsPredicted() {
		List<AstorOperator> allOps = new ArrayList<>();

		for (List ops : this.values()) {
			allOps.addAll(ops);
		}
		return allOps;
	}

}
