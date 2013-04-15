#!/bin/bash

git clean -df
git checkout .
git pull

echo -n "application.secret=" >> sare-webapp/conf/prod.conf
cat ../secret.key >> sare-webapp/conf/prod.conf

SQL_PWD=$(<../sql.pwd)
echo -n "db.default.password=${SQL_PWD}" >> sare-webapp/conf/prod.conf
xmlstarlet ed -N x="http://java.sun.com/xml/ns/persistence" -u "/x:persistence/x:persistence-unit/x:properties/x:property[@name='javax.persistence.jdbc.password']/@value" -v "${SQL_PWD}" sare-entitymanager/src/main/resources/META-INFpersistence.xml

mvn clean compile install
rm -rf $PLAY_HOME/repository/cache/edu.sabanciuniv.sentilab
cd sare-webapp
play clean compile stage
./target/start -Dconfig.resource=prod.conf &
cd ..