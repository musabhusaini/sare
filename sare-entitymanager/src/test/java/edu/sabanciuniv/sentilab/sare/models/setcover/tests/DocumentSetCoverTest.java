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

import java.util.EnumSet;

import org.junit.*;

import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.sare.models.base.ModelTestsBase;
import edu.sabanciuniv.sentilab.sare.models.base.document.*;
import edu.sabanciuniv.sentilab.sare.models.base.document.TokenizingOptions.TagCaptureOptions;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;
import edu.sabanciuniv.sentilab.sare.models.setcover.*;

public class DocumentSetCoverTest extends ModelTestsBase {

	private OpinionCorpus testCorpus;
	private OpinionDocument testDocument;
	
	private DocumentSetCover testSetCover;
	private SetCoverDocument testSetCoverDocument;
	
	private TokenizingOptions testTokenizingOptions;

	@Before
	public void setUp() throws Exception {
		testTokenizingOptions = new TokenizingOptions()
			.setLemmatized(true)
			.setTags(EnumSet.of(TagCaptureOptions.STARTS_WITH, TagCaptureOptions.IGNORE_CASE), "nn");
			
		testCorpus = (OpinionCorpus)new OpinionCorpus()
			.setLanguage("en")
			.setTitle("test corpus")
			.setDescription("this is a test corpus");
		testDocument = (OpinionDocument)new OpinionDocument()
			.setContent("this is a test document")
			.setStore(testCorpus);
		testCorpus.addDocument(testDocument);
		
//		em.getTransaction().begin();
//		persist(testCorpus);
//		persist(testDocument);
//		em.getTransaction().commit();
		
		testSetCover = new DocumentSetCover(testCorpus);
		testSetCoverDocument = (SetCoverDocument)new SetCoverDocument(testDocument)
			.setTokenizingOptions(testTokenizingOptions)
			.setStore(testSetCover);
		testSetCover.addDocument(testSetCoverDocument);

	}

	@After
	public void tearDown() throws Exception {
		//
	}

	@Test
	public void testTitle() {
		assertEquals(testCorpus.getTitle(), testSetCover.getTitle());
		
		testSetCover.setTitle("test set cover");
		
		assertFalse(testCorpus.getTitle().equals(testSetCover.getTitle()));
	}

	@Test
	public void testLanguage() {
		assertEquals(testCorpus.getLanguage(), testSetCover.getLanguage());
		
		testSetCover.setLanguage("tr");
		
		assertFalse(testCorpus.getLanguage().equals(testSetCover.getLanguage()));
	}

	@Test
	public void testDescription() {
		assertEquals(testCorpus.getDescription(), testSetCover.getDescription());
		
		testSetCover.setDescription("this is a set cover");
		
		assertFalse(testCorpus.getDescription().equals(testSetCover.getDescription()));
	}

	@Test
	public void testReplaceDocuments() {
		SetCoverDocument anotherSetCoverDocument = new SetCoverDocument();
		
		boolean replaced = testSetCover.replaceDocuments(testSetCoverDocument, anotherSetCoverDocument);
		
		assertTrue(replaced);
		assertFalse(Iterables.contains(testSetCover.getDocuments(), testSetCoverDocument));
		assertTrue(Iterables.contains(testSetCover.getDocuments(), anotherSetCoverDocument));
	}
}