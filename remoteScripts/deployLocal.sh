
FILE="online-test-suite-0.2-SNAPSHOT.zip"
DIR="/srv/tester/"


systemctl stop tester.service
rm -r ${DIR}"online-test-suite-0.2-SNAPSHOT"
unzip "../jvm/target/universal/"${FILE} -d ${DIR}
cp appconfig.properties ${DIR}"online-test-suite-0.2-SNAPSHOT/bin/"
systemctl start tester.service
