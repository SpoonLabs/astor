if [[ $TRAVIS_BRANCH == "deploy" ]]; then
        mvn versions:set -DnewVersion=`git rev-parse HEAD`
		mvn deploy --settings .travis-settings.xml -DskipTests=true -B;
		echo "Deployed"
fi