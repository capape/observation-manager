echo Starting ObservationManager

:: Going to batch files dir
cd %0\..\

:. Starting up Observation Manager
java -Dfile.encoding=UTF-8 -Dextensions.dir=.\extensions -cp classpath;classpath/observation-manager-jar-with-dependencies.jar de.lehmannet.om.ObservationManagerApp %1 %2 %3 %4 %5 %6 %7 %8 %9
