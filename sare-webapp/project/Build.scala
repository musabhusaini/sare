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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
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
			.exclude("junit", "junit")
    )

    val main = play.Project(appName, appVersion, appDependencies).settings(
    	// Add your own project settings here
    	
    	resolvers += "Local Maven Respository" at "file:///"+Path.userHome.absolutePath+"/.m2/repository"      
    )

}