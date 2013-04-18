#!/bin/bash

noRun=false
for arg in "$@"
do
	if [ $arg == "-norun" ] ; then
		noRun=false
	fi
done

./scripts/deploy-prod.sh "$@" -norun

cd sare-webapp

if ! $noRun ; then
	./target/start -Dhttp.port=9001 -Dconfig.resource=prod.conf -Dlogger.resource=prod-logger.xml &
fi

cd ..