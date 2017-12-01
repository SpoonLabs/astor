package fr.inria.astor.approaches.extensions;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.core.loop.spaces.ingredients.transformations.RandomTransformationStrategy;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;

/**
 * 
 * @author Matias Martinez
 *
 */
public class TibraApproach extends JGenProg {

	public TibraApproach(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);
	}

	@Override
	public void loadExtensionPoints() throws Exception {
		ConfigurationProperties.setProperty("ingredienttransformstrategy",RandomTransformationStrategy.class.getCanonicalName());
		super.loadExtensionPoints();
	}

	
}
