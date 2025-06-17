package fr.inria.astor.core.ingredientbased;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.martiansoftware.jsap.JSAPException;

import fr.inria.astor.approaches.jgenprog.operators.ReplaceOp;
import fr.inria.astor.core.entities.*;
import fr.inria.astor.core.manipulation.MutationSupporter;
import fr.inria.astor.core.manipulation.filters.TargetElementProcessor;
import fr.inria.astor.core.setup.ConfigurationProperties;
import fr.inria.astor.core.setup.ProjectRepairFacade;
import fr.inria.astor.core.solutionsearch.ExhaustiveSearchEngine;
import fr.inria.astor.core.solutionsearch.navigation.SuspiciousNavigationValues;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPool;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientSearchStrategy;
import fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.IngredientTransformationStrategy;
import fr.inria.astor.core.solutionsearch.spaces.operators.AstorOperator;
import fr.inria.astor.core.solutionsearch.spaces.operators.IngredientBasedOperator;
import fr.inria.astor.core.stats.PatchHunkStats;
import fr.inria.astor.core.stats.PatchStat.HunkStatEnum;
import fr.inria.main.AstorOutputStatus;
import fr.inria.main.evolution.ExtensionPoints;
import spoon.reflect.code.CtCodeSnippetStatement;
import spoon.reflect.declaration.CtElement;

/**
 * Exhaustive Search Engine
 * 
 * @author Matias Martinez, matias.martinez@inria.fr
 * 
 */
public class LLMIngredientEngine extends ExhaustiveSearchEngine implements IngredientBasedApproach {

	protected IngredientPool ingredientSpace = null;

	protected IngredientTransformationStrategy ingredientTransformationStrategy;

	public LLMIngredientEngine(MutationSupporter mutatorExecutor, ProjectRepairFacade projFacade)
			throws JSAPException {
		super(mutatorExecutor, projFacade);
		ConfigurationProperties.properties.setProperty(ExtensionPoints.TARGET_CODE_PROCESSOR.identifier, "statements");
		//ConfigurationProperties.properties.setProperty(ExtensionPoints.OPERATORS_SPACE.identifier, "irr-statements");
		ConfigurationProperties.properties.setProperty(ExtensionPoints.SUSPICIOUS_NAVIGATION.identifier,
				SuspiciousNavigationValues.INORDER.toString());
	}

	@Override
	public void startSearch() throws Exception {

		if (this.ingredientSpace == null) {
			this.ingredientSpace = IngredientBasedEvolutionaryRepairApproachImpl
					.getIngredientPool(getTargetElementProcessors());
		}
		dateInitEvolution = new Date();
		// We don't evolve variants, so the generation is always one.
		generationsExecuted = 1;
		// For each variant (one is enough)
		int maxMinutes = ConfigurationProperties.getPropertyInt("maxtime");
		int maxGenerations = ConfigurationProperties.getPropertyInt("maxGeneration");
		// for stats
		int modifPointsAnalyzed = 0;
		int operatorExecuted = 0;

		getIngredientSpace().defineSpace(originalVariant);

		int totalmodfpoints = variants.get(0).getModificationPoints().size();
		for (ProgramVariant parentVariant : variants) {

			for (ModificationPoint modifPoint : this.getSuspiciousNavigationStrategy()
					.getSortedModificationPointsList(parentVariant.getModificationPoints())) {

				modifPointsAnalyzed++;

				log.info("\n MP (" + modifPointsAnalyzed + "/" + parentVariant.getModificationPoints().size()
						+ ") location to modify: " + modifPoint);

				// We create all operators to apply in the modifpoint
				List<OperatorInstance> operatorInstances = createInstancesOfOperators(
						(SuspiciousModificationPoint) modifPoint);

				// log.info("--- List of operators (" + operatorInstances.size()
				// + ") : " + operatorInstances);
				// We create the operator instances
				if (operatorInstances == null || operatorInstances.isEmpty())
					continue;

				for (OperatorInstance pointOperation : operatorInstances) {

					operatorExecuted++;

					// We validate the variant after applying the operator
					ProgramVariant solutionVariant = variantFactory.createProgramVariantFromAnother(parentVariant,
							generationsExecuted);
					solutionVariant.getOperations().put(generationsExecuted, Arrays.asList(pointOperation));

					applyNewMutationOperationToSpoonElement(pointOperation);

					log.debug("Operator:\n " + pointOperation);
					boolean solution = processCreatedVariant(solutionVariant, generationsExecuted);
				
					if (solution) {
						log.info("Solution found " + getSolutions().size());

						this.solutions.add(solutionVariant);

					}

					// We undo the operator (for try the next one)
					undoOperationToSpoonElement(pointOperation);

					if (solution) {
						log.info("Solution found " + getSolutions().size());
						this.savePatch(solutionVariant);
					}

					if (!this.solutions.isEmpty() && ConfigurationProperties.getPropertyBool("stopfirst")) {
						this.setOutputStatus(AstorOutputStatus.STOP_BY_PATCH_FOUND);
						log.debug(" modpoint analyzed " + modifPointsAnalyzed + ", operators " + operatorExecuted);
						return;
					}

					if (!belowMaxTime(dateInitEvolution, maxMinutes)) {
						this.setOutputStatus(AstorOutputStatus.TIME_OUT);
						log.debug("Max time reached");
						return;
					}

					if (maxGenerations <= operatorExecuted) {

						this.setOutputStatus(AstorOutputStatus.MAX_GENERATION);
						log.info("Stop-Max operator Applied " + operatorExecuted);
						log.info("modpoint:" + modifPointsAnalyzed + ":all:" + totalmodfpoints + ":operators:"
								+ operatorExecuted);
						return;
					}

					if (this.getSolutions().size() >= ConfigurationProperties.getPropertyInt("maxnumbersolutions")) {
						this.setOutputStatus(AstorOutputStatus.STOP_BY_PATCH_FOUND);
						log.debug("Stop-Max solutions reached " + operatorExecuted);
						log.debug("modpoint:" + modifPointsAnalyzed + ":all:" + totalmodfpoints + ":operators:"
								+ operatorExecuted);
						return;
					}
				}
			}
		}

		this.setOutputStatus(AstorOutputStatus.EXHAUSTIVE_NAVIGATED);
		System.out.println("\nEND exhaustive search Summary:\n" + "modpoint:" + modifPointsAnalyzed + ":all:"
				+ totalmodfpoints + ":operators:" + operatorExecuted);

	}

