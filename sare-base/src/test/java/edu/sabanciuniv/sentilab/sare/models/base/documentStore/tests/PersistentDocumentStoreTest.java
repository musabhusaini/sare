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

package edu.sabanciuniv.sentilab.sare.models.base.documentStore.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;

import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;

import edu.sabanciuniv.sentilab.sare.models.base.PersistentObject;
import edu.sabanciuniv.sentilab.sare.models.base.document.PersistentDocument;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;

public class PersistentDocumentStoreTest {

	private class PersistentDocumentEx
		extends PersistentDocument {
	
		/**
		 * 
		 */
		private static final long serialVersionUID = 6250829637687712614L;
	
		public List<PersistentObject> getReferences() {
			return this.referencedObjects;
		}
		
		@Override
		public String getContent() {
			return null;
		}
	}

	private class PersistentDocumentStoreEx extends PersistentDocumentStore {

		/**
		 * 
		 */
		private static final long serialVersionUID = 4975683421733963358L;

		public List<PersistentObject> getReferences() {
			return this.referencedObjects;
		}

		public List<PersistentObject> getReferers() {
			return this.refererObjects;
		}

		@Override
		public boolean hasDerivedStore(PersistentDocumentStore derivedStore) {
			return super.hasDerivedStore(derivedStore);
		}

		@Override
		public PersistentDocumentStore addDerivedStore(
				PersistentDocumentStore derivedStore) {
			return super.addDerivedStore(derivedStore);
		}

		@Override
		public boolean removeDerivedStore(PersistentDocumentStore derivedStore) {
			return super.removeDerivedStore(derivedStore);
		}
	}

	private PersistentDocumentEx testDocument1;
	private PersistentDocumentEx testDocument2;
	private PersistentDocumentEx testDocument3;
	private PersistentDocumentStoreEx testStore1;
	private PersistentDocumentStoreEx testStore2;
	private PersistentDocumentStoreEx testStore3;

	@Before
	public void setUp() throws Exception {
		testDocument1 = new PersistentDocumentEx();
		testDocument2 = new PersistentDocumentEx();
		testDocument3 = new PersistentDocumentEx();
		
		testStore1 = new PersistentDocumentStoreEx();
		testStore2 = new PersistentDocumentStoreEx();
		testStore3 = new PersistentDocumentStoreEx();
	}
	
	public void testEmptyStoreDoesNotReturnNullDocuments() {
		assertNotNull(testStore1.getDocuments());
		assertEquals(0, Iterables.size(testStore1.getDocuments()));
	}
	
	@Test
	public void testSetBaseStorePropagates() {
		testStore1.setBaseStore(testStore2);

		assertTrue(testStore1.getBaseStore() == testStore2);
		assertTrue(testStore1.getReferences().contains(testStore2));
		assertTrue(testStore2.hasDerivedStore(testStore1));
		assertTrue(testStore2.getReferers().contains(testStore1));
	}

	@Test
	public void testSetBaseStoreRemovesOldBase() {
		testStore1.setBaseStore(testStore2);
		testStore1.setBaseStore(testStore3);

		assertTrue(testStore1.getBaseStore() == testStore3);
		assertFalse(testStore1.getReferences().contains(testStore2));
		assertTrue(testStore1.getReferences().contains(testStore3));
		assertFalse(testStore2.hasDerivedStore(testStore1));
		assertFalse(testStore2.getReferers().contains(testStore1));
		assertTrue(testStore3.hasDerivedStore(testStore1));
		assertTrue(testStore3.getReferers().contains(testStore1));
	}
	
	@Test
	public void testSetBaseStoreChecksForCycle() {
		boolean thrown = false;
		try {
			testStore1.setBaseStore(testStore1);
		} catch (IllegalArgumentException e) {
			thrown = true;
		}
		assertTrue(thrown);
		
		thrown = false;
		testStore1.setBaseStore(testStore2);
		try {
			testStore2.setBaseStore(testStore1);
		} catch (IllegalArgumentException e) {
			thrown = true;
		}
		assertTrue(thrown);
	}

