Micro Focus Deployment Automation plugin for Jenkins 2.0
========================================================

This plugin allows Micro Focus Deployment Automation to be used in Jenkins Pipelines.
It provides a number of adhoc steps to integrate component version uploads and deployments.

Build Instructions
------------------

1) Install ***codestation2-client*** into local maven repository:

If ***com\urbancode\codestation2-client-all*** directory already exists in local maven repository (***.m2***) but pom is incomplete, 
you can copy the jar out the way, delete the directory and then execute the following command:

>mvn install:install-file -DgroupId=com.urbancode -DartifactId=codestation2-client-all -Dversion=1.1 -Dpackaging=jar -Dfile=codestation2-client-all-1.1.jar

2> Build the plugin:

>mvn clean package

3) Run the plugin in test Jenkins instance:

>mvn hpi:run -Djetty.port=8090

4) Browse to [http://localhost:8090/jenkins] and test the plugin...