	/**
	 * @param modificationPoint
	 * @return
	 */
	@Override
	protected List<OperatorInstance> createInstancesOfOperators(SuspiciousModificationPoint modificationPoint) {
		List<OperatorInstance> ops = new ArrayList<>();
		
		// Get the code to fix
		String codeToFix = modificationPoint.getCodeElement().toString();

		// Get failing test class name from suspicious
		String failingTest = modificationPoint.getSuspicious().getClassName();

		

		// Generate fix using LLM
		List<String> suggestions = getLLMSuggestion(codeToFix, failingTest);


		for (String fixedCode : suggestions) {


			// Create a CtCodeSnippetStatement from the fixed code string using MutationSupporter
			CtCodeSnippetStatement snippetStatement = MutationSupporter.factory.Core().createCodeSnippetStatement();
			snippetStatement.setValue(fixedCode);
			//opInstance.setModified(snippetStatement);

			// Create ReplaceOp with the fix
			ReplaceOp replaceOp = new ReplaceOp();
			//OperatorInstance opInstance = new StatementOperatorInstance();
			OperatorInstance opInstance = new StatementOperatorInstance(modificationPoint, replaceOp,
					modificationPoint.getCodeElement(), snippetStatement);
			//opInstance.setOperationApplied(replaceOp);
			//opInstance.setOriginal(modificationPoint.getCodeElement());
			//opInstance.setModificationPoint(modificationPoint);

			ops.add(opInstance);
		}

		return ops;
	}
	
