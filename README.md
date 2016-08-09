ASTOR
=====

[![Build Status](https://travis-ci.org/SpoonLabs/astor.svg?branch=master)](https://travis-ci.org/SpoonLabs/astor)

Astor is an automatic software repair framework in Java for Java. It contains an implementation of state of the art repair approaches such as GenProg and Kali.


If you use Astor for academic purposes, please cite the appropriate publication:

Matias Martinez, Martin Monperrus, "ASTOR: A Program Repair Library for Java", in Proceedings of ISSTA, Demonstration Track, 2016.

    @inproceedings{martinez:hal-01321615,
     title = {ASTOR: A Program Repair Library for Java},
     author = {Martinez, Matias and Monperrus, Martin},
     booktitle = {Proceedings of ISSTA, Demonstration Track},
     year = {2016},
    url = {https://hal.archives-ouvertes.fr/hal-01321615/document},
   }

Matias Martinez, Martin Monperrus. "ASTOR: Evolutionary Automatic Software Repair for Java". Technical Report hal-01075976, Inria; 2014. 

    @techreport{hal-01075976,
     TITLE = {{ASTOR: Evolutionary Automatic Software Repair for Java}},
     AUTHOR = {Martinez, Matias and Monperrus, Martin},
     URL = {https://hal.archives-ouvertes.fr/hal-01075976},
     INSTITUTION = {{Inria}},
     YEAR = {2014}
    }


Compilation
-------

Please install a JDK 1.8 and configure Maven or your IDE to use it. Fill property jvm4testexecution in `src/main/resources/configuration.properties`.

To compile using maven, first execute:

    mvn clean
    mvn compile

We recommend to remove all package-info.java files from the project to repair (You can use command: `find . -name "package-info.java" -type f -delete`).

Execution
-------
We provide an implementation of GenProg repair algorithm called jGenProg.The class to run it is:

    fr.inria.main.evolution.MainjGenProg

After the execution of a repair attempt, Astor writes in the output folder (property `workingDirectory` in the mentioned file), a folder with all the variants that fulfill the goals i.e., repair the bugs.
Each variant folder contains the files that Astor has analyzed (and eventually modified). Additionally, it contains a file called 'Patch.xml' that summarized all changes done in the variant.
The summary of the execution is also printed on the screen at the end of the execution. If there is at least one solution, it prints “Solution found” and then it lists the program variants that are solution i.e., they fixed versions of the program. Then, if you go to the folder to each of those variants, the file patch appears, which summarizes the changes done for repairing the bug. In other words, the file `patch.xml` is only present if the variant is a valid solution (fixes the failing test and no regression).
If Astor does not find any solution in the execution, it prints at the screen something like “Not solution found”. 


**jGenProg**

We present an command line with the required arguments for executing jGenProg.  Optional arguments can find using option -help are listed below. They arguments can also be changed  in "configuration.properties".

Minimum arguments:
    java fr.inria.main.evolution.MainjGenProg 
    -mode statement 

    -location "location of the project to repair" 

    -dependencies "folder with the dependencies of the application to repair" 

    -failing "failing test case" (if there are several failing test case, give them separated by the classpath separator (: in linux/mac  and ; in windows)
        
    -package "package to manipulate" (only the statements from this package are manipulated to find a patch)

    -jvm4testexecution "jdklocation"/java-1.7.0/bin/ 

    -javacompliancelevel "compliance level of source code e.g. 5"

    -stopfirst true 

    -flthreshold "minimun suspicious value for fault localization e.g. 0.1"

Other options: 
      -srcjavafolder "source code folder"
      -srctestfolder "test source code folder"
      -binjavafolder "class folder" 
      -bintestfolder "test class folder" 


If you use command line, the -cp argument of java must include the absolute path of Astor jar. Otherwise, it could be the case that an exception is thrown by the fault localization tool (Gzoltar) used by Astor.

**Output**:

The Astor's output is located in folder "./outputMutation". You can change it through command line argument '-out'. Inside the folder "/src/" Astor stores the source code of the solutions that it found.

Folder “default” contains the original program, without any modification. It's a sanity check, it’s the output of spoon without applying any processor over the spoon model of the application under repair.

Each folder "variant-x" is a valid solution to the repair problem (passes all tests). There is an command line argument `saveall` that allows you to save all variants that Astor generates, even they are not solution.
For each variant x that is solution, Astor summarizes all changes applied to variant x inside the file '/variant-x/patch.xml'.

**jKali**

For executing Astor in jKali mode, we use the option `-mode statement-remove`. jKali and jGenProg share the same arguments.

    java  -cp astor.jar fr.inria.main.evolution.MainjGenProg -mode statement-remove -location <>......


**Bug Example**

The distribution contains a version of Apache commons Math with a real defect (reported in issue 280 https://issues.apache.org/jira/browse/MATH-280).

To run it using jGenProg, type: 

     java -version # it is JDK 7?
     mvn clean compile # compiling  astor
     cd examples/Math-issue-280
     mvn clean compile test  # compiling and running bug example
     cd ../../
     mvn  dependency:build-classpath | egrep -v "(^\[INFO\]|^\[WARNING\])" | tee /tmp/astor-classpath.txt
     cat /tmp/astor-classpath.txt
     java -cp $(cat /tmp/astor-classpath.txt):target/classes fr.inria.main.evolution.MainjGenProg -bug280


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
matias.sebastian.martinez@usi.ch
martin.monperrus@univ-lille1.fr
