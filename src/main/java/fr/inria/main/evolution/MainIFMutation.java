package fr.inria.main.evolution;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.cli.ParseException;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.faultlocalization.SuspiciousCode;
import fr.inria.astor.core.loop.evolutionary.MutationalRepair;
import fr.inria.astor.core.loop.evolutionary.population.FitnessPopulationController;
import fr.inria.astor.core.loop.evolutionary.population.ProgramValidatorSpoonRegression;
import fr.inria.astor.core.loop.evolutionary.population.ProgramVariantFactory;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.UniformRandomRepairOperatorSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.UniformRandomFixSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.IFConditionFixSpaceProcessor;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.TransformationProperties;
import fr.inria.astor.core.stats.Stats;
import fr.inria.main.AbstractMain;
/**
 *   Main for version of Mutation Repair that repairs If conditions
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class MainIFMutation extends AbstractMain {
	static List<SuspiciousCode> filtercandidates = null;
	@Override
	public void run(String location, String projectName, String dependencies, Stats currentStat, String packageToMine)
			throws Exception {
		throw new IllegalArgumentException("Functionality not provided");

	}

	@Override
	public void run(String location, 
			String projectName, 
			String dependencies, 
			Stats currentStat, 
			String packageToMine,
			double thfl, String failing) throws Exception {

			String method = this.getClass().getSimpleName();
			
			if(thfl>0)
				TransformationProperties.THRESHOLD_SUSPECTNESS = thfl;
				
			List<String> failingList = Arrays.asList(new String[] { failing });
			
			
			rep = getProject(location, projectName,method, failing, failingList,dependencies,false);
			rep.getProperties().setExperimentName(this.getClass().getSimpleName());
			
			rep.init(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
			MutationSupporter mutSupporter = new MutationSupporter(getFactory());
			
			MutationalRepair mutloop = new MutationalRepair(mutSupporter,rep);
			mutloop.setCurrentStat(currentStat);
			mutloop.setFixspace(new UniformRandomFixSpace());
			
			mutloop.setRepairSpace(new UniformRandomRepairOperatorSpace());
			mutloop.setPopulationControler(new FitnessPopulationController());
			
			List<AbstractFixSpaceProcessor> suspiciousProcessor = new ArrayList<AbstractFixSpaceProcessor>();
			suspiciousProcessor.add(new IFConditionFixSpaceProcessor());
			mutloop.setVariantFactory(new ProgramVariantFactory(suspiciousProcessor));
			
			
		/*	List<AbstractFixSpaceProcessor> ctElementProcessors = new ArrayList<AbstractFixSpaceProcessor>();
			ctElementProcessors.add(new LoopExpressionFixSpaceProcessor());
			ctElementProcessors.add(new IFExpressionFixSpaceProcessor());*/
			
			mutloop.setProgramVariantValidator(new ProgramValidatorSpoonRegression());
			//
			
			try {
				if(filtercandidates == null){
				List<SuspiciousCode> candidates = rep.getSuspicious(
						rep.getProperties().getPackageToInstrument()
						,ProgramVariant.DEFAULT_ORIGINAL_VARIANT);

				
				assertNotNull(candidates);
		  		assertTrue("No candidates: ",candidates.size() > 0);
				
				filtercandidates = new ArrayList<SuspiciousCode>();
				for (SuspiciousCode suspiciousCode : candidates) {
					if(!suspiciousCode.getClassName().endsWith("Exception")
							&& !suspiciousCode.getClassName().endsWith("Test")
							&& !suspiciousCode.getClassName().endsWith("TestCase")
							
							){
						filtercandidates.add(suspiciousCode);
					}
				}
				currentStat.fl_size = filtercandidates.size();
				currentStat.fl_threshold = TransformationProperties.THRESHOLD_SUSPECTNESS ;
		}
				mutloop.start(filtercandidates);
				
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
	public static void main(String[] args) throws Exception  {
		
			
			MainIFMutation m = new MainIFMutation();
			m.execute(args);	
		
		
	}

}
