# Micro Focus Deployment Automation plugin for Jenkins 2.0

This plugin allows Micro Focus Deployment Automation to be used in Jenkins Pipelines.
It currently provides the following adhoc build steps:

- Create Version
- Add Files to Version
- Add Status to Version
- Run Application Process

The steps can be used in "freestyle" projects but are designed to be used in Pipelines. Some example pipeline code is illustrated below:

'''
node {
    def verProperties = 
"""
job.url=${env.BUILD_URL}
jenkins.url=${env.JENKINS_URL}
"""
    def versions =
"""
jenkins-test=version-${BUILD_NUMBER}
"""
   
   stage('Preparation') { // for display purposes
      echo "preparing"
      deleteDir()
   }
   stage('Build') {
      echo "building"
      writeFile file: 'hello.txt', text: 'hello world'
   }
   stage('Test') {
      echo "testing"
   }
   stage('Deploy') {
        daCreateVersion url: "${DA_URL}",
            credentialsId: "microfocus-da-admin", 
            component: "jenkins-test", 
            version: "version-${BUILD_NUMBER}", 
            properties: verProperties,
            failIfVersionExists: false
        daAddFilesToVersion url: "${DA_URL}",
            credentialsId: "microfocus-da-admin", 
            component: "jenkins-test", 
            version: "version-${BUILD_NUMBER}",
            base: "${WORKSPACE}", 
            offset: "", 
            excludes: "", 
            includes: "**/*"
        daAddStatusToVersion url: "${DA_URL}",
            credentialsId: "microfocus-da-admin", 
            component: "jenkins-test", 
            version: "version-${BUILD_NUMBER}", 
            status: "BUILT"
        daRunApplicationProcess url: "${DA_URL}",
            credentialsId: "microfocus-da-admin", 
            application: "Jenkins Test", 
            process: "Deploy", 
            environment: "Integration",
            versions: versions,
            properties: verProperties,
            onlyChangedVersions: false    
   }
}
'''

## Build Instructions
------------------

1) Clone the repository from github.

2> Build the plugin:

```
mvn clean package
```

3) Run the plugin in test Jenkins instance:

```
mvn hpi:run -Djetty.port=8090
```

4) Browse to **http://localhost:8090/jenkins** to test the plugin. 

5) Create a new "credential" to connect to Micro Focus DA, and that will be referred to in the steps.

Note: you will have to load the [Jenkins Pipeline](https://wiki.jenkins-ci.org/display/JENKINS/Pipeline+Plugin) and [Credentials](https://wiki.jenkins-ci.org/display/JENKINS/Credentials+Plugin) plugins to be able to create pipelines.

