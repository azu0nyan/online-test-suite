#!/bin/bash

# htop tmux unzip openjdk-14-jdk firejail


USER="azu"

HOST="130.193.57.57"

ssh ${USER}@${HOST} sudo apt-get install gnupg
ssh ${USER}@${HOST} wget -qO - https://www.mongodb.org/static/pgp/server-4.4.asc | sudo apt-key add -
ssh ${USER}@${HOST} echo "deb [ arch=amd64,arm64 ] https://repo.mongodb.org/apt/ubuntu focal/mongodb-org/4.4 multiverse" | sudo tee /etc/apt/sources.list.d/mongodb-org-4.4.list
ssh ${USER}@${HOST} sudo apt-get update
ssh ${USER}@${HOST} sudo apt-get install -y mongodb-org
ssh ${USER}@${HOST} sudo systemctl start mongod
ssh ${USER}@${HOST} sudo systemctl enable mongod


#sudo apt-get install \
#    apt-transport-https \
#    ca-certificates \
#    curl \
#    gnupg-agent \
#    software-properties-common
#curl -fsSL https://download.docker.com/linux/ubuntu/gpg | sudo apt-key add -
#sudo apt-key fingerprint 0EBFCD88
#sudo add-apt-repository \
#   "deb [arch=amd64] https://download.docker.com/linux/ubuntu \
#   $(lsb_release -cs) \
#   stable"
#sudo apt-get update
# sudo apt-get install docker-ce docker-ce-cli containerd.io