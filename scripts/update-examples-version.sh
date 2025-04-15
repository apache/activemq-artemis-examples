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
   echo "Usage: ./scripts/update-examples-version.sh <new-version>"
   echo ""
   echo "Must be run from within an activemq-artemis-examples checkout root."
   echo ""
   echo "Example:"
   echo "cd <path.to>/activemq-artemis-examples"
   echo "./scripts/update-examples-version.sh 2.32.0"
   echo ""
   exit 64
}

if [ ! -d examples ] || [ ! -f .asf.yaml ]; then
    error "This script has to be run from inside the root of an activemq-artemis-examples checkout"
    exit 1
fi

if [ "$#" -lt 1 ]; then
  error "The new Artemis / examples version to use must be specified"
  exit 1
fi


NEW_VERSION="$1"
echo "Setting examples version to ${NEW_VERSION}"

# Update base pom version element
sed -i.bak "/<\/parent>/,/<version>/ s~<version>[^<]*~<version>${NEW_VERSION}~g" ./pom.xml

# Update parent pom version element for base pom and all other poms
find . -path '*/target' -type d -prune -false -o -type f -name 'pom.xml' -print0 | xargs -0 sed -i.bak "/<parent>/,/<version>/ s~<version>[^<]*~<version>${NEW_VERSION}~g"

# Clear out pom.xml.bak files
find . -name "pom.xml.bak" -type f -exec rm -f '{}' \;
