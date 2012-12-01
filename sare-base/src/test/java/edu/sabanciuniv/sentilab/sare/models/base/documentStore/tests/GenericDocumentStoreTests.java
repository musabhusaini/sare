package edu.sabanciuniv.sentilab.sare.models.base.documentStore.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.google.common.collect.Lists;

import edu.sabanciuniv.sentilab.sare.models.base.PersistentObject;
import edu.sabanciuniv.sentilab.sare.models.base.document.GenericDocument;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.GenericDocumentStore;

public class GenericDocumentStoreTests {

	private class GenericDocumentEx
		extends GenericDocument<GenericDocumentEx> {
	
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

	private class GenericDocumentStoreEx
		extends GenericDocumentStore<GenericDocumentEx> {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = -1926552522367328642L;
	
		public List<PersistentObject> getReferers() {
			return this.refererObjects;
		}
	}

	private GenericDocumentEx testDocument1;
	private GenericDocumentEx testDocument2;
	private GenericDocumentEx testDocument3;
	private GenericDocumentStoreEx testStore;
	
	@Before
	public void setUp() throws Exception {
		testDocument1 = new GenericDocumentEx();
		testDocument2 = new GenericDocumentEx();
		testDocument3 = new GenericDocumentEx();
		testStore = new GenericDocumentStoreEx();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddDocumentPropagates() {
		testStore.addDocument(testDocument1);
		
		assertTrue(testStore.hasDocument(testDocument1));
		assertTrue(testStore.getReferers().contains(testDocument1));
		assertTrue(testDocument1.getStore() == testStore);
		assertTrue(testDocument1.getReferences().contains(testStore));
	}

	@Test
	public void testRemoveDocumentPropagates() {
		testStore.addDocument(testDocument1);
		testStore.removeDocument(testDocument1);
		
		assertFalse(testStore.hasDocument(testDocument1));
		assertFalse(testStore.getReferers().contains(testDocument1));
		assertFalse(testDocument1.getStore() == testStore);
		assertFalse(testDocument1.getReferences().contains(testStore));
	}
	
	@Test
	public void testSetDocumentsPropagates() {
		Iterable<GenericDocumentEx> documents = Lists.newArrayList(testDocument1, testDocument2);
		testStore.setDocuments(documents);
		
		for (GenericDocumentEx document : documents) {
			assertTrue(testStore.hasDocument(document));
			assertTrue(testStore.getReferers().contains(document));
			assertTrue(document.getStore() == testStore);
			assertTrue(document.getReferences().contains(testStore));
		}
	}
	
	@Test
	public void testSetDocumentsRemovesOldDocuments() {
		Iterable<GenericDocumentEx> documents = Lists.newArrayList(testDocument1, testDocument2);
		testStore.setDocuments(documents);
		testStore.setDocuments(Lists.newArrayList(testDocument3));
		
		for (GenericDocumentEx document : documents) {
			assertFalse(testStore.hasDocument(document));
			assertFalse(testStore.getReferers().contains(document));
			assertFalse(document.getStore() == testStore);
			assertFalse(document.getReferences().contains(testStore));
		}
	}
}