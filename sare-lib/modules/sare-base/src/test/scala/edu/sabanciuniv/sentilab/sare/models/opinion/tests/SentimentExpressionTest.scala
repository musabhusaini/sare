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

import org.junit._
import org.junit.Assert._
import edu.sabanciuniv.sentilab.sare.models.opinion.SentimentExpression

class SentimentExpressionTest {
	
	private val exp = "good"
	private var sentExp: SentimentExpression = _
  
	@Before
	def setup {
		sentExp = new SentimentExpression(exp)
	}
	
	@Test
	def testContent {
		assertEquals(exp, sentExp.getContent)
	}
	
	@Test
	def testPos {
		val pos = "adjective"
		sentExp.setPos(pos)
		assertEquals(pos, sentExp.getPos)
	}
	
	@Test
	def testNegative {
		val neg = 0.25
		sentExp.setNegative(neg)
		assertEquals(neg, sentExp.getNegative, 0)
	}
	
	@Test
	def testNeutral {
		val neu = 0.25
		sentExp.setNeutral(neu)
		assertEquals(neu, sentExp.getNeutral, 0)
	}
	
	@Test
	def testPositive {
		val pos = 0.50
		sentExp.setPositive(pos)
		assertEquals(pos, sentExp.getPositive, 0)
	}
}