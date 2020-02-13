properties([[$class: 'JiraProjectProperty'], parameters([string(defaultValue: 'v1', description: 'Please provide a version number.', name: 'APP_VERSION', trim: false)])])
node {
    stage("pull repo"){
        git 'https://github.com/hakten/nodejs.git'
    }
    stage("build image"){
        sh "docker build -t app1:${APP_VERSION} ."
    }
    stage("tag image"){
        sh '''docker tag app1:${APP_VERSION} 103872286656.dkr.ecr.eu-west-1.amazonaws.com/app1:${APP_VERSION}'''
    }
    stage("login to ECR"){
        sh '''$(aws ecr get-login --no-include-email --region eu-west-1)'''
    }
    stage("push image"){
            sh "docker images"
            sh "docker push 103872286656.dkr.ecr.eu-west-1.amazonaws.com/app1:${APP_VERSION}"
    }
    stage("notify"){
        sh "echo Hello"   
    }
    
}
