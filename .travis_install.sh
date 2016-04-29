#!/bin/bash

# compile test projects
cd examples/Math-0c1ef/
mvn test -DskipTests
cd ../../

cd examples/math_85/
mvn test -DskipTests
cd ../../
 
cd examples/math_70/
mvn test -DskipTests
cd ../../

cd examples/Math-issue-288/
mvn test -DskipTests
cd ../../


cd examples/math_2/
mvn test -DskipTests
cd ../../


cd examples/jsoup31be24/
mvn test -DskipTests
cd ../../