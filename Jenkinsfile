pipeline {
    agent any
    tools {
        gradle 'gradle-7.4.1'
    }
    environment {
        BUILD_TARGET_HOME = '${BUILD_HOME}'
        PROJECT = 'multi'
        APP_AUTH = 'auth-server'
        APP_API = 'api-server'
        APP_FE = 'front-server'
        IMG_AUTH = 'CONMOTO-AUTH'
        IMG_API = 'CONMOTO-API'
        IMG_FE = 'CONMOTO-FE'
    }
    stages {
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
                        echo 'Docker Build Start "${APP_AUTH}"'
                        script {
                            docker.build('"${IMG_AUTH}"')
                        }
                        echo 'Docker Build End "${APP_AUTH}"'
                    }
                }
                stage('docker-build-api-server') {
                    when {
                        changeset "api-server/**/*"
                    }
                    steps {
                        echo 'Build Start "${APP_API}"'
                        script {
                            docker.build('"${IMG_API}"')
                        }
                        echo 'Build End "${APP_API}"'
                    }
                }
                stage('docker-build-front-server') {
                    when {
                        changeset "front-server/**/*"
                    }
                    steps {
                        echo 'Build Start "${APP_FE}"'
                        script {
                            docker.build('"${IMG_FE}"')
                        }
                        echo 'Build End "${APP_FE}"'
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
                        sh 'docker ps -f name="${IMG_AUTH}" -q | xargs --no-run-if-empty docker container stop'
                        sh 'docker container ls -a -f name="${IMG_AUTH}" -q | xargs -r docker container rm'
                        sh 'docker images --no-trunc --all --quiet --filter="dangling=true" | xargs --no-run-if-empty docker rmi'
                        sh 'docker run -d --name "${IMG_AUTH}" -p 8100:8100 "${IMG_AUTH}"'
                        echo 'Deploy End "${IMG_AUTH}"'
                    }
                }
                stage('deploy-app-api') {
                    when {
                        changeset "api-server/**/*"
                    }
                    steps {
                        echo 'Deploy Start "${IMG_API}"'
                        sh 'docker ps -f name="${IMG_API}" -q | xargs --no-run-if-empty docker container stop'
                        sh 'docker container ls -a -f name="${IMG_API}" -q | xargs -r docker container rm'
                        sh 'docker images --no-trunc --all --quiet --filter="dangling=true" | xargs --no-run-if-empty docker rmi'
                        sh 'docker run -d --name "${IMG_API}" -p 8300:8300 "${IMG_AUTH}"'
                        echo 'Deploy End "${IMG_AUTH}"'
                    }
                }
                stage('deploy-app-fe') {
                    when {
                        changeset "front-server/**/*"
                    }
                    steps {
                        echo 'Deploy Start "${IMG_FE}"'
                        sh 'docker ps -f name="${IMG_FE}" -q | xargs --no-run-if-empty docker container stop'
                        sh 'docker container ls -a -f name="${IMG_FE}" -q | xargs -r docker container rm'
                        sh 'docker images --no-trunc --all --quiet --filter="dangling=true" | xargs --no-run-if-empty docker rmi'
                        sh 'docker run -d --name "${IMG_FE}" -p 8200:8200 "${IMG_AUTH}"'
                        echo 'Deploy End "${IMG_FE}"'
                    }
                }
            }
        }
    }
}