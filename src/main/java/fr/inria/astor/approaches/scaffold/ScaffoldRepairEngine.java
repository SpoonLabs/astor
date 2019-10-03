package fr.inria.astor.approaches.scaffold;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.scaffold.scaffoldgeneration.ConditionAddTransform;
import fr.inria.astor.approaches.scaffold.scaffoldgeneration.ConditionMutationTransform;
import fr.inria.astor.approaches.scaffold.scaffoldgeneration.ConditionRemoveTransform;
import fr.inria.astor.approaches.scaffold.scaffoldgeneration.ExpressionTransform;
import fr.inria.astor.approaches.scaffold.scaffoldgeneration.OperatorTransform;
import fr.inria.astor.approaches.scaffold.scaffoldgeneration.OverloadMethodTransform;
import fr.inria.astor.approaches.scaffold.scaffoldgeneration.TransformStrategy;
import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.ScaffoldSynthesisEntry;
import fr.inria.astor.approaches.scaffold.scaffoldsynthesis.executor.ScaffoldExecutor;
import fr.inria.astor.core.entities.ModificationPoint;
import fr.inria.astor.core.entities.ProgramVariant;
import fr.inria.astor.core.entities.validation.VariantValidationResult;
import fr.inria.astor.core.faultlocalization.entity.SuspiciousCode;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.bytecode.entities.CompilationResult;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.AstorCoreEngine;
import fr.inria.main.evolution.ExtensionPoints;

public class ScaffoldRepairEngine extends AstorCoreEngine {

	public ScaffoldRepairEngine(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade)
			throws JSAPException {
		super(mutatorExecutor, projFacade);
		ConfigurationProperties.setProperty(ExtensionPoints.SUSPICIOUS_NAVIGATION.identifier, "inorder");
		ConfigurationProperties.setProperty("skipfitnessinitialpopulation", "true");
		ConfigurationProperties.setProperty("preservelinenumbers", "false");
	}
	
	@Override
	public void atEnd() {
		// to add
	}
	
	public boolean synthesizeSketch(String sketchtype, ModificationPoint changepoint) throws Exception {
		ProgramVariant varianttoexplore=createVariant();
		URL[] originalURL = projectFacade.getClassPathURLforProgramVariant(ProgramVariant.DEFAULT_ORIGINAL_VARIANT);

		CompilationResult compilation = compiler.compile(varianttoexplore, originalURL);
//		List<String> errorinfo=compilation.getErrorList();
//		for(int number=0;number<errorinfo.size();number++)
//			System.out.println(errorinfo.get(number));

		boolean childCompiles = compilation.compiles();
		varianttoexplore.setCompilation(compilation);

		if (childCompiles) {
			log.debug("-The created sketch compiles: SketchType " + sketchtype+" ModificationPoint "
			+changepoint.toString());
			
			do {
				ScaffoldSynthesisEntry.reset();
				try {
					VariantValidationResult validationResult = validateInstance(varianttoexplore); 

					if (validationResult.isSuccessful() ) {
						log.info("Found patch:  " + ScaffoldSynthesisEntry.getString());
						return true;
					} 
				} catch (Exception e) {}
			} while (ScaffoldExecutor.incrementCounter());
		} else {
			log.debug("-The created sketch compiles: SketchType " + sketchtype+" ModificationPoint "
					+changepoint.toString() + ", errors: "
					+ compilation.getErrorList());
		}
		
		return false;
	}
	
	public ProgramVariant createVariant() {
		return this.variantFactory.createProgramVariantFromAnother(this.originalVariant, 0);
	}

	@Override
	public void startEvolution() throws Exception {
		
	}
	
	@Override
	public void loadExtensionPoints() throws Exception {
		super.loadFaultLocalization();
		super.loadTargetElements();
		super.loadSuspiciousNavigation();
		super.loadCompiler();
		super.loadValidator();
	}
	
	@Override
	public void initPopulation(List<SuspiciousCode> suspicious) throws Exception {

		this.mutatorSupporter.getFactory().getEnvironment().setCommentEnabled(true);
		
		super.initPopulation(suspicious);

		log.info("\n---- Scaffold Generation Start");
		
	//	scaffoldGenerationAll();
		
		log.info("\n---- Scaffold Generation End");
	}
	
