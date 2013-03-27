# Sentilab SARE #
## A Sentiment Analysis Research Environment ##

[SARE](http://sare2.sabanciuniv.edu) is a project of Sabancı University Sentilab Group. It is a modular and easily extendible environment that supports various sentiment analysis research activities.

### Prerequisites ###
1. [Java](http://www.oracle.com/technetwork/java/javase/downloads/index.html).
2. [Maven](http://maven.apache.org/download.cgi).
3. [Play Framework 2](http://www.playframework.com/download).
4. [MySQL](http://www.mysql.com/downloads/) server running locally.
5. [Git](http://git-scm.com/downloads) (optional if you have the source already).

### Installation ###
1.	Clone the repo using:
		
		:::shell
		git clone git@bitbucket.org:musabhusaini/sare.git

2.	While logged in as root, run the MySQL script [scripts\setup.sql][setup.sql] to create the required databases and user.
3.	Run the batch script [scripts\deploy.bat][deploy.bat] for Windows or the shell script [scripts\deploy.sh][deploy.sh] for Linux.
4.	From command line, `cd` into `sare-webapp`, and execute `play run` to start the application.
5.	The web application is up and running and can now be reached at: <http://localhost:9000/>.

**Hint:** When deploying to production, the respective scripts suffixed with `prod` can be used. The production deploy script will also start the application.

[setup.sql]: https://bitbucket.org/musabhusaini/sare/raw/master/scripts/setup.sql	"SQL setup script"
[deploy.bat]: https://bitbucket.org/musabhusaini/sare/raw/master/scripts/deploy.bat	"Windows deploy script"
[deploy.sh]: https://bitbucket.org/musabhusaini/sare/raw/master/scripts/deploy.sh	"Linux deploy script"