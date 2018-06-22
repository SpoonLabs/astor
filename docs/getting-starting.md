# Getting started Astor

## jGenProg

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
     
Then the main command (note that the "location" argument is mandatory, and must be an absolute path):

     java -cp $(cat /tmp/astor-classpath.txt):target/classes fr.inria.main.evolution.AstorMain -mode jgenprog -srcjavafolder /src/java/ -srctestfolder /src/test/  -binjavafolder /target/classes/ -bintestfolder  /target/test-classes/ -location /home/user/astor/examples/Math-issue-280/ -dependencies examples/Math-issue-280/lib


Output: Astor uses the standard output to print the solutions (i.e., the patches code), if any. 
Astor writes in the output folder (property `workingDirectory` in the mentioned command line), a folder with all the variants that fulfill the goals i.e., repair the bugs. Example output:

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

Each variant folder contains the files that Astor has analyzed (and eventually modified). Additionally, it contains a file called `patch.diff` that summarized all changes done in the variant.
The summary of the execution is also printed on the screen at the end of the execution. If there is at least one solution, it prints “Solution found” and then it lists the program variants that are solution i.e., they fixed versions of the program. Then, if you go to the folder to each of those variants, the file patch appears, which summarizes the changes done for repairing the bug. In other words, the file `patch.diff` is only present if the variant is a valid solution (fixes the failing test and no regression).
If Astor does not find any solution in the execution, it prints at the screen something like “Not solution found”. 


Moreover, Astor saves the patched version of the program on disk.
The Astor's output is located in folder "./output_astor". You can change it through command line argument '-out'. There Astor writes a JSON file ('astor_output.json') which summarizes the information of each patch found (location, code modified, etc.) and some statistics.
Inside the folder "/src/" Astor stores the source code of the solutions that it found.

Folder “default” contains the original program, without any modification. It's a sanity check, it’s the output of spoon without applying any processor over the spoon model of the application under repair.

Each folder "variant-x" is a valid solution to the repair problem (passes all tests). The "x" corresponds to the id (unique identifier) assigned to a variant. There is an command line argument `saveall` that allows you to save all variants that Astor generates, even they are not solution.

## jKali

For executing Astor in jKali mode:

    java  -cp astor.jar fr.inria.main.evolution.AstorMain -mode jkali -location <>......

## jMutRepair

For executing Astor in jMutRepair mode:

    java  -cp astor.jar fr.inria.main.evolution.AstorMain -mode jMutRepair -location <>......

# Command line arguments

     -mode jgenprog|jkali|jMutRepair|... 

    -location "absolute location of the project to repair" (**MANDATORY**)

    -dependencies "folder with the dependencies of the application to repair" 

    -failing "failing test case" (if there are several failing test case, give them separated by the classpath separator (: in linux/mac  and ; in windows)
        
    -package "package to manipulate" (only the statements from this package are manipulated to find a patch)

    -jvm4testexecution "Path to the Java VM used by Astor to execute the validation (e.g., running test cases). It must reference to the `bin` subfolder" 

    -javacompliancelevel "compliance level of source code e.g. 5"

    -stopfirst "indicates whether Astor stops the search after finding the first solution." (true/false)

    -flthreshold "minimun suspicious value for fault localization e.g. 0.1"
    
    -srcjavafolder "source code folder"
    
    -srctestfolder "test source code folder"
    
    -binjavafolder "class folder"
    
    -bintestfolder "test class folder" 
    
    
### Command line arguments example Math-70 from Defects4J: 
	
	-location <PATH_TO_ASTOR_FOLDER>/examples/math_70 
  	
    -mode statement
	
	-package  org.apache.commons 
	
	-jvm4testexecution "<PATH_TO_JVM_FOLDER>"/bin/ 
	
	-failing org.apache.commons.math.analysis.solvers.BisectionSolverTest
	
	-srcjavafolder /src/java/
	
	-srctestfolder /src/test/
	
	-binjavafolder /target/classes
	
	-bintestfolder /target/test-classes 
	
	-flthreshold 0.5 
	 
	-stopfirst true 
	
	-dependencies <PATH_TO_ASTOR_FOLDER>examples/libs/junit-4.4.jar, 

Note that `-location` argument has a absolute path to the project to repair, and -dependency has the absolute path to the libraries or to the folder that contains them.
However, note that the folders that specify the structure of the project under repair (`-srcjavafolder`, `-srctestfolder`, `-binjavafolder` and `-bintestfolder`) are relative.


# Notes about running Astor over [Defects4J](https://github.com/rjust/defects4j) bugs.

Astor needs to be executed over a JVM 8+. By default, Astor uses the JVM specified on the `JAVA_HOME` variable to run the test cases for validating candidate patches. However, Defects4J subjects needs to be executed using a JMV 7 (according to the official documentation).
For example, running bug Math-70 from Defects4J on a JVM 8 could produce two failing/errored test cases:

  	testMath280(org.apache.commons.math.distribution.NormalDistributionTest)
	testMinpackMeyer(org.apache.commons.math.estimation.MinpackTest)

The first one (`testMath280`) is the test that expose the bug Math-70. It's correct that it fails. However, the second one (`testMinpackMeyer`) fails due to the JVM and it must not fail. It's a platform-related error.
As conclusion, using a java VM 8+ could produce that Astor does not find patches for bugs that must be repaired by the tool.

As solution, Astor provides an argument (`-jvm4testexecution`) to specify the location of the virtual machine that will be used to run the test cases. In the case of trying to repair Defects4J bugs it must be a JVM 7, otherwise the result of test cases could not be the expected (i.e., having more failing test cases) and, consequently, patches could not be found.







 


