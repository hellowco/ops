pipeline{
    agent any
    environment {
        SCRIPT_PATH = '/var/jenkins_home/custom/llmops-api'
    }
    tools {
        gradle 'gradle 8.11.1'
    }
    stages{
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Get Commit Message') {
            steps {
                script {
                    def gitCommitMessage = sh(
                        script: "git log -1 --pretty=%B",
                        returnStdout: true
                    ).trim()
                    echo "Commit Message: ${gitCommitMessage}"
                    env.GIT_COMMIT_MESSAGE = gitCommitMessage
                }
            }
        }
        stage('Prepare'){
            steps {
                sh 'gradle clean'
            }
        }
        stage('Build') {
            steps {
                sh 'gradle build -x test'
            }
        }
        stage('Deploy') {
            steps {
                sh '''
                	mkdir -p ${SCRIPT_PATH}
                    cp ./docker/Dockerfile ${SCRIPT_PATH}
                    cp ./deploy/rebuild_and_run.sh ${SCRIPT_PATH}
                    cp ./deploy/root-ca.der ${SCRIPT_PATH}
                    cp ./build/libs/*.jar ${SCRIPT_PATH}
                    chmod +x ${SCRIPT_PATH}/rebuild_and_run.sh
                    ${SCRIPT_PATH}/rebuild_and_run.sh
                '''
            }
        }
    }    
    post {
        success {
            slackSend (
                message: "성공: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL}). 최근 커밋: '${env.GIT_COMMIT_MESSAGE}'",
            )
        }
        failure {
            slackSend (
                message: "실패: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL}). 최근 커밋: '${env.GIT_COMMIT_MESSAGE}'",
            )
        }
    }
}