# List of extension points of Astor

The architecture and extension points provided by Astor framework are described in:

* [Astor: Exploring the Design Space of Generate-and-Validate Program Repair beyond GenProg](https://arxiv.org/abs/1802.03365) (Matias Martinez, Martin Monperrus), Journal of Systems and Software, 2019.


Fault Localization (EP\_FL) 
---------------------------

#### Implemented components

-   GZoltar: use of third-party library GZoltar.

-   CoCoSpoon: use of third-party library CoCoSpoon.

-   *Custom*: name of class that implements interface `FaultLocalizationStrategy`.

#### Command line option: 

-faultlocalization

### Description

This extension point allows to specify the fault localization algorithm
that Astor executes to obtain the buggy suspicious locations. 
The extension point takes as input
the program under repair and the test suite, and produces as output a
list of program locations, each one with a suspicious value. The
suspicious value associated to location $l$ goes between 0 (very low
probability that $l$ is buggy) and 1 (very high probability that $l$ is
buggy). New fault localization techniques such that PRFL presented by
Zhang et al. [@Zhang et al. 2017] can be implemented in this extension point.


### Implementation details, Inputs and outputs

To implement the extension point, its necessary to create a class that implements the interface `fr.inria.astor.core.solutionsearch.extensionFaultLocalizationStrategy`.
Then, the class must implement the method:

```
public FaultLocalizationResult searchSuspicious(ProjectRepairFacade projectToRepair) throws Exception;
``` 


The input `ProjectRepairFacade projectToRepair`  stores all the information of the project to repair (e.g., path to sources, dependencies, etc).

The output is an object of class `FaultLocalizationResult`, which contains a list of all suspicious locations.




Granularity of Modification points (EP\_MPG) 
--------------------------------------------

### Implemented components

-   Statements: each modification point corresponds to a *statement*. Repair operators generate code at the level of *statements*.

-   Expressions: each modification point corresponds to an *expression*. Repair operators generate code at the level of *expressions*.

-   Logical-relational-operators: Modification points target to binary expression whose operators are logical (AND, OR) or relational  (e.g., $>,==$).

-   *Custom*: name of class that implements interface `TargetElementProcessor`.


#### Command line option 

-targetelementprocessor

### Description

The extension point EP\_MPG allows to specify the *granularity* of code
that is manipulated by a repair approach over Astor. The granularity
impacts two components of Astor. First, it impacts the program
representation: Astor creates modifications points only for suspicious
code elements of a given granularity. Second, it impacts the repair operator space:
a repair operator takes as input code of a given granularity and
generates a modified version of that piece of code. For example, the
approach jGenProg manipulates *statements*, i.e., the modification
points refer to statements and it has 3 repair operators: add, remove
and replace of statements. Differently, jMutation manipulates binary and
unary expressions using repair operators that change binary and unary
operators.


### Implementation details, Inputs and outputs

To implement the extension point, its necessary to create a class that extends the abstract class `fr.inria.astor.core.manipulation.filters.TargetElementProcessor`.
Then, it is necessary to override the method that visit the elements that the new approach targets.
For example, if the approach targets Literals (i.e., a modification point is a literal) then the class must contain the overrided method:

```
	@Override
	public void process(CtLiteral element) {
		add(element);
	}
``` 

Finally, the method `process` must call the method `add` (defined on the abstract class `TargetElementProcessor`) to store the elements that it is interested in. (In the example, every literal is stored) 



Navigation Strategy (EP\_NS)
----------------------------

### Implemented components

-   Exhaustive: complete navigation of the search space.

-   Selective: partial navigation of search space guided, by default, by  random steps.

-   Evolutionary: navigation of the search space using genetic   algorithm.

-   *Custom*: name of class that extends class `AstorCoreEngine`.


#### Command line option: 

-customengine

### Description

The extension point EP\_NS allows to define a strategy for navigating
the search space. Astor provides three navigation strategies:
*exhaustive*, *selective* and *evolutionary*.

#### Exhaustive navigation

This strategy *exhaustively* navigates the search space, that is, all
the candidate solutions are considered and validated. An approach that
carries out an exhaustive search visits *every* modification point
$mp_i$ and applies to it *every* repair operator $op_j$ from the repair
operator space. For each combination $mp_i$ and $op_j$, the approach
generates zero or more candidates patches. Then, for each synthesized
$patch_i$ the approach applies it into the program under repair $P$ and
then executes the validation process.

#### Selective navigation

The selective navigation visits a portion of the search space. This
strategy is necessary when the search space is too large to be
exhaustively navigated. On each step of the navigation, it uses two
strategies for determining where to modify (i.e., modification points)
and how (i.e., repair operators). By default, the selective navigation
uses weighted random for selecting modification points, where the weight
is the suspiciousness value, and uniform random for selecting operators.
Those strategies can be customized using extension points EP\_MPS
 and EP\_OS, respectively.

#### Evolutionary navigation 

Astor frameworks also provides the Genetic Programming [@Koza, 1992]
technique for navigating the solution search space. This technique was
introduced in the domain of the automatic program repair by JAFF
[@ArcuriEvolutionary] and GenProg [@Weimer et al. 2009]. The idea is to evolve a
buggy program by applying repair operators to arrive to a modified
version that does not have the bug. In Astor, it is implemented as
follows: one considers an initial population of size $S$ of program
variants and one evolves them across $n$ generations. On each generation
$i$, Astor first creates, for each program variant $pv$, an offspring
$pvo$ (i.e., a new program variant) and applies, with a given
probability, repair operators to one or more modification points from
$pvo$. Then, it applies, with a given probability, the crossover
operator between two program variants which involves to exchange one or
more modification points. Astor finally evaluates each variant (i.e.,
the patch synthesized from the different operators applied) and then
chooses the $S$ variants with best fitness values  to be part of the next generation.


### Implementation details, Inputs and outputs

To implement a new strategy of navigation of the search space (which it involves the creation of a new approach) it is necessary to create a class that extends `fr.inria.astor.core.solutionsearch.AstorCoreEngine`

Then, that class must override the method `public void startEvolution() throws Exception` to implement the desired navigation strategy.
Note that the suspicious space and the operator spaces are built according to the values of the extension point EP\_FL and EP\_OD, respectively.



Selection of suspicious modification points (EP\_MPS)
-----------------------------------------------------

### Implemented components

-   Uniform-random: every modification point has the same probability to    be selected and later changed by an operator.

-   Weighted-random: the probability of changed of a modification point   depends on the suspiciousness of the pointed code.

-   Sequential: modification points are changes according to the   suspiciousness value, in decreasing order.

-   *Custom*: name of class that extends class  `SuspiciousNavigationStrategy`.


#### Command line option: 

-modificationpointnavigation

### Description

The extension point EP\_MPS allows to specify the strategy to navigate
the search space of suspicious components represented by modification
points. This extension point is invoked in every iteration of the
navigation loop: the strategy selects the
modification points where the repair algorithm will apply repair
operators.


### Implementation details, Inputs and outputs


To implement the extension point, its necessary to create a class that implements the interface `fr.inria.astor.core.solutionsearch.navigationuspiciousNavigationStrategy`.
Then, the class must implement the method:

```
List<ModificationPoint> getSortedModificationPointsList(ProgramVariant variant);
``` 


The input of `List<ModificationPoint> modificationPoints`  is the list of modification points to be navigated in the order that the method ` getSortedModificationPointsList` defines.

The output is a list of the modification points `FaultLocalizationResult`, which contains the points sorted according to a given criterion.





Operator spaces definition (EP\_OD)
-----------------------------------

### Implemented components

-   IRR-Statements: insertion, removement and replacement of statements.

-   Relational-Logical-operators: change of unary operators, and logical and relational binary operators.

-   Suppression: suppression of statement, change of if conditions by    True or False value, insertion of remove statement.

-   R-expression: replacement of expression by another expression.

-   *Custom*: name of class that extends class `OperatorSpace`.


#### Command line option:

-operatorspace

### Description

After a modification point is selected, Astor selects a repair operator
from the *repair operator space* to apply into that point. Astor
provides the extension point EP\_OD for specifying the repair operator
space used by a repair approach built on Astor. 
The operators space configuration depends on the repair strategy. For
example, jGenProg has 3 operators (insert, replace and remove statement)
whereas Cardumen has one (replace expression).



### Implementation details, Inputs and outputs


To implement the extension point, its necessary to create a class that extends the abstract class  `fr.inria.astor.core.solutionsearch.spaces.operators.OperatorSpace`.
That class has a field `List<AstorOperator> operators` that corresponds to the operator space.
As initially it is empty, the class that we create can add operators from its constructor by calling the method `public void register(AstorOperator operator)`.
The class `OperatorSpace` provides a getter of the mentioned field `List<AstorOperator> operators`.




Selection of repair operator (EP\_OS)
-------------------------------------

### Implemented components

-   Uniform-random: every repair operator has the same probability of be   chosen to modify a modification point.

-   Weighted-random: selection of operator based on non-uniform    probability distribution over the repair operators.

-   *Custom*: name of class that extends `OperatorSelectionStrategy`.

#### Command line option: 

-opselectionstrategy

### Description

The extension point EP\_OS allows Astor's users to specify a strategy to
select, given a modification point $mp$, one operator from the operator
space. By default, Astor provides a strategy that applies uniform random
selection and it does not depend on the selected $mp$. This strategy is
used by approaches that uses selective navigation of the search space
such as jGenProg. This extension point is useful for
implementing strategies based on probabilistic models such those
presented by [@Martinez et Monperrus,  2013]. In that work, several repair models are
defined from different sets of bug fix commits, where each model is
composed by repair operators and their associated probabilities
calculated based on changes found in the commits.

### Implementation details, Inputs and outputs



To implement the extension point, its necessary to create a class that extends the abstract class  `fr.inria.astor.core.solutionsearch.spaces.operators.OperatorSelectionStrategy`.
The class has a field, `protected OperatorSpace operatorSpace`, passed as argument in the constructor, which corresponds to the operator space i.e., all the operators available. Then, the strategy will select operators from there. 

The strategy must implement two methods:


 ```
	public abstract AstorOperator getNextOperator(SuspiciousModificationPoint modificationPoint);
```

which has an input a modification point,
and as output it retrieves an operator to apply to that point.

```
	public abstract AstorOperator getNextOperator();
``` 
which returns one operator, according to the strategy implemented. Here, the operator selection does not consider the modification point where the operator will be applied.


Ingredient pool definition (EP\_IPD)
------------------------------------

### Implemented components

-   File: pool with ingredients written in the same file where the patch    is applied.

-   Package: pool with ingredients written in the same package where the   patch is applied.

-   Global: pool with all ingredients from the application under repair.

-   *Custom*: name of class that extends class `AstorIngredientSpace`.


#### Command line option: 

-scope


### Description

The ingredient pool contains all pieces of code that an ingredient-based
repair approach can use for synthesizing a patch. The extension point EP\_IPD allows
to customize the creation of the ingredient pool. Astor provides three
methods of building an ingredient pool: file, package and global scopes.
For synthesizing a candidate patch to be applied in the modification
point $mp$, when "file" scope is used, the approach selects ingredients
from the same file where the patch will be applied (i.e., $mp$). When
the scope is "package", the ingredient pool is conformed by all code
from the package that contains the modification point $mp$. When the
scope is "global", the ingredient pool has all code from the program
under repair. The "file" ingredient pool is smaller than the
package-one, which is itself smaller than the global one.


### Implementation details, Inputs and outputs


To implement the extension point, its necessary to create a class that implements the interface `fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientPool`.
A pool in Astor has a structure of a Map, i.e., there are keys and values related to one key.
A key can be, for instance, the location of an ingredient in an application.

IngredientPool is a parametrized interface, which parameters are:

```
 Q type of the element to modify using an ingredient from this space.
       
K  key of the ingredient, example, location of the ingredient according
       
 I ingredient (e.g., a= b+c;) 

 T type of the ingredient (e.g., a statement, if, a while, etc)
```


Then, the class under construction must implement the methods:

```
public void defineSpace(ProgramVariant variant);
``` 

which creates the Space using the classes from a Variant.


``` 
List<I> getIngredients(Q elementToModify);
``` 

which returns the list of ingredients from location.


``` 
List<I> getIngredients(Q elementToModify, T type);
``` 

returns the list of ingredient of a given type from a location.

``` 
public void setIngredients(Q elementToModify, List<I> ingredients);	
``` 

set the lists of ingredients in the location.


``` 
public K calculateLocation(Q elementToModify);
``` 
	
returns, for a given piece of code Q, the location according to the scope.
For instance, if the scope of the Space is file, it returns the file name that contains Q, 
if the scope is package, it returns the package where Q is located.



``` 
public List<K> getLocations();
``` 

gets all the locations for a given scope.
For instance, if the scope is 'Package' it will return all package with ingredients.
If the scope is method, it returns all methods with at least one ingredient.


``` 
public T getType(I element);
``` 
returns the type of an ingredient.





Selection of ingredients (EP\_IS) 
---------------------------------

### Implemented components

-   Uniform-random: ingredient randomly chosen from ingredient pool.

-   Code-similarity-based: ingredient chosen from similar methods to the   buggy method.

-   Name-probability-based: ingredient chosen based on the frequency of   its variable's names.

-   *Custom*: name of class that extends class `IngredientSearchStrategy`.


#### Command line option:

-ingredientstrategy

### Description

The extension point EP\_IS allows to specify the strategy that an
ingredient-based repair approach from Astor uses for selecting an
ingredient from the ingredient pool. 
Between the implementations of this point provided by Astor, 
there is one,  used by default by jGenProg,which executes uniform random selection for selecting an ingredient from a pool built
given a scope. 
Another, defined for DeepRepair approach, prioritizes ingredients that come from methods which are
similar to the buggy method.


### Implementation details, Inputs and outputs


To implement the extension point, its necessary to create a class that extends the abstract class  `fr.inria.astor.core.solutionsearch.spaces.ingredients.IngredientSearchStrategy`.
That class has a field `IngredientPool ingredientPool` that corresponds to the ingredient pool.
When Astor instantiates the selection strategy,  it passes the ingredient pool to it (i.e., the constructor must receive an `IngredientPool`).
Then, the strategy must implement the method:
```
public abstract Ingredient getFixIngredient(ModificationPoint modificationPoint, 
			AstorOperator operationType);
```

The inputs of `getFixIngredient` are two: a) the modification point where the candidate patch under construction will be located, b) the operator used to synthesize the patch (For some approaches such as jGenProg, Astor considers the type of the operator for querying the ingredient pool).

