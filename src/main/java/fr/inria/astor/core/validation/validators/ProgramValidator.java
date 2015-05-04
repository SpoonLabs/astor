package fr.inria.astor.core.validation.validators;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.ProgramVariantValidationResult;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.stats.Stats;

/**
 * 
 * @author Matias Martinez
 *
 */
public abstract class ProgramValidator {

	Stats currentStats = null;

	public void setStats(Stats stats) {
		currentStats = stats;
	};

	public abstract ProgramVariantValidationResult validate(ProgramVariant variant, ProjectRepairFacade projectFacade);

}
