package fr.inria.astor.approaches.tos.core.evalTos;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.solutionsearch.extension.AstorExtensionPoint;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.util.MapList;
import spoon.reflect.declaration.CtElement;

/**
 * 
 * @author Matias Martinez
 *
 */
public interface IPredictor extends AstorExtensionPoint {
	MapList<CtElement, AstorOperator> computePredictionsForModificationPoint(ModificationPoint iModifPoint);
}
