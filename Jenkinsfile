pipeline {
    agent any
    tools {
        gradle 'gradle-7.4.1'
    }
    environment {
        BUILD_TARGET_HOME = '${BUILD_HOME}'
        APP_AUTH = 'auth-server'
        APP_API = 'api-server'
        APP_FE = 'front-server'
        IMG_AUTH = 'huyeon123/conmoto-auth'
        IMG_API = 'huyeon123/conmoto-api'
        IMG_FE = 'huyeon123/conmoto-front'
    }
    stages {
        stage('Grant Gradle Permission') {
            steps {
                echo 'Grant Gradle Permission'
                sh 'chmod +x gradlew'
            }
        }
        stage('Build') {
            parallel {
                stage('build-auth-server') {
                    when {
                        changeset "auth-server/**/*"
                    }
                    steps {
                        echo 'Build Start "${APP_AUTH}"'
                        sh './gradlew ${APP_AUTH}:bootJar'
                        echo 'Build End "${APP_AUTH}"'
                    }
                }
                stage('build-api-server') {
                    when {
                        changeset "api-server/**/*"
                    }
                    steps {
                        echo 'Build Start "${APP_API}"'
                        sh './gradlew ${APP_API}:bootJar'
                        echo 'Build End "${APP_API}"'
                    }
                }
                stage('build-front-server') {
                    when {
                        changeset "front-server/**/*"
                    }
                    steps {
                        echo 'Build Start "${APP_FE}"'
                        sh './gradlew ${APP_FE}:bootJar'
                        echo 'Build End "${APP_FE}"'
                    }
                }
            }
        }
        stage('Docker Build') {
            parallel {
                stage('docker-build-auth-server') {
                    when {
                        changeset "auth-server/**/*"
                    }
                    steps {
                        echo 'Docker Build Start "${IMG_AUTH}"'
                        sh 'cd auth-server'
                        sh 'sudo docker build -t "${IMG_AUTH}" .'
                        echo 'Docker Build End "${IMG_AUTH}"'
                    }
                }
                stage('docker-build-api-server') {
                    when {
                        changeset "api-server/**/*"
                    }
                    steps {
                        echo 'Docker Build Start "${IMG_API}"'
                        sh 'cd api-server'
                        sh 'sudo docker build -t "${IMG_API}" .'
                        echo 'Docker Build End "${IMG_API}"'
                    }
                }
                stage('docker-build-front-server') {
                    when {
                        changeset "front-server/**/*"
                    }
                    steps {
                        echo 'Docker Build Start "${IMG_FE}"'
                        sh 'cd front-server'
                        sh 'sudo docker build -t "${IMG_FE}" .'
                        echo 'Docker Build End "${IMG_FE}"'
                    }
                }
            }
        }
        stage('Deploy') {
            parallel {
                stage('deploy-app-auth') {
                    when {
                        changeset "auth-server/**/*"
                    }
                    steps {
                        echo 'Deploy Start "${IMG_AUTH}"'
                        sh 'sudo docker ps -f name="${APP_AUTH}" -q | sudo xargs --no-run-if-empty docker container stop'
                        sh 'sudo docker container ls -a -f name="${APP_AUTH}" -q | sudo xargs -r docker container rm'
                        sh 'sudo docker images --no-trunc --all --quiet --filter="dangling=true" | sudo xargs --no-run-if-empty docker rmi'
                        sh 'sudo docker run -d --name "${APP_AUTH}" -p 8100:8100 "${IMG_AUTH}"'
                        echo 'Deploy End "${IMG_AUTH}"'
                    }
                }
                stage('deploy-app-api') {
                    when {
                        changeset "api-server/**/*"
                    }
                    steps {
                        echo 'Deploy Start "${IMG_API}"'
                        sh 'sudo docker ps -f name="${APP_API}" -q | sudo xargs --no-run-if-empty docker container stop'
                        sh 'sudo docker container ls -a -f name="${APP_API}" -q | sudo xargs -r docker container rm'
                        sh 'sudo docker images --no-trunc --all --quiet --filter="dangling=true" | sudo xargs --no-run-if-empty docker rmi'
                        sh 'sudo docker run -d --name "${APP_API}" -p 8300:8300 "${IMG_API}"'
                        echo 'Deploy End "${IMG_API}"'
                    }
                }
                stage('deploy-app-fe') {
                    when {
                        changeset "front-server/**/*"
                    }
                    steps {
                        echo 'Deploy Start "${IMG_FE}"'
                        sh 'sudo docker ps -f name="${APP_FE}" -q | sudo xargs --no-run-if-empty docker container stop'
                        sh 'sudo docker container ls -a -f name="${APP_FE}" -q | sudo xargs -r docker container rm'
                        sh 'sudo docker images --no-trunc --all --quiet --filter="dangling=true" | sudo xargs --no-run-if-empty docker rmi'
                        sh 'sudo docker run -d --name "${APP_FE}" -p 8200:8200 "${IMG_FE}"'
                        echo 'Deploy End "${IMG_FE}"'
                    }
                }
            }
        }
    }
}