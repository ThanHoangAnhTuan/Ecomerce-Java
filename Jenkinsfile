pipeline {
    agent {
        docker {
            image 'maven:3.9.9-eclipse-temurin-21'
        }
    }
    environment {
        BACKEND_IMAGE = 'thantuan/backend-app'
        FRONTEND_IMAGE = 'thantuan/frontend-app'
        IMAGE_TAG = 'latest' // Sử dụng số build của Jenkins làm tag nếu cần: BUILD_NUMBER
    }
    stages {
        stage('Clone Repository') {
            steps {
                git branch: 'master', url: 'https://github.com/ThanHoangAnhTuan/Ecomerce-Java.git'
            }
        }
        stage('Build Maven Project') {
            steps {
                sh 'mvn clean install' // Build project bằng Maven
            }
        }
        stage('Build and Push Backend Image') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'docker-hub-token') {
                        def backendImage = docker.build("${BACKEND_IMAGE}:${IMAGE_TAG}", "./backend")
                        backendImage.push()
                    }
                }
            }
        }
        stage('Build and Push Frontend Image') {
            steps {
                script {
                    docker.withRegistry('https://index.docker.io/v1/', 'docker-hub-token') {
                        def frontendImage = docker.build("${FRONTEND_IMAGE}:${IMAGE_TAG}", "./frontend")
                        frontendImage.push()
                    }
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
