import sbt._
import Keys._
import play.Project._

object ApplicationBuild extends Build {

    val appName         = "sare-webapp"
    val appVersion      = "2.0.0-SNAPSHOT"

    val appDependencies = Seq(
		javaCore, javaJdbc, javaEbean,
		// Add your project dependencies here,
		("edu.sabanciuniv.sentilab" % "sare-entitymanager" % "2.0.0-SNAPSHOT")
			.exclude("org.apache.commons", "commons-lang3")
			.exclude("com.google.guava", "guava")
			.exclude("org.reflections", "reflections")
			.exclude("joda-time", "joda-time")
			.exclude("junit", "junit")
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
    	// Add your own project settings here
    	
    	resolvers += "Local Maven Respository" at "file:///"+Path.userHome.absolutePath+"/.m2/repository"      
    )

}
