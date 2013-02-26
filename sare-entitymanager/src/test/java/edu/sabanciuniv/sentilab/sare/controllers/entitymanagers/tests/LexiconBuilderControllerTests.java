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

package edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.tests;

import static org.junit.Assert.*;

import java.util.List;
import java.util.Set;

import org.junit.*;

import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.LexiconBuilderController;
import edu.sabanciuniv.sentilab.sare.models.aspect.*;
import edu.sabanciuniv.sentilab.sare.models.base.document.*;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.LexiconBuilderDocumentStore;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;
import edu.sabanciuniv.sentilab.sare.tests.PersistenceTestsBase;
import edu.sabanciuniv.sentilab.utils.text.nlp.base.LinguisticToken;

public class LexiconBuilderControllerTests
	extends PersistenceTestsBase {

	OpinionCorpus testCorpus;
	OpinionDocument testDocument1;
	OpinionDocument testDocument2;
	AspectLexicon testLexicon;
	LexiconBuilderDocumentStore testBuilder;
	LexiconBuilderController testController;
	LexiconBuilderDocument testLBDocument1;
	LexiconBuilderDocument testLBDocument2;
	LexiconDocument testToken1;
	LexiconDocument testToken2;
	
	@Before
	public void setUp() throws Exception {
		testCorpus = new OpinionCorpus();
		testDocument1 = (OpinionDocument)new OpinionDocument()
			.setContent("this is the first test document. it's ok.")
			.setStore(testCorpus);
		testDocument2 = (OpinionDocument)new OpinionDocument()
			.setContent("this is the second test document. it's better than the other.")
			.setStore(testCorpus);
		
		testLexicon = new AspectLexicon();
		
		em.getTransaction().begin();
		persist(testCorpus);
		persist(testLexicon);
		em.getTransaction().commit();
		
		testBuilder = new AspectLexiconBuilderDocumentStore(testCorpus, testLexicon);
		Iterable<PersistentDocument> docs = testBuilder.getDocuments();
		testLBDocument1 = (LexiconBuilderDocument)Iterables.get(docs, 0);
		testLBDocument2 = (LexiconBuilderDocument)Iterables.get(docs, 1);
		testLBDocument1.setSeen(true);
		testLBDocument2.setSeen(false);
		
		testToken1 = (LexiconDocument)new LexiconDocument()
			.setContent("token1")
			.setStore(testBuilder);
		testToken2 = (LexiconDocument)new LexiconDocument()
			.setContent("token2")
			.setStore(testBuilder);
		
		testController = new LexiconBuilderController();
	}

	@Test
	public void testFindBuilderFindsWithValidBuilder() {
		em.getTransaction().begin();
		persist(testBuilder);
		em.getTransaction().commit();
		em.clear();
		
		LexiconBuilderDocumentStore actualBuilder = testController.findBuilder(em, testCorpus, testLexicon);
		assertNotNull(actualBuilder);
		assertEquals(testBuilder, actualBuilder);
	}
	
	@Test
	public void testFindBuilderWithNoBuilder() {
		LexiconBuilderDocumentStore actualBuilder = testController.findBuilder(em, testCorpus, testLexicon);
		assertNull(actualBuilder);
	}
	
	@Test
	public void testGetDocuments() {
		em.getTransaction().begin();
		persist(testBuilder);
		em.getTransaction().commit();
		em.clear();
		
		List<LexiconBuilderDocument> actualDocuments = testController.getDocuments(em, testBuilder);
		assertNotNull(actualDocuments);
		assertEquals(2, actualDocuments.size());
		assertTrue(actualDocuments.contains(testLBDocument1));
		assertTrue(actualDocuments.contains(testLBDocument2));
		
		actualDocuments = testController.getDocuments(em, testBuilder, true);
		assertNotNull(actualDocuments);
		assertEquals(1, actualDocuments.size());
		assertTrue(actualDocuments.contains(testLBDocument1));
		assertFalse(actualDocuments.contains(testLBDocument2));
		
		actualDocuments = testController.getDocuments(em, testBuilder, false);
		assertNotNull(actualDocuments);
		assertEquals(1, actualDocuments.size());
		assertFalse(actualDocuments.contains(testLBDocument1));
		assertTrue(actualDocuments.contains(testLBDocument2));
	}
	
	@Test
	public void testGetDocumentsWithNone() {
		testLBDocument1.setStore(null);
		testLBDocument2.setStore(null);
		
		em.getTransaction().begin();
		persist(testBuilder);
		em.getTransaction().commit();
		em.clear();
		
		List<LexiconBuilderDocument> actualDocuments = testController.getDocuments(em, testBuilder);
		assertNotNull(actualDocuments);
		assertEquals(0, actualDocuments.size());
	}
	
	@Test
	public void testGetSeenTokens() {
		em.getTransaction().begin();
		persist(testBuilder);
		em.getTransaction().commit();
		em.clear();
		
		List<LexiconDocument> actualTokens = testController.getSeenTokens(em, testBuilder);
		assertNotNull(actualTokens);
		assertEquals(2, actualTokens.size());
	}
	
	@Test
	public void testGetSeenTokensWithNone() {
		testToken1.setStore(null);
		testToken2.setStore(null);
		
		em.getTransaction().begin();
		persist(testBuilder);
		em.getTransaction().commit();
		em.clear();
		
		List<LexiconDocument> actualTokens = testController.getSeenTokens(em, testBuilder);
		assertNotNull(actualTokens);
		assertEquals(0, actualTokens.size());
	}
	
	@Test
	public void testIsSeenToken() {
		testToken2.setStore(null);
		
		em.getTransaction().begin();
		persist(testBuilder);
		em.getTransaction().commit();
		em.clear();
		
		assertTrue(testController.isSeenToken(em, testBuilder, testToken1.getContent()));
		assertFalse(testController.isSeenToken(em, testBuilder, testToken2.getContent()));
	}
	
	@Test
	public void testGetDocument() {
		em.getTransaction().begin();
		persist(testBuilder);
		em.getTransaction().commit();
		em.clear();
		
		LexiconBuilderDocument actualDocument1 = testController.getDocument(em, testBuilder, 0);
		assertNotNull(actualDocument1);
		assertEquals(testLBDocument2, actualDocument1);
		
		LexiconBuilderDocument actualDocument2 = testController.getDocument(em, testBuilder, 1);
		assertNotNull(actualDocument2);
		assertEquals(testLBDocument1, actualDocument2);
	}
	
	@Test
	public void testGetNextDocument() {
		testLBDocument1.setSeen(false);
		
		em.getTransaction().begin();
		persist(testBuilder);
		em.getTransaction().commit();
		em.clear();

		LexiconBuilderDocument actualDocument = testController.getNextDocument(em, testBuilder);
		assertNotNull(actualDocument);
		assertEquals(testLBDocument2, actualDocument);
	}

	@Test
	public void testGetNextDocumentWithSeenDocument() {
		testLBDocument1.setSeen(false);
		testLBDocument2.setSeen(true);
		
		em.getTransaction().begin();
		persist(testBuilder);
		em.getTransaction().commit();
		em.clear();

		LexiconBuilderDocument actualDocument = testController.getNextDocument(em, testBuilder);
		assertNotNull(actualDocument);
		assertEquals(testLBDocument1, actualDocument);
	}

	@Test
	public void testSetSeenDocument() {
		testLBDocument1.setSeen(false);
		
		em.getTransaction().begin();
		persist(testBuilder);
		em.getTransaction().commit();
		em.clear();
		
		LexiconBuilderDocument actualDocument = testController.getNextDocument(em, testBuilder);
		Set<LinguisticToken> tokens = actualDocument.getTokenWeightMap().keySet();
		
		em.getTransaction().begin();
		testController.setSeenDocument(em, actualDocument);
		em.getTransaction().commit();
		em.clear();
		
		assertNotEquals(actualDocument, testController.getNextDocument(em, testBuilder));
		assertTrue(testController.getDocuments(em, testBuilder, true).contains(actualDocument));
		for (LinguisticToken token : tokens) {
			assertTrue(testController.isSeenToken(em, testBuilder, token.getWord()));
		}
	}
}