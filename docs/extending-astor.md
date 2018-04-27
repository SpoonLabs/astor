How to extend the Astor repair framework?
=========================================

We detail the list of all extension points in the document [Extension Points](https://github.com/SpoonLabs/astor/blob/master/docs/extension_points.md).


Walkthrough through the main methods
----------

**AstorMain.main**: Everything begins by constructing an AstorMain instance (which owns a Logger, a MutationSupporter, a spoon.reflect.factory.Factory, a ProjectRepairFacade, and a CommandLineParser) and reading configuration properties such as the URL of the project to manipulate, the name of the project, the project’s dependencies, failing test cases, and the fault localization threshold to run the program. 

**AstorMain.run**: uses six properties to initialize a project (AstorMain.initProject) before defining a JGenProg object for a particular mode of executionwithin: Execution.JGenProg, ExecutionMode.jKali, and ExecutionMode.MutRepair corresponding to three test suite based repair approaches, viz. GenProg, Kali, and a mutation-testing based repair approach, respectively. Subsequently, the method creates a repair engine (AstorMain.createEngine) and an initial population of program variants (JGenProg.createInitialPopulation). It evolves the program variants (JGenProg.startEvolution) while we still have variants; we are still failing test cases; we have neither reached the maximum number of generations nor the maximum number of minutes. Afterward, run will show the results (JGenProg.showResults) and log the total time elapsed from initializing the project to showing the results.

**AstorMain.initProject**: initProject parses a string argument into a list of failing Junit test cases. Then initProject initializes a project façade before setting up temp directories, which will contain copies of the original src and bin directories for the experiment. Finally, it defines the regression suite.

**AstorMain.createEngine**: createEngine creates a repair engine based on the mode of execution. It constructs a MutationSupporter to carry out supporter tasks, e.g., creating directories, copying directories, and calling the compiler. Then it constructs a list of source code transformers based on the Spoon transformation library (which serves as the fix space) with a default of a new SingleStatementFixSpaceProcessor; therefore, the ingredients processor will retrieve statements (excluding blocks, methods, and classes). More specifically, it only adds statements whose parent is a block. Afterward, different properties will be defined, depending on the mode of execution. Then—focusing on JgenProg— method createEngine sets the repair action space and handles the ingredients to build the patches by inspecting the scope of the ingredient search space, which is a ConfigurationProperty. There are three scopes: global (all classes from the application under analysis), package (classes from the same package), and local (same class); the default is local. GlobalBasicFixSpace and PackageBasicFixSpace extend LocalFixSpace which extends the abstract class UniformRandomFixSpace (UniformRandomFixSpace takes uniform random elements from the search space. It takes a location and a CtType root and creates a fix space from a CtClass) which implements FixLocationSpace. createEngine sets the fix space and then sets the population controller. A FitnessPopulationController controls the population of program variants by the fitness values. For the three test suite based approaches, the fitness relates to the number of failing test cases, and the goal is to find a program with a fitness of zero. Finally, createEngine sets the variant factory and the program validator.

**method createInitialPopulation**: If we do not use fault localization, then createInitialPopulation initializes a population with a new ArrayList<SuspiciousCode>(). Otherwise, it initializes the population using the project façade, which will run spectrum-based fault localization to get a list of suspicious statements. After building and processing the Spoon model, the population is initialized using the list of suspicious statements, which creates the program variants from the suspicious code. Specifically, for each suspicious line, a list of genotypes will be created from that suspicious line (when it’s possible) by querying suspicious Spoon elements. The first suspicious element is used to query all the variables in scope, which serves as the context. (This process of establishing the context is made easy by the fact that only statements are mutated.) A genotype is created for each filtered element. Finally, the program variants are compiled, and the fitness value is computed.


Other Documents: 
---------------


***Extension point for Ingredient Selection Strategy***

Astor allows to integrate a custom strategy for selecting ingredients from the ingredient search space.
By default, Astor randomly takes one ingredient from a given ingredient scope (Application, Package or File).

A customize ingredient selection strategy class extends from abstract class fr.inria.astor.core.loop.spaces.ingredients.IngredientStrategy.

Then, the canonical name of this strategy's class is passed to Astor via the argument '-ingredientstrategy'.

We detail this extension point in the document [Ingredient Space Tutorial](https://github.com/SpoonLabs/astor/blob/master/docs/tutorials/readme_ing_space.md).