	private List<String> getLLMSuggestion(String buggyCodeOriginal, String testCode) {
		String buggyCode = "";
		String restCode = "";
		if (buggyCodeOriginal.contains("\n")) {
			String[] splitCode = buggyCodeOriginal.split("\n");
			buggyCode = splitCode[0];// Use only the first line of buggy code for simplicity
			for (int i = 1; i < splitCode.length; i++) {
				restCode += "\n" + splitCode[i].trim(); // Reconstruct the code without leading spaces
			}

		}
		else{
			buggyCode = buggyCodeOriginal; // Use the whole code if no new lines
		}


        List<String> candidates = new ArrayList<>();

		//if (true){
		//	candidates.add("if (perm.get(j) >= perm.get(i)) {\n"+restCode);
		//	return candidates;
		//}

        
        int maxP = ConfigurationProperties.getPropertyInt("maxsuggestionsperpoint");
		//maxP = (maxP > 0 && maxP < 7) ? maxP : 6;  // max suggestions for each modification point given by LLM is 6
        
		String llmService = ConfigurationProperties.getProperty("llmService");
        if (llmService == null || llmService.trim().isEmpty()) {
            llmService = "ollama"; // Default to Ollama if not specified
        }
        
		String llmModel = ConfigurationProperties.getProperty("llmmodel");
        if (llmModel == null || llmModel.trim().isEmpty()) {
            llmModel = "codellama:7b"; // Default to CodeLlama 7b if not specified
        }

        String templateName = ConfigurationProperties.getProperty("llmprompttemplate");        
        String template = LLMPromptTemplate.getTemplate(templateName);
        if (template == null) {
            template = LLMPromptTemplate.getTemplate("MULTIPLE_SOLUTIONS");
        }
        String prompt = LLMPromptTemplate.fillTemplate(template, buggyCode, testCode, maxP);
        
		System.out.println("-------------------------------------------------");
        System.out.println("\nSending prompt to LLM model" + llmModel + ":\n" + prompt);

        try {
            // Get the response from the LLM with explicit parameters
            String response = LLMService.generateCode(prompt, llmService, llmModel);
            
            // Print the raw response
            System.out.println("Raw LLM response:\n" + response); // Show LLM response for debugging. 
            
            // Check if we're using the multiple solutions template
            if (templateName != null && templateName.equals("MULTIPLE_SOLUTIONS")) {
                // Extract multiple solutions from the response
                candidates = extractMultipleSolutions(response);
            } else {
                // Clean the response and add as a single solution
                String cleanedResponse = cleanLLMResponse(response);
                candidates.add(cleanedResponse);
            }
            List<String> ncandidates = new ArrayList();
			for (String candidate : candidates) {
				// if restCode is not empty, append it to the candidate
				if (!restCode.isEmpty()) {
					candidate += restCode;
					ncandidates.add(candidate);
				}

			}
			if (!ncandidates.isEmpty()) {
				candidates = ncandidates;
			}
        } catch (Exception e) {
            log.error("Error getting LLM suggestion", e);
        }
        
        // Limit the number of suggestions
        int numSuggestions = Math.min(candidates.size(), maxP);

        System.out.println("\nFinal LLM suggestions: " + candidates.subList(0, numSuggestions) + "\n");

        return candidates.subList(0, numSuggestions);
    }

    // Helper method to extract multiple solutions from LLM response
    private List<String> extractMultipleSolutions(String response) {
        List<String> solutions = new ArrayList<>();
        
        if (response == null || response.isEmpty()) {
            return solutions;
        }
        
        // Remove markdown code blocks first
        response = response.replaceAll("```java", "").replaceAll("```", "");
        
        // Try to extract solutions based on the format pattern
        String[] lines = response.split("\n");
        StringBuilder currentSolution = new StringBuilder();
        boolean collectingSolution = false;
        
        for (String line : lines) {
            line = line.trim();
            
            if (line.startsWith("SOLUTION") || line.contains("SOLUTION")) {
                // If we were collecting a previous solution, add it
                if (collectingSolution && currentSolution.length() > 0) {
                    solutions.add(currentSolution.toString().trim());
                    currentSolution = new StringBuilder();
                }
                collectingSolution = true;
            } else if (collectingSolution && !line.isEmpty() ) {
                // This looks like actual code
                currentSolution.append(line);
            }
        }
        
        // Add the last solution if there is one
        if (collectingSolution && currentSolution.length() > 0) {
            solutions.add(currentSolution.toString().trim());
        }
        
        // If the format was not as expected, fallback to a simpler approach
        if (solutions.isEmpty()) {
            // Look for lines that might contain solutions
            for (String line : lines) {
                line = line.trim();
                if (line.isEmpty()) continue;
                
                if (line.contains("return") || line.contains("=") || line.contains(";")) {
                    solutions.add(line);
                }
            }
        }
        
        return solutions;
    }

    // Helper method to clean LLM responses
    private String cleanLLMResponse(String response) {
        if (response == null) {
            return "";
        }
        
        // Remove markdown code blocks
        response = response.replaceAll("```java", "").replaceAll("```", "");
		// Workaround for backticks
		response = response.replace("`", ""); // Remove any backticks that might be left

        // If the response contains multiple lines, try to extract just the code line
        if (response.contains("\n")) {
            String[] lines = response.split("\n");
            for (String line : lines) {
                // Look for lines that might contain a solution
                if (line.contains("return") || line.contains(";")) {
                    return line.trim();
                }
            }
        }



        return response.trim();
    }

