#!/bin/bash

GIT_COMMIT=$(git rev-parse HEAD)

docker push spireon/GLProcessor:latest
docker push spireon/GLProcessor:${GIT_COMMIT}