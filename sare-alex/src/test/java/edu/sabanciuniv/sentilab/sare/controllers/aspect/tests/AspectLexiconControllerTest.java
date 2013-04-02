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

package edu.sabanciuniv.sentilab.sare.controllers.aspect.tests;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.core.models.factory.IllegalFactoryOptionsException;
import edu.sabanciuniv.sentilab.sare.controllers.aspect.AspectLexiconController;
import edu.sabanciuniv.sentilab.sare.models.aspect.*;
import edu.sabanciuniv.sentilab.sare.models.opinion.OpinionCorpus;

public class AspectLexiconControllerTest {

	private OpinionCorpus testCorpus = new OpinionCorpus();
	private AspectLexiconController testController = new AspectLexiconController();
	private AspectLexiconFactoryOptions testOptions = new AspectLexiconFactoryOptions();
	
	private String testXmlLexiconFilename;
	private AspectLexicon expectedXmlLexicon;
	
	@Before
	public void setUp() throws Exception {
		testOptions = new AspectLexiconFactoryOptions();
		testController = new AspectLexiconController();
	}
	
	private boolean areLexicaEqual(AspectLexicon expectedLexicon, AspectLexicon actualLexicon) {
		if (!expectedLexicon.getTitle().equals(actualLexicon.getTitle())) {
			return false;
		}
		if (expectedLexicon.getDescription().equals(actualLexicon.getDescription())) {
			return false;
		}
		if (Iterables.size(expectedLexicon.getExpressions()) != Iterables.size(actualLexicon.getExpressions())) {
			return false;
		}
		if (Iterables.size(expectedLexicon.getAspects()) != Iterables.size(actualLexicon.getAspects())) {
			return false;
		}
		
		for (AspectExpression actualExpression : actualLexicon.getExpressions()) {
			AspectExpression expectedExpression = expectedLexicon.findExpression(actualExpression.getContent());
			if (expectedExpression == null) {
				return false;
			}
		}
		
		for (AspectLexicon actualAspect : actualLexicon.getAspects()) {
			AspectLexicon expectedAspect = expectedLexicon.findAspect(actualAspect.getTitle());
			if (expectedAspect == null) {
				return false;
			}
			
			if (!areLexicaEqual(expectedAspect, actualAspect)) {
				return false;
			}
		}
		
		return true;
	}
	
	@Test
	public void testCreateFromXmlFile() {
		testXmlLexiconFilename = "/test-lexicon.xml";
		
		expectedXmlLexicon = (AspectLexicon)new AspectLexicon()
			.setTitle("test lexicon")
			.setDescription("a test lexicon");
		
		AspectLexicon aspect1 = expectedXmlLexicon.addAspect("aspect 1");
		aspect1.addExpression("expression 1.1");
		aspect1.addExpression("expression 1.2");
		AspectLexicon subAspect11 = aspect1.addAspect("sub-aspect 1.1");
		subAspect11.addExpression("expression 1.1.1");
		AspectLexicon subAspect12 = aspect1.addAspect("sub-aspect 1.2");
		subAspect12.addExpression("expression 1.2.1");
		
		AspectLexicon aspect2 = expectedXmlLexicon.addAspect("aspect 2");
		aspect2.addExpression("expression 2.1");
		aspect2.addExpression("expression 2.2");
		
		testOptions.setFile(new File(getClass().getResource(testXmlLexiconFilename).getPath()));
		
		AspectLexicon actualLexicon = null;
		try {
			actualLexicon = testController.create(testOptions);
		} catch (IllegalFactoryOptionsException e) {
			fail("could not open file");
		}
		
		assertNotNull(actualLexicon);
		assertTrue(areLexicaEqual(expectedXmlLexicon, actualLexicon));
	}
	
	@Test
	public void testCreateSetsBaseCorpus() {
		testOptions.setBaseStore(testCorpus);
		AspectLexicon actualLexicon = testController.create(testOptions);
		
		assertNotNull(actualLexicon);
		assertEquals(testCorpus, actualLexicon.getBaseStore());
	}
	
	@Test
	public void testCreateSetsTitle() {
		testOptions.setTitle("test");
		AspectLexicon actualLexicon = testController.create(testOptions);
		
		assertNotNull(actualLexicon);
		assertEquals(testOptions.getTitle(), actualLexicon.getTitle());
	}
	
	@Test
	public void testCreateSetsDescription() {
		testOptions.setDescription("test lexicon");
		AspectLexicon actualLexicon = testController.create(testOptions);
		
		assertNotNull(actualLexicon);
		assertEquals(testOptions.getDescription(), actualLexicon.getDescription());
	}
	
	@Test
	public void testCreateSetsLanguage() {
		testOptions.setLanguage("tr");
		AspectLexicon actualLexicon = testController.create(testOptions);
		
		assertNotNull(actualLexicon);
		assertEquals(testOptions.getLanguage(), actualLexicon.getLanguage());
	}
}