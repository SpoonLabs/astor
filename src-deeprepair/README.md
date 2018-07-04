ASTOR: A Program Repair Library for Java
========================================


DeepRepair by Astor
----------

DeepRepair is an extension of jGenProg leveraging code similarity. The experiments about DeepRepair are described in

* [Sorting and Transforming Program Repair Ingredients via Deep Learning Code Similarities](https://arxiv.org/pdf/1707.04742.pdf) (Martin White, Michele Tufano, Matias Martinez, Martin Monperrus and Denys Poshyvanyk), Arxiv 1707.04742, 2017.


To run DeepRepair is necessary to previously generate 3 files per bug to repair:

1. `executables.distances.csv` (or `types.distances.csv` according to the desired granularity) which contains a Matrix the distances of two executables (of types, resp.)

2. `executables.key` (or `types.key`), which contains the name of each executable/type of the distance Matrix. 
 
3. `clustering.csv` which contains the cluster of identifiers.

To generate those files, check the tool [AutoenCODE](https://github.com/micheletufano/AutoenCODE)

