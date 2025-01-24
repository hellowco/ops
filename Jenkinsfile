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
}