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

package edu.sabanciuniv.sentilab.sare.tests

import org.junit.runner.RunWith
import org.junit.runners.Suite
import org.junit.runners.Suite.SuiteClasses
import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.tests._
import edu.sabanciuniv.sentilab.sare.controllers.opinion.tests._
import edu.sabanciuniv.sentilab.sare.models.base.tests._
import edu.sabanciuniv.sentilab.sare.models.opinion.tests._
import edu.sabanciuniv.sentilab.sare.models.setcover.tests._
import edu.sabanciuniv.sentilab.sare.controllers.setcover.tests._

@RunWith(classOf[Suite])
@SuiteClasses(Array[Class[_]](
	classOf[PersistentObjectTest],
	classOf[OpinionDocumentTest],
	classOf[OpinionCorpusTest],
	classOf[SetCoverFactoryTest],
	classOf[SetCoverDocumentTest],
	classOf[DocumentSetCoverTest],
	classOf[PersistentDocumentControllerTest],
	classOf[PersistentDocumentStoreControllerTest],
	classOf[LexiconBuilderControllerTest],
	classOf[LexiconControllerTest],
	classOf[OpinionCorpusControllerTest],
	classOf[OpinionDocumentControllerTest]
))
class PersistenceTests {
}