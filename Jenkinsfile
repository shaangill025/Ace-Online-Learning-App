#!/usr/bin/env groovy

node {
    stage('checkout') {
        checkout scm
    }

    stage('check java') {
        sh "java -version"
    }

    stage('clean') {
        sh "chmod +x mvnw"
        sh "./mvnw clean"
    }

    stage('install tools') {
        sh "./mvnw com.github.eirslett:frontend-maven-plugin:install-node-and-yarn -DnodeVersion=v10.14.1 -DyarnVersion=v1.12.3"
    }

    stage('yarn install') {
        sh "./mvnw com.github.eirslett:frontend-maven-plugin:yarn"
    }

    stage('backend tests') {
        try {
            sh "./mvnw test"
        } catch(err) {
            throw err
        } finally {
            junit '**/target/surefire-reports/TEST-*.xml'
        }
    }

    stage('frontend tests') {
        try {
            sh "./mvnw com.github.eirslett:frontend-maven-plugin:yarn -Dfrontend.yarn.arguments='run test'"
        } catch(err) {
            throw err
        } finally {
            junit '**/target/test-results/jest/TESTS-*.xml'
        }
    }

    stage('package and deploy') {
        sh "./mvnw com.heroku.sdk:heroku-maven-plugin:2.0.5:deploy -DskipTests -Pprod -Dheroku.buildpacks=heroku/jvm -Dheroku.appName=ace-online"
        archiveArtifacts artifacts: '**/target/*.war', fingerprint: true
    }
    stage('quality analysis') {
        withSonarQubeEnv('aceaol-sonar') {
            sh "./mvnw sonar:sonar"
        }
    }
}
