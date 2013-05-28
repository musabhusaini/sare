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

name := "sare-lib"

scalaVersion in ThisBuild := "2.10.0"

organization in ThisBuild := "edu.sabanciuniv.sentilab"

version in ThisBuild := "2.0.1"

libraryDependencies in ThisBuild += "com.novocode" % "junit-interface" % "0.10-M4" % "test"

parallelExecution in Test in ThisBuild := false

EclipseKeys.createSrc in ThisBuild := EclipseCreateSrc.Default + EclipseCreateSrc.Resource

EclipseKeys.withSource in ThisBuild := true