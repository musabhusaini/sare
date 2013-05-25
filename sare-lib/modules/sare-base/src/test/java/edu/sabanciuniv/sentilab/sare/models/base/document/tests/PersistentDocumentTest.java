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

package edu.sabanciuniv.sentilab.sare.models.base.document.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;

import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.sare.models.base.PersistentObject;
import edu.sabanciuniv.sentilab.sare.models.base.document.PersistentDocument;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;

public class PersistentDocumentTest {

	private class PersistentDocumentEx
		extends PersistentDocument {

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
		public boolean hasDerivedDocument(PersistentDocument derivedDocument) {
			return super.hasDerivedDocument(derivedDocument);
		}
		
		@Override
		public PersistentDocument addDerivedDocument(PersistentDocument derivedDocument) {
			return super.addDerivedDocument(derivedDocument);
		}
		
		@Override
		public PersistentDocument removeDerivedDocument(PersistentDocument derivedDocument) {
			return super.removeDerivedDocument(derivedDocument);
		}
		
		@Override
		public String getContent() {
			return null;
		}
	}

	private class PersistentDocumentStoreEx
		extends PersistentDocumentStore {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -1926552522367328642L;
	
		public List<PersistentObject> getReferers() {
			return this.refererObjects;
		}
	}

	private PersistentDocumentEx testDocument1;
	private PersistentDocumentEx testDocument2;
	private PersistentDocumentEx testDocument3;
	private PersistentDocumentStoreEx testStore1;
	private PersistentDocumentStoreEx testStore2;

	@Before
	public void setUp() throws Exception {
		testDocument1 = new PersistentDocumentEx();
		testDocument2 = new PersistentDocumentEx();
		testDocument3 = new PersistentDocumentEx();
		
		testStore1 = new PersistentDocumentStoreEx();
		testStore2 = new PersistentDocumentStoreEx();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetBaseDocumentPropagates() {
		testDocument1.setBaseDocument(testDocument2);
		
		assertTrue(testDocument1.getBaseDocument() == testDocument2);
		assertTrue(testDocument1.getReferences().contains(testDocument2));
		assertTrue(testDocument2.hasDerivedDocument(testDocument1));
		assertTrue(testDocument2.getReferers().contains(testDocument1));
	}

	@Test
	public void testSetBaseDocumentRemovesOldBase() {
		testDocument1.setBaseDocument(testDocument2);
		testDocument1.setBaseDocument(testDocument3);
		
		assertTrue(testDocument1.getBaseDocument() == testDocument3);
		assertFalse(testDocument1.getReferences().contains(testDocument2));
		assertTrue(testDocument1.getReferences().contains(testDocument3));
		assertFalse(testDocument2.hasDerivedDocument(testDocument1));
		assertFalse(testDocument2.getReferers().contains(testDocument1));
		assertTrue(testDocument3.hasDerivedDocument(testDocument1));
		assertTrue(testDocument3.getReferers().contains(testDocument1));
	}
	
	@Test
	public void testSetBaseDocumentChecksForCycle() {
		boolean thrown = false;
		try {
			testDocument1.setBaseDocument(testDocument1);
		} catch (IllegalArgumentException e) {
			thrown = true;
		}
		assertTrue(thrown);
		
		thrown = false;
		testDocument1.setBaseDocument(testDocument2);
		try {
			testDocument2.setBaseDocument(testDocument1);
		} catch (IllegalArgumentException e) {
			thrown = true;
		}
		assertTrue(thrown);
	}
	
	@Test
	public void testAddDerivedDocumentsPropagates() {
		testDocument1.addDerivedDocument(testDocument2);
		
		assertTrue(testDocument1.hasDerivedDocument(testDocument2));
		assertTrue(testDocument1.getReferers().contains(testDocument2));
		assertTrue(testDocument2.getBaseDocument() == testDocument1);
		assertTrue(testDocument2.getReferences().contains(testDocument1));
	}
	
	@Test
	public void testAddDerivedDocumentsChecksForCycle() {
		boolean thrown = true;
		try {
			testDocument1.addDerivedDocument(testDocument1);
		} catch (IllegalArgumentException e) {
			thrown = true;
		}
		assertTrue(thrown);
		
		thrown = false;
		testDocument1.addDerivedDocument(testDocument2);
		try {
			testDocument2.addDerivedDocument(testDocument1);
		} catch (IllegalArgumentException e) {
			thrown = true;
		}
		assertTrue(thrown);
	}
	
	@Test
	public void testRemoveDerivedDocumentPropagates() {
		testDocument1.addDerivedDocument(testDocument2);
		testDocument1.removeDerivedDocument(testDocument2);
		
		assertFalse(testDocument1.hasDerivedDocument(testDocument2));
		assertFalse(testDocument1.getReferers().contains(testDocument2));
		assertFalse(testDocument2.getBaseDocument() == testDocument1);
		assertFalse(testDocument2.getReferences().contains(testDocument1));
	}
	
	@Test
	public void testSetStorePropagates() {
		testDocument1.setStore(testStore1);
		
		assertTrue(testDocument1.getStore() == testStore1);
		assertTrue(Iterables.contains(testStore1.getDocuments(), testDocument1));
		assertTrue(testDocument1.getReferences().contains(testStore1));
		assertTrue(testStore1.getReferers().contains(testDocument1));
	}
	
	@Test
	public void testSetStoreRemovesOldStore() {
		testDocument1.setStore(testStore1);
		testDocument1.setStore(testStore2);
		
		assertTrue(testDocument1.getStore() == testStore2);
		assertFalse(Iterables.contains(testStore1.getDocuments(), testDocument1));
		assertTrue(Iterables.contains(testStore2.getDocuments(), testDocument1));
		assertFalse(testDocument1.getReferences().contains(testStore1));
		assertTrue(testDocument1.getReferences().contains(testStore2));
		assertFalse(testStore1.getReferers().contains(testDocument1));
		assertTrue(testStore2.getReferers().contains(testDocument1));
	}
}