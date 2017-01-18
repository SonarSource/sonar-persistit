#!/bin/bash

set -euo pipefail
 
function configureTravis {
  mkdir ~/.local
  curl -sSL https://github.com/SonarSource/travis-utils/tarball/v33 | tar zx --strip-components 1 -C ~/.local
  source ~/.local/bin/install                                                                                                                                   
}

configureTravis
 
# Enable QA on PR you can allow deployment of PR artifacts to repox
export DEPLOY_PULL_REQUEST=true

# Build (incl. tests), deploy artifacts and triggers Next analysis (incl. JaCoCo)
regular_mvn_build_deploy_analyze
