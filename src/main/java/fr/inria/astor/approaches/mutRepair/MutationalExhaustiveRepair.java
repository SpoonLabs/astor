package fr.inria.astor.approaches.mutRepair;

import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.loop.ExhaustiveSearchEngine;
import fr.inria.astor.core.loop.population.ProgramVariantFactory;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.AbstractFixSpaceProcessor;
import fr.inria.astor.core.manipulation.filters.IFConditionFixSpaceProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;

/**
 * 
 * @author Matias Martinez
 *
 */
public class MutationalExhaustiveRepair extends ExhaustiveSearchEngine{

	public MutationalExhaustiveRepair(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade)
			throws JSAPException {
		super(mutatorExecutor, projFacade);
		ConfigurationProperties.properties.setProperty("regressionforfaultlocalization", "true");
		ConfigurationProperties.properties.setProperty("population", "1");
	
	}

	@Override
	public void loadExtensionPoints() throws Exception {
			super.loadExtensionPoints();
			
			setOperatorSpace(new MutRepairSpace());
			List<AbstractFixSpaceProcessor<?>> ingredientProcessors = new ArrayList<AbstractFixSpaceProcessor<?>>();
			ingredientProcessors.add(new IFConditionFixSpaceProcessor());
			setVariantFactory(new ProgramVariantFactory(ingredientProcessors));

	}
	
	

}
