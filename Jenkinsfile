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
                echo 'Starting application for Selenium tests'

                bat '''
                start "PatentDisclosureApp" /B cmd /c "mvn spring-boot:run > jenkins-app.log 2>&1"

                echo Waiting for application on port 8090...
                timeout /t 25 /nobreak

                powershell -NoProfile -Command ^
                "$r = Invoke-WebRequest 'http://localhost:8090' -UseBasicParsing; ^
                if ($r.StatusCode -ne 200) { exit 1 }"

                echo Running Selenium tests...
                call mvn test
                '''
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
                echo 'Deploying WAR to Apache Tomcat 11 on port 8081'

                bat '''
                copy /Y target\\patent-disclosure-workflow-1.0.0.war ^
                "C:\\Users\\madhu\\apache-tomcat-11.0.24\\webapps\\patent-disclosure-workflow.war"
                '''
            }
        }

        stage('Verify') {
            steps {
                echo 'Verifying deployed Patent Disclosure application'

                bat '''
                timeout /t 10 /nobreak

                powershell -NoProfile -Command ^
                "$r = Invoke-WebRequest 'http://localhost:8081/patent-disclosure-workflow/index.html' -UseBasicParsing; ^
                if ($r.StatusCode -ne 200) { exit 1 }"
                '''
            }
        }
    }

    post {
        always {
            echo 'Publishing JUnit test results'

            junit allowEmptyResults: true,
                  testResults: 'target/surefire-reports/*.xml'

            echo 'Stopping temporary Spring Boot test process'

            bat '''
            powershell -NoProfile -Command ^
            "Get-CimInstance Win32_Process | ^
            Where-Object { $_.CommandLine -like '*spring-boot:run*' } | ^
            ForEach-Object { Stop-Process -Id $_.ProcessId -Force -ErrorAction SilentlyContinue }"
            '''
        }

        success {
            echo 'T9 PIPELINE AS CODE AND SERVER DEPLOYMENT SUCCESSFUL'
        }

        failure {
            echo 'T9 PIPELINE FAILED'
        }
    }
}