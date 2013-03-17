#!/bin/bash

git pull
mvn clean compile install
rm -rf $PLAY_HOME/repository/cache/edu.sabanciuniv.sentilab
cd sare-webapp
play clean compile run
