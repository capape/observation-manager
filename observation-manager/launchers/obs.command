#!/bin/bash

echo Starting ObservationManager

# Going to script files dir (should be installation directory...)
DIR=`dirname "$0"`
cd $DIR

# Starting up Observation Manager
java -splash:images/splash_transparent.png -Dfile.encoding=UTF-8 -Dextensions.dir="$DIR"/extensions -cp classpath:classpath/observation-manager-jar-with-dependencies.jar de.lehmannet.om.ObservationManagerApp
