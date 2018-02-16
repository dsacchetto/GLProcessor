#!/bin/bash

GIT_COMMIT=$(git rev-parse HEAD)
# BUILD_TIME=$(date -u +"%Y-%m-%dT%H:%M:%SZ" | tr -d '\n')
DOCKER_REPO="spireon/GLProcessor"
DOCKER_RELEASE_TAG="${DOCKER_REPO}:${GIT_COMMIT}"
DOCKER_LATEST_TAG="${DOCKER_REPO}:latest"


die() {
	echo $1
	exit 1
}

rm -rf build
mkdir build
cp ../target/GLProcessor-1.0-SNAPSHOT-jar-with-dependencies.jar build/
echo "Building spireon/glservice...${DOCKER_RELEASE_TAG}"
# docker pull spireon/spireonapp:latest
docker build --no-cache --build-arg GIT_COMMIT=${GIT_COMMIT}  -t ${DOCKER_RELEASE_TAG} . || die "Failed to build"
docker tag ${DOCKER_RELEASE_TAG} ${DOCKER_LATEST_TAG}