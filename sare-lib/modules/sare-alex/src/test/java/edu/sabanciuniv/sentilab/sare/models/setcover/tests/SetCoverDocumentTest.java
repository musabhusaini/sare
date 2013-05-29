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

package edu.sabanciuniv.sentilab.sare.models.setcover.tests;

import static org.junit.Assert.*;

import org.junit.*;

import edu.sabanciuniv.sentilab.sare.models.opinion.*;
import edu.sabanciuniv.sentilab.sare.models.setcover.SetCoverDocument;

public class SetCoverDocumentTest {

	OpinionCorpus testCorpus;
	OpinionDocument testOpinion;
	SetCoverDocument testSetCoverDocument;
	
	@Before
	public void setUp() throws Exception {
		testCorpus = new OpinionCorpus();
		testCorpus.setLanguage("en");
		
		testOpinion = (OpinionDocument)new OpinionDocument()
			.setContent("this is a test document.")
			.setStore(testCorpus);
	}

	@Test
	public void testConstructorCreatesEnrichedContent() {
		testSetCoverDocument = new SetCoverDocument(testOpinion);
		assertEquals(testOpinion.getParsedContent().toString(true), testSetCoverDocument.getContent(true));
	}
	
	@Test
	public void testSetBaseDocumentCreatesEnrichedContent() {
		testSetCoverDocument = new SetCoverDocument();
		assertNull(testSetCoverDocument.getContent(true));
		
		testSetCoverDocument.setBaseDocument(testOpinion);
		assertEquals(testOpinion.getParsedContent().toString(true), testSetCoverDocument.getContent(true));
	}
}