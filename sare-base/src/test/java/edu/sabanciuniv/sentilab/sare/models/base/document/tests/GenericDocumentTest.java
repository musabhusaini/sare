package edu.sabanciuniv.sentilab.sare.models.base.document.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;

import com.google.common.collect.Iterables;

import edu.sabanciuniv.sentilab.sare.models.base.PersistentObject;
import edu.sabanciuniv.sentilab.sare.models.base.document.GenericDocument;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.GenericDocumentStore;

public class GenericDocumentTest {

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
	
	private GenericDocumentEx testDocument;
	private GenericDocumentStoreEx testStore1;
	private GenericDocumentStoreEx testStore2;
	
	@Before
	public void setUp() throws Exception {
		testDocument = new GenericDocumentEx();
		testStore1 = new GenericDocumentStoreEx();
		testStore2 = new GenericDocumentStoreEx();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testSetStorePropagates() {
		testDocument.setStore(testStore1);
		
		assertTrue(testDocument.getStore() == testStore1);
		assertTrue(Iterables.contains(testStore1.getDocuments(), testDocument));
		assertTrue(testDocument.getReferences().contains(testStore1));
		assertTrue(testStore1.getReferers().contains(testDocument));
	}
	
	@Test
	public void testSetStoreRemovesOldStore() {
		testDocument.setStore(testStore1);
		testDocument.setStore(testStore2);
		
		assertTrue(testDocument.getStore() == testStore2);
		assertFalse(Iterables.contains(testStore1.getDocuments(), testDocument));
		assertTrue(Iterables.contains(testStore2.getDocuments(), testDocument));
		assertFalse(testDocument.getReferences().contains(testStore1));
		assertTrue(testDocument.getReferences().contains(testStore2));
		assertFalse(testStore1.getReferers().contains(testDocument));
		assertTrue(testStore2.getReferers().contains(testDocument));
	}
}