Getting started
-------

If you use Astor, please cite this technical report:
Matias Martinez, Martin Monperrus. "ASTOR: Evolutionary Automatic Software Repair for Java". Technical Report hal-01075976, Inria; 2014. 

To compile using maven:
mvn install

See configuration file "configuration.properties" for configuring, for instance, the output folder location or jvm dir.

**Evolutionary**

A) If condition repair:
Astor implements 3 state of the art automatic program repair.
The class to run them are:
fr.inria.main.evolution.MainIFjGenProg
fr.inria.main.evolution.MainIFMutation
fr.inria.main.evolution.MainIFPar


The option -help shows the usage of them.

Additionally, the distribution contains a version of Apache commons Math with a real defect (reported in issue 280 https://issues.apache.org/jira/browse/MATH-280).
To run it using GenProg, type: java fr.inria.main.evolution.MainIFGenProg -bug280

Use Java 1.7

B) jGenProg:
We provide an implementation of GenProg repair algorithm.
The class to run it is:
fr.inria.main.evolution.MainjGenProg

This implementations applies the GenProg's operators over 1) statements in a code block, 2) conditions in if and boucle .
After the execution, Astor writes in the output folder (property 'workingDirectory'in the mentioned file), a folder with all the variants that fulfill the goals i.e., repair the bugs.
Each variant folder contains the files that Astor have analyzed (and eventually modified). Additionally, it contains a file called 'Patch.xml' that summarized all changes done in the variant.


** Mutation Repair ** 
To generate mutants, execute: 

    fr.inria.main.mutation.Main  (old MuTestingMutantGeneratorTest)

To verify rates of killed/alived mutants execute MuTestExecutorTest.java


Contacts
--------
matias.martinez@inria.fr
martin.monperrus@univ-lille1.fr
