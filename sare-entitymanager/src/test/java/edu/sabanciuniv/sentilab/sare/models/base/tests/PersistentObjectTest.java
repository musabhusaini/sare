package edu.sabanciuniv.sentilab.sare.models.base.tests;

import static org.junit.Assert.*;

import java.lang.reflect.*;
import java.util.*;

import org.junit.*;

import com.google.common.collect.Iterables;
import com.google.gson.JsonObject;

import edu.sabanciuniv.sentilab.sare.models.base.*;
import edu.sabanciuniv.sentilab.sare.models.opinion.OpinionDocument;
import edu.sabanciuniv.sentilab.utils.UuidUtils;

public class PersistentObjectTest extends ModelTestsBase {

	private class PersistentObjectWrapper {
		PersistentObject obj;
		
		public PersistentObjectWrapper(PersistentObject obj) {
			this.obj = obj;
		}

		private Object getField(String name) {
			try {
				Class<?> clazz = obj.getClass();
				while (clazz != null) {
					for (Field field : clazz.getDeclaredFields()) {
						if (field.getName().equals(name)) {
							field.setAccessible(true);
							return field.get(obj);
						}
					}
					
					clazz = clazz.getSuperclass();
				}
			} catch (Throwable e) {
				//
			}
			
			return null;
		}
		
		private Object invokeMethod(String name, Class<?>[] parameterTypes, Object[] args) {
			try {
				Class<?> clazz = obj.getClass();
				while (clazz != null) {
					for (Method method : clazz.getDeclaredMethods()) {
						if (method.getName().equals(name) && Arrays.equals(method.getParameterTypes(), parameterTypes)) {
							method.setAccessible(true);
							return method.invoke(obj, args);
						}
					}
					
					clazz = clazz.getSuperclass();
				}
			} catch (Throwable e) {
				//
			}
			
			return null;
		}
		
		@SuppressWarnings("unchecked")
		public List<PersistentObject> getReferences() {
			return (List<PersistentObject>)getField("referencedObjects");
		}
		
		@SuppressWarnings("unchecked")
		public List<PersistentObject> getReferers() {
			return (List<PersistentObject>)getField("refererObjects");
		}
		
		public PersistentObject addReference(PersistentObject reference) {
			return (PersistentObject)invokeMethod("addReference", new Class<?>[] { PersistentObject.class },
				new Object[] { reference });
		}
		
		public PersistentObject removeReference(PersistentObject reference) {
			return (PersistentObject)invokeMethod("removeReference", new Class<?>[] { PersistentObject.class },
				new Object[] { reference });
		}
		
		public PersistentObject addReferer(PersistentObject referer) {
			return (PersistentObject)invokeMethod("addReferer", new Class<?>[] { PersistentObject.class },
				new Object[] { referer });
		}
		
		public PersistentObject removeReferer(PersistentObject referer) {
			return (PersistentObject)invokeMethod("removeReferer", new Class<?>[] { PersistentObject.class },
				new Object[] { referer });
		}
	}
	
	private OpinionDocument testObject1;
	private PersistentObjectWrapper wrappedTestObject1;
	private OpinionDocument testObject2;
	private JsonObject testOtherData;
	
	@Before
	public void setUp() throws Exception {
		testObject1 = new OpinionDocument();
		wrappedTestObject1 = new PersistentObjectWrapper(testObject1);
		testObject2 = new OpinionDocument();
		
		testOtherData = new JsonObject();
		testOtherData.addProperty("a", "x");
		testOtherData.addProperty("b", "y");
	}

	@After
	public void tearDown() throws Exception {
	}
	
	@Test
	public void testOtherDataGetsSetOnCreate() {
		testObject1.setOtherData(testOtherData);
		
		em.getTransaction().begin();
		persist(testObject1);
		em.getTransaction().commit();
		em.clear();
		
		OpinionDocument actualObject1 = em.find(OpinionDocument.class, testObject1.getId());
		assertNotNull(actualObject1);
		
		JsonObject actualOtherData = actualObject1.getOtherData();
		assertNotNull(actualOtherData);
		assertEquals(testOtherData, actualOtherData);
	}
	
