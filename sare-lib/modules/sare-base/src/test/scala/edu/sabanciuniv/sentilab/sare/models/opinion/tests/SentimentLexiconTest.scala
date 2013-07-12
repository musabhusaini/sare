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
	private val pos1 = "adjective"
	private val pos2 = "noun"
	private var sentLex: SentimentLexicon = _
	
	@Before
	def setup {
		sentLex = new SentimentLexicon()
	}
	
	@Test
	def testAddExpression {
		val sentExp1 = sentLex.addExpression(exp, pos1)
		
		assertNotNull(sentExp1)
		assertEquals(exp, sentExp1.getContent)
		assertEquals(pos1, sentExp1.getPos)
		assertTrue(sentLex.getExpressions.toSeq contains sentExp1)
		assertNull(sentLex.addExpression(exp, pos1))
		
		val sentExp2 = sentLex.addExpression(exp, pos2)
		
		assertNotNull(sentExp2)
		assertEquals(exp, sentExp2.getContent)
		assertEquals(pos2, sentExp2.getPos)
		assertTrue(sentLex.getExpressions.toSeq contains sentExp2)
	}
	
	@Test
	def testFindExpressionTrue {
		val sentExp1 = sentLex.addExpression(exp, pos1)
		val sentExp2 = sentLex.addExpression(exp, pos2)
		val actualSentExp1 = sentLex.findExpression(exp, pos1)
		val actualSentExp2 = sentLex.findExpression(exp, pos2)
		
		assertNotNull(actualSentExp1)
		assertNotNull(actualSentExp2)
		assertEquals(sentExp1, actualSentExp1)
		assertEquals(sentExp2, actualSentExp2)
	}
	
	@Test
	def testFindExpressionIgnoreCaseTrue {
		val sentExp = sentLex.addExpression(exp, pos1)
		
		assertEquals(sentExp, sentLex.findExpression(exp.toUpperCase, pos1))
	}
	
	@Test
	def testFindExpressionFalse {
		assertNull(sentLex.findExpression(exp, pos1))
		
		sentLex.addExpression(exp, pos1)
		assertNull(sentLex.findExpression(exp, pos2))
	}
	
	@Test
	def testFindExpressionsTrue {
		val sentExp1 = sentLex.addExpression(exp, pos1)
		val sentExp2 = sentLex.addExpression(exp, pos2)
		
		sentLex.addExpression("something")
		val sentExps = sentLex.findExpressions(exp)
		
		assertNotNull(sentExps)
		assertEquals(2, sentExps.size)
		assertTrue(sentExps.toSeq.contains(sentExp1))
		assertTrue(sentExps.toSeq.contains(sentExp2))
	}
	
	@Test
	def testFindExpressionsFalse {
		sentLex.addExpression("something")
		assertTrue(sentLex.findExpressions(exp).size == 0)
	}
	
	@Test
	def testHasExpressionTrue {
		val sentExp1 = sentLex.addExpression(exp, pos1)
		val sentExp2 = sentLex.addExpression(exp, pos2)
		
		assertTrue(sentLex.hasExpression(exp))
		assertTrue(sentLex.hasExpression(exp, pos1))
		assertTrue(sentLex.hasExpression(exp, pos2))
	}
	
	@Test
	def testHasExpressionFalse {
		assertFalse(sentLex.hasExpression(exp, pos1))
		
		sentLex.addExpression(exp, pos1)
		assertFalse(sentLex.hasExpression(exp, pos2))
	}
	
	@Test
	def testRemoveExpressionTrue {
		val sentExp1 = sentLex.addExpression(exp, pos1)
		val sentExp2 = sentLex.addExpression(exp, pos2)
		val actualSentExp2 = sentLex.removeExpression(exp, pos2)
		
		assertNotNull(actualSentExp2)
		assertEquals(sentExp2, actualSentExp2)
		assertTrue(sentLex.getExpressions.toSeq contains sentExp1)
		assertFalse(sentLex.getExpressions.toSeq contains sentExp2)
	}
	
	@Test
	def testRemoveExpressionFalse {
		assertNull(sentLex.removeExpression(exp, pos1))
		
		sentLex.addExpression(exp, pos1)
		assertNull(sentLex.removeExpression(exp, pos2))
	}
	
	@Test
	def testUpdateExpression {
		val exp2 = "bad"
		val sentExp1 = sentLex.addExpression(exp, pos1)
		val sentExp2 = sentLex.addExpression(exp, pos2)
		val actualSentExp2 = sentLex.updateExpression(exp, pos2, exp2)
		
		assertNotNull(actualSentExp2)
		assertEquals(sentExp2, actualSentExp2)
		assertEquals(exp2, actualSentExp2.getContent)
		assertTrue(sentLex.hasExpression(exp, pos1))
		assertFalse(sentLex.hasExpression(exp, pos2))
		assertTrue(sentLex.hasExpression(exp2, pos2))
	}
}