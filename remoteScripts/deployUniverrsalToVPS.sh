#!/bin/bash


USER="cni"
#USER="azu"
PASSWORD=""
HOST="tester"
#HOST="130.193.57.57"
FILE="online-test-suite-0.2-SNAPSHOT.zip"
DIR="tester"


#sshpass -p ${PASSWORD} ssh ${USER}@${HOST} "killall -9 java"
#sshpass -p ${PASSWORD} ssh ${USER}@${HOST} "rm ~/${DIR} -r;cd ~; mkdir ${DIR}"
#cd ../jvm/target/universal || exit
#sshpass -p ${PASSWORD} scp ${FILE} ${USER}@${HOST}:~/${DIR}/${FILE}
#sshpass -p ${PASSWORD} ssh ${USER}@${HOST} "cd ~/${DIR};unzip ${FILE};cd online-test-suite-0.2-SNAPSHOT/bin;./online-test-suite"

ssh ${USER}@${HOST} "killall -9 java"
ssh ${USER}@${HOST} "rm ~/${DIR} -r;cd ~; mkdir ${DIR}"
cd ../jvm/target/universal || exit
scp ${FILE} ${USER}@${HOST}:~/${DIR}/${FILE}
ssh ${USER}@${HOST} "cd ~/${DIR};unzip ${FILE};cd online-test-suite-0.2-SNAPSHOT/bin;./online-test-suite"




