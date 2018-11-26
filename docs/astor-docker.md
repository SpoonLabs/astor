# Running Astor on Docker

Astor can be executed on Docker thanks to a Docker image created by [Thomas Durieux](https://durieux.me).
The image allows to execute Astor on Defects4J bugs.

## Setup

First, install Docker ([doc](https://docs.docker.com/)).

Then, execute the command:

```
docker pull tdurieux/astor

```



## Running Astor on Docker

### Basic command

The shortest command to run Astor on a particular defect from Defects4J is: 
```
docker run -it --rm tdurieux/astor --id Chart_5 

```

The parameter `--id` (or `-i`) indicates the buggy version from Defects4J. 
In this example, the command triggers Astor on defect `Chart 5`. 
Other values can be, for instance, `Math_70`.

The result is printed on the screen:

```
Patch stats:

Patch 1
VARIANT_ID=34
TIME=16
VALIDATION=|true|0|2184|[]|
GENERATION=17
FOLDER_SOLUTION_CODE=/script/jGenProg_Defects4J_Math_70/./output_astor/AstorMain-Math-70//bin//variant-34
--Patch Hunk #1
OPERATOR=ReplaceOp

LOCATION=org.apache.commons.math.analysis.solvers.BisectionSolver

PATH=/script/jGenProg_Defects4J_Math_70/output_astor/AstorMain-Math-70/src/default/org/apache/commons/math/analysis/solvers/BisectionSolver.java

MODIFIED_FILE_PATH=/script/jGenProg_Defects4J_Math_70/./output_astor/AstorMain-Math-70//src//variant-34_f/org/apache/commons/math/analysis/solvers/BisectionSolver.java

LINE=72

SUSPICIOUNESS=1

MP_RANKING=0

ORIGINAL_CODE=return solve(min, max)

BUGGY_CODE_TYPE=CtReturnImpl|CtBlockImpl

PATCH_HUNK_CODE=return solve(f, min, max)

PATCH_HUNK_TYPE=CtReturnImpl|CtBlockImpl

INGREDIENT_SCOPE=LOCAL

INGREDIENT_PARENT=return solve(f, min, max)

PATCH_DIFF_ORIG=--- org/apache/commons/math/analysis/solvers/BisectionSolver.java
+++ org/apache/commons/math/analysis/solvers/BisectionSolver.java
@@ -68,8 +68,8 @@
 
 
 	public double solve(final org.apache.commons.math.analysis.UnivariateRealFunction f, double min, double max, double initial) throws 
	org.apache.commons.math.FunctionEvaluationException, org.apache.commons.math.MaxIterationsExceededException {
-		return solve(min, max);
+   return solve(f, min, max);
 	}
 
```


### Arguments

#### Output

Astor writes on disk the solution, if any. 
It stores the patched classes, the diff corresponding to the patch and meta-data.
Please, see in [this document](https://github.com/SpoonLabs/astor/blob/master/docs/getting-starting.md) to get more information about the output.

Now, to store the output obtained from Docker in your disk, add the command `-v`. For instance, the command is now as follows:

```
docker run -it --rm -v <path_to_store_results>:/results tdurieux/astor -i chart-5 --scope package
```

Replace the `<path_to_store_results>` with the folder you want to store the results.
Note that the folder must be previously created and included in the list of Shared Files of Docker (go to `Preferences... -> File Sharing`).

#### Scope

The argument `--scope` allows to specify the type of ingredient space. Three possible values: `file`, `package` and `global`. See our [doc  about ingredients](https://github.com/SpoonLabs/astor/blob/master/docs/extension_points.md#implemented-components-6) for more information.

```
docker run -it --rm -v /tmp:results tdurieux/astor -i chart-5 --scope package
```

#### Parameters:

It is also possible to change any configuration parameter of Astor using the argument named `--parameters`.

```
docker run -it --rm -v /tmp/results:/results tdurieux/astor -i chart-5 --scope package --parameters stopfirst:true:maxGeneration:100
```

The value of `--parameters` has a following format: 
`<property_name_1>:<property_value_1>:<property_name_2>:<property_value_2>:...<property_name_n>:<property_value_n>`

For example, in the previous command `--parameters stopfirst:true:maxGeneration:100`, we have that `stopfirst`is the first parameter which value is `true`, and `maxGeneration` is the second parameter with value `100`.


The list with the most important arguments can be found [here](https://github.com/SpoonLabs/astor/blob/master/docs/arguments.md).