	public List<String> scaffoldGenerationAll() {
		
		List<ModificationPoint> modificationPointsToProcess = this.suspiciousNavigationStrategy
				.getSortedModificationPointsList(this.originalVariant.getModificationPoints());
		
		List<TransformStrategy> transformer = new ArrayList<TransformStrategy>();
		
		List<String> files = new ArrayList<String>();
		int modificationPointIndex=0;
		
		for (ModificationPoint modificationPoint : modificationPointsToProcess) {

			log.debug("\n--- start analyzing modificationPoint position: " + modificationPoint.identified);

			modificationPointIndex+=1;
			transformer.add(new OverloadMethodTransform(modificationPoint, modificationPointIndex, this.mutatorSupporter, this.projectFacade, this));
			transformer.add(new ConditionRemoveTransform(modificationPoint, modificationPointIndex, this.mutatorSupporter, this.projectFacade, this));
			transformer.add(new ConditionMutationTransform(modificationPoint, modificationPointIndex, this.mutatorSupporter, this.projectFacade, this));
			transformer.add(new ConditionAddTransform(modificationPoint, modificationPointIndex, this.mutatorSupporter, this.projectFacade, this));
			transformer.add(new OperatorTransform(modificationPoint, modificationPointIndex, this.mutatorSupporter, this.projectFacade, this));
			transformer.add(new ExpressionTransform(modificationPoint, modificationPointIndex, this.mutatorSupporter, this.projectFacade, this));

			for (TransformStrategy strategy : transformer) {
				files.addAll(strategy.transform());
			}
			
			transformer.clear();
			log.debug("\n--- finish analyzing modificationPoint position: " + modificationPoint.identified);
		}	
		
		return files;
	}
	
    public List<String> scaffoldGenerationSpecific(String strategyName) {
		
		List<ModificationPoint> modificationPointsToProcess = this.suspiciousNavigationStrategy
				.getSortedModificationPointsList(this.originalVariant.getModificationPoints());
				
		List<String> files = new ArrayList<String>();
		int modificationPointIndex=0;
		
		for (ModificationPoint modificationPoint : modificationPointsToProcess) {

			log.debug("\n--- start analyzing modificationPoint position: " + modificationPoint.identified);

			modificationPointIndex+=1;

		    files.addAll(scaffoldGenerationSpecific(strategyName,modificationPoint,modificationPointIndex));
			
			log.debug("\n--- finish analyzing modificationPoint position: " + modificationPoint.identified);
		}	
		
		return files;
	}
    
    public List<String> scaffoldGenerationSpecific(String strategyName, ModificationPoint specificModificationPoint, int index) {
		
		TransformStrategy transformer = null;
		
		List<String> files = new ArrayList<String>();
			
		if(strategyName.equals("OverloadMethodTransform"))
			transformer=new OverloadMethodTransform(specificModificationPoint, index, this.mutatorSupporter, this.projectFacade, this);
		else if(strategyName.equals("ConditionRemoveTransform"))
			transformer=new ConditionRemoveTransform(specificModificationPoint, index, this.mutatorSupporter, this.projectFacade, this);
		else if(strategyName.equals("ConditionMutationTransform"))
			transformer=new ConditionMutationTransform(specificModificationPoint, index, this.mutatorSupporter, this.projectFacade, this);
		else if(strategyName.equals("ConditionAddTransform"))
			transformer=new ConditionAddTransform(specificModificationPoint, index, this.mutatorSupporter, this.projectFacade, this);
	    else if(strategyName.equals("OperatorTransform"))
			transformer=new OperatorTransform(specificModificationPoint, index, this.mutatorSupporter, this.projectFacade, this);
	    else if(strategyName.equals("ExpressionTransform"))
			transformer=new ExpressionTransform(specificModificationPoint, index, this.mutatorSupporter, this.projectFacade, this);

		files.addAll(transformer.transform());
						
		return files;
	}
    
    public ProgramVariant getVariant() {
		return this.originalVariant;
	}
}

