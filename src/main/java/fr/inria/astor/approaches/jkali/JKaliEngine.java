package fr.inria.astor.approaches.jkali;

import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;
import fr.inria.astor.approaches.jkali.JKaliSpace;
import fr.inria.astor.core.loop.ExhaustiveSearchEngine;
import fr.inria.astor.core.loop.population.ProgramVariantFactory;
import fr.inria.astor.core.loop.spaces.operators.OperatorSpace;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.AbstractFixSpaceProcessor;
import fr.inria.astor.core.manipulation.filters.SingleStatementFixSpaceProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.main.evolution.PlugInLoader;

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
		
		OperatorSpace operatorSpaceCustom = PlugInLoader.loadOperatorSpace();
		if(operatorSpaceCustom == null)
			operatorSpaceCustom = new JKaliSpace();
		this.setOperatorSpace(operatorSpaceCustom);
		
		List<AbstractFixSpaceProcessor<?>> ingredientProcessors = new ArrayList<AbstractFixSpaceProcessor<?>>();
		ingredientProcessors.add(new SingleStatementFixSpaceProcessor());
		this.setVariantFactory(new ProgramVariantFactory(ingredientProcessors));
		
	}

}