The output is an ingredient retrieved from the pool, which will be used to synthesize a patch at the modification point given as parameter.




Ingredient transformation (EP\_IT)
----------------------------------

### Implemented components

-   No-transformation: ingredients are not transformed.

-   Random-variable-replacement: out-of-scope variables from an  ingredients are replaced by randomly chosen in-scope variables.

-   Name-cluster-based: out-of-scope variables from an ingredients are  replaced by similar named in-scope variables.

-   Name-probability-based: out-of-scope variables from an ingredients    are replaced by in-scope variable based on the frequency of  variable's names.

-   *Custom*: name of class that extends class  `IngredientTransformationStrategy`.

#### Command line option:

-ingredienttransformstrategy

### Description

The extension point EP\_IT allows to specify the strategy used for
transforming ingredients selected from the pool. Astor provides four
implementation of this extension point. For instance, the strategy
defined for DeepRepair approach replaces each out-of-scope variable form
the ingredient by one variable in the scope of the modification points.
The selection of that variable is based on a cluster of variable names,
which each cluster variable having semantically related names
[@white et al. 2018]. Cardumen uses a probabilistic model for selecting the
most frequent variables names to be used in the patch. On the contrary,
jGenProg, as also the original GenProg, does not transform any
ingredient.

### Implementation details, Inputs and outputs


