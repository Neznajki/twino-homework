#!/bin/sh
set -e

NEW_USER=${DOCKER_USER:-developer}
NEW_USER_ID=${DOCKER_USER_ID:-1000}
NEW_USER_GID=${DOCKER_USER_GID:-1000}

if [ -w "/etc/passwd" ]
then
  echo "${NEW_USER}::${NEW_USER_ID}:${NEW_USER_GID}:${NEW_USER}:/home/${NEW_USER}:/bin/bash" >> /etc/passwd
fi