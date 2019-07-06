package fr.inria.astor.approaches.jgenprog.extension;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.RandomTransformationStrategy;

/**
 * 
 * @author Matias Martinez
 *
 */
public class TibraApproach extends JGenProg {

	public TibraApproach(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);

		ConfigurationProperties.setProperty("ingredienttransformstrategy",
				RandomTransformationStrategy.class.getCanonicalName());
		ConfigurationProperties.setProperty("consideryvarloops", "false");
	}

}
