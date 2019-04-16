package fr.inria.astor.approaches.tos.core.evalTos;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.solutionsearch.extension.AstorExtensionPoint;

/**
 * 
 * @author Matias Martinez
 *
 */
public interface IPredictor extends AstorExtensionPoint {
	PredictionResult computePredictionsForModificationPoint(ModificationPoint iModifPoint);
}
