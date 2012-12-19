import sbt._
import Keys._
import PlayProject._

object ApplicationBuild extends Build {

    val appName         = "sare-webapp"
    val appVersion      = "2.0.0-SNAPSHOT"

    val appDependencies = Seq(
      // Add your project dependencies here,
      "edu.sabanciuniv.sentilab" % "sare-entitymanager" % "2.0.0-SNAPSHOT"
    )

    val main = PlayProject(appName, appVersion, appDependencies, mainLang = JAVA).settings(
      // Add your own project settings here
      
      resolvers += "Local Maven Respository" at "file:///"+Path.userHome.absolutePath+"/.m2/repository"      
    )

}
