pipeline {
     agent any
     tools {
        maven 'Maven 3.3.9'
     }
     environment {
        GHCR_CREDENTIALS = credentials("tomwey2-ghcr")
        IMAGE_VERSION = '0.1.0-SNAPSHOT'
     }
     stages {
        stage("initialize") {
            steps {
                sh "env"
                sh "mvn --version"
                sh "java -version"
            }
        }
        stage("compile") {
            steps {
                sh "./mvnw compile"
            }
        }
        stage("Unit test") {
            steps {
                sh "./mvnw test"
            }
        }
        stage("Code coverage") {
            steps {
                sh "./mvnw verify -DskipTests"
                publishHTML (target: [
                    reportDir: "target/site/jacoco",
                    reportFiles: "index.html",
                    reportName: "JaCoCo Report"
                ])
            }
        }
        stage("Static code analysis") {
            steps {
                sh "./mvnw checkstyle:checkstyle"
                publishHTML (target: [
                    reportDir: "target/reports",
                    reportFiles: "checkstyle.html",
                    reportName: "Checkstyle Report"
                ])
            }
        }
        stage("Docker build") {
            steps {
                sh "docker build -t ghcr.io/tomwey2/taskapp-backend:$IMAGE_VERSION -t ghcr.io/tomwey2/taskapp-backend:latest ."
                sh "docker login --username $GHCR_CREDENTIALS_USR --password $GHCR_CREDENTIALS_PSW ghcr.io"
                sh "docker push ghcr.io/tomwey2/taskapp-backend:$IMAGE_VERSION"
                sh "docker push ghcr.io/tomwey2/taskapp-backend:latest"
            }
        }
    }
    post {
        always {
            sh "docker logout"
        }
    }
 }