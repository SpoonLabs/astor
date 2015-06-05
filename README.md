If you use Astor, please cite this technical report:

Matias Martinez, Martin Monperrus. "ASTOR: Evolutionary Automatic Software Repair for Java". Technical Report hal-01075976, Inria; 2014. 

    @techreport{hal-01075976,
     TITLE = {{ASTOR: Evolutionary Automatic Software Repair for Java}},
     AUTHOR = {Martinez, Matias and Monperrus, Martin},
     URL = {https://hal.archives-ouvertes.fr/hal-01075976},
     INSTITUTION = {{Inria}},
     YEAR = {2014}
    }



Getting started
-------


To compile using maven:
First execute 'mvn clean' and  then 'mvn install'

Note that this project requires the use of Java 1.7; it does not build (on OS X 10.10.3) with Java 1.8.

See configuration file "configuration.properties" for configuring, for instance, the output folder location or jvm dir.  This is a required step; look for jvm4testexecution and be sure it is accurate for your environment.

**Evolutionary**

A) If condition repair:
Astor implements 3 state of the art automatic program repair.
The class to run them are:
fr.inria.main.evolution.MainIFjGenProg
fr.inria.main.evolution.MainIFMutation
fr.inria.main.evolution.MainIFPar


The option -help shows the usage of them.



B) jGenProg:
We provide an implementation of GenProg repair algorithm.
The class to run it is:
fr.inria.main.evolution.MainjGenProg

Additionally, the distribution contains a version of Apache commons Math with a real defect (reported in issue 280 https://issues.apache.org/jira/browse/MATH-280).
To run it using GenProg, type: java fr.inria.main.evolution.MainjGenProg -bug280

This implementations applies the GenProg's operators over 1) statements in a code block, 2) conditions in if and boucle.
After the execution, Astor writes in the output folder (property 'workingDirectory'in the mentioned file), a folder with all the variants that fulfill the goals i.e., repair the bugs.
Each variant folder contains the files that Astor have analyzed (and eventually modified). Additionally, it contains a file called 'Patch.xml' that summarized all changes done in the variant.


Contacts
--------
matias.martinez@inria.fr
martin.monperrus@univ-lille1.fr
