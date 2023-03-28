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
        APP_CDN = 'cdn-server'
        IMG_AUTH = 'huyeon123/conmoto-auth'
        IMG_API = 'huyeon123/conmoto-api'
        IMG_FE = 'huyeon123/conmoto-front'
        IMG_CDN = 'huyeon123/conmoto-cdn'
        ENCRYPT_KEY = sh(script: "echo $ENCRYPT_KEY", returnStdout: true).trim()
        CONFIG_SERVER = sh(script: "echo $CONFIG_SERVER", returnStdout: true).trim()
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
                stage('build-cdn-server') {
                    when {
                        changeset "cdn-server/**/*"
                    }
                    steps {
                        echo 'Build Start "${APP_CDN}"'
                        sh './gradlew ${APP_CDN}:bootJar'
                        echo 'Build End "${APP_CDN}"'
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
                        sh 'sudo docker build -t "${IMG_AUTH}" --build-arg ENCRYPT_KEY="${ENCRYPT_KEY}" --build-arg CONFIG_SERVER="${CONFIG_SERVER}" "${APP_AUTH}"'
                        echo 'Docker Build End "${IMG_AUTH}"'
                    }
                }
                stage('docker-build-api-server') {
                    when {
                        changeset "api-server/**/*"
                    }
                    steps {
                        echo 'Docker Build Start "${IMG_API}"'
                        sh 'sudo docker build -t "${IMG_API}" --build-arg ENCRYPT_KEY="${ENCRYPT_KEY}" --build-arg CONFIG_SERVER="${CONFIG_SERVER}" "${APP_API}"'
                        echo 'Docker Build End "${IMG_API}"'
                    }
                }
                stage('docker-build-front-server') {
                    when {
                        changeset "front-server/**/*"
                    }
                    steps {
                        echo 'Docker Build Start "${IMG_FE}"'
                        sh 'sudo docker build -t "${IMG_FE}" --build-arg ENCRYPT_KEY="${ENCRYPT_KEY}" --build-arg CONFIG_SERVER="${CONFIG_SERVER}" "${APP_FE}"'
                        echo 'Docker Build End "${IMG_FE}"'
                    }
                }
                stage('docker-build-cdn-server') {
                    when {
                        changeset "cdn-server/**/*"
                    }
                    steps {
                        echo 'Docker Build Start "${IMG_CDN}"'
                        sh 'sudo docker build -t "${IMG_CDN}" --build-arg ENCRYPT_KEY="${ENCRYPT_KEY}" --build-arg CONFIG_SERVER="${CONFIG_SERVER}" "${APP_CDN}"'
                        echo 'Docker Build End "${IMG_CDN}"'
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
                stage('deploy-app-cdn') {
                    when {
                        changeset "cdn-server/**/*"
                    }
                    steps {
                        echo 'Deploy Start "${IMG_CDN}"'
                        sh 'sudo docker ps -f name="${APP_CDN}" -q | sudo xargs --no-run-if-empty docker container stop'
                        sh 'sudo docker container ls -a -f name="${APP_CDN}" -q | sudo xargs -r docker container rm'
                        sh 'sudo docker images --no-trunc --all --quiet --filter="dangling=true" | sudo xargs --no-run-if-empty docker rmi'
                        sh 'sudo docker run -d --name "${APP_CDN}" -p 8400:8400 -v ~:/config "${IMG_CDN}"'
                        echo 'Deploy End "${IMG_CDN}"'
                    }
                }
            }
        }
    }
}