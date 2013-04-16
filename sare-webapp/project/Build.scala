/*
 * Sentilab SARE: a Sentiment Analysis Research Environment
 * Copyright (C) 2013 Sabanci University Sentilab
 * http://sentilab.sabanciuniv.edu
 * 
 * This file is part of SARE.
 * 
 * SARE is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *  
 * SARE is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

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
				.exclude("junit", "junit"),
			"org.twitter4j" % "twitter4j-core" % "[3.0,)",
			"com.mchange" % "c3p0" % "0.9.2.1"
    )

	// Only compile the bootstrap bootstrap.less file and any other *.less file in the stylesheets directory
	def customLessEntryPoints(base: File): PathFinder = (
		(base / "app" / "assets" / "stylesheets" / "bootstrap" * "bootstrap.less") +++
		(base / "app" / "assets" / "stylesheets" / "bootstrap" * "responsive.less") +++
		(base / "app" / "assets" / "stylesheets" * "*.less")
	)

    val main = play.Project(appName, appVersion, appDependencies).settings(
    	// Add your own project settings here
        routesImport ++= Seq("java.util.UUID", "extensions.Binders._"),
    	lessEntryPoints <<= baseDirectory(customLessEntryPoints),
    	resolvers += "Local Maven Respository" at "file:///"+Path.userHome.absolutePath+"/.m2/repository"      
    )

}