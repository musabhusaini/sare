package edu.sabanciuniv.sentilab.sare.models.base.documentStore.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;

import edu.sabanciuniv.sentilab.sare.models.base.PersistentObject;
import edu.sabanciuniv.sentilab.sare.models.base.document.IDocument;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.PersistentDocumentStore;

public class PersistentDocumentStoreTest {

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
		public PersistentDocumentStore removeDerivedStore(
				PersistentDocumentStore derivedStore) {
			return super.removeDerivedStore(derivedStore);
		}

		@Override
		public Iterable<? extends IDocument> getDocuments() {
			return null;
		}
	}

	private PersistentDocumentStoreEx testStore1;
	private PersistentDocumentStoreEx testStore2;
	private PersistentDocumentStoreEx testStore3;

	@Before
	public void setUp() throws Exception {
		testStore1 = new PersistentDocumentStoreEx();
		testStore2 = new PersistentDocumentStoreEx();
		testStore3 = new PersistentDocumentStoreEx();
	}

	@After
	public void tearDown() throws Exception {
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
}