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

* Matias Martinez (University of Valenciennes) (project leader: matias.sebastian.martinez@gmail.com)
* Martin Monperrus (KTH Royal Institute of Technology) (martin.monperrus@csc.kth.se)
* Thomas Durieux (Inria)
* Martin G. White (William and Mary)
* Claire Le Goues (CMU)
* Simon Urli (Inria)

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

jMutRepair is an implementation of mutation-based repair. The experiments about jMutRepair are described in

* [ASTOR: A Program Repair Library for Java](https://hal.archives-ouvertes.fr/hal-01321615/document) (Matias Martinez, Martin Monperrus), in Proceedings of ISSTA, Demonstration Track, 2016.
https://github.com/SpoonLabs/astor-experiments
* Open-science repository with jMutRepair patches: <https://github.com/SpoonLabs/astor-experiments/>

Cardumen
----------

Cardumen is a repair approach based on mined templates. The experiments about Cardumen are described in

* [Ultra-Large Repair Search Space with Automatically Mined Templates: the Cardumen Mode of Astor ](https://arxiv.org/pdf/1712.03854v2), Proceedings of the 10th International Symposium on Search-Based Software Engineering, 2018
* [Open-ended Exploration of the Program Repair Search Space with Mined Templates: the Next 8935 Patches for Defects4J](https://arxiv.org/pdf/1712.03854v1) (Martin Monperrus, Matias Martinez), Arxiv 1712.03854, 2017
* Open-science repository with Cardumen patches: <https://github.com/SpoonLabs/astor-experiments/>


DeepRepair
----------

DeepRepair is an extension of jGenProg leveraging code similarity. The experiments about DeepRepair are described in

* [Sorting and Transforming Program Repair Ingredients via Deep Learning Code Similarities](https://arxiv.org/pdf/1707.04742.pdf) (Martin White, Michele Tufano, Matias Martinez, Martin Monperrus and Denys Poshyvanyk), Arxiv 1707.04742, 2017.


Usage
------

* [Getting Started](https://github.com/SpoonLabs/astor/blob/master/docs/getting-starting.md)
* [How to extend Astor?](https://github.com/SpoonLabs/astor/blob/master/docs/extending_astor.md)
* [List of Extension points](https://github.com/SpoonLabs/astor/blob/master/docs/extension-points.md)



Usage of Astor in education
-------------------------------

* Astor is being used in a [course by the Chair of Software Engineering (Prof. Dr. rer. nat. Lars Grunske) at Humboldt-Universität zu Berlin](https://www.informatik.hu-berlin.de/de/forschung/gebiete/se/teaching/ss2018/se2/se2)
* Contact us or make a PR to add your course here :-)


Astor in the literature
-----------------------

* X. Liu and H. Zhong, Mining stackoverflow for program repair, IEEE 25th International Conference on Software Analysis, Evolution and Reengineering (SANER), 2018.

* Urli, S., Yu, Z., Seinturier, L., & Monperrus, M. How to design a program repair bot?: insights from the repairnator project. In Proceedings of the 40th International Conference on Software Engineering: Software Engineering in Practice (pp. 95-104). ACM. 2018.

* K. Naitou, A. Tanikado, S. Matsumoto, Y. Higo, S. Kusumoto, H.  Kirinuki, T. Kurabayashi, H. Tanno, Haruto. Toward introducing automated program repair techniques to industrial software development. ICPC, 2018.

* Ye, H., Martinez, M., & Monperrus, M. A Comprehensive Study of Automatic Program Repair on the QuixBugs Benchmark. arXiv preprint arXiv:1805.03454, 2018.

* C. Trad, R. Abou Assi, W. Masri, F. Zaraket. CFAAR: Control Flow Alteration to Assist Repair. ArXiv preprint 	arXiv:1808.09229, 2018.

* A. Tanikado, H. Yokoyama, M. Yamamoto, S. Sumi, Y. Higo, and S. Kusumoto. New strategies for selecting reuse candidates on automated program repair. In 2017 IEEE 41st Annual Computer Software and Appli- cations Conference (COMPSAC), volume 2, pages 266–267, July 2017.

* Ming Wen, Junjie Chen, Rongxin Wu, Dan Hao, and Shing-Chi Cheung. An empirical analysis of the influence of fault space on search-based automated program repair.  Arxiv 1707.05172,  2017.

* Liu, Yuefei. Understanding and generating patches for bugs introduced by third-party library upgrades. Master’s thesis, 2017.

* Qi Xin and Steven P Reiss. Leveraging syntax-related code for automated program repair. In Proceedings of the 32nd IEEE/ACM International Conference on Automated Software Engineering (ASE), pages 660–670. IEEE, 2017.

* Matias Martinez, Thomas Durieux, Romain Sommerard, Jifeng Xuan, and Martin Monperrus. Automatic repair of real bugs in java: A large-scale experiment on the defects4j dataset. Empirical Software Engineering, pages 1–29, 2016

* Xuan Bach D Le, David Lo, and Claire Le Goues. History driven program repair. In Software Analysis, Evolution, and Reengineering (SANER), 2016 IEEE 23rd International Conference on, volume 1, pages 213–224. IEEE, 2016.

* Yingfei Xiong, Jie Wang, Runfa Yan, Jiachen Zhang, Shi Han, Gang Huang, and Lu Zhang. Precise condition synthesis for program repair. In Proceedings of the 39th International Conference on Software Engineering, ICSE ’17, pages 416–426, Piscataway, NJ, USA, 2017. IEEE Press.

* Yuan Yuan and Wolfgang Banzhaf. Arja: Automated repair of java programs via multi-objective genetic programming, 2017. Arviv 1712.07804.

* Liushan Chen, Yu Pei, and Carlo A. Furia. Contract-based program repair without the contracts. In Proceedings of the 32Nd IEEE/ACM International Conference on Automated Software Engineering, ASE 2017, pages 637–647, Piscataway, NJ, USA, 2017. IEEE Press

* Ripon K. Saha, Yingjun Lyu, Hiroaki Yoshida, and Mukul R. Prasad. Elixir: Effective object oriented program repair. In Proceedings of the 32Nd IEEE/ACM International Conference on Automated Software Engineering, ASE 2017, pages 648–659, Piscataway, NJ, USA, 2017. IEEE Press.

* Manish Motwani, Sandhya Sankaranarayanan, Ren ́e Just, and Yuriy Brun. Do automated program repair techniques repair hard and important bugs? Empirical Software Engineering, pages 1–47, 2017.

* H. Yokoyama, Y. Higo, and S. Kusumoto. Evaluating automated program repair using characteristics of defects. In 2017 8th International Workshop on Empirical Software Engineering in Practice (IWESEP), pages 47–52, March 2017.

* Xinyuan Liu, Muhan Zeng, Yingfei Xiong, Lu Zhang, and Gang Huang. Identifying patch correctness in test-based automatic program repair. arXiv preprint arXiv:1706.09120, 2017.

* Jiajun Jiang and Yingfei Xiong. Can defects be fixed with weak test suites? an analysis of 50 defects from defects4j. arXiv preprint arXiv:1705.04149, 2017.