To implement the extension point, its necessary to create a class that implements the interface `fr.inria.astor.core.solutionsearch.spaces.ingredients.transformations.IngredientTransformationStrategy`.
There is one method to implement:

```
public  List<Ingredient> transform(ModificationPoint modificationPoint, Ingredient ingredient);
```

It takes two inputs: a) the modification point where the candidate patch will be located, b) the ingredient used to synthesize the patch

The output is a list of transformed ingredients, derived from that one received as argument.


Candidate Patch Validation (EP\_PV)
-----------------------------------

### Implemented components

-   Test-suite: original test-suite used for validating a candidate    patch.

-   Augmented-test-suite: new test cases are generated for augmented the   original test suite used for validation.

-   *Custom*: name of class that extends class  `ProgramVariantValidator`.

#### Command line option: 

-validation


### Description

The extension point EP\_PV executes the validation process of a patch. Astor framework provides to test-suite
based repair approaches a validation process that runs the test-suite on
the patched program.

Another strategy implemented in Astor was called MinImpact
[@Zu et al., 2018], proposed to alleviate the problem of patch
overfitting [@Smith et al., 2015]. MinImpact uses additional automatically
generated test cases to further check the correctness of a list of
generated test-suite adequate patches and returns the one with the
highest probability of being correct. MinImpact implements the extension
point EP\_PV by generating new test cases (i.e., inputs and outputs)
over the buggy suspicious files, using Evosuite [@Gordon et Arcuri] as
test-suite generation tool. Once generated the new test cases, MinImpact
executes them over the patched version. The intuition is that the more
additional test cases fail on a tentatively patched program, the more
likely the corresponding patch is an overfitting patch. MinImpact then
sorts the generated patches by prioritizing those with less failures
over the new tests.

