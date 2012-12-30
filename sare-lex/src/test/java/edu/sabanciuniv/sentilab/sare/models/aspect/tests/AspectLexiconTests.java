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
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with SARE. If not, see <http://www.gnu.org/licenses/>.
 */

package edu.sabanciuniv.sentilab.sare.models.aspect.tests;

import static org.junit.Assert.*;

import org.junit.*;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.sare.models.aspect.*;

public class AspectLexiconTests {

	private AspectLexicon testLexicon;
	private String testString1;
	private String testString2;
	private String testString3;
	
	@Before
	public void setUp() throws Exception {
		testLexicon = new AspectLexicon();
		
		testString1 = "test-string-1";
		testString2 = "test-string-2";
		testString3 = "test-string-3";
	}
	
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFindExpression() {
		testLexicon.addExpression(testString1);
		testLexicon.addExpression(testString2);
		
		AspectExpression actualExpression = testLexicon.findExpression(testString1);
		assertNotNull(actualExpression);
		assertEquals(testString1, actualExpression.getContent());
		
		actualExpression = testLexicon.findExpression(testString3);
		assertNull(actualExpression);
	}
	
	@Test
	public void testFindExpressionNonRecursive() {
		AspectLexicon subAspect = testLexicon.addAspect(testString1);
		subAspect.addExpression(testString2);
		
		AspectExpression actualExpression = testLexicon.findExpression(testString2, false);
		assertNull(actualExpression);
	}

	@Test
	public void testFindExpressionRecursive() {
		AspectLexicon subAspect = testLexicon.addAspect(testString1);
		subAspect.addExpression(testString2);
		
		AspectExpression actualExpression = testLexicon.findExpression(testString2, true);
		assertNotNull(actualExpression);
		assertEquals(testString2, actualExpression.getContent());
		
		actualExpression = testLexicon.findExpression(testString3, true);
		assertNull(actualExpression);
	}
	
	@Test
	public void testHasExpression() {
		testLexicon.addExpression(testString1);
		
		assertTrue(testLexicon.hasExpression(testString1));
		assertFalse(testLexicon.hasExpression(testString2));
	}

	@Test
	public void testAddExpression() {
		testLexicon.addExpression(testString1);
		Iterable<AspectExpression> expressions = testLexicon.getExpressions();
		AspectExpression expression = Iterables.find(expressions, new Predicate<AspectExpression>() {
			@Override
			public boolean apply(AspectExpression input) {
				return testString1.equalsIgnoreCase(input.getContent());
			}
		}, null);
		
		assertNotNull(expression);
	}

	@Test
	public void testRemoveExpression() {
		testLexicon.addExpression(testString1);
		testLexicon.removeExpression(testString1);
		Iterable<AspectExpression> expressions = testLexicon.getExpressions();
		AspectExpression expression = Iterables.find(expressions, new Predicate<AspectExpression>() {
			@Override
			public boolean apply(AspectExpression input) {
				return testString1.equalsIgnoreCase(input.getContent());
			}
		}, null);
		
		assertNull(expression);
	}

	@Test
	public void testFindAspect() {
		testLexicon.addAspect(testString1);
		testLexicon.addAspect(testString2);
		
		AspectLexicon actualSubAspect = testLexicon.findAspect(testString1);
		assertNotNull(actualSubAspect);
		assertEquals(testString1, actualSubAspect.getTitle());
		
		actualSubAspect = testLexicon.findAspect(testString3);
		assertNull(actualSubAspect);
	}
	
	@Test
	public void testFindAspectNonRecursive() {
		AspectLexicon subAspect = testLexicon.addAspect(testString1);
		subAspect.addAspect(testString2);
		
		AspectLexicon actualSubAspect = testLexicon.findAspect(testString2, false);
		assertNull(actualSubAspect);
	}
	
	@Test
	public void testFindAspectRecursive() {
		AspectLexicon subAspect = testLexicon.addAspect(testString1);
		subAspect.addAspect(testString2);
		
		AspectLexicon foundSubAspect = testLexicon.findAspect(testString2, true);
		assertNotNull(foundSubAspect);
		assertEquals(testString2, foundSubAspect.getTitle());
		
		foundSubAspect = testLexicon.findAspect(testString3, true);
		assertNull(foundSubAspect);
	}

	@Test
	public void testHasAspect() {
		testLexicon.addAspect(testString1);
		
		assertTrue(testLexicon.hasAspect(testString1));
		assertFalse(testLexicon.hasAspect(testString2));
	}

	@Test
	public void testAddAspect() {
		testLexicon.addAspect(testString1);
		Iterable<AspectLexicon> subAspects = testLexicon.getAspects();
		AspectLexicon subAspect = Iterables.find(subAspects, new Predicate<AspectLexicon>() {
			@Override
			public boolean apply(AspectLexicon input) {
				return testString1.equalsIgnoreCase(input.getTitle());
			}
		}, null);
		
		assertNotNull(subAspect);
	}

	@Test
	public void testRemoveAspect() {
		testLexicon.addAspect(testString1);
		testLexicon.removeAspect(testString1);
		Iterable<AspectLexicon> subAspects = testLexicon.getAspects();
		AspectLexicon subAspect = Iterables.find(subAspects, new Predicate<AspectLexicon>() {
			@Override
			public boolean apply(AspectLexicon input) {
				return testString1.equalsIgnoreCase(input.getTitle());
			}
		}, null);
		
		assertNull(subAspect);
	}
}