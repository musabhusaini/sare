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

import java.io.File;
import java.util.UUID;

import org.junit.*;

import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.sare.controllers.opinion.OpinionCorpusFactory;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;
import edu.sabanciuniv.sentilab.sare.tests.PersistenceTestsBase;

public class OpinionCorpusControllerTest
		extends PersistenceTestsBase {

	private String testXmlCorpusFilename;
	private OpinionCorpus testCorpus;
	private OpinionCorpusFactory testFactory;
	
	@Before
	public void setUp() throws Exception {
		testXmlCorpusFilename = "/test-corpus.xml";
		testCorpus = new OpinionCorpus();
		testFactory = (OpinionCorpusFactory)new OpinionCorpusFactory()
			.setExistingId(testCorpus.getId())
			.setEm(em);
	}
	
	@Test
	public void testCreateWithExistingIdGetsExistingObject() {
		em.getTransaction().begin();
		persist(testCorpus);
		em.getTransaction().commit();
		em.clear();
		
		OpinionCorpus actualCorpus = testFactory.create();
		assertNotNull(actualCorpus);
		assertEquals(testCorpus.getIdentifier(), actualCorpus.getIdentifier());
	}
	
	@Test
	public void testCreateWithNonExistingIdCreatesNewObject() {
		UUID id = UUID.randomUUID();
		testFactory
			.setContent("some content")
			.setFormat("txt")
			.setExistingId(id)
			.setEm(em);
		OpinionCorpus actualCorpus = testFactory.create();
		assertNotNull(actualCorpus);
		assertNotEquals(id, actualCorpus.getIdentifier());
	}
	
	@Test
	public void testCreateWithExistingIdUpdatesObject() {
		em.getTransaction().begin();
		persist(testCorpus);
		em.getTransaction().commit();
		em.clear();
		
		testFactory.setFile(new File(getClass().getResource(testXmlCorpusFilename).getPath()));
		OpinionCorpus actualCorpus = testFactory.create();
		assertNotNull(actualCorpus);
		assertEquals(testCorpus.getIdentifier(), actualCorpus.getIdentifier());
		assertEquals(4, Iterables.size(actualCorpus.getDocuments()));
		
		OpinionDocument document = Iterables.getFirst(actualCorpus.getDocuments(OpinionDocument.class), null);
		assertNotNull(document);
		assertNotNull(document.getContent());
		assertNotNull(document.getPolarity());
		
		em.getTransaction().begin();
		for (OpinionDocument doc : actualCorpus.getDocuments(OpinionDocument.class)) {
			persist(doc);
		}
		em.merge(actualCorpus);
		em.getTransaction().commit();
		em.clear();
		
		actualCorpus = em.find(OpinionCorpus.class, testCorpus.getId());
		assertNotNull(actualCorpus);
		assertEquals(testCorpus.getIdentifier(), actualCorpus.getIdentifier());
		assertEquals(4, Iterables.size(actualCorpus.getDocuments()));

		document = Iterables.getFirst(actualCorpus.getDocuments(OpinionDocument.class), null);
		assertNotNull(document);
		assertNotNull(document.getContent());
		assertNotNull(document.getPolarity());
	}
}