	@Test
	public void testOtherDataGetsSetOnUpdate() {
		em.getTransaction().begin();
		persist(testObject1);
		em.getTransaction().commit();
		em.clear();
		
		OpinionDocument actualObject1 = em.find(OpinionDocument.class, testObject1.getId());
		assertNotNull(actualObject1);
		
		actualObject1.setOtherData(testOtherData);
		
		em.getTransaction().begin();
		em.merge(testObject1);
		em.getTransaction().commit();
		em.clear();
		
		actualObject1 = em.find(OpinionDocument.class, testObject1.getId());
		assertNotNull(actualObject1);
		
		JsonObject actualOtherData = actualObject1.getOtherData();
		assertNotNull(actualOtherData);
		assertEquals(testOtherData, actualOtherData);
	}

	@Test
	public void testAddReference() {
		wrappedTestObject1.addReference(testObject2);
		
		em.getTransaction().begin();
		persist(testObject1);
		persist(testObject2);
		em.getTransaction().commit();
		em.clear();
		
		OpinionDocument actualObject1 = em.find(OpinionDocument.class, testObject1.getId());
		assertNotNull(actualObject1);
		
		PersistentObjectWrapper wrappedActualObject1 = new PersistentObjectWrapper(actualObject1);
		assertTrue(Iterables.find(wrappedActualObject1.getReferences(),
			UuidUtils.identifierEqualsPredicate(testObject2.getIdentifier()), null) != null);
	}

	@Test
	public void testRemoveReference() {
		wrappedTestObject1.addReference(testObject2);
		
		em.getTransaction().begin();
		persist(testObject1);
		persist(testObject2);
		em.getTransaction().commit();
		em.clear();
		
		testObject1 = em.find(OpinionDocument.class, testObject1.getId());
		testObject2 = em.find(OpinionDocument.class, testObject2.getId());
		
		wrappedTestObject1 = new PersistentObjectWrapper(testObject1);
		wrappedTestObject1.removeReference(testObject2);
		
		em.getTransaction().begin();
		em.merge(testObject1);
		em.merge(testObject2);
		em.getTransaction().commit();
		em.clear();
		
		OpinionDocument actualObject1 = em.find(OpinionDocument.class, testObject1.getId());
		assertNotNull(actualObject1);
		
		PersistentObjectWrapper wrappedActualObject1 = new PersistentObjectWrapper(actualObject1);
		assertFalse(Iterables.find(wrappedActualObject1.getReferences(),
			UuidUtils.identifierEqualsPredicate(testObject2.getIdentifier()), null) != null);
	}

	@Test
	public void testAddReferer() {
		wrappedTestObject1.addReferer(testObject2);
		
		em.getTransaction().begin();
		persist(testObject1);
		persist(testObject2);
		em.getTransaction().commit();
		em.clear();
		
		OpinionDocument actualObject1 = em.find(OpinionDocument.class, testObject1.getId());
		assertNotNull(actualObject1);
		
		PersistentObjectWrapper wrappedActualObject1 = new PersistentObjectWrapper(actualObject1);
		assertTrue(Iterables.find(wrappedActualObject1.getReferers(),
			UuidUtils.identifierEqualsPredicate(testObject2.getIdentifier()), null) != null);
	}

