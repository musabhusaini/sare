#!/bin/bash

git clean -df
git checkout .
git pull

SECRET_KEY=$(<../secret.key)
echo "application.secret=${SECRET_KEY}" >> sare-webapp/conf/prod.conf

SQL_PWD=$(<../sql.pwd)
echo "db.default.password=${SQL_PWD}" >> sare-webapp/conf/prod.conf

xmlstarlet ed -L -N x="http://java.sun.com/xml/ns/persistence" -u "/x:persistence/x:persistence-unit/x:properties/x:property[@name='javax.persistence.jdbc.password']/@value" -v "${SQL_PWD}" sare-entitymanager/src/main/resources/META-INF/persistence.xml

xmlstarlet ed -L -u "/configuration/appender[@name='DB']/connectionSource/dataSource/password" -v "${SQL_PWD}" sare-webapp/conf/prod-logger.xml

mvn clean compile install
cd sare-webapp
play clean compile stage
./target/start -Dhttp.port=9001 -Dconfig.resource=prod.conf -Dlogger.resource=prod-logger.xml &
cd ..