Moreover, this extension point can be used to measure other functional
and not functional properties beyond the verification of the program
correctness. For example, instead of focusing on automated software
repair, an approach built over Astor could target on minimizing the
energy computation. For that, that approach would extend this extension
point for measuring the consumption of a program variant.


### Implementation details, Inputs and outputs


To implement the extension point, its necessary to create a class that extends the abstract class  `fr.inria.astor.core.validation.processbased.ProgramVariantValidator`.

The class must implement the following method `validate`: 

```
public abstract VariantValidationResult validate(ProgramVariant variant, ProjectRepairFacade projectFacade);
```

As input, the validation strategy implemented in method `validate` receives two parameters: a) `ProgramVariant variant` the program variant to validate,  b) `ProjectRepairFacade projectFacade`, which contains all information related to the project under repair (classpath, location of sources, bytecodes, dependencies, etc.)

As outputs the method `validate` must return an object that implements the interface `fr.inria.astor.core.entities.VariantValidationResult`.
That interface was one method `public boolean isSuccessful();` which indicates if the program is valid (TRUE) or invalid (FALSE) according to the validation done by the validation strategy that extends `ProgramVariantValidator`.





Fitness Function for evaluating candidate (EP\_FF)
--------------------------------------------------

### Implemented components

-   Number-failing-tests: the fitness is the number of failing test    cases. Lower is better. Zero means the patch is a test-suite   adequate patch.

