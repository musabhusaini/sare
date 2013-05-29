//
// Sentilab SARE: a Sentiment Analysis Research Environment
// Copyright (C) 2013 Sabanci University Sentilab
// http://sentilab.sabanciuniv.edu
// 
// This file is part of SARE.
// 
// SARE is free software: you can redistribute it and/or modify
// it under the terms of the GNU General Public License as published by
// the Free Software Foundation, either version 3 of the License, or
// (at your option) any later version.
//  
// SARE is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
// GNU General Public License for more details.
// 
// You should have received a copy of the GNU General Public License
// along with SARE. If not, see <http://www.gnu.org/licenses/>.
//

name := "utils"

libraryDependencies ++= Seq(
	"edu.stanford.nlp" % "stanford-corenlp" % "1.3.4",
	"edu.stanford.nlp" % "stanford-corenlp" % "1.3.4" classifier "models",
	"com.google.code.findbugs" % "annotations" % "2.0.1",
	"com.google.guava" % "guava" % "14.0.1",
	"org.apache.commons" % "commons-lang3" % "3.1",
	"commons-io" % "commons-io" % "2.4",
	"com.google.code.gson" % "gson" % "2.2.4",
	"org.reflections" % "reflections" % "0.9.8" exclude("com.google.guava", "guava")
)