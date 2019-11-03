
I haven't found fits dependency in any mvn repository.

To use it in the project first install to mvn repository:

mvn install:install-file -Dfile=fits1.3.jar -Dsources=fits1.3_source.jar  -DgroupId=eap.fits -DartifactId=fits -Dversion=1.3 -Dpackaging=jar

This lib can be download from
https://swift.gsfc.nasa.gov/sdc/software/java/

