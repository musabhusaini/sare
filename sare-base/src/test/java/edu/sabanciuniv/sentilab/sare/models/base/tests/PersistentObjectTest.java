package edu.sabanciuniv.sentilab.sare.models.base.tests;

import static org.junit.Assert.*;

import java.util.*;

import org.junit.*;

import edu.sabanciuniv.sentilab.sare.models.base.PersistentObject;

public class PersistentObjectTest {

	private class PersistentObjectEx
		extends PersistentObject {
		
		/**
		 * 
		 */
		private static final long serialVersionUID = 8662637764672496097L;

		public List<PersistentObject> getReferences() {
			return this.referencedObjects;
		}
		
		public List<PersistentObject> getReferers() {
			return this.refererObjects;
		}
		
		@Override
		public PersistentObject addReference(PersistentObject reference) {
			return super.addReference(reference);
		}
		
		@Override
		public PersistentObject removeReference(PersistentObject reference) {
			return super.removeReference(reference);
		}
		
		@Override
		public PersistentObject addReferer(PersistentObject referer) {
			return super.addReferer(referer);
		}
		
		@Override
		public PersistentObject removeReferer(PersistentObject referer) {
			return super.removeReferer(referer);
		}

		@Override
		public UUID getOwnerId() {
			// TODO Auto-generated method stub
			return null;
		}
	}
	
	private PersistentObjectEx testObject1;
	private PersistentObjectEx testObject2;
	
	@Before
	public void setUp() throws Exception {
		testObject1 = new PersistentObjectEx();
		testObject2 = new PersistentObjectEx();
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testAddReference() {
		testObject1.addReference(testObject2);
		
		assertTrue(testObject1.getReferences().contains(testObject2));
		assertTrue(testObject2.getReferers().contains(testObject1));
	}

	@Test
	public void testRemoveReference() {
		testObject1.addReference(testObject2);
		testObject1.removeReference(testObject2);
		
		assertFalse(testObject1.getReferences().contains(testObject2));
		assertFalse(testObject2.getReferers().contains(testObject1));
	}

	@Test
	public void testAddReferer() {
		testObject1.addReferer(testObject2);
		
		assertTrue(testObject1.getReferers().contains(testObject2));
		assertTrue(testObject2.getReferences().contains(testObject1));
	}

	@Test
	public void testRemoveReferer() {
		testObject1.addReferer(testObject2);
		testObject1.removeReferer(testObject2);
		
		assertFalse(testObject1.getReferers().contains(testObject2));
		assertFalse(testObject2.getReferences().contains(testObject1));
	}
	
	@Test
	public void testReferringSelfNotAllowed() {
		testObject1.addReference(testObject1);
		
		assertFalse(testObject1.getReferences().contains(testObject1));
		
		testObject1.addReferer(testObject1);
		
		assertFalse(testObject1.getReferers().contains(testObject1));
	}
}