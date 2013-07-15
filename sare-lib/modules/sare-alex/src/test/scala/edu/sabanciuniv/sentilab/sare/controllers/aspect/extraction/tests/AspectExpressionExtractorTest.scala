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

package edu.sabanciuniv.sentilab.sare.controllers.aspect.extraction.tests

import scala.collection.JavaConversions._

import java.io.File

import org.junit.Assert._
import org.junit._

import edu.sabanciuniv.sentilab.sare.controllers.opinion._
import edu.sabanciuniv.sentilab.sare.controllers.aspect.extraction.AspectExpressionExtractor
import edu.sabanciuniv.sentilab.sare.models.opinion._
import edu.sabanciuniv.sentilab.sare.models.aspect.extraction._

/**
 * @author Mus'ab Husaini
 */
class AspectExpressionExtractorTest {
	
	var corpus: OpinionCorpus = _
	
	var sentimentLexicon: SentimentLexicon = _
	
	var extractor: AspectExpressionExtractor = _
	
	@Before
	def setup {
	  	corpus = try {
			new OpinionCorpusFactory()
				.setFile(new File(getClass.getResource("/test-corpus.zip").getPath))
				.setLanguage("en")
				.create
		} catch {
		  	case _: Throwable => { fail("error reading input file"); null }
		}
		
		extractor = new AspectExpressionExtractor(corpus)
	}

	@Test
	def testCreate {
		val aspectLexicon = extractor.create
		assertNotNull(aspectLexicon)
		
		val expressionsAspect = aspectLexicon.findAspect(AspectExpressionExtractor.expressionsAspectTitle)
		assertNotNull(expressionsAspect)
		assertEquals(42, expressionsAspect.getExpressions.size)
		assertTrue(expressionsAspect.getExpressions.toSeq.contains("hotel"))
		assertTrue(expressionsAspect.getExpressions.toSeq.contains("room"))
		assertTrue(expressionsAspect.getExpressions.toSeq.contains("location"))
		
		val notExpressionsAspect = aspectLexicon.findAspect(AspectExpressionExtractor.notExpressionsAspectTitle)
		assertNotNull(notExpressionsAspect)
		assertEquals(126, notExpressionsAspect.getExpressions.size)
		assertTrue(notExpressionsAspect.getExpressions.toSeq.contains("darling"))
		assertTrue(notExpressionsAspect.getExpressions.toSeq.contains("person"))
		assertTrue(notExpressionsAspect.getExpressions.toSeq.contains("hand"))
		
		val unsureExpressionsAspect = aspectLexicon.findAspect(AspectExpressionExtractor.unsureExpressionsAspectTitle)
		assertNotNull(unsureExpressionsAspect)
		assertTrue(unsureExpressionsAspect.getExpressions.isEmpty)
	}
}