-   *Custom*: name of class that implements `FitnessFunction`.

#### Command line option: 

-fitnessfunction

### Description

The extension point EP\_FF allows to specify the *fitness function*,
which consumes the output from the validation process of a program
variant $pv$ and assigns to $pv$ its fitness value. Astor provides an
implementation of this extension points which considers as fitness value
the number of failing test cases (low is better).

On evolutionary approaches such as jGenProg, this fitness function
guides the evolution of a population of program variants throughout a
number of generations. In a given generation $t$, those variant with
better fitness will be part of the population at generation $t+1$. On
the contrary, on selective or exhaustive approaches, the fitness
function is only used to determined if a patched program is solution o
not.

### Implementation details, Inputs and outputs

To implement the extension point, its necessary to create a class that implements the interface `fr.inria.astor.core.solutionsearch.population.FitnessFunction`.
There are two methods to implement:

```
	public double calculateFitnessValue(VariantValidationResult validationResult);
```
which retrieves the results of a validation process (a object `VariantValidationResult`, see explanation of Extension point EP\_PV) and returns a numerical value (a double).


```
	public double getWorstMaxFitnessValue();
```
which returns the max fitness value (i.e., the worst value). This is used when, for instance, a validation process of a program variant fails (e.g., an infinite loop that avoid to executing the test cases) so, this worst value is assigned to that program variant.


Solution prioritization (EP\_SP)
--------------------------------

### Implemented components

-   Chronological: generated valid patches are printing chronological   order, according with the time they were discovered.

-   Less-regression: patches are presented according to the number of   failing cases from those generated test cases, in ascending order.

-   *Custom*: name of class that implements  `SolutionVariantSortCriterion`


#### Command line option:

-patchprioritization

### Description

The extension point EP\_SP allows to specify a method for sorting the
discovered valid patches. By default, approaches over Astor present
patches sorted by time of discovery in the search space. Astor proposes
an implementation of this point named *Less-regression*. The strategy,
defined by MinImpact [@Zu et al. 2018], sorts the *original
test-suite* adequate patches with the goal of minimizing the
introduction of regression faults, i.e., the approach prioritizes the
patches with *less failing test cases* from those tests automatically
generated during the validation process.


### Implementation details, Inputs and outputs

To implement the extension point, its necessary to create a class that implements the interface `fr.inria.astor.core.solutionsearch.extension.SolutionVariantSortCriterion`.
There is one method to implement:

```
public List<ProgramVariant> priorize(List<ProgramVariant> patches);
```

which receives as input a list of patches (i.e., program variant that are solutions) and returns as output a list of the patches sorted by a given criterion.



Output Result (EP\_OR)
--------------------------------

### Implemented components
-	PatchJSONStandarOutpu: JSON format

-	StandardOutputReport

-   *Custom*: name of class that implements  `ReportResults`

#### Command line option: 

-outputresult



### Implementation details, Inputs and outputs

To implement the extension point, its necessary to create a class that implements the interface `fr.inria.astor.core.output.ReportResults`.
There is one method to implement:
```
public Object produceOutput(List<PatchStat> statsForPatches, Map<GeneralStatEnum, Object> generalStats, String output);
```
which returns takes tree arguments as input: a) list of patches found (it can be empty), b) map with the statistics, c) path to the directory where Astor saves the results.

The output is an Object that represents the output in a given format. Example, one implementation of `ReportResults`, the class ` PatchJSONStandarOutput` returns an JSON object which represents the output (patches + stats) in JSON format.


