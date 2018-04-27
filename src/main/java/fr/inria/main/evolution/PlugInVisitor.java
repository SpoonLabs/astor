package fr.inria.main.evolution;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import fr.inria.astor.approaches.cardumen.CardumenOperatorSpace;
import fr.inria.astor.approaches.extensions.minimpact.validator.ProcessEvoSuiteValidator;
import fr.inria.astor.approaches.jgenprog.jGenProgSpace;
import fr.inria.astor.approaches.jkali.JKaliSpace;
import fr.inria.astor.approaches.jmutrepair.MutRepairSpace;
import fr.inria.astor.core.faultlocalization.FaultLocalizationStrategy;
import fr.inria.astor.core.faultlocalization.cocospoon.CocoFaultLocalization;
import fr.inria.astor.core.faultlocalization.gzoltar.GZoltarFaultLocalization;
import fr.inria.astor.core.manipulation.filters.ExpressionIngredientSpaceProcessor;
import fr.inria.astor.core.manipulation.filters.IFConditionFixSpaceProcessor;
import fr.inria.astor.core.manipulation.filters.IFExpressionFixSpaceProcessor;
import fr.inria.astor.core.manipulation.filters.SingleStatementFixSpaceProcessor;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.output.PatchJSONStandarOutput;
import fr.inria.astor.core.output.ReportResults;
import fr.inria.astor.core.output.StandardOutputReport;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.solutionsearch.AstorCoreEngine;
import fr.inria.astor.core.solutionsearch.extension.SolutionVariantSortCriterion;
import fr.inria.astor.core.solutionsearch.extension.VariantCompiler;
import fr.inria.astor.core.solutionsearch.navigation.InOrderSuspiciousNavigation;
import fr.inria.astor.core.solutionsearch.navigation.SequenceSuspiciousNavigationStrategy;
import fr.inria.astor.core.solutionsearch.navigation.SuspiciousNavigationStrategy;
import fr.inria.astor.core.solutionsearch.navigation.SuspiciousNavigationValues;
import fr.inria.astor.core.solutionsearch.navigation.UniformRandomSuspiciousNavigation;
import fr.inria.astor.core.solutionsearch.navigation.WeightRandomSuspiciousNavitation;
import fr.inria.astor.core.solutionsearch.population.FitnessFunction;
import fr.inria.astor.core.solutionsearch.population.PopulationController;
import fr.inria.astor.core.solutionsearch.population.ProgramVariantFactory;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.core.solutionsearch.spaces.operators.OperatorSelectionStrategy;
import fr.inria.astor.core.solutionsearch.spaces.operators.OperatorSpace;
import fr.inria.astor.core.solutionsearch.spaces.operators.UniformRandomRepairOperatorSpace;
import fr.inria.astor.core.solutionsearch.spaces.operators.WeightedRandomOperatorSelection;
import fr.inria.astor.core.validation.ProgramVariantValidator;
import fr.inria.astor.core.validation.processbased.ProcessValidator;

/**
 * 
 * @author Matias Martinez
 *
 */
public class PlugInVisitor {

	protected static Logger log = Logger.getLogger(PlugInLoader.class);

	protected void loadFaultLocalization(AstorCoreEngine approach) throws Exception {

		// Fault localization
		String flvalue = ConfigurationProperties.getProperty("faultlocalization").toLowerCase();
		if (flvalue.equals("gzoltar")) {
			approach.setFaultLocalization(new GZoltarFaultLocalization());
		} else if (flvalue.equals("cocospoon")) {
			approach.setFaultLocalization(new CocoFaultLocalization());
		} else
			approach.setFaultLocalization(
					(FaultLocalizationStrategy) PlugInLoader.loadPlugin(ExtensionPoints.FAULT_LOCALIZATION));

	}

	protected void loadSuspiciousNavigation(AstorCoreEngine approach) throws Exception {
		String mode = ConfigurationProperties.getProperty(ExtensionPoints.SUSPICIOUS_NAVIGATION.identifier)
				.toUpperCase();

		SuspiciousNavigationStrategy suspiciousNavigationStrategy = null;
		if (SuspiciousNavigationValues.INORDER.toString().equals(mode)) {
			suspiciousNavigationStrategy = new InOrderSuspiciousNavigation();
		} else if (SuspiciousNavigationValues.WEIGHT.toString().equals(mode))
			suspiciousNavigationStrategy = new WeightRandomSuspiciousNavitation();
		else if (SuspiciousNavigationValues.RANDOM.toString().equals(mode)) {
			suspiciousNavigationStrategy = new UniformRandomSuspiciousNavigation();
		} else if (SuspiciousNavigationValues.SEQUENCE.toString().equals(mode)) {
			suspiciousNavigationStrategy = new SequenceSuspiciousNavigationStrategy();
		} else {
			suspiciousNavigationStrategy = (SuspiciousNavigationStrategy) PlugInLoader
					.loadPlugin(ExtensionPoints.SUSPICIOUS_NAVIGATION);
		}
		approach.setSuspiciousNavigationStrategy(suspiciousNavigationStrategy);
	}

