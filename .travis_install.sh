#!/bin/bash

# compile test projects
function compile {
	
    if [[ ! -d "$1target/classes" ]]; then
    	echo compiling $1
        cd $1
        mvn test -DskipTests
        cd ../../
    fi
}

function compilef {
		echo compilingf $1
        cd $1
        mvn clean
        mvn install -DskipTests
        cd ../../
}

function compilemaven {
	DIR=$1
	OLD=`pwd`
	for FILE in ls "$DIR"*
	do
    	#cd $FILE
    	#pwd
    	echo compiling $FILE
		#if [[ ! -d "$1target/classes"  && -f "$1pom.xml" ]]; then
	     if [[ -f "$FILE/pom.xml" ]]; then
    	    cd $FILE
    	    echo in dir `pwd`
    	    mvn clean
    	    echo after claning $FILE
        	mvn test -DskipTests
        	cd ../../../
    	fi 
	done
#	cd $OLD
}


echo calling compile maven
compilemaven -q "examples/testMultiMet/"
compilemaven -q "examples/testMet/"

compile -q "examples/Math-0c1ef/"
compile -q "examples/math_85/"
compile -q "examples/math_70/"
compile -q "examples/Math-issue-280/"
compile -q "examples/Math-issue-288/"
compile -q "examples/math_2/"
compile -q "examples/math_5/"
compile -q "examples/jsoup31be24/"
compile -q "examples/introclass/3b2376/003/"
compile -q "examples/math_50/"
compile -q "examples/math_74/"
compile -q "examples/math_76/"
compile -q "examples/math_106/"
compile -q "examples/lang_63/"
compile -q "examples/lang_39/"
compile -q "examples/lang_1/"
compile -q "examples/lang_55/"
compile -q "examples/math_57/"
compile -q "examples/math_70_modified/"
compile -q "examples/lang_7/"