	@SuppressWarnings("unchecked")
	public List<OperatorInstance> createIngredientOpInstance(SuspiciousModificationPoint modificationPoint,
			IngredientBasedOperator astorOperator) throws Exception {

		List<OperatorInstance> ops = new ArrayList<>();
		List<Ingredient> ingredients = new ArrayList<>();

		if (astorOperator instanceof ReplaceOp) {
			String type = ingredientSpace.getType(new Ingredient(modificationPoint.getCodeElement())).toString();

			ingredients = ingredientSpace.getIngredients(modificationPoint.getCodeElement(), type);

		} else {
			ingredients = ingredientSpace.getIngredients(modificationPoint.getCodeElement());

		}

		if (ingredients == null) {
			log.error("Zero ingredients mp: " + modificationPoint + ", op " + astorOperator);
			return ops;
		}
		log.debug("Number of ingredients " + ingredients.size());
		for (Ingredient ingredient : ingredients) {

			List<OperatorInstance> operatorInstances = null;
			operatorInstances = astorOperator.createOperatorInstances(modificationPoint, ingredient,
					this.ingredientTransformationStrategy);

			ops.addAll(operatorInstances);

		}

		return ops;

	}

	public IngredientPool getIngredientSpace() {
		return ingredientSpace;
	}

	public void setIngredientSpace(IngredientPool ingredientSpace) {
		this.ingredientSpace = ingredientSpace;
	}

	@Override
	public IngredientPool getIngredientPool() {
		return this.ingredientSpace;
	}

	@Override
	public void setIngredientPool(IngredientPool ingredientPool) {
		this.ingredientSpace = ingredientPool;

	}

	@Override
	public void setIngredientTransformationStrategy(IngredientTransformationStrategy ingredientTransformationStrategy) {
		this.ingredientTransformationStrategy = ingredientTransformationStrategy;
	}

	@Override
	public IngredientTransformationStrategy getIngredientTransformationStrategy() {
		return ingredientTransformationStrategy;
	}

	@Override
	public IngredientSearchStrategy getIngredientSearchStrategy() {
		return null;
	}

	@Override
	public void setIngredientSearchStrategy(IngredientSearchStrategy ingredientStrategy) {

	}

	@SuppressWarnings("rawtypes")
	protected void loadIngredientPool() throws JSAPException, Exception {
		List<TargetElementProcessor<?>> ingredientProcessors = this.getTargetElementProcessors();
		// The ingredients for build the patches
		IngredientPool ingredientspace = IngredientBasedEvolutionaryRepairApproachImpl
				.getIngredientPool(ingredientProcessors);

		this.setIngredientPool(ingredientspace);

	}

	@SuppressWarnings("rawtypes")
	protected void loadIngredientSearchStrategy() throws Exception {
		this.setIngredientSearchStrategy(
				IngredientBasedEvolutionaryRepairApproachImpl.retrieveIngredientSearchStrategy(getIngredientPool()));
	}

	protected void loadIngredientTransformationStrategy() throws Exception {

		IngredientTransformationStrategy ingredientTransformationStrategyLoaded = IngredientBasedEvolutionaryRepairApproachImpl
				.retrieveIngredientTransformationStrategy();
		this.setIngredientTransformationStrategy(ingredientTransformationStrategyLoaded);
	}

	@Override
	protected void setParticularStats(PatchHunkStats hunk, OperatorInstance genOperationInstance) {
		// TODO Auto-generated method stub
		super.setParticularStats(hunk, genOperationInstance);
		hunk.getStats().put(HunkStatEnum.INGREDIENT_SCOPE,
				((genOperationInstance.getIngredientScope() != null) ? genOperationInstance.getIngredientScope()
						: "-"));

		if (genOperationInstance.getIngredient() != null
				&& genOperationInstance.getIngredient().getDerivedFrom() != null)
			hunk.getStats().put(HunkStatEnum.INGREDIENT_PARENT, genOperationInstance.getIngredient().getDerivedFrom());

	}

	@Override
	public void loadExtensionPoints() throws Exception {
		super.loadExtensionPoints();
		this.loadIngredientPool();
		this.loadIngredientSearchStrategy();
		this.loadIngredientTransformationStrategy();
	}

}