	protected void loadOperatorSpaceDefinition(AstorCoreEngine approach) throws Exception {

		// We check if the user defines the operators to include in the operator
		// space
		OperatorSpace operatorSpace = null;
		String customOp = ConfigurationProperties.getProperty("customop");
		if (customOp != null && !customOp.isEmpty()) {
			operatorSpace = createCustomOperatorSpace(customOp);
		} else {
			customOp = ConfigurationProperties.getProperty("operatorspace");
			if ("irr-statements".equals(customOp) || "jgenprogspace".equals(customOp)) {
				operatorSpace = new jGenProgSpace();
			} else if ("relational-Logical-op".equals(customOp) || "mutationspace".equals(customOp)) {
				operatorSpace = new MutRepairSpace();
			} else if ("suppression".equals(customOp) || "jkalispace".equals(customOp)) {
				operatorSpace = new JKaliSpace();
			} else if ("r-expression".equals(customOp) || "cardumenspace".equals(customOp)) {
				operatorSpace = new CardumenOperatorSpace();
			} else
			// Custom
			if (customOp != null && !customOp.isEmpty())
				operatorSpace = (OperatorSpace) PlugInLoader.loadPlugin(ExtensionPoints.OPERATORS_SPACE);
		}

		approach.setOperatorSpace(operatorSpace);

	}

	protected static OperatorSpace createCustomOperatorSpace(String customOp) throws Exception {
		OperatorSpace customSpace = new OperatorSpace();
		String[] operators = customOp.split(File.pathSeparator);
		for (String op : operators) {
			AstorOperator aop = (AstorOperator) PlugInLoader.loadPlugin(op, ExtensionPoints.CUSTOM_OPERATOR._class);
			if (aop != null)
				customSpace.register(aop);
		}
		if (customSpace.getOperators().isEmpty()) {

			throw new Exception("Empty custom operator space");
		}
		return customSpace;
	}

	protected void loadFitnessFunction(AstorCoreEngine approach) throws Exception {
		// Fault localization
		approach.setFitnessFunction((FitnessFunction) PlugInLoader.loadPlugin(ExtensionPoints.FITNESS_FUNCTION));
	}

	protected void loadOperatorSelectorStrategy(AstorCoreEngine approach) throws Exception {
		String opStrategyClassName = ConfigurationProperties.properties.getProperty("opselectionstrategy");
		if (opStrategyClassName != null) {
			if ("uniform-random".equals(opStrategyClassName)) {
				approach.setOperatorSelectionStrategy(
						new UniformRandomRepairOperatorSpace(approach.getOperatorSpace()));
			} else if ("weighted-random".equals(opStrategyClassName)) {
				approach.setOperatorSelectionStrategy(new WeightedRandomOperatorSelection(approach.getOperatorSpace()));
			} else {
				OperatorSelectionStrategy strategy = createOperationSelectionStrategy(opStrategyClassName,
						approach.getOperatorSpace());
				approach.setOperatorSelectionStrategy(strategy);
			}
		} else {// By default, uniform strategy
			approach.setOperatorSelectionStrategy(new UniformRandomRepairOperatorSpace(approach.getOperatorSpace()));
		}
	}

	protected OperatorSelectionStrategy createOperationSelectionStrategy(String opSelectionStrategyClassName,
			OperatorSpace space) throws Exception {
		Object object = null;
		try {
			Class classDefinition = Class.forName(opSelectionStrategyClassName);
			object = classDefinition.getConstructor(OperatorSpace.class).newInstance(space);
		} catch (Exception e) {
			throw new Exception("Loading strategy: " + e);
		}
		if (object instanceof OperatorSelectionStrategy)
			return (OperatorSelectionStrategy) object;
		else
			throw new Exception("The strategy " + opSelectionStrategyClassName + " does not extend from "
					+ OperatorSelectionStrategy.class.getName());
	}

