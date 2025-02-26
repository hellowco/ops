pipeline {
    agent any
    tools {
        gradle 'gradle 8.11.1'
    }
    stages {
        stage('Checkout') {
            steps {
                checkout scm
            }
        }
        stage('Set Environment Settings') {
            steps {
                script {
                    if (env.BRANCH_NAME == 'main') {
                        env.DEPLOY_ENV = 'prod'
                    } else {
                        env.DEPLOY_ENV = 'dev'
                    }

                   def scriptPath = (env.DEPLOY_ENV == 'prod') 
                        ? '/var/jenkins_home/custom/llmops-api-main' 
                        : '/var/jenkins_home/custom/llmops-api-dev'
                    
                    echo "브랜치: ${env.BRANCH_NAME}, 최종 DEPLOY_ENV: ${env.DEPLOY_ENV}"
                    echo "SCRIPT_PATH: ${scriptPath}"
                    
                    env.SCRIPT_PATH = scriptPath
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
        stage('Prepare') {
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
                    
                    if (env.DEPLOY_ENV == 'prod') {
                        echo "Deploying production build"
                        sh """
                            cp ./deploy/prod/Dockerfile ${deployDir}
                            cp ./deploy/prod/rebuild_and_run.sh ${deployDir}
                            cp ./deploy/prod/root-ca.der ${deployDir}
                            cp ./build/libs/*.jar ${deployDir}
                            chmod +x ${deployDir}/rebuild_and_run.sh
                            ${deployDir}/rebuild_and_run.sh
                        """
                    } else if (env.DEPLOY_ENV == 'dev') {
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
                message: "성공: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL}). 최근 커밋: '${env.GIT_COMMIT_MESSAGE}'"
            )
        }
        failure {
            slackSend (
                message: "실패: Job '${env.JOB_NAME} [${env.BUILD_NUMBER}]' (${env.BUILD_URL}). 최근 커밋: '${env.GIT_COMMIT_MESSAGE}'"
            )
        }
    }
}
