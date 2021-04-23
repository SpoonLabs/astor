FROM maven:3.6.3-jdk-11

RUN apt-get update && apt-get install bash software-properties-common -y

# This block install ancient java in modern linux
WORKDIR /java
COPY ./lib/zulu7.44.0.11-ca-jdk7.0.292-linux_amd64.deb .
# Zulu Java, to get the legacy Java 7 experience
RUN apt-key adv --keyserver hkp://keyserver.ubuntu.com:80 --recv-keys 0xB1998361219BD9C9
RUN apt-get install ./zulu7.44.0.11-ca-jdk7.0.292-linux_amd64.deb -y
RUN apt-get update
# Java is in ./usr/lib/jvm/zulu-7-amd64


ENV JAVA7_HOME /usr/lib/jvm/zulu-7-amd64
# Either Gzoltar or CoCoSpoon
ENV astor_faultlocalization Gzoltar
# Either jMutRepair, jGenProg, jKali
ENV astor_mode jMutRepair
# Max Time in Minutes for astor to run - abort after
ENV astor_max_time 10
# Population of Candidates (for approaches that support this) in Astor
ENV astor_population 8
ENV astor_max_generations 3000
ENV gzoltar_lib_path /apps/astor/lib/moderngzoltar

# Copy Astor around and install it 
WORKDIR /apps/astor
# Copy all - make sure to have .dockerignore up
COPY . .

# Install tduriex extra at the moment
RUN mvn install:install-file -Dfile=/apps/astor/lib/tdurieux-plugin/project-config-maven-plugin-1.0-SNAPSHOT.jar -DpomFile=/apps/astor/lib/tdurieux-plugin/project-config-maven-plugin-1.0-SNAPSHOT.pom

# From Getting Started Troubleshooting
RUN mvn install:install-file -Dfile=/apps/astor/lib/gzoltar/com.gzoltar-0.0.3.jar -DgroupId=com.gzoltar -DartifactId=gzoltar -Dversion=0.0.3 -Dpackaging=jar


RUN mvn compile
RUN mvn dependency:build-classpath -B | egrep -v "(^\[INFO\]|^\[WARNING\])" | tee /apps/astor/astor-classpath.txt

RUN mvn install -DskipTests=true

# Copy example
COPY examples/Math-issue-280 /examples/Math
#COPY examples/Math-issue-340 /examples/Math
#COPY examples/time_2 /examples/time

WORKDIR /examples/Math
# run java home before, to set for the next mvn run a different jvm
RUN JAVA_HOME=/usr/lib/jvm/zulu-7-amd64 && mvn compile test-compile

WORKDIR /apps/astor


ENTRYPOINT ["bash","./simple_entrypoint.sh"]