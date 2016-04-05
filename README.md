ASTOR
=====

[![Build Status](https://travis-ci.org/SpoonLabs/astor.svg?branch=master)](https://travis-ci.org/SpoonLabs/astor)

If you use Astor, please cite this technical report:

Matias Martinez, Martin Monperrus. "ASTOR: Evolutionary Automatic Software Repair for Java". Technical Report hal-01075976, Inria; 2014. 

    @techreport{hal-01075976,
     TITLE = {{ASTOR: Evolutionary Automatic Software Repair for Java}},
     AUTHOR = {Martinez, Matias and Monperrus, Martin},
     URL = {https://hal.archives-ouvertes.fr/hal-01075976},
     INSTITUTION = {{Inria}},
     YEAR = {2014}
    }

Astor is an evolutionary Automatic Software Repair framework that contains implementation of state of the art repair approaches such as GenProg and Kali.


Getting started
-------

Note that this project requires the use of **Java 1.7**; it does not build or does not run (on OS X 10.10.3) with Java 1.8. Please install a JDK 1.7 and configure Maven or your IDE to use it. Fill property jvm4testexecution in `src/main/resources/configuration.properties`.

To compile using maven, first execute:

    mvn clean
    mvn compile

We recommend to remove all package-info.java files from the project to repair (You can use command: `find . -name "package-info.java" -type f -delete`).

We provide an implementation of GenProg repair algorithm called jGenProg.The class to run it is:

    fr.inria.main.evolution.MainjGenProg

After the execution of a repair attempt, Astor writes in the output folder (property `workingDirectory` in the mentioned file), a folder with all the variants that fulfill the goals i.e., repair the bugs.
Each variant folder contains the files that Astor has analyzed (and eventually modified). Additionally, it contains a file called 'Patch.xml' that summarized all changes done in the variant.
The summary of the execution is alsoprinted on the screen at the end of the execution. If there is at least one solution, it prints “Solution found” and then it lists the program variants that are solution i.e., they fixed versions of the program. Then, if you go to the folder to each of those variants, the file patch appears, which summarizes the changes done for repairing the bug. In other words, the file `patch.xml` is only present if the variant is a valid solution (fixes the failing test and no regression).
If Astor does not find any solution in the execution, it prints at the screen something like “Not solution found”. 


**jGenProg**

We present an command line with the required arguments for executing jGenProg.  Optional arguments can find using option -help are listed below. They arguments can also be changed  in "configuration.properties".

Minimum arguments:
    java fr.inria.main.evolution.MainjGenProg 
    -mode statement 

    -location "location of the project to repair" 

    -dependencies "folder with the dependencies of the application to repair" 

    -failing "failing test case>"
    
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


**Output**:

The Astor's output is located in folder "./outputMutation". You can change it through command line argument '-out'. Inside the folder "/src/" Astor stores the source code of the solutions that it found.

Folder “default” contains the original program, without any modification. It's a sanity check, it’s the output of spoon without applying any processor over the spoon model of the application under repair.

Each folder "variant-x" is a valid solution to the repair problem (passes all tests). There is an command line argument `saveall` that allows you to save all variants that Astor generates, even they are not solution.
For each variant x that is solution, Astor sumarizes all changes applied to variant x inside the file '/variant-x/patch.xml'.

**jKali**

For executing Astor in jKali mode, we use the option `-mode statement-remove`. jKali and jGenProg share the same arguments.

    java  -cp astor.jar fr.inria.main.evolution.MainjGenProg -mode statement-remove -location <>......


**Bug Example**

The distribution contains a version of Apache commons Math with a real defect (reported in issue 280 https://issues.apache.org/jira/browse/MATH-280).

To run it using jGenProg, type: 

     java -version # it is JDK 7?
     cd examples/Math-issue-280
     mvn test 
     cd ../../
     mvn compile
     mvn  dependency:build-classpath | egrep -v "(^\[INFO\]|^\[WARNING\])" | tee /tmp/astor-classpath.txt
     cat /tmp/astor-classpath.txt
     java -cp $(cat /tmp/astor-classpath.txt):target/classes fr.inria.main.evolution.MainjGenProg -bug280

or 

    java fr.inria.main.evolution.MainjGenProg -bug280

Extesion points
-------
Astor can be extended witout modifying the source code.
For instance, one could add to Astor new repair operators or to add a customize strategy for navigating the search space.

***Custom Repair Operator***

Astor provides a way to add customized repair operators to each of the mentioned approaches (jGenProg, jMutRepair, jKali).
For example, one can include to jMutRepair a new operator that mutates right-side expressions from assignments.

A customize operator class extends from abstract class fr.inria.astor.core.loop.spaces.operators.AstorOperator.

Then, the canonical name of this operator's class is passed to Astor via the argument '-customop'.


***Custom Ingredient Selection Strategy***

Astor allows to integrate a custom strategy for selecting ingredients from the ingredient search space.
By default, Astor randomly takes one ingredient from a given ingredient scope (Application, Package or File).

A customize ingredient selection strategy class extends from abstract class fr.inria.astor.core.loop.spaces.ingredients. IngredientStrategy.

Then, the canonical name of this strategy's class is passed to Astor via the argument '-ingredientstrategy'.



Contacts
--------
matias.sebastian.martinez@usi.ch
martin.monperrus@univ-lille1.fr
