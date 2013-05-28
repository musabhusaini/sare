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
		javaCore,
		javaJdbc,
		javaEbean,
		"org.twitter4j" % "twitter4j-core" % "[3.0,)",
		"com.mchange" % "c3p0" % "0.9.2.1"
    )

	val core = Project(id = "core", base = file("sare-lib/modules/core"))
	val utils = Project(id = "utils", base = file("sare-lib/modules/utils"))
		.dependsOn(core)
	val sareBase = Project(id = "sare-base", base = file("sare-lib/modules/sare-base"))
		.dependsOn(utils)
	val sareAlex = Project(id = "sare-alex", base = file("sare-lib/modules/sare-alex"))
		.dependsOn(sareBase)
	val sareEntityManager = Project(id = "sare-entitymanager", base = file("sare-lib/modules/sare-entitymanager"))
		.dependsOn(sareBase, sareAlex)
	
	val sareLibrary = Project(id = "sare-lib", base = file("sare-lib"))
		.settings(
		    exportJars in ThisBuild := true
		)
		.aggregate(core, utils, sareBase, sareAlex, sareEntityManager)

	// Only compile the bootstrap bootstrap.less file and any other *.less file in the stylesheets directory
	def customLessEntryPoints(base: File): PathFinder = (
		(base / "app" / "assets" / "stylesheets" / "bootstrap" * "bootstrap.less") +++
		(base / "app" / "assets" / "stylesheets" / "bootstrap" * "responsive.less") +++
		(base / "app" / "assets" / "stylesheets" * "*.less")
	)

    val main = play.Project(appName, appVersion, appDependencies).settings(
    	// Add your own project settings here
        organization := "edu.sabanciuniv.sentilab",
        routesImport ++= Seq("java.util.UUID", "extensions.Binders._"),
    	lessEntryPoints <<= baseDirectory(customLessEntryPoints),
    	resolvers += "Local Maven Respository" at "file:///"+Path.userHome.absolutePath+"/.m2/repository"
    ).dependsOn(sareEntityManager)

}