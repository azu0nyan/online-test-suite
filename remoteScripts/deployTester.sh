
USER="azu"
HOST="tester"
RUNUSER="tester"

FILETOCOPY="../jvm/target/universal/online-test-suite-0.2-SNAPSHOT.zip"
FILE="online-test-suite-0.2-SNAPSHOT.zip"
DIR="/srv/tester/"


ssh ${USER}@${HOST} sudo -S systemctl stop tester.service

ssh ${USER}@${HOST} rm ${FILE}
ssh ${USER}@${HOST} rm -r ${DIR}"online-test-suite-0.2-SNAPSHOT"

scp ${FILETOCOPY} ${USER}@${HOST}:~/${FILE}
ssh ${USER}@${HOST} unzip ~/${FILE} -d ${DIR}

scp appconfig.properties ${USER}@${HOST}:${DIR}online-test-suite-0.2-SNAPSHOT/bin/

#ssh ${USER}@${HOST} chown -R ${RUNUSER} ${DIR}

ssh ${USER}@${HOST} sudo -S systemctl start tester.service
