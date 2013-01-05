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

package edu.sabanciuniv.sentilab.sare.models.opinion.tests;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.*;

import edu.sabanciuniv.sentilab.sare.models.base.ModelTestsBase;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;

public class OpinionDocumentTest
	extends ModelTestsBase {
	
	private String testContent1;
	private String testContent2;
	private double testPolarity1;
	private double testPolarity2;
	
	private OpinionCorpus testCorpus;
	private OpinionDocument testDocument;
	
	@Before
	public void setUp() throws Exception {
		testContent1 = "this is a test";
		testContent2 = "this is another test";
		testPolarity1 = 0.8796;
		testPolarity2 = 0.6978;
		testCorpus = (OpinionCorpus)new OpinionCorpus()
			.setTitle("test corpus")
			.setLanguage("en");
		
		em.getTransaction().begin();
		persist(testCorpus);
		em.getTransaction().commit();
		
		testDocument = (OpinionDocument)new OpinionDocument().setStore(testCorpus);
	}

	@After
	public void tearDown() throws Exception {
		//
	}

	@Test
	public void testContent() {
		testDocument.setContent(testContent1);
		
		em.getTransaction().begin();
		persist(testDocument);
		em.getTransaction().commit();
		
		em.clear();
		OpinionDocument actualDocument = em.find(OpinionDocument.class, testDocument.getId());
		
		assertNotNull(actualDocument);
		assertEquals(testDocument.getContent(), actualDocument.getContent());
	}
	
	@Test
	public void testContentUpdate() {
		testDocument.setContent(testContent1);
		
		em.getTransaction().begin();
		persist(testDocument);
		em.getTransaction().commit();
		
		em.clear();
		OpinionDocument actualDocument = em.find(OpinionDocument.class, testDocument.getId());
		actualDocument.setContent(testContent2);
		
		em.getTransaction().begin();
		em.merge(actualDocument);
		em.getTransaction().commit();
		
		em.clear();
		actualDocument = em.find(OpinionDocument.class, testDocument.getId());
		
		assertNotNull(actualDocument);
		assertEquals(testContent2, actualDocument.getContent());
	}

	@Test
	public void testPolarityUpdate() {
		testDocument.setPolarity(testPolarity1);
		
		em.getTransaction().begin();
		persist(testDocument);
		em.getTransaction().commit();
		
		em.clear();
		OpinionDocument actualDocument = em.find(OpinionDocument.class, testDocument.getId());
		actualDocument.setPolarity(testPolarity2);
		
		em.getTransaction().begin();
		em.merge(actualDocument);
		em.getTransaction().commit();
		
		em.clear();
		actualDocument = em.find(OpinionDocument.class, testDocument.getId());
		
		assertNotNull(actualDocument);
		assertEquals(testPolarity2, actualDocument.getPolarity(), 0);
	}
	
	@Test
	public void testPolarity() {
		testDocument.setPolarity(testPolarity1);
		
		em.getTransaction().begin();
		persist(testDocument);
		em.getTransaction().commit();
		
		em.clear();
		OpinionDocument actualDocument = em.find(OpinionDocument.class, testDocument.getId());
		
		assertNotNull(actualDocument);
		assertEquals(testDocument.getPolarity(), actualDocument.getPolarity(), 0);
	}

	@Test
	public void testStore() {
		em.getTransaction().begin();
		persist(testDocument);
		em.getTransaction().commit();
		
		UUID testStoreGuid = testDocument.getStore().getIdentifier();
		
		em.clear();
		OpinionDocument actualDocument = em.find(OpinionDocument.class, testDocument.getId());
		
		assertNotNull(actualDocument);
		assertEquals(testStoreGuid, actualDocument.getStore().getIdentifier());
	}

	@Test
	public void testBaseDocument() {
		OpinionDocument testBaseDocument = (OpinionDocument)new OpinionDocument()
			.setStore(testCorpus);
		testCorpus.addDocument(testBaseDocument);
		testDocument.setBaseDocument(testBaseDocument);
		
		em.getTransaction().begin();
		persist(testBaseDocument);
		persist(testDocument);
		em.getTransaction().commit();
		
		em.clear();
		OpinionDocument actualDocument = em.find(OpinionDocument.class, testDocument.getId());
		
		assertNotNull(actualDocument);
		assertNotNull(actualDocument.getBaseDocument());
		
		assertNotNull(actualDocument.getBaseDocument());
		assertEquals(testBaseDocument.getIdentifier(), actualDocument.getBaseDocument().getIdentifier());		
	}
}