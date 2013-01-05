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

package edu.sabanciuniv.sentilab.sare.controllers.opinion.tests;

import static org.junit.Assert.*;

import java.util.UUID;

import org.junit.*;

import edu.sabanciuniv.sentilab.sare.controllers.opinion.OpinionDocumentFactory;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;
import edu.sabanciuniv.sentilab.sare.tests.PersistenceTestsBase;

public class OpinionDocumentControllerTest extends PersistenceTestsBase {

	private OpinionCorpus testCorpus;
	private OpinionDocument testDocument;
	private OpinionDocumentFactory testFactory;
	private OpinionDocumentFactoryOptions testOptions;
	
	@Before
	public void setUp() throws Exception {
		testCorpus = new OpinionCorpus();
		testFactory = new OpinionDocumentFactory();
		
		testDocument = new OpinionDocument();
		testCorpus.addDocument(testDocument);
	}

	@Test
	public void testFactoryCreatedDataIsPersisted() {
		testOptions = new OpinionDocumentFactoryOptions()
			.setPolarity(0.8)
			.setContent("some content")
			.setCorpus(testCorpus);
	
		testDocument = testFactory.create(testOptions);
		
		em.getTransaction().begin();
		persist(testCorpus);
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
		persist(testCorpus);
		em.getTransaction().commit();
		em.clear();
		
		testOptions = new OpinionDocumentFactoryOptions()
			.setExistingId(testDocument.getIdentifier())
			.setEm(em)
			.setCorpus(testCorpus);
		OpinionDocument actualDocument = testFactory.create(testOptions);
		assertNotNull(actualDocument);
		assertEquals(testDocument.getIdentifier(), actualDocument.getIdentifier());
	}
	
	@Test
	public void testCreateWithNonExistingIdCreatesNewObject() {
		UUID id = UUID.randomUUID();
		testOptions = new OpinionDocumentFactoryOptions()
			.setExistingId(id)
			.setEm(em)
			.setContent("some content");
		
		OpinionDocument actualDocument = testFactory.create(testOptions);
		assertNotNull(actualDocument);
		assertNotEquals(id, actualDocument.getIdentifier());
	}
	
	@Test
	public void testCreateWithExistingIdUpdatesObject() {
		em.getTransaction().begin();
		persist(testCorpus);
		em.getTransaction().commit();
		em.clear();

		testOptions = new OpinionDocumentFactoryOptions()
			.setExistingId(testDocument.getId())
			.setEm(em)
			.setContent("some content");
		
		OpinionDocument actualDocument = testFactory.create(testOptions);
		assertNotNull(actualDocument);
		assertEquals(testDocument.getIdentifier(), actualDocument.getIdentifier());
		assertEquals(testOptions.getContent(), actualDocument.getContent());
	}
}