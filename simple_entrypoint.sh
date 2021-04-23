echo "Starting astor"

java \
-Dlog4j.configurationFile=/apps/astor/log4j2.xml \
-Dgzoltarpath=/apps/astor/lib/moderngzoltar \
-cp $(cat /apps/astor/astor-classpath.txt):target/classes \
fr.inria.main.evolution.AstorMain \
-mode $astor_mode \
-location /examples/Math \
-dependencies /examples/Math/lib \
-population $astor_population \
-maxtime $astor_max_time \
-maxgen $astor_max_generations \
-faultlocalization $astor_faultlocalization \
-jvm4testexecution $JAVA7_HOME \

# To keep the container open for inspection
# tail -f /dev/null