	protected void loadValidator(AstorCoreEngine approach) throws Exception {

		// After initializing population, we set up specific validation
		// mechanism
		// Select the kind of validation of a variant.
		String validationArgument = ConfigurationProperties.properties.getProperty("validation");
		if (validationArgument.equals("evosuite") || validationArgument.equals("augmented-test-suite")) {
			ProcessEvoSuiteValidator validator = new ProcessEvoSuiteValidator();
			approach.setProgramValidator(validator);
		} else
		// if validation is different to default (process)
		if (validationArgument.equals("process") || validationArgument.equals("test-suite")) {
			ProcessValidator validator = new ProcessValidator();
			approach.setProgramValidator(validator);
		} else {
			approach.setProgramValidator((ProgramVariantValidator) PlugInLoader.loadPlugin(ExtensionPoints.VALIDATION));
		}
	}

	protected void loadCompiler(AstorCoreEngine approach) throws Exception {
		approach.setCompiler((VariantCompiler) PlugInLoader.loadPlugin(ExtensionPoints.COMPILER));
	}

	protected void loadPopulation(AstorCoreEngine approach) throws Exception {
		// Population controller
		approach.setPopulationControler(
				(PopulationController) PlugInLoader.loadPlugin(ExtensionPoints.POPULATION_CONTROLLER));
	}

	protected void loadTargetElements(AstorCoreEngine approach) throws Exception {

		List<TargetElementProcessor<?>> targetElementProcessors = new ArrayList<TargetElementProcessor<?>>();

		approach.setTargetElementProcessors(targetElementProcessors);
		// Fix Space
		ExtensionPoints epoint = ExtensionPoints.INGREDIENT_PROCESSOR;
		if (!ConfigurationProperties.hasProperty(epoint.identifier)) {
			// By default, we use statements as granularity level.
			approach.getTargetElementProcessors().add(new SingleStatementFixSpaceProcessor());
		} else {
			// We load custom processors
			String ingrProcessors = ConfigurationProperties.getProperty(epoint.identifier);
			String[] in = ingrProcessors.split(File.pathSeparator);
			for (String processor : in) {
				if (processor.equals("statements")) {
					approach.getTargetElementProcessors().add(new SingleStatementFixSpaceProcessor());
				} else if (processor.equals("expression")) {
					approach.getTargetElementProcessors().add(new ExpressionIngredientSpaceProcessor());
				} else if (processor.equals("logical-relationaloperators")) {
					approach.getTargetElementProcessors().add(new IFExpressionFixSpaceProcessor());
				} else if (processor.equals("if-conditions")) {
					approach.getTargetElementProcessors().add(new IFConditionFixSpaceProcessor());
				} else {
					TargetElementProcessor proc_i = (TargetElementProcessor) PlugInLoader.loadPlugin(processor,
							epoint._class);
					targetElementProcessors.add(proc_i);
				}
			}
		}
		approach.setVariantFactory(new ProgramVariantFactory(approach.getTargetElementProcessors()));
	}

	protected void loadSolutionPrioritization(AstorCoreEngine approach) throws Exception {

		String patchpriority = ConfigurationProperties.getProperty("patchprioritization");
		if (patchpriority != null && !patchpriority.trim().isEmpty()) {
			SolutionVariantSortCriterion priorizStrategy = null;

			priorizStrategy = (SolutionVariantSortCriterion) PlugInLoader
					.loadPlugin(ExtensionPoints.SOLUTION_SORT_CRITERION);
			approach.setPatchSortCriterion(priorizStrategy);

		}
	}

	protected void loadOutputResults(AstorCoreEngine approach) throws Exception {

		List<ReportResults> outputs = new ArrayList<>();
		approach.setOutputResults(outputs);

		String outputproperty = ConfigurationProperties.getProperty("outputresults");
		if (outputproperty != null && !outputproperty.trim().isEmpty()) {
			String[] outprocess = outputproperty.split("|");

			for (String outp : outprocess) {
				ReportResults outputresult = (ReportResults) PlugInLoader.loadPlugin(outp,
						ExtensionPoints.OUTPUT_RESULTS._class);
				outputs.add(outputresult);
			}

		} else {
			outputs.add(new StandardOutputReport());
			outputs.add(new PatchJSONStandarOutput());
		}

	}

	public void load(AstorCoreEngine approach) throws Exception {
		this.loadFaultLocalization(approach);
		this.loadTargetElements(approach);
		this.loadSuspiciousNavigation(approach);
		this.loadCompiler(approach);
		this.loadValidator(approach);
		this.loadPopulation(approach);
		this.loadFitnessFunction(approach);
		this.loadOperatorSpaceDefinition(approach);
		this.loadOperatorSelectorStrategy(approach);
		this.loadSolutionPrioritization(approach);
		this.loadOutputResults(approach);

	}

}
