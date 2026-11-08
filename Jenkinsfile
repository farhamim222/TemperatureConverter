
pipeline {
    agent any

    tools {
        jdk 'JDK21'
        maven 'Maven3'
    }

    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build and Test') {
            steps {
                bat 'mvn -B clean verify'
            }
        }

        stage('Archive Test Results') {
            steps {
                junit 'target/surefire-reports/*.xml'
                archiveArtifacts artifacts: 'target/site/jacoco/**',
                                 allowEmptyArchive: true
            }
        }
    }

    post {
        success {
            echo 'TemperatureConverter pipeline completed successfully!'
        }

        failure {
            echo 'TemperatureConverter pipeline failed.'
        }
    }
}
