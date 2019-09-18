
# Astor parameters

It is possible to change any configuration parameter of Astor using the command line argument named `-parameters`.

The value of -parameters has a following format: <property_name_1>:<property_value_1>:<property_name_2>:<property_value_2>:...<property_name_n>:<property_value_n>

For example, in the previous command `-parameters stopfirst:true:maxGeneration:100`, we have that `stopfirst` is the first parameter which value is `true`, and `maxGeneration` is the second parameter with value `100`.

All the default values are stored in the file  [astor.properties](https://github.com/SpoonLabs/astor/blob/master/src/main/resources/astor.properties).

## List of Parameters (to be used exclusivelly via the parameter -parameters):


### Description of project under repair

`javacompliancelevel` indicates the Java compliance level. Default 8.

`location` root folder where the program under repair is located.

`srcjavafolder` indicates the folder inside the project under repair where the source codes of the program are located. Default   src/main/java

`srctestfolder` indicates the folder inside the project under repair where the source codes of the test cases are located. Default   src/test/java

`binjavafolder` indicates the folder inside the project under repair where the bytecode of the app are located. Default   /target/classes

`bintestfolder` indicates the folder inside the project under repair where the bytecodes of the test cases are located. Default  /target/test-classes

`resourcesfolder`  has the list of the resources folder, separated by ':'. /src/main/resources:/src/test/resources:


###  Execution parameters

`Mode` is the name of the approach to execute (by default `jgenprog`). Other approaches are: jmutrepair, jkali, cardumen. 


`maxtime` in the max execution time (in minutes). Default 60

`maxGeneration` is the max number of generation executed. (Default 200)

`population` is the size of the population (number of variants for generation ). Default 1


`workingDirectory` indicates the working directory (i.e., there temp files are stored). Default value `./output_astor/`

`stopfirst` indicates if Astor stops when it finds the first solution. Default false

`maxnumbersolutions` indicates the max number of solutions. Default1000000

`maxsuspcandidates` is the Max number of suspicious analyzed. Default 1000


### Fault Locatization

`faultlocalization` indicates the fault localization approach to execute. Default  gzoltar

`maxmodificationpoints` is the Max Number of modification points that Astor can modify. Default 1000

`flthreshold` is the fault localization threshold. Default 0.1

`regressionforfaultlocalization` =true

`packageToInstrument` indicates the package that gZoltar instruments, that means, It only considers suspicious statements from classes included in that package.

`skipfaultlocalization` indicates if Astor uses fault localization to reduce the number or suspicious, or all statement are uniformly suspicious. Default  false

`skipfitnessinitialpopulation` indicates if Astor skips to apply the fitness function in the initial population. Default false




### Repair configuration


`scope` indicates the ingredient. Default  package

`modificationpointnavigation` strategy to navigate the modification point space. Possible values are inorder, random, weight,sequence. Default weight.

`mutationrate` indicates the probability that a selected modification point be modified. Default  1 




### Validation

`validation` indicates the mechanism to validate a program variant i.e., execute the test cases. It can be 1) process (Astor launches a process for the test)|2)thread (Astor launches a thread for the test) |3) local (Astor executes the test in the current thread). Default `process`


`tmax1`is the max time for evaluation the failing test cases (in milliseconds). Default 10000

`tmax2`is the max time for evaluation the test suite i.e., regression testing (in milliseconds). Default 600000

`overridemaxtime` indicates if the tmax values are calculated according to an estimation of the execution time of the tests. Default true

`testbystep` indicates if each test case is executed separatelly (e.g., each in one process or thread). Default  false

`ignoredTestCases`  has the name of test cases to Ignore (separated using File.pathSeparator (': in Unix/Linux/Solaris), please, replace it by ';' if you use Windows)




### Output
`savesolution` indicates if program variants that are solutions are stored on disk. Default true

`saveall` indicates if saves all program variants generated, even they are not solution. Default false

`multipointmodification` indicates if a generation can modify more than a modification point.  Default false


`pvariantfoldername` has the prefix of program variant folder name. Default   variant-

`loglevel` indicates the logging level. Default  INFO

`logpatternlayout `  indicates the loggin pattern. Default   [%-5p] %l - %m%n

`jsonoutputname` indicates the name of output JSON file with statistics. Default   astor_output


