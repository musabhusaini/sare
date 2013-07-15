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

package edu.sabanciuniv.sentilab.sare.models.aspect.extraction.tests

import scala.collection.JavaConversions._

import org.junit.Assert._
import org.junit._

import edu.sabanciuniv.sentilab.sare.models.opinion._
import edu.sabanciuniv.sentilab.sare.models.aspect.extraction._

/**
 * @author Mus'ab Husaini
 */
class AspectExprExtrDocumentStoreTest {

	val documentText1 = "this is a very good hotel."
	val corpus = new OpinionCorpus
	var extractorStore: AspectExprExtrDocumentStore = _
	
	@Before
	def setup {
		corpus.setLanguage("en")
		corpus.addDocument(new OpinionDocument().setContent(documentText1))
		corpus.addDocument(new OpinionDocument().setContent("this hotel was great. the staff were amazing."))
		corpus.addDocument(new OpinionDocument().setContent("this is an excellent hotel. the staff were very helpful. i loved the service."))
		
		extractorStore = new AspectExprExtrDocumentStore(corpus)
	}

	@Test
	def testGetCandidateExpressions {
		assertNotNull(extractorStore.getCandidateExpressions)
		assertEquals(3, extractorStore.getCandidateExpressions.size)
		assertTrue(extractorStore.getCandidateExpressions.toSeq.contains("hotel"))
		assertTrue(extractorStore.getCandidateExpressions.toSeq.contains("staff"))
		assertTrue(extractorStore.getCandidateExpressions.toSeq.contains("service"))
	}
	
	@Test
	def testGetExtractorDocuments {
		assertNotNull(extractorStore.getExtractorDocuments)
		assertEquals(3, extractorStore.getExtractorDocuments.size)
	}
	
	@Test
	def testSetBaseStoreResetsCandidateExpressions {
		extractorStore.setBaseStore(null)
		assertEquals(0, extractorStore.getCandidateExpressions.size)
	}
	
	@Test
	def testCandidateExpressions {
		val candidateExpression = extractorStore.getCandidateExpressions find { _ equals "hotel" } getOrElse null
		assertNotNull(candidateExpression)
		assertEquals(3, candidateExpression.sentences.size)
		assertTrue(candidateExpression.sentences exists { _.getText equals documentText1 })
	}
	
	@Test
	def testAutoLabelCandidateExpressions {
		val (labeled, unlabeled) = extractorStore.autoLabelCandidateExpressions(1)
		assertNotNull(labeled)
		assertNotNull(unlabeled)
		
		assertEquals(2, labeled.size)
		assertEquals(1, unlabeled.size)
		
		val hotel = labeled find { _ equals "hotel" } getOrElse null
		assertNotNull(hotel)
		assertTrue(hotel.label isDefined)
		assertTrue(hotel.label get)
		
		val service = labeled find { _ equals "service" } getOrElse null
		assertNotNull(service)
		assertTrue(service.label isDefined)
		assertFalse(service.label get)
		
		val staff = unlabeled find { _ equals "staff" } getOrElse null
		assertNotNull(staff)
		assertTrue(staff.label isEmpty)
	}
	
	@Test
	def testSplitLabeledCandidateExpressions {
		extractorStore.autoLabelCandidateExpressions(1)
		val (positive, negative) = extractorStore.splitLabeledCandidateExpressions
		
		assertEquals(1, positive.size)
		assertEquals(1, negative.size)
		
		val hotel = positive find { _ equals "hotel" } getOrElse null
		assertNotNull(hotel)
		assertTrue(hotel.label isDefined)
		assertTrue(hotel.label get)

		val service = negative find { _ equals "service" } getOrElse null
		assertNotNull(service)
		assertTrue(service.label isDefined)
		assertFalse(service.label get)
	}
}