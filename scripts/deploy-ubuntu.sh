#!/bin/bash

git pull
mvn clean compile install
rm -rf /var/local/play2/repository/cache/edu.sabanciuniv.sentilab
cd sare-webapp
play clean compile run