	@Test
	public void testAddDerivedStoresPropagates() {
		testStore1.addDerivedStore(testStore2);

		assertTrue(testStore1.hasDerivedStore(testStore2));
		assertTrue(testStore1.getReferers().contains(testStore2));
		assertTrue(testStore2.getBaseStore() == testStore1);
		assertTrue(testStore2.getReferences().contains(testStore1));
	}

	@Test
	public void testRemoveDerivedStorePropagates() {
		testStore1.addDerivedStore(testStore2);
		testStore1.removeDerivedStore(testStore2);

		assertFalse(testStore1.hasDerivedStore(testStore2));
		assertFalse(testStore1.getReferers().contains(testStore2));
		assertFalse(testStore2.getBaseStore() == testStore1);
		assertFalse(testStore2.getReferences().contains(testStore1));
	}
	
	@Test
	public void testAddDerivedStoresChecksForCycle() {
		boolean thrown = false;
		try {
			testStore1.addDerivedStore(testStore1);
		} catch (IllegalArgumentException e) {
			thrown = true;
		}
		assertTrue(thrown);
		
		thrown = false;
		testStore1.addDerivedStore(testStore2);
		try {
			testStore2.addDerivedStore(testStore1);
		} catch (IllegalArgumentException e) {
			thrown = true;
		}
		assertTrue(thrown);
	}
	
	@Test
	public void testAddDocumentPropagates() {
		testStore1.addDocument(testDocument1);
		
		assertTrue(testStore1.hasDocument(testDocument1));
		assertTrue(testStore1.getReferers().contains(testDocument1));
		assertTrue(testDocument1.getStore() == testStore1);
		assertTrue(testDocument1.getReferences().contains(testStore1));
	}

	@Test
	public void testRemoveDocumentPropagates() {
		testStore1.addDocument(testDocument1);
		testStore1.removeDocument(testDocument1);
		
		assertFalse(testStore1.hasDocument(testDocument1));
		assertFalse(testStore1.getReferers().contains(testDocument1));
		assertFalse(testDocument1.getStore() == testStore1);
		assertFalse(testDocument1.getReferences().contains(testStore1));
	}
	
	@Test
	public void testSetDocumentsPropagates() {
		Iterable<PersistentDocumentEx> documents = Lists.newArrayList(testDocument1, testDocument2);
		testStore1.setDocuments(documents);
		
		for (PersistentDocumentEx document : documents) {
			assertTrue(testStore1.hasDocument(document));
			assertTrue(testStore1.getReferers().contains(document));
			assertTrue(document.getStore() == testStore1);
			assertTrue(document.getReferences().contains(testStore1));
		}
	}
	
	@Test
	public void testSetDocumentsRemovesOldDocuments() {
		Iterable<PersistentDocumentEx> documents = Lists.newArrayList(testDocument1, testDocument2);
		testStore1.setDocuments(documents);
		testStore1.setDocuments(Lists.newArrayList(testDocument3));
		
		for (PersistentDocumentEx document : documents) {
			assertFalse(testStore1.hasDocument(document));
			assertFalse(testStore1.getReferers().contains(document));
			assertFalse(document.getStore() == testStore1);
			assertFalse(document.getReferences().contains(testStore1));
		}
	}
	
	@Test
	public void testSetDescriptionSets() {
		String testDescription = "some description";
		testStore1.setDescription(testDescription);
		
		String actualDescription = testStore1.getDescription();
		assertEquals(testDescription, actualDescription);
	}
	
	@Test
	public void testSetLanguageSets() {
		String testLanguage = "some language";
		testStore1.setLanguage(testLanguage);
		
		String actualLanguage = testStore1.getLanguage();
		assertEquals(testLanguage, actualLanguage);
	}
}