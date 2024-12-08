pipeline {
    agent any
    tools {
        jdk "jdk-21" // valid options are: "jdk-8", "jdk-16", "jdk-17" or "jdk-21", choose which one you need
    }
    stages {
        stage('Clean') {
            steps {
                echo 'Cleaning Project'
                sh 'chmod +x gradlew'
                sh './gradlew clean'
            }
        }
        stage('Build & Publish') {
            steps {
                echo 'Building & Publishing'
                sh './gradlew build publish'
            }
        }
    }
    post {
        always {
            discordSend(
                webhookURL: "https://discord.com/api/webhooks/1313642177083216073/H8EnQ65JusCjpqGTqLXDZI1BYBe4ArLJn3xSjEOWthg4Nwa7ZFSHu1SYag8fU4v4DB8k"
                title: "GTCore ${TAG_NAME} #${BUILD_NUMBER}"
                link: env.BUILD_URL
                result: currentBuild.currentResult
                description: "Build: [${BUILD_NUMBER}](${env.BUILD_URL})\nStatus: ${currentBuild.currentResult}"
            )
        }
    }
}