	@Test
	public void testRemoveReferer() {
		wrappedTestObject1.addReferer(testObject2);
		
		em.getTransaction().begin();
		persist(testObject1);
		persist(testObject2);
		em.getTransaction().commit();
		em.clear();
		
		testObject1 = em.find(OpinionDocument.class, testObject1.getId());
		testObject2 = em.find(OpinionDocument.class, testObject2.getId());
		
		wrappedTestObject1 = new PersistentObjectWrapper(testObject1);
		wrappedTestObject1.removeReferer(testObject2);
		
		em.getTransaction().begin();
		em.merge(testObject1);
		em.merge(testObject2);
		em.getTransaction().commit();
		em.clear();
		
		OpinionDocument actualObject1 = em.find(OpinionDocument.class, testObject1.getId());
		assertNotNull(actualObject1);
		
		PersistentObjectWrapper wrappedActualObject1 = new PersistentObjectWrapper(actualObject1);
		assertFalse(Iterables.find(wrappedActualObject1.getReferers(),
			UuidUtils.identifierEqualsPredicate(testObject2.getIdentifier()), null) != null);
	}

	@Test
	public void testFirstCreatedDateGetsSet() {
		assertNull(testObject1.getFirstCreatedDate());
		
		em.getTransaction().begin();
		Date timeThen = new Date();
		persist(testObject1);
		em.getTransaction().commit();
		em.clear();
		
		OpinionDocument actualObject1 = em.find(OpinionDocument.class, testObject1.getId());
		assertNotNull(actualObject1);
		
		assertNotNull(actualObject1.getFirstCreatedDate());
		assertEquals(timeThen.getTime(), actualObject1.getFirstCreatedDate().getTime(), 500);
	}
	
	@Test
	public void testFirstCreatedDateDoesNotGetResetOnUpdate() {
		em.getTransaction().begin();
		persist(testObject1);
		em.getTransaction().commit();
		em.clear();
		
		OpinionDocument actualObject1 = em.find(OpinionDocument.class, testObject1.getId());
		assertNotNull(actualObject1);
		
		Date firstCreatedDate = actualObject1.getFirstCreatedDate();

		// make it sleep so we get the possibility of a different time.
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		
		actualObject1.setContent("something");
		em.getTransaction().begin();
		Date timeThen = new Date();
		em.merge(actualObject1);
		em.getTransaction().commit();
		em.clear();
		
		actualObject1 = em.find(OpinionDocument.class, testObject1.getId());
		assertEquals(firstCreatedDate, actualObject1.getFirstCreatedDate());
		assertNotEquals(timeThen.getTime(), actualObject1.getFirstCreatedDate().getTime(), 500);
	}

	@Test
	public void testLastUpdatedDateGetsSetOnCreate() {
		assertNull(testObject1.getLastUpdatedDate());
		
		em.getTransaction().begin();
		Date timeThen = new Date();
		persist(testObject1);
		em.getTransaction().commit();
		em.clear();
		
		OpinionDocument actualObject1 = em.find(OpinionDocument.class, testObject1.getId());
		assertNotNull(actualObject1);
		
		assertNotNull(actualObject1.getLastUpdatedDate());
		assertEquals(timeThen.getTime(), actualObject1.getLastUpdatedDate().getTime(), 500);
	}
	
	@Test
	public void testLastUpdateDateGetsSetOnUpdate() {
		em.getTransaction().begin();
		persist(testObject1);
		em.getTransaction().commit();
		em.clear();
		
		OpinionDocument actualObject1 = em.find(OpinionDocument.class, testObject1.getId());
		assertNotNull(actualObject1);
		
		Date lastUpdatedDate = actualObject1.getLastUpdatedDate();
		
		// make it sleep so we get the possibility of a different time.
		try {
			Thread.sleep(1000);
		} catch (InterruptedException ex) {
			Thread.currentThread().interrupt();
		}
		
		actualObject1.setContent("something");
		em.getTransaction().begin();
		Date timeThen = new Date();
		em.merge(actualObject1);
		em.getTransaction().commit();
		em.clear();
		
		actualObject1 = em.find(OpinionDocument.class, testObject1.getId());
		assertNotEquals(lastUpdatedDate, actualObject1.getLastUpdatedDate());
		assertEquals(timeThen.getTime(), actualObject1.getLastUpdatedDate().getTime(), 500);
	}
}