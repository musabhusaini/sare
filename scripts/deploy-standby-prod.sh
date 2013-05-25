#!/bin/bash

. scripts/deploy-prod.sh "$@" -norun

noRun=false
for arg in "$@"
do
	if [ $arg == "-norun" ] ; then
		noRun=true
	fi
done

cd sare-webapp

if ! $noRun ; then
	./target/start -Dhttp.port=9001 -Dconfig.resource=prod.conf -Dlogger.resource=prod-logger.xml &
fi

cd ..