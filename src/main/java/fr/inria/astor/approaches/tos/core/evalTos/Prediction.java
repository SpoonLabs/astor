package fr.inria.astor.approaches.tos.core.evalTos;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.util.MapList;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public class Prediction extends MapList<PredictionElement, AstorOperator> implements IPrediction {

	@Override
	public List<PredictionElement> getElementsWithPrediction() {

		return new ArrayList(this.keySet());
	}

	@Override
	public List<AstorOperator> getPrediction(PredictionElement element) {

		return this.get(element);
	}

	@Override
	public JsonElement toJson() {

		JsonObject root = new JsonObject();

		for (PredictionElement predictedElement : this.getElementsWithPrediction()) {
			CtElement element = predictedElement.getElement();
			root.addProperty("code", element.toString());
			root.addProperty("type", element.getClass().getSimpleName());
			root.addProperty("path", element.getPath().toString());
			root.addProperty("index", predictedElement.getIndex());
			JsonArray ops = new JsonArray();
			root.add("ops", ops);
			for (AstorOperator op : this.get(predictedElement)) {
				if (op != null)
					ops.add(op.name());
			}

		}

		return root;
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
