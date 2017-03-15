package fr.inria.astor.approaches.jkali;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.jkali.JKaliSpace;
import fr.inria.astor.core.loop.ExhaustiveSearchEngine;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;

/**
 * 
 * @author Matias Martinez
 *
 */
public class JKaliEngine extends ExhaustiveSearchEngine {

	public JKaliEngine(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);
		ConfigurationProperties.properties.setProperty("regressionforfaultlocalization", "true");
		ConfigurationProperties.properties.setProperty("population", "1");

	}

	@Override
	public void loadExtensionPoints() throws Exception {

		super.loadExtensionPoints();
		this.setOperatorSpace(new JKaliSpace());
	}

}
