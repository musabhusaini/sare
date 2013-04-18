#!/bin/bash

bypassRepo=false
skipBackend=false
clearDependencyCache=false
stopApp=false
noRun=false

for arg in "$@"
do
	if [ $arg == "-br" ] ; then
		bypassRepo=true
	elif [ $arg == "-sb" ] ; then
		skipBackend=true
	elif [ $arg == "-cdc" ] ; then
		clearDependencyCache=true
	elif [ $arg == "-stop" ] ; then
		stopApp=true
	elif [ $arg == "-norun" ] ; then
		noRun=false
	elif [ $arg == "--help" ] ; then
		echo "Options:"
		echo "--help: display this help message"
		echo "-br: bypass repository and use local copy"
		echo "-sb: skip compilation and installation of backend"
		echo "-cdc: clear Play's dependency cache"
		echo "-stop: stop the web app before compiling it"
		echo "-norun: do not run the web app after compile"
		exit
	fi
done

if ! $bypassRepo ; then
	git clean -df
	git checkout .
	git pull
fi

SECRET_KEY=$(<../secret.key)
echo "application.secret=${SECRET_KEY}" >> sare-webapp/conf/prod.conf

SQL_PWD=$(<../sql.pwd)
echo "db.default.password=${SQL_PWD}" >> sare-webapp/conf/prod.conf

if ! $skipBackend ; then
	xmlstarlet ed -L -N x="http://java.sun.com/xml/ns/persistence" -u "/x:persistence/x:persistence-unit/x:properties/x:property[@name='javax.persistence.jdbc.password']/@value" -v "${SQL_PWD}" sare-entitymanager/src/main/resources/META-INF/persistence.xml

	mvn clean compile install
fi

xmlstarlet ed -L -u "/configuration/appender[@name='DB']/connectionSource/dataSource/password" -v "${SQL_PWD}" sare-webapp/conf/prod-logger.xml

cd sare-webapp

if $stopApp ; then
	play stop
fi

if $clearDependencyCache ; then
	rm -rf $PLAY_HOME/repository/cache/edu.sabanciuniv.sentilab
fi

play clean compile stage

if ! $noRun ; then
	./target/start -Dconfig.resource=prod.conf -Dlogger.resource=prod-logger.xml &
fi

cd ..