pipeline {
    agent any

    stages {

        stage('Checkout') {
            steps {
                echo 'Checking out Patent Disclosure Workflow from GitHub'

                git branch: 'main',
                    url: 'https://github.com/Madhura1705/Patent-Disclosure-Workflow.git'
            }
        }

        stage('Compile') {
            steps {
                echo 'Compiling Patent Disclosure application'

                bat 'mvn clean compile -DskipTests'
            }
        }

        stage('Test') {
            steps {
                echo 'Executing Selenium tests'

                bat 'mvn test'
            }
        }

        stage('Package') {
            steps {
                echo 'Creating WAR package'

                bat 'mvn package -DskipTests'

                archiveArtifacts artifacts: 'target/*.war',
                                 fingerprint: true
            }
        }

        stage('Deploy') {
            steps {
                echo 'Deploying WAR to Apache Tomcat 11'

                bat '''
                copy /Y target\\patent-disclosure-workflow-1.0.0.war ^
                "%USERPROFILE%\\apache-tomcat-11.0.24\\webapps\\patent-disclosure-workflow.war"
                '''
            }
        }

        stage('Verify') {
            steps {
                echo 'Verifying deployed Patent Disclosure application'

                bat '''
                powershell -NoProfile -Command ^
                "$r = Invoke-WebRequest 'http://localhost:8081/patent-disclosure-workflow/index.html' -UseBasicParsing; ^
                if ($r.StatusCode -ne 200) { exit 1 }"
                '''
            }
        }
    }

    post {
        always {
            junit allowEmptyResults: true,
                  testResults: 'target/surefire-reports/*.xml'
        }

        success {
            echo 'T9 PIPELINE AS CODE AND SERVER DEPLOYMENT SUCCESSFUL'
        }

        failure {
            echo 'T9 PIPELINE FAILED'
        }
    }
}
