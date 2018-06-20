package fr.inria.astor.approaches.jkali;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.ExhaustiveSearchEngine;
import fr.inria.main.evolution.ExtensionPoints;

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
		setPropertyIfNotDefined(ExtensionPoints.OPERATORS_SPACE.identifier, "suppression");
		setPropertyIfNotDefined(ExtensionPoints.TARGET_CODE_PROCESSOR.identifier, "statements");
	}


}
