package fr.inria.astor.approaches.cardumen;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.jgenprog.JGenProg;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.main.evolution.ExtensionPoints;

/**
 * 
 * @author Matias Martinez
 *
 */
public class CardumenApproach extends JGenProg {

	public CardumenApproach(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade) throws JSAPException {
		super(mutatorExecutor, projFacade);

		ConfigurationProperties.setProperty("cleantemplates", "true");
		setPropertyIfNotDefined(ExtensionPoints.INGREDIENT_PROCESSOR.identifier, "expression");

		setPropertyIfNotDefined(ExtensionPoints.OPERATORS_SPACE.identifier, "r-expression");
		setPropertyIfNotDefined(ExtensionPoints.INGREDIENT_SEARCH_STRATEGY.identifier,
				"name-probability-based");

	}

}
