# DeepRepair approach

DeepRepair is an extension of jGenProg leveraging code similarity. The experiments about DeepRepair are described in

* [Sorting and Transforming Program Repair Ingredients via Deep Learning Code Similarities](https://arxiv.org/pdf/1707.04742.pdf) (Martin White, Michele Tufano, Matias Martinez, Martin Monperrus and Denys Poshyvanyk), Proceedings of SANER, 2019.


## Getting started

DeepRepair can be executed in the same manner than the others repair approaches from Astor.
We explain the command line in the [getting started](getting-starting.md) page.

To run DeepRepair, the execution mode argument must be `-mode deeprepair`.
Then, DeepRepair needs two additional arguments.
First, the argument `-clonegranularity` with two possible values: `type` or `executable`.
This argument will indicate the granularity of the clone detection (i.e., if DeepRepair searches for ingredients from similar `types` or from similar `executables` (methods)). 

Second, the learning directory `-learningdir <path>`, which must store the following files:

### Cluster of variable names file: `clustering.csv`

The file has a line per cluster.
For example `maxIterations,109,MAX_VALUE;epsilon;tableau`
The first word is an identifier `k`  (followed by a `,`),  then it appears the cluster id, and finally a list of words of the clusters, which has a similarity with `k`.
 Each word in the cluster is separated by `;`.
  
You can create your clusters of variables using the cluster algorithm you prefer.


### Code Similarity files: `<granularity>.distances.cvs`  and `<granularity.key>`

The file `executables.distances.csv` (or `types.distances.csv` according to the desired granularity) which contains a Matrix with the distances of two executables (of types, resp.)
The code element (executable or type) corresponding to column or row `i` can be found in the `i`-row of file `executables.key` (or `types.key`).

Those files can be generated using [AutoenCODE](https://github.com/micheletufano/AutoenCODE)


### Run DeepRepair on Defects4J

We evaluated DeepRepair on [Defects4J](https://github.com/rjust/defects4j).
The learning files for each subjects from Defects4J (computed using AutoenCODE) are publicly available [here](https://drive.google.com/open?id=1x00FIHGdiYbrFLJQskaRvU84kv1GBert).
Unzip the file and pass the folder of the buggy subject (example Math-70) using the mentioned argument  `-learningdir <path>`. 




