package edu.sabanciuniv.sentilab.sare.models.base.document.tests;

import static org.junit.Assert.*;

import java.util.List;

import org.junit.*;

import edu.sabanciuniv.sentilab.sare.models.base.PersistentObject;
import edu.sabanciuniv.sentilab.sare.models.base.document.PersistentDocument;
import edu.sabanciuniv.sentilab.sare.models.base.documentStore.IDocumentStore;

public class PersistentDocumentTests {

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

		@Override
		public IDocumentStore getStore() {
			return null;
		}
	}
	
	private PersistentDocumentEx testDocument1;
	private PersistentDocumentEx testDocument2;
	private PersistentDocumentEx testDocument3;
	
	@Before
	public void setUp() throws Exception {
		testDocument1 = new PersistentDocumentEx();
		testDocument2 = new PersistentDocumentEx();
		testDocument3 = new PersistentDocumentEx();
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
	public void testAddDerivedDocumentsPropagates() {
		testDocument1.addDerivedDocument(testDocument2);
		
		assertTrue(testDocument1.hasDerivedDocument(testDocument2));
		assertTrue(testDocument1.getReferers().contains(testDocument2));
		assertTrue(testDocument2.getBaseDocument() == testDocument1);
		assertTrue(testDocument2.getReferences().contains(testDocument1));
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
}