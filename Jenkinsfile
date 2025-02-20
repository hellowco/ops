pipeline{
    agent any
    environment {
        SCRIPT_PATH = '/var/jenkins_home/custom/llmops-api-dev'
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
		stage('Set Branch Specific Settings') {
            steps {
                script {
                    if (env.BRANCH_NAME == 'main') {
                        env.SCRIPT_PATH = '/var/jenkins_home/custom/llmops-api-main'
                        echo "Main branch detected. Using prod settings."
                    } else if (env.BRANCH_NAME == 'dev') {
                        env.SCRIPT_PATH = '/var/jenkins_home/custom/llmops-api-dev'
                        echo "Dev branch detected. Using dev settings."
                    }
                }
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
                script {
                    def deployDir = env.SCRIPT_PATH
                    sh "mkdir -p ${deployDir}"
                    
                    if (env.BRANCH_NAME == 'main') {
                        echo "Deploying production build"
                        sh """
                            cp ./deploy/prod/Dockerfile ${deployDir}
                            cp ./deploy/prod/rebuild_and_run.sh ${deployDir}
                            cp ./deploy/prod/root-ca.der ${deployDir}
                            cp ./build/libs/*.jar ${deployDir}
                            chmod +x ${deployDir}/rebuild_and_run.sh
                            ${deployDir}/rebuild_and_run.sh
                        """
                    } else if (env.BRANCH_NAME == 'dev') {
                        echo "Deploying development build"
                        sh """
                            cp ./deploy/dev/Dockerfile ${deployDir}
                            cp ./deploy/dev/rebuild_and_run.sh ${deployDir}
                            cp ./deploy/dev/root-ca.der ${deployDir}
                            cp ./build/libs/*.jar ${deployDir}
                            chmod +x ${deployDir}/rebuild_and_run.sh
                            ${deployDir}/rebuild_and_run.sh
                        """
                    }
                }
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