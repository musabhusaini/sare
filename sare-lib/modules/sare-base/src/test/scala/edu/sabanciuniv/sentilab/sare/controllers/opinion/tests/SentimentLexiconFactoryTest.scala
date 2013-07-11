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

package edu.sabanciuniv.sentilab.sare.controllers.opinion.tests

import scala.collection.JavaConversions._

import java.io.File

import org.junit._
import org.junit.Assert._

import edu.sabanciuniv.sentilab.sare.models.opinion._
import edu.sabanciuniv.sentilab.sare.controllers.opinion.SentimentLexiconFactory
import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException

class SentimentLexiconFactoryTest {
	
	private val testTextLexiconFilename = "/test-sent-lex.txt"
	private val testXmlLexiconFilename = "/test-sent-lex.xml"
	
	private val expectedLexicon = new SentimentLexicon()
	expectedLexicon
		.setTitle("test-xml-sent-lex")
		.setDescription("test")
		.setLanguage("en")
	expectedLexicon.addExpression("good", 0.1, 0.2, 0.7)
	expectedLexicon.addExpression("bad", 0.7, 0.2, 0.1)
	
	private def assertLexicaEqual(expected: SentimentLexicon, actual: SentimentLexicon) {
		assertEquals(expected.getTitle, actual.getTitle)
		assertEquals(expected.getDescription, actual.getDescription)
		assertEquals(expected.getLanguage, actual.getLanguage)
		assertTrue(expected.getExpressions forall { sentExp =>
		  	sentExp.equals(actual.findExpression(sentExp.getContent))
		})
	}
	
	@Test
	def testCreateFromTextFile {
		val actualLexicon: SentimentLexicon = try {
			new SentimentLexiconFactory()
				.setTextDelimiter(",")
				.setFile(new File(getClass.getResource(testTextLexiconFilename).getPath))
				.create
		} catch {
		  	case _: Throwable => {
				fail("error reading file")
				null
		  	}
		}
		
		assertNotNull(actualLexicon)
		assertEquals(expectedLexicon.getExpressions.size, actualLexicon.getExpressions.size)
		
		actualLexicon
			.setTitle("test-xml-sent-lex")
			.setDescription("test")
			.setLanguage("en")
		
		assertLexicaEqual(expectedLexicon, actualLexicon)
	}
	
	@Test
	def testCreateFromXmlFile {
		val actualLexicon: SentimentLexicon = try {
			new SentimentLexiconFactory()
				.setTextDelimiter(",")
				.setFile(new File(getClass.getResource(testXmlLexiconFilename).getPath))
				.create
		} catch {
		  	case _: Throwable => {
				fail("error reading file")
				null
		  	}
		}
		
		assertNotNull(actualLexicon)
		assertEquals(expectedLexicon.getExpressions.size, actualLexicon.getExpressions.size)
		assertLexicaEqual(expectedLexicon, actualLexicon)
	}
}