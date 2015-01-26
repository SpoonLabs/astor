package fr.inria.main.evolution;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

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
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.UniformRandomFixSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.IFConditionFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.IFExpressionFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.LoopExpressionFixSpaceProcessor;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.TransformationProperties;
import fr.inria.astor.core.stats.Stats;
import fr.inria.main.AbstractMain;
/**
 *   Main for version of jGenProg that repairs If conditions
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class MainIFjGenProg extends AbstractMain {

	@Override
	public void run(String location, String projectName, String dependencies, Stats currentStat, String packageToMine)
			throws Exception {
	

	}

	
	public  MainIFjGenProg(){
	}	
	
		
	@Override
	public void run(String location, String projectName, String dependencies, Stats currentStat, 
			String packageToInstrument,
			double thfl, String failing) throws Exception {
		System.out.println(System.getProperty("java.class.path"));
		if(thfl>0)
			TransformationProperties.THRESHOLD_SUSPECTNESS = thfl;
		
		List<String> failingList = Arrays.asList(new String[] { failing });
		String method = this.getClass().getSimpleName();
		rep = getProject(location, projectName,method , failing, failingList,dependencies,true);
		rep.getProperties().setExperimentName(this.getClass().getSimpleName());
				
		rep.init(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
		Factory facade = FactoryImpl.getLauchingFactory();
		if(facade == null){
			facade = rep.createFactory();
			FactoryImpl.getLauchingFactory().getEnvironment().setDebug(true);
		}
		MutationSupporter mutSupporter = new MutationSupporter(facade);
		JGenProgIfExpression gploop = new JGenProgIfExpression(mutSupporter,rep);
		gploop.setCurrentStat(currentStat);
				
		List<AbstractFixSpaceProcessor> procCondition = new ArrayList<AbstractFixSpaceProcessor>();
		procCondition.add(new IFConditionFixSpaceProcessor());
		//
		gploop.setVariantFactory(new ProgramVariantFactory(procCondition));
		
		List<AbstractFixSpaceProcessor> procFix = new ArrayList<AbstractFixSpaceProcessor>();
		procFix.add(new LoopExpressionFixSpaceProcessor());
		procFix.add(new IFExpressionFixSpaceProcessor());
		
		gploop.setFixspace(new UniformRandomFixSpace(procFix));
		gploop.setRepairSpace(new UniformRandomRepairOperatorSpace());
		gploop.setPopulationControler(new FitnessPopulationController());
		
		List<SuspiciousCode> candidates = rep.getSuspicious(
				packageToInstrument,
				ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
		List<SuspiciousCode> filtercandidates = new ArrayList<SuspiciousCode>();

		for (SuspiciousCode suspiciousCode : candidates) {
			if(!suspiciousCode.getClassName().endsWith("Exception") 
								){
				filtercandidates.add(suspiciousCode);
			}
		}
		currentStat.fl_size = filtercandidates.size();
		currentStat.fl_threshold = TransformationProperties.THRESHOLD_SUSPECTNESS ;
		
		assertNotNull(candidates);
  		assertTrue(candidates.size() > 0);
		try {
			gploop.start(filtercandidates);
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
			MainIFjGenProg m = new MainIFjGenProg();
			m.execute(args);	
		
		
	}
	



}
