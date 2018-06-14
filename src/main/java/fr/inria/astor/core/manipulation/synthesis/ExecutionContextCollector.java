package fr.inria.astor.core.manipulation.synthesis;

import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.manipulation.synthesis.dynamoth.DynamothSynthesisContext;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.extension.AstorExtensionPoint;

/**
 * 
 * @author Matias Martinez
 *
 */
public interface ExecutionContextCollector extends AstorExtensionPoint {

	public DynamothSynthesisContext collectValues(ProjectRepairFacade facade, ModificationPoint mp);
}
