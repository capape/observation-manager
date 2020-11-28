#!/bin/bash

echo Starting ObservationManager

# Going to script files dir (should be installation directory...)
DIR=`dirname "$0"`
cd $DIR

# Starting up Observation Manager
java -Dextensions.dir="$DIR"/extensions -cp classpath:classpath/observation-manager-jar-with-dependencies.jar de.lehmannet.om.ObservationManagerApp
