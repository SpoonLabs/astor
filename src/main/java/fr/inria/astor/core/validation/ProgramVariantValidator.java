package fr.inria.astor.core.validation;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.VariantValidationResult;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.extension.AstorExtensionPoint;
import fr.inria.astor.core.stats.Stats;

/**
 * 
 * @author Matias Martinez
 *
 */
public abstract class ProgramVariantValidator implements  AstorExtensionPoint {

	protected Stats currentStats = Stats.getCurrentStat();

	public void setStats(Stats stats) {
		currentStats = stats;
	};

	public abstract VariantValidationResult validate(ProgramVariant variant, ProjectRepairFacade projectFacade);

}
