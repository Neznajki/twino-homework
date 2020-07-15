#!/bin/sh
set -e

ls -al /usr/local/bin

sudo -E /usr/local/bin/addUser.sh
sudo -E /usr/local/bin/initConfigs.sh

cat /etc/passwd
cd /project && mvn spring-boot:run
