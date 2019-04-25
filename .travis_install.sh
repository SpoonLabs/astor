#!/bin/bash

# compile test projects
function compile {
    if [[ ! -d "$1target/classes" ]]; then
        cd $1
        mvn test -DskipTests
        cd ../../
    fi
}

function compilemaven {
	DIR=$1
	OLD="$pwd"
	cd $1
	for FILE in ls "$DIR"*
	do
    	pwd
    	echo compiling $FILE
		#if [[ ! -d "$FILE/target/classes"  && -f "$FILE/pom.xml" ]]; then
	     if [[ -f "$FILE/pom.xml" ]]; then
    	    cd $FILE
        	mvn test -DskipTests
        	cd ../../../
    	fi 
	done
	#cd $OLD
}

compile "examples/Math-0c1ef/"
compile "examples/math_85/"
compile "examples/math_70/"
compile "examples/Math-issue-280/"
compile "examples/Math-issue-288/"
compile "examples/math_2/"
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

echo calling compile maven
compilemaven 'examples/testMultiMet/'
compilemaven 'examples/testMet/'
