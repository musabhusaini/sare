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

package edu.sabanciuniv.sentilab.sare.models.opinion.tests

import scala.collection.JavaConversions._
import org.junit._
import org.junit.Assert._
import edu.sabanciuniv.sentilab.sare.models.opinion._

class SentimentLexiconTest {
	
	private val exp = "good"
	private var sentLex: SentimentLexicon = _
	
	@Before
	def setup {
		sentLex = new SentimentLexicon()
	}
	
	@Test
	def testAddExpression {
		val sentExp = sentLex.addExpression(exp)
		assertNotNull(sentExp)
		
		assertEquals(exp, sentExp.getContent)
		assertTrue(sentLex.getExpressions.toSeq contains sentExp)
		
		assertNull(sentLex.addExpression(exp))
	}
	
	@Test
	def testFindExpressionTrue {
		val sentExp = sentLex.addExpression(exp)
		val actualSentExp = sentLex.findExpression(exp)
		
		assertNotNull(actualSentExp)
		assertEquals(sentExp, actualSentExp)
	}
	
	@Test
	def testFindExpressionIgnoreCaseTrue {
		val sentExp = sentLex.addExpression(exp)
		val actualSentExp = sentLex.findExpression(exp.toUpperCase)
		
		assertNotNull(actualSentExp)
		assertEquals(sentExp, actualSentExp)
	}
	
	@Test
	def testFindExpressionFalse {
		val actualSentExp = sentLex.findExpression(exp)
		
		assertNull(actualSentExp)
	}
	
	@Test
	def testHasExpressionTrue {
		val sentExp = sentLex.addExpression(exp)
		assertTrue(sentLex.hasExpression(exp))
	}
	
	@Test
	def testHasExpressionFalse {
		assertFalse(sentLex.hasExpression(exp))
	}
	
	@Test
	def testRemoveExpressionTrue {
		val sentExp = sentLex.addExpression(exp)
		val actualSentExp = sentLex.removeExpression(exp)
		
		assertNotNull(actualSentExp)
		assertEquals(sentExp, actualSentExp)
		assertFalse(sentLex.getExpressions.toSeq contains sentExp)
	}
	
	@Test
	def testRemoveExpressionFalse {
		assertNull(sentLex.removeExpression(exp))
	}
	
	@Test
	def testUpdateExpression {
		val exp2 = "bad"
		val sentExp = sentLex.addExpression(exp)
		val actualSentExp = sentLex.updateExpression(exp, exp2)
		
		assertNotNull(actualSentExp)
		assertEquals(sentExp, actualSentExp)
		assertEquals(exp2, actualSentExp.getContent)
		assertFalse(sentLex.hasExpression(exp))
		assertTrue(sentLex.hasExpression(exp2))
	}
}