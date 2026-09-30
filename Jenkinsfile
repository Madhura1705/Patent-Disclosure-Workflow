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

                bat 'start "PatentDisclosureApp" /B cmd /c "mvn spring-boot:run > jenkins-app.log 2>&1"'

                echo 'Waiting for application on port 8090'

                powershell '''
                $ready = $false

                for ($i = 1; $i -le 15; $i++) {
                    try {
                        $r = Invoke-WebRequest "http://localhost:8090" -UseBasicParsing -TimeoutSec 3

                        if ($r.StatusCode -eq 200) {
                            $ready = $true
                            break
                        }
                    }
                    catch {
                    }

                    Start-Sleep -Seconds 3
                }

                if (-not $ready) {
                    Write-Host "Application did not become ready."

                    if (Test-Path "jenkins-app.log") {
                        Get-Content "jenkins-app.log" -Tail 50
                    }

                    exit 1
                }

                Write-Host "Application is ready on port 8090."
                '''

                echo 'Running Selenium tests'

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

                bat 'copy /Y target\\patent-disclosure-workflow-1.0.0.war "C:\\Users\\madhu\\apache-tomcat-11.0.24\\webapps\\patent-disclosure-workflow.war"'
            }
        }

        stage('Verify') {
            steps {
                echo 'Verifying deployed Patent Disclosure application'

                powershell '''
                Start-Sleep -Seconds 15

                $r = Invoke-WebRequest `
                    "http://localhost:8081/patent-disclosure-workflow/index.html" `
                    -UseBasicParsing `
                    -TimeoutSec 10

                if ($r.StatusCode -ne 200) {
                    exit 1
                }

                Write-Host "Deployed application returned HTTP 200."
                '''
            }
        }
    }

    post {
        always {
            echo 'Publishing JUnit test results'

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