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

import static com.google.common.collect.Iterables.*;
import static com.google.common.collect.Lists.*;

import static edu.sabanciuniv.sentilab.utils.UuidUtils.*;

import java.util.*;

import org.junit.*;

import edu.sabanciuniv.sentilab.sare.controllers.entitymanagers.PersistentDocumentStoreController;
import edu.sabanciuniv.sentilab.sare.models.base.*;
import edu.sabanciuniv.sentilab.sare.models.opinion.*;

public class PersistentDocumentStoreControllerTest extends ModelTestsBase {

	private PersistentDocumentStoreController testController;
	private List<OpinionCorpus> testCorpora;
	private String testOwner;
	
	@Before
	public void setUp() throws Exception {
		testController = new PersistentDocumentStoreController();
		
		testOwner = normalize(UUID.randomUUID());
		
		testCorpora = newArrayList();
		testCorpora.add((OpinionCorpus)new OpinionCorpus()
			.addDocument(new OpinionDocument())
			.addDocument(new OpinionDocument())
			.setOwnerId(testOwner));
		
		testCorpora.add((OpinionCorpus)new OpinionCorpus()
			.setOwnerId(testOwner));
		
		em.getTransaction().begin();
		for (OpinionCorpus testCorpus : testCorpora) {
			persist(testCorpus);
		}
		em.getTransaction().commit();
		em.clear();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testGetAllUuids() {
		Iterable<String> expectedUuids = transform(testCorpora, toUuidStringFunction());
		List<String> actualUuids = testController.getAllUuids(em, testOwner);
		
		assertNotNull(actualUuids);
		assertEquals(testCorpora.size(), actualUuids.size());
		for (String uuid : actualUuids) {
			assertTrue(contains(expectedUuids, uuid));
		}
	}

	@Test
	public void testGetSize() {
		for (OpinionCorpus testCorpus : testCorpora) {
			long actualSize = testController.getSize(em, testCorpus.getIdentifier().toString());
			assertEquals(size(testCorpus.getDocuments()), actualSize);
		}
	}
}