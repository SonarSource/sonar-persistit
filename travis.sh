#!/bin/bash

set -euo pipefail
 
function configureTravis {
  mkdir ~/.local
  curl -sSL https://github.com/SonarSource/travis-utils/tarball/v33 | tar zx --strip-components 1 -C ~/.local
  source ~/.local/bin/install                                                                                                                                   
}

#                                                                                                                                                                                                                      
# A (too) old version of JDK8 is installed by default on Travis.                                                                                                                                                       
# This method is preferred over Travis apt oracle-java8-installer because                                                                                                                                              
# JDK is kept in cache. It does not need to be downloaded from Oracle                                                                                                                                                  
# at each build.                                                                                                                                                                                                       
#                                                                                                                                                                                                                      
function installJdk8 {                                                                                                                                                                                                 
  echo "Setup JDK 1.8u121"                                                                                                                                                                                             
  mkdir -p ~/jvm                                                                                                                                                                                                       
  pushd ~/jvm > /dev/null                                                                                                                                                                                              
  if [ ! -d "jdk1.8.0_121" ]; then                                                                                                                                                                                     
    echo "Download JDK8"                                                                                                                                                                                               
    wget --no-check-certificate -c --header "Cookie: oraclelicense=accept-securebackup-cookie" http://download.oracle.com/otn-pub/java/jdk/8u121-b13/e9e7ea248e2c4826b92b3f075a80e441/jdk-8u121-linux-x64.tar.gz       
    tar xzf jdk-8u121-linux-x64.tar.gz                                                                                                                                                                                 
    rm jdk-8u121-linux-x64.tar.gz                                                                                                                                                                                      
  fi                                                                                                                                                                                                                   
  popd > /dev/null
  export JAVA_HOME=~/jvm/jdk1.8.0_121
  export PATH=$JAVA_HOME/bin:$PATH
}

configureTravis
installJdk8
 
# Enable QA on PR you can allow deployment of PR artifacts to repox
export DEPLOY_PULL_REQUEST=true

# Build (incl. tests), deploy artifacts and triggers Next analysis (incl. JaCoCo)
regular_mvn_build_deploy_analyze
