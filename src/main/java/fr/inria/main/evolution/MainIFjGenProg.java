package fr.inria.main.evolution;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.ParseException;

import spoon.reflect.factory.Factory;
import spoon.reflect.factory.FactoryImpl;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.loop.evolutionary.JGenProgIfExpression;
import fr.inria.astor.core.loop.evolutionary.population.FitnessPopulationController;
import fr.inria.astor.core.loop.evolutionary.population.ProgramVariantFactory;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.UniformRandomRepairOperatorSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.IFConditionFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.IFExpressionFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.LoopExpressionFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.ingredients.LocalFixSpace;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.validation.validators.ProcessValidator;
import fr.inria.main.AbstractMain;

/**
 * Main for version of jGenProg that repairs If conditions
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class MainIFjGenProg extends AbstractMain {

	@Override
	public void run(String location, String projectName, String dependencies, String packageToMine) throws Exception {

	}

	public MainIFjGenProg() {
	}

	@Override
	public void run(String location, String projectName, String dependencies, String packageToInstrument, double thfl,
			String failing) throws Exception {

		String[] failTestS = failing.split(File.pathSeparator);
		
		List<String> failingList = Arrays.asList(failTestS);
				
		String method = this.getClass().getSimpleName();
		projectFacade = getProject(location, projectName, method, failingList, dependencies, true);
		projectFacade.getProperties().setExperimentName(this.getClass().getSimpleName());

		projectFacade.setupTempDirectories(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);

		MutationSupporter mutSupporter = new MutationSupporter(getFactory());
		JGenProgIfExpression gploop = new JGenProgIfExpression(mutSupporter, projectFacade);

		// This processor collects the If for creating a population of
		// suspicious ifs.
		List<AbstractFixSpaceProcessor<?>> procCondition = new ArrayList<AbstractFixSpaceProcessor<?>>();
		procCondition.add(new IFConditionFixSpaceProcessor());
		gploop.setVariantFactory(new ProgramVariantFactory(procCondition));

		// Now, we focus on the fix space i.e. ingredients for the fix.
		List<AbstractFixSpaceProcessor<?>> procFix = new ArrayList<AbstractFixSpaceProcessor<?>>();
		procFix.add(new LoopExpressionFixSpaceProcessor());
		procFix.add(new IFExpressionFixSpaceProcessor());
		gploop.setFixspace(new LocalFixSpace(procFix));

		//
		gploop.setRepairActionSpace(new UniformRandomRepairOperatorSpace());
		gploop.setPopulationControler(new FitnessPopulationController());

		gploop.setProgramValidator(new ProcessValidator());

		List<SuspiciousCode> candidates = projectFacade.getSuspicious(packageToInstrument,
				ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
		List<SuspiciousCode> filtercandidates = new ArrayList<SuspiciousCode>();

		for (SuspiciousCode suspiciousCode : candidates) {
			if (!suspiciousCode.getClassName().endsWith("Exception")) {
				filtercandidates.add(suspiciousCode);
			}
		}
	
		assertNotNull(candidates);
		assertTrue(candidates.size() > 0);
		try {
			gploop.initPopulation(filtercandidates);
		} catch (Exception e) {
			e.printStackTrace();
			fail(e.getMessage());
		}

	}

	/**
	 * @param args
	 * @throws Exception
	 * @throws ParseException
	 */
	public static void main(String[] args) throws Exception {
		MainIFjGenProg m = new MainIFjGenProg();
		m.processArguments(args);

	}

}
