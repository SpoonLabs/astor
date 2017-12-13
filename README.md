ASTOR
=====

[![Build Status](https://travis-ci.org/SpoonLabs/astor.svg?branch=master)](https://travis-ci.org/SpoonLabs/astor)

Astor is an automatic software repair framework in Java for Java. It contains the new state of the art repair approaches named DeepRepair and Cardumen, and the implementation of state of the art repair approaches such as GenProg and Kali (named jGenProg and jKali, respectively).


If you use Astor for academic purposes, please cite the appropriate publication:

Matias Martinez, Martin Monperrus, "[ASTOR: A Program Repair Library for Java](https://hal.archives-ouvertes.fr/hal-01321615/document)", in Proceedings of ISSTA, Demonstration Track, 2016.

    @inproceedings{martinez:hal-01321615,
     title = {ASTOR: A Program Repair Library for Java},
     author = {Martinez, Matias and Monperrus, Martin},
     booktitle = {Proceedings of ISSTA},
     year = {2016},
     doi = {10.1145/2931037.2948705},
    }

Execution
-------

### jGenProg

We present an command line with the required arguments for executing jGenProg.  Optional arguments can find using option -help are listed below. They arguments can also be changed  in "configuration.properties".

Getting started: 

     git clone https://github.com/SpoonLabs/astor.git
     cd astor
     mvn clean compile # compiling  astor
     cd examples/Math-issue-280
     mvn clean compile test  # compiling and running bug example
     cd ../../
     mvn dependency:build-classpath | egrep -v "(^\[INFO\]|^\[WARNING\])" | tee /tmp/astor-classpath.txt
     cat /tmp/astor-classpath.txt
     java -cp $(cat /tmp/astor-classpath.txt):target/classes fr.inria.main.evolution.MainjGenProg -bug280


Output: Astor uses the standard output to print the solutions (i.e., the patches code), if any. 
Astor writes in the output folder (property `workingDirectory` in the mentioned file), a folder with all the variants that fulfill the goals i.e., repair the bugs. Example output:

```
PATCH_DIFF=--- /afs/pdc.kth.se/home/m/monp/astor/./output_astor/AstorMain-Math-issue-280/src/default/org/apache/commons/math/analysis/solvers/UnivariateRealSolverUtils.java	
+++ /afs/pdc.kth.se/home/m/monp/astor/./output_astor/AstorMain-Math-issue-280/src/variant-388/org/apache/commons/math/analysis/solvers/UnivariateRealSolverUtils.java	
@@ -49,7 +49,6 @@
 			numIterations++;
 		} while ((((fa * fb) > 0.0) && (numIterations < maximumIterations)) && ((a > lowerBound) || (b < upperBound)) );
 		if ((fa * fb) >= 0.0) {
-			throw new org.apache.commons.math.ConvergenceException(("number of iterations={0}, maximum iterations={1}, " + ("initial={2}, lower bound={3}, upper bound={4}, final a value={5}, " + "final b value={6}, f(a)={7}, f(b)={8}")), numIterations, maximumIterations, initial, lowerBound, upperBound, a, b, fa, fb);
 		}
 		return new double[]{ a, b };
 	}



2017-12-13 15:21:34,170 INFO fr.inria.astor.core.stats.Stats - Storing ing JSON at /afs/pdc.kth.se/home/m/monp/astor/./output_astor/AstorMain-Math-issue-280//astor_output.json
2017-12-13 15:21:34,170 INFO fr.inria.astor.core.stats.Stats - astor_output:{"NR_RIGHT_COMPILATIONS":64,"NR_FAILLING_COMPILATIONS":130,"patches":[{"PATCH_DIFF":"--- \\\/afs\\\/pdc.kth.se\\\/home\\\/m\\\/monp\\\/astor\\\/.\\\/output_astor\\\/AstorMain-Math-issue-280\\\/src\\\/default\\\/org\\\/apache\\\/commons\\\/math\\\/analysis\\\/solvers\\\/UnivariateRealSolverUtils.java\\t\\n+++ \\\/afs\\\/pdc.kth.se\\\/home\\\/m\\\/monp\\\/astor\\\/.\\\/output_astor\\\/AstorMain-Math-issue-280\\\/src\\\/variant-388\\\/org\\\/apache\\\/commons\\\/math\\\/analysis\\\/solvers\\\/UnivariateRealSolverUtils.java\\t\\n@@ -49,7 +49,6 @@\\n \\t\\t\\tnumIterations++;\\n \\t\\t} while ((((fa * fb) > 0.0) && (numIterations < maximumIterations)) && ((a > lowerBound) || (b < upperBound)) );\\n \\t\\tif ((fa * fb) >= 0.0) {\\n-\\t\\t\\tthrow new org.apache.commons.math.ConvergenceException((\\\"number of iterations={0}, maximum iterations={1}, \\\" + (\\\"initial={2}, lower bound={3}, upper bound={4}, final a value={5}, \\\" + \\\"final b value={6}, f(a)={7}, f(b)={8}\\\")), numIterations, maximumIterations, initial, lowerBound, upperBound, a, b, fa, fb);\\n \\t\\t}\\n \\t\\treturn new double[]{ a, b };\\n \\t}\\n\\n","VARIANT_ID":"388","VALIDATION":"|true|0|1981|[]|","patchhunks":[{"LOCATION":"org.apache.commons.math.analysis.solvers.UnivariateRealSolverUtils","INGREDIENT_SCOPE":"-","ORIGINAL_CODE":"throw new org.apache.commons.math.ConvergenceException((\\\"number of iterations={0}, maximum iterations={1}, \\\" + (\\\"initial={2}, lower bound={3}, upper bound={4}, final a value={5}, \\\" + \\\"final b value={6}, f(a)={7}, f(b)={8}\\\")), numIterations, maximumIterations, initial, lowerBound, upperBound, a, b, fa, fb)","BUGGY_CODE_TYPE":"CtThrowImpl|CtBlockImpl","OPERATOR":"RemoveOp","LINE":"201","SUSPICIOUNESS":"1","MP_RANKING":"0"}],"TIME":"873","GENERATION":"194"}],"NR_GENERATIONS":194,"TOTAL_TIME":873008,"NR_FAILING_VALIDATION_PROCESS":null}
2017-12-13 15:21:34,171 INFO fr.inria.main.evolution.AstorMain - Time Total(s): 897.951

```

Each variant folder contains the files that Astor has analyzed (and eventually modified). Additionally, it contains a file called 'Patch.xml' that summarized all changes done in the variant.
The summary of the execution is also printed on the screen at the end of the execution. If there is at least one solution, it prints “Solution found” and then it lists the program variants that are solution i.e., they fixed versions of the program. Then, if you go to the folder to each of those variants, the file patch appears, which summarizes the changes done for repairing the bug. In other words, the file `patch.xml` is only present if the variant is a valid solution (fixes the failing test and no regression).
If Astor does not find any solution in the execution, it prints at the screen something like “Not solution found”. 



Moreover, Astor saves the patched version of the program on disk.
The Astor's output is located in folder "./output_astor". You can change it through command line argument '-out'. There Astor writes a JSON file which summarizes the information of each patch found (location, code modified, etc.) and some statistics.
Inside the folder "/src/" Astor stores the source code of the solutions that it found.

Folder “default” contains the original program, without any modification. It's a sanity check, it’s the output of spoon without applying any processor over the spoon model of the application under repair.

Each folder "variant-x" is a valid solution to the repair problem (passes all tests). There is an command line argument `saveall` that allows you to save all variants that Astor generates, even they are not solution.

### jKali

For executing Astor in jKali mode, we use the option `-mode statement-remove`. jKali and jGenProg share the same arguments.

    java  -cp astor.jar fr.inria.main.evolution.MainjGenProg -mode statement-remove -location <>......

Arguments
----------

    java fr.inria.main.evolution.AstorMain 
    
    -mode statement 

    -location "location of the project to repair" 

    -dependencies "folder with the dependencies of the application to repair" 

    -failing "failing test case" (if there are several failing test case, give them separated by the classpath separator (: in linux/mac  and ; in windows)
        
    -package "package to manipulate" (only the statements from this package are manipulated to find a patch)

    -jvm4testexecution "jdklocation"/java-1.7.0/bin/ 

    -javacompliancelevel "compliance level of source code e.g. 5"

    -stopfirst true 

    -flthreshold "minimun suspicious value for fault localization e.g. 0.1"
    
    -srcjavafolder "source code folder"
    
    -srctestfolder "test source code folder"
    
    -binjavafolder "class folder"
    
    -bintestfolder "test class folder" 



Walkthrough
----------

**AstorMain.main**: Everything begins by constructing an AstorMain instance (which owns a Logger, a MutationSupporter, a spoon.reflect.factory.Factory, a ProjectRepairFacade, and a CommandLineParser) and reading configuration properties such as the URL of the project to manipulate, the name of the project, the project’s dependencies, failing test cases, and the fault localization threshold to run the program. 

**AstorMain.run**: uses six properties to initialize a project (AstorMain.initProject) before defining a JGenProg object for a particular mode of executionwithin: Execution.JGenProg, ExecutionMode.jKali, and ExecutionMode.MutRepair corresponding to three test suite based repair approaches, viz. GenProg, Kali, and a mutation-testing based repair approach, respectively. Subsequently, the method creates a repair engine (AstorMain.createEngine) and an initial population of program variants (JGenProg.createInitialPopulation). It evolves the program variants (JGenProg.startEvolution) while we still have variants; we are still failing test cases; we have neither reached the maximum number of generations nor the maximum number of minutes. Afterward, run will show the results (JGenProg.showResults) and log the total time elapsed from initializing the project to showing the results.

**AstorMain.initProject**: initProject parses a string argument into a list of failing Junit test cases. Then initProject initializes a project façade before setting up temp directories, which will contain copies of the original src and bin directories for the experiment. Finally, it defines the regression suite.

**AstorMain.createEngine**: createEngine creates a repair engine based on the mode of execution. It constructs a MutationSupporter to carry out supporter tasks, e.g., creating directories, copying directories, and calling the compiler. Then it constructs a list of source code transformers based on the Spoon transformation library (which serves as the fix space) with a default of a new SingleStatementFixSpaceProcessor; therefore, the ingredients processor will retrieve statements (excluding blocks, methods, and classes). More specifically, it only adds statements whose parent is a block. Afterward, different properties will be defined, depending on the mode of execution. Then—focusing on JgenProg— method createEngine sets the repair action space and handles the ingredients to build the patches by inspecting the scope of the ingredient search space, which is a ConfigurationProperty. There are three scopes: global (all classes from the application under analysis), package (classes from the same package), and local (same class); the default is local. GlobalBasicFixSpace and PackageBasicFixSpace extend LocalFixSpace which extends the abstract class UniformRandomFixSpace (UniformRandomFixSpace takes uniform random elements from the search space. It takes a location and a CtType root and creates a fix space from a CtClass) which implements FixLocationSpace. createEngine sets the fix space and then sets the population controller. A FitnessPopulationController controls the population of program variants by the fitness values. For the three test suite based approaches, the fitness relates to the number of failing test cases, and the goal is to find a program with a fitness of zero. Finally, createEngine sets the variant factory and the program validator.

**JGenProg.createInitialPopulation**: If we do not use fault localization, then createInitialPopulation initializes a population with a new ArrayList<SuspiciousCode>(). Otherwise, it initializes the population using the project façade, which will run spectrum-based fault localization to get a list of suspicious statements. After building and processing the Spoon model, the population is initialized using the list of suspicious statements, which creates the program variants from the suspicious code. Specifically, for each suspicious line, a list of genotypes will be created from that suspicious line (when it’s possible) by querying suspicious Spoon elements. The first suspicious element is used to query all the variables in scope, which serves as the context. (This process of establishing the context is made easy by the fact that only statements are mutated.) A genotype is created for each filtered element. Finally, the program variants are compiled, and the fitness value is computed.

**JGenProg.startEvolution**. Here is where we start our solution search. For each program variant, the original variant will be saved and then a new program variant will be created by cloning a child variant from a given parent variant before mutating the child. Specifically, in each generation, Astor applies one source code transformation to each program variant (the parent variant) to produce a child variant and then evaluates a fitness function over each program variant. After each child variant is evaluated, Astor selects some variants among the children and parents to be part of the next generation according to their fitness values. 

Extension points
-------
Astor can be extended with out modifying the source code.
For instance, one could add to Astor new repair operators or to add a customize strategy for navigating the search space.

***Custom Repair Operator***

Astor provides a way to add customized repair operators to each of the mentioned approaches (jGenProg, jMutRepair, jKali).
For example, one can include to jMutRepair a new operator that mutates right-side expressions from assignments.

A customize operator class extends from abstract class fr.inria.astor.core.loop.spaces.operators.AstorOperator.

Then, the canonical name of this operator's class is passed to Astor via the argument '-customop'.


***Custom Ingredient Selection Strategy***

Astor allows to integrate a custom strategy for selecting ingredients from the ingredient search space.
By default, Astor randomly takes one ingredient from a given ingredient scope (Application, Package or File).

A customize ingredient selection strategy class extends from abstract class fr.inria.astor.core.loop.spaces.ingredients.IngredientStrategy.

Then, the canonical name of this strategy's class is passed to Astor via the argument '-ingredientstrategy'.

We detail this extension point in the document [Ingredient Space Tutorial](https://github.com/SpoonLabs/astor/blob/master/docs/tutorials/readme_ing_space.md).

Limitations
------

Due to limitation of fault localization, repair is impossible in a constructor for now. 

Contacts
--------
matias.sebastian.martinez@gmail.com
martin.monperrus@csc.kth.se
