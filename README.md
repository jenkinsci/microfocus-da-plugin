# Micro Focus Deployment Automation plugin for Jenkins 2.x

This plugin allows [Micro Focus Deployment Automation](http://www.serena.com/index.php/en/products/deployment-configuration-automation/serena-deployment-automation/overview) to be used in [Jenkins Pipelines](https://jenkins.io/solutions/pipeline/).
It provides the following adhoc build steps:

- Create Version
- Add Files to Version
- Add Status to Version
- Run Application Process
- Create Snapshot (TBD)
- Add Version to Snapshot (TBD)
- Run Generic Process (TBD)

The steps can be used in "freestyle" projects but are designed to be used in pipelines. Micro Focus Deployment Automation can be used across the 
development lifecycle but is typically used to automate releases into controlled environments (Staging/Pre-Prod/Production) and has additional enterprise capabilities above and beyond
Jenkins to support this. Therefore a typical scenario involving Jenkins and Deployment Automation might be something like the following:

1. Developer makes a commit
2. Jenkins automatically builds/packages application and creates a new version in Deployment Automation for the application
3. Jenkins calls Deployment Automation to push the package to the different environments in the pipeline
4. Release Engineer uses Deployment Automation User Interface to create snapshots, schedule and approve deployment to Production environments.

Note: Deployment Automation might additionally be used to schedule the deployment of multiple components/applications into Production.

## Example pipeline code

Below is an example of some pipeline code that creates new versions in Deployment Automation and deploys to a number of environments.
```
env.DA_URL = 'http://localhost:8080/serena_ra'
env.DA_CREDENTIALS_ID = 'microfocus-da-dmsys'
env.DA_COMPONENT = "${JOB_BASE_NAME}"
env.DA_APPLICATION = 'Ring2Park Microservices'
env.DA_PROCESS = 'Deploy (Docker)'

def mvnHome
def gitCommitId

stage('Build') {
    node {
        git 'https://github.com/ring2park-microservices/web-ui.git'
        sh "git rev-parse HEAD > .git/commit-id"
        gitCommitId = readFile('.git/commit-id').trim()
        mvnHome = tool 'maven_3.3.9'
        if (isUnix()) {
            sh "'${mvnHome}/bin/mvn' -Dmaven.test.failure.ignore clean package"
        } else {
            bat(/"${mvnHome}\bin\mvn" -Dmaven.test.failure.ignore clean package/)
        }
    }
}

stage('Tests') {
    node {
        echo "Tests would go here..."
    }
}

stage('Publish') {
    node {
        docker.withRegistry('http://devops:5000', 'dmsys-docker') {
            def dockerApp = docker.build "${DA_COMPONENT}:${BUILD_NUMBER}"
            dockerApp.push()
        }
        def verProperties = 
"""
job.url=${env.BUILD_URL}
jenkins.url=${env.JENKINS_URL}
git.commit.id=${gitCommitId}
"""
        daCreateVersion url: "${DA_URL}",
            credentialsId: "${DA_CREDENTIALS_ID}", 
            component: "${DA_COMPONENT}", 
            version: "${DA_COMPONENT}:${BUILD_NUMBER}", 
            properties: verProperties,
            failIfVersionExists: false
        daAddFilesToVersion url: "${DA_URL}",
            credentialsId: "${DA_CREDENTIALS_ID}", 
            component: "${DA_COMPONENT}", 
            version: "${DA_COMPONENT}:${BUILD_NUMBER}",
            base: "${WORKSPACE}", 
            offset: "", 
            excludes: "", 
            includes: "Dockerfile\ntarget/*.jar"    
        daAddStatusToVersion url: "${DA_URL}",
            credentialsId: "${DA_CREDENTIALS_ID}", 
            component: "${DA_COMPONENT}", 
            version: "${DA_COMPONENT}:${BUILD_NUMBER}", 
            status: "Built"  
    }
}
       
stage('Integration') {
    deployToEnvironment("Integration", "Integrated", false)    
}

stage('UAT') {
    deployToEnvironment("UAT", "Acceptance Tested", true)    
}

stage('Production') {
    // We could do this:
    deployToEnvironment("Production", "Live", true)
    // but lets create an input step to be invoked from DA via REST endpoint:
    //    http://server:port/jenkins/job/pipeline-release/${BUILD_NUMBER}/input/Released-${BUILD_NUMBER}/proceedEmpty
    input(id: "Released-${BUILD_NUMBER}", message: "Released build ${BUILD_NUMBER} to Production?")     
}

def deployToEnvironment(environment, verStatus, waitForInput) {
    def deployProperties = """
job.url=${env.BUILD_URL}
jenkins.url=${env.JENKINS_URL}
remove.container=true
"""
    node {
        if (waitForInput) {
            input "Deploy to ${environment}?"
        } else {
            echo "Proceeding with deployment to ${environment}..."
        }  
        milestone()
        daRunApplicationProcess url: "${DA_URL}",
            credentialsId: "${DA_CREDENTIALS_ID}", 
            application: "${DA_APPLICATION}", 
            process: "${DA_PROCESS}", 
            environment: "${environment}",
            versions: "${DA_COMPONENT}=${DA_COMPONENT}:${BUILD_NUMBER}",
            properties: "${deployProperties}",
            onlyChangedVersions: false
        daAddStatusToVersion url: "${DA_URL}",
            credentialsId: "${DA_CREDENTIALS_ID}", 
            component: "${DA_COMPONENT}",
            version: "${DA_COMPONENT}:${BUILD_NUMBER}",
            status: "${verStatus}"
    }
}
```
![Example Pipeline](https://github.com/jenkinsci/microfocus-da-plugin/blob/master/images/jenkins-pipeline.png)

## Build/Usage Instructions

1) Clone the repository from github.

2) Build the plugin:

```
mvn clean package
```

3) Run the plugin in test Jenkins instance:

```
mvn hpi:run -Djetty.port=8090
```

4) Browse to **http://localhost:8090/jenkins** to test the plugin. 

5) Create a new "credential" to connect to Micro Focus DA, and that will be referred to in the steps - "microfocus-da-admin" in the above example.

Note: you will have to install the [Jenkins Pipeline](https://wiki.jenkins-ci.org/display/JENKINS/Pipeline+Plugin) and [Credentials](https://wiki.jenkins-ci.org/display/JENKINS/Credentials+Plugin) plugins to be able to create pipelines.

##Release Notes

#####0.1-SNAPSHOT
*The plugin has not yet been completed or validated and so is not yet available for installation directly from the Jenkins
plugin repository. To try the plugin, please build locally using the above instructions and install the generated ".hpi" file directory.*

