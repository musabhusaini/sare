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

object SareBuild extends Build {
	lazy val sareLibrary = Project(id = "sare-lib", base = file("."))
		.aggregate(core, utils, sareBase, sareAlex, sareEntityManager)
	
	lazy val core = Project(id = "core", base = file("modules/core"))
	lazy val utils = Project(id = "utils", base = file("modules/utils"))
		.dependsOn(core % "test->test;compile")
	lazy val sareBase = Project(id = "sare-base", base = file("modules/sare-base"))
		.dependsOn(utils % "test->test;compile")
	lazy val sareAlex = Project(id = "sare-alex", base = file("modules/sare-alex"))
		.dependsOn(sareBase % "test->test;compile")
	lazy val sareEntityManager = Project(id = "sare-entitymanager", base = file("modules/sare-entitymanager"))
		.dependsOn(sareBase % "test->test;compile", sareAlex % "test->test;compile")
}