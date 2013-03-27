# Sentilab SARE #
## A Sentiment Analysis Research Environment ##

[SARE][] is a project of [Sabancı University][Sabanci] [Sentilab Group][Sentilab]. It is a modular and easily extendible environment that supports various sentiment analysis research activities.

[SARE]: http://sare2.sabanciuniv.edu	"Sentilab SARE"
[Sabanci]: http://www.sabanciuniv.edu	"Sabancı University"
[Sentilab]: http://sentilab.sabanciuniv.edu	"Sabancı Sentilab Group"

### Prerequisites ###
1. [Java][].
2. [Maven][].
3. [Play Framework 2][Play].
4. [MySQL][] server running locally.
5. [Git][] (optional if you have the source already).

[Java]: http://www.oracle.com/technetwork/java/javase/downloads/index.html	"JDK download"
[Maven]: http://maven.apache.org/download.cgi	"Maven download"
[Play]: http://www.playframework.com/download	"Play 2 download"
[MySQL]: http://www.mysql.com/downloads/	"MySQL download"
[Git]: http://git-scm.com/downloads	"Git SCM download"

### Installation ###
1.	Clone the project repo.
2.	Run the MySQL script [./scripts/setup.sql][setup.sql] as root to create the required databases and SQL user.
3.	In a command line terminal, set the current directory to the project's root folder and run [./scripts/deploy.bat][deploy.bat] or [./scripts/deploy.sh][deploy.sh] for Windows and Linux respectively.
4.	Now set the current directory to `sare-webapp`, and execute `play run` to start the application.
5.	The web application is up and running and can now be reached at: <http://localhost:9000/>.

**Note:** When deploying to production, the respective scripts suffixed with `-prod` should be used. Production deploy script will also start the application.

[setup.sql]: https://bitbucket.org/sentilab/sare/raw/master/scripts/setup.sql	"SQL setup script"
[deploy.bat]: https://bitbucket.org/sentilab/sare/raw/master/scripts/deploy.bat	"Windows deploy script"
[deploy.sh]: https://bitbucket.org/sentilab/sare/raw/master/scripts/deploy.sh	"Linux deploy script"