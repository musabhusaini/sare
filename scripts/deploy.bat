echo call git pull
call mvn clean compile source:jar javadoc:jar install
rd /S/Q c:\play2\repository\cache\edu.sabanciuniv.sentilab
cd sare-webapp
call play clean update compile eclipse
cd..