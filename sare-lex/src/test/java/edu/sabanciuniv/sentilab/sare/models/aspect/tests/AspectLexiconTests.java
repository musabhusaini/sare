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
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.DocumentCorpus;
import edu.sabanciuniv.sentilab.sare.models.opinion.OpinionCorpus;

public class AspectLexiconTests {

	private AspectLexicon testLexicon1;
	private AspectLexicon testLexicon2;
	private String testString1;
	private String testString2;
	private String testString3;
	
	@Before
	public void setUp() throws Exception {
		testLexicon1 = new AspectLexicon();
		testLexicon2 = new AspectLexicon();
		
		testString1 = "test-string-1";
		testString2 = "test-string-2";
		testString3 = "test-string-3";
	}
	
	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFindExpression() {
		testLexicon1.addExpression(testString1);
		testLexicon1.addExpression(testString2);
		
		AspectExpression actualExpression = testLexicon1.findExpression(testString1);
		assertNotNull(actualExpression);
		assertEquals(testString1, actualExpression.getContent());
		
		actualExpression = testLexicon1.findExpression(testString3);
		assertNull(actualExpression);
	}
	
	@Test
	public void testFindExpressionNonRecursive() {
		AspectLexicon subAspect = testLexicon1.addAspect(testString1);
		subAspect.addExpression(testString2);
		
		AspectExpression actualExpression = testLexicon1.findExpression(testString2, false);
		assertNull(actualExpression);
	}

	@Test
	public void testFindExpressionRecursive() {
		AspectLexicon subAspect = testLexicon1.addAspect(testString1);
		subAspect.addExpression(testString2);
		
		AspectExpression actualExpression = testLexicon1.findExpression(testString2, true);
		assertNotNull(actualExpression);
		assertEquals(testString2, actualExpression.getContent());
		
		actualExpression = testLexicon1.findExpression(testString3, true);
		assertNull(actualExpression);
	}
	
	@Test
	public void testHasExpression() {
		testLexicon1.addExpression(testString1);
		
		assertTrue(testLexicon1.hasExpression(testString1));
		assertFalse(testLexicon1.hasExpression(testString2));
	}

	@Test
	public void testAddExpression() {
		testLexicon1.addExpression(testString1);
		Iterable<AspectExpression> expressions = testLexicon1.getExpressions();
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
		testLexicon1.addExpression(testString1);
		testLexicon1.removeExpression(testString1);
		Iterable<AspectExpression> expressions = testLexicon1.getExpressions();
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
		testLexicon1.addAspect(testString1);
		testLexicon1.addAspect(testString2);
		
		AspectLexicon actualSubAspect = testLexicon1.findAspect(testString1);
		assertNotNull(actualSubAspect);
		assertEquals(testString1, actualSubAspect.getTitle());
		
		actualSubAspect = testLexicon1.findAspect(testString3);
		assertNull(actualSubAspect);
	}
	
	@Test
	public void testFindAspectNonRecursive() {
		AspectLexicon subAspect = testLexicon1.addAspect(testString1);
		subAspect.addAspect(testString2);
		
		AspectLexicon actualSubAspect = testLexicon1.findAspect(testString2, false);
		assertNull(actualSubAspect);
	}
	
	@Test
	public void testFindAspectRecursive() {
		AspectLexicon subAspect = testLexicon1.addAspect(testString1);
		subAspect.addAspect(testString2);
		
		AspectLexicon foundSubAspect = testLexicon1.findAspect(testString2, true);
		assertNotNull(foundSubAspect);
		assertEquals(testString2, foundSubAspect.getTitle());
		
		foundSubAspect = testLexicon1.findAspect(testString3, true);
		assertNull(foundSubAspect);
	}

	@Test
	public void testHasAspect() {
		testLexicon1.addAspect(testString1);
		
		assertTrue(testLexicon1.hasAspect(testString1));
		assertFalse(testLexicon1.hasAspect(testString2));
	}

	@Test
	public void testAddAspect() {
		testLexicon1.addAspect(testString1);
		Iterable<AspectLexicon> subAspects = testLexicon1.getAspects();
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
		AspectLexicon testAspect = testLexicon1.addAspect(testString1);
		AspectLexicon actualAspect = testLexicon1.removeAspect(testString1);
		assertEquals(testAspect, actualAspect);
		
		Iterable<AspectLexicon> subAspects = testLexicon1.getAspects();
		AspectLexicon subAspect = Iterables.find(subAspects, new Predicate<AspectLexicon>() {
			@Override
			public boolean apply(AspectLexicon input) {
				return testString1.equalsIgnoreCase(input.getTitle());
			}
		}, null);
		
		assertNull(subAspect);
	}
	
	@Test
	public void testGetBaseCorpus() {
		DocumentCorpus testCorpus = new OpinionCorpus();
		testLexicon1.setBaseStore(testCorpus);
		AspectLexicon testAspect = testLexicon1.addAspect(testString1);
		
		assertEquals(testCorpus, testLexicon1.getBaseCorpus());
		assertEquals(testCorpus, testAspect.getBaseCorpus());
	}
	
	@Test
	public void testMigrateAspectMigratesNonDupe() {
		AspectLexicon testAspect = testLexicon1.addAspect(testString1);
		assertTrue(testLexicon2.migrateAspect(testAspect));
		assertEquals(testAspect.getBaseStore(), testLexicon2);
	}
	
	@Test
	public void testMigrateAspectDoesNotMigrateDupe() {
		AspectLexicon testAspect = testLexicon1.addAspect(testString1);
		testLexicon2.addAspect(testString1);
		assertFalse(testLexicon2.migrateAspect(testAspect));
		assertNotEquals(testAspect.getBaseStore(), testLexicon2);
	}
	
	@Test
	public void testUpdateAspectUpdatesNonDupe() {
		AspectLexicon testAspect = testLexicon1.addAspect(testString1);
		AspectLexicon actualAspect = testLexicon1.updateAspect(testAspect.getTitle(), testString2);
		assertEquals(testAspect, actualAspect);
		assertEquals(testString2, actualAspect.getTitle());
	}
	
	@Test
	public void testUpdateAspectDoesNotUpdateDupe() {
		AspectLexicon testAspect = testLexicon1.addAspect(testString1);
		testLexicon1.addAspect(testString2);
		AspectLexicon actualAspect = testLexicon1.updateAspect(testAspect.getTitle(), testString2);
		assertNull(actualAspect);
		assertEquals(testString1, testAspect.getTitle());
		assertEquals(testLexicon1, testAspect.getBaseStore());
	}
}