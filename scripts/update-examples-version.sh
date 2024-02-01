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

# Update root pom parent pom version
echo "Updating root pom *parent* version"
sed -i.bak "/<parent>/,/<version>/ s~<version>[^<]*~<version>${NEW_VERSION}~g w /dev/stdout" ./pom.xml
rm -f pom.xml.bak

# Update root pom version and all the child modules to match
echo "Updating root pom version and child modules"
mvn versions:set -DgroupId="org.apache.activemq.examples.*" -DgenerateBackupPoms=false -DprocessAllModules=true -DnewVersion="${NEW_VERSION}" -DprocessFromLocalAggregationRoot=false
