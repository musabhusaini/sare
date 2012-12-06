package edu.sabanciuniv.sentilab.sare.models.aspect.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.base.Predicate;
import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.sare.models.aspect.AspectExpression;
import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon;

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