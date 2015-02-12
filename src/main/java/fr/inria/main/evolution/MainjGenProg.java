package fr.inria.main.evolution;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.loop.evolutionary.JGenProg;
import fr.inria.astor.core.loop.evolutionary.population.FitnessPopulationController;
import fr.inria.astor.core.loop.evolutionary.population.ProgramVariantFactory;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.UniformRandomRepairOperatorSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.BasicFixSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.UniformRandomFixSpace;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.AbstractFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.IFExpressionFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.LoopExpressionFixSpaceProcessor;
import fr.inria.astor.core.loop.evolutionary.spaces.implementation.spoon.processor.SingleStatementFixSpaceProcessor;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.setup.TransformationProperties;
import fr.inria.astor.core.validation.validators.ProcessValidator;
import fr.inria.main.AbstractMain;

/**
 * Main for full version of jGenProg
 * @author Matias Martinez,  matias.martinez@inria.fr
 *
 */
public class MainjGenProg extends AbstractMain{
	



@Override
public void run(String location, String projectName, String dependencies,  String packageToMine)
		throws Exception {
	
	
}

@Override
public void run(String location, String projectName, String dependencies, String packageToInstrument,
		double thfl, String failing) throws Exception {
	
	//System.out.println(System.getProperty("java.class.path"));
	if(thfl>0)
		TransformationProperties.THRESHOLD_SUSPECTNESS = thfl;
	
	List<String> failingList = Arrays.asList(new String[] { failing });
	String method = this.getClass().getSimpleName();
	rep = getProject(location, projectName,method , failing, failingList,dependencies,true);
	rep.getProperties().setExperimentName(this.getClass().getSimpleName());
			
	rep.init(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);
	
	
	MutationSupporter mutSupporter = new MutationSupporter(getFactory());
	JGenProg gploop = new JGenProg(mutSupporter,rep);

	
	//Fix Space
	List<AbstractFixSpaceProcessor<?>> ingredientProcessors = new ArrayList<AbstractFixSpaceProcessor<?>>();
	ingredientProcessors.add(new SingleStatementFixSpaceProcessor());
	ingredientProcessors.add(new LoopExpressionFixSpaceProcessor());
	ingredientProcessors.add(new IFExpressionFixSpaceProcessor());
	
	//We analyze 
	gploop.setVariantFactory(new ProgramVariantFactory(ingredientProcessors));
	//--
	
	//The ingredients for build the patches
	gploop.setFixspace(new BasicFixSpace(ingredientProcessors));
	//---
	
	//Repair Space
	gploop.setRepairActionSpace(new UniformRandomRepairOperatorSpace());
	
	//Pop controller
	gploop.setPopulationControler(new FitnessPopulationController());
	
	gploop.setProgramValidator(new ProcessValidator());
	
	//Suspicious
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
	
	assertNotNull(candidates);
	assertTrue(candidates.size() > 0);
	try {
		gploop.start(filtercandidates);
	} catch (Exception e) {
		e.printStackTrace();
		fail(e.getMessage());
	}
}

}
