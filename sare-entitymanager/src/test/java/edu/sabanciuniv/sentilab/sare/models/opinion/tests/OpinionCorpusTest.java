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

package edu.sabanciuniv.sentilab.sare.models.opinion.tests;

import static org.junit.Assert.*;

import org.junit.*;

import com.google.common.collect.*;

import edu.sabanciuniv.sentilab.sare.models.opinion.*;
import edu.sabanciuniv.sentilab.sare.tests.PersistenceTestsBase;

public class OpinionCorpusTest
	extends PersistenceTestsBase {

	private OpinionCorpus testCorpus;
	private OpinionCorpus testBaseCorpus;
	private OpinionDocument testDocument;
	
	@Before
	public void setUp() throws Exception {
		testCorpus = new OpinionCorpus();
		testDocument = (OpinionDocument)new OpinionDocument()
			.setStore(testCorpus);
	}

	@After
	public void tearDown() throws Exception {
		//
	}

	@Test
	public void testDocuments() {
		testCorpus.setDocuments(Lists.newArrayList(testDocument));
		
		em.getTransaction().begin();
		persist(testCorpus);
		persist(testDocument);
		em.getTransaction().commit();
		
		em.clear();
		OpinionCorpus actualCorpus = em.find(OpinionCorpus.class, testCorpus.getId());
		
		assertNotNull(actualCorpus);
		assertEquals(Iterables.size(testCorpus.getDocuments()), Iterables.size(actualCorpus.getDocuments()));
		
		OpinionDocument actualDocument = Iterables.getFirst(actualCorpus.getDocuments(OpinionDocument.class),
			null);
		assertNotNull(actualDocument);
		assertEquals(testDocument.getIdentifier(), actualDocument.getIdentifier());
	}

	@Test
	public void testAddDocument() {
		testCorpus.addDocument(testDocument);
		assertTrue(Iterables.contains(testCorpus.getDocuments(), testDocument));
	}

	@Test
	public void testRemoveDocument() {
		testCorpus.addDocument(testDocument);
		testCorpus.removeDocument(testDocument);
		assertFalse(Iterables.contains(testCorpus.getDocuments(), testDocument));
	}

	@Test
	public void testBaseStore() {
		testBaseCorpus = new OpinionCorpus();
		testCorpus.setBaseStore(testBaseCorpus);
		
		assertTrue(testBaseCorpus.hasDerivedStore(testCorpus));
		
		em.getTransaction().begin();
		persist(testBaseCorpus);
		persist(testCorpus);
		em.getTransaction().commit();
		
		em.clear();
		OpinionCorpus actualCorpus = em.find(OpinionCorpus.class, testCorpus.getId());
		
		assertNotNull(actualCorpus);
		assertNotNull(actualCorpus.getBaseStore());
		assertEquals(testCorpus.getBaseStore().getIdentifier(), actualCorpus.getBaseStore().getIdentifier());
	}

	@Test
	public void testTitle() {
		testCorpus.setTitle("test corpus");
		
		em.getTransaction().begin();
		persist(testCorpus);
		em.getTransaction().commit();
		
		em.clear();
		OpinionCorpus actualCorpus = em.find(OpinionCorpus.class, testCorpus.getId());
		
		assertNotNull(actualCorpus);
		assertEquals(testCorpus.getTitle(), actualCorpus.getTitle());
	}

	@Test
	public void testLanguage() {
		testCorpus.setLanguage("en");
		
		em.getTransaction().begin();
		persist(testCorpus);
		em.getTransaction().commit();
		
		em.clear();
		OpinionCorpus actualCorpus = em.find(OpinionCorpus.class, testCorpus.getId());
		
		assertNotNull(actualCorpus);
		assertEquals(testCorpus.getLanguage(), actualCorpus.getLanguage());
	}
	
	@Test
	public void testLanguageUpdate() {
		testCorpus.setLanguage("en");
		
		em.getTransaction().begin();
		persist(testCorpus);
		em.getTransaction().commit();
		
		testCorpus.setLanguage("tr");
		em.getTransaction().begin();
		em.merge(testCorpus);
		em.getTransaction().commit();
		
		em.clear();
		OpinionCorpus actualCorpus = em.find(OpinionCorpus.class, testCorpus.getId());
		
		assertNotNull(actualCorpus);
		assertEquals(testCorpus.getLanguage(), actualCorpus.getLanguage());
	}

	@Test
	public void testDescription() {
		testCorpus.setDescription("this is a test corpus");
		
		em.getTransaction().begin();
		persist(testCorpus);
		em.getTransaction().commit();
		
		testCorpus.setDescription("this is also test corpus");
		em.getTransaction().begin();
		em.merge(testCorpus);
		em.getTransaction().commit();
		
		em.clear();
		OpinionCorpus actualCorpus = em.find(OpinionCorpus.class, testCorpus.getId());
		
		assertNotNull(actualCorpus);
		assertEquals(testCorpus.getDescription(), actualCorpus.getDescription());
	}
	
	@Test
	public void testDescriptionUpdate() {
		testCorpus.setDescription("this is a test corpus");
		
		em.getTransaction().begin();
		persist(testCorpus);
		em.getTransaction().commit();
		
		em.clear();
		OpinionCorpus actualCorpus = em.find(OpinionCorpus.class, testCorpus.getId());
		
		assertNotNull(actualCorpus);
		assertEquals(testCorpus.getDescription(), actualCorpus.getDescription());
	}
}