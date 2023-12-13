#!/bin/bash
# Licensed to the Apache Software Foundation (ASF) under one
# or more contributor license agreements.  See the NOTICE file
# distributed with this work for additional information
# regarding copyright ownership.  The ASF licenses this file
# to you under the Apache License, Version 2.0 (the
# "License"); you may not use this file except in compliance
# with the License.  You may obtain a copy of the License at
#
#   http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing,
# software distributed under the License is distributed on an
# "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
# KIND, either express or implied.  See the License for the
# specific language governing permissions and limitations
# under the License.

# Setting the script to fail if anything goes wrong
set -e -u -o pipefail
shopt -s failglob

error () {
   echo ""
   echo "** ERROR: $@ **"
   echo ""
   echo "Usage: ./scripts/release/update-branch-versions.sh <release-version> <next-snapshot-version>"
   echo ""
   echo "Must be run from within an activemq-artemis-examples checkout root."
   echo ""
   echo "Example:"
   echo "cd <path.to>/activemq-artemis-examples"
   echo "./scripts/release/update-branch-versions.sh 2.32.0 2.33.0-SNAPSHOT"
   echo ""
   exit 64
}

if [ ! -d examples ] || [ ! -f .asf.yaml ]; then
    error "This script has to be run from inside the root of an activemq-artemis-examples checkout"
    exit 1
fi

if [ "$#" -lt 2 ]; then
  error "The new Artemis / examples release version and the next development version to use must both be specified"
  exit 1
fi

RELEASE_VERSION="$1"
DEVELOPMENT_VERSION="$2"

git checkout development

./scripts/update-examples-version.sh ${RELEASE_VERSION}
git commit -am "Update examples to version ${RELEASE_VERSION}"

git checkout main
git merge development --ff-only

git checkout development
./scripts/update-examples-version.sh ${DEVELOPMENT_VERSION}
git commit -am "Update examples to version ${DEVELOPMENT_VERSION}"

echo ""
echo "Update complete, main branch set to ${RELEASE_VERSION}, and development branch set to ${DEVELOPMENT_VERSION}."
echo ""
echo "Check things over and then push both the *development* and *main* branches."
echo "(optionally use your fork, to test things out before pushing to the main examples repo or even to raise PRs)".

