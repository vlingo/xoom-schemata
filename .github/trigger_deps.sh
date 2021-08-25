#!/bin/sh

# Params:
#   - Downstream module name (e.g. xoom-actors)
#   - Release version (e.g. 1.0.4)
#   - Next development version (e.g. 1.0.5-SNAPSHOT)
trigger_dependency()
{
    echo "Triggering $1 v$2. Next development version $3"

    cd $GITHUB_WORKSPACE/..
    git clone --depth=50 --branch=master https://${RELEASE_GITHUB_TOKEN}@github.com/vlingo/$1.git
    cd $1

    [ -f .github/release.sh ] && ./.github/release.sh $2
    [ -f .github/next-dev.sh ] && ./.github/next-dev.sh $3
}

VERSION=$(mvn help:evaluate -Dexpression=project.version -q -DforceStdout)
echo "Detected version $VERSION"

git config --global user.email "vaughn@kalele.io"
git config --global user.name "Vaughn Vernon"

# New version
MAJOR=$(echo $VERSION | cut -f 1 -d '.')
MINOR=$(echo $VERSION | cut -f 2 -d '.')
PATCH=$(echo $VERSION | cut -f 3 -d '.')
NEW_VERSION=$MAJOR.$MINOR.$(($PATCH + 1))-SNAPSHOT

for dependency in "xoom-designer";
do
    trigger_dependency $dependency $VERSION $NEW_VERSION
done
