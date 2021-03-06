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

package edu.sabanciuniv.sentilab.sare.controllers.opinion.tests;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

import java.util.UUID;

import org.junit.*;

import edu.sabanciuniv.sentilab.sare.controllers.opinion.OpinionDocumentFactory;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;
import edu.sabanciuniv.sentilab.sare.tests.PersistenceTestsBase;

public class OpinionDocumentControllerTest
		extends PersistenceTestsBase {

	private OpinionCorpus testCorpus;
	private OpinionDocument testDocument;
	private OpinionDocumentFactory testFactory;
	
	@Before
	public void setUp() throws Exception {
		testCorpus = new OpinionCorpus();
		
		testDocument = new OpinionDocument();
		testCorpus.addDocument(testDocument);
	}

	@Test
	public void testFactoryCreatedDataIsPersisted() {
		testFactory = new OpinionDocumentFactory()
			.setPolarity(0.8)
			.setContent("some content")
			.setCorpus(testCorpus);
	
		testDocument = testFactory.create();
		
		em.getTransaction().begin();
		em.persist(testCorpus);
		em.getTransaction().commit();
		em.clear();
		
		OpinionDocument actualDocument = em.find(OpinionDocument.class, testDocument.getId());
		assertNotNull(actualDocument);
		assertEquals(testDocument.getPolarity(), actualDocument.getPolarity(), 0);
		assertEquals(testDocument.getContent(), actualDocument.getContent());
	}
	
	@Test
	public void testCreateWithExistingIdGetsExistingObject() {
		em.getTransaction().begin();
		em.persist(testCorpus);
		em.getTransaction().commit();
		em.clear();
		
		testFactory = (OpinionDocumentFactory)new OpinionDocumentFactory()
			.setCorpus(testCorpus)
			.setExistingId(testDocument.getIdentifier())
			.setEm(em);
		OpinionDocument actualDocument = testFactory.create();
		assertNotNull(actualDocument);
		assertEquals(testDocument.getIdentifier(), actualDocument.getIdentifier());
	}
	
	@Test
	public void testCreateWithNonExistingIdCreatesNewObject() {
		UUID id = UUID.randomUUID();
		testFactory = (OpinionDocumentFactory)new OpinionDocumentFactory()
			.setContent("some content")
			.setExistingId(id)
			.setEm(em);
		
		OpinionDocument actualDocument = testFactory.create();
		assertNotNull(actualDocument);
		assertThat(id, not(actualDocument.getIdentifier()));
	}
	
	@Test
	public void testCreateWithExistingIdUpdatesObject() {
		em.getTransaction().begin();
		em.persist(testCorpus);
		em.getTransaction().commit();
		em.clear();

		testFactory = (OpinionDocumentFactory)new OpinionDocumentFactory()
			.setContent("some content")
			.setExistingId(testDocument.getId())
			.setEm(em);
		
		OpinionDocument actualDocument = testFactory.create();
		assertNotNull(actualDocument);
		assertEquals(testDocument.getIdentifier(), actualDocument.getIdentifier());
		assertEquals(testFactory.getContent(), actualDocument.getContent());
	}
}