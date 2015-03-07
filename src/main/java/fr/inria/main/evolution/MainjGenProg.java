package fr.inria.main.evolution;

import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.ParseException;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.loop.evolutionary.JGenProg;
import fr.inria.astor.core.loop.evolutionary.population.FitnessPopulationController;
import fr.inria.astor.core.loop.evolutionary.population.ProgramVariantFactory;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.UniformRandomRepairOperatorSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.SingleStatementFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.ingredients.BasicFixSpace;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.validation.validators.ProcessValidator;
import fr.inria.main.AbstractMain;

/**
 * Main for full version of jGenProg
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 *
 */
public class MainjGenProg extends AbstractMain {

	@Override
	public void run(String location, String projectName, String dependencies, String packageToMine) throws Exception {

	}

	public void initProject(String location, String projectName, String dependencies, String packageToInstrument,
			double thfl, String failing) throws Exception {

		List<String> failingList = Arrays.asList(new String[] { failing });
		String method = this.getClass().getSimpleName();
		rep = getProject(location, projectName, method, failing, failingList, dependencies, true);
		rep.getProperties().setExperimentName(this.getClass().getSimpleName());

		rep.init(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);

	}

	public JGenProg statementMode() throws Exception {

		MutationSupporter mutSupporter = new MutationSupporter(getFactory());
		JGenProg gploop = new JGenProg(mutSupporter, rep);

		// Fix Space
		List<AbstractFixSpaceProcessor<?>> ingredientProcessors = new ArrayList<AbstractFixSpaceProcessor<?>>();
		ingredientProcessors.add(new SingleStatementFixSpaceProcessor());
		// ingredientProcessors.add(new LoopExpressionFixSpaceProcessor());
		// ingredientProcessors.add(new IFExpressionFixSpaceProcessor());
		// ingredientProcessors.add(new MethodInvocationFixSpaceProcessor());

		// We analyze
		gploop.setVariantFactory(new ProgramVariantFactory(ingredientProcessors));
		// --

		// The ingredients for build the patches
		gploop.setFixspace(new BasicFixSpace(ingredientProcessors));
		// ---

		// Repair Space
		gploop.setRepairActionSpace(new UniformRandomRepairOperatorSpace());

		// Pop controller
		gploop.setPopulationControler(new FitnessPopulationController());

		gploop.setProgramValidator(new ProcessValidator());

		// Suspicious
		List<SuspiciousCode> candidates = rep.getSuspicious(ConfigurationProperties.getProperty("packageToInstrument"),
				ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
		List<SuspiciousCode> filtercandidates = new ArrayList<SuspiciousCode>();

		for (SuspiciousCode suspiciousCode : candidates) {
			if (!suspiciousCode.getClassName().endsWith("Exception")) {
				filtercandidates.add(suspiciousCode);
			}
		}

		if(candidates == null || candidates.isEmpty())
			 throw new IllegalArgumentException("No suspicious gen for analyze");
		gploop.setup(filtercandidates);

		return gploop;
	}

	@Override
	public void run(String location, String projectName, String dependencies, String packageToInstrument, double thfl,
			String failing) throws Exception {

		initProject(location, projectName, dependencies, packageToInstrument, thfl, failing);
		JGenProg gploop = statementMode();

		try {
			gploop.start();
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

		MainjGenProg m = new MainjGenProg();
		
		boolean correct = m.processArguments(args);
		if(!correct){
			return;
		}
		
		boolean isExample = m.executeExample(args);
		if (isExample)
			return;

		String dependencies = ConfigurationProperties.getProperty("dependenciespath");
		String failing = ConfigurationProperties.getProperty("failing");
		String location = ConfigurationProperties.getProperty("location");
		String packageToInstrument = ConfigurationProperties.getProperty("packageToInstrument");
		double thfl = ConfigurationProperties.getPropertyDouble("flthreshold");
		String projectName = ConfigurationProperties.getProperty("projectIdentifier");

		m.run(location, projectName, dependencies, packageToInstrument, thfl, failing);

	}

}
