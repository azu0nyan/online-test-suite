#!/bin/bash


USER="root"
PASSWORD=""
HOST="213.178.155.74"
FILE="online-test-suite-0.2-SNAPSHOT.zip"
DIR="tester"

#sshpass -p ${PASSWORD} ssh ${USER}@${HOST} "lscpu;lscpu;lscpu"
sshpass -p ${PASSWORD} ssh ${USER}@${HOST} "killall -9 java"
sshpass -p ${PASSWORD} ssh ${USER}@${HOST} "rm ~/${DIR} -r;cd ~; mkdir ${DIR}"
cd ../jvm/target/universal || exit
ls | grep .zip
sshpass -p ${PASSWORD} scp ${FILE} ${USER}@${HOST}:~/${DIR}/${FILE}
sshpass -p ${PASSWORD} ssh ${USER}@${HOST} "cd ~/${DIR};unzip ${FILE};cd online-test-suite-0.2-SNAPSHOT/bin;./online-test-suite"




