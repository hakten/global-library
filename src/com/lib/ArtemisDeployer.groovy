def deployer(){

  node {
	properties(
		[parameters(
		[choice(choices: 
		[
		'version/0.1', 
		'version/0.2', 
		'version/0.3', 
		'version/0.4', 
		'version/0.5'], 
	description: 'Which version of the app should I deploy? ', 
	name: 'Version'), 
    text(defaultValue: 'dummy@gmail.com', description: 'Please provide email(s) for notifications. Use , for multiple emails', name: 'EMAIL_TO_SEND'),
	choice(choices: 
	[
		'dev1.huseyinakten.net', 
		'qa1.huseyinakten.net', 
		'stage1.huseyinakten.net', 
		'prod1.huseyinakten.net'], 
	description: 'Please provide an environment to build the application', 
	name: 'ENVIR')])])
	
	stage("Stage1"){
		timestamps {
			ws {
				checkout([$class: 'GitSCM', branches: [[name: '${Version}']], doGenerateSubmoduleConfigurations: false, extensions: [], submoduleCfg: [], userRemoteConfigs: [[url: 'https://github.com/fuchicorp/artemis.git']]])
		}
	}
}
	stage("Install Prerequisites"){
		timestamps {
			ws{
				sh '''
					ssh centos@${ENVIR} sudo yum install epel-release -y
					ssh centos@${ENVIR} sudo yum install python-pip -y 
					ssh centos@${ENVIR} sudo pip install Flask
					'''
		}
	}
}
	stage("Copy Artemis"){
		timestamps {
			ws {
				sh '''
					scp -r * centos@${ENVIR}:/tmp
					'''
		}
	}
}
	stage("Run Artemis"){
		timestamps {
			ws {
				sh '''
					ssh centos@${ENVIR} nohup python /tmp/artemis.py  &
					'''
		}
	}
}
	stage("Send slack notifications"){
		timestamps {
			ws {
				echo "Slack"
				//slackSend color: '#BADA55', message: 'Hello, World!'
			}
		}
	}
    stage("Send Email"){
    mail bcc: '', 
    body: "Hello, your artemis app is deployed to ${ENVIR}", 
    cc: '', 
    from: 'husakten@gmail.com', 
    replyTo: '', 
    subject: "Artemis ${Version} has been deployed", 
    to: "${EMAIL_TO_SEND}"
    }
}
}


