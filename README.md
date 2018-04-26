ASTOR: A Program Repair Library for Java
========================================

[![Build Status](https://travis-ci.org/SpoonLabs/astor.svg?branch=master)](https://travis-ci.org/SpoonLabs/astor)

Astor is an automatic software repair framework in Java for Java, done by Inria, the University of Lille, the University of Valenciennes, and KTH Royal Institute of Technology. 
Astor is an acronym standing for "Automatic Software Transformations fOr program Repair".

If you use Astor for academic purposes, please cite the appropriate publication:

Matias Martinez, Martin Monperrus, "[ASTOR: A Program Repair Library for Java](https://hal.archives-ouvertes.fr/hal-01321615/document)", in Proceedings of ISSTA, Demonstration Track, 2016.

    @inproceedings{martinez:hal-01321615,
     title = {ASTOR: A Program Repair Library for Java},
     author = {Martinez, Matias and Monperrus, Martin},
     booktitle = {Proceedings of ISSTA},
     year = {2016},
     doi = {10.1145/2931037.2948705},
    }

The very first paper about Astor is [ASTOR: Evolutionary Automatic Software Repair for Java (Arxiv 1410.6651, 2014)](https://arxiv.org/abs/1410.6651).

Contributing
------------

We do welcome bug fixes, features and new repair approaches as pull request. Welcome to Astor!

[Contributors](https://github.com/SpoonLabs/astor/graphs/contributors):

* Matias Martinez (University of Valenciennes) (Main contact point: matias.sebastian.martinez@gmail.com)
* Martin Monperrus (KTH Royal Institute of Technology) (martin.monperrus@csc.kth.se)
* Thomas Durieux (Inria)
* Martin G. White (William and Mary)
* Claire Le Goues (CMU)

jGenProg
-------

jGenProg is an implementation of GenProg. The experiments about jGenProg are described in:

* [Automatic Repair of Real Bugs in Java: A Large-Scale Experiment on the Defects4J Dataset](https://hal.archives-ouvertes.fr/hal-01387556/document) (Matias Martinez, Thomas Durieux, Romain Sommerard, Jifeng Xuan and Martin Monperrus), In Empirical Software Engineering, Springer Verlag, volume 22, 2017.
* [ASTOR: A Program Repair Library for Java](https://hal.archives-ouvertes.fr/hal-01321615/document) (Matias Martinez, Martin Monperrus) , in Proceedings of ISSTA, Demonstration Track, 2016.
* Open-science repository with jGenProg patches: <https://github.com/Spirals-Team/defects4j-repair/>

jKali
-----

jGenProg is an implementation of Kali. The experiments about jKali are described in:

* [Automatic Repair of Real Bugs in Java: A Large-Scale Experiment on the Defects4J Dataset](https://hal.archives-ouvertes.fr/hal-01387556/document) (Matias Martinez, Thomas Durieux, Romain Sommerard, Jifeng Xuan and Martin Monperrus), In Empirical Software Engineering, Springer Verlag, volume 22, 2017.
* [ASTOR: A Program Repair Library for Java](https://hal.archives-ouvertes.fr/hal-01321615/document) (Matias Martinez, Martin Monperrus) , in Proceedings of ISSTA, Demonstration Track, 2016.
* Open-science repository with jKali patches: <https://github.com/Spirals-Team/defects4j-repair/>


jMutRepair
----------

jGenProg is an implementation of mutation-based repair. The experiments about jMutRepair are described in

* [ASTOR: A Program Repair Library for Java](https://hal.archives-ouvertes.fr/hal-01321615/document) (Matias Martinez, Martin Monperrus), in Proceedings of ISSTA, Demonstration Track, 2016.
https://github.com/SpoonLabs/astor-experiments
* Open-science repository with jMutRepair patches: <https://github.com/SpoonLabs/astor-experiments/>

Cardumen
----------

Cardumen is a repair approach based on mined templates. The experiments about DeepRepair are described in

* [Open-ended Exploration of the Program Repair Search Space with Mined Templates: the Next 8935 Patches for Defects4J](https://arxiv.org/pdf/1712.03854) (Martin Monperrus, Matias Martinez), Arxiv 1712.03854, 2017
* Open-science repository with Cardumen patches: <https://github.com/SpoonLabs/astor-experiments/>


DeepRepair
----------

DeepRepair is an extension of jGenProg leveraging code similarity. The experiments about DeepRepair are described in

* [Sorting and Transforming Program Repair Ingredients via Deep Learning Code Similarities](https://arxiv.org/pdf/1707.04742.pdf) (Martin White, Michele Tufano, Matias Martinez, Martin Monperrus and Denys Poshyvanyk), Arxiv 1707.04742, Arxiv, 2017.

Astor in the literature
-----------------------

*

Usage of Astor in education
-------------------------------

* Astor is being used in a [course by the Chair of Software Engineering (Prof. Dr. rer. nat. Lars Grunske) at Humboldt-Universität zu Berlin](https://www.informatik.hu-berlin.de/de/forschung/gebiete/se/teaching/ss2018/se2/se2)
* Contact us or make a PR to add your course here :-)

Usage
------

* [Getting Started](https://github.com/SpoonLabs/astor/blob/master/docs/getting-starting.md)
* [How to extend Astor?](https://github.com/SpoonLabs/astor/blob/master/docs/extending-astor.md)

