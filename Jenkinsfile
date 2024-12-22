pipeline {
    agent any
    environment {
        DOCKER_HUB_USERNAME = 'thantuan'
        DOCKER_HUB_PASSWORD = credentials('docker-hub-password')
        BACKEND_IMAGE = 'thantuan/backend-app'
        FRONTEND_IMAGE = 'thantuan/frontend-app'
        IMAGE_TAG = 'latest'  // Sử dụng số build của Jenkins làm tag
    }
    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'main', url: 'https://github.com/ThanHoangAnhTuan/Ecomerce-Java.git'
            }
        }
        stage('Build and Push Backend Image') {
            steps {
                script {
                    sh """
                    docker build -t ${BACKEND_IMAGE}:${IMAGE_TAG} ./backend
                    echo "${DOCKER_HUB_PASSWORD}" | docker login -u "${DOCKER_HUB_USERNAME}" --password-stdin
                    docker push ${BACKEND_IMAGE}:${IMAGE_TAG}
                    """
                }
            }
        }
        stage('Build and Push Frontend Image') {
            steps {
                script {
                    sh """
                    docker build -t ${FRONTEND_IMAGE}:${IMAGE_TAG} ./frontend
                    echo "${DOCKER_HUB_PASSWORD}" | docker login -u "${DOCKER_HUB_USERNAME}" --password-stdin
                    docker push ${FRONTEND_IMAGE}:${IMAGE_TAG}
                    """
                }
            }
        }
        stage('Deploy to Production') {
            steps {
                script {
                    sh """
                        ansible-playbook -i ansible/inventory.yml ansible/deploy.yml --extra-vars "backend_image=${BACKEND_IMAGE}:${IMAGE_TAG} frontend_image=${FRONTEND_IMAGE}:${IMAGE_TAG}"
                    """
                }
            }
        }
    }
}
