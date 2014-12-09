package fr.inria.main.mutation;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import spoon.processing.ProcessingManager;
import spoon.reflect.code.CtIf;
import spoon.reflect.declaration.CtElement;
import spoon.reflect.declaration.CtPackage;
import spoon.reflect.factory.FactoryImpl;
import spoon.support.QueueProcessingManager;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.core.loop.evolutionary.MutationalRepair;
import fr.inria.astor.core.loop.evolutionary.population.FitnessPopulationController;
import fr.inria.astor.core.loop.evolutionary.population.ProgramValidatorSpoonRegression;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.UniformRandomRepairOperatorSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.IFCollectorProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.IFExpressionFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.LoopExpressionFixSpaceProcessor;
import fr.inria.astor.core.loop.mutation.muttest.MutTestingMutantGenerator;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.MutationSupporter;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.stats.Stats;
import fr.inria.main.AbstractMain;
/**
 * 
 * @author Matias Martinez
 *
 */
public class Main extends AbstractMain {

	
	private void setUpFilters(MutTestingMutantGenerator gploop) throws JSAPException {

		List<AbstractFixSpaceProcessor> proc = new ArrayList<AbstractFixSpaceProcessor>();
		proc.add(new LoopExpressionFixSpaceProcessor());
		proc.add(new IFExpressionFixSpaceProcessor());
		gploop.setRepairSpace(new UniformRandomRepairOperatorSpace());
		gploop.setPopulationControler(new FitnessPopulationController());
	}
	
	public void run(
			String location, 
			String projectName, 
			String dependencies, 
			Stats currentStat, 
			String packageToMine)
			throws Exception {

		String method = this.getClass().getSimpleName();

		rep = getProject(location, projectName, method, null/* failing */, null /* regression */,
				dependencies, false);
		rep.getProperties().setExperimentName(method);

		this.mutateIfConditions(rep, packageToMine, currentStat);

	}

	private void mutateIfConditions(ProjectRepairFacade repCreated, String packageToMine, Stats currentStat) throws Exception {

		rep = repCreated;
		//
		rep.init(MutationSupporter.DEFAULT_ORIGINAL_VARIANT);

		MutationalRepair mutOriginal = new MutationalRepair(mutSupporter, rep);
		MutTestingMutantGenerator mutEngine = new MutTestingMutantGenerator(mutSupporter, rep);
		mutEngine.mutEngine = mutOriginal;
		mutEngine.setCurrentStat(currentStat);
		setUpFilters(mutEngine);

		mutEngine.setProgramVariantValidator(new ProgramValidatorSpoonRegression());

		int maxif = Integer.valueOf(ConfigurationProperties.getProperty("elementsToMutate"));

		try {
			mutEngine.initModel();

			CtPackage pqk = mutEngine.getMutatorSupporter().getFactory().Package().get(packageToMine);

			List<CtElement> ifs = getIfToMutate(pqk);

			for (CtElement ctElement : ifs) {
				System.out.println("if " + ((CtIf) ctElement).getCondition() + " in  "
						+ ctElement.getPosition().getCompilationUnit().getFile());
			}

			System.out.println("Number of if: " + ifs.size());
			List<CtElement> shuffleIf = new ArrayList<>(ifs);
			Collections.shuffle(shuffleIf);
			mutEngine.start(shuffleIf.subList(0, (shuffleIf.size() > maxif) ? maxif : shuffleIf.size()), false);
		
			System.out.println("Mutants generated at "+repCreated.getProperties().getOutDir());
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}
	
	public List<CtElement> getIfToMutate(CtElement element) {

		AbstractFixSpaceProcessor.spaceElements.clear();

		ProcessingManager processing = new QueueProcessingManager(FactoryImpl.getLauchingFactory());
		processing.addProcessor(IFCollectorProcessor.class.getName());
		processing.process(element);

		return new ArrayList(AbstractFixSpaceProcessor.spaceElements);

	}

	@Override
	public void run(
			String location,
			String projectName, String dependencies, Stats currentStat,
			 String packageToMine, double thfl, String failing)
			throws Exception {

		throw new IllegalArgumentException("Functionality not available");
	}
}
