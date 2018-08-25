node {
  try {
    stage('Checkout') {
      checkout scm
    }
    stage('Environment') {
      sh 'git --version'
      echo "Branch: master"
      sh 'docker -v'
      sh 'printenv'
    }
    stage('Deploy'){
      if(env.BRANCH_NAME == 'master'){
        try {
          sh 'cp /home/DNAenv.json .'
        } catch (err) {}

        try {
          sh 'docker rmi -f socketapiserver'
        } catch (err) {}

        try {
          sh 'docker rm -f socketapiserver'   
        } catch (err) {}

        try {
          sh 'docker rmi $(docker images -f "dangling=true" -q)'
        } catch (err) {}
        
        sh 'docker build -t socketapiserver --no-cache .'
        sh 'docker run -d -p 9013:9013 --name=socketapiserver socketapiserver:latest'     
      }
    }
  } catch (e) {
    currentBuild.result = "FAILED"
    throw e
  } finally {
    notifyBuild(currentBuild.result)
  }
}

def notifyBuild(String buildStatus = 'STARTED') {
  buildStatus =  buildStatus ?: 'SUCCESSFUL'

  def colorName = 'RED'
  def colorCode = '#F7387D'
  def subject = "${buildStatus} : Job to Deployment '${env.JOB_NAME} [#${env.BUILD_NUMBER}]'"
  def summary = "${subject} (${env.BUILD_URL})"

  if (buildStatus == 'STARTED') {
    color = 'YELLOW'
    colorCode = '#FFFF00'
  } else if (buildStatus == 'SUCCESSFUL') {
    color = 'BLUE'
    colorCode = '#50ABBB'
  } else {
    color = 'RED'
    colorCode = '#F7387D'
  }

  slackSend (color: colorCode, message: summary)
}