package fr.inria.astor.core.validation;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.ProgramVariantValidationResult;
import fr.inria.astor.core.setup.ProjectRepairFacade;

public interface IProgramValidator {

		public ProgramVariantValidationResult validate(ProgramVariant variant,  ProjectRepairFacade  projectFacade) ;

}
