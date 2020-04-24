#!/bin/bash

# compile test projects
function compile {
	
    if [[ ! -d "$1target/classes" ]]; then
    	echo compiling $1
        cd $1
        mvn -ff -q test -DskipTests
        cd ../../
    fi
}

function compilef {
		echo compilingf $1
        cd $1
        mvn -ff -q clean
        mvn -ff -q install -DskipTests
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
    	    mvn -ff -q clean
    	    echo after claning $FILE
        	mvn -ff -q test -DskipTests
        	cd ../../../
    	fi 
	done
#	cd $OLD
}


echo calling compile maven
compilemaven "examples/testMultiMet/"
compilemaven "examples/testMet/"

compile "examples/Math-0c1ef/"
compile "examples/math_85/"
compile "examples/math_70/"
compile "examples/Math-issue-280/"
compile "examples/Math-issue-288/"
compile "examples/math_2/"
compile "examples/math_5/"
compile "examples/jsoup31be24/"
compile "examples/introclass/3b2376/003/"
compile "examples/math_50/"
compile "examples/math_74/"
compile "examples/math_76/"
compile "examples/math_106/"
compile "examples/lang_63/"
compile "examples/lang_39/"
compile "examples/lang_1/"
compile "examples/lang_55/"
compile "examples/math_57/"
compile "examples/math_70_modified/"
compile "examples/lang_7/"
compile "examples/example_return_mutation/"
compile "examples/example_return_arthmetic/"
compile "examples/issues/LeapYearIssue196/"
cd ../
compile "examples/issues/LeapYearIssue196-bis/"
cd ../


