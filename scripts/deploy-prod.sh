#!/bin/bash

bypassRepo=false
stopApp=false
noRun=false

for arg in "$@"
do
	if [ $arg == "-br" ] ; then
		bypassRepo=true
	elif [ $arg == "-stop" ] ; then
		stopApp=true
	elif [ $arg == "-norun" ] ; then
		noRun=true
	elif [ $arg == "--help" ] ; then
		echo "Options:"
		echo "--help: display this help message"
		echo "-br: bypass repository and use local copy"
		echo "-stop: stop the web app before compiling it"
		echo "-norun: do not run the web app after compile"
		return
	fi
done

if ! $bypassRepo ; then
	git clean -df
	git checkout .
	git pull
fi

SECRET_KEY=$(<../secret.key)
echo "application.secret=\"${SECRET_KEY}\"" >> ./sare-webapp/conf/prod.conf

SQL_PWD=$(<../sql.pwd)
echo "db.default.password=\"${SQL_PWD}\"" >> ./sare-webapp/conf/prod.conf

xmlstarlet ed -L -N x="http://java.sun.com/xml/ns/persistence" -u "/x:persistence/x:persistence-unit/x:properties/x:property[@name='javax.persistence.jdbc.password']/@value" -v "${SQL_PWD}" ./sare-lib/sare-entitymanager/src/main/resources/META-INF/persistence.xml

xmlstarlet ed -L -u "/configuration/appender[@name='DB']/connectionSource/dataSource/password" -v "${SQL_PWD}" ./sare-webapp/conf/prod-logger.xml

cd sare-webapp

if ! [ -e "./sare-lib" ] ; then
	ln -s ../sare-lib .
fi

if $stopApp ; then
	play stop
fi

play clean compile stage

if ! $noRun ; then
	./target/start -Dconfig.resource=prod.conf -Dlogger.resource=prod-logger.xml &
fi

cd ..