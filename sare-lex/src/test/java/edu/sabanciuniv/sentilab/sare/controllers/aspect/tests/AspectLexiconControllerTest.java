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

package edu.sabanciuniv.sentilab.sare.controllers.aspect.tests;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import edu.sabanciuniv.sentilab.sare.controllers.aspect.AspectLexiconController;
import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexicon;
import edu.sabanciuniv.sentilab.sare.models.aspect.AspectLexiconFactoryOptions;
import edu.sabanciuniv.sentilab.sare.models.opinion.OpinionCorpus;

public class AspectLexiconControllerTest {

	private OpinionCorpus testCorpus = new OpinionCorpus();
	private AspectLexiconController testController = new AspectLexiconController();
	private AspectLexiconFactoryOptions testOptions = new AspectLexiconFactoryOptions();
	
	@Before
	public void setUp() throws Exception {
		testOptions = new AspectLexiconFactoryOptions();
		testController = new AspectLexiconController();
	}

	@Test
	public void testCreateSetsBaseCorpus() {
		testOptions.setBaseCorpus(testCorpus);
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