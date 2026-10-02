pipeline {
    agent any

    environment {
        REGISTRY = 'localhost:5000'
        IMAGE_NAME = 'patent-disclosure-workflow'
        IMAGE_TAG = "1.0.${BUILD_NUMBER}"
        CONTAINER_NAME = 'patent-disclosure-container'
    }

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

        stage('Docker Build') {
            steps {
                echo "Building Docker image ${REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}"

                bat 'docker build -t %REGISTRY%/%IMAGE_NAME%:%IMAGE_TAG% .'

                echo 'Docker image built successfully'

                bat 'docker images %REGISTRY%/%IMAGE_NAME%'
            }
        }

        stage('Publish to Local Registry') {
            steps {
                echo "Publishing ${REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG} to local Docker Registry"

                bat 'docker push %REGISTRY%/%IMAGE_NAME%:%IMAGE_TAG%'

                echo 'Verifying image in local registry'

                powershell '''
                $tags = Invoke-WebRequest `
                    "http://localhost:5000/v2/patent-disclosure-workflow/tags/list" `
                    -UseBasicParsing `
                    -ErrorAction Stop

                Write-Host "Registry response:"
                Write-Host $tags.Content
                '''
            }
        }

        stage('Deploy Docker Container') {
            steps {
                echo "Deploying fresh container from ${REGISTRY}/${IMAGE_NAME}:${IMAGE_TAG}"

                bat 'docker pull %REGISTRY%/%IMAGE_NAME%:%IMAGE_TAG%'

                bat 'docker rm -f %CONTAINER_NAME% 2>NUL || exit /B 0'

                bat 'docker run -d --name %CONTAINER_NAME% -p 8091:8090 %REGISTRY%/%IMAGE_NAME%:%IMAGE_TAG%'

                echo 'Docker container deployed successfully'

                bat 'docker ps --filter "name=%CONTAINER_NAME%"'
            }
        }

        stage('Verify Docker Deployment') {
            steps {
                echo 'Verifying Docker deployment on port 8091'

                powershell '''
                $ready = $false

                for ($i = 1; $i -le 15; $i++) {
                    try {
                        $r = Invoke-WebRequest `
                            "http://localhost:8091" `
                            -UseBasicParsing `
                            -TimeoutSec 5 `
                            -ErrorAction Stop

                        Write-Host "Docker application returned HTTP $($r.StatusCode)."

                        if ($r.StatusCode -eq 200) {
                            $ready = $true
                            break
                        }
                    }
                    catch {
                        Write-Host "Waiting for Docker application..."
                        Start-Sleep -Seconds 3
                    }
                }

                if (-not $ready) {
                    Write-Host "Docker deployment verification failed."

                    docker logs %CONTAINER_NAME%

                    exit 1
                }

                Write-Host "Docker deployment verification completed successfully."
                '''

                bat 'docker inspect --format="{{.Config.Image}}" %CONTAINER_NAME%'
            }
        }

        stage('Deploy WAR to Tomcat') {
            steps {
                echo 'Deploying WAR to Apache Tomcat 11'

                bat 'copy /Y target\\patent-disclosure-workflow-1.0.0.war "C:\\Users\\madhu\\apache-tomcat-11.0.24\\webapps\\patent-disclosure-workflow.war"'
            }
        }

        stage('Verify Tomcat') {
            steps {
                echo 'Verifying deployed Patent Disclosure application on Tomcat'

                powershell '''
                $ready = $false

                for ($i = 1; $i -le 10; $i++) {
                    try {
                        $r = Invoke-WebRequest `
                            "http://localhost:8081/patent-disclosure-workflow/" `
                            -UseBasicParsing `
                            -TimeoutSec 10 `
                            -ErrorAction Stop

                        Write-Host "Tomcat returned HTTP $($r.StatusCode)."

                        $ready = $true
                        break
                    }
                    catch {
                        Write-Host "Waiting for Tomcat deployment..."
                        Start-Sleep -Seconds 5
                    }
                }

                if (-not $ready) {
                    Write-Host "Tomcat deployment verification failed."
                    exit 1
                }

                Write-Host "Tomcat deployment verification completed successfully."
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
            echo 'T13 JENKINS-DOCKER CONTINUOUS DEPLOYMENT SUCCESSFUL'
        }

        failure {
            echo 'T13 PIPELINE FAILED'
        }
    }
}
